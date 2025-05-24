// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import com.google.common.collect.MapDifference;
import cpw.mods.fml.common.network.ModMissingPacket;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.EntitySpawnAdjustmentPacket;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.logging.Logger;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import com.google.common.base.Throwables;
import java.util.Iterator;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.launchwrapper.Launch;
import cpw.mods.fml.common.LoaderException;
import java.util.logging.Level;
import cpw.mods.fml.common.MetadataCollection;
import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.client.modloader.ModLoaderClientHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.List;
import cpw.mods.fml.common.DuplicateModsFoundException;
import cpw.mods.fml.common.WrongMinecraftVersionException;
import cpw.mods.fml.common.toposort.ModSortingException;
import cpw.mods.fml.common.MissingModsException;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.IFMLSidedHandler;

public class FMLClientHandler implements IFMLSidedHandler
{
    private static final FMLClientHandler INSTANCE;
    private atv client;
    private DummyModContainer optifineContainer;
    private boolean guiLoaded;
    private boolean serverIsRunning;
    private MissingModsException modsMissing;
    private ModSortingException modSorting;
    private boolean loading;
    private WrongMinecraftVersionException wrongMC;
    private CustomModLoadingErrorDisplayException customError;
    private DuplicateModsFoundException dupesFound;
    private boolean serverShouldBeKilledQuietly;
    private List<bjr> resourcePackList;
    private bjm resourceManager;
    private Map<String, bjr> resourcePackMap;
    
    public FMLClientHandler() {
        this.loading = true;
    }
    
