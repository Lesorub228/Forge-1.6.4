// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event.sound;

public class PlaySoundSourceEvent extends SoundEvent
{
    public final bln manager;
    public final String name;
    public final float x;
    public final float y;
    public final float z;
    
    public PlaySoundSourceEvent(final bln manager, final String name, final float x, final float y, final float z) {
        this.manager = manager;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
