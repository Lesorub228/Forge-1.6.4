// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.Collection;
import java.util.ArrayList;

public class BiomeManager
{
    public static void addVillageBiome(final acq biome, final boolean canSpawn) {
        if (!aiw.e.contains(biome)) {
            final ArrayList<acq> biomes = new ArrayList<acq>(aiw.e);
            biomes.add(biome);
            aiw.e = biomes;
        }
    }
    
    public static void removeVillageBiome(final acq biome) {
        if (aiw.e.contains(biome)) {
            final ArrayList<acq> biomes = new ArrayList<acq>(aiw.e);
            biomes.remove(biome);
            aiw.e = biomes;
        }
    }
    
    public static void addStrongholdBiome(final acq biome) {
        if (!ahq.allowedBiomes.contains(biome)) {
            ahq.allowedBiomes.add(biome);
        }
    }
    
    public static void removeStrongholdBiome(final acq biome) {
        if (ahq.allowedBiomes.contains(biome)) {
            ahq.allowedBiomes.remove(biome);
        }
    }
    
    public static void addSpawnBiome(final acq biome) {
        if (!acv.allowedBiomes.contains(biome)) {
            acv.allowedBiomes.add(biome);
        }
    }
    
    public static void removeSpawnBiome(final acq biome) {
        if (acv.allowedBiomes.contains(biome)) {
            acv.allowedBiomes.remove(biome);
        }
    }
}
