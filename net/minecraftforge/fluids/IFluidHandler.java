// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import net.minecraftforge.common.ForgeDirection;

public interface IFluidHandler
{
    int fill(final ForgeDirection p0, final FluidStack p1, final boolean p2);
    
    FluidStack drain(final ForgeDirection p0, final FluidStack p1, final boolean p2);
    
    FluidStack drain(final ForgeDirection p0, final int p1, final boolean p2);
    
    boolean canFill(final ForgeDirection p0, final Fluid p1);
    
    boolean canDrain(final ForgeDirection p0, final Fluid p1);
    
    FluidTankInfo[] getTankInfo(final ForgeDirection p0);
}
