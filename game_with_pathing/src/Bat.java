import processing.core.PImage;

import java.util.List;

public class Bat extends Monster implements ActiveEntity
{
    public Bat(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod, new AStarPathingStrategy());
    }
}
