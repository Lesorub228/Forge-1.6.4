// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.world;

import net.minecraftforge.event.Event;

public class ChunkWatchEvent extends Event
{
    public final abp chunk;
    public final jv player;
    
    public ChunkWatchEvent(final abp chunk, final jv player) {
        this.chunk = chunk;
        this.player = player;
    }
    
    public static class Watch extends ChunkWatchEvent
    {
        public Watch(final abp chunk, final jv player) {
            super(chunk, player);
        }
    }
    
    public static class UnWatch extends ChunkWatchEvent
    {
        public UnWatch(final abp chunkLocation, final jv player) {
            super(chunkLocation, player);
        }
    }
}
