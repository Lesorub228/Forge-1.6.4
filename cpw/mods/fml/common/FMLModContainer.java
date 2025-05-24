// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import java.lang.reflect.Field;
import java.util.Iterator;
import com.google.common.collect.SetMultimap;
import com.google.common.base.Function;
import cpw.mods.fml.common.discovery.ASMDataTable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import com.google.common.base.Throwables;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.zip.ZipFile;
import java.util.Properties;
import java.util.List;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import com.google.common.base.Strings;
import java.util.logging.Level;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.common.discovery.ModCandidate;
import java.lang.reflect.Method;
import com.google.common.collect.ListMultimap;
import java.security.cert.Certificate;
import java.util.Set;
import cpw.mods.fml.common.versioning.VersionRange;
import java.lang.annotation.Annotation;
import cpw.mods.fml.common.event.FMLEvent;
import com.google.common.collect.BiMap;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import com.google.common.eventbus.EventBus;
import java.util.Map;
import java.io.File;

public class FMLModContainer implements ModContainer
{
    private Mod modDescriptor;
    private Object modInstance;
    private File source;
    private ModMetadata modMetadata;
    private String className;
    private Map<String, Object> descriptor;
    private boolean enabled;
    private String internalVersion;
    private boolean overridesMetadata;
    private EventBus eventBus;
    private LoadController controller;
    private DefaultArtifactVersion processedVersion;
    private boolean isNetworkMod;
    private static final BiMap<Class<? extends FMLEvent>, Class<? extends Annotation>> modAnnotationTypes;
    private static final BiMap<Class<? extends Annotation>, Class<? extends FMLEvent>> modTypeAnnotations;
    private String annotationDependencies;
    private VersionRange minecraftAccepted;
    private boolean fingerprintNotPresent;
    private Set<String> sourceFingerprints;
    private Certificate certificate;
    private String modLanguage;
    private ILanguageAdapter languageAdapter;
    private ListMultimap<Class<? extends FMLEvent>, Method> eventMethods;
    private Map<String, String> customModProperties;
    private ModCandidate candidate;
    
    public FMLModContainer(final String className, final ModCandidate container, final Map<String, Object> modDescriptor) {
        this.enabled = true;
        this.className = className;
        this.source = container.getModContainer();
        this.candidate = container;
        this.descriptor = modDescriptor;
        this.modLanguage = modDescriptor.get("modLanguage");
        this.languageAdapter = ("scala".equals(this.modLanguage) ? new ILanguageAdapter.ScalaAdapter() : new ILanguageAdapter.JavaAdapter());
        this.eventMethods = (ListMultimap<Class<? extends FMLEvent>, Method>)ArrayListMultimap.create();
    }
    
    private ILanguageAdapter getLanguageAdapter() {
        return this.languageAdapter;
    }
    
    @Override
    public String getModId() {
        return this.descriptor.get("modid");
    }
    
    @Override
    public String getName() {
        return this.modMetadata.name;
    }
    
    @Override
    public String getVersion() {
        return this.internalVersion;
    }
    
    @Override
    public File getSource() {
        return this.source;
    }
    
    @Override
    public ModMetadata getMetadata() {
        return this.modMetadata;
    }
    
