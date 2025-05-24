// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import com.google.common.collect.Iterables;
import com.google.common.base.Joiner;
import java.lang.reflect.InvocationTargetException;
import com.google.common.collect.ImmutableBiMap;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import com.google.common.base.Function;
import java.util.Collection;
import com.google.common.collect.Collections2;
import cpw.mods.fml.common.functions.ArtifactVersionNameFunction;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLEvent;
import com.google.common.eventbus.Subscribe;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.common.event.FMLLoadEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;

public class LoadController
{
    private Loader loader;
    private EventBus masterChannel;
    private ImmutableMap<String, EventBus> eventChannels;
    private LoaderState state;
    private Multimap<String, LoaderState.ModState> modStates;
    private Multimap<String, Throwable> errors;
    private Map<String, ModContainer> modList;
    private List<ModContainer> activeModList;
    private ModContainer activeContainer;
    private BiMap<ModContainer, Object> modObjectList;
    
    public LoadController(final Loader loader) {
        this.modStates = (Multimap<String, LoaderState.ModState>)ArrayListMultimap.create();
        this.errors = (Multimap<String, Throwable>)ArrayListMultimap.create();
        this.activeModList = Lists.newArrayList();
        this.loader = loader;
        (this.masterChannel = new EventBus("FMLMainChannel")).register((Object)this);
        this.state = LoaderState.NOINIT;
    }
    
    @Subscribe
    public void buildModList(final FMLLoadEvent event) {
        this.modList = this.loader.getIndexedModList();
        final ImmutableMap.Builder<String, EventBus> eventBus = (ImmutableMap.Builder<String, EventBus>)ImmutableMap.builder();
        for (final ModContainer mod : this.loader.getModList()) {
            FMLRelaunchLog.makeLog(mod.getModId());
            final Logger modLogger = Logger.getLogger(mod.getModId());
            final Logger eventLog = Logger.getLogger(EventBus.class.getName() + "." + mod.getModId());
            eventLog.setParent(modLogger);
            final EventBus bus = new EventBus(mod.getModId());
            final boolean isActive = mod.registerBus(bus, this);
            if (isActive) {
                final Level level = Logger.getLogger(mod.getModId()).getLevel();
                FMLLog.log(mod.getModId(), Level.FINE, "Mod Logging channel %s configured at %s level.", mod.getModId(), (level == null) ? "default" : level);
                FMLLog.log(mod.getModId(), Level.INFO, "Activating mod %s", mod.getModId());
                this.activeModList.add(mod);
                this.modStates.put((Object)mod.getModId(), (Object)LoaderState.ModState.UNLOADED);
                eventBus.put((Object)mod.getModId(), (Object)bus);
                FMLCommonHandler.instance().addModToResourcePack(mod);
            }
            else {
                FMLLog.log(mod.getModId(), Level.WARNING, "Mod %s has been disabled through configuration", mod.getModId());
                this.modStates.put((Object)mod.getModId(), (Object)LoaderState.ModState.UNLOADED);
                this.modStates.put((Object)mod.getModId(), (Object)LoaderState.ModState.DISABLED);
            }
        }
        this.eventChannels = (ImmutableMap<String, EventBus>)eventBus.build();
        FMLCommonHandler.instance().updateResourcePackList();
    }
    
    public void distributeStateMessage(final LoaderState state, final Object... eventData) {
        if (state.hasEvent()) {
            this.masterChannel.post((Object)state.getEvent(eventData));
        }
    }
    
    public void transition(final LoaderState desiredState, final boolean forceState) {
        final LoaderState oldState = this.state;
        this.state = this.state.transition(!this.errors.isEmpty());
        if (this.state == desiredState || forceState) {
            if (this.state != desiredState && forceState) {
                FMLLog.info("The state engine was in incorrect state %s and forced into state %s. Errors may have been discarded.", this.state, desiredState);
                this.forceState(desiredState);
            }
            return;
        }
        Throwable toThrow = null;
        FMLLog.severe("Fatal errors were detected during the transition from %s to %s. Loading cannot continue", oldState, desiredState);
        final StringBuilder sb = new StringBuilder();
        this.printModStates(sb);
        FMLLog.getLogger().severe(sb.toString());
        if (this.errors.size() <= 0) {
            FMLLog.severe("The ForgeModLoader state engine has become corrupted. Probably, a state was missed by and invalid modification to a base classForgeModLoader depends on. This is a critical error and not recoverable. Investigate any modifications to base classes outside ofForgeModLoader, especially Optifine, to see if there are fixes available.", new Object[0]);
            throw new RuntimeException("The ForgeModLoader state engine is invalid");
        }
        FMLLog.severe("The following problems were captured during this phase", new Object[0]);
        for (final Map.Entry<String, Throwable> error : this.errors.entries()) {
            FMLLog.log(Level.SEVERE, error.getValue(), "Caught exception from %s", error.getKey());
            if (error.getValue() instanceof IFMLHandledException) {
                toThrow = error.getValue();
            }
            else {
                if (toThrow != null) {
                    continue;
                }
                toThrow = error.getValue();
            }
        }
        if (toThrow != null && toThrow instanceof RuntimeException) {
            throw (RuntimeException)toThrow;
        }
        throw new LoaderException(toThrow);
    }
    
