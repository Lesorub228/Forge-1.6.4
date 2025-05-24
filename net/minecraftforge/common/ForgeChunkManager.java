// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.common;

import java.util.LinkedHashSet;
import com.google.common.collect.Sets;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.LinkedHashMultimap;
import net.minecraftforge.event.Event;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.server.MinecraftServer;
import java.util.Iterator;
import com.google.common.collect.ImmutableListMultimap;
import java.util.List;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import com.google.common.collect.Maps;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ArrayListMultimap;
import java.io.IOException;
import java.util.Set;
import java.io.File;
import com.google.common.cache.Cache;
import java.util.UUID;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Multimap;
import java.util.Map;

public class ForgeChunkManager
{
    private static int defaultMaxCount;
    private static int defaultMaxChunks;
    private static boolean overridesEnabled;
    private static Map<abw, Multimap<String, Ticket>> tickets;
    private static Map<String, Integer> ticketConstraints;
    private static Map<String, Integer> chunkConstraints;
    private static SetMultimap<String, Ticket> playerTickets;
    private static Map<String, LoadingCallback> callbacks;
    private static Map<abw, ImmutableSetMultimap<abp, Ticket>> forcedChunks;
    private static BiMap<UUID, Ticket> pendingEntities;
    private static Map<abw, Cache<Long, adr>> dormantChunkCache;
    private static File cfgFile;
    private static Configuration config;
    private static int playerTicketLength;
    private static int dormantChunkCacheSize;
    private static Set<String> warnedMods;
    
    public static boolean savedWorldHasForcedChunkTickets(final File chunkDir) {
        final File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");
        if (chunkLoaderData.exists() && chunkLoaderData.isFile()) {
            try {
                final by forcedChunkData = ci.a(chunkLoaderData);
                return forcedChunkData.m("TicketList").c() > 0;
            }
            catch (final IOException ex) {}
        }
        return false;
    }
    
