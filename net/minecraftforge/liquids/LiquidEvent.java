// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

@Deprecated
public class LiquidEvent extends Event
{
    public final LiquidStack liquid;
    public final int x;
    public final int y;
    public final int z;
    public final abw world;
    
    public LiquidEvent(final LiquidStack liquid, final abw world, final int x, final int y, final int z) {
        this.liquid = liquid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public static final void fireEvent(final LiquidEvent event) {
        MinecraftForge.EVENT_BUS.post(event);
    }
    
    public static class LiquidMotionEvent extends LiquidEvent
    {
        public LiquidMotionEvent(final LiquidStack liquid, final abw world, final int x, final int y, final int z) {
            super(liquid, world, x, y, z);
        }
    }
    
    public static class LiquidFillingEvent extends LiquidEvent
    {
        public final ILiquidTank tank;
        
        public LiquidFillingEvent(final LiquidStack liquid, final abw world, final int x, final int y, final int z, final ILiquidTank tank) {
            super(liquid, world, x, y, z);
            this.tank = tank;
        }
    }
    
    public static class LiquidDrainingEvent extends LiquidEvent
    {
        public final ILiquidTank tank;
        
        public LiquidDrainingEvent(final LiquidStack liquid, final abw world, final int x, final int y, final int z, final ILiquidTank tank) {
            super(liquid, world, x, y, z);
            this.tank = tank;
        }
    }
    
    public static class LiquidSpilledEvent extends LiquidEvent
    {
        public LiquidSpilledEvent(final LiquidStack liquid, final abw world, final int x, final int y, final int z) {
            super(liquid, world, x, y, z);
        }
    }
}
