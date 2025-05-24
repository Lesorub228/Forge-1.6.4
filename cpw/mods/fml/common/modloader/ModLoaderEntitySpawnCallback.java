// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import com.google.common.base.Function;

public class ModLoaderEntitySpawnCallback implements Function<EntitySpawnPacket, nn>
{
    private BaseModProxy mod;
    private EntityRegistry.EntityRegistration registration;
    private boolean isAnimal;
    
    public ModLoaderEntitySpawnCallback(final BaseModProxy mod, final EntityRegistry.EntityRegistration er) {
        this.mod = mod;
        this.registration = er;
    }
    
    public nn apply(final EntitySpawnPacket input) {
        return ModLoaderHelper.sidedHelper.spawnEntity(this.mod, input, this.registration);
    }
}
