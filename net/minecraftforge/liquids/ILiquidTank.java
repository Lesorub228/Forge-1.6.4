// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

@Deprecated
public interface ILiquidTank
{
    LiquidStack getLiquid();
    
    int getCapacity();
    
    int fill(final LiquidStack p0, final boolean p1);
    
    LiquidStack drain(final int p0, final boolean p1);
    
    int getTankPressure();
}
