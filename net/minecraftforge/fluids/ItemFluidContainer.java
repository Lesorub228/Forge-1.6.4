// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

public class ItemFluidContainer extends yc implements IFluidContainerItem
{
    protected int capacity;
    
    public ItemFluidContainer(final int itemID) {
        super(itemID);
    }
    
    public ItemFluidContainer(final int itemID, final int capacity) {
        super(itemID);
        this.capacity = capacity;
    }
    
    public ItemFluidContainer setCapacity(final int capacity) {
        this.capacity = capacity;
        return this;
    }
    
    public FluidStack getFluid(final ye container) {
        if (container.e == null || !container.e.b("Fluid")) {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(container.e.l("Fluid"));
    }
    
    public int getCapacity(final ye container) {
        return this.capacity;
    }
    
    public int fill(final ye container, final FluidStack resource, final boolean doFill) {
        if (resource == null) {
            return 0;
        }
        if (!doFill) {
            if (container.e == null || !container.e.b("Fluid")) {
                return Math.min(this.capacity, resource.amount);
            }
            final FluidStack stack = FluidStack.loadFluidStackFromNBT(container.e.l("Fluid"));
            if (stack == null) {
                return Math.min(this.capacity, resource.amount);
            }
            if (!stack.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(this.capacity - stack.amount, resource.amount);
        }
        else {
            if (container.e == null) {
                container.e = new by();
            }
            if (!container.e.b("Fluid")) {
                final by fluidTag = resource.writeToNBT(new by());
                if (this.capacity < resource.amount) {
                    fluidTag.a("Amount", this.capacity);
                    container.e.a("Fluid", (cl)fluidTag);
                    return this.capacity;
                }
                container.e.a("Fluid", (cl)fluidTag);
                return resource.amount;
            }
            else {
                final by fluidTag = container.e.l("Fluid");
                final FluidStack stack2 = FluidStack.loadFluidStackFromNBT(fluidTag);
                if (!stack2.isFluidEqual(resource)) {
                    return 0;
                }
                int filled = this.capacity - stack2.amount;
                if (resource.amount < filled) {
                    final FluidStack fluidStack = stack2;
                    fluidStack.amount += resource.amount;
                    filled = resource.amount;
                }
                else {
                    stack2.amount = this.capacity;
                }
                container.e.a("Fluid", (cl)stack2.writeToNBT(fluidTag));
                return filled;
            }
        }
    }
    
    public FluidStack drain(final ye container, final int maxDrain, final boolean doDrain) {
        if (container.e == null || !container.e.b("Fluid") || maxDrain == 0) {
            return null;
        }
        final FluidStack stack = FluidStack.loadFluidStackFromNBT(container.e.l("Fluid"));
        if (stack == null) {
            return null;
        }
        final int drained = Math.min(stack.amount, maxDrain);
        if (doDrain) {
            if (maxDrain >= stack.amount) {
                container.e.o("Fluid");
                if (container.e.d()) {
                    container.e = null;
                }
                return stack;
            }
            final by fluidTag = container.e.l("Fluid");
            fluidTag.a("Amount", fluidTag.e("Amount") - maxDrain);
            container.e.a("Fluid", (cl)fluidTag);
        }
        stack.amount = drained;
        return stack;
    }
}
