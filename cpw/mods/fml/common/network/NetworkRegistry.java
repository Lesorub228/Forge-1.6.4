// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.network;

import java.util.logging.Level;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLCommonHandler;
import com.google.common.base.Splitter;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import cpw.mods.fml.common.FMLLog;
import com.google.common.base.Strings;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import com.google.common.base.Joiner;
import cpw.mods.fml.relauncher.Side;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.ArrayListMultimap;
import java.util.List;
import cpw.mods.fml.common.ModContainer;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.Multimap;

public class NetworkRegistry
{
    private static final NetworkRegistry INSTANCE;
    private Multimap<Player, String> activeChannels;
    private Multimap<String, IPacketHandler> universalPacketHandlers;
    private Multimap<String, IPacketHandler> clientPacketHandlers;
    private Multimap<String, IPacketHandler> serverPacketHandlers;
    private Set<IConnectionHandler> connectionHandlers;
    private Map<ModContainer, IGuiHandler> serverGuiHandlers;
    private Map<ModContainer, IGuiHandler> clientGuiHandlers;
    private List<IChatListener> chatListeners;
    
    public NetworkRegistry() {
        this.activeChannels = (Multimap<Player, String>)ArrayListMultimap.create();
        this.universalPacketHandlers = (Multimap<String, IPacketHandler>)ArrayListMultimap.create();
        this.clientPacketHandlers = (Multimap<String, IPacketHandler>)ArrayListMultimap.create();
        this.serverPacketHandlers = (Multimap<String, IPacketHandler>)ArrayListMultimap.create();
        this.connectionHandlers = Sets.newLinkedHashSet();
        this.serverGuiHandlers = Maps.newHashMap();
        this.clientGuiHandlers = Maps.newHashMap();
        this.chatListeners = Lists.newArrayList();
    }
    
    public static NetworkRegistry instance() {
        return NetworkRegistry.INSTANCE;
    }
    
    byte[] getPacketRegistry(final Side side) {
        return Joiner.on('\0').join(Iterables.concat((Iterable)Arrays.asList("FML"), (Iterable)this.universalPacketHandlers.keySet(), (Iterable)(side.isClient() ? this.clientPacketHandlers.keySet() : this.serverPacketHandlers.keySet()))).getBytes(Charsets.UTF_8);
    }
    
    public boolean isChannelActive(final String channel, final Player player) {
        return this.activeChannels.containsEntry((Object)player, (Object)channel);
    }
    
    public void registerChannel(final IPacketHandler handler, final String channelName) {
        if (Strings.isNullOrEmpty(channelName) || (channelName != null && channelName.length() > 16)) {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");
        }
        this.universalPacketHandlers.put((Object)channelName, (Object)handler);
    }
    
    public void registerChannel(final IPacketHandler handler, final String channelName, final Side side) {
        if (side == null) {
            this.registerChannel(handler, channelName);
            return;
        }
        if (Strings.isNullOrEmpty(channelName) || (channelName != null && channelName.length() > 16)) {
            FMLLog.severe("Invalid channel name '%s' : %s", channelName, Strings.isNullOrEmpty(channelName) ? "Channel name is empty" : "Channel name is too long (16 chars is maximum)");
            throw new RuntimeException("Channel name is invalid");
        }
        if (side.isClient()) {
            this.clientPacketHandlers.put((Object)channelName, (Object)handler);
        }
        else {
            this.serverPacketHandlers.put((Object)channelName, (Object)handler);
        }
    }
    
    void activateChannel(final Player player, final String channel) {
        this.activeChannels.put((Object)player, (Object)channel);
    }
    
    void deactivateChannel(final Player player, final String channel) {
        this.activeChannels.remove((Object)player, (Object)channel);
    }
    
    public void registerConnectionHandler(final IConnectionHandler handler) {
        this.connectionHandlers.add(handler);
    }
    
    public void registerChatListener(final IChatListener listener) {
        this.chatListeners.add(listener);
    }
    
    void playerLoggedIn(final jv player, final ka netHandler, final cm manager) {
        this.generateChannelRegistration((uf)player, (ez)netHandler, manager);
        for (final IConnectionHandler handler : this.connectionHandlers) {
            handler.playerLoggedIn((Player)player, (ez)netHandler, manager);
        }
    }
    
    String connectionReceived(final jy netHandler, final cm manager) {
        for (final IConnectionHandler handler : this.connectionHandlers) {
            final String kick = handler.connectionReceived(netHandler, manager);
            if (!Strings.isNullOrEmpty(kick)) {
                return kick;
            }
        }
        return null;
    }
    
    void connectionOpened(final ez netClientHandler, final String server, final int port, final cm networkManager) {
        for (final IConnectionHandler handler : this.connectionHandlers) {
            handler.connectionOpened(netClientHandler, server, port, networkManager);
        }
    }
    
    void connectionOpened(final ez netClientHandler, final MinecraftServer server, final cm networkManager) {
        for (final IConnectionHandler handler : this.connectionHandlers) {
            handler.connectionOpened(netClientHandler, server, networkManager);
        }
    }
    
