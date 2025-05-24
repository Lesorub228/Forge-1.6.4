// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import com.google.common.collect.Maps;
import java.security.cert.Certificate;
import cpw.mods.fml.common.versioning.VersionRange;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Constructor;
import cpw.mods.fml.common.ModClassLoader;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.ILanguageAdapter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import java.util.EnumSet;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.common.MetadataCollection;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.lang.reflect.Modifier;
import cpw.mods.fml.common.LoaderException;
import java.util.logging.Level;
import java.io.Reader;
import java.io.FileReader;
import cpw.mods.fml.common.FMLLog;
import java.util.Properties;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import cpw.mods.fml.common.LoadController;
import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.ProxyInjector;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.discovery.ContainerType;
import java.util.ArrayList;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.util.Set;
import java.io.File;
import cpw.mods.fml.common.ModContainer;

public class ModLoaderModContainer implements ModContainer
{
    public BaseModProxy mod;
    private File modSource;
    public Set<ArtifactVersion> requirements;
    public ArrayList<ArtifactVersion> dependencies;
    public ArrayList<ArtifactVersion> dependants;
    private ContainerType sourceType;
    private ModMetadata metadata;
    private ProxyInjector sidedProxy;
    private BaseModTicker gameTickHandler;
    private BaseModTicker guiTickHandler;
    private String modClazzName;
    private String modId;
    private EventBus bus;
    private LoadController controller;
    private boolean enabled;
    private String sortingProperties;
    private ArtifactVersion processedVersion;
    private boolean isNetworkMod;
    private List<ab> serverCommands;
    
    public ModLoaderModContainer(final String className, final File modSource, final String sortingProperties) {
        this.requirements = Sets.newHashSet();
        this.dependencies = Lists.newArrayList();
        this.dependants = Lists.newArrayList();
        this.enabled = true;
        this.serverCommands = Lists.newArrayList();
        this.modClazzName = className;
        this.modSource = modSource;
        this.modId = (className.contains(".") ? className.substring(className.lastIndexOf(46) + 1) : className);
        this.sortingProperties = (Strings.isNullOrEmpty(sortingProperties) ? "" : sortingProperties);
    }
    
    ModLoaderModContainer(final BaseModProxy instance) {
        this.requirements = Sets.newHashSet();
        this.dependencies = Lists.newArrayList();
        this.dependants = Lists.newArrayList();
        this.enabled = true;
        this.serverCommands = Lists.newArrayList();
        this.mod = instance;
        this.gameTickHandler = new BaseModTicker(instance, false);
        this.guiTickHandler = new BaseModTicker(instance, true);
    }
    
