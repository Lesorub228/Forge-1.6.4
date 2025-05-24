// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.ModClassLoader;

public class FMLConstructionEvent extends FMLStateEvent
{
    private ModClassLoader modClassLoader;
    private ASMDataTable asmData;
    
    public FMLConstructionEvent(final Object... eventData) {
        super(new Object[0]);
        this.modClassLoader = (ModClassLoader)eventData[0];
        this.asmData = (ASMDataTable)eventData[1];
    }
    
    public ModClassLoader getModClassLoader() {
        return this.modClassLoader;
    }
    
    @Override
    public LoaderState.ModState getModState() {
        return LoaderState.ModState.CONSTRUCTED;
    }
    
    public ASMDataTable getASMHarvestedData() {
        return this.asmData;
    }
}