    @Override
    public void bindMetadata(final MetadataCollection mc) {
        this.modMetadata = mc.getMetadataForId(this.getModId(), this.descriptor);
        if (this.descriptor.containsKey("useMetadata")) {
            this.overridesMetadata = !this.descriptor.get("useMetadata");
        }
        if (this.overridesMetadata || !this.modMetadata.useDependencyInformation) {
            final Set<ArtifactVersion> requirements = Sets.newHashSet();
            final List<ArtifactVersion> dependencies = Lists.newArrayList();
            final List<ArtifactVersion> dependants = Lists.newArrayList();
            this.annotationDependencies = this.descriptor.get("dependencies");
            Loader.instance().computeDependencies(this.annotationDependencies, requirements, dependencies, dependants);
            this.modMetadata.requiredMods = requirements;
            this.modMetadata.dependencies = dependencies;
            this.modMetadata.dependants = dependants;
            FMLLog.log(this.getModId(), Level.FINEST, "Parsed dependency info : %s %s %s", requirements, dependencies, dependants);
        }
        else {
            FMLLog.log(this.getModId(), Level.FINEST, "Using mcmod dependency info : %s %s %s", this.modMetadata.requiredMods, this.modMetadata.dependencies, this.modMetadata.dependants);
        }
        if (Strings.isNullOrEmpty(this.modMetadata.name)) {
            FMLLog.log(this.getModId(), Level.INFO, "Mod %s is missing the required element 'name'. Substituting %s", this.getModId(), this.getModId());
            this.modMetadata.name = this.getModId();
        }
        this.internalVersion = this.descriptor.get("version");
        if (Strings.isNullOrEmpty(this.internalVersion)) {
            final Properties versionProps = this.searchForVersionProperties();
            if (versionProps != null) {
                this.internalVersion = versionProps.getProperty(this.getModId() + ".version");
                FMLLog.log(this.getModId(), Level.FINE, "Found version %s for mod %s in version.properties, using", this.internalVersion, this.getModId());
            }
        }
        if (Strings.isNullOrEmpty(this.internalVersion) && !Strings.isNullOrEmpty(this.modMetadata.version)) {
            FMLLog.log(this.getModId(), Level.WARNING, "Mod %s is missing the required element 'version' and a version.properties file could not be found. Falling back to metadata version %s", this.getModId(), this.modMetadata.version);
            this.internalVersion = this.modMetadata.version;
        }
        if (Strings.isNullOrEmpty(this.internalVersion)) {
            FMLLog.log(this.getModId(), Level.WARNING, "Mod %s is missing the required element 'version' and no fallback can be found. Substituting '1.0'.", this.getModId());
            final ModMetadata modMetadata = this.modMetadata;
            final String s = "1.0";
            this.internalVersion = s;
            modMetadata.version = s;
        }
        final String mcVersionString = this.descriptor.get("acceptedMinecraftVersions");
        if (!Strings.isNullOrEmpty(mcVersionString)) {
            this.minecraftAccepted = VersionParser.parseRange(mcVersionString);
        }
        else {
            this.minecraftAccepted = Loader.instance().getMinecraftModContainer().getStaticVersionRange();
        }
    }
    
    public Properties searchForVersionProperties() {
        try {
            FMLLog.log(this.getModId(), Level.FINE, "Attempting to load the file version.properties from %s to locate a version number for %s", this.getSource().getName(), this.getModId());
            Properties version = null;
            if (this.getSource().isFile()) {
                final ZipFile source = new ZipFile(this.getSource());
                final ZipEntry versionFile = source.getEntry("version.properties");
                if (versionFile != null) {
                    version = new Properties();
                    version.load(source.getInputStream(versionFile));
                }
                source.close();
            }
            else if (this.getSource().isDirectory()) {
                final File propsFile = new File(this.getSource(), "version.properties");
                if (propsFile.exists() && propsFile.isFile()) {
                    version = new Properties();
                    final FileInputStream fis = new FileInputStream(propsFile);
                    version.load(fis);
                    fis.close();
                }
            }
            return version;
        }
        catch (final Exception e) {
            Throwables.propagateIfPossible((Throwable)e);
            FMLLog.log(this.getModId(), Level.FINEST, "Failed to find a usable version.properties file", new Object[0]);
            return null;
        }
    }
    
