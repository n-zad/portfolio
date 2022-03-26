/*
Action: ideally what our various entities might do in our virtual world
 */

public abstract class Action
{
   private final Entity entity;

   public Action(Entity entity)
   {
      this.entity = entity;
   }

   // protected accessor methods
   protected Entity getEntity() { return entity; }

   public abstract void executeAction(EventScheduler scheduler);
}
