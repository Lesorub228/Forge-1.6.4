// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

@Deprecated
public class LiquidContainerData
{
    public final LiquidStack stillLiquid;
    public final ye filled;
    public final ye container;
    
    public LiquidContainerData(final LiquidStack stillLiquid, final ye filled, final ye container) {
        this.stillLiquid = stillLiquid;
        this.filled = filled;
        this.container = container;
        if (stillLiquid == null || filled == null || container == null) {
            throw new RuntimeException("stillLiquid, filled, or container is null, this is an error");
        }
    }
}
