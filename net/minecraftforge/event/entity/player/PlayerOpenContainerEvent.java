// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Event;

@HasResult
public class PlayerOpenContainerEvent extends PlayerEvent
{
    public final boolean canInteractWith;
    
    public PlayerOpenContainerEvent(final uf player, final uy openContainer) {
        super(player);
        this.canInteractWith = openContainer.a(player);
    }
}
