// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import java.util.Random;
import cpw.mods.fml.common.IWorldGenerator;

public class ModLoaderWorldGenerator implements IWorldGenerator
{
    private BaseModProxy mod;
    
    public ModLoaderWorldGenerator(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public void generate(final Random random, final int chunkX, final int chunkZ, final abw world, final ado chunkGenerator, final ado chunkProvider) {
        if (chunkGenerator instanceof aet) {
            this.mod.generateSurface(world, random, chunkX << 4, chunkZ << 4);
        }
        else if (chunkGenerator instanceof aep) {
            this.mod.generateNether(world, random, chunkX << 4, chunkZ << 4);
        }
    }
}
