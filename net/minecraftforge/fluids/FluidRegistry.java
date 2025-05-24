// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import java.util.Iterator;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.event.Event;
import net.minecraftforge.common.MinecraftForge;
import java.util.Map;
import com.google.common.collect.BiMap;
import java.util.HashMap;

public abstract class FluidRegistry
{
    static int maxID;
    static HashMap<String, Fluid> fluids;
    static BiMap<String, Integer> fluidIDs;
    static BiMap<aqz, Fluid> fluidBlocks;
    public static final Fluid WATER;
    public static final Fluid LAVA;
    public static int renderIdFluid;
    
    private FluidRegistry() {
    }
    
    static void initFluidIDs(final BiMap<String, Integer> newfluidIDs) {
        FluidRegistry.maxID = newfluidIDs.size();
        FluidRegistry.fluidIDs.clear();
        FluidRegistry.fluidIDs.putAll((Map)newfluidIDs);
    }
    
    public static boolean registerFluid(final Fluid fluid) {
        if (FluidRegistry.fluidIDs.containsKey((Object)fluid.getName())) {
            return false;
        }
        FluidRegistry.fluids.put(fluid.getName(), fluid);
        FluidRegistry.fluidIDs.put((Object)fluid.getName(), (Object)(++FluidRegistry.maxID));
        MinecraftForge.EVENT_BUS.post(new FluidRegisterEvent(fluid.getName(), FluidRegistry.maxID));
        return true;
    }
    
    public static boolean isFluidRegistered(final Fluid fluid) {
        return FluidRegistry.fluidIDs.containsKey((Object)fluid.getName());
    }
    
    public static boolean isFluidRegistered(final String fluidName) {
        return FluidRegistry.fluidIDs.containsKey((Object)fluidName);
    }
    
    public static Fluid getFluid(final String fluidName) {
        return FluidRegistry.fluids.get(fluidName);
    }
    
    public static Fluid getFluid(final int fluidID) {
        return FluidRegistry.fluids.get(getFluidName(fluidID));
    }
    
    public static String getFluidName(final int fluidID) {
        return (String)FluidRegistry.fluidIDs.inverse().get((Object)fluidID);
    }
    
    public static String getFluidName(final FluidStack stack) {
        return getFluidName(stack.fluidID);
    }
    
    public static int getFluidID(final String fluidName) {
        return (int)FluidRegistry.fluidIDs.get((Object)fluidName);
    }
    
    public static FluidStack getFluidStack(final String fluidName, final int amount) {
        if (!FluidRegistry.fluidIDs.containsKey((Object)fluidName)) {
            return null;
        }
        return new FluidStack(getFluidID(fluidName), amount);
    }
    
    public static Map<String, Fluid> getRegisteredFluids() {
        return (Map<String, Fluid>)ImmutableMap.copyOf((Map)FluidRegistry.fluids);
    }
    
    public static Map<String, Integer> getRegisteredFluidIDs() {
        return (Map<String, Integer>)ImmutableMap.copyOf((Map)FluidRegistry.fluidIDs);
    }
    
    public static Fluid lookupFluidForBlock(final aqz block) {
        if (FluidRegistry.fluidBlocks == null) {
            FluidRegistry.fluidBlocks = (BiMap<aqz, Fluid>)HashBiMap.create();
            for (final Fluid fluid : FluidRegistry.fluids.values()) {
                if (fluid.canBePlacedInWorld() && aqz.s[fluid.getBlockID()] != null) {
                    FluidRegistry.fluidBlocks.put((Object)aqz.s[fluid.getBlockID()], (Object)fluid);
                }
            }
        }
        return (Fluid)FluidRegistry.fluidBlocks.get((Object)block);
    }
    
    static {
        FluidRegistry.maxID = 0;
        FluidRegistry.fluids = new HashMap<String, Fluid>();
        FluidRegistry.fluidIDs = (BiMap<String, Integer>)HashBiMap.create();
        WATER = new Fluid("water") {
            @Override
            public String getLocalizedName() {
                return bu.a("tile.water.name");
            }
        }.setBlockID(aqz.G.cF).setUnlocalizedName(aqz.G.a());
        LAVA = new Fluid("lava") {
            @Override
            public String getLocalizedName() {
                return bu.a("tile.lava.name");
            }
        }.setBlockID(aqz.I.cF).setLuminosity(15).setDensity(3000).setViscosity(6000).setTemperature(1300).setUnlocalizedName(aqz.I.a());
        FluidRegistry.renderIdFluid = -1;
        registerFluid(FluidRegistry.WATER);
        registerFluid(FluidRegistry.LAVA);
    }
    
    public static class FluidRegisterEvent extends Event
    {
        public final String fluidName;
        public final int fluidID;
        
        public FluidRegisterEvent(final String fluidName, final int fluidID) {
            this.fluidName = fluidName;
            this.fluidID = fluidID;
        }
    }
}
