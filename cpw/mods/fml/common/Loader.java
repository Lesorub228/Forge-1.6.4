// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.versioning.VersionParser;
import com.google.common.base.Joiner;
import java.io.Reader;
import java.io.FileReader;
import java.util.Properties;
import com.google.common.base.CharMatcher;
import cpw.mods.fml.common.registry.GameData;
import java.net.MalformedURLException;
import cpw.mods.fml.common.event.FMLLoadEvent;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import java.io.IOException;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multisets;
import java.util.Comparator;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.Ordering;
import cpw.mods.fml.common.functions.ModIdFunction;
import cpw.mods.fml.common.discovery.ModDiscoverer;
import java.util.Iterator;
import com.google.common.collect.BiMap;
import cpw.mods.fml.common.toposort.ModSortingException;
import java.util.logging.Level;
import java.util.Collection;
import cpw.mods.fml.common.toposort.ModSorter;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import com.google.common.collect.ImmutableList;
import java.util.Set;
import com.google.common.collect.Sets;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.functions.ArtifactVersionNameFunction;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Map;
import java.util.List;
import com.google.common.base.Splitter;

public class Loader
{
    private static final Splitter DEPENDENCYPARTSPLITTER;
    private static final Splitter DEPENDENCYSPLITTER;
    private static Loader instance;
    private static String major;
    private static String minor;
    private static String rev;
    private static String build;
    private static String mccversion;
    private static String mcpversion;
    private ModClassLoader modClassLoader;
    private List<ModContainer> mods;
    private Map<String, ModContainer> namedMods;
    private File canonicalConfigDir;
    private File canonicalMinecraftDir;
    private Exception capturedError;
    private File canonicalModsDir;
    private LoadController modController;
    private MinecraftDummyContainer minecraft;
    private MCPDummyContainer mcp;
    private static File minecraftDir;
    private static List<String> injectedContainers;
    private File loggingProperties;
    private ImmutableMap<String, String> fmlBrandingProperties;
    
    public static Loader instance() {
        if (Loader.instance == null) {
            Loader.instance = new Loader();
        }
        return Loader.instance;
    }
    
    public static void injectData(final Object... data) {
        Loader.major = (String)data[0];
        Loader.minor = (String)data[1];
        Loader.rev = (String)data[2];
        Loader.build = (String)data[3];
        Loader.mccversion = (String)data[4];
        Loader.mcpversion = (String)data[5];
        Loader.minecraftDir = (File)data[6];
        Loader.injectedContainers = (List)data[7];
    }
    
    private Loader() {
        this.modClassLoader = new ModClassLoader(this.getClass().getClassLoader());
        final String actualMCVersion = new c((b)null).a();
        if (!Loader.mccversion.equals(actualMCVersion)) {
            FMLLog.severe("This version of FML is built for Minecraft %s, we have detected Minecraft %s in your minecraft jar file", Loader.mccversion, actualMCVersion);
            throw new LoaderException();
        }
        this.minecraft = new MinecraftDummyContainer(actualMCVersion);
        this.mcp = new MCPDummyContainer(MetadataCollection.from(this.getClass().getResourceAsStream("/mcpmod.info"), "MCP").getMetadataForId("mcp", null));
    }
    
