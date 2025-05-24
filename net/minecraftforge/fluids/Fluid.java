// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.FMLLog;
import net.minecraftforge.common.ForgeDummyContainer;
import java.util.Locale;
import java.util.Map;

public class Fluid
{
    protected final String fluidName;
    protected String unlocalizedName;
    protected ms stillIcon;
    protected ms flowingIcon;
    protected int luminosity;
    protected int density;
    protected int temperature;
    protected int viscosity;
    protected boolean isGaseous;
    protected yq rarity;
    protected int blockID;
    private static Map<String, String> legacyNames;
    
    public Fluid(final String fluidName) {
        this.luminosity = 0;
        this.density = 1000;
        this.temperature = 295;
        this.viscosity = 1000;
        this.rarity = yq.a;
        this.blockID = -1;
        this.fluidName = fluidName.toLowerCase(Locale.ENGLISH);
        this.unlocalizedName = fluidName;
    }
    
    public Fluid setUnlocalizedName(final String unlocalizedName) {
        this.unlocalizedName = unlocalizedName;
        return this;
    }
    
    public Fluid setBlockID(final int blockID) {
        if (this.blockID == -1 || this.blockID == blockID) {
            this.blockID = blockID;
        }
        else {
            if (ForgeDummyContainer.forceDuplicateFluidBlockCrash) {
                FMLLog.severe("A mod has attempted to assign BlockID " + blockID + " to the Fluid '" + this.fluidName + "' but this Fluid has already been linked to BlockID " + this.blockID + ". Configure your mods to prevent this from happening.", new Object[0]);
                throw new LoaderException(new RuntimeException("A mod has attempted to assign BlockID " + blockID + " to the Fluid '" + this.fluidName + "' but this Fluid has already been linked to BlockID " + this.blockID + ". Configure your mods to prevent this from happening."));
            }
            FMLLog.warning("A mod has attempted to assign BlockID " + blockID + " to the Fluid '" + this.fluidName + "' but this Fluid has already been linked to BlockID " + this.blockID + ". Configure your mods to prevent this from happening.", new Object[0]);
        }
        return this;
    }
    
    public Fluid setBlockID(final aqz block) {
        return this.setBlockID(block.cF);
    }
    
    public Fluid setLuminosity(final int luminosity) {
        this.luminosity = luminosity;
        return this;
    }
    
    public Fluid setDensity(final int density) {
        this.density = density;
        return this;
    }
    
    public Fluid setTemperature(final int temperature) {
        this.temperature = temperature;
        return this;
    }
    
    public Fluid setViscosity(final int viscosity) {
        this.viscosity = viscosity;
        return this;
    }
    
    public Fluid setGaseous(final boolean isGaseous) {
        this.isGaseous = isGaseous;
        return this;
    }
    
    public Fluid setRarity(final yq rarity) {
        this.rarity = rarity;
        return this;
    }
    
    public final String getName() {
        return this.fluidName;
    }
    
    public final int getID() {
        return FluidRegistry.getFluidID(this.fluidName);
    }
    
    public final int getBlockID() {
        return this.blockID;
    }
    
    public final boolean canBePlacedInWorld() {
        return this.blockID != -1;
    }
    
    public String getLocalizedName() {
        final String s = this.getUnlocalizedName();
        return (s == null) ? "" : bu.a(s);
    }
    
    public String getUnlocalizedName() {
        return "fluid." + this.unlocalizedName;
    }
    
    public final int getSpriteNumber() {
        return 0;
    }
    
    public final int getLuminosity() {
        return this.luminosity;
    }
    
    public final int getDensity() {
        return this.density;
    }
    
    public final int getTemperature() {
        return this.temperature;
    }
    
    public final int getViscosity() {
        return this.viscosity;
    }
    
    public final boolean isGaseous() {
        return this.isGaseous;
    }
    
    public yq getRarity() {
        return this.rarity;
    }
    
    public int getColor() {
        return 16777215;
    }
    
    public final Fluid setStillIcon(final ms stillIcon) {
        this.stillIcon = stillIcon;
        return this;
    }
    
    public final Fluid setFlowingIcon(final ms flowingIcon) {
        this.flowingIcon = flowingIcon;
        return this;
    }
    
    public final Fluid setIcons(final ms stillIcon, final ms flowingIcon) {
        return this.setStillIcon(stillIcon).setFlowingIcon(flowingIcon);
    }
    
    public final Fluid setIcons(final ms commonIcon) {
        return this.setStillIcon(commonIcon).setFlowingIcon(commonIcon);
    }
    
    public ms getIcon() {
        return this.getStillIcon();
    }
    
    public ms getStillIcon() {
        return this.stillIcon;
    }
    
    public ms getFlowingIcon() {
        return this.flowingIcon;
    }
    
    public int getLuminosity(final FluidStack stack) {
        return this.getLuminosity();
    }
    
    public int getDensity(final FluidStack stack) {
        return this.getDensity();
    }
    
    public int getTemperature(final FluidStack stack) {
        return this.getTemperature();
    }
    
    public int getViscosity(final FluidStack stack) {
        return this.getViscosity();
    }
    
    public boolean isGaseous(final FluidStack stack) {
        return this.isGaseous();
    }
    
    public yq getRarity(final FluidStack stack) {
        return this.getRarity();
    }
    
    public int getColor(final FluidStack stack) {
        return this.getColor();
    }
    
    public ms getIcon(final FluidStack stack) {
        return this.getIcon();
    }
    
    public int getLuminosity(final abw world, final int x, final int y, final int z) {
        return this.getLuminosity();
    }
    
    public int getDensity(final abw world, final int x, final int y, final int z) {
        return this.getDensity();
    }
    
    public int getTemperature(final abw world, final int x, final int y, final int z) {
        return this.getTemperature();
    }
    
    public int getViscosity(final abw world, final int x, final int y, final int z) {
        return this.getViscosity();
    }
    
    public boolean isGaseous(final abw world, final int x, final int y, final int z) {
        return this.isGaseous();
    }
    
    public yq getRarity(final abw world, final int x, final int y, final int z) {
        return this.getRarity();
    }
    
    public int getColor(final abw world, final int x, final int y, final int z) {
        return this.getColor();
    }
    
    public ms getIcon(final abw world, final int x, final int y, final int z) {
        return this.getIcon();
    }
    
    static String convertLegacyName(final String fluidName) {
        return (fluidName != null && Fluid.legacyNames.containsKey(fluidName)) ? Fluid.legacyNames.get(fluidName) : fluidName;
    }
    
    public static void registerLegacyName(final String legacyName, final String canonicalName) {
        Fluid.legacyNames.put(legacyName.toLowerCase(Locale.ENGLISH), canonicalName);
    }
    
    static {
        Fluid.legacyNames = Maps.newHashMap();
    }
}