    static void loadWorld(final abw world) {
        final ArrayListMultimap<String, Ticket> newTickets = (ArrayListMultimap<String, Ticket>)ArrayListMultimap.create();
        ForgeChunkManager.tickets.put(world, (Multimap<String, Ticket>)newTickets);
        ForgeChunkManager.forcedChunks.put(world, (ImmutableSetMultimap<abp, Ticket>)ImmutableSetMultimap.of());
        if (!(world instanceof js)) {
            return;
        }
        ForgeChunkManager.dormantChunkCache.put(world, (Cache<Long, adr>)CacheBuilder.newBuilder().maximumSize((long)ForgeChunkManager.dormantChunkCacheSize).build());
        final js worldServer = (js)world;
        final File chunkDir = worldServer.getChunkSaveLocation();
        final File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");
        if (chunkLoaderData.exists() && chunkLoaderData.isFile()) {
            final ArrayListMultimap<String, Ticket> loadedTickets = (ArrayListMultimap<String, Ticket>)ArrayListMultimap.create();
            final Map<String, ListMultimap<String, Ticket>> playerLoadedTickets = Maps.newHashMap();
            by forcedChunkData;
            try {
                forcedChunkData = ci.a(chunkLoaderData);
            }
            catch (final IOException e) {
                FMLLog.log(Level.WARNING, e, "Unable to read forced chunk data at %s - it will be ignored", chunkLoaderData.getAbsolutePath());
                return;
            }
            final cg ticketList = forcedChunkData.m("TicketList");
            for (int i = 0; i < ticketList.c(); ++i) {
                final by ticketHolder = (by)ticketList.b(i);
                String modId = ticketHolder.i("Owner");
                final boolean isPlayer = "Forge".equals(modId);
                if (!isPlayer && !Loader.isModLoaded(modId)) {
                    FMLLog.warning("Found chunkloading data for mod %s which is currently not available or active - it will be removed from the world save", modId);
                }
                else if (!isPlayer && !ForgeChunkManager.callbacks.containsKey(modId)) {
                    FMLLog.warning("The mod %s has registered persistent chunkloading data but doesn't seem to want to be called back with it - it will be removed from the world save", modId);
                }
                else {
                    final cg tickets = ticketHolder.m("Tickets");
                    for (int j = 0; j < tickets.c(); ++j) {
                        final by ticket = (by)tickets.b(j);
                        modId = (ticket.b("ModId") ? ticket.i("ModId") : modId);
                        final Type type = Type.values()[ticket.c("Type")];
                        final byte ticketChunkDepth = ticket.c("ChunkListDepth");
                        final Ticket tick = new Ticket(modId, type, world);
                        if (ticket.b("ModData")) {
                            tick.modData = ticket.l("ModData");
                        }
                        if (ticket.b("Player")) {
                            tick.player = ticket.i("Player");
                            if (!playerLoadedTickets.containsKey(tick.modId)) {
                                playerLoadedTickets.put(modId, (ListMultimap<String, Ticket>)ArrayListMultimap.create());
                            }
                            playerLoadedTickets.get(tick.modId).put((Object)tick.player, (Object)tick);
                        }
                        else {
                            loadedTickets.put((Object)modId, (Object)tick);
                        }
                        if (type == Type.ENTITY) {
                            tick.entityChunkX = ticket.e("chunkX");
                            tick.entityChunkZ = ticket.e("chunkZ");
                            final UUID uuid = new UUID(ticket.f("PersistentIDMSB"), ticket.f("PersistentIDLSB"));
                            ForgeChunkManager.pendingEntities.put((Object)uuid, (Object)tick);
                        }
                    }
                }
            }
            for (final Ticket tick2 : ImmutableSet.copyOf((Collection)ForgeChunkManager.pendingEntities.values())) {
                if (tick2.ticketType == Type.ENTITY && tick2.entity == null) {
                    world.e(tick2.entityChunkX, tick2.entityChunkZ);
                }
            }
            for (final Ticket tick2 : ImmutableSet.copyOf((Collection)ForgeChunkManager.pendingEntities.values())) {
                if (tick2.ticketType == Type.ENTITY && tick2.entity == null) {
                    FMLLog.warning("Failed to load persistent chunkloading entity %s from store.", ForgeChunkManager.pendingEntities.inverse().get((Object)tick2));
                    loadedTickets.remove((Object)tick2.modId, (Object)tick2);
                }
            }
            ForgeChunkManager.pendingEntities.clear();
            for (final String modId2 : loadedTickets.keySet()) {
                final LoadingCallback loadingCallback = ForgeChunkManager.callbacks.get(modId2);
                if (loadingCallback == null) {
                    continue;
                }
                final int maxTicketLength = getMaxTicketLengthFor(modId2);
                List<Ticket> tickets2 = loadedTickets.get((Object)modId2);
                if (loadingCallback instanceof OrderedLoadingCallback) {
                    final OrderedLoadingCallback orderedLoadingCallback = (OrderedLoadingCallback)loadingCallback;
                    tickets2 = orderedLoadingCallback.ticketsLoaded((List<Ticket>)ImmutableList.copyOf((Collection)tickets2), world, maxTicketLength);
                }
                if (tickets2.size() > maxTicketLength) {
                    FMLLog.warning("The mod %s has too many open chunkloading tickets %d. Excess will be dropped", modId2, tickets2.size());
                    tickets2.subList(maxTicketLength, tickets2.size()).clear();
                }
                ForgeChunkManager.tickets.get(world).putAll((Object)modId2, (Iterable)tickets2);
                loadingCallback.ticketsLoaded((List<Ticket>)ImmutableList.copyOf((Collection)tickets2), world);
            }
            for (final String modId2 : playerLoadedTickets.keySet()) {
                final LoadingCallback loadingCallback = ForgeChunkManager.callbacks.get(modId2);
                if (loadingCallback == null) {
                    continue;
                }
                ListMultimap<String, Ticket> tickets3 = playerLoadedTickets.get(modId2);
                if (loadingCallback instanceof PlayerOrderedLoadingCallback) {
                    final PlayerOrderedLoadingCallback orderedLoadingCallback2 = (PlayerOrderedLoadingCallback)loadingCallback;
                    tickets3 = orderedLoadingCallback2.playerTicketsLoaded((ListMultimap<String, Ticket>)ImmutableListMultimap.copyOf((Multimap)tickets3), world);
                    ForgeChunkManager.playerTickets.putAll((Multimap)tickets3);
                }
                ForgeChunkManager.tickets.get(world).putAll((Object)"Forge", (Iterable)tickets3.values());
                loadingCallback.ticketsLoaded((List<Ticket>)ImmutableList.copyOf(tickets3.values()), world);
            }
        }
    }
    
