// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.fluids;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockFluid implements ISimpleBlockRenderingHandler
{
    public static RenderBlockFluid instance;
    static final float LIGHT_Y_NEG = 0.5f;
    static final float LIGHT_Y_POS = 1.0f;
    static final float LIGHT_XZ_NEG = 0.8f;
    static final float LIGHT_XZ_POS = 0.6f;
    static final double RENDER_OFFSET = 0.0010000000474974513;
    
    public float getFluidHeightAverage(final float[] flow) {
        float total = 0.0f;
        int count = 0;
        float end = 0.0f;
        for (int i = 0; i < flow.length; ++i) {
            if (flow[i] >= 0.875f && end != 1.0f) {
                end = flow[i];
            }
            if (flow[i] >= 0.0f) {
                total += flow[i];
                ++count;
            }
        }
        if (end == 0.0f) {
            end = total / count;
        }
        return end;
    }
    
    public float getFluidHeightForRender(final acf world, final int x, final int y, final int z, final BlockFluidBase block) {
        if (world.a(x, y, z) == block.cF) {
            if (world.g(x, y - block.densityDir, z).d()) {
                return 1.0f;
            }
            if (world.h(x, y, z) == block.getMaxRenderHeightMeta()) {
                return 0.875f;
            }
        }
        return (!world.g(x, y, z).a() && world.a(x, y - block.densityDir, z) == block.cF) ? 1.0f : (block.getQuantaPercentage(world, x, y, z) * 0.875f);
    }
    
    @Override
    public void renderInventoryBlock(final aqz block, final int metadata, final int modelID, final bfr renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(final acf world, final int x, final int y, final int z, final aqz block, final int modelId, final bfr renderer) {
        if (!(block instanceof BlockFluidBase)) {
            return false;
        }
        final bfq tessellator = bfq.a;
        final int color = block.c(world, x, y, z);
        final float red = (color >> 16 & 0xFF) / 255.0f;
        final float green = (color >> 8 & 0xFF) / 255.0f;
        final float blue = (color & 0xFF) / 255.0f;
        final BlockFluidBase theFluid = (BlockFluidBase)block;
        final int bMeta = world.h(x, y, z);
        final boolean renderTop = world.a(x, y - theFluid.densityDir, z) != theFluid.cF;
        final boolean renderBottom = block.a(world, x, y + theFluid.densityDir, z, 0) && world.a(x, y + theFluid.densityDir, z) != theFluid.cF;
        final boolean[] renderSides = { block.a(world, x, y, z - 1, 2), block.a(world, x, y, z + 1, 3), block.a(world, x - 1, y, z, 4), block.a(world, x + 1, y, z, 5) };
        if (!renderTop && !renderBottom && !renderSides[0] && !renderSides[1] && !renderSides[2] && !renderSides[3]) {
            return false;
        }
        boolean rendered = false;
        final float flow11 = this.getFluidHeightForRender(world, x, y, z, theFluid);
        double heightNW;
        double heightSW;
        double heightSE;
        double heightNE;
        if (flow11 != 1.0f) {
            final float flow12 = this.getFluidHeightForRender(world, x - 1, y, z - 1, theFluid);
            final float flow13 = this.getFluidHeightForRender(world, x - 1, y, z, theFluid);
            final float flow14 = this.getFluidHeightForRender(world, x - 1, y, z + 1, theFluid);
            final float flow15 = this.getFluidHeightForRender(world, x, y, z - 1, theFluid);
            final float flow16 = this.getFluidHeightForRender(world, x, y, z + 1, theFluid);
            final float flow17 = this.getFluidHeightForRender(world, x + 1, y, z - 1, theFluid);
            final float flow18 = this.getFluidHeightForRender(world, x + 1, y, z, theFluid);
            final float flow19 = this.getFluidHeightForRender(world, x + 1, y, z + 1, theFluid);
            heightNW = this.getFluidHeightAverage(new float[] { flow12, flow13, flow15, flow11 });
            heightSW = this.getFluidHeightAverage(new float[] { flow13, flow14, flow16, flow11 });
            heightSE = this.getFluidHeightAverage(new float[] { flow16, flow18, flow19, flow11 });
            heightNE = this.getFluidHeightAverage(new float[] { flow15, flow17, flow18, flow11 });
        }
        else {
            heightNW = flow11;
            heightSW = flow11;
            heightSE = flow11;
            heightNE = flow11;
        }
        final boolean rises = theFluid.densityDir == 1;
        if (renderer.f || renderTop) {
            rendered = true;
            ms iconStill = block.a(1, bMeta);
            final float flowDir = (float)BlockFluidBase.getFlowDirection(world, x, y, z);
            if (flowDir > -999.0f) {
                iconStill = block.a(2, bMeta);
            }
            heightNW -= 0.0010000000474974513;
            heightSW -= 0.0010000000474974513;
            heightSE -= 0.0010000000474974513;
            heightNE -= 0.0010000000474974513;
            double u2;
            double v2;
            double u3;
            double v3;
            double u4;
            double v4;
            double u5;
            double v5;
            if (flowDir < -999.0f) {
                u2 = iconStill.a(0.0);
                v2 = iconStill.b(0.0);
                u3 = u2;
                v3 = iconStill.b(16.0);
                u4 = iconStill.a(16.0);
                v4 = v3;
                u5 = u4;
                v5 = v2;
            }
            else {
                final float xFlow = ls.a(flowDir) * 0.25f;
                final float zFlow = ls.b(flowDir) * 0.25f;
                u2 = iconStill.a((double)(8.0f + (-zFlow - xFlow) * 16.0f));
                v2 = iconStill.b((double)(8.0f + (-zFlow + xFlow) * 16.0f));
                u3 = iconStill.a((double)(8.0f + (-zFlow + xFlow) * 16.0f));
                v3 = iconStill.b((double)(8.0f + (zFlow + xFlow) * 16.0f));
                u4 = iconStill.a((double)(8.0f + (zFlow + xFlow) * 16.0f));
                v4 = iconStill.b((double)(8.0f + (zFlow - xFlow) * 16.0f));
                u5 = iconStill.a((double)(8.0f + (zFlow - xFlow) * 16.0f));
                v5 = iconStill.b((double)(8.0f + (-zFlow - xFlow) * 16.0f));
            }
            tessellator.c(block.e(world, x, y, z));
            tessellator.a(1.0f * red, 1.0f * green, 1.0f * blue);
            if (!rises) {
                tessellator.a((double)(x + 0), y + heightNW, (double)(z + 0), u2, v2);
                tessellator.a((double)(x + 0), y + heightSW, (double)(z + 1), u3, v3);
                tessellator.a((double)(x + 1), y + heightSE, (double)(z + 1), u4, v4);
                tessellator.a((double)(x + 1), y + heightNE, (double)(z + 0), u5, v5);
            }
            else {
                tessellator.a((double)(x + 1), y + 1 - heightNE, (double)(z + 0), u5, v5);
                tessellator.a((double)(x + 1), y + 1 - heightSE, (double)(z + 1), u4, v4);
                tessellator.a((double)(x + 0), y + 1 - heightSW, (double)(z + 1), u3, v3);
                tessellator.a((double)(x + 0), y + 1 - heightNW, (double)(z + 0), u2, v2);
            }
        }
        if (renderer.f || renderBottom) {
            rendered = true;
            tessellator.c(block.e(world, x, y - 1, z));
            if (!rises) {
                tessellator.a(0.5f * red, 0.5f * green, 0.5f * blue);
                renderer.a(block, (double)x, y + 0.0010000000474974513, (double)z, block.a(0, bMeta));
            }
            else {
                tessellator.a(1.0f * red, 1.0f * green, 1.0f * blue);
                renderer.b(block, (double)x, y + 0.0010000000474974513, (double)z, block.a(1, bMeta));
            }
        }
        for (int side = 0; side < 4; ++side) {
            int x2 = x;
            int z2 = z;
            switch (side) {
                case 0: {
                    --z2;
                    break;
                }
                case 1: {
                    ++z2;
                    break;
                }
                case 2: {
                    --x2;
                    break;
                }
                case 3: {
                    ++x2;
                    break;
                }
            }
            final ms iconFlow = block.a(side + 2, bMeta);
            if (renderer.f || renderSides[side]) {
                rendered = true;
                double ty1;
                double ty2;
                double tx1;
                double tx2;
                double tz1;
                double tz2;
                if (side == 0) {
                    ty1 = heightNW;
                    ty2 = heightNE;
                    tx1 = x;
                    tx2 = x + 1;
                    tz1 = z + 0.0010000000474974513;
                    tz2 = z + 0.0010000000474974513;
                }
                else if (side == 1) {
                    ty1 = heightSE;
                    ty2 = heightSW;
                    tx1 = x + 1;
                    tx2 = x;
                    tz1 = z + 1 - 0.0010000000474974513;
                    tz2 = z + 1 - 0.0010000000474974513;
                }
                else if (side == 2) {
                    ty1 = heightSW;
                    ty2 = heightNW;
                    tx1 = x + 0.0010000000474974513;
                    tx2 = x + 0.0010000000474974513;
                    tz1 = z + 1;
                    tz2 = z;
                }
                else {
                    ty1 = heightNE;
                    ty2 = heightSE;
                    tx1 = x + 1 - 0.0010000000474974513;
                    tx2 = x + 1 - 0.0010000000474974513;
                    tz1 = z;
                    tz2 = z + 1;
                }
                final float u1Flow = iconFlow.a(0.0);
                final float u2Flow = iconFlow.a(8.0);
                final float v1Flow = iconFlow.b((1.0 - ty1) * 16.0 * 0.5);
                final float v2Flow = iconFlow.b((1.0 - ty2) * 16.0 * 0.5);
                final float v3Flow = iconFlow.b(8.0);
                tessellator.c(block.e(world, x2, y, z2));
                float sideLighting = 1.0f;
                if (side < 2) {
                    sideLighting = 0.8f;
                }
                else {
                    sideLighting = 0.6f;
                }
                tessellator.a(1.0f * sideLighting * red, 1.0f * sideLighting * green, 1.0f * sideLighting * blue);
                if (!rises) {
                    tessellator.a(tx1, y + ty1, tz1, (double)u1Flow, (double)v1Flow);
                    tessellator.a(tx2, y + ty2, tz2, (double)u2Flow, (double)v2Flow);
                    tessellator.a(tx2, (double)(y + 0), tz2, (double)u2Flow, (double)v3Flow);
                    tessellator.a(tx1, (double)(y + 0), tz1, (double)u1Flow, (double)v3Flow);
                }
                else {
                    tessellator.a(tx1, (double)(y + 1 - 0), tz1, (double)u1Flow, (double)v3Flow);
                    tessellator.a(tx2, (double)(y + 1 - 0), tz2, (double)u2Flow, (double)v3Flow);
                    tessellator.a(tx2, y + 1 - ty2, tz2, (double)u2Flow, (double)v2Flow);
                    tessellator.a(tx1, y + 1 - ty1, tz1, (double)u1Flow, (double)v1Flow);
                }
            }
        }
        renderer.i = 0.0;
        renderer.j = 1.0;
        return rendered;
    }
    
    @Override
    public boolean shouldRender3DInInventory() {
        return false;
    }
    
    @Override
    public int getRenderId() {
        return FluidRegistry.renderIdFluid;
    }
    
    static {
        RenderBlockFluid.instance = new RenderBlockFluid();
    }
}
