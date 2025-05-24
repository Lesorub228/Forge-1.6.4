// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.util.HashMap;
import java.util.Comparator;
import net.minecraft.launchwrapper.ITweaker;
import cpw.mods.fml.common.launcher.FMLInjectionAndSortingTweaker;
import net.minecraft.launchwrapper.Launch;
import java.util.Iterator;
import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.toposort.TopologicalSort;
import com.google.common.collect.Lists;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import com.google.common.primitives.Ints;
import com.google.common.base.Strings;
import java.util.jar.JarFile;
import java.util.Arrays;
import com.google.common.collect.ObjectArrays;
import java.io.FilenameFilter;
import java.util.ArrayList;
import com.google.common.base.Throwables;
import java.util.logging.Level;
import java.io.IOException;
import net.minecraft.launchwrapper.LaunchClassLoader;
import java.util.Map;
import java.lang.reflect.Method;
import java.io.File;
import cpw.mods.fml.common.launcher.FMLTweaker;
import java.util.List;
import java.util.jar.Attributes;

public class CoreModManager
{
    private static final Attributes.Name COREMODCONTAINSFMLMOD;
    private static String[] rootPlugins;
    private static List<String> loadedCoremods;
    private static List<FMLPluginWrapper> loadPlugins;
    private static boolean deobfuscatedEnvironment;
    private static FMLTweaker tweaker;
    private static File mcDir;
    private static List<String> reparsedCoremods;
    private static Method ADDURL;
    private static Map<String, Integer> tweakSorting;
    
    public static void handleLaunch(final File mcDir, final LaunchClassLoader classLoader, final FMLTweaker tweaker) {
        CoreModManager.mcDir = mcDir;
        CoreModManager.tweaker = tweaker;
        try {
            final byte[] bs = classLoader.getClassBytes("net.minecraft.world.World");
            if (bs != null) {
                FMLRelaunchLog.info("Managed to load a deobfuscated Minecraft name- we are in a deobfuscated environment. Skipping runtime deobfuscation", new Object[0]);
                CoreModManager.deobfuscatedEnvironment = true;
            }
        }
        catch (final IOException ex) {}
        if (!CoreModManager.deobfuscatedEnvironment) {
            FMLRelaunchLog.fine("Enabling runtime deobfuscation", new Object[0]);
        }
        tweaker.injectCascadingTweak("cpw.mods.fml.common.launcher.FMLInjectionAndSortingTweaker");
        try {
            classLoader.registerTransformer("cpw.mods.fml.common.asm.transformers.PatchingTransformer");
        }
        catch (final Exception e) {
            FMLRelaunchLog.log(Level.SEVERE, e, "The patch transformer failed to load! This is critical, loading cannot continue!", new Object[0]);
            throw Throwables.propagate((Throwable)e);
        }
        CoreModManager.loadPlugins = new ArrayList<FMLPluginWrapper>();
        for (final String rootPluginName : CoreModManager.rootPlugins) {
            loadCoreMod(classLoader, rootPluginName, new File(FMLTweaker.getJarLocation()));
        }
        if (CoreModManager.loadPlugins.isEmpty()) {
            throw new RuntimeException("A fatal error has occured - no valid fml load plugin was found - this is a completely corrupt FML installation.");
        }
        FMLRelaunchLog.fine("All fundamental core mods are successfully located", new Object[0]);
        final String commandLineCoremods = System.getProperty("fml.coreMods.load", "");
        for (final String coreModClassName : commandLineCoremods.split(",")) {
            if (!coreModClassName.isEmpty()) {
                FMLRelaunchLog.info("Found a command line coremod : %s", coreModClassName);
                loadCoreMod(classLoader, coreModClassName, null);
            }
        }
        discoverCoreMods(mcDir, classLoader);
    }
    
