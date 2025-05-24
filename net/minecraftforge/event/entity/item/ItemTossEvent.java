// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.item;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class ItemTossEvent extends ItemEvent
{
    public final uf player;
    
    public ItemTossEvent(final ss entityItem, final uf player) {
        super(entityItem);
        this.player = player;
    }
}
