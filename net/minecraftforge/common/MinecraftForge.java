// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.FMLLog;
import java.util.List;
import java.util.Arrays;
import java.io.Serializable;
import net.minecraftforge.event.EventBus;

public class MinecraftForge
{
    public static final EventBus EVENT_BUS;
    public static final EventBus TERRAIN_GEN_BUS;
    public static final EventBus ORE_GEN_BUS;
    private static final ForgeInternalHandler INTERNAL_HANDLER;
    
    public static void addGrassPlant(final aqz block, final int metadata, final int weight) {
        ForgeHooks.grassList.add(new ForgeHooks.GrassEntry(block, metadata, weight));
    }
    
    public static void addGrassSeed(final ye seed, final int weight) {
        ForgeHooks.seedList.add(new ForgeHooks.SeedEntry(seed, weight));
    }
    
    public static void setToolClass(final yc tool, final String toolClass, final int harvestLevel) {
        ForgeHooks.toolClasses.put(tool, Arrays.asList(toolClass, harvestLevel));
    }
    
    public static void setBlockHarvestLevel(final aqz block, final int metadata, final String toolClass, final int harvestLevel) {
        final List key = Arrays.asList(block, metadata, toolClass);
        ForgeHooks.toolHarvestLevels.put(key, harvestLevel);
        ForgeHooks.toolEffectiveness.add(key);
    }
    
    public static void removeBlockEffectiveness(final aqz block, final int metadata, final String toolClass) {
        final List key = Arrays.asList(block, metadata, toolClass);
        ForgeHooks.toolEffectiveness.remove(key);
    }
    
    public static void setBlockHarvestLevel(final aqz block, final String toolClass, final int harvestLevel) {
        for (int metadata = 0; metadata < 16; ++metadata) {
            final List key = Arrays.asList(block, metadata, toolClass);
            ForgeHooks.toolHarvestLevels.put(key, harvestLevel);
            ForgeHooks.toolEffectiveness.add(key);
        }
    }
    
    public static int getBlockHarvestLevel(final aqz block, final int metadata, final String toolClass) {
        ForgeHooks.initTools();
        final List key = Arrays.asList(block, metadata, toolClass);
        final Integer harvestLevel = ForgeHooks.toolHarvestLevels.get(key);
        return (harvestLevel == null) ? -1 : harvestLevel;
    }
    
    public static void removeBlockEffectiveness(final aqz block, final String toolClass) {
        for (int metadata = 0; metadata < 16; ++metadata) {
            final List key = Arrays.asList(block, metadata, toolClass);
            ForgeHooks.toolEffectiveness.remove(key);
        }
    }
    
    public static void initialize() {
        System.out.printf("MinecraftForge v%s Initialized\n", ForgeVersion.getVersion());
        FMLLog.info("MinecraftForge v%s Initialized", ForgeVersion.getVersion());
        final aqz filler = new aqz(0, akc.a) {
            @SideOnly(Side.CLIENT)
            public void a(final mt register) {
            }
        };
        aqz.s[0] = null;
        aqz.t[0] = false;
        aqz.u[0] = 0;
        filler.c("ForgeFiller");
        for (int x = 256; x < 4096; ++x) {
            if (yc.g[x] != null) {
                aqz.s[x] = filler;
            }
        }
        final boolean[] temp = new boolean[4096];
        System.arraycopy(tg.br, 0, temp, 0, tg.br.length);
        tg.br = temp;
        MinecraftForge.EVENT_BUS.register(MinecraftForge.INTERNAL_HANDLER);
        OreDictionary.getOreName(0);
        new b("ThisIsFake", (Throwable)new Exception("Not real"));
    }
    
    public static String getBrandingVersion() {
        return "Minecraft Forge " + ForgeVersion.getVersion();
    }
    
    static {
        EVENT_BUS = new EventBus();
        TERRAIN_GEN_BUS = new EventBus();
        ORE_GEN_BUS = new EventBus();
        INTERNAL_HANDLER = new ForgeInternalHandler();
    }
}
