// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import com.google.common.base.Strings;
import java.util.Iterator;
import java.util.Set;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.lang.annotation.Annotation;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.versioning.VersionRange;
import java.lang.reflect.Method;
import cpw.mods.fml.common.ModContainer;

public class NetworkModHandler
{
    private static Object connectionHandlerDefaultValue;
    private static Object packetHandlerDefaultValue;
    private static Object clientHandlerDefaultValue;
    private static Object serverHandlerDefaultValue;
    private static Object tinyPacketHandlerDefaultValue;
    private static int assignedIds;
    private int localId;
    private int networkId;
    private ModContainer container;
    private NetworkMod mod;
    private Method checkHandler;
    private VersionRange acceptableRange;
    private ITinyPacketHandler tinyPacketHandler;
    
    public NetworkModHandler(final ModContainer container, final NetworkMod modAnnotation) {
        this.container = container;
        this.mod = modAnnotation;
        this.localId = NetworkModHandler.assignedIds++;
        this.networkId = this.localId;
        if (yc.bf.cv == NetworkModHandler.assignedIds) {
            ++NetworkModHandler.assignedIds;
        }
    }
    
    public NetworkModHandler(final ModContainer container, final Class<?> networkModClass, final ASMDataTable table) {
        this(container, networkModClass.getAnnotation(NetworkMod.class));
        if (this.mod == null) {
            return;
        }
        final Set<ASMDataTable.ASMData> versionCheckHandlers = table.getAnnotationsFor(container).get((Object)NetworkMod.VersionCheckHandler.class.getName());
        String versionCheckHandlerMethod = null;
        for (final ASMDataTable.ASMData vch : versionCheckHandlers) {
            if (vch.getClassName().equals(networkModClass.getName())) {
                versionCheckHandlerMethod = vch.getObjectName();
                versionCheckHandlerMethod = versionCheckHandlerMethod.substring(0, versionCheckHandlerMethod.indexOf(40));
                break;
            }
        }
        if (versionCheckHandlerMethod != null) {
            try {
                final Method checkHandlerMethod = networkModClass.getDeclaredMethod(versionCheckHandlerMethod, String.class);
                if (checkHandlerMethod.isAnnotationPresent(NetworkMod.VersionCheckHandler.class)) {
                    this.checkHandler = checkHandlerMethod;
                }
            }
            catch (final Exception e) {
                FMLLog.log(Level.WARNING, e, "The declared version check handler method %s on network mod id %s is not accessible", versionCheckHandlerMethod, container.getModId());
            }
        }
        this.configureNetworkMod(container);
    }
    
    protected void configureNetworkMod(final ModContainer container) {
        if (this.checkHandler == null) {
            final String versionBounds = this.mod.versionBounds();
            if (!Strings.isNullOrEmpty(versionBounds)) {
                try {
                    this.acceptableRange = VersionRange.createFromVersionSpec(versionBounds);
                }
                catch (final InvalidVersionSpecificationException e) {
                    FMLLog.log(Level.WARNING, e, "Invalid bounded range %s specified for network mod id %s", versionBounds, container.getModId());
                }
            }
        }
        FMLLog.finest("Testing mod %s to verify it accepts its own version in a remote connection", container.getModId());
        final boolean acceptsSelf = this.acceptVersion(container.getVersion());
        if (!acceptsSelf) {
            FMLLog.severe("The mod %s appears to reject its own version number (%s) in its version handling. This is likely a severe bug in the mod!", container.getModId(), container.getVersion());
        }
        else {
            FMLLog.finest("The mod %s accepts its own version (%s)", container.getModId(), container.getVersion());
        }
        this.tryCreatingPacketHandler(container, this.mod.packetHandler(), this.mod.channels(), null);
        if (FMLCommonHandler.instance().getSide().isClient() && this.mod.clientPacketHandlerSpec() != this.getClientHandlerSpecDefaultValue()) {
            this.tryCreatingPacketHandler(container, this.mod.clientPacketHandlerSpec().packetHandler(), this.mod.clientPacketHandlerSpec().channels(), Side.CLIENT);
        }
        if (this.mod.serverPacketHandlerSpec() != this.getServerHandlerSpecDefaultValue()) {
            this.tryCreatingPacketHandler(container, this.mod.serverPacketHandlerSpec().packetHandler(), this.mod.serverPacketHandlerSpec().channels(), Side.SERVER);
        }
        if (this.mod.connectionHandler() != this.getConnectionHandlerDefaultValue()) {
            IConnectionHandler instance;
            try {
                instance = (IConnectionHandler)this.mod.connectionHandler().newInstance();
            }
            catch (final Exception e2) {
                FMLLog.log(Level.SEVERE, e2, "Unable to create connection handler instance %s", this.mod.connectionHandler().getName());
                throw new FMLNetworkException(e2);
            }
            NetworkRegistry.instance().registerConnectionHandler(instance);
        }
        if (this.mod.tinyPacketHandler() != this.getTinyPacketHandlerDefaultValue()) {
            try {
                this.tinyPacketHandler = (ITinyPacketHandler)this.mod.tinyPacketHandler().newInstance();
            }
            catch (final Exception e3) {
                FMLLog.log(Level.SEVERE, e3, "Unable to create tiny packet handler instance %s", this.mod.tinyPacketHandler().getName());
                throw new FMLNetworkException(e3);
            }
        }
    }
    
