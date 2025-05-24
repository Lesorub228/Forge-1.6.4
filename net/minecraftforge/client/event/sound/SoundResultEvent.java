// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event.sound;

public abstract class SoundResultEvent extends SoundEvent
{
    public final bln manager;
    public final blm source;
    public final String name;
    public final float volume;
    public final float pitch;
    public blm result;
    
    public SoundResultEvent(final bln manager, final blm source, final String name, final float volume, final float pitch) {
        this.manager = manager;
        this.source = source;
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
        this.result = source;
    }
}
