// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity;

import net.minecraftforge.event.Event;

public class EntityEvent extends Event
{
    public final nn entity;
    
    public EntityEvent(final nn entity) {
        this.entity = entity;
    }
    
    public static class EntityConstructing extends EntityEvent
    {
        public EntityConstructing(final nn entity) {
            super(entity);
        }
    }
    
    public static class CanUpdate extends EntityEvent
    {
        public boolean canUpdate;
        
        public CanUpdate(final nn entity) {
            super(entity);
            this.canUpdate = false;
        }
    }
    
    public static class EnteringChunk extends EntityEvent
    {
        public int newChunkX;
        public int newChunkZ;
        public int oldChunkX;
        public int oldChunkZ;
        
        public EnteringChunk(final nn entity, final int newChunkX, final int newChunkZ, final int oldChunkX, final int oldChunkZ) {
            super(entity);
            this.newChunkX = newChunkX;
            this.newChunkZ = newChunkZ;
            this.oldChunkX = oldChunkX;
            this.oldChunkZ = oldChunkZ;
        }
    }
}
