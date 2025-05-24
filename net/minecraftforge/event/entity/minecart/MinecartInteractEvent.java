// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.minecart;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class MinecartInteractEvent extends MinecartEvent
{
    public final uf player;
    
    public MinecartInteractEvent(final st minecart, final uf player) {
        super(minecart);
        this.player = player;
    }
}
