// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

public class FluidTank implements IFluidTank
{
    protected FluidStack fluid;
    protected int capacity;
    protected asp tile;
    
    public FluidTank(final int capacity) {
        this(null, capacity);
    }
    
    public FluidTank(final FluidStack stack, final int capacity) {
        this.fluid = stack;
        this.capacity = capacity;
    }
    
    public FluidTank(final Fluid fluid, final int amount, final int capacity) {
        this(new FluidStack(fluid, amount), capacity);
    }
    
    public FluidTank readFromNBT(final by nbt) {
        if (!nbt.b("Empty")) {
            final FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
            if (fluid != null) {
                this.setFluid(fluid);
            }
        }
        return this;
    }
    
    public by writeToNBT(final by nbt) {
        if (this.fluid != null) {
            this.fluid.writeToNBT(nbt);
        }
        else {
            nbt.a("Empty", "");
        }
        return nbt;
    }
    
    public void setFluid(final FluidStack fluid) {
        this.fluid = fluid;
    }
    
    public void setCapacity(final int capacity) {
        this.capacity = capacity;
    }
    
    @Override
    public FluidStack getFluid() {
        return this.fluid;
    }
    
    @Override
    public int getFluidAmount() {
        if (this.fluid == null) {
            return 0;
        }
        return this.fluid.amount;
    }
    
    @Override
    public int getCapacity() {
        return this.capacity;
    }
    
    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }
    
    @Override
    public int fill(final FluidStack resource, final boolean doFill) {
        if (resource == null) {
            return 0;
        }
        if (!doFill) {
            if (this.fluid == null) {
                return Math.min(this.capacity, resource.amount);
            }
            if (!this.fluid.isFluidEqual(resource)) {
                return 0;
            }
            return Math.min(this.capacity - this.fluid.amount, resource.amount);
        }
        else {
            if (this.fluid == null) {
                this.fluid = new FluidStack(resource, Math.min(this.capacity, resource.amount));
                if (this.tile != null) {
                    FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(this.fluid, this.tile.k, this.tile.l, this.tile.m, this.tile.n, this));
                }
                return this.fluid.amount;
            }
            if (!this.fluid.isFluidEqual(resource)) {
                return 0;
            }
            int filled = this.capacity - this.fluid.amount;
            if (resource.amount < filled) {
                final FluidStack fluid = this.fluid;
                fluid.amount += resource.amount;
                filled = resource.amount;
            }
            else {
                this.fluid.amount = this.capacity;
            }
            if (this.tile != null) {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(this.fluid, this.tile.k, this.tile.l, this.tile.m, this.tile.n, this));
            }
            return filled;
        }
    }
    
    @Override
    public FluidStack drain(final int maxDrain, final boolean doDrain) {
        if (this.fluid == null) {
            return null;
        }
        int drained = maxDrain;
        if (this.fluid.amount < drained) {
            drained = this.fluid.amount;
        }
        final FluidStack stack = new FluidStack(this.fluid, drained);
        if (doDrain) {
            final FluidStack fluid = this.fluid;
            fluid.amount -= drained;
            if (this.fluid.amount <= 0) {
                this.fluid = null;
            }
            if (this.tile != null) {
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(this.fluid, this.tile.k, this.tile.l, this.tile.m, this.tile.n, this));
            }
        }
        return stack;
    }
}