    static void unloadWorld(final abw world) {
        if (!(world instanceof js)) {
            return;
        }
        ForgeChunkManager.forcedChunks.remove(world);
        ForgeChunkManager.dormantChunkCache.remove(world);
        if (!MinecraftServer.F().o()) {
            ForgeChunkManager.playerTickets.clear();
            ForgeChunkManager.tickets.clear();
        }
    }
    
    public static void setForcedChunkLoadingCallback(final Object mod, final LoadingCallback callback) {
        final ModContainer container = getContainer(mod);
        if (container == null) {
            FMLLog.warning("Unable to register a callback for an unknown mod %s (%s : %x)", mod, mod.getClass().getName(), System.identityHashCode(mod));
            return;
        }
        ForgeChunkManager.callbacks.put(container.getModId(), callback);
    }
    
    public static int ticketCountAvailableFor(final Object mod, final abw world) {
        final ModContainer container = getContainer(mod);
        if (container != null) {
            final String modId = container.getModId();
            final int allowedCount = getMaxTicketLengthFor(modId);
            return allowedCount - ForgeChunkManager.tickets.get(world).get((Object)modId).size();
        }
        return 0;
    }
    
    private static ModContainer getContainer(final Object mod) {
        final ModContainer container = (ModContainer)Loader.instance().getModObjectList().inverse().get(mod);
        return container;
    }
    
    public static int getMaxTicketLengthFor(final String modId) {
        final int allowedCount = (ForgeChunkManager.ticketConstraints.containsKey(modId) && ForgeChunkManager.overridesEnabled) ? ForgeChunkManager.ticketConstraints.get(modId) : ForgeChunkManager.defaultMaxCount;
        return allowedCount;
    }
    
    public static int getMaxChunkDepthFor(final String modId) {
        final int allowedCount = (ForgeChunkManager.chunkConstraints.containsKey(modId) && ForgeChunkManager.overridesEnabled) ? ForgeChunkManager.chunkConstraints.get(modId) : ForgeChunkManager.defaultMaxChunks;
        return allowedCount;
    }
    
    public static int ticketCountAvailableFor(final String username) {
        return ForgeChunkManager.playerTicketLength - ForgeChunkManager.playerTickets.get((Object)username).size();
    }
    
    public static Ticket requestPlayerTicket(final Object mod, final String player, final abw world, final Type type) {
        final ModContainer mc = getContainer(mod);
        if (mc == null) {
            FMLLog.log(Level.SEVERE, "Failed to locate the container for mod instance %s (%s : %x)", mod, mod.getClass().getName(), System.identityHashCode(mod));
            return null;
        }
        if (ForgeChunkManager.playerTickets.get((Object)player).size() > ForgeChunkManager.playerTicketLength) {
            FMLLog.warning("Unable to assign further chunkloading tickets to player %s (on behalf of mod %s)", player, mc.getModId());
            return null;
        }
        final Ticket ticket = new Ticket(mc.getModId(), type, world, player);
        ForgeChunkManager.playerTickets.put((Object)player, (Object)ticket);
        ForgeChunkManager.tickets.get(world).put((Object)"Forge", (Object)ticket);
        return ticket;
    }
    
    public static Ticket requestTicket(final Object mod, final abw world, final Type type) {
        final ModContainer container = getContainer(mod);
        if (container == null) {
            FMLLog.log(Level.SEVERE, "Failed to locate the container for mod instance %s (%s : %x)", mod, mod.getClass().getName(), System.identityHashCode(mod));
            return null;
        }
        final String modId = container.getModId();
        if (!ForgeChunkManager.callbacks.containsKey(modId)) {
            FMLLog.severe("The mod %s has attempted to request a ticket without a listener in place", modId);
            throw new RuntimeException("Invalid ticket request");
        }
        final int allowedCount = ForgeChunkManager.ticketConstraints.containsKey(modId) ? ForgeChunkManager.ticketConstraints.get(modId) : ForgeChunkManager.defaultMaxCount;
        if (ForgeChunkManager.tickets.get(world).get((Object)modId).size() >= allowedCount) {
            if (!ForgeChunkManager.warnedMods.contains(modId)) {
                FMLLog.info("The mod %s has attempted to allocate a chunkloading ticket beyond it's currently allocated maximum : %d", modId, allowedCount);
                ForgeChunkManager.warnedMods.add(modId);
            }
            return null;
        }
        final Ticket ticket = new Ticket(modId, type, world);
        ForgeChunkManager.tickets.get(world).put((Object)modId, (Object)ticket);
        return ticket;
    }
    
