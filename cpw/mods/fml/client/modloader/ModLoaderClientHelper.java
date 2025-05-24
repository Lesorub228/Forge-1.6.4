// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client.modloader;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import com.google.common.collect.Iterables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.modloader.BaseModProxy;
import com.google.common.collect.Multimaps;
import java.util.Collections;
import java.util.Collection;
import com.google.common.base.Supplier;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import com.google.common.collect.MapMaker;
import java.util.Iterator;
import com.google.common.collect.MapDifference;
import com.google.common.base.Equivalence;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import java.util.Map;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.modloader.IModLoaderSidedHelper;

public class ModLoaderClientHelper implements IModLoaderSidedHelper
{
    private atv client;
    private static Multimap<ModLoaderModContainer, ModLoaderKeyBindingHandler> keyBindingContainers;
    private Map<cm, ez> managerLookups;
    
    public static int obtainBlockModelIdFor(final BaseMod mod, final boolean inventoryRenderer) {
        final int renderId = RenderingRegistry.getNextAvailableRenderId();
        final ModLoaderBlockRendererHandler bri = new ModLoaderBlockRendererHandler(renderId, inventoryRenderer, mod);
        RenderingRegistry.registerBlockHandler(bri);
        return renderId;
    }
    
    public static void handleFinishLoadingFor(final ModLoaderModContainer mc, final atv game) {
        FMLLog.log(mc.getModId(), Level.FINE, "Handling post startup activities for ModLoader mod %s", mc.getModId());
        final BaseMod mod = (BaseMod)mc.getMod();
        final Map<Class<? extends nn>, bgm> renderers = Maps.newHashMap(bgl.a.q);
        try {
            FMLLog.log(mc.getModId(), Level.FINEST, "Requesting renderers from basemod %s", mc.getModId());
            mod.addRenderer(renderers);
            FMLLog.log(mc.getModId(), Level.FINEST, "Received %d renderers from basemod %s", renderers.size(), mc.getModId());
        }
        catch (final Exception e) {
            FMLLog.log(mc.getModId(), Level.SEVERE, e, "A severe problem was detected with the mod %s during the addRenderer call. Continuing, but expect odd results", mc.getModId());
        }
        final MapDifference<Class<? extends nn>, bgm> difference = (MapDifference<Class<? extends nn>, bgm>)Maps.difference(bgl.a.q, (Map)renderers, Equivalence.identity());
        for (final Map.Entry<Class<? extends nn>, bgm> e2 : difference.entriesOnlyOnLeft().entrySet()) {
            FMLLog.log(mc.getModId(), Level.WARNING, "The mod %s attempted to remove an entity renderer %s from the entity map. This will be ignored.", mc.getModId(), e2.getKey().getName());
        }
        for (final Map.Entry<Class<? extends nn>, bgm> e2 : difference.entriesOnlyOnRight().entrySet()) {
            FMLLog.log(mc.getModId(), Level.FINEST, "Registering ModLoader entity renderer %s as instance of %s", e2.getKey().getName(), e2.getValue().getClass().getName());
            RenderingRegistry.registerEntityRenderingHandler(e2.getKey(), e2.getValue());
        }
        for (final Map.Entry<Class<? extends nn>, MapDifference.ValueDifference<bgm>> e3 : difference.entriesDiffering().entrySet()) {
            FMLLog.log(mc.getModId(), Level.FINEST, "Registering ModLoader entity rendering override for %s as instance of %s", e3.getKey().getName(), e3.getValue().rightValue().getClass().getName());
            RenderingRegistry.registerEntityRenderingHandler(e3.getKey(), (bgm)e3.getValue().rightValue());
        }
        try {
            mod.registerAnimation(game);
        }
        catch (final Exception e4) {
            FMLLog.log(mc.getModId(), Level.SEVERE, e4, "A severe problem was detected with the mod %s during the registerAnimation call. Continuing, but expect odd results", mc.getModId());
        }
    }
    
    public ModLoaderClientHelper(final atv client) {
        this.managerLookups = new MapMaker().weakKeys().weakValues().makeMap();
        this.client = client;
        ModLoaderHelper.sidedHelper = this;
        ModLoaderClientHelper.keyBindingContainers = (Multimap<ModLoaderModContainer, ModLoaderKeyBindingHandler>)Multimaps.newMultimap((Map)Maps.newHashMap(), (Supplier)new Supplier<Collection<ModLoaderKeyBindingHandler>>() {
            public Collection<ModLoaderKeyBindingHandler> get() {
                return Collections.singleton(new ModLoaderKeyBindingHandler());
            }
        });
    }
    
    @Override
    public void finishModLoading(final ModLoaderModContainer mc) {
        handleFinishLoadingFor(mc, this.client);
    }
    
    public static void registerKeyBinding(final BaseModProxy mod, final ats keyHandler, final boolean allowRepeat) {
        final ModLoaderModContainer mlmc = (ModLoaderModContainer)Loader.instance().activeModContainer();
        final ModLoaderKeyBindingHandler handler = (ModLoaderKeyBindingHandler)Iterables.getOnlyElement((Iterable)ModLoaderClientHelper.keyBindingContainers.get((Object)mlmc));
        handler.setModContainer(mlmc);
        handler.addKeyBinding(keyHandler, allowRepeat);
        KeyBindingRegistry.registerKeyBinding(handler);
    }
    
    @Override
    public Object getClientGui(final BaseModProxy mod, final uf player, final int ID, final int x, final int y, final int z) {
        return ((BaseMod)mod).getContainerGUI((bdi)player, ID, x, y, z);
    }
    
    @Override
    public nn spawnEntity(final BaseModProxy mod, final EntitySpawnPacket input, final EntityRegistry.EntityRegistration er) {
        return ((BaseMod)mod).spawnEntity(er.getModEntityId(), (abw)this.client.f, input.scaledX, input.scaledY, input.scaledZ);
    }
    
    @Override
    public void sendClientPacket(final BaseModProxy mod, final ea packet) {
        ((BaseMod)mod).clientCustomPayload(this.client.h.a, packet);
    }
    
    @Override
    public void clientConnectionOpened(final ez netClientHandler, final cm manager, final BaseModProxy mod) {
        this.managerLookups.put(manager, netClientHandler);
        ((BaseMod)mod).clientConnect((bcw)netClientHandler);
    }
    
    @Override
    public boolean clientConnectionClosed(final cm manager, final BaseModProxy mod) {
        if (this.managerLookups.containsKey(manager)) {
            ((BaseMod)mod).clientDisconnect((bcw)this.managerLookups.get(manager));
            return true;
        }
        return false;
    }
}
