// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.item;

import net.minecraftforge.event.entity.EntityEvent;

public class ItemEvent extends EntityEvent
{
    public final ss entityItem;
    
    public ItemEvent(final ss itemEntity) {
        super((nn)itemEntity);
        this.entityItem = itemEntity;
    }
}
