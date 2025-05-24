// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import net.minecraftforge.common.ForgeDirection;

public class TileFluidHandler extends asp implements IFluidHandler
{
    protected FluidTank tank;
    
    public TileFluidHandler() {
        this.tank = new FluidTank(1000);
    }
    
    public void a(final by tag) {
        super.a(tag);
        this.tank.writeToNBT(tag);
    }
    
    public void b(final by tag) {
        super.b(tag);
        this.tank.readFromNBT(tag);
    }
    
    public int fill(final ForgeDirection from, final FluidStack resource, final boolean doFill) {
        return this.tank.fill(resource, doFill);
    }
    
    public FluidStack drain(final ForgeDirection from, final FluidStack resource, final boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.tank.getFluid())) {
            return null;
        }
        return this.tank.drain(resource.amount, doDrain);
    }
    
    public FluidStack drain(final ForgeDirection from, final int maxDrain, final boolean doDrain) {
        return this.tank.drain(maxDrain, doDrain);
    }
    
    public boolean canFill(final ForgeDirection from, final Fluid fluid) {
        return true;
    }
    
    public boolean canDrain(final ForgeDirection from, final Fluid fluid) {
        return true;
    }
    
    public FluidTankInfo[] getTankInfo(final ForgeDirection from) {
        return new FluidTankInfo[] { this.tank.getInfo() };
    }
}