    private void configureMod(final Class<? extends BaseModProxy> modClazz, final ASMDataTable asmData) {
        final File configDir = Loader.instance().getConfigDir();
        final File modConfig = new File(configDir, String.format("%s.cfg", this.getModId()));
        final Properties props = new Properties();
        boolean existingConfigFound = false;
        boolean mlPropFound = false;
        if (modConfig.exists()) {
            try {
                FMLLog.fine("Reading existing configuration file for %s : %s", this.getModId(), modConfig.getName());
                final FileReader configReader = new FileReader(modConfig);
                props.load(configReader);
                configReader.close();
            }
            catch (final Exception e) {
                FMLLog.log(Level.SEVERE, e, "Error occured reading mod configuration file %s", modConfig.getName());
                throw new LoaderException(e);
            }
            existingConfigFound = true;
        }
        final StringBuffer comments = new StringBuffer();
        comments.append("MLProperties: name (type:default) min:max -- information\n");
        final List<ModProperty> mlPropFields = Lists.newArrayList();
        try {
            for (final ASMDataTable.ASMData dat : Sets.union(asmData.getAnnotationsFor(this).get((Object)"net.minecraft.src.MLProp"), asmData.getAnnotationsFor(this).get((Object)"MLProp"))) {
                if (dat.getClassName().equals(this.modClazzName)) {
                    try {
                        mlPropFields.add(new ModProperty(modClazz.getDeclaredField(dat.getObjectName()), dat.getAnnotationInfo()));
                        FMLLog.finest("Found an MLProp field %s in %s", dat.getObjectName(), this.getModId());
                    }
                    catch (final Exception e2) {
                        FMLLog.log(Level.WARNING, e2, "An error occured trying to access field %s in mod %s", dat.getObjectName(), this.getModId());
                    }
                }
            }
            for (final ModProperty property : mlPropFields) {
                if (!Modifier.isStatic(property.field().getModifiers())) {
                    FMLLog.info("The MLProp field %s in mod %s appears not to be static", property.field().getName(), this.getModId());
                }
                else {
                    FMLLog.finest("Considering MLProp field %s", property.field().getName());
                    final Field f = property.field();
                    final String propertyName = Strings.nullToEmpty(property.name()).isEmpty() ? f.getName() : property.name();
                    String propertyValue = null;
                    Object defaultValue = null;
                    try {
                        defaultValue = f.get(null);
                        propertyValue = props.getProperty(propertyName, this.extractValue(defaultValue));
                        final Object currentValue = this.parseValue(propertyValue, property, f.getType(), propertyName);
                        FMLLog.finest("Configuration for %s.%s found values default: %s, configured: %s, interpreted: %s", this.modClazzName, propertyName, defaultValue, propertyValue, currentValue);
                        if (currentValue != null && !currentValue.equals(defaultValue)) {
                            FMLLog.finest("Configuration for %s.%s value set to: %s", this.modClazzName, propertyName, currentValue);
                            f.set(null, currentValue);
                        }
                    }
                    catch (final Exception e3) {
                        FMLLog.log(Level.SEVERE, e3, "Invalid configuration found for %s in %s", propertyName, modConfig.getName());
                        throw new LoaderException(e3);
                    }
                    finally {
                        comments.append(String.format("MLProp : %s (%s:%s", propertyName, f.getType().getName(), defaultValue));
                        if (property.min() != Double.MIN_VALUE) {
                            comments.append(",>=").append(String.format("%.1f", property.min()));
                        }
                        if (property.max() != Double.MAX_VALUE) {
                            comments.append(",<=").append(String.format("%.1f", property.max()));
                        }
                        comments.append(")");
                        if (!Strings.nullToEmpty(property.info()).isEmpty()) {
                            comments.append(" -- ").append(property.info());
                        }
                        if (propertyValue != null) {
                            props.setProperty(propertyName, this.extractValue(propertyValue));
                        }
                        comments.append("\n");
                    }
                    mlPropFound = true;
                }
            }
        }
        finally {
            if (!mlPropFound && !existingConfigFound) {
                FMLLog.fine("No MLProp configuration for %s found or required. No file written", this.getModId());
                return;
            }
            if (!mlPropFound && existingConfigFound) {
                final File mlPropBackup = new File(modConfig.getParent(), modConfig.getName() + ".bak");
                FMLLog.fine("MLProp configuration file for %s found but not required. Attempting to rename file to %s", this.getModId(), mlPropBackup.getName());
                final boolean renamed = modConfig.renameTo(mlPropBackup);
                if (renamed) {
                    FMLLog.fine("Unused MLProp configuration file for %s renamed successfully to %s", this.getModId(), mlPropBackup.getName());
                }
                else {
                    FMLLog.fine("Unused MLProp configuration file for %s renamed UNSUCCESSFULLY to %s", this.getModId(), mlPropBackup.getName());
                }
                return;
            }
            try {
                final FileWriter configWriter = new FileWriter(modConfig);
                props.store(configWriter, comments.toString());
                configWriter.close();
                FMLLog.fine("Configuration for %s written to %s", this.getModId(), modConfig.getName());
            }
            catch (final IOException e4) {
                FMLLog.log(Level.SEVERE, e4, "Error trying to write the config file %s", modConfig.getName());
                throw new LoaderException(e4);
            }
        }
    }
    
