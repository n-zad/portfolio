import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

/*
ImageStore: to ideally keep track of the images used in our virtual world
 */

final class ImageStore
{
   private final Map<String, List<PImage>> images;
   private final List<PImage> defaultImages;

   public ImageStore(PImage defaultImage)
   {
      this.images = new HashMap<>();
      defaultImages = new LinkedList<>();
      defaultImages.add(defaultImage);
   }

   public List<PImage> getImageList(String key)
   {
      return images.getOrDefault(key, defaultImages);
   }

   public void loadImages(Scanner in, PApplet screen)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            processImageLine(in.nextLine(), screen);
         }
         catch (NumberFormatException e)
         {
            System.out.printf("Image format error on line %d%n", lineNumber);
         }
         lineNumber++;
      }
   }

   private void processImageLine(String line, PApplet screen)
   {
      String[] attrs = line.split("\\s");
      if (attrs.length >= 2)
      {
         String key = attrs[0];
         PImage img = screen.loadImage(attrs[1]);
         if (img != null && img.width != -1)
         {
            List<PImage> imgs = getImages(key);
            imgs.add(img);

            if (attrs.length >= GameAttributes.KEYED_IMAGE_MIN)
            {
               int r = Integer.parseInt(attrs[GameAttributes.KEYED_RED_IDX]);
               int g = Integer.parseInt(attrs[GameAttributes.KEYED_GREEN_IDX]);
               int b = Integer.parseInt(attrs[GameAttributes.KEYED_BLUE_IDX]);
               setAlpha(img, screen.color(r, g, b), 0);
            }
         }
      }
   }

   private List<PImage> getImages(String key)
   {
      List<PImage> imgs = images.get(key);
      if (imgs == null)
      {
         imgs = new LinkedList<>();
         images.put(key, imgs);
      }
      return imgs;
   }

   /*
     Called with color for which alpha should be set and alpha value.
     setAlpha(img, color(255, 255, 255), 0));
   */
   private void setAlpha(PImage img, int maskColor, int alpha)
   {
      int alphaValue = alpha << 24;
      int nonAlpha = maskColor & GameAttributes.COLOR_MASK;
      img.format = PApplet.ARGB;
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         if ((img.pixels[i] & GameAttributes.COLOR_MASK) == nonAlpha)
         {
            img.pixels[i] = alphaValue | nonAlpha;
         }
      }
      img.updatePixels();
   }

   public void load(Scanner in, WorldModel world)
   {
      int lineNumber = 0;
      while (in.hasNextLine())
      {
         try
         {
            if (!processLine(in.nextLine(), world))
            {
               System.err.printf("invalid entry on line %d%n", lineNumber);
            }
         }
         catch (NumberFormatException e)
         {
            System.err.printf("invalid entry on line %d%n", lineNumber);
         }
         catch (IllegalArgumentException e)
         {
            System.err.printf("issue on line %d: %s%n", lineNumber, e.getMessage());
         }
         lineNumber++;
      }
   }

   private boolean processLine(String line, WorldModel world)
   {
      String[] properties = line.split("\\s");
      if (properties.length > 0)
      {
         switch (properties[GameAttributes.PROPERTY_KEY])
         {
            case GameAttributes.BGND_KEY:
               return world.parseBackground(properties, this);
            case GameAttributes.SKELETON_KEY:
               return world.parseSkeleton(properties, this);
            case GameAttributes.BAT_KEY:
               return world.parseBat(properties, this);
            case GameAttributes.SPACE_KEY:
               return world.parseSpace(properties, this);
            case GameAttributes.BARREL_KEY:
               return world.parseBarrel(properties, this);
            case GameAttributes.PLAYER_KEY:
               return world.parsePlayer(properties, this);
            case GameAttributes.TORCH_KEY:
               return world.parseTorch(properties, this);
         }
      }

      return false;
   }

}
