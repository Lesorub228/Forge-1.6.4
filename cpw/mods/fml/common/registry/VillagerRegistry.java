// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.registry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Collections;
import java.util.Collection;
import cpw.mods.fml.common.FMLLog;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ArrayListMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Multimap;

public class VillagerRegistry
{
    private static final VillagerRegistry INSTANCE;
    private Multimap<Integer, IVillageTradeHandler> tradeHandlers;
    private Map<Class<?>, IVillageCreationHandler> villageCreationHandlers;
    private List<Integer> newVillagerIds;
    @SideOnly(Side.CLIENT)
    private Map<Integer, bjo> newVillagers;
    
    public VillagerRegistry() {
        this.tradeHandlers = (Multimap<Integer, IVillageTradeHandler>)ArrayListMultimap.create();
        this.villageCreationHandlers = Maps.newHashMap();
        this.newVillagerIds = Lists.newArrayList();
    }
    
    public static VillagerRegistry instance() {
        return VillagerRegistry.INSTANCE;
    }
    
    public void registerVillagerId(final int id) {
        if (this.newVillagerIds.contains(id)) {
            FMLLog.severe("Attempt to register duplicate villager id %d", id);
            throw new RuntimeException();
        }
        this.newVillagerIds.add(id);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerVillagerSkin(final int villagerId, final bjo villagerSkin) {
        if (this.newVillagers == null) {
            this.newVillagers = Maps.newHashMap();
        }
        this.newVillagers.put(villagerId, villagerSkin);
    }
    
    public void registerVillageCreationHandler(final IVillageCreationHandler handler) {
        this.villageCreationHandlers.put(handler.getComponentClass(), handler);
    }
    
    public void registerVillageTradeHandler(final int villagerId, final IVillageTradeHandler handler) {
        this.tradeHandlers.put((Object)villagerId, (Object)handler);
    }
    
    @SideOnly(Side.CLIENT)
    public static bjo getVillagerSkin(final int villagerType, final bjo defaultSkin) {
        if (instance().newVillagers != null && instance().newVillagers.containsKey(villagerType)) {
            return instance().newVillagers.get(villagerType);
        }
        return defaultSkin;
    }
    
    public static Collection<Integer> getRegisteredVillagers() {
        return Collections.unmodifiableCollection((Collection<? extends Integer>)instance().newVillagerIds);
    }
    
    public static void manageVillagerTrades(final abm recipeList, final ub villager, final int villagerType, final Random random) {
        for (final IVillageTradeHandler handler : instance().tradeHandlers.get((Object)villagerType)) {
            handler.manipulateTradesForVillager(villager, recipeList, random);
        }
    }
    
    public static void addExtraVillageComponents(final ArrayList components, final Random random, final int i) {
        final List<ajd> parts = components;
        for (final IVillageCreationHandler handler : instance().villageCreationHandlers.values()) {
            parts.add(handler.getVillagePieceWeight(random, i));
        }
    }
    
    public static Object getVillageComponent(final ajd villagePiece, final ajj startPiece, final List pieces, final Random random, final int p1, final int p2, final int p3, final int p4, final int p5) {
        return instance().villageCreationHandlers.get(villagePiece.a).buildComponent(villagePiece, startPiece, pieces, random, p1, p2, p3, p4, p5);
    }
    
    public static void addEmeraldBuyRecipe(final ub villager, final abm list, final Random random, final yc item, final float chance, final int min, final int max) {
        if (min > 0 && max > 0) {
            ub.bB.put(item.cv, new mh((Object)min, (Object)max));
        }
        ub.a(list, item.o(), random, chance);
    }
    
    public static void addEmeraldSellRecipe(final ub villager, final abm list, final Random random, final yc item, final float chance, final int min, final int max) {
        if (min > 0 && max > 0) {
            ub.bC.put(item.cv, new mh((Object)min, (Object)max));
        }
        ub.b(list, item.o(), random, chance);
    }
    
    public static void applyRandomTrade(final ub villager, final Random rand) {
        final int extra = instance().newVillagerIds.size();
        final int trade = rand.nextInt(5 + extra);
        villager.p((trade < 5) ? trade : ((int)instance().newVillagerIds.get(trade - 5)));
    }
    
    static {
        INSTANCE = new VillagerRegistry();
    }
    
    public interface IVillageTradeHandler
    {
        void manipulateTradesForVillager(final ub p0, final abm p1, final Random p2);
    }
    
    public interface IVillageCreationHandler
    {
        ajd getVillagePieceWeight(final Random p0, final int p1);
        
        Class<?> getComponentClass();
        
        Object buildComponent(final ajd p0, final ajj p1, final List p2, final Random p3, final int p4, final int p5, final int p6, final int p7, final int p8);
    }
}
