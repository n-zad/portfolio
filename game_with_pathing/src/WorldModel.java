import processing.core.PImage;

import java.util.*;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel
{
   private final int numRows;
   private final int numCols;
   private final Background[][] background;
   private final Entity[][] occupancy;
   private final Set<Entity> entities;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   //accessor methods
   public int getNumRows() { return numRows; }
   public int getNumCols() { return numCols; }
   public Set<Entity> getEntities() { return entities; }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows
              && pos.x >= 0 && pos.x < numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   public Optional<Entity> nearestEntity(List<Entity> entities, Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = nearest.getPosition().distanceSquared(pos);

         for (Entity other : entities)
         {
            int otherDistance = other.getPosition().distanceSquared(pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public Optional<Entity> findNearest(Point pos, String class_name)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (entity.getClass().getSimpleName().equals(class_name))
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   /*
      Assumes that there is no entity currently occupying the
      intended destination cell.
   */
   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   public void removeEntityAt(Point pos)
   {
      if (withinBounds(pos) && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
         return Optional.of(getBackgroundCell(pos).getCurrentImage());
      else
         return Optional.empty();
   }

   public void setBackground(Point pos, Background background)
   {
      if (withinBounds(pos))
         setBackgroundCell(pos, background);
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
         return Optional.of(getOccupancyCell(pos));
      else
         return Optional.empty();
   }

   public Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos, Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public Background getBackgroundCell(Point pos)
   {
      return background[pos.y][pos.x];
   }

   public void setBackgroundCell(Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public boolean parseBackground(String [] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.BGND_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[GameAttributes.BGND_COL]),
                 Integer.parseInt(properties[GameAttributes.BGND_ROW]));

         String id = properties[GameAttributes.BGND_ID];
         setBackground(pt, new Background(id, imageStore.getImageList(id)));
      }

      return properties.length == GameAttributes.BGND_NUM_PROPERTIES;
   }

   public boolean parseSkeleton(String [] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.SKELETON_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[GameAttributes.SKELETON_COL]),
                 Integer.parseInt(properties[GameAttributes.SKELETON_ROW]));

         Skeleton skeleton = new Skeleton(properties[GameAttributes.SKELETON_ID], pt,
                 imageStore.getImageList(GameAttributes.SKELETON_KEY),
                 Integer.parseInt(properties[GameAttributes.SKELETON_ACTION_PERIOD]),
                 Integer.parseInt(properties[GameAttributes.SKELETON_ANIMATION_PERIOD]));

         tryAddEntity(skeleton);
      }

      return properties.length == GameAttributes.SKELETON_NUM_PROPERTIES;
   }

   public boolean parseSpace(String [] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.SPACE_NUM_PROPERTIES)
      {
         Point pt = new Point(
                 Integer.parseInt(properties[GameAttributes.SPACE_COL]),
                 Integer.parseInt(properties[GameAttributes.SPACE_ROW]));

         EmptySpace emptySpace = new EmptySpace(properties[GameAttributes.SPACE_ID], pt,
                 imageStore.getImageList(GameAttributes.SPACE_KEY));

         tryAddEntity(emptySpace);
      }

      return properties.length == GameAttributes.SPACE_NUM_PROPERTIES;
   }

   public boolean parseBarrel(String[] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.BARREL_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[GameAttributes.BARREL_COL]),
                 Integer.parseInt(properties[GameAttributes.BARREL_ROW]));

         Barrel barrel = new Barrel(properties[GameAttributes.BARREL_ID], pt,
                 imageStore.getImageList(GameAttributes.BARREL_KEY));

         tryAddEntity(barrel);
      }

      return properties.length == GameAttributes.BARREL_NUM_PROPERTIES;
   }

   public boolean parsePlayer(String[] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.PLAYER_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[GameAttributes.PLAYER_COL]),
                 Integer.parseInt(properties[GameAttributes.PLAYER_ROW]));

         GameAttributes.player = new Player(properties[GameAttributes.PLAYER_ID], pt,
                 imageStore.getImageList(GameAttributes.PLAYER_KEY),
                 0, GameAttributes.PLAYER_ANIMATION_PERIOD);

         tryAddEntity(GameAttributes.player);
      }

      return properties.length == GameAttributes.PLAYER_NUM_PROPERTIES;
   }

   public boolean parseTorch(String[] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.TORCH_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[GameAttributes.TORCH_COL]),
                 Integer.parseInt(properties[GameAttributes.TORCH_ROW]));

         Torch torch = new Torch(properties[GameAttributes.TORCH_ID], pt,
                 imageStore.getImageList(GameAttributes.TORCH_KEY),
                 Integer.parseInt(properties[GameAttributes.TORCH_ANIMATION_PERIOD]));

         tryAddEntity(torch);
      }

      return properties.length == GameAttributes.TORCH_NUM_PROPERTIES;
   }

   public boolean parseBat(String[] properties, ImageStore imageStore)
   {
      if (properties.length == GameAttributes.BAT_NUM_PROPERTIES)
      {
         Point pt = new Point(Integer.parseInt(properties[GameAttributes.BAT_COL]),
                 Integer.parseInt(properties[GameAttributes.BAT_ROW]));

         Bat bat = new Bat(properties[GameAttributes.BAT_ID], pt,
                 imageStore.getImageList(GameAttributes.BAT_KEY),
                 Integer.parseInt(properties[GameAttributes.BAT_ACTION_PERIOD]),
                 Integer.parseInt(properties[GameAttributes.BAT_ANIMATION_PERIOD]));

         tryAddEntity(bat);
      }

      return properties.length == GameAttributes.BAT_NUM_PROPERTIES;
   }
}
