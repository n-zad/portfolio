public class AnimationAction extends Action
{
    private final int repeatCount;

    public AnimationAction(AnimatedEntity entity, int repeatCount)
    {
        super(entity);
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler)
    {
        AnimatedEntity ae = (AnimatedEntity) getEntity();
        ae.nextImage();

        if (repeatCount != 1)
            scheduler.scheduleEvent(
                    ae,
                    ae.createAnimationAction(Math.max(repeatCount - 1, 0)),
                    ae.getAnimationPeriod());
    }

}