    public static void releaseTicket(final Ticket ticket) {
        if (ticket == null) {
            return;
        }
        Label_0056: {
            if (ticket.isPlayerTicket()) {
                if (ForgeChunkManager.playerTickets.containsValue((Object)ticket)) {
                    break Label_0056;
                }
            }
            else if (ForgeChunkManager.tickets.get(ticket.world).containsEntry((Object)ticket.modId, (Object)ticket)) {
                break Label_0056;
            }
            return;
        }
        if (ticket.requestedChunks != null) {
            for (final abp chunk : ImmutableSet.copyOf((Collection)ticket.requestedChunks)) {
                unforceChunk(ticket, chunk);
            }
        }
        if (ticket.isPlayerTicket()) {
            ForgeChunkManager.playerTickets.remove((Object)ticket.player, (Object)ticket);
            ForgeChunkManager.tickets.get(ticket.world).remove((Object)"Forge", (Object)ticket);
        }
        else {
            ForgeChunkManager.tickets.get(ticket.world).remove((Object)ticket.modId, (Object)ticket);
        }
    }
    
    public static void forceChunk(final Ticket ticket, final abp chunk) {
        if (ticket == null || chunk == null) {
            return;
        }
        if (ticket.ticketType == Type.ENTITY && ticket.entity == null) {
            throw new RuntimeException("Attempted to use an entity ticket to force a chunk, without an entity");
        }
        Label_0105: {
            if (ticket.isPlayerTicket()) {
                if (ForgeChunkManager.playerTickets.containsValue((Object)ticket)) {
                    break Label_0105;
                }
            }
            else if (ForgeChunkManager.tickets.get(ticket.world).containsEntry((Object)ticket.modId, (Object)ticket)) {
                break Label_0105;
            }
            FMLLog.severe("The mod %s attempted to force load a chunk with an invalid ticket. This is not permitted.", ticket.modId);
            return;
        }
        ticket.requestedChunks.add(chunk);
        MinecraftForge.EVENT_BUS.post(new ForceChunkEvent(ticket, chunk));
        final ImmutableSetMultimap<abp, Ticket> newMap = (ImmutableSetMultimap<abp, Ticket>)ImmutableSetMultimap.builder().putAll((Multimap)ForgeChunkManager.forcedChunks.get(ticket.world)).put((Object)chunk, (Object)ticket).build();
        ForgeChunkManager.forcedChunks.put(ticket.world, newMap);
        if (ticket.maxDepth > 0 && ticket.requestedChunks.size() > ticket.maxDepth) {
            final abp removed = (abp)ticket.requestedChunks.iterator().next();
            unforceChunk(ticket, removed);
        }
    }
    
    public static void reorderChunk(final Ticket ticket, final abp chunk) {
        if (ticket == null || chunk == null || !ticket.requestedChunks.contains(chunk)) {
            return;
        }
        ticket.requestedChunks.remove(chunk);
        ticket.requestedChunks.add(chunk);
    }
    
    public static void unforceChunk(final Ticket ticket, final abp chunk) {
        if (ticket == null || chunk == null) {
            return;
        }
        ticket.requestedChunks.remove(chunk);
        MinecraftForge.EVENT_BUS.post(new UnforceChunkEvent(ticket, chunk));
        final LinkedHashMultimap<abp, Ticket> copy = (LinkedHashMultimap<abp, Ticket>)LinkedHashMultimap.create((Multimap)ForgeChunkManager.forcedChunks.get(ticket.world));
        copy.remove((Object)chunk, (Object)ticket);
        final ImmutableSetMultimap<abp, Ticket> newMap = (ImmutableSetMultimap<abp, Ticket>)ImmutableSetMultimap.copyOf((Multimap)copy);
        ForgeChunkManager.forcedChunks.put(ticket.world, newMap);
    }
    
