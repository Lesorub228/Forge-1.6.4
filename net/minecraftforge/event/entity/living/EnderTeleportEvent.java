// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class EnderTeleportEvent extends LivingEvent
{
    public double targetX;
    public double targetY;
    public double targetZ;
    public float attackDamage;
    
    public EnderTeleportEvent(final of entity, final double targetX, final double targetY, final double targetZ, final float attackDamage) {
        super(entity);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.attackDamage = attackDamage;
    }
}
