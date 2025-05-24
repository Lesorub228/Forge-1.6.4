// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.event;

import cpw.mods.fml.common.FMLLog;
import java.util.Collection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.LoaderState;
import cpw.mods.fml.common.Loader;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;

public class FMLInterModComms
{
    private static final ImmutableList<IMCMessage> emptyIMCList;
    private static ArrayListMultimap<String, IMCMessage> modMessages;
    
    public static boolean sendMessage(final String modId, final String key, final by value) {
        return enqueueStartupMessage(modId, new IMCMessage(key, (Object)value));
    }
    
    public static boolean sendMessage(final String modId, final String key, final ye value) {
        return enqueueStartupMessage(modId, new IMCMessage(key, (Object)value));
    }
    
    public static boolean sendMessage(final String modId, final String key, final String value) {
        return enqueueStartupMessage(modId, new IMCMessage(key, (Object)value));
    }
    
    public static void sendRuntimeMessage(final Object sourceMod, final String modId, final String key, final by value) {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, (Object)value));
    }
    
    public static void sendRuntimeMessage(final Object sourceMod, final String modId, final String key, final ye value) {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, (Object)value));
    }
    
    public static void sendRuntimeMessage(final Object sourceMod, final String modId, final String key, final String value) {
        enqueueMessage(sourceMod, modId, new IMCMessage(key, (Object)value));
    }
    
    private static boolean enqueueStartupMessage(final String modTarget, final IMCMessage message) {
        if (Loader.instance().activeModContainer() == null) {
            return false;
        }
        enqueueMessage(Loader.instance().activeModContainer(), modTarget, message);
        return Loader.isModLoaded(modTarget) && !Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION);
    }
    
    private static void enqueueMessage(final Object sourceMod, final String modTarget, final IMCMessage message) {
        ModContainer mc;
        if (sourceMod instanceof ModContainer) {
            mc = (ModContainer)sourceMod;
        }
        else {
            mc = FMLCommonHandler.instance().findContainerFor(sourceMod);
        }
        if (mc != null && Loader.isModLoaded(modTarget)) {
            message.setSender(mc);
            FMLInterModComms.modMessages.put((Object)modTarget, (Object)message);
        }
    }
    
    public static ImmutableList<IMCMessage> fetchRuntimeMessages(final Object forMod) {
        final ModContainer mc = FMLCommonHandler.instance().findContainerFor(forMod);
        if (mc != null) {
            return (ImmutableList<IMCMessage>)ImmutableList.copyOf((Collection)FMLInterModComms.modMessages.removeAll((Object)mc.getModId()));
        }
        return FMLInterModComms.emptyIMCList;
    }
    
    static {
        emptyIMCList = ImmutableList.of();
        FMLInterModComms.modMessages = (ArrayListMultimap<String, IMCMessage>)ArrayListMultimap.create();
    }
    
    public static class IMCEvent extends FMLEvent
    {
        private ModContainer activeContainer;
        private ImmutableList<IMCMessage> currentList;
        
        @Override
        public void applyModContainer(final ModContainer activeContainer) {
            this.activeContainer = activeContainer;
            this.currentList = null;
            FMLLog.finest("Attempting to deliver %d IMC messages to mod %s", FMLInterModComms.modMessages.get((Object)activeContainer.getModId()).size(), activeContainer.getModId());
        }
        
        public ImmutableList<IMCMessage> getMessages() {
            if (this.currentList == null) {
                this.currentList = (ImmutableList<IMCMessage>)ImmutableList.copyOf((Collection)FMLInterModComms.modMessages.removeAll((Object)this.activeContainer.getModId()));
            }
            return this.currentList;
        }
    }
    
    public static final class IMCMessage
    {
        private String sender;
        public final String key;
        private Object value;
        
        private IMCMessage(final String key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String toString() {
            return this.sender;
        }
        
        public String getSender() {
            return this.sender;
        }
        
        void setSender(final ModContainer activeModContainer) {
            this.sender = activeModContainer.getModId();
        }
        
        public String getStringValue() {
            return (String)this.value;
        }
        
        public by getNBTValue() {
            return (by)this.value;
        }
        
        public ye getItemStackValue() {
            return (ye)this.value;
        }
        
        public Class<?> getMessageType() {
            return this.value.getClass();
        }
        
        public boolean isStringMessage() {
            return String.class.isAssignableFrom(this.getMessageType());
        }
        
        public boolean isItemStackMessage() {
            return ye.class.isAssignableFrom(this.getMessageType());
        }
        
        public boolean isNBTMessage() {
            return by.class.isAssignableFrom(this.getMessageType());
        }
    }
}
