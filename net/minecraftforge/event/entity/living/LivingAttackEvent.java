// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingAttackEvent extends LivingEvent
{
    public final nb source;
    public final float ammount;
    
    public LivingAttackEvent(final of entity, final nb source, final float ammount) {
        super(entity);
        this.source = source;
        this.ammount = ammount;
    }
}
