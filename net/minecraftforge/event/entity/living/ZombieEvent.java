// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Event;
import net.minecraftforge.event.entity.EntityEvent;

public class ZombieEvent extends EntityEvent
{
    public ZombieEvent(final tw entity) {
        super((nn)entity);
    }
    
    public tw getSummoner() {
        return (tw)this.entity;
    }
    
    @HasResult
    public static class SummonAidEvent extends ZombieEvent
    {
        public tw customSummonedAid;
        public final abw world;
        public final int x;
        public final int y;
        public final int z;
        public final of attacker;
        public final double summonChance;
        
        public SummonAidEvent(final tw entity, final abw world, final int x, final int y, final int z, final of attacker, final double summonChance) {
            super(entity);
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.attacker = attacker;
            this.summonChance = summonChance;
        }
    }
}
