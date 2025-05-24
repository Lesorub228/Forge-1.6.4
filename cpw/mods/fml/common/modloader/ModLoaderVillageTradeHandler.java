// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common.modloader;

import java.util.Iterator;
import java.util.Random;
import com.google.common.collect.Lists;
import java.util.List;
import cpw.mods.fml.common.registry.VillagerRegistry;

public class ModLoaderVillageTradeHandler implements VillagerRegistry.IVillageTradeHandler
{
    private List<TradeEntry> trades;
    
    public ModLoaderVillageTradeHandler() {
        this.trades = Lists.newArrayList();
    }
    
    @Override
    public void manipulateTradesForVillager(final ub villager, final abm recipeList, final Random random) {
        for (final TradeEntry ent : this.trades) {
            if (ent.buying) {
                VillagerRegistry.addEmeraldBuyRecipe(villager, recipeList, random, yc.g[ent.id], ent.chance, ent.min, ent.max);
            }
            else {
                VillagerRegistry.addEmeraldSellRecipe(villager, recipeList, random, yc.g[ent.id], ent.chance, ent.min, ent.max);
            }
        }
    }
    
    public void addTrade(final TradeEntry entry) {
        this.trades.add(entry);
    }
}
