// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

public interface IFluidBlock
{
    Fluid getFluid();
    
    FluidStack drain(final abw p0, final int p1, final int p2, final int p3, final boolean p4);
    
    boolean canDrain(final abw p0, final int p1, final int p2, final int p3);
    
    float getFilledPercentage(final abw p0, final int p1, final int p2, final int p3);
}