    public void beginMinecraftLoading(final atv minecraft, final List resourcePackList, final bjm resourceManager) {
        this.client = minecraft;
        this.resourcePackList = resourcePackList;
        this.resourceManager = resourceManager;
        this.resourcePackMap = Maps.newHashMap();
        if (minecraft.p()) {
            FMLLog.severe("DEMO MODE DETECTED, FML will not work. Finishing now.", new Object[0]);
            this.haltGame("FML will not run in demo mode", new RuntimeException());
            return;
        }
        FMLCommonHandler.instance().beginLoading(this);
        new ModLoaderClientHelper(this.client);
        try {
            final Class<?> optifineConfig = Class.forName("Config", false, Loader.instance().getModClassLoader());
            final String optifineVersion = (String)optifineConfig.getField("VERSION").get(null);
            final Map<String, Object> dummyOptifineMeta = (Map<String, Object>)ImmutableMap.builder().put((Object)"name", (Object)"Optifine").put((Object)"version", (Object)optifineVersion).build();
            final ModMetadata optifineMetadata = MetadataCollection.from(this.getClass().getResourceAsStream("optifinemod.info"), "optifine").getMetadataForId("optifine", dummyOptifineMeta);
            this.optifineContainer = new DummyModContainer(optifineMetadata);
            FMLLog.info("Forge Mod Loader has detected optifine %s, enabling compatibility features", this.optifineContainer.getVersion());
        }
        catch (final Exception e) {
            this.optifineContainer = null;
        }
        try {
            Loader.instance().loadMods();
        }
        catch (final WrongMinecraftVersionException wrong) {
            this.wrongMC = wrong;
        }
        catch (final DuplicateModsFoundException dupes) {
            this.dupesFound = dupes;
        }
        catch (final MissingModsException missing) {
            this.modsMissing = missing;
        }
        catch (final ModSortingException sorting) {
            this.modSorting = sorting;
        }
        catch (final CustomModLoadingErrorDisplayException custom) {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt", new Object[0]);
            this.customError = custom;
        }
        catch (final LoaderException le) {
            this.haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        Map<String, Map<String, String>> sharedModList = Launch.blackboard.get("modList");
        if (sharedModList == null) {
            sharedModList = Maps.newHashMap();
            Launch.blackboard.put("modList", sharedModList);
        }
        for (final ModContainer mc : Loader.instance().getActiveModList()) {
            final Map<String, String> sharedModDescriptor = mc.getSharedModDescriptor();
            if (sharedModDescriptor != null) {
                final String sharedModId = "fml:" + mc.getModId();
                sharedModList.put(sharedModId, sharedModDescriptor);
            }
        }
    }
    
    @Override
    public void haltGame(final String message, final Throwable t) {
        this.client.c(new b(message, t));
        throw Throwables.propagate(t);
    }
    
    public void finishMinecraftLoading() {
        if (this.modsMissing != null || this.wrongMC != null || this.customError != null || this.dupesFound != null || this.modSorting != null) {
            return;
        }
        try {
            Loader.instance().initializeMods();
        }
        catch (final CustomModLoadingErrorDisplayException custom) {
            FMLLog.log(Level.SEVERE, custom, "A custom exception was thrown by a mod, the game will now halt", new Object[0]);
            this.customError = custom;
            return;
        }
        catch (final LoaderException le) {
            this.haltGame("There was a severe problem during mod loading that has caused the game to fail", le);
            return;
        }
        this.client.v.LOAD_SOUND_SYSTEM = true;
        this.client.a();
        RenderingRegistry.instance().loadEntityRenderers(bgl.a.q);
        this.loading = false;
        KeyBindingRegistry.instance().uploadKeyBindingsToGame(this.client.u);
    }
    
    public void extendModList() {
        final Map<String, Map<String, String>> modList = Launch.blackboard.get("modList");
        if (modList != null) {
            for (final Map.Entry<String, Map<String, String>> modEntry : modList.entrySet()) {
                final String sharedModId = modEntry.getKey();
                final String system = sharedModId.split(":")[0];
                if ("fml".equals(system)) {
                    continue;
                }
                final Map<String, String> mod = modEntry.getValue();
                final String modSystem = mod.get("modsystem");
                final String modId = mod.get("id");
                final String modVersion = mod.get("version");
                final String modName = mod.get("name");
                final String modURL = mod.get("url");
                final String modAuthors = mod.get("authors");
                final String modDescription = mod.get("description");
            }
        }
    }
    
    public void onInitializationComplete() {
        if (this.wrongMC != null) {
            this.client.a((awe)new GuiWrongMinecraft(this.wrongMC));
        }
        else if (this.modsMissing != null) {
            this.client.a((awe)new GuiModsMissing(this.modsMissing));
        }
        else if (this.dupesFound != null) {
            this.client.a((awe)new GuiDupesFound(this.dupesFound));
        }
        else if (this.modSorting != null) {
            this.client.a((awe)new GuiSortingProblem(this.modSorting));
        }
        else if (this.customError != null) {
            this.client.a((awe)new GuiCustomModLoadingErrorScreen(this.customError));
        }
    }
    
    public atv getClient() {
        return this.client;
    }
    
    public Logger getMinecraftLogger() {
        return null;
    }
    
    public static FMLClientHandler instance() {
        return FMLClientHandler.INSTANCE;
    }
    
    public void displayGuiScreen(final uf player, final awe gui) {
        if (this.client.h == player && gui != null) {
            this.client.a(gui);
        }
    }
    
    public void addSpecialModEntries(final ArrayList<ModContainer> mods) {
        if (this.optifineContainer != null) {
            mods.add(this.optifineContainer);
        }
    }
    
    @Override
    public List<String> getAdditionalBrandingInformation() {
        if (this.optifineContainer != null) {
            return Arrays.asList(String.format("Optifine %s", this.optifineContainer.getVersion()));
        }
        return (List<String>)ImmutableList.of();
    }
    
    @Override
    public Side getSide() {
        return Side.CLIENT;
    }
    
    public boolean hasOptifine() {
        return this.optifineContainer != null;
    }
    
    @Override
    public void showGuiScreen(final Object clientGuiElement) {
        final awe gui = (awe)clientGuiElement;
        this.client.a(gui);
    }
    
    @Override
    public nn spawnEntityIntoClientWorld(final EntityRegistry.EntityRegistration er, final EntitySpawnPacket packet) {
        final bdd wc = this.client.f;
        final Class<? extends nn> cls = er.getEntityClass();
        try {
            nn entity;
            if (er.hasCustomSpawning()) {
                entity = er.doCustomSpawning(packet);
            }
            else {
                entity = (nn)cls.getConstructor(abw.class).newInstance(wc);
                final int offset = packet.entityId - entity.k;
                entity.k = packet.entityId;
                entity.b(packet.scaledX, packet.scaledY, packet.scaledZ, packet.scaledYaw, packet.scaledPitch);
                if (entity instanceof og) {
                    ((og)entity).aP = packet.scaledHeadYaw;
                }
                final nn[] parts = entity.ao();
                if (parts != null) {
                    for (int j = 0; j < parts.length; ++j) {
                        final nn nn = parts[j];
                        nn.k += offset;
                    }
                }
            }
            entity.bZ = packet.rawX;
            entity.ca = packet.rawY;
            entity.cb = packet.rawZ;
            if (entity instanceof IThrowableEntity) {
                final nn thrower = (nn)((this.client.h.k == packet.throwerId) ? this.client.h : wc.a(packet.throwerId));
                ((IThrowableEntity)entity).setThrower(thrower);
            }
            if (packet.metadata != null) {
                entity.v().a(packet.metadata);
            }
            if (packet.throwerId > 0) {
                entity.h(packet.speedScaledX, packet.speedScaledY, packet.speedScaledZ);
            }
            if (entity instanceof IEntityAdditionalSpawnData) {
                ((IEntityAdditionalSpawnData)entity).readSpawnData(packet.dataStream);
            }
            wc.a(packet.entityId, entity);
            return entity;
        }
        catch (final Exception e) {
            FMLLog.log(Level.SEVERE, e, "A severe problem occurred during the spawning of an entity", new Object[0]);
            throw Throwables.propagate((Throwable)e);
        }
    }
    
    @Override
    public void adjustEntityLocationOnClient(final EntitySpawnAdjustmentPacket packet) {
        final nn ent = this.client.f.a(packet.entityId);
        if (ent != null) {
            ent.bZ = packet.serverX;
            ent.ca = packet.serverY;
            ent.cb = packet.serverZ;
        }
        else {
            FMLLog.fine("Attempted to adjust the position of entity %d which is not present on the client", packet.entityId);
        }
    }
    
    @Override
    public void beginServerLoading(final MinecraftServer server) {
        this.serverShouldBeKilledQuietly = false;
    }
    
    @Override
    public void finishServerLoading() {
    }
    
    @Override
    public MinecraftServer getServer() {
        return (MinecraftServer)this.client.C();
    }
    
    @Override
    public void sendPacket(final ey packet) {
        if (this.client.h != null) {
            this.client.h.a.c(packet);
        }
    }
    
    @Override
    public void displayMissingMods(final ModMissingPacket modMissingPacket) {
        this.client.a((awe)new GuiModsMissingForServer(modMissingPacket));
    }
    
    public boolean isLoading() {
        return this.loading;
    }
    
    @Override
    public void handleTinyPacket(final ez handler, final dr mapData) {
        ((bcw)handler).fmlPacket131Callback(mapData);
    }
    
    @Override
    public void setClientCompatibilityLevel(final byte compatibilityLevel) {
        bcw.setConnectionCompatibilityLevel(compatibilityLevel);
    }
    
    @Override
    public byte getClientCompatibilityLevel() {
        return bcw.getConnectionCompatibilityLevel();
    }
    
    public void warnIDMismatch(final MapDifference<Integer, ItemData> idDifferences, final boolean mayContinue) {
        final GuiIdMismatchScreen mismatch = new GuiIdMismatchScreen(idDifferences, mayContinue);
        this.client.a((awe)mismatch);
    }
    
    public void callbackIdDifferenceResponse(final boolean response) {
        if (response) {
            this.serverShouldBeKilledQuietly = false;
            GameData.releaseGate(true);
            this.client.continueWorldLoading();
        }
        else {
            this.serverShouldBeKilledQuietly = true;
            GameData.releaseGate(false);
            this.client.a((bdd)null);
            this.client.a((awe)null);
        }
    }
    
    @Override
    public boolean shouldServerShouldBeKilledQuietly() {
        return this.serverShouldBeKilledQuietly;
    }
    
    @Override
    public void disconnectIDMismatch(final MapDifference<Integer, ItemData> s, final ez toKill, final cm mgr) {
        boolean criticalMismatch = !s.entriesOnlyOnLeft().isEmpty();
        for (final Map.Entry<Integer, MapDifference.ValueDifference<ItemData>> mismatch : s.entriesDiffering().entrySet()) {
            final MapDifference.ValueDifference<ItemData> vd = mismatch.getValue();
            if (!((ItemData)vd.leftValue()).mayDifferByOrdinal((ItemData)vd.rightValue())) {
                criticalMismatch = true;
            }
        }
        if (!criticalMismatch) {
            return;
        }
        ((bcw)toKill).f();
        bcy.forceTermination((bcy)this.client.n);
        mgr.b();
        this.client.a((bdd)null);
        this.warnIDMismatch(s, false);
    }
    
    public boolean isGUIOpen(final Class<? extends awe> gui) {
        return this.client.n != null && this.client.n.getClass().equals(gui);
    }
    
    @Override
    public void addModAsResource(final ModContainer container) {
        final Class<?> resourcePackType = container.getCustomResourcePackClass();
        if (resourcePackType != null) {
            try {
                final bjr pack = (bjr)resourcePackType.getConstructor(ModContainer.class).newInstance(container);
                this.resourcePackList.add(pack);
                this.resourcePackMap.put(container.getModId(), pack);
            }
            catch (final NoSuchMethodException e) {
                FMLLog.log(Level.SEVERE, "The container %s (type %s) returned an invalid class for it's resource pack.", container.getName(), container.getClass().getName());
            }
            catch (final Exception e2) {
                FMLLog.log(Level.SEVERE, e2, "An unexpected exception occurred constructing the custom resource pack for %s", container.getName());
                throw Throwables.propagate((Throwable)e2);
            }
        }
    }
    
    @Override
    public void updateResourcePackList() {
        this.client.a();
    }
    
    public bjr getResourcePackFor(final String modId) {
        return this.resourcePackMap.get(modId);
    }
    
    @Override
    public String getCurrentLanguage() {
        return this.client.M().c().a();
    }
    
    @Override
    public void serverStopped() {
        final MinecraftServer server = this.getServer();
        if (server != null && !server.ah()) {
            ObfuscationReflectionHelper.setPrivateValue(MinecraftServer.class, server, true, "field_71296_Q", "serverIsRunning");
        }
    }
    
    static {
        INSTANCE = new FMLClientHandler();
    }
}
