// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkModHandler;

public class ModLoaderNetworkHandler extends NetworkModHandler
{
    private BaseModProxy baseMod;
    
    public ModLoaderNetworkHandler(final ModLoaderModContainer mlmc) {
        super(mlmc, null);
    }
    
    public void setBaseMod(final BaseModProxy baseMod) {
        this.baseMod = baseMod;
    }
    
    @Override
    public boolean requiresClientSide() {
        return false;
    }
    
    @Override
    public boolean requiresServerSide() {
        return false;
    }
    
    @Override
    public boolean acceptVersion(final String version) {
        return this.baseMod.getVersion().equals(version);
    }
    
    @Override
    public boolean isNetworkMod() {
        return true;
    }
}
