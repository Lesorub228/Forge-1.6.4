// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;

public abstract class BlockFluidBase extends aqz implements IFluidBlock
{
    protected static final Map<Integer, Boolean> defaultDisplacementIds;
    protected Map<Integer, Boolean> displacementIds;
    protected int quantaPerBlock;
    protected float quantaPerBlockFloat;
    protected int density;
    protected int densityDir;
    protected int temperature;
    protected int tickRate;
    protected int renderPass;
    protected int maxScaledLight;
    protected final String fluidName;
    
    public BlockFluidBase(final int id, final Fluid fluid, final akc material) {
        super(id, material);
        this.displacementIds = new HashMap<Integer, Boolean>();
        this.quantaPerBlock = 8;
        this.quantaPerBlockFloat = 8.0f;
        this.density = 1;
        this.densityDir = -1;
        this.temperature = 295;
        this.tickRate = 20;
        this.renderPass = 1;
        this.maxScaledLight = 0;
        this.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        this.b(true);
        this.C();
        this.fluidName = fluid.getName();
        this.density = fluid.density;
        this.temperature = fluid.temperature;
        this.maxScaledLight = fluid.luminosity;
        this.tickRate = fluid.viscosity / 200;
        this.densityDir = ((fluid.density > 0) ? -1 : 1);
        fluid.setBlockID(id);
        this.displacementIds.putAll(BlockFluidBase.defaultDisplacementIds);
    }
    
    public BlockFluidBase setQuantaPerBlock(int quantaPerBlock) {
        if (quantaPerBlock > 16 || quantaPerBlock < 1) {
            quantaPerBlock = 8;
        }
        this.quantaPerBlock = quantaPerBlock;
        this.quantaPerBlockFloat = (float)quantaPerBlock;
        return this;
    }
    
    public BlockFluidBase setDensity(int density) {
        if (density == 0) {
            density = 1;
        }
        this.density = density;
        this.densityDir = ((density > 0) ? -1 : 1);
        return this;
    }
    
    public BlockFluidBase setTemperature(final int temperature) {
        this.temperature = temperature;
        return this;
    }
    
    public BlockFluidBase setTickRate(int tickRate) {
        if (tickRate <= 0) {
            tickRate = 20;
        }
        this.tickRate = tickRate;
        return this;
    }
    
    public BlockFluidBase setRenderPass(final int renderPass) {
        this.renderPass = renderPass;
        return this;
    }
    
    public BlockFluidBase setMaxScaledLight(final int maxScaledLight) {
        this.maxScaledLight = maxScaledLight;
        return this;
    }
    
    public boolean canDisplace(final acf world, final int x, final int y, final int z) {
        if (world.c(x, y, z)) {
            return true;
        }
        final int bId = world.a(x, y, z);
        if (bId == this.cF) {
            return false;
        }
        if (this.displacementIds.containsKey(bId)) {
            return this.displacementIds.get(bId);
        }
        final akc material = aqz.s[bId].cU;
        if (material.c() || material == akc.D) {
            return false;
        }
        final int density = getDensity(world, x, y, z);
        return density == Integer.MAX_VALUE || this.density > density;
    }
    
    public boolean displaceIfPossible(final abw world, final int x, final int y, final int z) {
        if (world.c(x, y, z)) {
            return true;
        }
        final int bId = world.a(x, y, z);
        if (bId == this.cF) {
            return false;
        }
        if (this.displacementIds.containsKey(bId)) {
            if (this.displacementIds.get(bId)) {
                aqz.s[bId].c(world, x, y, z, world.h(x, y, z), 0);
                return true;
            }
            return false;
        }
        else {
            final akc material = aqz.s[bId].cU;
            if (material.c() || material == akc.D) {
                return false;
            }
            final int density = getDensity((acf)world, x, y, z);
            if (density == Integer.MAX_VALUE) {
                aqz.s[bId].c(world, x, y, z, world.h(x, y, z), 0);
                return true;
            }
            return this.density > density;
        }
    }
    
