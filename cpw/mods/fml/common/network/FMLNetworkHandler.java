// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import com.google.common.hash.Hashing;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.net.NetworkInterface;
import com.google.common.collect.Lists;
import java.net.InetAddress;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Set;
import cpw.mods.fml.common.InjectedModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import java.net.SocketAddress;
import net.minecraft.server.MinecraftServer;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.ModContainer;
import java.util.Map;

public class FMLNetworkHandler
{
    private static final int FML_HASH;
    private static final int PROTOCOL_VERSION = 2;
    private static final FMLNetworkHandler INSTANCE;
    static final int LOGIN_RECEIVED = 1;
    static final int CONNECTION_VALID = 2;
    static final int FML_OUT_OF_DATE = -1;
    static final int MISSING_MODS_OR_VERSIONS = -2;
    private Map<jy, Integer> loginStates;
    private Map<ModContainer, NetworkModHandler> networkModHandlers;
    private Map<Integer, NetworkModHandler> networkIdLookup;
    
    public FMLNetworkHandler() {
        this.loginStates = Maps.newHashMap();
        this.networkModHandlers = Maps.newHashMap();
        this.networkIdLookup = Maps.newHashMap();
    }
    
    public static void handlePacket250Packet(final ea packet, final cm network, final ez handler) {
        final String target = packet.a;
        if (target.startsWith("MC|")) {
            handler.handleVanilla250Packet(packet);
        }
        if (target.equals("FML")) {
            instance().handleFMLPacket(packet, network, handler);
        }
        else {
            NetworkRegistry.instance().handleCustomPacket(packet, network, handler);
        }
    }
    
    public static void onConnectionEstablishedToServer(final ez clientHandler, final cm manager, final ep login) {
        NetworkRegistry.instance().clientLoggedIn(clientHandler, manager, login);
    }
    
    private void handleFMLPacket(final ea packet, final cm network, final ez netHandler) {
        final FMLPacket pkt = FMLPacket.readPacket(network, packet.c);
        if (pkt == null) {
            return;
        }
        String userName = "";
        if (netHandler instanceof jy) {
            userName = ((jy)netHandler).g;
        }
        else {
            final uf pl = netHandler.getPlayer();
            if (pl != null) {
                userName = pl.c_();
            }
        }
        pkt.execute(network, this, netHandler, userName);
    }
    
    public static void onConnectionReceivedFromClient(final jy netLoginHandler, final MinecraftServer server, final SocketAddress address, final String userName) {
        instance().handleClientConnection(netLoginHandler, server, address, userName);
    }
    
    private void handleClientConnection(final jy netLoginHandler, final MinecraftServer server, final SocketAddress address, final String userName) {
        if (this.loginStates.containsKey(netLoginHandler)) {
            switch (this.loginStates.get(netLoginHandler)) {
                case 1: {
                    final String modKick = NetworkRegistry.instance().connectionReceived(netLoginHandler, (cm)netLoginHandler.a);
                    if (modKick != null) {
                        netLoginHandler.completeConnection(modKick);
                        this.loginStates.remove(netLoginHandler);
                        return;
                    }
                    if (!this.handleVanillaLoginKick(netLoginHandler, server, address, userName)) {
                        this.loginStates.remove(netLoginHandler);
                        return;
                    }
                    jy.a(netLoginHandler, false);
                    netLoginHandler.a.a((ey)this.getModListRequestPacket());
                    this.loginStates.put(netLoginHandler, 2);
                    break;
                }
                case 2: {
                    netLoginHandler.completeConnection((String)null);
                    this.loginStates.remove(netLoginHandler);
                    break;
                }
                case -2: {
                    netLoginHandler.completeConnection("The server requires mods that are absent or out of date on your client");
                    this.loginStates.remove(netLoginHandler);
                    break;
                }
                case -1: {
                    netLoginHandler.completeConnection("Your client is not running a new enough version of FML to connect to this server");
                    this.loginStates.remove(netLoginHandler);
                    break;
                }
                default: {
                    netLoginHandler.completeConnection("There was a problem during FML negotiation");
                    this.loginStates.remove(netLoginHandler);
                    break;
                }
            }
            return;
        }
        if (this.handleVanillaLoginKick(netLoginHandler, server, address, userName)) {
            FMLLog.fine("Connection from %s rejected - no FML packet received from client", userName);
            netLoginHandler.completeConnection("You don't have FML installed, you cannot connect to this server");
            return;
        }
        FMLLog.fine("Connection from %s was closed by vanilla minecraft", userName);
    }
    
