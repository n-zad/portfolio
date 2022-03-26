import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public abstract class Monster extends AnimatedEntity implements ActiveEntity
{
    private final int actionPeriod;
    private final PathingStrategy strategy;

    public Monster(String id, Point position, List<PImage> images,
                   int actionPeriod, int animationPeriod, PathingStrategy strategy)
    {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
        this.strategy = strategy;
    }

    @Override
    public int getActionPeriod() { return actionPeriod; }

    protected boolean moveTo(WorldModel world, Player target, EventScheduler scheduler)
    {
        if (adjacent(target.getPosition()))
        {
            target.loseHp();
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            return true;
        }
        else if (world.findNearest(getPosition(), "Fire").isPresent()
                && adjacent(world.findNearest(getPosition(), "Fire").get().getPosition()))
        {
            // if monster hits fire, it dies
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            // fire also goes away
            Fire fire = (Fire) world.findNearest(getPosition(), "Fire").get();
            world.removeEntity(fire);
            scheduler.unscheduleAllEvents(fire);
            return false;
        }
        else
        {
            List<Point> path = computePath(target.getPosition(), world);

            Point nextPos = getPosition();

            if (!path.isEmpty())
                nextPos = path.get(0);

            if (!getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                occupant.ifPresent(scheduler::unscheduleAllEvents);

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    protected boolean adjacent(Point p2)
    {
        Point p1 = super.getPosition();
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
                (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
    }

    private List<Point> computePath(Point goal, WorldModel world)
    {
        return strategy.computePath(getPosition(), goal,
                p -> world.withinBounds(p) && !world.isOccupied(p),
                Monster::neighbors,
                PathingStrategy.CARDINAL_NEIGHBORS);
    }

    private static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }

    @Override
    public Action createActivityAction(WorldModel world, ImageStore imageStore)
    {
        return new ActivityAction(this, world, imageStore);
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(), "Player");

        if (notFullTarget.isEmpty()
                || !this.moveTo(world, (Player) notFullTarget.get(), scheduler))
        {

            scheduler.scheduleEvent(
                    this,
                    this.createActivityAction(world, imageStore),
                    this.getActionPeriod());
        }
    }
}
