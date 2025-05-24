// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client;

import net.minecraftforge.common.EnumHelper;

public class EnumHelperClient extends EnumHelper
{
    private static Class[][] clentTypes;
    
    public static ace addGameType(final String name, final int id, final String displayName) {
        return addEnum(ace.class, name, id, displayName);
    }
    
    public static aun addOptions(final String name, final String langName, final boolean isSlider, final boolean isToggle) {
        return addEnum(aun.class, name, langName, isSlider, isToggle);
    }
    
    public static x addOS2(final String name) {
        return addEnum(x.class, name, new Object[0]);
    }
    
    public static yq addRarity(final String name, final int color, final String displayName) {
        return addEnum(yq.class, name, color, displayName);
    }
    
    public static <T extends Enum<?>> T addEnum(final Class<T> enumType, final String enumName, final Object... paramValues) {
        return EnumHelper.addEnum(EnumHelperClient.clentTypes, enumType, enumName, paramValues);
    }
    
    static {
        EnumHelperClient.clentTypes = new Class[][] { { ace.class, Integer.TYPE, String.class }, { aun.class, String.class, Boolean.TYPE, Boolean.TYPE }, { x.class }, { yq.class, Integer.TYPE, String.class } };
    }
}