    private void sortModList() {
        FMLLog.finer("Verifying mod requirements are satisfied", new Object[0]);
        try {
            final BiMap<String, ArtifactVersion> modVersions = (BiMap<String, ArtifactVersion>)HashBiMap.create();
            for (final ModContainer mod : this.getActiveModList()) {
                modVersions.put((Object)mod.getModId(), (Object)mod.getProcessedVersion());
            }
            for (final ModContainer mod : this.getActiveModList()) {
                if (!mod.acceptableMinecraftVersionRange().containsVersion(this.minecraft.getProcessedVersion())) {
                    FMLLog.severe("The mod %s does not wish to run in Minecraft version %s. You will have to remove it to play.", mod.getModId(), this.getMCVersionString());
                    throw new WrongMinecraftVersionException(mod);
                }
                final Map<String, ArtifactVersion> names = (Map<String, ArtifactVersion>)Maps.uniqueIndex((Iterable)mod.getRequirements(), (Function)new ArtifactVersionNameFunction());
                final Set<ArtifactVersion> versionMissingMods = Sets.newHashSet();
                final Set<String> missingMods = (Set<String>)Sets.difference((Set)names.keySet(), modVersions.keySet());
                if (!missingMods.isEmpty()) {
                    FMLLog.severe("The mod %s (%s) requires mods %s to be available", mod.getModId(), mod.getName(), missingMods);
                    for (final String modid : missingMods) {
                        versionMissingMods.add(names.get(modid));
                    }
                    throw new MissingModsException(versionMissingMods);
                }
                final ImmutableList<ArtifactVersion> allDeps = (ImmutableList<ArtifactVersion>)ImmutableList.builder().addAll((Iterable)mod.getDependants()).addAll((Iterable)mod.getDependencies()).build();
                for (final ArtifactVersion v : allDeps) {
                    if (modVersions.containsKey((Object)v.getLabel()) && !v.containsVersion((ArtifactVersion)modVersions.get((Object)v.getLabel()))) {
                        versionMissingMods.add(v);
                    }
                }
                if (!versionMissingMods.isEmpty()) {
                    FMLLog.severe("The mod %s (%s) requires mod versions %s to be available", mod.getModId(), mod.getName(), versionMissingMods);
                    throw new MissingModsException(versionMissingMods);
                }
            }
            FMLLog.finer("All mod requirements are satisfied", new Object[0]);
            final ModSorter sorter = new ModSorter(this.getActiveModList(), this.namedMods);
            try {
                FMLLog.finer("Sorting mods into an ordered list", new Object[0]);
                final List<ModContainer> sortedMods = sorter.sort();
                this.modController.getActiveModList().clear();
                this.modController.getActiveModList().addAll(sortedMods);
                this.mods.removeAll(sortedMods);
                sortedMods.addAll(this.mods);
                this.mods = sortedMods;
                FMLLog.finer("Mod sorting completed successfully", new Object[0]);
            }
            catch (final ModSortingException sortException) {
                FMLLog.severe("A dependency cycle was detected in the input mod set so an ordering cannot be determined", new Object[0]);
                final ModSortingException.SortingExceptionData<ModContainer> exceptionData = sortException.getExceptionData();
                FMLLog.severe("The first mod in the cycle is %s", exceptionData.getFirstBadNode());
                FMLLog.severe("The mod cycle involves", new Object[0]);
                for (final ModContainer mc : exceptionData.getVisitedNodes()) {
                    FMLLog.severe("%s : before: %s, after: %s", mc.toString(), mc.getDependants(), mc.getDependencies());
                }
                FMLLog.log(Level.SEVERE, sortException, "The full error", new Object[0]);
                throw sortException;
            }
        }
        finally {
            FMLLog.fine("Mod sorting data", new Object[0]);
            int unprintedMods = this.mods.size();
            for (final ModContainer mod2 : this.getActiveModList()) {
                if (!mod2.isImmutable()) {
                    FMLLog.fine("\t%s(%s:%s): %s (%s)", mod2.getModId(), mod2.getName(), mod2.getVersion(), mod2.getSource().getName(), mod2.getSortingRules());
                    --unprintedMods;
                }
            }
            if (unprintedMods == this.mods.size()) {
                FMLLog.fine("No user mods found to sort", new Object[0]);
            }
        }
    }
    
