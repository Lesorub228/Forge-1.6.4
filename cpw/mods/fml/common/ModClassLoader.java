// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.asm.transformers.ModAPITransformer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import java.util.Set;
import java.util.Iterator;
import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import cpw.mods.fml.common.modloader.BaseModProxy;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.net.MalformedURLException;
import java.io.File;
import java.net.URL;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.util.List;
import java.net.URLClassLoader;

public class ModClassLoader extends URLClassLoader
{
    private static final List<String> STANDARD_LIBRARIES;
    private LaunchClassLoader mainClassLoader;
    
    public ModClassLoader(final ClassLoader parent) {
        super(new URL[0], (ClassLoader)null);
        this.mainClassLoader = (LaunchClassLoader)parent;
    }
    
    public void addFile(final File modFile) throws MalformedURLException {
        final URL url = modFile.toURI().toURL();
        this.mainClassLoader.addURL(url);
    }
    
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return this.mainClassLoader.loadClass(name);
    }
    
    public File[] getParentSources() {
        final List<URL> urls = this.mainClassLoader.getSources();
        final File[] sources = new File[urls.size()];
        try {
            for (int i = 0; i < urls.size(); ++i) {
                sources[i] = new File(urls.get(i).toURI());
            }
            return sources;
        }
        catch (final URISyntaxException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to process our input to locate the minecraft code", new Object[0]);
            throw new LoaderException(e);
        }
    }
    
    public List<String> getDefaultLibraries() {
        return ModClassLoader.STANDARD_LIBRARIES;
    }
    
    public Class<? extends BaseModProxy> loadBaseModClass(final String modClazzName) throws Exception {
        AccessTransformer accessTransformer = null;
        for (final IClassTransformer transformer : this.mainClassLoader.getTransformers()) {
            if (transformer instanceof AccessTransformer) {
                accessTransformer = (AccessTransformer)transformer;
                break;
            }
        }
        if (accessTransformer == null) {
            FMLLog.log(Level.SEVERE, "No access transformer found", new Object[0]);
            throw new LoaderException();
        }
        accessTransformer.ensurePublicAccessFor(modClazzName);
        return (Class<? extends BaseModProxy>)Class.forName(modClazzName, true, this);
    }
    
    public void clearNegativeCacheFor(final Set<String> classList) {
        this.mainClassLoader.clearNegativeEntries((Set)classList);
    }
    
    public ModAPITransformer addModAPITransformer(final ASMDataTable dataTable) {
        this.mainClassLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.ModAPITransformer");
        final List<IClassTransformer> transformers = this.mainClassLoader.getTransformers();
        final ModAPITransformer modAPI = (ModAPITransformer)transformers.get(transformers.size() - 1);
        modAPI.initTable(dataTable);
        return modAPI;
    }
    
    static {
        STANDARD_LIBRARIES = (List)ImmutableList.of((Object)"jinput.jar", (Object)"lwjgl.jar", (Object)"lwjgl_util.jar");
    }
}