    private boolean handleVanillaLoginKick(final jy netLoginHandler, final MinecraftServer server, final SocketAddress address, final String userName) {
        final hn playerList = server.af();
        final String kickReason = playerList.a(address, userName);
        if (kickReason != null) {
            netLoginHandler.completeConnection(kickReason);
        }
        return kickReason == null;
    }
    
    public static void handleLoginPacketOnServer(final jy handler, final ep login) {
        if (login.a == FMLNetworkHandler.FML_HASH) {
            if (login.e == 2) {
                FMLLog.finest("Received valid FML login packet from %s", handler.a.c());
                instance().loginStates.put(handler, 1);
            }
            else if (login.e != 2) {
                FMLLog.finest("Received incorrect FML (%x) login packet from %s", login.e, handler.a.c());
                instance().loginStates.put(handler, -1);
            }
        }
        else {
            FMLLog.fine("Received invalid login packet (%x, %x) from %s", login.a, login.e, handler.a.c());
        }
    }
    
    static void setHandlerState(final jy handler, final int state) {
        instance().loginStates.put(handler, state);
    }
    
    public static FMLNetworkHandler instance() {
        return FMLNetworkHandler.INSTANCE;
    }
    
    public static ep getFMLFakeLoginPacket() {
        FMLCommonHandler.instance().getSidedDelegate().setClientCompatibilityLevel((byte)0);
        final ep fake = new ep();
        fake.a = FMLNetworkHandler.FML_HASH;
        fake.e = 2;
        fake.d = ace.a;
        fake.b = acg.a[0];
        return fake;
    }
    
    public ea getModListRequestPacket() {
        return PacketDispatcher.getPacket("FML", FMLPacket.makePacket(FMLPacket.Type.MOD_LIST_REQUEST, new Object[0]));
    }
    
    public void registerNetworkMod(final NetworkModHandler handler) {
        this.networkModHandlers.put(handler.getContainer(), handler);
        this.networkIdLookup.put(handler.getNetworkId(), handler);
    }
    
    public boolean registerNetworkMod(final ModContainer container, final Class<?> networkModClass, final ASMDataTable asmData) {
        final NetworkModHandler handler = new NetworkModHandler(container, networkModClass, asmData);
        if (handler.isNetworkMod()) {
            this.registerNetworkMod(handler);
        }
        return handler.isNetworkMod();
    }
    
    public NetworkModHandler findNetworkModHandler(final Object mc) {
        if (mc instanceof InjectedModContainer) {
            return this.networkModHandlers.get(((InjectedModContainer)mc).wrappedContainer);
        }
        if (mc instanceof ModContainer) {
            return this.networkModHandlers.get(mc);
        }
        if (mc instanceof Integer) {
            return this.networkIdLookup.get(mc);
        }
        return this.networkModHandlers.get(FMLCommonHandler.instance().findContainerFor(mc));
    }
    
    public Set<ModContainer> getNetworkModList() {
        return this.networkModHandlers.keySet();
    }
    
    public static void handlePlayerLogin(final jv player, final ka netHandler, final cm manager) {
        NetworkRegistry.instance().playerLoggedIn(player, netHandler, manager);
        GameRegistry.onPlayerLogin((uf)player);
    }
    
    public Map<Integer, NetworkModHandler> getNetworkIdMap() {
        return this.networkIdLookup;
    }
    