    @Override
    public void setEnabledState(final boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public Set<ArtifactVersion> getRequirements() {
        return this.modMetadata.requiredMods;
    }
    
    @Override
    public List<ArtifactVersion> getDependencies() {
        return this.modMetadata.dependencies;
    }
    
    @Override
    public List<ArtifactVersion> getDependants() {
        return this.modMetadata.dependants;
    }
    
    @Override
    public String getSortingRules() {
        return (this.overridesMetadata || !this.modMetadata.useDependencyInformation) ? Strings.nullToEmpty(this.annotationDependencies) : this.modMetadata.printableSortingRules();
    }
    
    @Override
    public boolean matches(final Object mod) {
        return mod == this.modInstance;
    }
    
    @Override
    public Object getMod() {
        return this.modInstance;
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        if (this.enabled) {
            FMLLog.log(this.getModId(), Level.FINE, "Enabling mod %s", this.getModId());
            this.eventBus = bus;
            this.controller = controller;
            this.eventBus.register((Object)this);
            return true;
        }
        return false;
    }
    
    private Method gatherAnnotations(final Class<?> clazz) throws Exception {
        Method factoryMethod = null;
        for (final Method m : clazz.getDeclaredMethods()) {
            for (final Annotation a : m.getAnnotations()) {
                if (FMLModContainer.modTypeAnnotations.containsKey((Object)a.annotationType())) {
                    final Class<?>[] paramTypes = { (Class)FMLModContainer.modTypeAnnotations.get((Object)a.annotationType()) };
                    if (Arrays.equals(m.getParameterTypes(), paramTypes)) {
                        m.setAccessible(true);
                        this.eventMethods.put(FMLModContainer.modTypeAnnotations.get((Object)a.annotationType()), (Object)m);
                    }
                    else {
                        FMLLog.log(this.getModId(), Level.SEVERE, "The mod %s appears to have an invalid method annotation %s. This annotation can only apply to methods with argument types %s -it will not be called", this.getModId(), a.annotationType().getSimpleName(), Arrays.toString(paramTypes));
                    }
                }
                else if (a.annotationType().equals(Mod.EventHandler.class)) {
                    if (m.getParameterTypes().length == 1 && FMLModContainer.modAnnotationTypes.containsKey((Object)m.getParameterTypes()[0])) {
                        m.setAccessible(true);
                        this.eventMethods.put((Object)m.getParameterTypes()[0], (Object)m);
                    }
                    else {
                        FMLLog.log(this.getModId(), Level.SEVERE, "The mod %s appears to have an invalid event annotation %s. This annotation can only apply to methods with recognized event arguments - it will not be called", this.getModId(), a.annotationType().getSimpleName());
                    }
                }
                else if (a.annotationType().equals(Mod.InstanceFactory.class)) {
                    if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && factoryMethod == null) {
                        m.setAccessible(true);
                        factoryMethod = m;
                    }
                    else if (!Modifier.isStatic(m.getModifiers()) || m.getParameterTypes().length != 0) {
                        FMLLog.log(this.getModId(), Level.SEVERE, "The InstanceFactory annotation can only apply to a static method, taking zero arguments - it will be ignored on %s(%s)", m.getName(), Arrays.asList(m.getParameterTypes()));
                    }
                    else if (factoryMethod != null) {
                        FMLLog.log(this.getModId(), Level.SEVERE, "The InstanceFactory annotation can only be used once, the application to %s(%s) will be ignored", m.getName(), Arrays.asList(m.getParameterTypes()));
                    }
                }
            }
        }
        return factoryMethod;
    }
    
    private void processFieldAnnotations(final ASMDataTable asmDataTable) throws Exception {
        final SetMultimap<String, ASMDataTable.ASMData> annotations = asmDataTable.getAnnotationsFor(this);
        this.parseSimpleFieldAnnotation(annotations, Mod.Instance.class.getName(), (Function<ModContainer, Object>)new Function<ModContainer, Object>() {
            public Object apply(final ModContainer mc) {
                return mc.getMod();
            }
        });
        this.parseSimpleFieldAnnotation(annotations, Mod.Metadata.class.getName(), (Function<ModContainer, Object>)new Function<ModContainer, Object>() {
            public Object apply(final ModContainer mc) {
                return mc.getMetadata();
            }
        });
    }
    
