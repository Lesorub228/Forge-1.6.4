// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.ICraftingHandler;

public class ModLoaderCraftingHelper implements ICraftingHandler
{
    private BaseModProxy mod;
    
    public ModLoaderCraftingHelper(final BaseModProxy mod) {
        this.mod = mod;
    }
    
    @Override
    public void onCrafting(final uf player, final ye item, final mo craftMatrix) {
        this.mod.takenFromCrafting(player, item, craftMatrix);
    }
    
    @Override
    public void onSmelting(final uf player, final ye item) {
        this.mod.takenFromFurnace(player, item);
    }
}
