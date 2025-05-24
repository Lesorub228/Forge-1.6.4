// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event.sound;

public class PlaySoundEffectSourceEvent extends SoundEvent
{
    public final bln manager;
    public final String name;
    
    public PlaySoundEffectSourceEvent(final bln manager, final String name) {
        this.manager = manager;
        this.name = name;
    }
}
