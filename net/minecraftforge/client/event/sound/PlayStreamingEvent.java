// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event.sound;

public class PlayStreamingEvent extends SoundResultEvent
{
    public final float x;
    public final float y;
    public final float z;
    
    public PlayStreamingEvent(final bln manager, final blm source, final String name, final float x, final float y, final float z) {
        super(manager, source, name, 0.0f, 0.0f);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
