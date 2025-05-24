// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.player;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.Cancelable;

@Cancelable
@HasResult
public class FillBucketEvent extends PlayerEvent
{
    public final ye current;
    public final abw world;
    public final ata target;
    public ye result;
    
    public FillBucketEvent(final uf player, final ye current, final abw world, final ata target) {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }
}
