// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

public class LivingSetAttackTargetEvent extends LivingEvent
{
    public final of target;
    
    public LivingSetAttackTargetEvent(final of entity, final of target) {
        super(entity);
        this.target = target;
    }
}
