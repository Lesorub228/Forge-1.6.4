// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;

public class FMLInitializationEvent extends FMLStateEvent
{
    public FMLInitializationEvent(final Object... data) {
        super(data);
    }
    
    @Override
    public LoaderState.ModState getModState() {
        return LoaderState.ModState.INITIALIZED;
    }
}
