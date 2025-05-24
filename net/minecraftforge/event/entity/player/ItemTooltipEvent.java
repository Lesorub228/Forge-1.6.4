// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import java.util.List;

public class ItemTooltipEvent extends PlayerEvent
{
    public final boolean showAdvancedItemTooltips;
    public final ye itemStack;
    public final List<String> toolTip;
    
    public ItemTooltipEvent(final ye itemStack, final uf entityPlayer, final List<String> toolTip, final boolean showAdvancedItemTooltips) {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.toolTip = toolTip;
        this.showAdvancedItemTooltips = showAdvancedItemTooltips;
    }
}
