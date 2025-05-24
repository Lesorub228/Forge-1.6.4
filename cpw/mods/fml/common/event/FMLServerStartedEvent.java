// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.LoaderState;

public class FMLServerStartedEvent extends FMLStateEvent
{
    public FMLServerStartedEvent(final Object... data) {
        super(data);
    }
    
    @Override
    public LoaderState.ModState getModState() {
        return LoaderState.ModState.AVAILABLE;
    }
}
