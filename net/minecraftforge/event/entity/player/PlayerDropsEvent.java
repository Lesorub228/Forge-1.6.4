// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import java.util.ArrayList;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

@Cancelable
public class PlayerDropsEvent extends LivingDropsEvent
{
    public final uf entityPlayer;
    
    public PlayerDropsEvent(final uf entity, final nb source, final ArrayList<ss> drops, final boolean recentlyHit) {
        super((of)entity, source, drops, (source.i() instanceof uf) ? aaw.g((of)source.i()) : 0, recentlyHit, 0);
        this.entityPlayer = entity;
    }
}