    private Object parseValue(final String val, final ModProperty property, final Class<?> type, final String propertyName) {
        if (type.isAssignableFrom(String.class)) {
            return val;
        }
        if (type.isAssignableFrom(Boolean.TYPE) || type.isAssignableFrom(Boolean.class)) {
            return Boolean.parseBoolean(val);
        }
        if (!Number.class.isAssignableFrom(type) && !type.isPrimitive()) {
            throw new IllegalArgumentException(String.format("MLProp declared on %s of type %s, an unsupported type", propertyName, type.getName()));
        }
        Number n = null;
        if (type.isAssignableFrom(Double.TYPE) || Double.class.isAssignableFrom(type)) {
            n = Double.parseDouble(val);
        }
        else if (type.isAssignableFrom(Float.TYPE) || Float.class.isAssignableFrom(type)) {
            n = Float.parseFloat(val);
        }
        else if (type.isAssignableFrom(Long.TYPE) || Long.class.isAssignableFrom(type)) {
            n = Long.parseLong(val);
        }
        else if (type.isAssignableFrom(Integer.TYPE) || Integer.class.isAssignableFrom(type)) {
            n = Integer.parseInt(val);
        }
        else if (type.isAssignableFrom(Short.TYPE) || Short.class.isAssignableFrom(type)) {
            n = Short.parseShort(val);
        }
        else {
            if (!type.isAssignableFrom(Byte.TYPE) && !Byte.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(String.format("MLProp declared on %s of type %s, an unsupported type", propertyName, type.getName()));
            }
            n = Byte.parseByte(val);
        }
        final double dVal = n.doubleValue();
        if ((property.min() != Double.MIN_VALUE && dVal < property.min()) || (property.max() != Double.MAX_VALUE && dVal > property.max())) {
            FMLLog.warning("Configuration for %s.%s found value %s outside acceptable range %s,%s", this.modClazzName, propertyName, n, property.min(), property.max());
            return null;
        }
        return n;
    }
    
    private String extractValue(final Object value) {
        if (String.class.isInstance(value)) {
            return (String)value;
        }
        if (Number.class.isInstance(value) || Boolean.class.isInstance(value)) {
            return String.valueOf(value);
        }
        throw new IllegalArgumentException("MLProp declared on non-standard type");
    }
    
    @Override
    public String getName() {
        return (this.mod != null) ? this.mod.getName() : this.modId;
    }
    
    @Override
    public String getSortingRules() {
        return this.sortingProperties;
    }
    
    @Override
    public boolean matches(final Object mod) {
        return this.mod == mod;
    }
    
    public static <A extends BaseModProxy> List<A> findAll(final Class<A> clazz) {
        final ArrayList<A> modList = new ArrayList<A>();
        for (final ModContainer mc : Loader.instance().getActiveModList()) {
            if (mc instanceof ModLoaderModContainer && mc.getMod() != null) {
                modList.add((A)((ModLoaderModContainer)mc).mod);
            }
        }
        return modList;
    }
    
    @Override
    public File getSource() {
        return this.modSource;
    }
    
    @Override
    public Object getMod() {
        return this.mod;
    }
    
    @Override
    public Set<ArtifactVersion> getRequirements() {
        return this.requirements;
    }
    
    @Override
    public List<ArtifactVersion> getDependants() {
        return this.dependants;
    }
    
    @Override
    public List<ArtifactVersion> getDependencies() {
        return this.dependencies;
    }
    
    @Override
    public String toString() {
        return this.modId;
    }
    
    @Override
    public ModMetadata getMetadata() {
        return this.metadata;
    }
    
    @Override
    public String getVersion() {
        if (this.mod == null || this.mod.getVersion() == null) {
            return "Not available";
        }
        return this.mod.getVersion();
    }
    
    public BaseModTicker getGameTickHandler() {
        return this.gameTickHandler;
    }
    
    public BaseModTicker getGUITickHandler() {
        return this.guiTickHandler;
    }
    
    @Override
    public String getModId() {
        return this.modId;
    }
    
    @Override
    public void bindMetadata(final MetadataCollection mc) {
        final Map<String, Object> dummyMetadata = (Map<String, Object>)ImmutableMap.builder().put((Object)"name", (Object)this.modId).put((Object)"version", (Object)"1.0").build();
        this.metadata = mc.getMetadataForId(this.modId, dummyMetadata);
        Loader.instance().computeDependencies(this.sortingProperties, this.getRequirements(), this.getDependencies(), this.getDependants());
    }
    
    @Override
    public void setEnabledState(final boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        if (this.enabled) {
            FMLLog.fine("Enabling mod %s", this.getModId());
            this.bus = bus;
            this.controller = controller;
            bus.register((Object)this);
            return true;
        }
        return false;
    }
    