    public abstract int getQuantaValue(final acf p0, final int p1, final int p2, final int p3);
    
    public abstract boolean a(final int p0, final boolean p1);
    
    public abstract int getMaxRenderHeightMeta();
    
    public void a(final abw world, final int x, final int y, final int z) {
        world.a(x, y, z, this.cF, this.tickRate);
    }
    
    public void a(final abw world, final int x, final int y, final int z, final int blockId) {
        world.a(x, y, z, this.cF, this.tickRate);
    }
    
    public boolean l() {
        return false;
    }
    
    public boolean b(final acf world, final int x, final int y, final int z) {
        return true;
    }
    
    public asx b(final abw world, final int x, final int y, final int z) {
        return null;
    }
    
    public int a(final int par1, final Random par2Random, final int par3) {
        return 0;
    }
    
    public int a(final Random par1Random) {
        return 0;
    }
    
    public int a(final abw world) {
        return this.tickRate;
    }
    
    public void a(final abw world, final int x, final int y, final int z, final nn entity, final atc vec) {
        if (this.densityDir > 0) {
            return;
        }
        final atc vec_flow = this.getFlowVector((acf)world, x, y, z);
        vec.c += vec_flow.c * (this.quantaPerBlock * 4);
        vec.d += vec_flow.d * (this.quantaPerBlock * 4);
        vec.e += vec_flow.e * (this.quantaPerBlock * 4);
    }
    
    public int getLightValue(final acf world, final int x, final int y, final int z) {
        if (this.maxScaledLight == 0) {
            return super.getLightValue(world, x, y, z);
        }
        final int data = world.h(x, y, z);
        return (int)(data / this.quantaPerBlockFloat * this.maxScaledLight);
    }
    
    public int d() {
        return FluidRegistry.renderIdFluid;
    }
    
    public boolean c() {
        return false;
    }
    
    public boolean b() {
        return false;
    }
    
    public float f(final acf world, final int x, final int y, final int z) {
        final float lightThis = world.q(x, y, z);
        final float lightUp = world.q(x, y + 1, z);
        return (lightThis > lightUp) ? lightThis : lightUp;
    }
    
    public int e(final acf world, final int x, final int y, final int z) {
        final int lightThis = world.h(x, y, z, 0);
        final int lightUp = world.h(x, y + 1, z, 0);
        final int lightThisBase = lightThis & 0xFF;
        final int lightUpBase = lightUp & 0xFF;
        final int lightThisExt = lightThis >> 16 & 0xFF;
        final int lightUpExt = lightUp >> 16 & 0xFF;
        return ((lightThisBase > lightUpBase) ? lightThisBase : lightUpBase) | ((lightThisExt > lightUpExt) ? lightThisExt : lightUpExt) << 16;
    }
    
    public int n() {
        return this.renderPass;
    }
    
    public boolean a(final acf world, final int x, final int y, final int z, final int side) {
        if (world.a(x, y, z) != this.cF) {
            return !world.t(x, y, z);
        }
        final akc mat = world.g(x, y, z);
        return mat != this.cU && super.a(world, x, y, z, side);
    }
    
    public static final int getDensity(final acf world, final int x, final int y, final int z) {
        final aqz block = aqz.s[world.a(x, y, z)];
        if (!(block instanceof BlockFluidBase)) {
            return Integer.MAX_VALUE;
        }
        return ((BlockFluidBase)block).density;
    }
    
    public static final int getTemperature(final acf world, final int x, final int y, final int z) {
        final aqz block = aqz.s[world.a(x, y, z)];
        if (!(block instanceof BlockFluidBase)) {
            return Integer.MAX_VALUE;
        }
        return ((BlockFluidBase)block).temperature;
    }
    
    public static double getFlowDirection(final acf world, final int x, final int y, final int z) {
        final aqz block = aqz.s[world.a(x, y, z)];
        if (!world.g(x, y, z).d()) {
            return -1000.0;
        }
        final atc vec = ((BlockFluidBase)block).getFlowVector(world, x, y, z);
        return (vec.c == 0.0 && vec.e == 0.0) ? -1000.0 : (Math.atan2(vec.e, vec.c) - 1.5707963267948966);
    }
    