    private void parseSimpleFieldAnnotation(final SetMultimap<String, ASMDataTable.ASMData> annotations, final String annotationClassName, final Function<ModContainer, Object> retreiver) throws IllegalAccessException {
        final String[] annName = annotationClassName.split("\\.");
        final String annotationName = annName[annName.length - 1];
        for (final ASMDataTable.ASMData targets : annotations.get((Object)annotationClassName)) {
            final String targetMod = targets.getAnnotationInfo().get("value");
            Field f = null;
            Object injectedMod = null;
            ModContainer mc = this;
            boolean isStatic = false;
            Class<?> clz = this.modInstance.getClass();
            if (!Strings.isNullOrEmpty(targetMod)) {
                if (Loader.isModLoaded(targetMod)) {
                    mc = Loader.instance().getIndexedModList().get(targetMod);
                }
                else {
                    mc = null;
                }
            }
            if (mc != null) {
                try {
                    clz = Class.forName(targets.getClassName(), true, Loader.instance().getModClassLoader());
                    f = clz.getDeclaredField(targets.getObjectName());
                    f.setAccessible(true);
                    isStatic = Modifier.isStatic(f.getModifiers());
                    injectedMod = retreiver.apply((Object)mc);
                }
                catch (final Exception e) {
                    Throwables.propagateIfPossible((Throwable)e);
                    FMLLog.log(this.getModId(), Level.WARNING, e, "Attempting to load @%s in class %s for %s and failing", annotationName, targets.getClassName(), mc.getModId());
                }
            }
            if (f != null) {
                Object target = null;
                if (!isStatic) {
                    target = this.modInstance;
                    if (!this.modInstance.getClass().equals(clz)) {
                        FMLLog.log(this.getModId(), Level.WARNING, "Unable to inject @%s in non-static field %s.%s for %s as it is NOT the primary mod instance", annotationName, targets.getClassName(), targets.getObjectName(), mc.getModId());
                        continue;
                    }
                }
                f.set(target, injectedMod);
            }
        }
    }
    
