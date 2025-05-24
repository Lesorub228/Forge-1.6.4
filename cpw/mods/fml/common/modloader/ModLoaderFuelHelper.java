// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IFuelHandler;

public class ModLoaderFuelHelper implements IFuelHandler
{
    private BaseModProxy mod;
    
    public ModLoaderFuelHelper(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public int getBurnTime(final ye fuel) {
        return this.mod.addFuel(fuel.d, fuel.k());
    }
}
