// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

import net.minecraftforge.common.ForgeDirection;

@Deprecated
public interface ITankContainer
{
    int fill(final ForgeDirection p0, final LiquidStack p1, final boolean p2);
    
    int fill(final int p0, final LiquidStack p1, final boolean p2);
    
    LiquidStack drain(final ForgeDirection p0, final int p1, final boolean p2);
    
    LiquidStack drain(final int p0, final int p1, final boolean p2);
    
    ILiquidTank[] getTanks(final ForgeDirection p0);
    
    ILiquidTank getTank(final ForgeDirection p0, final LiquidStack p1);
}