    @Subscribe
    public void constructMod(final FMLConstructionEvent event) {
        try {
            final ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(this.source);
            modClassLoader.clearNegativeCacheFor(this.candidate.getClassList());
            final Class<?> clazz = Class.forName(this.className, true, modClassLoader);
            final Certificate[] certificates = clazz.getProtectionDomain().getCodeSource().getCertificates();
            int len = 0;
            if (certificates != null) {
                len = certificates.length;
            }
            final ImmutableList.Builder<String> certBuilder = (ImmutableList.Builder<String>)ImmutableList.builder();
            for (int i = 0; i < len; ++i) {
                certBuilder.add((Object)CertificateHelper.getFingerprint(certificates[i]));
            }
            final ImmutableList<String> certList = (ImmutableList<String>)certBuilder.build();
            this.sourceFingerprints = (Set<String>)ImmutableSet.copyOf((Collection)certList);
            final String expectedFingerprint = this.descriptor.get("certificateFingerprint");
            this.fingerprintNotPresent = true;
            if (expectedFingerprint != null && !expectedFingerprint.isEmpty()) {
                if (!this.sourceFingerprints.contains(expectedFingerprint)) {
                    Level warnLevel = Level.SEVERE;
                    if (this.source.isDirectory()) {
                        warnLevel = Level.FINER;
                    }
                    FMLLog.log(this.getModId(), warnLevel, "The mod %s is expecting signature %s for source %s, however there is no signature matching that description", this.getModId(), expectedFingerprint, this.source.getName());
                }
                else {
                    this.certificate = certificates[certList.indexOf((Object)expectedFingerprint)];
                    this.fingerprintNotPresent = false;
                }
            }
            final List<Map<String, Object>> props = this.descriptor.get("customProperties");
            if (props != null) {
                final ImmutableMap.Builder<String, String> builder = (ImmutableMap.Builder<String, String>)ImmutableMap.builder();
                for (final Map<String, Object> p : props) {
                    builder.put((Object)p.get("k"), (Object)p.get("v"));
                }
                this.customModProperties = (Map<String, String>)builder.build();
            }
            else {
                this.customModProperties = FMLModContainer.EMPTY_PROPERTIES;
            }
            final Method factoryMethod = this.gatherAnnotations(clazz);
            this.modInstance = this.getLanguageAdapter().getNewInstance(this, clazz, modClassLoader, factoryMethod);
            this.isNetworkMod = FMLNetworkHandler.instance().registerNetworkMod(this, clazz, event.getASMHarvestedData());
            if (this.fingerprintNotPresent) {
                this.eventBus.post((Object)new FMLFingerprintViolationEvent(this.source.isDirectory(), this.source, (ImmutableSet<String>)ImmutableSet.copyOf((Collection)this.sourceFingerprints), expectedFingerprint));
            }
            ProxyInjector.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide(), this.getLanguageAdapter());
            this.processFieldAnnotations(event.getASMHarvestedData());
        }
        catch (final Throwable e) {
            this.controller.errorOccurred(this, e);
            Throwables.propagateIfPossible(e);
        }
    }
    
    @Subscribe
    public void handleModStateEvent(final FMLEvent event) {
        if (!this.eventMethods.containsKey((Object)event.getClass())) {
            return;
        }
        try {
            for (final Method m : this.eventMethods.get((Object)event.getClass())) {
                m.invoke(this.modInstance, event);
            }
        }
        catch (final Throwable t) {
            this.controller.errorOccurred(this, t);
        }
    }
    
    @Override
    public ArtifactVersion getProcessedVersion() {
        if (this.processedVersion == null) {
            this.processedVersion = new DefaultArtifactVersion(this.getModId(), this.getVersion());
        }
        return this.processedVersion;
    }
    
    @Override
    public boolean isImmutable() {
        return false;
    }
    
    @Override
    public boolean isNetworkMod() {
        return this.isNetworkMod;
    }
    
    @Override
    public String getDisplayVersion() {
        return this.modMetadata.version;
    }
    
    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return this.minecraftAccepted;
    }
    
    @Override
    public Certificate getSigningCertificate() {
        return this.certificate;
    }
    
    @Override
    public String toString() {
        return "FMLMod:" + this.getModId() + "{" + this.getVersion() + "}";
    }
    
    @Override
    public Map<String, String> getCustomModProperties() {
        return this.customModProperties;
    }
    
    @Override
    public Class<?> getCustomResourcePackClass() {
        try {
            return this.getSource().isDirectory() ? Class.forName("cpw.mods.fml.client.FMLFolderResourcePack", true, this.getClass().getClassLoader()) : Class.forName("cpw.mods.fml.client.FMLFileResourcePack", true, this.getClass().getClassLoader());
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
    }
    
    @Override
    public Map<String, String> getSharedModDescriptor() {
        final Map<String, String> descriptor = Maps.newHashMap();
        descriptor.put("modsystem", "FML");
        descriptor.put("id", this.getModId());
        descriptor.put("version", this.getDisplayVersion());
        descriptor.put("name", this.getName());
        descriptor.put("url", this.modMetadata.url);
        descriptor.put("authors", this.modMetadata.getAuthorList());
        descriptor.put("description", this.modMetadata.description);
        return descriptor;
    }
    
    static {
        modAnnotationTypes = (BiMap)ImmutableBiMap.builder().put((Object)FMLPreInitializationEvent.class, (Object)Mod.PreInit.class).put((Object)FMLInitializationEvent.class, (Object)Mod.Init.class).put((Object)FMLPostInitializationEvent.class, (Object)Mod.PostInit.class).put((Object)FMLServerAboutToStartEvent.class, (Object)Mod.ServerAboutToStart.class).put((Object)FMLServerStartingEvent.class, (Object)Mod.ServerStarting.class).put((Object)FMLServerStartedEvent.class, (Object)Mod.ServerStarted.class).put((Object)FMLServerStoppingEvent.class, (Object)Mod.ServerStopping.class).put((Object)FMLServerStoppedEvent.class, (Object)Mod.ServerStopped.class).put((Object)FMLInterModComms.IMCEvent.class, (Object)Mod.IMCCallback.class).put((Object)FMLFingerprintViolationEvent.class, (Object)Mod.FingerprintWarning.class).build();
        modTypeAnnotations = FMLModContainer.modAnnotationTypes.inverse();
    }
}
