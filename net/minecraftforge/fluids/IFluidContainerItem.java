// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

public interface IFluidContainerItem
{
    FluidStack getFluid(final ye p0);
    
    int getCapacity(final ye p0);
    
    int fill(final ye p0, final FluidStack p1, final boolean p2);
    
    FluidStack drain(final ye p0, final int p1, final boolean p2);
}