    static void loadConfiguration() {
        for (final String mod : ForgeChunkManager.config.getCategoryNames()) {
            if (!mod.equals("Forge")) {
                if (mod.equals("defaults")) {
                    continue;
                }
                final Property modTC = ForgeChunkManager.config.get(mod, "maximumTicketCount", 200);
                final Property modCPT = ForgeChunkManager.config.get(mod, "maximumChunksPerTicket", 25);
                ForgeChunkManager.ticketConstraints.put(mod, modTC.getInt(200));
                ForgeChunkManager.chunkConstraints.put(mod, modCPT.getInt(25));
            }
        }
        if (ForgeChunkManager.config.hasChanged()) {
            ForgeChunkManager.config.save();
        }
    }
    
    public static ImmutableSetMultimap<abp, Ticket> getPersistentChunksFor(final abw world) {
        return (ImmutableSetMultimap<abp, Ticket>)(ForgeChunkManager.forcedChunks.containsKey(world) ? ForgeChunkManager.forcedChunks.get(world) : ImmutableSetMultimap.of());
    }
    
    static void saveWorld(final abw world) {
        if (!(world instanceof js)) {
            return;
        }
        final js worldServer = (js)world;
        final File chunkDir = worldServer.getChunkSaveLocation();
        final File chunkLoaderData = new File(chunkDir, "forcedchunks.dat");
        final by forcedChunkData = new by();
        final cg ticketList = new cg();
        forcedChunkData.a("TicketList", (cl)ticketList);
        final Multimap<String, Ticket> ticketSet = ForgeChunkManager.tickets.get(worldServer);
        for (final String modId : ticketSet.keySet()) {
            final by ticketHolder = new by();
            ticketList.a((cl)ticketHolder);
            ticketHolder.a("Owner", modId);
            final cg tickets = new cg();
            ticketHolder.a("Tickets", (cl)tickets);
            for (final Ticket tick : ticketSet.get((Object)modId)) {
                final by ticket = new by();
                ticket.a("Type", (byte)tick.ticketType.ordinal());
                ticket.a("ChunkListDepth", (byte)tick.maxDepth);
                if (tick.isPlayerTicket()) {
                    ticket.a("ModId", tick.modId);
                    ticket.a("Player", tick.player);
                }
                if (tick.modData != null) {
                    ticket.a("ModData", tick.modData);
                }
                if (tick.ticketType == Type.ENTITY && tick.entity != null && tick.entity.d(new by())) {
                    ticket.a("chunkX", ls.c((double)tick.entity.aj));
                    ticket.a("chunkZ", ls.c((double)tick.entity.al));
                    ticket.a("PersistentIDMSB", tick.entity.getPersistentID().getMostSignificantBits());
                    ticket.a("PersistentIDLSB", tick.entity.getPersistentID().getLeastSignificantBits());
                    tickets.a((cl)ticket);
                }
                else {
                    if (tick.ticketType == Type.ENTITY) {
                        continue;
                    }
                    tickets.a((cl)ticket);
                }
            }
        }
        try {
            ci.b(forcedChunkData, chunkLoaderData);
        }
        catch (final IOException e) {
            FMLLog.log(Level.WARNING, e, "Unable to write forced chunk data to %s - chunkloading won't work", chunkLoaderData.getAbsolutePath());
        }
    }
    
    static void loadEntity(final nn entity) {
        final UUID id = entity.getPersistentID();
        final Ticket tick = (Ticket)ForgeChunkManager.pendingEntities.get((Object)id);
        if (tick != null) {
            tick.bindEntity(entity);
            ForgeChunkManager.pendingEntities.remove((Object)id);
        }
    }
    
    public static void putDormantChunk(final long coords, final adr chunk) {
        final Cache<Long, adr> cache = ForgeChunkManager.dormantChunkCache.get(chunk.e);
        if (cache != null) {
            cache.put((Object)coords, (Object)chunk);
        }
    }
    
    public static adr fetchDormantChunk(final long coords, final abw world) {
        final Cache<Long, adr> cache = ForgeChunkManager.dormantChunkCache.get(world);
        if (cache == null) {
            return null;
        }
        final adr chunk = (adr)cache.getIfPresent((Object)coords);
        if (chunk != null) {
            for (final List<nn> eList : chunk.j) {
                for (final nn e : eList) {
                    e.resetEntityId();
                }
            }
        }
        return chunk;
    }
    
