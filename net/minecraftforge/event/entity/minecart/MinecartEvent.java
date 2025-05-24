// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.minecart;

import net.minecraftforge.event.entity.EntityEvent;

public class MinecartEvent extends EntityEvent
{
    public final st minecart;
    
    public MinecartEvent(final st minecart) {
        super((nn)minecart);
        this.minecart = minecart;
    }
}
