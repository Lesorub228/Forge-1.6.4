// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class ArrowLooseEvent extends PlayerEvent
{
    public final ye bow;
    public int charge;
    
    public ArrowLooseEvent(final uf player, final ye bow, final int charge) {
        super(player);
        this.bow = bow;
        this.charge = charge;
    }
}