    static void captureConfig(final File configDir) {
        ForgeChunkManager.cfgFile = new File(configDir, "forgeChunkLoading.cfg");
        ForgeChunkManager.config = new Configuration(ForgeChunkManager.cfgFile, true);
        try {
            ForgeChunkManager.config.load();
        }
        catch (final Exception e) {
            final File dest = new File(ForgeChunkManager.cfgFile.getParentFile(), "forgeChunkLoading.cfg.bak");
            if (dest.exists()) {
                dest.delete();
            }
            ForgeChunkManager.cfgFile.renameTo(dest);
            FMLLog.log(Level.SEVERE, e, "A critical error occured reading the forgeChunkLoading.cfg file, defaults will be used - the invalid file is backed up at forgeChunkLoading.cfg.bak", new Object[0]);
        }
        ForgeChunkManager.config.addCustomCategoryComment("defaults", "Default configuration for forge chunk loading control");
        final Property maxTicketCount = ForgeChunkManager.config.get("defaults", "maximumTicketCount", 200);
        maxTicketCount.comment = "The default maximum ticket count for a mod which does not have an override\nin this file. This is the number of chunk loading requests a mod is allowed to make.";
        ForgeChunkManager.defaultMaxCount = maxTicketCount.getInt(200);
        final Property maxChunks = ForgeChunkManager.config.get("defaults", "maximumChunksPerTicket", 25);
        maxChunks.comment = "The default maximum number of chunks a mod can force, per ticket, \nfor a mod without an override. This is the maximum number of chunks a single ticket can force.";
        ForgeChunkManager.defaultMaxChunks = maxChunks.getInt(25);
        final Property playerTicketCount = ForgeChunkManager.config.get("defaults", "playerTicketCount", 500);
        playerTicketCount.comment = "The number of tickets a player can be assigned instead of a mod. This is shared across all mods and it is up to the mods to use it.";
        ForgeChunkManager.playerTicketLength = playerTicketCount.getInt(500);
        final Property dormantChunkCacheSizeProperty = ForgeChunkManager.config.get("defaults", "dormantChunkCacheSize", 0);
        dormantChunkCacheSizeProperty.comment = "Unloaded chunks can first be kept in a dormant cache for quicker\nloading times. Specify the size (in chunks) of that cache here";
        ForgeChunkManager.dormantChunkCacheSize = dormantChunkCacheSizeProperty.getInt(0);
        FMLLog.info("Configured a dormant chunk cache size of %d", dormantChunkCacheSizeProperty.getInt(0));
        final Property modOverridesEnabled = ForgeChunkManager.config.get("defaults", "enabled", true);
        modOverridesEnabled.comment = "Are mod overrides enabled?";
        ForgeChunkManager.overridesEnabled = modOverridesEnabled.getBoolean(true);
        ForgeChunkManager.config.addCustomCategoryComment("Forge", "Sample mod specific control section.\nCopy this section and rename the with the modid for the mod you wish to override.\nA value of zero in either entry effectively disables any chunkloading capabilities\nfor that mod");
        Property sampleTC = ForgeChunkManager.config.get("Forge", "maximumTicketCount", 200);
        sampleTC.comment = "Maximum ticket count for the mod. Zero disables chunkloading capabilities.";
        sampleTC = ForgeChunkManager.config.get("Forge", "maximumChunksPerTicket", 25);
        sampleTC.comment = "Maximum chunks per ticket for the mod.";
        for (final String mod : ForgeChunkManager.config.getCategoryNames()) {
            if (!mod.equals("Forge")) {
                if (mod.equals("defaults")) {
                    continue;
                }
                final Property modTC = ForgeChunkManager.config.get(mod, "maximumTicketCount", 200);
                final Property modCPT = ForgeChunkManager.config.get(mod, "maximumChunksPerTicket", 25);
            }
        }
    }
    
    public static ConfigCategory getConfigFor(final Object mod) {
        final ModContainer container = getContainer(mod);
        if (container != null) {
            return ForgeChunkManager.config.getCategory(container.getModId());
        }
        return null;
    }
    
    public static void addConfigProperty(final Object mod, final String propertyName, final String value, final Property.Type type) {
        final ModContainer container = getContainer(mod);
        if (container != null) {
            final ConfigCategory cat = ForgeChunkManager.config.getCategory(container.getModId());
            cat.put(propertyName, new Property(propertyName, value, type));
        }
    }
    
