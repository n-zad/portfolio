public interface ActiveEntity
{
    int getActionPeriod();
    Action createActivityAction(WorldModel world, ImageStore imageStore);
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
}
