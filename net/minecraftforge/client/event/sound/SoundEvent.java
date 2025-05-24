// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.event.sound;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;

public class SoundEvent extends Event
{
    public static blm getResult(final SoundResultEvent event) {
        MinecraftForge.EVENT_BUS.post(event);
        return event.result;
    }
}
