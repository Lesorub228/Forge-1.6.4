// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public class LivingSpawnEvent extends LivingEvent
{
    public final abw world;
    public final float x;
    public final float y;
    public final float z;
    
    public LivingSpawnEvent(final og entity, final abw world, final float x, final float y, final float z) {
        super((of)entity);
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @HasResult
    public static class CheckSpawn extends LivingSpawnEvent
    {
        public CheckSpawn(final og entity, final abw world, final float x, final float y, final float z) {
            super(entity, world, x, y, z);
        }
    }
    
    @Cancelable
    public static class SpecialSpawn extends LivingSpawnEvent
    {
        public SpecialSpawn(final og entity, final abw world, final float x, final float y, final float z) {
            super(entity, world, x, y, z);
        }
    }
    
    @HasResult
    public static class AllowDespawn extends LivingSpawnEvent
    {
        public AllowDespawn(final og entity) {
            super(entity, entity.q, (float)entity.u, (float)entity.v, (float)entity.w);
        }
    }
}
