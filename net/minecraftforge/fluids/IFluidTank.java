// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

public interface IFluidTank
{
    FluidStack getFluid();
    
    int getFluidAmount();
    
    int getCapacity();
    
    FluidTankInfo getInfo();
    
    int fill(final FluidStack p0, final boolean p1);
    
    FluidStack drain(final int p0, final boolean p1);
}
