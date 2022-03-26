import processing.core.PImage;

import java.util.List;

final class Background
{
   private final String id;
   private final List<PImage> images;
   private final int imageIndex;

   public Background(String id, List<PImage> images)
   {
      this.id = id;
      this.images = images;
      this.imageIndex = 0;
   }

   public PImage getCurrentImage() { return images.get(imageIndex); }
}
