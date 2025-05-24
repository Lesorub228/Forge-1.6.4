// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.EnumSet;

public enum TickType
{
    WORLD, 
    RENDER, 
    WORLDLOAD, 
    CLIENT, 
    PLAYER, 
    SERVER;
    
    public EnumSet<TickType> partnerTicks() {
        if (this == TickType.CLIENT) {
            return EnumSet.of(TickType.RENDER);
        }
        if (this == TickType.RENDER) {
            return EnumSet.of(TickType.CLIENT);
        }
        return EnumSet.noneOf(TickType.class);
    }
}
