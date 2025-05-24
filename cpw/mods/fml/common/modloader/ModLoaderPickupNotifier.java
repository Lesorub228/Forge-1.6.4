// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.IPickupNotifier;

public class ModLoaderPickupNotifier implements IPickupNotifier
{
    private BaseModProxy mod;
    
    public ModLoaderPickupNotifier(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public void notifyPickup(final ss item, final uf player) {
        this.mod.onItemPickup(player, item.d());
    }
}
