// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import java.util.Locale;

public class FluidStack
{
    public int fluidID;
    public int amount;
    public by tag;
    
    public FluidStack(final Fluid fluid, final int amount) {
        this.fluidID = fluid.getID();
        this.amount = amount;
    }
    
    public FluidStack(final int fluidID, final int amount) {
        this.fluidID = fluidID;
        this.amount = amount;
    }
    
    public FluidStack(final int fluidID, final int amount, final by nbt) {
        this(fluidID, amount);
        if (nbt != null) {
            this.tag = (by)nbt.b();
        }
    }
    
    public FluidStack(final FluidStack stack, final int amount) {
        this(stack.fluidID, amount, stack.tag);
    }
    
    public static FluidStack loadFluidStackFromNBT(final by nbt) {
        if (nbt == null) {
            return null;
        }
        String fluidName = nbt.i("FluidName");
        if (fluidName == null) {
            fluidName = (nbt.b("LiquidName") ? nbt.i("LiquidName").toLowerCase(Locale.ENGLISH) : null);
            fluidName = Fluid.convertLegacyName(fluidName);
        }
        if (fluidName == null || FluidRegistry.getFluid(fluidName) == null) {
            return null;
        }
        final FluidStack stack = new FluidStack(FluidRegistry.getFluidID(fluidName), nbt.e("Amount"));
        if (nbt.b("Tag")) {
            stack.tag = nbt.l("Tag");
        }
        else if (nbt.b("extra")) {
            stack.tag = nbt.l("extra");
        }
        return stack;
    }
    
    public by writeToNBT(final by nbt) {
        nbt.a("FluidName", FluidRegistry.getFluidName(this.fluidID));
        nbt.a("Amount", this.amount);
        if (this.tag != null) {
            nbt.a("Tag", (cl)this.tag);
        }
        return nbt;
    }
    
    public final Fluid getFluid() {
        return FluidRegistry.getFluid(this.fluidID);
    }
    
    public FluidStack copy() {
        return new FluidStack(this.fluidID, this.amount, this.tag);
    }
    
    public boolean isFluidEqual(final FluidStack other) {
        return other != null && this.fluidID == other.fluidID && this.isFluidStackTagEqual(other);
    }
    
    private boolean isFluidStackTagEqual(final FluidStack other) {
        return (this.tag == null) ? (other.tag == null) : (other.tag != null && this.tag.equals((Object)other.tag));
    }
    
    public static boolean areFluidStackTagsEqual(final FluidStack stack1, final FluidStack stack2) {
        return (stack1 == null && stack2 == null) || (stack1 != null && stack2 != null && stack1.isFluidStackTagEqual(stack2));
    }
    
    public boolean containsFluid(final FluidStack other) {
        return this.isFluidEqual(other) && this.amount >= other.amount;
    }
    
    public boolean isFluidStackIdentical(final FluidStack other) {
        return this.isFluidEqual(other) && this.amount == other.amount;
    }
    
    public boolean isFluidEqual(final ye other) {
        if (other == null) {
            return false;
        }
        if (other.b() instanceof IFluidContainerItem) {
            return this.isFluidEqual(((IFluidContainerItem)other.b()).getFluid(other));
        }
        return this.isFluidEqual(FluidContainerRegistry.getFluidForFilledItem(other));
    }
    
    @Override
    public final int hashCode() {
        return this.fluidID;
    }
    
    @Override
    public final boolean equals(final Object o) {
        return o instanceof FluidStack && this.isFluidEqual((FluidStack)o);
    }
}
