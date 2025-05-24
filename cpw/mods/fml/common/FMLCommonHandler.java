// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import com.google.common.base.Joiner;
import cpw.mods.fml.common.registry.ItemData;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import java.util.concurrent.Callable;
import cpw.mods.fml.server.FMLServerHandler;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.server.MinecraftServer;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Collection;
import com.google.common.base.Objects;
import java.util.EnumSet;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import java.util.logging.Level;
import java.util.Map;
import com.google.common.collect.Sets;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Lists;
import java.util.Set;
import java.util.List;

public class FMLCommonHandler
{
    private static final FMLCommonHandler INSTANCE;
    private IFMLSidedHandler sidedDelegate;
    private List<IScheduledTickHandler> scheduledClientTicks;
    private List<IScheduledTickHandler> scheduledServerTicks;
    private Class<?> forge;
    private boolean noForge;
    private List<String> brandings;
    private List<ICrashCallable> crashCallables;
    private Set<alq> handlerSet;
    
    public FMLCommonHandler() {
        this.scheduledClientTicks = Lists.newArrayList();
        this.scheduledServerTicks = Lists.newArrayList();
        this.crashCallables = Lists.newArrayList((Object[])new ICrashCallable[] { Loader.instance().getCallableCrashInformation() });
        this.handlerSet = Sets.newSetFromMap((Map)new MapMaker().weakKeys().makeMap());
    }
    
    public void beginLoading(final IFMLSidedHandler handler) {
        this.sidedDelegate = handler;
        FMLLog.log("MinecraftForge", Level.INFO, "Attempting early MinecraftForge initialization", new Object[0]);
        this.callForgeMethod("initialize");
        this.callForgeMethod("registerCrashCallable");
        FMLLog.log("MinecraftForge", Level.INFO, "Completed early MinecraftForge initialization", new Object[0]);
    }
    
    public void rescheduleTicks(final Side side) {
        TickRegistry.updateTickQueue(side.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks, side);
    }
    
    public void tickStart(final EnumSet<TickType> ticks, final Side side, final Object... data) {
        final List<IScheduledTickHandler> scheduledTicks = side.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks;
        if (scheduledTicks.size() == 0) {
            return;
        }
        for (final IScheduledTickHandler ticker : scheduledTicks) {
            final EnumSet<TickType> ticksToRun = EnumSet.copyOf((EnumSet<TickType>)Objects.firstNonNull((Object)ticker.ticks(), (Object)EnumSet.noneOf(TickType.class)));
            ticksToRun.retainAll(ticks);
            if (!ticksToRun.isEmpty()) {
                ticker.tickStart(ticksToRun, data);
            }
        }
    }
    
    public void tickEnd(final EnumSet<TickType> ticks, final Side side, final Object... data) {
        final List<IScheduledTickHandler> scheduledTicks = side.isClient() ? this.scheduledClientTicks : this.scheduledServerTicks;
        if (scheduledTicks.size() == 0) {
            return;
        }
        for (final IScheduledTickHandler ticker : scheduledTicks) {
            final EnumSet<TickType> ticksToRun = EnumSet.copyOf((EnumSet<TickType>)Objects.firstNonNull((Object)ticker.ticks(), (Object)EnumSet.noneOf(TickType.class)));
            ticksToRun.retainAll(ticks);
            if (!ticksToRun.isEmpty()) {
                ticker.tickEnd(ticksToRun, data);
            }
        }
    }
    
    public static FMLCommonHandler instance() {
        return FMLCommonHandler.INSTANCE;
    }
    
    public ModContainer findContainerFor(final Object mod) {
        return (ModContainer)Loader.instance().getReversedModObjectList().get(mod);
    }
    
    public Logger getFMLLogger() {
        return FMLLog.getLogger();
    }
    
    public Side getSide() {
        return this.sidedDelegate.getSide();
    }
    
    public Side getEffectiveSide() {
        final Thread thr = Thread.currentThread();
        if (thr instanceof hi || thr instanceof iy) {
            return Side.SERVER;
        }
        return Side.CLIENT;
    }
    
    public void raiseException(final Throwable exception, final String message, final boolean stopGame) {
        FMLLog.log(Level.SEVERE, exception, "Something raised an exception. The message was '%s'. 'stopGame' is %b", message, stopGame);
        if (stopGame) {
            this.getSidedDelegate().haltGame(message, exception);
        }
    }
    