    @Subscribe
    public void constructMod(final FMLConstructionEvent event) {
        try {
            final ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(this.modSource);
            final EnumSet<TickType> ticks = EnumSet.noneOf(TickType.class);
            this.gameTickHandler = new BaseModTicker(ticks, false);
            this.guiTickHandler = new BaseModTicker(ticks.clone(), true);
            final Class<? extends BaseModProxy> modClazz = modClassLoader.loadBaseModClass(this.modClazzName);
            this.configureMod(modClazz, event.getASMHarvestedData());
            this.isNetworkMod = FMLNetworkHandler.instance().registerNetworkMod(this, modClazz, event.getASMHarvestedData());
            ModLoaderNetworkHandler dummyHandler = null;
            if (!this.isNetworkMod) {
                FMLLog.fine("Injecting dummy network mod handler for BaseMod %s", this.getModId());
                dummyHandler = new ModLoaderNetworkHandler(this);
                FMLNetworkHandler.instance().registerNetworkMod(dummyHandler);
            }
            final Constructor<? extends BaseModProxy> ctor = modClazz.getConstructor((Class<?>[])new Class[0]);
            ctor.setAccessible(true);
            this.mod = (BaseModProxy)modClazz.newInstance();
            if (dummyHandler != null) {
                dummyHandler.setBaseMod(this.mod);
            }
            ProxyInjector.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide(), new ILanguageAdapter.JavaAdapter());
        }
        catch (final Exception e) {
            this.controller.errorOccurred(this, e);
            Throwables.propagateIfPossible((Throwable)e);
        }
    }
    
    @Subscribe
    public void preInit(final FMLPreInitializationEvent event) {
        try {
            this.gameTickHandler.setMod(this.mod);
            this.guiTickHandler.setMod(this.mod);
            TickRegistry.registerTickHandler(this.gameTickHandler, Side.CLIENT);
            TickRegistry.registerTickHandler(this.guiTickHandler, Side.CLIENT);
            GameRegistry.registerWorldGenerator(ModLoaderHelper.buildWorldGenHelper(this.mod));
            GameRegistry.registerFuelHandler(ModLoaderHelper.buildFuelHelper(this.mod));
            GameRegistry.registerCraftingHandler(ModLoaderHelper.buildCraftingHelper(this.mod));
            GameRegistry.registerPickupHandler(ModLoaderHelper.buildPickupHelper(this.mod));
            NetworkRegistry.instance().registerChatListener(ModLoaderHelper.buildChatListener(this.mod));
            NetworkRegistry.instance().registerConnectionHandler(ModLoaderHelper.buildConnectionHelper(this.mod));
        }
        catch (final Exception e) {
            this.controller.errorOccurred(this, e);
            Throwables.propagateIfPossible((Throwable)e);
        }
    }
    
    @Subscribe
    public void init(final FMLInitializationEvent event) {
        try {
            this.mod.load();
        }
        catch (final Throwable t) {
            this.controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
        }
    }
    
    @Subscribe
    public void postInit(final FMLPostInitializationEvent event) {
        try {
            this.mod.modsLoaded();
        }
        catch (final Throwable t) {
            this.controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
        }
    }
    
    @Subscribe
    public void loadComplete(final FMLLoadCompleteEvent complete) {
        ModLoaderHelper.finishModLoading(this);
    }
    
    @Subscribe
    public void serverStarting(final FMLServerStartingEvent evt) {
        for (final ab cmd : this.serverCommands) {
            evt.registerServerCommand(cmd);
        }
    }
    
    @Override
    public ArtifactVersion getProcessedVersion() {
        if (this.processedVersion == null) {
            this.processedVersion = new DefaultArtifactVersion(this.modId, this.getVersion());
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
        return (this.metadata != null) ? this.metadata.version : this.getVersion();
    }
    
    public void addServerCommand(final ab command) {
        this.serverCommands.add(command);
    }
    
    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return Loader.instance().getMinecraftModContainer().getStaticVersionRange();
    }
    
    @Override
    public Certificate getSigningCertificate() {
        return null;
    }
    
    @Override
    public Map<String, String> getCustomModProperties() {
        return ModLoaderModContainer.EMPTY_PROPERTIES;
    }
    
    @Override
    public Class<?> getCustomResourcePackClass() {
        return null;
    }
    
    @Override
    public Map<String, String> getSharedModDescriptor() {
        final Map<String, String> descriptor = Maps.newHashMap();
        descriptor.put("modsystem", "ModLoader");
        descriptor.put("id", this.getModId());
        descriptor.put("version", this.getDisplayVersion());
        descriptor.put("name", this.getName());
        descriptor.put("url", this.metadata.url);
        descriptor.put("authors", this.metadata.getAuthorList());
        descriptor.put("description", this.metadata.description);
        return descriptor;
    }
}
