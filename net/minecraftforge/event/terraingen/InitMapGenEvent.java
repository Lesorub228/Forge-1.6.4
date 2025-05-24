// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;

public class InitMapGenEvent extends Event
{
    public final EventType type;
    public final aer originalGen;
    public aer newGen;
    
    InitMapGenEvent(final EventType type, final aer original) {
        this.type = type;
        this.originalGen = original;
        this.newGen = original;
    }
    
    public enum EventType
    {
        CAVE, 
        MINESHAFT, 
        NETHER_BRIDGE, 
        NETHER_CAVE, 
        RAVINE, 
        SCATTERED_FEATURE, 
        STRONGHOLD, 
        VILLAGE, 
        CUSTOM;
    }
}