    private static void discoverCoreMods(final File mcDir, final LaunchClassLoader classLoader) {
        FMLRelaunchLog.fine("Discovering coremods", new Object[0]);
        final File coreMods = setupCoreModDir(mcDir);
        final FilenameFilter ff = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".jar");
            }
        };
        File[] coreModList = coreMods.listFiles(ff);
        final File versionedModDir = new File(coreMods, FMLInjectionData.mccversion);
        if (versionedModDir.isDirectory()) {
            final File[] versionedCoreMods = versionedModDir.listFiles(ff);
            coreModList = (File[])ObjectArrays.concat((Object[])coreModList, (Object[])versionedCoreMods, (Class)File.class);
        }
        Arrays.sort(coreModList);
        for (final File coreMod : coreModList) {
            FMLRelaunchLog.fine("Examining for coremod candidacy %s", coreMod.getName());
            JarFile jar = null;
            Attributes mfAttributes = null;
            try {
                jar = new JarFile(coreMod);
                if (jar.getManifest() == null) {}
                mfAttributes = jar.getManifest().getMainAttributes();
            }
            catch (final IOException ioe) {
                FMLRelaunchLog.log(Level.SEVERE, ioe, "Unable to read the jar file %s - ignoring", coreMod.getName());
            }
            finally {
                if (jar != null) {
                    try {
                        jar.close();
                    }
                    catch (final IOException ex) {}
                }
            }
            final String cascadedTweaker = mfAttributes.getValue("TweakClass");
            Label_0508: {
                if (cascadedTweaker != null) {
                    FMLRelaunchLog.info("Loading tweaker %s from %s", cascadedTweaker, coreMod.getName());
                    Integer sortOrder = Ints.tryParse(Strings.nullToEmpty(mfAttributes.getValue("TweakOrder")));
                    sortOrder = ((sortOrder == null) ? Integer.valueOf(0) : sortOrder);
                    handleCascadingTweak(coreMod, jar, cascadedTweaker, classLoader, sortOrder);
                    CoreModManager.loadedCoremods.add(coreMod.getName());
                }
                else {
                    final String fmlCorePlugin = mfAttributes.getValue("FMLCorePlugin");
                    if (fmlCorePlugin == null) {
                        FMLRelaunchLog.fine("Not found coremod data in %s", coreMod.getName());
                    }
                    else {
                        try {
                            classLoader.addURL(coreMod.toURI().toURL());
                            if (!mfAttributes.containsKey(CoreModManager.COREMODCONTAINSFMLMOD)) {
                                FMLRelaunchLog.finest("Adding %s to the list of known coremods, it will not be examined again", coreMod.getName());
                                CoreModManager.loadedCoremods.add(coreMod.getName());
                            }
                            else {
                                FMLRelaunchLog.finest("Found FMLCorePluginContainsFMLMod marker in %s, it will be examined later for regular @Mod instances", coreMod.getName());
                                CoreModManager.reparsedCoremods.add(coreMod.getName());
                            }
                        }
                        catch (final MalformedURLException e) {
                            FMLRelaunchLog.log(Level.SEVERE, e, "Unable to convert file into a URL. weird", new Object[0]);
                            break Label_0508;
                        }
                        loadCoreMod(classLoader, fmlCorePlugin, coreMod);
                    }
                }
            }
        }
    }
    
    private static void handleCascadingTweak(final File coreMod, final JarFile jar, final String cascadedTweaker, final LaunchClassLoader classLoader, final Integer sortingOrder) {
        try {
            if (CoreModManager.ADDURL == null) {
                (CoreModManager.ADDURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class)).setAccessible(true);
            }
            CoreModManager.ADDURL.invoke(classLoader.getClass().getClassLoader(), coreMod.toURI().toURL());
            classLoader.addURL(coreMod.toURI().toURL());
            CoreModManager.tweaker.injectCascadingTweak(cascadedTweaker);
            CoreModManager.tweakSorting.put(cascadedTweaker, sortingOrder);
        }
        catch (final Exception e) {
            FMLRelaunchLog.log(Level.INFO, e, "There was a problem trying to load the mod dir tweaker %s", coreMod.getAbsolutePath());
        }
    }
    
    private static void injectTweakWrapper(final FMLPluginWrapper wrapper) {
        CoreModManager.loadPlugins.add(wrapper);
    }
    
    private static File setupCoreModDir(final File mcDir) {
        File coreModDir = new File(mcDir, "mods");
        try {
            coreModDir = coreModDir.getCanonicalFile();
        }
        catch (final IOException e) {
            throw new RuntimeException(String.format("Unable to canonicalize the coremod dir at %s", mcDir.getName()), e);
        }
        if (!coreModDir.exists()) {
            coreModDir.mkdir();
        }
        else if (coreModDir.exists() && !coreModDir.isDirectory()) {
            throw new RuntimeException(String.format("Found a coremod file in %s that's not a directory", mcDir.getName()));
        }
        return coreModDir;
    }
    
    public static List<String> getLoadedCoremods() {
        return CoreModManager.loadedCoremods;
    }
    
    public static List<String> getReparseableCoremods() {
        return CoreModManager.reparsedCoremods;
    }
    
    private static FMLPluginWrapper loadCoreMod(final LaunchClassLoader classLoader, final String coreModClass, final File location) {
        String coreModName = coreModClass.substring(coreModClass.lastIndexOf(46) + 1);
        try {
            FMLRelaunchLog.fine("Instantiating coremod class %s", coreModName);
            classLoader.addTransformerExclusion(coreModClass);
            final Class<?> coreModClazz = Class.forName(coreModClass, true, (ClassLoader)classLoader);
            final IFMLLoadingPlugin.Name coreModNameAnn = coreModClazz.getAnnotation(IFMLLoadingPlugin.Name.class);
            if (coreModNameAnn != null && !Strings.isNullOrEmpty(coreModNameAnn.value())) {
                coreModName = coreModNameAnn.value();
                FMLRelaunchLog.finest("coremod named %s is loading", coreModName);
            }
            final IFMLLoadingPlugin.MCVersion requiredMCVersion = coreModClazz.getAnnotation(IFMLLoadingPlugin.MCVersion.class);
            if (!Arrays.asList(CoreModManager.rootPlugins).contains(coreModClass) && (requiredMCVersion == null || Strings.isNullOrEmpty(requiredMCVersion.value()))) {
                FMLRelaunchLog.log(Level.WARNING, "The coremod %s does not have a MCVersion annotation, it may cause issues with this version of Minecraft", coreModClass);
            }
            else {
                if (requiredMCVersion != null && !FMLInjectionData.mccversion.equals(requiredMCVersion.value())) {
                    FMLRelaunchLog.log(Level.SEVERE, "The coremod %s is requesting minecraft version %s and minecraft is %s. It will be ignored.", coreModClass, requiredMCVersion.value(), FMLInjectionData.mccversion);
                    return null;
                }
                if (requiredMCVersion != null) {
                    FMLRelaunchLog.log(Level.FINE, "The coremod %s requested minecraft version %s and minecraft is %s. It will be loaded.", coreModClass, requiredMCVersion.value(), FMLInjectionData.mccversion);
                }
            }
            final IFMLLoadingPlugin.TransformerExclusions trExclusions = coreModClazz.getAnnotation(IFMLLoadingPlugin.TransformerExclusions.class);
            if (trExclusions != null) {
                for (final String st : trExclusions.value()) {
                    classLoader.addTransformerExclusion(st);
                }
            }
            final IFMLLoadingPlugin.DependsOn deplist = coreModClazz.getAnnotation(IFMLLoadingPlugin.DependsOn.class);
            String[] dependencies = new String[0];
            if (deplist != null) {
                dependencies = deplist.value();
            }
            final IFMLLoadingPlugin.SortingIndex index = coreModClazz.getAnnotation(IFMLLoadingPlugin.SortingIndex.class);
            final int sortIndex = (index != null) ? index.value() : 0;
            final IFMLLoadingPlugin plugin = (IFMLLoadingPlugin)coreModClazz.newInstance();
            final FMLPluginWrapper wrap = new FMLPluginWrapper(coreModName, plugin, location, sortIndex, dependencies);
            CoreModManager.loadPlugins.add(wrap);
            FMLRelaunchLog.fine("Enqueued coremod %s", coreModName);
            return wrap;
        }
        catch (final ClassNotFoundException cnfe) {
            if (!Lists.newArrayList((Object[])CoreModManager.rootPlugins).contains(coreModClass)) {
                FMLRelaunchLog.log(Level.SEVERE, cnfe, "Coremod %s: Unable to class load the plugin %s", coreModName, coreModClass);
            }
            else {
                FMLRelaunchLog.fine("Skipping root plugin %s", coreModClass);
            }
        }
        catch (final ClassCastException cce) {
            FMLRelaunchLog.log(Level.SEVERE, cce, "Coremod %s: The plugin %s is not an implementor of IFMLLoadingPlugin", coreModName, coreModClass);
        }
        catch (final InstantiationException ie) {
            FMLRelaunchLog.log(Level.SEVERE, ie, "Coremod %s: The plugin class %s was not instantiable", coreModName, coreModClass);
        }
        catch (final IllegalAccessException iae) {
            FMLRelaunchLog.log(Level.SEVERE, iae, "Coremod %s: The plugin class %s was not accessible", coreModName, coreModClass);
        }
        return null;
    }
    
    private static void sortCoreMods() {
        final TopologicalSort.DirectedGraph<FMLPluginWrapper> sortGraph = new TopologicalSort.DirectedGraph<FMLPluginWrapper>();
        final Map<String, FMLPluginWrapper> pluginMap = Maps.newHashMap();
        for (final FMLPluginWrapper plug : CoreModManager.loadPlugins) {
            sortGraph.addNode(plug);
            pluginMap.put(plug.name, plug);
        }
        for (final FMLPluginWrapper plug : CoreModManager.loadPlugins) {
            for (final String dep : plug.predepends) {
                if (!pluginMap.containsKey(dep)) {
                    FMLRelaunchLog.log(Level.SEVERE, "Missing coremod dependency - the coremod %s depends on coremod %s which isn't present.", plug.name, dep);
                    throw new RuntimeException();
                }
                sortGraph.addEdge(plug, pluginMap.get(dep));
            }
        }
        try {
            CoreModManager.loadPlugins = TopologicalSort.topologicalSort(sortGraph);
            FMLRelaunchLog.fine("Sorted coremod list %s", CoreModManager.loadPlugins);
        }
        catch (final Exception e) {
            FMLLog.log(Level.SEVERE, e, "There was a problem performing the coremod sort", new Object[0]);
            throw Throwables.propagate((Throwable)e);
        }
    }
    
    public static void injectTransformers(final LaunchClassLoader classLoader) {
        Launch.blackboard.put("fml.deobfuscatedEnvironment", CoreModManager.deobfuscatedEnvironment);
        CoreModManager.tweaker.injectCascadingTweak("cpw.mods.fml.common.launcher.FMLDeobfTweaker");
        CoreModManager.tweakSorting.put("cpw.mods.fml.common.launcher.FMLDeobfTweaker", 1000);
    }
    
    public static void injectCoreModTweaks(final FMLInjectionAndSortingTweaker fmlInjectionAndSortingTweaker) {
        final List<ITweaker> tweakers = Launch.blackboard.get("Tweaks");
        tweakers.add(0, (ITweaker)fmlInjectionAndSortingTweaker);
        for (final FMLPluginWrapper wrapper : CoreModManager.loadPlugins) {
            tweakers.add((ITweaker)wrapper);
        }
    }
    
    public static void sortTweakList() {
        final List<ITweaker> tweakers = Launch.blackboard.get("Tweaks");
        sort(tweakers, new Comparator<ITweaker>() {
            @Override
            public int compare(final ITweaker o1, final ITweaker o2) {
                Integer first = null;
                Integer second = null;
                if (o1 instanceof FMLInjectionAndSortingTweaker) {
                    first = Integer.MIN_VALUE;
                }
                if (o2 instanceof FMLInjectionAndSortingTweaker) {
                    second = Integer.MIN_VALUE;
                }
                if (o1 instanceof FMLPluginWrapper) {
                    first = ((FMLPluginWrapper)o1).sortIndex;
                }
                else if (first == null) {
                    first = CoreModManager.tweakSorting.get(o1.getClass().getName());
                }
                if (o2 instanceof FMLPluginWrapper) {
                    second = ((FMLPluginWrapper)o2).sortIndex;
                }
                else if (second == null) {
                    second = CoreModManager.tweakSorting.get(o2.getClass().getName());
                }
                if (first == null) {
                    first = 0;
                }
                if (second == null) {
                    second = 0;
                }
                return Ints.saturatedCast(first - (long)second);
            }
        });
    }
    
    static {
        COREMODCONTAINSFMLMOD = new Attributes.Name("FMLCorePluginContainsFMLMod");
        CoreModManager.rootPlugins = new String[] { "cpw.mods.fml.relauncher.FMLCorePlugin", "net.minecraftforge.classloading.FMLForgePlugin" };
        CoreModManager.loadedCoremods = Lists.newArrayList();
        CoreModManager.reparsedCoremods = Lists.newArrayList();
        CoreModManager.tweakSorting = Maps.newHashMap();
    }
    
    public static void sort(final List arg0, final Comparator arg1) {
        final Object[] array = arg0.toArray(new Object[arg0.size()]);
        Arrays.sort(array, arg1);
        for (int i = 0; i < array.length; ++i) {
            arg0.set(i, array[i]);
        }
    }
    
    private static class FMLPluginWrapper implements ITweaker
    {
        public final String name;
        public final IFMLLoadingPlugin coreModInstance;
        public final List<String> predepends;
        public final File location;
        public final int sortIndex;
        
        public FMLPluginWrapper(final String name, final IFMLLoadingPlugin coreModInstance, final File location, final int sortIndex, final String... predepends) {
            this.name = name;
            this.coreModInstance = coreModInstance;
            this.location = location;
            this.sortIndex = sortIndex;
            this.predepends = Lists.newArrayList((Object[])predepends);
        }
        
        @Override
        public String toString() {
            return String.format("%s {%s}", this.name, this.predepends);
        }
        
        public void acceptOptions(final List<String> args, final File gameDir, final File assetsDir, final String profile) {
        }
        
        public void injectIntoClassLoader(final LaunchClassLoader classLoader) {
            FMLRelaunchLog.fine("Injecting coremod %s {%s} class transformers", this.name, this.coreModInstance.getClass().getName());
            if (this.coreModInstance.getASMTransformerClass() != null) {
                for (final String transformer : this.coreModInstance.getASMTransformerClass()) {
                    FMLRelaunchLog.finest("Registering transformer %s", transformer);
                    classLoader.registerTransformer(transformer);
                }
            }
            FMLRelaunchLog.fine("Injection complete", new Object[0]);
            FMLRelaunchLog.fine("Running coremod plugin for %s {%s}", this.name, this.coreModInstance.getClass().getName());
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("mcLocation", CoreModManager.mcDir);
            data.put("coremodList", CoreModManager.loadPlugins);
            data.put("runtimeDeobfuscationEnabled", !CoreModManager.deobfuscatedEnvironment);
            FMLRelaunchLog.fine("Running coremod plugin %s", this.name);
            data.put("coremodLocation", this.location);
            this.coreModInstance.injectData(data);
            final String setupClass = this.coreModInstance.getSetupClass();
            if (setupClass != null) {
                try {
                    final IFMLCallHook call = (IFMLCallHook)Class.forName(setupClass, true, (ClassLoader)classLoader).newInstance();
                    final Map<String, Object> callData = new HashMap<String, Object>();
                    callData.put("mcLocation", CoreModManager.mcDir);
                    callData.put("classLoader", classLoader);
                    callData.put("coremodLocation", this.location);
                    callData.put("deobfuscationFileName", FMLInjectionData.debfuscationDataName());
                    call.injectData(callData);
                    call.call();
                }
                catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
            FMLRelaunchLog.fine("Coremod plugin class %s run successfully", this.coreModInstance.getClass().getSimpleName());
            final String modContainer = this.coreModInstance.getModContainerClass();
            if (modContainer != null) {
                FMLInjectionData.containers.add(modContainer);
            }
        }
        
        public String getLaunchTarget() {
            return "";
        }
        
        public String[] getLaunchArguments() {
            return new String[0];
        }
    }
}
