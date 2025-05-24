// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import com.google.common.collect.Sets;
import java.util.Set;
import cpw.mods.fml.common.network.IGuiHandler;

public class ModLoaderGuiHelper implements IGuiHandler
{
    private BaseModProxy mod;
    private Set<Integer> ids;
    private uy container;
    private int currentID;
    
    ModLoaderGuiHelper(final BaseModProxy mod) {
        this.mod = mod;
        this.ids = Sets.newHashSet();
    }
    
    @Override
    public Object getServerGuiElement(final int ID, final uf player, final abw world, final int x, final int y, final int z) {
        return this.container;
    }
    
    @Override
    public Object getClientGuiElement(final int ID, final uf player, final abw world, final int x, final int y, final int z) {
        return ModLoaderHelper.getClientSideGui(this.mod, player, ID, x, y, z);
    }
    
    public void injectContainerAndID(final uy container, final int ID) {
        this.container = container;
        this.currentID = ID;
    }
    
    public Object getMod() {
        return this.mod;
    }
    
    public void associateId(final int additionalID) {
        this.ids.add(additionalID);
    }
}
