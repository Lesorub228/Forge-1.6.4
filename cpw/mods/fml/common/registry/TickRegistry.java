// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import com.google.common.collect.Queues;
import java.util.List;
import cpw.mods.fml.common.SingleIntervalHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.IScheduledTickHandler;
import java.util.concurrent.atomic.AtomicLong;
import java.util.PriorityQueue;

public class TickRegistry
{
    private static PriorityQueue<TickQueueElement> clientTickHandlers;
    private static PriorityQueue<TickQueueElement> serverTickHandlers;
    private static AtomicLong clientTickCounter;
    private static AtomicLong serverTickCounter;
    
    public static void registerScheduledTickHandler(final IScheduledTickHandler handler, final Side side) {
        getQueue(side).add(new TickQueueElement(handler, getCounter(side).get()));
    }
    
    private static PriorityQueue<TickQueueElement> getQueue(final Side side) {
        return side.isClient() ? TickRegistry.clientTickHandlers : TickRegistry.serverTickHandlers;
    }
    
    private static AtomicLong getCounter(final Side side) {
        return side.isClient() ? TickRegistry.clientTickCounter : TickRegistry.serverTickCounter;
    }
    
    public static void registerTickHandler(final ITickHandler handler, final Side side) {
        registerScheduledTickHandler(new SingleIntervalHandler(handler), side);
    }
    
    public static void updateTickQueue(final List<IScheduledTickHandler> ticks, final Side side) {
        synchronized (ticks) {
            ticks.clear();
            final long tick = getCounter(side).incrementAndGet();
            final PriorityQueue<TickQueueElement> tickHandlers = getQueue(side);
            while (tickHandlers.size() != 0 && tickHandlers.peek().scheduledNow(tick)) {
                final TickQueueElement tickQueueElement = tickHandlers.poll();
                tickQueueElement.update(tick);
                tickHandlers.offer(tickQueueElement);
                ticks.add(tickQueueElement.ticker);
            }
        }
    }
    
    static {
        TickRegistry.clientTickHandlers = Queues.newPriorityQueue();
        TickRegistry.serverTickHandlers = Queues.newPriorityQueue();
        TickRegistry.clientTickCounter = new AtomicLong();
        TickRegistry.serverTickCounter = new AtomicLong();
    }
    
    public static class TickQueueElement implements Comparable<TickQueueElement>
    {
        private long next;
        public IScheduledTickHandler ticker;
        
        public TickQueueElement(final IScheduledTickHandler ticker, final long tickCounter) {
            this.ticker = ticker;
            this.update(tickCounter);
        }
        
        @Override
        public int compareTo(final TickQueueElement o) {
            return (int)(this.next - o.next);
        }
        
        public void update(final long tickCounter) {
            this.next = tickCounter + Math.max(this.ticker.nextTickSpacing(), 1);
        }
        
        public boolean scheduledNow(final long tickCounter) {
            return tickCounter >= this.next;
        }
    }
}
