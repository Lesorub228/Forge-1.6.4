// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.discovery;

import cpw.mods.fml.common.ModContainer;
import java.util.List;
import com.google.common.base.Throwables;

public enum ContainerType
{
    JAR((Class<? extends ITypeDiscoverer>)JarDiscoverer.class), 
    DIR((Class<? extends ITypeDiscoverer>)DirectoryDiscoverer.class);
    
    private ITypeDiscoverer discoverer;
    
    private ContainerType(final Class<? extends ITypeDiscoverer> discovererClass) {
        try {
            this.discoverer = (ITypeDiscoverer)discovererClass.newInstance();
        }
        catch (final Exception e) {
            throw Throwables.propagate((Throwable)e);
        }
    }
    
    public List<ModContainer> findMods(final ModCandidate candidate, final ASMDataTable table) {
        return this.discoverer.discover(candidate, table);
    }
}
