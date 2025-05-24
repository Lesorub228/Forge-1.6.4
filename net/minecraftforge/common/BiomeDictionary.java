// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.EnumSet;
import net.minecraftforge.event.terraingen.DeferredBiomeDecorator;
import cpw.mods.fml.common.FMLLog;
import java.util.Iterator;
import java.util.ArrayList;

public class BiomeDictionary
{
    private static final int BIOME_LIST_SIZE = 256;
    private static BiomeInfo[] biomeList;
    private static ArrayList<acq>[] typeInfoList;
    
    public static boolean registerBiomeType(final acq biome, final Type... types) {
        if (acq.a[biome.N] != null) {
            for (final Type type : types) {
                if (BiomeDictionary.typeInfoList[type.ordinal()] == null) {
                    BiomeDictionary.typeInfoList[type.ordinal()] = new ArrayList<acq>();
                }
                BiomeDictionary.typeInfoList[type.ordinal()].add(biome);
            }
            if (BiomeDictionary.biomeList[biome.N] == null) {
                BiomeDictionary.biomeList[biome.N] = new BiomeInfo(types);
            }
            else {
                for (final Type type : types) {
                    BiomeDictionary.biomeList[biome.N].typeList.add(type);
                }
            }
            return true;
        }
        return false;
    }
    
    public static acq[] getBiomesForType(final Type type) {
        if (BiomeDictionary.typeInfoList[type.ordinal()] != null) {
            return BiomeDictionary.typeInfoList[type.ordinal()].toArray(new acq[0]);
        }
        return new acq[0];
    }
    
    public static Type[] getTypesForBiome(final acq biome) {
        checkRegistration(biome);
        if (BiomeDictionary.biomeList[biome.N] != null) {
            return BiomeDictionary.biomeList[biome.N].typeList.toArray(new Type[0]);
        }
        return new Type[0];
    }
    
    public static boolean areBiomesEquivalent(final acq biomeA, final acq biomeB) {
        final int a = biomeA.N;
        final int b = biomeB.N;
        checkRegistration(biomeA);
        checkRegistration(biomeB);
        if (BiomeDictionary.biomeList[a] != null && BiomeDictionary.biomeList[b] != null) {
            for (final Type type : BiomeDictionary.biomeList[a].typeList) {
                if (containsType(BiomeDictionary.biomeList[b], type)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean isBiomeOfType(final acq biome, final Type type) {
        checkRegistration(biome);
        return BiomeDictionary.biomeList[biome.N] != null && containsType(BiomeDictionary.biomeList[biome.N], type);
    }
    
    public static boolean isBiomeRegistered(final acq biome) {
        return BiomeDictionary.biomeList[biome.N] != null;
    }
    
    public static boolean isBiomeRegistered(final int biomeID) {
        return BiomeDictionary.biomeList[biomeID] != null;
    }
    
    public static void registerAllBiomes() {
        FMLLog.warning("Redundant call to BiomeDictionary.registerAllBiomes ignored", new Object[0]);
    }
    
    public static void registerAllBiomesAndGenerateEvents() {
        for (int i = 0; i < acq.a.length; ++i) {
            final acq biome = acq.a[i];
            if (biome != null) {
                if (biome.I instanceof DeferredBiomeDecorator) {
                    final DeferredBiomeDecorator decorator = (DeferredBiomeDecorator)biome.I;
                    decorator.fireCreateEventAndReplace();
                }
                checkRegistration(biome);
            }
        }
    }
    
    public static void makeBestGuess(final acq biome) {
        if (biome.I.z >= 3) {
            if (biome.e() && biome.F >= 1.0f) {
                registerBiomeType(biome, Type.JUNGLE);
            }
            else if (!biome.e()) {
                registerBiomeType(biome, Type.FOREST);
            }
        }
        else if (biome.E <= 0.3f && biome.E >= 0.0f && (!biome.e() || biome.D >= 0.0f)) {
            registerBiomeType(biome, Type.PLAINS);
        }
        if (biome.e() && biome.D < 0.0f && biome.E <= 0.3f && biome.E >= 0.0f) {
            registerBiomeType(biome, Type.SWAMP);
        }
        if (biome.D <= -0.5f) {
            registerBiomeType(biome, Type.WATER);
        }
        if (biome.E >= 1.5f) {
            registerBiomeType(biome, Type.MOUNTAIN);
        }
        if (biome.c() || biome.F < 0.2f) {
            registerBiomeType(biome, Type.FROZEN);
        }
        if (!biome.e() && biome.F >= 1.0f) {
            registerBiomeType(biome, Type.DESERT);
        }
    }
    
    private static void checkRegistration(final acq biome) {
        if (!isBiomeRegistered(biome)) {
            makeBestGuess(biome);
        }
    }
    
    private static boolean containsType(final BiomeInfo info, final Type type) {
        return info.typeList.contains(type);
    }
    
    private static void registerVanillaBiomes() {
        registerBiomeType(acq.b, Type.WATER);
        registerBiomeType(acq.c, Type.PLAINS);
        registerBiomeType(acq.d, Type.DESERT);
        registerBiomeType(acq.e, Type.MOUNTAIN);
        registerBiomeType(acq.f, Type.FOREST);
        registerBiomeType(acq.g, Type.FOREST, Type.FROZEN);
        registerBiomeType(acq.u, Type.FOREST, Type.FROZEN);
        registerBiomeType(acq.h, Type.SWAMP);
        registerBiomeType(acq.i, Type.WATER);
        registerBiomeType(acq.l, Type.WATER, Type.FROZEN);
        registerBiomeType(acq.m, Type.WATER, Type.FROZEN);
        registerBiomeType(acq.n, Type.FROZEN);
        registerBiomeType(acq.o, Type.FROZEN);
        registerBiomeType(acq.r, Type.BEACH);
        registerBiomeType(acq.s, Type.DESERT);
        registerBiomeType(acq.w, Type.JUNGLE);
        registerBiomeType(acq.x, Type.JUNGLE);
        registerBiomeType(acq.t, Type.FOREST);
        registerBiomeType(acq.k, Type.END);
        registerBiomeType(acq.j, Type.NETHER);
        registerBiomeType(acq.p, Type.MUSHROOM);
        registerBiomeType(acq.v, Type.MOUNTAIN);
        registerBiomeType(acq.q, Type.MUSHROOM, Type.BEACH);
    }
    
    static {
        BiomeDictionary.biomeList = new BiomeInfo[256];
        BiomeDictionary.typeInfoList = new ArrayList[Type.values().length];
        registerVanillaBiomes();
    }
    
    public enum Type
    {
        FOREST, 
        PLAINS, 
        MOUNTAIN, 
        HILLS, 
        SWAMP, 
        WATER, 
        DESERT, 
        FROZEN, 
        JUNGLE, 
        WASTELAND, 
        BEACH, 
        NETHER, 
        END, 
        MUSHROOM, 
        MAGICAL;
    }
    
    private static class BiomeInfo
    {
        public EnumSet<Type> typeList;
        
        public BiomeInfo(final Type[] types) {
            this.typeList = EnumSet.noneOf(Type.class);
            for (final Type t : types) {
                this.typeList.add(t);
            }
        }
    }
}
