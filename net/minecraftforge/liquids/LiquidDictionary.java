// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import com.google.common.collect.BiMap;

@Deprecated
public abstract class LiquidDictionary
{
    private static BiMap<String, LiquidStack> liquids;
    
    public static LiquidStack getOrCreateLiquid(final String name, final LiquidStack liquid) {
        if (liquid == null) {
            throw new NullPointerException("You cannot register a null LiquidStack");
        }
        final LiquidStack existing = (LiquidStack)LiquidDictionary.liquids.get((Object)name);
        if (existing != null) {
            return existing.copy();
        }
        LiquidDictionary.liquids.put((Object)name, (Object)liquid.copy());
        MinecraftForge.EVENT_BUS.post(new LiquidRegisterEvent(name, liquid));
        return liquid;
    }
    
    public static LiquidStack getLiquid(final String name, final int amount) {
        LiquidStack liquid = (LiquidStack)LiquidDictionary.liquids.get((Object)name);
        if (liquid == null) {
            return null;
        }
        liquid = liquid.copy();
        liquid.amount = amount;
        return liquid;
    }
    
    public static LiquidStack getCanonicalLiquid(final String name) {
        return (LiquidStack)LiquidDictionary.liquids.get((Object)name);
    }
    
    public static Map<String, LiquidStack> getLiquids() {
        return (Map<String, LiquidStack>)ImmutableMap.copyOf((Map)LiquidDictionary.liquids);
    }
    
    public static String findLiquidName(final LiquidStack reference) {
        if (reference != null) {
            return (String)LiquidDictionary.liquids.inverse().get((Object)reference);
        }
        return null;
    }
    
    public static LiquidStack getCanonicalLiquid(final LiquidStack liquidStack) {
        return (LiquidStack)LiquidDictionary.liquids.get(LiquidDictionary.liquids.inverse().get((Object)liquidStack));
    }
    
    static {
        LiquidDictionary.liquids = (BiMap<String, LiquidStack>)HashBiMap.create();
        getOrCreateLiquid("Water", new LiquidStack(aqz.G, 1000));
        getOrCreateLiquid("Lava", new LiquidStack(aqz.I, 1000));
    }
    
    public static class LiquidRegisterEvent extends Event
    {
        public final String Name;
        public final LiquidStack Liquid;
        
        public LiquidRegisterEvent(final String name, final LiquidStack liquid) {
            this.Name = name;
            this.Liquid = liquid.copy();
        }
    }
}
