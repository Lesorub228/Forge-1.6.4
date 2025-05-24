// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class FluidEvent extends Event
{
    public final FluidStack fluid;
    public final int x;
    public final int y;
    public final int z;
    public final abw world;
    
    public FluidEvent(final FluidStack fluid, final abw world, final int x, final int y, final int z) {
        this.fluid = fluid;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public static final void fireEvent(final FluidEvent event) {
        MinecraftForge.EVENT_BUS.post(event);
    }
    
    public static class FluidMotionEvent extends FluidEvent
    {
        public FluidMotionEvent(final FluidStack fluid, final abw world, final int x, final int y, final int z) {
            super(fluid, world, x, y, z);
        }
    }
    
    public static class FluidFillingEvent extends FluidEvent
    {
        public final IFluidTank tank;
        
        public FluidFillingEvent(final FluidStack fluid, final abw world, final int x, final int y, final int z, final IFluidTank tank) {
            super(fluid, world, x, y, z);
            this.tank = tank;
        }
    }
    
    public static class FluidDrainingEvent extends FluidEvent
    {
        public final IFluidTank tank;
        
        public FluidDrainingEvent(final FluidStack fluid, final abw world, final int x, final int y, final int z, final IFluidTank tank) {
            super(fluid, world, x, y, z);
            this.tank = tank;
        }
    }
    
    public static class FluidSpilledEvent extends FluidEvent
    {
        public FluidSpilledEvent(final FluidStack fluid, final abw world, final int x, final int y, final int z) {
            super(fluid, world, x, y, z);
        }
    }
}
