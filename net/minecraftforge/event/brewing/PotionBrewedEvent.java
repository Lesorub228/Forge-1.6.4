// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.brewing;

import net.minecraftforge.event.Event;

public class PotionBrewedEvent extends Event
{
    public ye[] brewingStacks;
    
    public PotionBrewedEvent(final ye[] brewingStacks) {
        this.brewingStacks = brewingStacks;
    }
}
