// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class DrawBlockHighlightEvent extends Event
{
    public final bfl context;
    public final uf player;
    public final ata target;
    public final int subID;
    public final ye currentItem;
    public final float partialTicks;
    
    public DrawBlockHighlightEvent(final bfl context, final uf player, final ata target, final int subID, final ye currentItem, final float partialTicks) {
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.currentItem = currentItem;
        this.partialTicks = partialTicks;
    }
}
