// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event.sound;

public class PlaySoundEvent extends SoundResultEvent
{
    public final float x;
    public final float y;
    public final float z;
    
    public PlaySoundEvent(final bln manager, final blm source, final String name, final float x, final float y, final float z, final float volume, final float pitch) {
        super(manager, source, name, volume, pitch);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