    public ModContainer activeContainer() {
        return this.activeContainer;
    }
    
    @Subscribe
    public void propogateStateMessage(final FMLEvent stateEvent) {
        if (stateEvent instanceof FMLPreInitializationEvent) {
            this.modObjectList = (BiMap<ModContainer, Object>)this.buildModObjectList();
        }
        for (final ModContainer mc : this.activeModList) {
            this.sendEventToModContainer(stateEvent, mc);
        }
    }
    
    private void sendEventToModContainer(final FMLEvent stateEvent, final ModContainer mc) {
        final String modId = mc.getModId();
        final Collection<String> requirements = Collections2.transform((Collection)mc.getRequirements(), (Function)new ArtifactVersionNameFunction());
        for (final ArtifactVersion av : mc.getDependencies()) {
            if (av.getLabel() != null && requirements.contains(av.getLabel()) && this.modStates.containsEntry((Object)av.getLabel(), (Object)LoaderState.ModState.ERRORED)) {
                FMLLog.log(modId, Level.SEVERE, "Skipping event %s and marking errored mod %s since required dependency %s has errored", stateEvent.getEventType(), modId, av.getLabel());
                this.modStates.put((Object)modId, (Object)LoaderState.ModState.ERRORED);
                return;
            }
        }
        this.activeContainer = mc;
        stateEvent.applyModContainer(this.activeContainer());
        FMLLog.log(modId, Level.FINEST, "Sending event %s to mod %s", stateEvent.getEventType(), modId);
        ((EventBus)this.eventChannels.get((Object)modId)).post((Object)stateEvent);
        FMLLog.log(modId, Level.FINEST, "Sent event %s to mod %s", stateEvent.getEventType(), modId);
        this.activeContainer = null;
        if (stateEvent instanceof FMLStateEvent) {
            if (!this.errors.containsKey((Object)modId)) {
                this.modStates.put((Object)modId, (Object)((FMLStateEvent)stateEvent).getModState());
            }
            else {
                this.modStates.put((Object)modId, (Object)LoaderState.ModState.ERRORED);
            }
        }
    }
    
    public ImmutableBiMap<ModContainer, Object> buildModObjectList() {
        final ImmutableBiMap.Builder<ModContainer, Object> builder = (ImmutableBiMap.Builder<ModContainer, Object>)ImmutableBiMap.builder();
        for (final ModContainer mc : this.activeModList) {
            if (!mc.isImmutable() && mc.getMod() != null) {
                builder.put((Object)mc, mc.getMod());
            }
            if (mc.getMod() == null && !mc.isImmutable() && this.state != LoaderState.CONSTRUCTING) {
                FMLLog.severe("There is a severe problem with %s - it appears not to have constructed correctly", mc.getModId());
                if (this.state == LoaderState.CONSTRUCTING) {
                    continue;
                }
                this.errorOccurred(mc, new RuntimeException());
            }
        }
        return (ImmutableBiMap<ModContainer, Object>)builder.build();
    }
    
    public void errorOccurred(final ModContainer modContainer, final Throwable exception) {
        if (exception instanceof InvocationTargetException) {
            this.errors.put((Object)modContainer.getModId(), (Object)((InvocationTargetException)exception).getCause());
        }
        else {
            this.errors.put((Object)modContainer.getModId(), (Object)exception);
        }
    }
    
    public void printModStates(final StringBuilder ret) {
        for (final ModContainer mc : this.loader.getModList()) {
            ret.append("\n\t").append(mc.getModId()).append("{").append(mc.getVersion()).append("} [").append(mc.getName()).append("] (").append(mc.getSource().getName()).append(") ");
            Joiner.on("->").appendTo(ret, (Iterable)this.modStates.get((Object)mc.getModId()));
        }
    }
    
    public List<ModContainer> getActiveModList() {
        return this.activeModList;
    }
    
    public LoaderState.ModState getModState(final ModContainer selectedMod) {
        return (LoaderState.ModState)Iterables.getLast((Iterable)this.modStates.get((Object)selectedMod.getModId()), (Object)LoaderState.ModState.AVAILABLE);
    }
    
    public void distributeStateMessage(final Class<?> customEvent) {
        try {
            this.masterChannel.post((Object)customEvent.newInstance());
        }
        catch (final Exception e) {
            FMLLog.log(Level.SEVERE, e, "An unexpected exception", new Object[0]);
            throw new LoaderException(e);
        }
    }
    
    public BiMap<ModContainer, Object> getModObjectList() {
        if (this.modObjectList == null) {
            FMLLog.severe("Detected an attempt by a mod %s to perform game activity during mod construction. This is a serious programming error.", this.activeContainer);
            return (BiMap<ModContainer, Object>)this.buildModObjectList();
        }
        return (BiMap<ModContainer, Object>)ImmutableBiMap.copyOf((Map)this.modObjectList);
    }
    
    public boolean isInState(final LoaderState state) {
        return this.state == state;
    }
    
    boolean hasReachedState(final LoaderState state) {
        return this.state.ordinal() >= state.ordinal() && this.state != LoaderState.ERRORED;
    }
    
    void forceState(final LoaderState newState) {
        this.state = newState;
    }
}