    private Class<?> findMinecraftForge() {
        if (this.forge == null && !this.noForge) {
            try {
                this.forge = Class.forName("net.minecraftforge.common.MinecraftForge");
            }
            catch (final Exception ex) {
                this.noForge = true;
            }
        }
        return this.forge;
    }
    
    private Object callForgeMethod(final String method) {
        if (this.noForge) {
            return null;
        }
        try {
            return this.findMinecraftForge().getMethod(method, (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public void computeBranding() {
        if (this.brandings == null) {
            final ImmutableList.Builder brd = ImmutableList.builder();
            brd.add((Object)Loader.instance().getMCVersionString());
            brd.add((Object)Loader.instance().getMCPVersionString());
            brd.add((Object)("FML v" + Loader.instance().getFMLVersionString()));
            final String forgeBranding = (String)this.callForgeMethod("getBrandingVersion");
            if (!Strings.isNullOrEmpty(forgeBranding)) {
                brd.add((Object)forgeBranding);
            }
            if (this.sidedDelegate != null) {
                brd.addAll((Iterable)this.sidedDelegate.getAdditionalBrandingInformation());
            }
            if (Loader.instance().getFMLBrandingProperties().containsKey("fmlbranding")) {
                brd.add((Object)Loader.instance().getFMLBrandingProperties().get("fmlbranding"));
            }
            final int tModCount = Loader.instance().getModList().size();
            final int aModCount = Loader.instance().getActiveModList().size();
            brd.add((Object)String.format("%d mod%s loaded, %d mod%s active", tModCount, (tModCount != 1) ? "s" : "", aModCount, (aModCount != 1) ? "s" : ""));
            this.brandings = (List<String>)brd.build();
        }
    }
    
    public List<String> getBrandings() {
        if (this.brandings == null) {
            this.computeBranding();
        }
        return (List<String>)ImmutableList.copyOf((Collection)this.brandings);
    }
    
    public IFMLSidedHandler getSidedDelegate() {
        return this.sidedDelegate;
    }
    
    public void onPostServerTick() {
        this.tickEnd(EnumSet.of(TickType.SERVER), Side.SERVER, new Object[0]);
    }
    
    public void onPostWorldTick(final Object world) {
        this.tickEnd(EnumSet.of(TickType.WORLD), Side.SERVER, world);
    }
    
    public void onPreServerTick() {
        this.tickStart(EnumSet.of(TickType.SERVER), Side.SERVER, new Object[0]);
    }
    
    public void onPreWorldTick(final Object world) {
        this.tickStart(EnumSet.of(TickType.WORLD), Side.SERVER, world);
    }
    
    public void onWorldLoadTick(final abw[] worlds) {
        this.rescheduleTicks(Side.SERVER);
        for (final abw w : worlds) {
            this.tickStart(EnumSet.of(TickType.WORLDLOAD), Side.SERVER, w);
        }
    }
    
    public boolean handleServerAboutToStart(final MinecraftServer server) {
        return Loader.instance().serverAboutToStart(server);
    }
    
    public boolean handleServerStarting(final MinecraftServer server) {
        return Loader.instance().serverStarting(server);
    }
    
    public void handleServerStarted() {
        Loader.instance().serverStarted();
    }
    
    public void handleServerStopping() {
        Loader.instance().serverStopping();
    }
    
    public MinecraftServer getMinecraftServerInstance() {
        return this.sidedDelegate.getServer();
    }
    
    public void showGuiScreen(final Object clientGuiElement) {
        this.sidedDelegate.showGuiScreen(clientGuiElement);
    }
    
    public nn spawnEntityIntoClientWorld(final EntityRegistry.EntityRegistration registration, final EntitySpawnPacket entitySpawnPacket) {
        return this.sidedDelegate.spawnEntityIntoClientWorld(registration, entitySpawnPacket);
    }
    
    public void adjustEntityLocationOnClient(final EntitySpawnAdjustmentPacket entitySpawnAdjustmentPacket) {
        this.sidedDelegate.adjustEntityLocationOnClient(entitySpawnAdjustmentPacket);
    }
    
    public void onServerStart(final is dedicatedServer) {
        FMLServerHandler.instance();
        this.sidedDelegate.beginServerLoading((MinecraftServer)dedicatedServer);
    }
    
    public void onServerStarted() {
        this.sidedDelegate.finishServerLoading();
    }
    
    public void onPreClientTick() {
        this.tickStart(EnumSet.of(TickType.CLIENT), Side.CLIENT, new Object[0]);
    }
    
    public void onPostClientTick() {
        this.tickEnd(EnumSet.of(TickType.CLIENT), Side.CLIENT, new Object[0]);
    }
    
    public void onRenderTickStart(final float timer) {
        this.tickStart(EnumSet.of(TickType.RENDER), Side.CLIENT, timer);
    }
    
    public void onRenderTickEnd(final float timer) {
        this.tickEnd(EnumSet.of(TickType.RENDER), Side.CLIENT, timer);
    }
    
    public void onPlayerPreTick(final uf player) {
        final Side side = (player instanceof jv) ? Side.SERVER : Side.CLIENT;
        this.tickStart(EnumSet.of(TickType.PLAYER), side, player);
    }
    
    public void onPlayerPostTick(final uf player) {
        final Side side = (player instanceof jv) ? Side.SERVER : Side.CLIENT;
        this.tickEnd(EnumSet.of(TickType.PLAYER), side, player);
    }
    
    public void registerCrashCallable(final ICrashCallable callable) {
        this.crashCallables.add(callable);
    }
    
    public void enhanceCrashReport(final b crashReport, final m category) {
        for (final ICrashCallable call : this.crashCallables) {
            category.a(call.getLabel(), (Callable)call);
        }
    }
    
    public void handleTinyPacket(final ez handler, final dr mapData) {
        this.sidedDelegate.handleTinyPacket(handler, mapData);
    }
    
    public void handleWorldDataSave(final alq handler, final als worldInfo, final by tagCompound) {
        for (final ModContainer mc : Loader.instance().getModList()) {
            if (mc instanceof InjectedModContainer) {
                final WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                if (wac == null) {
                    continue;
                }
                final by dataForWriting = wac.getDataForWriting(handler, worldInfo);
                tagCompound.a(mc.getModId(), dataForWriting);
            }
        }
    }
    
    public void handleWorldDataLoad(final alq handler, final als worldInfo, final by tagCompound) {
        if (this.getEffectiveSide() != Side.SERVER) {
            return;
        }
        if (this.handlerSet.contains(handler)) {
            return;
        }
        this.handlerSet.add(handler);
        final Map<String, cl> additionalProperties = Maps.newHashMap();
        worldInfo.setAdditionalProperties((Map)additionalProperties);
        for (final ModContainer mc : Loader.instance().getModList()) {
            if (mc instanceof InjectedModContainer) {
                final WorldAccessContainer wac = ((InjectedModContainer)mc).getWrappedWorldAccessContainer();
                if (wac == null) {
                    continue;
                }
                wac.readData(handler, worldInfo, additionalProperties, tagCompound.l(mc.getModId()));
            }
        }
    }
    
    public boolean shouldServerBeKilledQuietly() {
        return this.sidedDelegate != null && this.sidedDelegate.shouldServerShouldBeKilledQuietly();
    }
    
    public void disconnectIDMismatch(final MapDifference<Integer, ItemData> serverDifference, final ez toKill, final cm network) {
        this.sidedDelegate.disconnectIDMismatch(serverDifference, toKill, network);
    }
    
    public void handleServerStopped() {
        this.sidedDelegate.serverStopped();
        final MinecraftServer server = this.getMinecraftServerInstance();
        Loader.instance().serverStopped();
        if (server != null) {
            ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, false, "field_71316_v", "u", "serverStopped");
        }
    }
    
    public String getModName() {
        final List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        if (!this.noForge) {
            modNames.add("forge");
        }
        if (Loader.instance().getFMLBrandingProperties().containsKey("snooperbranding")) {
            modNames.add(Loader.instance().getFMLBrandingProperties().get("snooperbranding"));
        }
        return Joiner.on(',').join((Iterable)modNames);
    }
    
    public void addModToResourcePack(final ModContainer container) {
        this.sidedDelegate.addModAsResource(container);
    }
    
    public void updateResourcePackList() {
        this.sidedDelegate.updateResourcePackList();
    }
    
    public String getCurrentLanguage() {
        return this.sidedDelegate.getCurrentLanguage();
    }
    
    static {
        INSTANCE = new FMLCommonHandler();
    }
}
