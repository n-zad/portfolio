import processing.core.PImage;

import java.util.List;

public class AnimatedEntity extends Entity
{
    private final int animationPeriod;

    public AnimatedEntity(String id, Point position, List<PImage> images, int animationPeriod) {
        super(id, position, images);
        this.animationPeriod = animationPeriod;
    }

    //public accessor methods
    public int getAnimationPeriod() { return animationPeriod; }

    public Action createAnimationAction(int repeatCount)
    {
        return new AnimationAction(this, repeatCount);
    }

    public void nextImage() {
        setImageIndex((getImageIndex() + 1) % getImages().size());
    }
}
