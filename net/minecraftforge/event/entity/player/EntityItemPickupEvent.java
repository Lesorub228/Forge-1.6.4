// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Cancelable;

@Cancelable
@HasResult
public class EntityItemPickupEvent extends PlayerEvent
{
    public final ss item;
    private boolean handled;
    
    public EntityItemPickupEvent(final uf player, final ss item) {
        super(player);
        this.handled = false;
        this.item = item;
    }
}
