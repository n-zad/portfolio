import processing.core.PImage;

import java.util.List;

public class Skeleton extends Monster implements ActiveEntity
{
    public Skeleton(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod, new SingleStepPathingStrategy());
    }
}