    void clientLoggedIn(final ez clientHandler, final cm manager, final ep login) {
        this.generateChannelRegistration(clientHandler.getPlayer(), clientHandler, manager);
        for (final IConnectionHandler handler : this.connectionHandlers) {
            handler.clientLoggedIn(clientHandler, manager, login);
        }
    }
    
    void connectionClosed(final cm manager, final uf player) {
        for (final IConnectionHandler handler : this.connectionHandlers) {
            handler.connectionClosed(manager);
        }
        this.activeChannels.removeAll((Object)player);
    }
    
    void generateChannelRegistration(final uf player, final ez netHandler, final cm manager) {
        final ea pkt = new ea();
        pkt.a = "REGISTER";
        pkt.c = this.getPacketRegistry((player instanceof jv) ? Side.SERVER : Side.CLIENT);
        pkt.b = pkt.c.length;
        manager.a((ey)pkt);
    }
    
    void handleCustomPacket(final ea packet, final cm network, final ez handler) {
        if ("REGISTER".equals(packet.a)) {
            this.handleRegistrationPacket(packet, (Player)handler.getPlayer());
        }
        else if ("UNREGISTER".equals(packet.a)) {
            this.handleUnregistrationPacket(packet, (Player)handler.getPlayer());
        }
        else {
            this.handlePacket(packet, network, (Player)handler.getPlayer());
        }
    }
    
    private void handlePacket(final ea packet, final cm network, final Player player) {
        final String channel = packet.a;
        for (final IPacketHandler handler : Iterables.concat((Iterable)this.universalPacketHandlers.get((Object)channel), (Iterable)((player instanceof jv) ? this.serverPacketHandlers.get((Object)channel) : this.clientPacketHandlers.get((Object)channel)))) {
            handler.onPacketData(network, packet, player);
        }
    }
    
    private void handleRegistrationPacket(final ea packet, final Player player) {
        final List<String> channels = this.extractChannelList(packet);
        for (final String channel : channels) {
            this.activateChannel(player, channel);
        }
    }
    
    private void handleUnregistrationPacket(final ea packet, final Player player) {
        final List<String> channels = this.extractChannelList(packet);
        for (final String channel : channels) {
            this.deactivateChannel(player, channel);
        }
    }
    
    private List<String> extractChannelList(final ea packet) {
        final String request = new String(packet.c, Charsets.UTF_8);
        final List<String> channels = Lists.newArrayList(Splitter.on('\0').split((CharSequence)request));
        return channels;
    }
    
    public void registerGuiHandler(final Object mod, final IGuiHandler handler) {
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        if (mc == null) {
            mc = Loader.instance().activeModContainer();
            FMLLog.log(Level.WARNING, "Mod %s attempted to register a gui network handler during a construction phase", mc.getModId());
        }
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
        if (nmh == null) {
            FMLLog.log(Level.FINE, "The mod %s needs to be a @NetworkMod to register a Networked Gui Handler", mc.getModId());
        }
        else {
            this.serverGuiHandlers.put(mc, handler);
        }
        this.clientGuiHandlers.put(mc, handler);
    }
    
    void openRemoteGui(final ModContainer mc, final jv player, final int modGuiId, final abw world, final int x, final int y, final int z) {
        final IGuiHandler handler = this.serverGuiHandlers.get(mc);
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);
        if (handler != null && nmh != null) {
            final uy container = (uy)handler.getServerGuiElement(modGuiId, (uf)player, world, x, y, z);
            if (container != null) {
                player.bN();
                player.k();
                final int windowId = player.bY;
                final ea pkt = new ea();
                pkt.a = "FML";
                pkt.c = FMLPacket.makePacket(FMLPacket.Type.GUIOPEN, windowId, nmh.getNetworkId(), modGuiId, x, y, z);
                pkt.b = pkt.c.length;
                player.a.b((ey)pkt);
                player.bp = container;
                player.bp.d = windowId;
                player.bp.a((vi)player);
            }
        }
    }
    
    void openLocalGui(final ModContainer mc, final uf player, final int modGuiId, final abw world, final int x, final int y, final int z) {
        final IGuiHandler handler = this.clientGuiHandlers.get(mc);
        FMLCommonHandler.instance().showGuiScreen(handler.getClientGuiElement(modGuiId, player, world, x, y, z));
    }
    
    public dm handleChat(final ez handler, dm chat) {
        Side s = Side.CLIENT;
        if (handler instanceof ka) {
            s = Side.SERVER;
        }
        for (final IChatListener listener : this.chatListeners) {
            chat = (s.isClient() ? listener.clientChat(handler, chat) : listener.serverChat(handler, chat));
        }
        return chat;
    }
    
    public void handleTinyPacket(final ez handler, final dr mapData) {
        final NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler((int)mapData.a);
        if (nmh == null) {
            FMLLog.info("Received a tiny packet for network id %d that is not recognised here", mapData.a);
            return;
        }
        if (nmh.hasTinyPacketHandler()) {
            nmh.getTinyPacketHandler().handle(handler, mapData);
        }
        else {
            FMLLog.info("Received a tiny packet for a network mod that does not accept tiny packets %s", nmh.getContainer().getModId());
        }
    }
    
    static {
        INSTANCE = new NetworkRegistry();
    }
}