    private void tryCreatingPacketHandler(final ModContainer container, final Class<? extends IPacketHandler> clazz, final String[] channels, final Side side) {
        if (side != null && side.isClient() && !FMLCommonHandler.instance().getSide().isClient()) {
            return;
        }
        if (clazz != this.getPacketHandlerDefaultValue()) {
            if (channels.length == 0) {
                FMLLog.log(Level.WARNING, "The mod id %s attempted to register a packet handler without specifying channels for it", container.getModId());
            }
            else {
                IPacketHandler instance;
                try {
                    instance = (IPacketHandler)clazz.newInstance();
                }
                catch (final Exception e) {
                    FMLLog.log(Level.SEVERE, e, "Unable to create a packet handler instance %s for mod %s", clazz.getName(), container.getModId());
                    throw new FMLNetworkException(e);
                }
                for (final String channel : channels) {
                    NetworkRegistry.instance().registerChannel(instance, channel, side);
                }
            }
        }
        else if (channels.length > 0) {
            FMLLog.warning("The mod id %s attempted to register channels without specifying a packet handler", container.getModId());
        }
    }
    
    private Object getConnectionHandlerDefaultValue() {
        try {
            if (NetworkModHandler.connectionHandlerDefaultValue == null) {
                NetworkModHandler.connectionHandlerDefaultValue = NetworkMod.class.getMethod("connectionHandler", (Class<?>[])new Class[0]).getDefaultValue();
            }
            return NetworkModHandler.connectionHandlerDefaultValue;
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Derp?", e);
        }
    }
    
    private Object getPacketHandlerDefaultValue() {
        try {
            if (NetworkModHandler.packetHandlerDefaultValue == null) {
                NetworkModHandler.packetHandlerDefaultValue = NetworkMod.class.getMethod("packetHandler", (Class<?>[])new Class[0]).getDefaultValue();
            }
            return NetworkModHandler.packetHandlerDefaultValue;
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Derp?", e);
        }
    }
    
    private Object getTinyPacketHandlerDefaultValue() {
        try {
            if (NetworkModHandler.tinyPacketHandlerDefaultValue == null) {
                NetworkModHandler.tinyPacketHandlerDefaultValue = NetworkMod.class.getMethod("tinyPacketHandler", (Class<?>[])new Class[0]).getDefaultValue();
            }
            return NetworkModHandler.tinyPacketHandlerDefaultValue;
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Derp?", e);
        }
    }
    
    private Object getClientHandlerSpecDefaultValue() {
        try {
            if (NetworkModHandler.clientHandlerDefaultValue == null) {
                NetworkModHandler.clientHandlerDefaultValue = NetworkMod.class.getMethod("clientPacketHandlerSpec", (Class<?>[])new Class[0]).getDefaultValue();
            }
            return NetworkModHandler.clientHandlerDefaultValue;
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Derp?", e);
        }
    }
    
    private Object getServerHandlerSpecDefaultValue() {
        try {
            if (NetworkModHandler.serverHandlerDefaultValue == null) {
                NetworkModHandler.serverHandlerDefaultValue = NetworkMod.class.getMethod("serverPacketHandlerSpec", (Class<?>[])new Class[0]).getDefaultValue();
            }
            return NetworkModHandler.serverHandlerDefaultValue;
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException("Derp?", e);
        }
    }
    
    public boolean requiresClientSide() {
        return this.mod.clientSideRequired();
    }
    
    public boolean requiresServerSide() {
        return this.mod.serverSideRequired();
    }
    
    public boolean acceptVersion(final String version) {
        if (this.checkHandler != null) {
            try {
                return (boolean)this.checkHandler.invoke(this.container.getMod(), version);
            }
            catch (final Exception e) {
                FMLLog.log(Level.WARNING, e, "There was a problem invoking the checkhandler method %s for network mod id %s", this.checkHandler.getName(), this.container.getModId());
                return false;
            }
        }
        if (this.acceptableRange != null) {
            return this.acceptableRange.containsVersion(new DefaultArtifactVersion(version));
        }
        return this.container.getVersion().equals(version);
    }
    
    public int getLocalId() {
        return this.localId;
    }
    
    public int getNetworkId() {
        return this.networkId;
    }
    
    public ModContainer getContainer() {
        return this.container;
    }
    
    public NetworkMod getMod() {
        return this.mod;
    }
    
    public boolean isNetworkMod() {
        return this.mod != null;
    }
    
    public void setNetworkId(final int value) {
        this.networkId = value;
    }
    
    public boolean hasTinyPacketHandler() {
        return this.tinyPacketHandler != null;
    }
    
    public ITinyPacketHandler getTinyPacketHandler() {
        return this.tinyPacketHandler;
    }
    
    static {
        NetworkModHandler.assignedIds = 1;
    }
}
