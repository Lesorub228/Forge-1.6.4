// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.util.Map;
import java.util.concurrent.Callable;

public interface IFMLCallHook extends Callable<Void>
{
    void injectData(final Map<String, Object> p0);
}
