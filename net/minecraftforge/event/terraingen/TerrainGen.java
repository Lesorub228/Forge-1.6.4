// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.event.terraingen;

import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import java.util.Random;

public abstract class TerrainGen
{
    public static ajt[] getModdedNoiseGenerators(final abw world, final Random rand, final ajt[] original) {
        final InitNoiseGensEvent event = new InitNoiseGensEvent(world, rand, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.newNoiseGens;
    }
    
    public static aer getModdedMapGen(final aer original, final InitMapGenEvent.EventType type) {
        final InitMapGenEvent event = new InitMapGenEvent(type, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.newGen;
    }
    
    public static boolean populate(final ado chunkProvider, final abw world, final Random rand, final int chunkX, final int chunkZ, final boolean hasVillageGenerated, final PopulateChunkEvent.Populate.EventType type) {
        final PopulateChunkEvent.Populate event = new PopulateChunkEvent.Populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }
    
    public static boolean decorate(final abw world, final Random rand, final int chunkX, final int chunkZ, final DecorateBiomeEvent.Decorate.EventType type) {
        final DecorateBiomeEvent.Decorate event = new DecorateBiomeEvent.Decorate(world, rand, chunkX, chunkZ, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }
    
    public static boolean generateOre(final abw world, final Random rand, final afe generator, final int worldX, final int worldZ, final OreGenEvent.GenerateMinable.EventType type) {
        final OreGenEvent.GenerateMinable event = new OreGenEvent.GenerateMinable(world, rand, generator, worldX, worldZ, type);
        MinecraftForge.ORE_GEN_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }
    
    public static boolean saplingGrowTree(final abw world, final Random rand, final int x, final int y, final int z) {
        final SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, x, y, z);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Event.Result.DENY;
    }
}
