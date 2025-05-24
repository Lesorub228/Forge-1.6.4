// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.liquids;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Map;

@Deprecated
public class LiquidContainerRegistry
{
    public static final int BUCKET_VOLUME = 1000;
    public static final ye EMPTY_BUCKET;
    private static Map<List, LiquidContainerData> mapFilledItemFromLiquid;
    private static Map<List, LiquidContainerData> mapLiquidFromFilledItem;
    private static Set<List> setContainerValidation;
    private static Set<List> setLiquidValidation;
    private static ArrayList<LiquidContainerData> liquids;
    
    public static void registerLiquid(final LiquidContainerData data) {
        LiquidContainerRegistry.mapFilledItemFromLiquid.put(Arrays.asList(data.container.d, data.container.k(), data.stillLiquid.itemID, data.stillLiquid.itemMeta), data);
        LiquidContainerRegistry.mapLiquidFromFilledItem.put(Arrays.asList(data.filled.d, data.filled.k()), data);
        LiquidContainerRegistry.setContainerValidation.add(Arrays.asList(data.container.d, data.container.k()));
        LiquidContainerRegistry.setLiquidValidation.add(Arrays.asList(data.stillLiquid.itemID, data.stillLiquid.itemMeta));
        LiquidContainerRegistry.liquids.add(data);
    }
    
    public static LiquidStack getLiquidForFilledItem(final ye filledContainer) {
        if (filledContainer == null) {
            return null;
        }
        final LiquidContainerData ret = LiquidContainerRegistry.mapLiquidFromFilledItem.get(Arrays.asList(filledContainer.d, filledContainer.k()));
        return (ret == null) ? null : ret.stillLiquid.copy();
    }
    
    public static ye fillLiquidContainer(final LiquidStack liquid, final ye emptyContainer) {
        if (emptyContainer == null || liquid == null) {
            return null;
        }
        final LiquidContainerData ret = LiquidContainerRegistry.mapFilledItemFromLiquid.get(Arrays.asList(emptyContainer.d, emptyContainer.k(), liquid.itemID, liquid.itemMeta));
        if (ret != null && liquid.amount >= ret.stillLiquid.amount) {
            return ret.filled.m();
        }
        return null;
    }
    
    public static boolean containsLiquid(final ye filledContainer, final LiquidStack liquid) {
        if (filledContainer == null || liquid == null) {
            return false;
        }
        final LiquidContainerData ret = LiquidContainerRegistry.mapLiquidFromFilledItem.get(Arrays.asList(filledContainer.d, filledContainer.k()));
        return ret != null && ret.stillLiquid.isLiquidEqual(liquid);
    }
    
    public static boolean isBucket(final ye container) {
        if (container == null) {
            return false;
        }
        if (container.a(LiquidContainerRegistry.EMPTY_BUCKET)) {
            return true;
        }
        final LiquidContainerData ret = LiquidContainerRegistry.mapLiquidFromFilledItem.get(Arrays.asList(container.d, container.k()));
        return ret != null && ret.container.a(LiquidContainerRegistry.EMPTY_BUCKET);
    }
    
    public static boolean isContainer(final ye container) {
        return isEmptyContainer(container) || isFilledContainer(container);
    }
    
    public static boolean isEmptyContainer(final ye emptyContainer) {
        return emptyContainer != null && LiquidContainerRegistry.setContainerValidation.contains(Arrays.asList(emptyContainer.d, emptyContainer.k()));
    }
    
    public static boolean isFilledContainer(final ye filledContainer) {
        return filledContainer != null && getLiquidForFilledItem(filledContainer) != null;
    }
    
    public static boolean isLiquid(final ye item) {
        return item != null && LiquidContainerRegistry.setLiquidValidation.contains(Arrays.asList(item.d, item.k()));
    }
    
    public static LiquidContainerData[] getRegisteredLiquidContainerData() {
        return LiquidContainerRegistry.liquids.toArray(new LiquidContainerData[LiquidContainerRegistry.liquids.size()]);
    }
    
    static {
        EMPTY_BUCKET = new ye(yc.ay);
        LiquidContainerRegistry.mapFilledItemFromLiquid = new HashMap<List, LiquidContainerData>();
        LiquidContainerRegistry.mapLiquidFromFilledItem = new HashMap<List, LiquidContainerData>();
        LiquidContainerRegistry.setContainerValidation = new HashSet<List>();
        LiquidContainerRegistry.setLiquidValidation = new HashSet<List>();
        LiquidContainerRegistry.liquids = new ArrayList<LiquidContainerData>();
        registerLiquid(new LiquidContainerData(new LiquidStack(aqz.G, 1000), new ye(yc.az), new ye(yc.ay)));
        registerLiquid(new LiquidContainerData(new LiquidStack(aqz.I, 1000), new ye(yc.aA), new ye(yc.ay)));
        registerLiquid(new LiquidContainerData(new LiquidStack(aqz.G, 1000), new ye((yc)yc.bu), new ye(yc.bv)));
    }
}