    public void bindNetworkId(final String key, final Integer value) {
        final Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
        final NetworkModHandler handler = this.findNetworkModHandler(mods.get(key));
        if (handler != null) {
            handler.setNetworkId(value);
            this.networkIdLookup.put(value, handler);
        }
    }
    
    public static void onClientConnectionToRemoteServer(final ez netClientHandler, final String server, final int port, final cm networkManager) {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, port, networkManager);
    }
    
    public static void onClientConnectionToIntegratedServer(final ez netClientHandler, final MinecraftServer server, final cm networkManager) {
        NetworkRegistry.instance().connectionOpened(netClientHandler, server, networkManager);
    }
    
    public static void onConnectionClosed(final cm manager, final uf player) {
        NetworkRegistry.instance().connectionClosed(manager, player);
    }
    
    public static void openGui(final uf player, final Object mod, final int modGuiId, final abw world, final int x, final int y, final int z) {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null) {
            final NetworkModHandler nmh = instance().findNetworkModHandler(mod);
            if (nmh == null) {
                FMLLog.warning("A mod tried to open a gui on the server without being a NetworkMod", new Object[0]);
                return;
            }
            mc = nmh.getContainer();
        }
        if (player instanceof jv) {
            NetworkRegistry.instance().openRemoteGui(mc, (jv)player, modGuiId, world, x, y, z);
        }
        else if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT)) {
            NetworkRegistry.instance().openLocalGui(mc, player, modGuiId, world, x, y, z);
        }
        else {
            FMLLog.fine("Invalid attempt to open a local GUI on a dedicated server. This is likely a bug. GUIID: %s,%d", mc.getModId(), modGuiId);
        }
    }
    
    public static ey getEntitySpawningPacket(final nn entity) {
        final EntityRegistry.EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
        if (er == null) {
            return null;
        }
        if (er.usesVanillaSpawning()) {
            return null;
        }
        return (ey)PacketDispatcher.getPacket("FML", FMLPacket.makePacket(FMLPacket.Type.ENTITYSPAWN, er, entity, instance().findNetworkModHandler(er.getContainer())));
    }
    
    public static void makeEntitySpawnAdjustment(final int entityId, final jv player, final int serverX, final int serverY, final int serverZ) {
        final ea pkt = PacketDispatcher.getPacket("FML", FMLPacket.makePacket(FMLPacket.Type.ENTITYSPAWNADJUSTMENT, entityId, serverX, serverY, serverZ));
        player.a.b((ey)pkt);
    }
    
    public static InetAddress computeLocalHost() throws IOException {
        InetAddress add = null;
        final List<InetAddress> addresses = Lists.newArrayList();
        final InetAddress localHost = InetAddress.getLocalHost();
        for (final NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isLoopback() && ni.isUp()) {
                addresses.addAll(Collections.list(ni.getInetAddresses()));
                if (addresses.contains(localHost)) {
                    add = localHost;
                    break;
                }
                continue;
            }
        }
        if (add == null && !addresses.isEmpty()) {
            for (final InetAddress addr : addresses) {
                if (addr.getAddress().length == 4) {
                    add = addr;
                    break;
                }
            }
        }
        if (add == null) {
            add = localHost;
        }
        return add;
    }
    
    public static dm handleChatMessage(final ez handler, final dm chat) {
        return NetworkRegistry.instance().handleChat(handler, chat);
    }
    
    public static void handlePacket131Packet(final ez handler, final dr mapData) {
        if (handler instanceof ka || mapData.a != yc.bf.cv) {
            NetworkRegistry.instance().handleTinyPacket(handler, mapData);
        }
        else {
            FMLCommonHandler.instance().handleTinyPacket(handler, mapData);
        }
    }
    
    public static int getCompatibilityLevel() {
        return 2;
    }
    
    public static boolean vanillaLoginPacketCompatibility() {
        return FMLCommonHandler.instance().getSidedDelegate().getClientCompatibilityLevel() == 0;
    }
    
    static {
        FML_HASH = Hashing.murmur3_32().hashString((CharSequence)"FML").asInt();
        INSTANCE = new FMLNetworkHandler();
    }
}
