// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class AttackEntityEvent extends PlayerEvent
{
    public final nn target;
    
    public AttackEntityEvent(final uf player, final nn target) {
        super(player);
        this.target = target;
    }
}
