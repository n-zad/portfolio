import java.util.*;

/*
EventScheduler: ideally our way of controlling what happens in our virtual world
 */

final class EventScheduler
{
   private final PriorityQueue<Event> eventQueue;
   private final Map<Entity, List<Event>> pendingEvents;
   private final double timeScale;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }

   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() + (long)(afterPeriod * timeScale);
      Event event = new Event(action, time, entity);

      eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = pendingEvents.getOrDefault(entity, new LinkedList<>());
      pending.add(event);
      pendingEvents.put(entity, pending);
   }

   public void unscheduleAllEvents(Entity entity)
   {
      List<Event> pending = pendingEvents.remove(entity);

      if (pending != null)
         for (Event event : pending)
            eventQueue.remove(event);
   }

   public void removePendingEvent(Event event)
   {
      List<Event> pending = pendingEvents.get(event.getEntity());

      if (pending != null)
         pending.remove(event);
   }

   public void updateOnTime(long time)
   {
      while (!eventQueue.isEmpty() && eventQueue.peek().getTime() < time)
      {
         Event next = eventQueue.poll();

         removePendingEvent(next);

         next.getAction().executeAction(this);
      }
   }

   public void scheduleActions(Entity entity, WorldModel world, ImageStore imageStore)
   {
      if (Arrays.asList(Skeleton.class, Bat.class).contains(entity.getClass())) {
         Monster me = (Monster) entity; // all movable entities are animated
         scheduleEvent(me,
                 me.createActivityAction(world, imageStore),
                 me.getActionPeriod());
         scheduleEvent(me,
                 me.createAnimationAction(0),
                 me.getAnimationPeriod());
      } else if (Player.class == entity.getClass()) {
         Player player = (Player) entity;
         scheduleEvent(player,
                 player.createActivityAction(world, imageStore),
                 player.getActionPeriod());
         scheduleEvent(player,
                 player.createAnimationAction(0),
                 player.getAnimationPeriod());
      } else if (Torch.class == entity.getClass()) {
         Torch torch = (Torch) entity;
         scheduleEvent(torch,
                 torch.createAnimationAction(0),
                 torch.getAnimationPeriod());
      } else if (Fire.class == entity.getClass()) {
         Fire fire = (Fire) entity;
         scheduleEvent(fire,
                 fire.createAnimationAction(0),
                 fire.getAnimationPeriod());
      }
   }
}
