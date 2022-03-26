import processing.core.PImage;
import java.util.List;

public class Player extends AnimatedEntity implements ActiveEntity
{
    private final int actionPeriod;
    private int hp;
    private int torch_count;

    public Player(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
        this.hp = GameAttributes.PLAYER_HP;
        this.torch_count = 0;
    }

    @Override
    public int getActionPeriod() { return actionPeriod; }

    public int getHp() {
        return hp;
    }

    public int getTorch_count() {
        return torch_count;
    }

    public void resetStats() {
        hp = GameAttributes.PLAYER_HP;
        torch_count = 0;
    }

    public void loseHp() {
        hp--;
    }

    public void useTorch() {
        torch_count--;
    }

    public void moveUp(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point newPos = new Point(getPosition().x, getPosition().y - 1);
        executeMove(newPos, world, imageStore, scheduler);
    }

    public void moveDown(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point newPos = new Point(getPosition().x, getPosition().y + 1);
        executeMove(newPos, world, imageStore, scheduler);
    }

    public void moveLeft(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point newPos = new Point(getPosition().x - 1, getPosition().y);
        executeMove(newPos, world, imageStore, scheduler);
    }

    public void moveRight(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Point newPos = new Point(getPosition().x + 1, getPosition().y);
        executeMove(newPos, world, imageStore, scheduler);
    }

    private void executeMove(Point newPos, WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!world.isOccupied(newPos)) {
            world.moveEntity(this, newPos);
            scheduler.scheduleActions(this, world, imageStore);
        } else if (world.getOccupancyCell(newPos).getClass() == Torch.class) {
            torch_count++;
            world.moveEntity(this, newPos);
            scheduler.scheduleActions(this, world, imageStore);
        } else if (world.getOccupancyCell(newPos).getClass() == Fire.class) {
            hp--;
            Fire fire = (Fire) world.getOccupancyCell(newPos);
            world.removeEntity(fire);
            scheduler.unscheduleAllEvents(fire);
        }
    }

    @Override
    public Action createActivityAction(WorldModel world, ImageStore imageStore)
    {
        return new ActivityAction(this, world, imageStore);
    }

    @Override
    public String toString() {
        return "Player(hp: " + hp + ", torches: " + torch_count + ")";
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.scheduleEvent(
                this,
                this.createActivityAction(world, imageStore),
                this.getActionPeriod());
    }
}
