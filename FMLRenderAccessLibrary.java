import cpw.mods.fml.client.registry.RenderingRegistry;
import java.util.logging.Level;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Logger;

// 
// Decompiled by Procyon v0.6.0
// 

public class FMLRenderAccessLibrary
{
    public static Logger getLogger() {
        final Logger l = Logger.getLogger("FMLRenderAccessLibrary");
        l.setParent(FMLLog.getLogger());
        return l;
    }
    
    public static void log(final Level level, final String message) {
        FMLLog.log("FMLRenderAccessLibrary", level, message, new Object[0]);
    }
    
    public static void log(final Level level, final String message, final Throwable throwable) {
        FMLLog.log(level, throwable, message, new Object[0]);
    }
    
    public static boolean renderWorldBlock(final bfr renderer, final acf world, final int x, final int y, final int z, final aqz block, final int modelId) {
        return RenderingRegistry.instance().renderWorldBlock(renderer, world, x, y, z, block, modelId);
    }
    
    public static void renderInventoryBlock(final bfr renderer, final aqz block, final int metadata, final int modelID) {
        RenderingRegistry.instance().renderInventoryBlock(renderer, block, metadata, modelID);
    }
    
    public static boolean renderItemAsFull3DBlock(final int modelId) {
        return RenderingRegistry.instance().renderItemAsFull3DBlock(modelId);
    }
}
