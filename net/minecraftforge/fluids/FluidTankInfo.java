// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

public final class FluidTankInfo
{
    public final FluidStack fluid;
    public final int capacity;
    
    public FluidTankInfo(final FluidStack fluid, final int capacity) {
        this.fluid = fluid;
        this.capacity = capacity;
    }
    
    public FluidTankInfo(final IFluidTank tank) {
        this.fluid = tank.getFluid();
        this.capacity = tank.getCapacity();
    }
}