    public final int getQuantaValueBelow(final acf world, final int x, final int y, final int z, final int belowThis) {
        final int quantaRemaining = this.getQuantaValue(world, x, y, z);
        if (quantaRemaining >= belowThis) {
            return -1;
        }
        return quantaRemaining;
    }
    
    public final int getQuantaValueAbove(final acf world, final int x, final int y, final int z, final int aboveThis) {
        final int quantaRemaining = this.getQuantaValue(world, x, y, z);
        if (quantaRemaining <= aboveThis) {
            return -1;
        }
        return quantaRemaining;
    }
    
    public final float getQuantaPercentage(final acf world, final int x, final int y, final int z) {
        final int quantaRemaining = this.getQuantaValue(world, x, y, z);
        return quantaRemaining / this.quantaPerBlockFloat;
    }
    
    public atc getFlowVector(final acf world, final int x, final int y, final int z) {
        atc vec = world.V().a(0.0, 0.0, 0.0);
        final int decay = this.quantaPerBlock - this.getQuantaValue(world, x, y, z);
        for (int side = 0; side < 4; ++side) {
            int x2 = x;
            int z2 = z;
            switch (side) {
                case 0: {
                    --x2;
                    break;
                }
                case 1: {
                    --z2;
                    break;
                }
                case 2: {
                    ++x2;
                    break;
                }
                case 3: {
                    ++z2;
                    break;
                }
            }
            int otherDecay = this.quantaPerBlock - this.getQuantaValue(world, x2, y, z2);
            if (otherDecay >= this.quantaPerBlock) {
                if (!world.g(x2, y, z2).c()) {
                    otherDecay = this.quantaPerBlock - this.getQuantaValue(world, x2, y - 1, z2);
                    if (otherDecay >= 0) {
                        final int power = otherDecay - (decay - this.quantaPerBlock);
                        vec = vec.c((double)((x2 - x) * power), (double)((y - y) * power), (double)((z2 - z) * power));
                    }
                }
            }
            else if (otherDecay >= 0) {
                final int power = otherDecay - decay;
                vec = vec.c((double)((x2 - x) * power), (double)((y - y) * power), (double)((z2 - z) * power));
            }
        }
        if (world.a(x, y + 1, z) == this.cF) {
            final boolean flag = this.a_(world, x, y, z - 1, 2) || this.a_(world, x, y, z + 1, 3) || this.a_(world, x - 1, y, z, 4) || this.a_(world, x + 1, y, z, 5) || this.a_(world, x, y + 1, z - 1, 2) || this.a_(world, x, y + 1, z + 1, 3) || this.a_(world, x - 1, y + 1, z, 4) || this.a_(world, x + 1, y + 1, z, 5);
            if (flag) {
                vec = vec.a().c(0.0, -6.0, 0.0);
            }
        }
        vec = vec.a();
        return vec;
    }
    
    public Fluid getFluid() {
        return FluidRegistry.getFluid(this.fluidName);
    }
    
    public float getFilledPercentage(final abw world, final int x, final int y, final int z) {
        final int quantaRemaining = this.getQuantaValue((acf)world, x, y, z) + 1;
        float remaining = quantaRemaining / this.quantaPerBlockFloat;
        if (remaining > 1.0f) {
            remaining = 1.0f;
        }
        return remaining * ((this.density > 0) ? 1 : -1);
    }
    
    static {
        (defaultDisplacementIds = new HashMap<Integer, Boolean>()).put(aqz.aJ.cF, false);
        BlockFluidBase.defaultDisplacementIds.put(aqz.aQ.cF, false);
        BlockFluidBase.defaultDisplacementIds.put(aqz.aI.cF, false);
        BlockFluidBase.defaultDisplacementIds.put(aqz.aN.cF, false);
        BlockFluidBase.defaultDisplacementIds.put(aqz.bc.cF, false);
    }
}
