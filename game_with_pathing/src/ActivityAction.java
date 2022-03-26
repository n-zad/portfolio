public class ActivityAction extends Action
{
    private final WorldModel world;
    private final ImageStore imageStore;

    public ActivityAction(Entity entity, WorldModel world, ImageStore imageStore)
    {
        super(entity);
        this.world = world;
        this.imageStore = imageStore;
    }

    @Override
    public void executeAction(EventScheduler scheduler)
    {
        ActiveEntity ae = (ActiveEntity) getEntity();
        ae.executeActivity(world, imageStore, scheduler);
    }
}
