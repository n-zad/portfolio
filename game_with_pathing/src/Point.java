final class Point
{
   public final int x;
   public final int y;

   public Point(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   @Override
   public String toString()
   {
      return "(" + x + "," + y + ")";
   }

   @Override
   public boolean equals(Object other)
   {
      return other instanceof Point
         && ((Point)other).x == this.x
         && ((Point)other).y == this.y;
   }

   @Override
   public int hashCode()
   {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }

   public int distanceSquared(Point p2)
   {
      int deltaX = this.x - p2.x;
      int deltaY = this.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public boolean adjacent(Point p)
   {
      return (x == p.x && Math.abs(y - p.y) == 1)
              || (y == p.y && Math.abs(x - p.x) == 1);
   }
}
