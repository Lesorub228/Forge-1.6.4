// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowNockEvent extends PlayerEvent
{
    public ye result;
    
    public ArrowNockEvent(final uf player, final ye result) {
        super(player);
        this.result = result;
    }
}
