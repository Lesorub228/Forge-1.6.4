// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.item;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class ItemExpireEvent extends ItemEvent
{
    public int extraLife;
    
    public ItemExpireEvent(final ss entityItem, final int extraLife) {
        super(entityItem);
        this.extraLife = extraLife;
    }
}