    static {
        ForgeChunkManager.tickets = new MapMaker().weakKeys().makeMap();
        ForgeChunkManager.ticketConstraints = Maps.newHashMap();
        ForgeChunkManager.chunkConstraints = Maps.newHashMap();
        ForgeChunkManager.playerTickets = (SetMultimap<String, Ticket>)HashMultimap.create();
        ForgeChunkManager.callbacks = Maps.newHashMap();
        ForgeChunkManager.forcedChunks = new MapMaker().weakKeys().makeMap();
        ForgeChunkManager.pendingEntities = (BiMap<UUID, Ticket>)HashBiMap.create();
        ForgeChunkManager.dormantChunkCache = new MapMaker().weakKeys().makeMap();
        ForgeChunkManager.warnedMods = Sets.newHashSet();
    }
    
    public enum Type
    {
        NORMAL, 
        ENTITY;
    }
    
    public static class Ticket
    {
        private String modId;
        private Type ticketType;
        private LinkedHashSet<abp> requestedChunks;
        private by modData;
        public final abw world;
        private int maxDepth;
        private String entityClazz;
        private int entityChunkX;
        private int entityChunkZ;
        private nn entity;
        private String player;
        
        Ticket(final String modId, final Type type, final abw world) {
            this.modId = modId;
            this.ticketType = type;
            this.world = world;
            this.maxDepth = ForgeChunkManager.getMaxChunkDepthFor(modId);
            this.requestedChunks = Sets.newLinkedHashSet();
        }
        
        Ticket(final String modId, final Type type, final abw world, final String player) {
            this(modId, type, world);
            if (player != null) {
                this.player = player;
                return;
            }
            FMLLog.log(Level.SEVERE, "Attempt to create a player ticket without a valid player", new Object[0]);
            throw new RuntimeException();
        }
        
        public void setChunkListDepth(final int depth) {
            if (depth > ForgeChunkManager.getMaxChunkDepthFor(this.modId) || (depth <= 0 && ForgeChunkManager.getMaxChunkDepthFor(this.modId) > 0)) {
                FMLLog.warning("The mod %s tried to modify the chunk ticket depth to: %d, its allowed maximum is: %d", this.modId, depth, ForgeChunkManager.getMaxChunkDepthFor(this.modId));
            }
            else {
                this.maxDepth = depth;
            }
        }
        
        public int getChunkListDepth() {
            return this.maxDepth;
        }
        
        public int getMaxChunkListDepth() {
            return ForgeChunkManager.getMaxChunkDepthFor(this.modId);
        }
        
        public void bindEntity(final nn entity) {
            if (this.ticketType != Type.ENTITY) {
                throw new RuntimeException("Cannot bind an entity to a non-entity ticket");
            }
            this.entity = entity;
        }
        
        public by getModData() {
            if (this.modData == null) {
                this.modData = new by();
            }
            return this.modData;
        }
        
        public nn getEntity() {
            return this.entity;
        }
        
        public boolean isPlayerTicket() {
            return this.player != null;
        }
        
        public String getPlayerName() {
            return this.player;
        }
        
        public String getModId() {
            return this.modId;
        }
        
        public Type getType() {
            return this.ticketType;
        }
        
        public ImmutableSet getChunkList() {
            return ImmutableSet.copyOf((Collection)this.requestedChunks);
        }
    }
    
    public static class ForceChunkEvent extends Event
    {
        public final Ticket ticket;
        public final abp location;
        
        public ForceChunkEvent(final Ticket ticket, final abp location) {
            this.ticket = ticket;
            this.location = location;
        }
    }
    
    public static class UnforceChunkEvent extends Event
    {
        public final Ticket ticket;
        public final abp location;
        
        public UnforceChunkEvent(final Ticket ticket, final abp location) {
            this.ticket = ticket;
            this.location = location;
        }
    }
    
    public interface PlayerOrderedLoadingCallback extends LoadingCallback
    {
        ListMultimap<String, Ticket> playerTicketsLoaded(final ListMultimap<String, Ticket> p0, final abw p1);
    }
    
    public interface LoadingCallback
    {
        void ticketsLoaded(final List<Ticket> p0, final abw p1);
    }
    
    public interface OrderedLoadingCallback extends LoadingCallback
    {
        List<Ticket> ticketsLoaded(final List<Ticket> p0, final abw p1, final int p2);
    }
}
