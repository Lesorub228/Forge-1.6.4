// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class PlaySoundAtEntityEvent extends EntityEvent
{
    public String name;
    public final float volume;
    public final float pitch;
    
    public PlaySoundAtEntityEvent(final nn entity, final String name, final float volume, final float pitch) {
        super(entity);
        this.name = name;
        this.volume = volume;
        this.pitch = pitch;
    }
}
