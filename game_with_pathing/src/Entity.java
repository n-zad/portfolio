import processing.core.PImage;

import java.util.List;

/*
Entity ideally would includes functions for how all the entities in our virtual world might act...
 */

public abstract class Entity
{
   private final String id;
   private Point position;
   private final List<PImage> images;
   private int imageIndex;

   public Entity(String id, Point position, List<PImage> images)
   {
      this.id = id;
      this.position = position;
      this.images = images;
      this.imageIndex = 0;
   }

   //protected accessor methods
   protected String getId() { return id; }
   protected Point getPosition() { return position; }
   protected List<PImage> getImages() { return images; }
   protected int getImageIndex() { return imageIndex; }

   protected void setImageIndex(int index) { imageIndex = index; }

   public PImage getCurrentImage() { return images.get(imageIndex); }

   //mutator methods
   public void setPosition(Point point) { position = point; }
}
