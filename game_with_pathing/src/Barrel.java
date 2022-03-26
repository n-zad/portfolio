import processing.core.PImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Barrel extends Entity
{
    public Barrel(String id, Point position, List<PImage> images)
    {
        super(id, position, images);
    }

    public void explode(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        List<Point> neighbors = getNeighbors(world);
        Collections.shuffle(neighbors);
        neighbors.stream()
                .limit(GameAttributes.BARREL_FIRE_COUNT)
                .forEach(point -> createFire(point, world, imageStore, scheduler));
        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);
    }

    private List<Point> getNeighbors(WorldModel world)
    {
        Point pt = getPosition();
        return Stream.<Point>builder()
                .add(new Point(pt.x, pt.y))
                .add(new Point(pt.x, pt.y - 1))
                .add(new Point(pt.x, pt.y + 1))
                .add(new Point(pt.x - 1, pt.y))
                .add(new Point(pt.x + 1, pt.y))
                .add(new Point(pt.x - 1, pt.y - 1))
                .add(new Point(pt.x + 1, pt.y + 1))
                .add(new Point(pt.x - 1, pt.y + 1))
                .add(new Point(pt.x + 1, pt.y - 1))
                .build()
                .filter(point -> !world.isOccupied(point) && world.withinBounds(point))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void createFire(Point pt, WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        String fire_id = GameAttributes.FIRE_ID_PREFIX
                + String.format("_%1$d_%2$d", pt.x, pt.y);
        Fire fire = new Fire(fire_id, pt,
                imageStore.getImageList(GameAttributes.FIRE_KEY), 100);

        world.tryAddEntity(fire);
        scheduler.scheduleActions(fire, world, imageStore);
    }
}
