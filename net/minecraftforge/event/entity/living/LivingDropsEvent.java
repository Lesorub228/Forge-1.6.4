// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.entity.living;

import java.util.ArrayList;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class LivingDropsEvent extends LivingEvent
{
    public final nb source;
    public final ArrayList<ss> drops;
    public final int lootingLevel;
    public final boolean recentlyHit;
    public final int specialDropValue;
    
    public LivingDropsEvent(final of entity, final nb source, final ArrayList<ss> drops, final int lootingLevel, final boolean recentlyHit, final int specialDropValue) {
        super(entity);
        this.source = source;
        this.drops = drops;
        this.lootingLevel = lootingLevel;
        this.recentlyHit = recentlyHit;
        this.specialDropValue = specialDropValue;
    }
}
