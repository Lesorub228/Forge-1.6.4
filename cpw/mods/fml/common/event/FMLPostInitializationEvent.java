// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import com.google.common.base.Throwables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

public class FMLPostInitializationEvent extends FMLStateEvent
{
    public FMLPostInitializationEvent(final Object... data) {
        super(data);
    }
    
    @Override
    public LoaderState.ModState getModState() {
        return LoaderState.ModState.POSTINITIALIZED;
    }
    
    public Object buildSoftDependProxy(final String modId, final String className) {
        if (Loader.isModLoaded(modId)) {
            try {
                final Class<?> clz = Class.forName(className, true, Loader.instance().getModClassLoader());
                return clz.newInstance();
            }
            catch (final Exception e) {
                Throwables.propagateIfPossible((Throwable)e);
                return null;
            }
        }
        return null;
    }
}
