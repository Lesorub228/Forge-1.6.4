// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.event.FMLStateEvent;

public enum LoaderState
{
    NOINIT("Uninitialized", (Class<? extends FMLStateEvent>)null), 
    LOADING("Loading", (Class<? extends FMLStateEvent>)null), 
    CONSTRUCTING("Constructing mods", (Class<? extends FMLStateEvent>)FMLConstructionEvent.class), 
    PREINITIALIZATION("Pre-initializing mods", (Class<? extends FMLStateEvent>)FMLPreInitializationEvent.class), 
    INITIALIZATION("Initializing mods", (Class<? extends FMLStateEvent>)FMLInitializationEvent.class), 
    POSTINITIALIZATION("Post-initializing mods", (Class<? extends FMLStateEvent>)FMLPostInitializationEvent.class), 
    AVAILABLE("Mod loading complete", (Class<? extends FMLStateEvent>)FMLLoadCompleteEvent.class), 
    SERVER_ABOUT_TO_START("Server about to start", (Class<? extends FMLStateEvent>)FMLServerAboutToStartEvent.class), 
    SERVER_STARTING("Server starting", (Class<? extends FMLStateEvent>)FMLServerStartingEvent.class), 
    SERVER_STARTED("Server started", (Class<? extends FMLStateEvent>)FMLServerStartedEvent.class), 
    SERVER_STOPPING("Server stopping", (Class<? extends FMLStateEvent>)FMLServerStoppingEvent.class), 
    SERVER_STOPPED("Server stopped", (Class<? extends FMLStateEvent>)FMLServerStoppedEvent.class), 
    ERRORED("Mod Loading errored", (Class<? extends FMLStateEvent>)null);
    
    private Class<? extends FMLStateEvent> eventClass;
    private String name;
    
    private LoaderState(final String name, final Class<? extends FMLStateEvent> event) {
        this.name = name;
        this.eventClass = event;
    }
    
    public LoaderState transition(final boolean errored) {
        if (errored) {
            return LoaderState.ERRORED;
        }
        if (this == LoaderState.SERVER_STOPPED) {
            return LoaderState.AVAILABLE;
        }
        return values()[(this.ordinal() < values().length) ? (this.ordinal() + 1) : this.ordinal()];
    }
    
    public boolean hasEvent() {
        return this.eventClass != null;
    }
    
    public FMLStateEvent getEvent(final Object... eventData) {
        try {
            return (FMLStateEvent)this.eventClass.getConstructor(Object[].class).newInstance(eventData);
        }
        catch (final Exception e) {
            throw Throwables.propagate((Throwable)e);
        }
    }
    
    public LoaderState requiredState() {
        if (this == LoaderState.NOINIT) {
            return LoaderState.NOINIT;
        }
        return values()[this.ordinal() - 1];
    }
    
    public enum ModState
    {
        UNLOADED("Unloaded"), 
        LOADED("Loaded"), 
        CONSTRUCTED("Constructed"), 
        PREINITIALIZED("Pre-initialized"), 
        INITIALIZED("Initialized"), 
        POSTINITIALIZED("Post-initialized"), 
        AVAILABLE("Available"), 
        DISABLED("Disabled"), 
        ERRORED("Errored");
        
        private String label;
        
        private ModState(final String label) {
            this.label = label;
        }
        
        @Override
        public String toString() {
            return this.label;
        }
    }
}
