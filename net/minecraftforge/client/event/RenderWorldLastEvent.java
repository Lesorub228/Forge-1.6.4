// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event;

import net.minecraftforge.event.Event;

public class RenderWorldLastEvent extends Event
{
    public final bfl context;
    public final float partialTicks;
    
    public RenderWorldLastEvent(final bfl context, final float partialTicks) {
        this.context = context;
        this.partialTicks = partialTicks;
    }
}