    private ModDiscoverer identifyMods() {
        FMLLog.fine("Building injected Mod Containers %s", Loader.injectedContainers);
        this.mods.add(new InjectedModContainer(this.mcp, new File("minecraft.jar")));
        for (final String cont : Loader.injectedContainers) {
            ModContainer mc;
            try {
                mc = (ModContainer)Class.forName(cont, true, this.modClassLoader).newInstance();
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "A problem occured instantiating the injected mod container %s", cont);
                throw new LoaderException(e);
            }
            this.mods.add(new InjectedModContainer(mc, mc.getSource()));
        }
        final ModDiscoverer discoverer = new ModDiscoverer();
        FMLLog.fine("Attempting to load mods contained in the minecraft jar file and associated classes", new Object[0]);
        discoverer.findClasspathMods(this.modClassLoader);
        FMLLog.fine("Minecraft jar mods loaded successfully", new Object[0]);
        FMLLog.info("Searching %s for mods", this.canonicalModsDir.getAbsolutePath());
        discoverer.findModDirMods(this.canonicalModsDir);
        final File versionSpecificModsDir = new File(this.canonicalModsDir, Loader.mccversion);
        if (versionSpecificModsDir.isDirectory()) {
            FMLLog.info("Also searching %s for mods", versionSpecificModsDir);
            discoverer.findModDirMods(versionSpecificModsDir);
        }
        this.mods.addAll(discoverer.identifyMods());
        this.identifyDuplicates(this.mods);
        this.namedMods = (Map<String, ModContainer>)Maps.uniqueIndex((Iterable)this.mods, (Function)new ModIdFunction());
        FMLLog.info("Forge Mod Loader has identified %d mod%s to load", this.mods.size(), (this.mods.size() != 1) ? "s" : "");
        for (final String modId : this.namedMods.keySet()) {
            FMLLog.makeLog(modId);
        }
        return discoverer;
    }
    
    private void identifyDuplicates(final List<ModContainer> mods) {
        final TreeMultimap<ModContainer, File> dupsearch = (TreeMultimap<ModContainer, File>)TreeMultimap.create((Comparator)new ModIdComparator(), (Comparator)Ordering.arbitrary());
        for (final ModContainer mc : mods) {
            if (mc.getSource() != null) {
                dupsearch.put((Object)mc, (Object)mc.getSource());
            }
        }
        final ImmutableMultiset<ModContainer> duplist = (ImmutableMultiset<ModContainer>)Multisets.copyHighestCountFirst(dupsearch.keys());
        final SetMultimap<ModContainer, File> dupes = (SetMultimap<ModContainer, File>)LinkedHashMultimap.create();
        for (final Multiset.Entry<ModContainer> e : duplist.entrySet()) {
            if (e.getCount() > 1) {
                FMLLog.severe("Found a duplicate mod %s at %s", ((ModContainer)e.getElement()).getModId(), dupsearch.get(e.getElement()));
                dupes.putAll(e.getElement(), (Iterable)dupsearch.get(e.getElement()));
            }
        }
        if (!dupes.isEmpty()) {
            throw new DuplicateModsFoundException(dupes);
        }
    }
    
    private void initializeLoader() {
        final File modsDir = new File(Loader.minecraftDir, "mods");
        final File configDir = new File(Loader.minecraftDir, "config");
        String canonicalModsPath;
        String canonicalConfigPath;
        try {
            this.canonicalMinecraftDir = Loader.minecraftDir.getCanonicalFile();
            canonicalModsPath = modsDir.getCanonicalPath();
            canonicalConfigPath = configDir.getCanonicalPath();
            this.canonicalConfigDir = configDir.getCanonicalFile();
            this.canonicalModsDir = modsDir.getCanonicalFile();
        }
        catch (final IOException ioe) {
            FMLLog.log(Level.SEVERE, ioe, "Failed to resolve loader directories: mods : %s ; config %s", this.canonicalModsDir.getAbsolutePath(), configDir.getAbsolutePath());
            throw new LoaderException(ioe);
        }
        if (!this.canonicalModsDir.exists()) {
            FMLLog.info("No mod directory found, creating one: %s", canonicalModsPath);
            final boolean dirMade = this.canonicalModsDir.mkdir();
            if (!dirMade) {
                FMLLog.severe("Unable to create the mod directory %s", canonicalModsPath);
                throw new LoaderException();
            }
            FMLLog.info("Mod directory created successfully", new Object[0]);
        }
        if (!this.canonicalConfigDir.exists()) {
            FMLLog.fine("No config directory found, creating one: %s", canonicalConfigPath);
            final boolean dirMade = this.canonicalConfigDir.mkdir();
            if (!dirMade) {
                FMLLog.severe("Unable to create the config directory %s", canonicalConfigPath);
                throw new LoaderException();
            }
            FMLLog.info("Config directory created successfully", new Object[0]);
        }
        if (!this.canonicalModsDir.isDirectory()) {
            FMLLog.severe("Attempting to load mods from %s, which is not a directory", canonicalModsPath);
            throw new LoaderException();
        }
        if (!configDir.isDirectory()) {
            FMLLog.severe("Attempting to load configuration from %s, which is not a directory", canonicalConfigPath);
            throw new LoaderException();
        }
        this.loggingProperties = new File(this.canonicalConfigDir, "logging.properties");
        FMLLog.info("Reading custom logging properties from %s", this.loggingProperties.getPath());
        FMLRelaunchLog.loadLogConfiguration(this.loggingProperties);
        FMLLog.log(Level.OFF, "Logging level for ForgeModLoader logging is set to %s", FMLRelaunchLog.log.getLogger().getLevel());
    }
    
    public List<ModContainer> getModList() {
        return (List<ModContainer>)((instance().mods != null) ? ImmutableList.copyOf((Collection)instance().mods) : ImmutableList.of());
    }
    
    public void loadMods() {
        this.initializeLoader();
        this.mods = Lists.newArrayList();
        this.namedMods = Maps.newHashMap();
        (this.modController = new LoadController(this)).transition(LoaderState.LOADING, false);
        final ModDiscoverer disc = this.identifyMods();
        ModAPIManager.INSTANCE.manageAPI(this.modClassLoader, disc);
        this.disableRequestedMods();
        FMLLog.fine("Reloading logging properties from %s", this.loggingProperties.getPath());
        FMLRelaunchLog.loadLogConfiguration(this.loggingProperties);
        FMLLog.fine("Reloaded logging properties", new Object[0]);
        this.modController.distributeStateMessage(FMLLoadEvent.class);
        this.sortModList();
        ModAPIManager.INSTANCE.cleanupAPIContainers(this.modController.getActiveModList());
        ModAPIManager.INSTANCE.cleanupAPIContainers(this.mods);
        this.mods = (List<ModContainer>)ImmutableList.copyOf((Collection)this.mods);
        for (final File nonMod : disc.getNonModLibs()) {
            if (nonMod.isFile()) {
                FMLLog.info("FML has found a non-mod file %s in your mods directory. It will now be injected into your classpath. This could severe stability issues, it should be removed if possible.", nonMod.getName());
                try {
                    this.modClassLoader.addFile(nonMod);
                }
                catch (final MalformedURLException e) {
                    FMLLog.log(Level.SEVERE, e, "Encountered a weird problem with non-mod file injection : %s", nonMod.getName());
                }
            }
        }
        this.modController.transition(LoaderState.CONSTRUCTING, false);
        this.modController.distributeStateMessage(LoaderState.CONSTRUCTING, this.modClassLoader, disc.getASMTable());
        FMLLog.fine("Mod signature data", new Object[0]);
        for (final ModContainer mod : this.getActiveModList()) {
            FMLLog.fine("\t%s(%s:%s): %s (%s)", mod.getModId(), mod.getName(), mod.getVersion(), mod.getSource().getName(), CertificateHelper.getFingerprint(mod.getSigningCertificate()));
        }
        if (this.getActiveModList().isEmpty()) {
            FMLLog.fine("No user mod signature data found", new Object[0]);
        }
        this.modController.transition(LoaderState.PREINITIALIZATION, false);
        this.modController.distributeStateMessage(LoaderState.PREINITIALIZATION, disc.getASMTable(), this.canonicalConfigDir);
        this.modController.transition(LoaderState.INITIALIZATION, false);
        GameData.validateRegistry();
    }
    
    private void disableRequestedMods() {
        final String forcedModList = System.getProperty("fml.modStates", "");
        FMLLog.finer("Received a system property request '%s'", forcedModList);
        final Map<String, String> sysPropertyStateList = Splitter.on(CharMatcher.anyOf((CharSequence)";:")).omitEmptyStrings().trimResults().withKeyValueSeparator("=").split((CharSequence)forcedModList);
        FMLLog.finer("System property request managing the state of %d mods", sysPropertyStateList.size());
        final Map<String, String> modStates = Maps.newHashMap();
        final File forcedModFile = new File(this.canonicalConfigDir, "fmlModState.properties");
        final Properties forcedModListProperties = new Properties();
        if (forcedModFile.exists() && forcedModFile.isFile()) {
            FMLLog.finer("Found a mod state file %s", forcedModFile.getName());
            try {
                forcedModListProperties.load(new FileReader(forcedModFile));
                FMLLog.finer("Loaded states for %d mods from file", forcedModListProperties.size());
            }
            catch (final Exception e) {
                FMLLog.log(Level.INFO, e, "An error occurred reading the fmlModState.properties file", new Object[0]);
            }
        }
        modStates.putAll((Map<? extends String, ? extends String>)Maps.fromProperties(forcedModListProperties));
        modStates.putAll(sysPropertyStateList);
        FMLLog.fine("After merging, found state information for %d mods", modStates.size());
        final Map<String, Boolean> isEnabled = Maps.transformValues((Map)modStates, (Function)new Function<String, Boolean>() {
            public Boolean apply(final String input) {
                return Boolean.parseBoolean(input);
            }
        });
        for (final Map.Entry<String, Boolean> entry : isEnabled.entrySet()) {
            if (this.namedMods.containsKey(entry.getKey())) {
                FMLLog.info("Setting mod %s to enabled state %b", entry.getKey(), entry.getValue());
                this.namedMods.get(entry.getKey()).setEnabledState(entry.getValue());
            }
        }
    }
    
    public static boolean isModLoaded(final String modname) {
        return instance().namedMods.containsKey(modname) && instance().modController.getModState(Loader.instance.namedMods.get(modname)) != LoaderState.ModState.DISABLED;
    }
    
    public File getConfigDir() {
        return this.canonicalConfigDir;
    }
    
    public String getCrashInformation() {
        if (this.modController == null) {
            return "";
        }
        final StringBuilder ret = new StringBuilder();
        final List<String> branding = FMLCommonHandler.instance().getBrandings();
        Joiner.on(' ').skipNulls().appendTo(ret, (Iterable)branding.subList(1, branding.size()));
        if (this.modController != null) {
            this.modController.printModStates(ret);
        }
        return ret.toString();
    }
    
    public String getFMLVersionString() {
        return String.format("%s.%s.%s.%s", Loader.major, Loader.minor, Loader.rev, Loader.build);
    }
    
    public ClassLoader getModClassLoader() {
        return this.modClassLoader;
    }
    
    public void computeDependencies(final String dependencyString, final Set<ArtifactVersion> requirements, final List<ArtifactVersion> dependencies, final List<ArtifactVersion> dependants) {
        if (dependencyString == null || dependencyString.length() == 0) {
            return;
        }
        boolean parseFailure = false;
        for (final String dep : Loader.DEPENDENCYSPLITTER.split((CharSequence)dependencyString)) {
            final List<String> depparts = Lists.newArrayList(Loader.DEPENDENCYPARTSPLITTER.split((CharSequence)dep));
            if (depparts.size() != 2) {
                parseFailure = true;
            }
            else {
                final String instruction = depparts.get(0);
                final String target = depparts.get(1);
                final boolean targetIsAll = target.startsWith("*");
                if (targetIsAll && target.length() > 1) {
                    parseFailure = true;
                }
                else {
                    if ("required-before".equals(instruction) || "required-after".equals(instruction)) {
                        if (targetIsAll) {
                            parseFailure = true;
                            continue;
                        }
                        requirements.add(VersionParser.parseVersionReference(target));
                    }
                    if (targetIsAll && target.indexOf(64) > -1) {
                        parseFailure = true;
                    }
                    else if ("required-before".equals(instruction) || "before".equals(instruction)) {
                        dependants.add(VersionParser.parseVersionReference(target));
                    }
                    else if ("required-after".equals(instruction) || "after".equals(instruction)) {
                        dependencies.add(VersionParser.parseVersionReference(target));
                    }
                    else {
                        parseFailure = true;
                    }
                }
            }
        }
        if (parseFailure) {
            FMLLog.log(Level.WARNING, "Unable to parse dependency string %s", dependencyString);
            throw new LoaderException();
        }
    }
    
    public Map<String, ModContainer> getIndexedModList() {
        return (Map<String, ModContainer>)ImmutableMap.copyOf((Map)this.namedMods);
    }
    
    public void initializeMods() {
        this.modController.distributeStateMessage(LoaderState.INITIALIZATION, new Object[0]);
        this.modController.transition(LoaderState.POSTINITIALIZATION, false);
        GameData.buildModObjectTable();
        this.modController.distributeStateMessage(FMLInterModComms.IMCEvent.class);
        this.modController.distributeStateMessage(LoaderState.POSTINITIALIZATION, new Object[0]);
        this.modController.transition(LoaderState.AVAILABLE, false);
        this.modController.distributeStateMessage(LoaderState.AVAILABLE, new Object[0]);
        GameData.dumpRegistry(Loader.minecraftDir);
        FMLLog.info("Forge Mod Loader has successfully loaded %d mod%s", this.mods.size(), (this.mods.size() == 1) ? "" : "s");
    }
    
    public ICrashCallable getCallableCrashInformation() {
        return new ICrashCallable() {
            @Override
            public String call() throws Exception {
                return Loader.this.getCrashInformation();
            }
            
            @Override
            public String getLabel() {
                return "FML";
            }
        };
    }
    
    public List<ModContainer> getActiveModList() {
        return (List<ModContainer>)((this.modController != null) ? this.modController.getActiveModList() : ImmutableList.of());
    }
    
    public LoaderState.ModState getModState(final ModContainer selectedMod) {
        return this.modController.getModState(selectedMod);
    }
    
    public String getMCVersionString() {
        return "Minecraft " + Loader.mccversion;
    }
    
    public boolean serverStarting(final Object server) {
        try {
            this.modController.distributeStateMessage(LoaderState.SERVER_STARTING, server);
            this.modController.transition(LoaderState.SERVER_STARTING, false);
        }
        catch (final Throwable t) {
            FMLLog.log(Level.SEVERE, t, "A fatal exception occurred during the server starting event", new Object[0]);
            return false;
        }
        return true;
    }
    
    public void serverStarted() {
        this.modController.distributeStateMessage(LoaderState.SERVER_STARTED, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STARTED, false);
    }
    
    public void serverStopping() {
        this.modController.distributeStateMessage(LoaderState.SERVER_STOPPING, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STOPPING, false);
    }
    
    public BiMap<ModContainer, Object> getModObjectList() {
        return this.modController.getModObjectList();
    }
    
    public BiMap<Object, ModContainer> getReversedModObjectList() {
        return (BiMap<Object, ModContainer>)this.getModObjectList().inverse();
    }
    
    public ModContainer activeModContainer() {
        return (this.modController != null) ? this.modController.activeContainer() : null;
    }
    
    public boolean isInState(final LoaderState state) {
        return this.modController.isInState(state);
    }
    
    public MinecraftDummyContainer getMinecraftModContainer() {
        return this.minecraft;
    }
    
    public boolean hasReachedState(final LoaderState state) {
        return this.modController != null && this.modController.hasReachedState(state);
    }
    
    public String getMCPVersionString() {
        return String.format("MCP v%s", Loader.mcpversion);
    }
    
    public void serverStopped() {
        this.modController.distributeStateMessage(LoaderState.SERVER_STOPPED, new Object[0]);
        this.modController.transition(LoaderState.SERVER_STOPPED, true);
        this.modController.transition(LoaderState.AVAILABLE, true);
    }
    
    public boolean serverAboutToStart(final Object server) {
        try {
            this.modController.distributeStateMessage(LoaderState.SERVER_ABOUT_TO_START, server);
            this.modController.transition(LoaderState.SERVER_ABOUT_TO_START, false);
        }
        catch (final Throwable t) {
            FMLLog.log(Level.SEVERE, t, "A fatal exception occurred during the server about to start event", new Object[0]);
            return false;
        }
        return true;
    }
    
    public Map<String, String> getFMLBrandingProperties() {
        if (this.fmlBrandingProperties == null) {
            final Properties loaded = new Properties();
            try {
                loaded.load(this.getClass().getClassLoader().getResourceAsStream("fmlbranding.properties"));
            }
            catch (final Exception ex) {}
            this.fmlBrandingProperties = (ImmutableMap<String, String>)Maps.fromProperties(loaded);
        }
        return (Map<String, String>)this.fmlBrandingProperties;
    }
    
    public Map<String, String> getCustomModProperties(final String modId) {
        return this.getIndexedModList().get(modId).getCustomModProperties();
    }
    
    static {
        DEPENDENCYPARTSPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
        DEPENDENCYSPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();
    }
    
    private class ModIdComparator implements Comparator<ModContainer>
    {
        @Override
        public int compare(final ModContainer o1, final ModContainer o2) {
            return o1.getModId().compareTo(o2.getModId());
        }
    }
}
