import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   public static final int TIMER_ACTION_PERIOD = 100;

   public static final int VIEW_WIDTH = 640;
   public static final int VIEW_HEIGHT = 480;
   public static final int TILE_WIDTH = 32;
   public static final int TILE_HEIGHT = 32;
   public static final int WORLD_WIDTH_SCALE = 1;
   public static final int WORLD_HEIGHT_SCALE = 1;

   public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   public static final String IMAGE_LIST_FILE_NAME = "imagelist";
   public static final String DEFAULT_IMAGE_NAME = "background_default";
   public static final int DEFAULT_IMAGE_COLOR = 0x808080;

   public static final String LOAD_FILE_NAME = "world.save";

   public static final String FAST_FLAG = "-fast";
   public static final String FASTER_FLAG = "-faster";
   public static final String FASTEST_FLAG = "-fastest";
   public static final double FAST_SCALE = 0.5;
   public static final double FASTER_SCALE = 0.25;
   public static final double FASTEST_SCALE = 0.10;

   public static double timeScale = 1.0;

   public ImageStore imageStore;
   public WorldModel world;
   public WorldView view;
   public EventScheduler scheduler;

   public long next_time;

   private long startTime = 0;
   private long survivalTime = 0;
   private boolean gameStart = false;
   private boolean playerWon = false;
   private boolean playerLost = false;

   @Override
   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.
   */
   @Override
   public void setup()
   {
      this.imageStore = new ImageStore(
         createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
         createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
         TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      generateEntityData();
      loadWorld(world, LOAD_FILE_NAME, imageStore);

      scheduleActions(world, scheduler, imageStore);

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   @Override
   public void draw()
   {
      if (gameStart)
      {
         long time = System.currentTimeMillis();
         if (startTime == 0)
            startTime = time;
         if (time >= next_time) {
            scheduler.updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }

         view.drawViewport();

         // draw hp and torch count text
         stroke(255);
         textSize(18);
         text("Health: " + GameAttributes.player.getHp(), VIEW_WIDTH - 110, 25);
         text("Torches: " + GameAttributes.player.getTorch_count(), VIEW_WIDTH - 110, 50);
         long currentTime = (long) Math.floor((float) (time - startTime) / 1000);
         String currentSeconds = "Time Left: " + (30 - currentTime) + "s";
         text(currentSeconds, VIEW_WIDTH - 120, 85);

         if (GameAttributes.player.getHp() <= 0) {
            gameStart = false;
            playerLost = true;
            survivalTime = currentTime;
            restartGame();
         }

         if (currentTime == 30) { // after 30 seconds
            playerWon = true;
            gameStart = false;
            restartGame();
         }
      }
      else if (playerWon)
      {
         background(color(32, 127, 32));
         stroke(255);
         textSize(32);
         text("You Won",(float) VIEW_WIDTH/2 - 70,(float) VIEW_HEIGHT/2 - 100);
         textSize(18);
         String msg = "You survived for 30 seconds";
         text(msg,(float) VIEW_WIDTH/2 - 110,(float) VIEW_HEIGHT/2 - 60);
         fill(color(32, 127, 32));
         rect((float) VIEW_WIDTH/2 - 50,(float) VIEW_HEIGHT/2 + 80, 100, 40);
         fill(255);
         textSize(18);
         text("Play Again", (float) VIEW_WIDTH/2 - 42, (float) VIEW_HEIGHT/2 + 106);
      }
      else if (playerLost)
      {
         background(color(127, 32, 32));
         stroke(255);
         textSize(32);
         text("You Lost",(float) VIEW_WIDTH/2 - 70,(float) VIEW_HEIGHT/2 - 100);
         textSize(18);
         String msg = "You survived for " + survivalTime + " seconds";
         text(msg,(float) VIEW_WIDTH/2 - 110,(float) VIEW_HEIGHT/2 - 60);
         fill(color(127, 32, 32));
         rect((float) VIEW_WIDTH/2 - 50,(float) VIEW_HEIGHT/2 + 80, 100, 40);
         fill(255);
         textSize(18);
         text("Play Again", (float) VIEW_WIDTH/2 - 42, (float) VIEW_HEIGHT/2 + 106);
      }
      else
      {
         background(color(0, 127, 127));
         stroke(255);
         textSize(32);
         text("Survive the Dungeon",(float) VIEW_WIDTH/2 - 150,(float) VIEW_HEIGHT/2 - 100);
         textSize(18);
         String msg = "Move around using arrow keys or wasd";
         text(msg,(float) VIEW_WIDTH/2 - 160,(float) VIEW_HEIGHT/2 - 60);
         msg = "Win by surviving for 30 seconds or killing all the enemies";
         text(msg,(float) VIEW_WIDTH/2 - 220,(float) VIEW_HEIGHT/2 - 20);
         msg = "Collect torches to light up barrels by clicking on them " +
                 "\n                 killing enemies near the barrel";
         text(msg,(float) VIEW_WIDTH/2 - 200,(float) VIEW_HEIGHT/2 + 20);
         fill(color(0, 127, 127));
         rect((float) VIEW_WIDTH/2 - 50,(float) VIEW_HEIGHT/2 + 80, 100, 40);
         fill(255);
         textSize(28);
         text("Start", (float) VIEW_WIDTH/2 - 30, (float) VIEW_HEIGHT/2 + 110);
         textSize(12);
         msg = "Game assets were made by Calciumtrice and downloaded from opengameart.com";
         text(msg, 5, VIEW_HEIGHT - 5);
      }
   }

   @Override
   public void mouseClicked()
   {
      int x = mouseX;
      int y = mouseY;

      boolean inButton = x > (float) VIEW_WIDTH/2 - 30 && x < (float) VIEW_WIDTH/2 + 30
              && y > (float) VIEW_HEIGHT/2 + 80 && y < (float) VIEW_HEIGHT/2 + 100;

      if (inButton)
      {
         GameAttributes.player.resetStats();

         if (!gameStart)
            gameStart = true;
         else if (playerWon)
            playerWon = false;
         else if (playerLost)
            playerLost = false;
      }
      else
      {
         Point pt = new Point(x/TILE_WIDTH, y/TILE_HEIGHT);

         if (world.isOccupied(pt)
                 && world.getOccupancyCell(pt).getClass() == Barrel.class
                 && GameAttributes.player.getTorch_count() > 0)
         {
            GameAttributes.player.useTorch();
            Barrel b = (Barrel) world.getOccupancyCell(pt);
            b.explode(world, imageStore, scheduler);
         }
      }
   }

   @Override
   public void keyPressed()
   {
      if (!gameStart)
         return;

      if (key == CODED)
      {
         switch (keyCode)
         {
            case UP:
               GameAttributes.player.moveUp(world, imageStore, scheduler);
               break;
            case DOWN:
               GameAttributes.player.moveDown(world, imageStore, scheduler);
               break;
            case LEFT:
               GameAttributes.player.moveLeft(world, imageStore, scheduler);
               break;
            case RIGHT:
               GameAttributes.player.moveRight(world, imageStore, scheduler);
               break;
         }
      }
      else
      {
         switch(key)
         {
            case 'w':
               GameAttributes.player.moveUp(world, imageStore, scheduler);
               break;
            case 's':
               GameAttributes.player.moveDown(world, imageStore, scheduler);
               break;
            case 'a':
               GameAttributes.player.moveLeft(world, imageStore, scheduler);
               break;
            case 'd':
               GameAttributes.player.moveRight(world, imageStore, scheduler);
               break;
         }
      }
   }

   private void restartGame()
   {
      startTime = 0;
      List<Entity> entities = new ArrayList<>(world.getEntities());
      for (Entity entity : entities)
      {
         world.removeEntity(entity);
         scheduler.unscheduleAllEvents(entity);
      }
      generateEntityData();
      loadWorld(world, LOAD_FILE_NAME, imageStore);
      scheduleActions(world, scheduler, imageStore);
   }

   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void loadWorld(WorldModel world, String filename,
      ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.load(in, world);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      //Only start actions for entities that include action (not those with just animations)
      world.getEntities().stream()
              .filter(entity -> entity instanceof ActiveEntity
                      || entity instanceof AnimatedEntity)
              .forEach(entity -> scheduler.scheduleActions(entity, world, imageStore));
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   private static void generateEntityData()
   {
      File file = new File(LOAD_FILE_NAME);
      FileWriter fw;
      Random rand = new Random();

      try
      {
         fw = new FileWriter(file);
         List<Point> occupied = new ArrayList<>();

         // add space tiles
         for (int j = 0; j < 3; j++) {
            for (int i = 16; i < 20; i++) {
               fw.write(String.format("space space_%1$d_%2$d %1$d %2$d\n", i, j));
               occupied.add(new Point(i, j));
            }
         }

         // add player
         Point pt = new Point(rand.nextInt(WORLD_COLS), rand.nextInt(WORLD_ROWS));
         while (occupied.contains(pt))
            pt = new Point(rand.nextInt(WORLD_COLS), rand.nextInt(WORLD_ROWS));
         String pos = String.format("_%1$d_%2$d %1$d %2$d", pt.x, pt.y);
         fw.write("player player" + pos + " 100\n");
         occupied.add(pt);

         // add barrels
         Point point;
         int count = 0;
         while (count < GameAttributes.NUM_BARRELS) {
            point = new Point(rand.nextInt(WORLD_COLS), rand.nextInt(WORLD_ROWS));
            if (pointsFarEnough(point, pt) && !occupied.contains(point)) {
               pos = String.format("_%1$d_%2$d %1$d %2$d", point.x, point.y);
               fw.write("barrel barrel" + pos + "\n");
               count++;
               occupied.add(point);
            }
         }

         // add torches
         count = 0;
         while (count < GameAttributes.NUM_TORCHES) {
            point = new Point(rand.nextInt(WORLD_COLS), rand.nextInt(WORLD_ROWS));
            if (pointsFarEnough(point, pt) && !occupied.contains(point)) {
               pos = String.format("_%1$d_%2$d %1$d %2$d", point.x, point.y);
               fw.write("torch torch" + pos + " 100\n");
               count++;
               occupied.add(point);
            }
         }

         // add skeletons
         count = 0;
         while (count < GameAttributes.NUM_SKELETONS) {
            point = new Point(rand.nextInt(WORLD_COLS), rand.nextInt(WORLD_ROWS));
            if (pointsFarEnough(point, pt) && !occupied.contains(point)) {
               pos = String.format("_%1$d_%2$d %1$d %2$d", point.x, point.y);
               fw.write("skeleton skeleton" + pos + " " + (rand.nextInt(200) + 800) + " 100\n");
               count++;
               occupied.add(point);
            }
         }

         // add bats
         count = 0;
         while (count < GameAttributes.NUM_BATS) {
            point = new Point(rand.nextInt(WORLD_COLS), rand.nextInt(WORLD_ROWS));
            if (pointsFarEnough(point, pt) && !occupied.contains(point)) {
               pos = String.format("_%1$d_%2$d %1$d %2$d", point.x, point.y);
               fw.write("bat bat" + pos + " " + (rand.nextInt(200) + 800) + " 100\n");
               count++;
               occupied.add(point);
            }
         }

         // add floor
         for (int j = 0; j < WORLD_ROWS; j++)
            for (int i = 0; i < WORLD_COLS; i++)
               fw.write(String.format("background floor %1$d %2$d\n", i, j));

         fw.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private static boolean pointsFarEnough(Point p1, Point p2)
   {
      return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) > 3;
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
