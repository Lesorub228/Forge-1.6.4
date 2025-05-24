// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import java.util.HashSet;
import java.util.HashMap;
import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.Map;

public abstract class FluidContainerRegistry
{
    private static Map<List, FluidContainerData> containerFluidMap;
    private static Map<List, FluidContainerData> filledContainerMap;
    private static Set<List> emptyContainers;
    public static final int BUCKET_VOLUME = 1000;
    public static final ye EMPTY_BUCKET;
    public static final ye EMPTY_BOTTLE;
    private static final ye NULL_EMPTYCONTAINER;
    
    private FluidContainerRegistry() {
    }
    
    public static boolean registerFluidContainer(final FluidStack stack, final ye filledContainer, final ye emptyContainer) {
        return registerFluidContainer(new FluidContainerData(stack, filledContainer, emptyContainer));
    }
    
    public static boolean registerFluidContainer(final Fluid fluid, final ye filledContainer, final ye emptyContainer) {
        if (!FluidRegistry.isFluidRegistered(fluid)) {
            FluidRegistry.registerFluid(fluid);
        }
        return registerFluidContainer(new FluidStack(fluid, 1000), filledContainer, emptyContainer);
    }
    
    public static boolean registerFluidContainer(final FluidStack stack, final ye filledContainer) {
        return registerFluidContainer(new FluidContainerData(stack, filledContainer, null, true));
    }
    
    public static boolean registerFluidContainer(final Fluid fluid, final ye filledContainer) {
        if (!FluidRegistry.isFluidRegistered(fluid)) {
            FluidRegistry.registerFluid(fluid);
        }
        return registerFluidContainer(new FluidStack(fluid, 1000), filledContainer);
    }
    
    public static boolean registerFluidContainer(final FluidContainerData data) {
        if (isFilledContainer(data.filledContainer)) {
            return false;
        }
        FluidContainerRegistry.containerFluidMap.put(Arrays.asList(data.filledContainer.d, data.filledContainer.k()), data);
        if (data.emptyContainer != null && data.emptyContainer != FluidContainerRegistry.NULL_EMPTYCONTAINER) {
            FluidContainerRegistry.filledContainerMap.put(Arrays.asList(data.emptyContainer.d, data.emptyContainer.k(), data.fluid.fluidID), data);
            FluidContainerRegistry.emptyContainers.add(Arrays.asList(data.emptyContainer.d, data.emptyContainer.k()));
        }
        MinecraftForge.EVENT_BUS.post(new FluidContainerRegisterEvent(data));
        return true;
    }
    
    public static FluidStack getFluidForFilledItem(final ye container) {
        if (container == null) {
            return null;
        }
        final FluidContainerData data = FluidContainerRegistry.containerFluidMap.get(Arrays.asList(container.d, container.k()));
        return (data == null) ? null : data.fluid.copy();
    }
    
    public static ye fillFluidContainer(final FluidStack fluid, final ye container) {
        if (container == null || fluid == null) {
            return null;
        }
        final FluidContainerData data = FluidContainerRegistry.filledContainerMap.get(Arrays.asList(container.d, container.k(), fluid.fluidID));
        if (data != null && fluid.amount >= data.fluid.amount) {
            return data.filledContainer.m();
        }
        return null;
    }
    
    public static boolean containsFluid(final ye container, final FluidStack fluid) {
        if (container == null || fluid == null) {
            return false;
        }
        final FluidContainerData data = FluidContainerRegistry.filledContainerMap.get(Arrays.asList(container.d, container.k(), fluid.fluidID));
        return data != null && data.fluid.isFluidEqual(fluid);
    }
    
    public static boolean isBucket(final ye container) {
        if (container == null) {
            return false;
        }
        if (container.a(FluidContainerRegistry.EMPTY_BUCKET)) {
            return true;
        }
        final FluidContainerData data = FluidContainerRegistry.containerFluidMap.get(Arrays.asList(container.d, container.k()));
        return data != null && data.emptyContainer.a(FluidContainerRegistry.EMPTY_BUCKET);
    }
    
    public static boolean isContainer(final ye container) {
        return isEmptyContainer(container) || isFilledContainer(container);
    }
    
    public static boolean isEmptyContainer(final ye container) {
        return container != null && FluidContainerRegistry.emptyContainers.contains(Arrays.asList(container.d, container.k()));
    }
    
    public static boolean isFilledContainer(final ye container) {
        return container != null && getFluidForFilledItem(container) != null;
    }
    
    public static FluidContainerData[] getRegisteredFluidContainerData() {
        return FluidContainerRegistry.containerFluidMap.values().toArray(new FluidContainerData[FluidContainerRegistry.containerFluidMap.size()]);
    }
    
    static {
        FluidContainerRegistry.containerFluidMap = new HashMap<List, FluidContainerData>();
        FluidContainerRegistry.filledContainerMap = new HashMap<List, FluidContainerData>();
        FluidContainerRegistry.emptyContainers = new HashSet<List>();
        EMPTY_BUCKET = new ye(yc.ay);
        EMPTY_BOTTLE = new ye(yc.bv);
        NULL_EMPTYCONTAINER = new ye(yc.ay);
        registerFluidContainer(FluidRegistry.WATER, new ye(yc.az), FluidContainerRegistry.EMPTY_BUCKET);
        registerFluidContainer(FluidRegistry.LAVA, new ye(yc.aA), FluidContainerRegistry.EMPTY_BUCKET);
        registerFluidContainer(FluidRegistry.WATER, new ye((yc)yc.bu), FluidContainerRegistry.EMPTY_BOTTLE);
    }
    
    public static class FluidContainerData
    {
        public final FluidStack fluid;
        public final ye filledContainer;
        public final ye emptyContainer;
        
        public FluidContainerData(final FluidStack stack, final ye filledContainer, final ye emptyContainer) {
            this(stack, filledContainer, emptyContainer, false);
        }
        
        public FluidContainerData(final FluidStack stack, final ye filledContainer, final ye emptyContainer, final boolean nullEmpty) {
            this.fluid = stack;
            this.filledContainer = filledContainer;
            this.emptyContainer = ((emptyContainer == null) ? FluidContainerRegistry.NULL_EMPTYCONTAINER : emptyContainer);
            if (stack == null || filledContainer == null || (emptyContainer == null && !nullEmpty)) {
                throw new RuntimeException("Invalid FluidContainerData - a parameter was null.");
            }
        }
        
        public FluidContainerData copy() {
            return new FluidContainerData(this.fluid, this.filledContainer, this.emptyContainer, true);
        }
    }
    
    public static class FluidContainerRegisterEvent extends Event
    {
        public final FluidContainerData data;
        
        public FluidContainerRegisterEvent(final FluidContainerData data) {
            this.data = data.copy();
        }
    }
}
