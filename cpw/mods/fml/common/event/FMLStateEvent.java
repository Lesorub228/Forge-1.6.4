// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.LoaderState;

public abstract class FMLStateEvent extends FMLEvent
{
    public FMLStateEvent(final Object... data) {
    }
    
    public abstract LoaderState.ModState getModState();
    
    public Side getSide() {
        return FMLCommonHandler.instance().getSide();
    }
}
