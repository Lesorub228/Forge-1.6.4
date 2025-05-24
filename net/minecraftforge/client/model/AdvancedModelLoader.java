// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model;

import net.minecraftforge.client.model.techne.TechneModelLoader;
import net.minecraftforge.client.model.obj.ObjModelLoader;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.net.URL;
import cpw.mods.fml.common.FMLLog;
import java.util.Map;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedModelLoader
{
    private static Map<String, IModelCustomLoader> instances;
    
    public static void registerModelHandler(final IModelCustomLoader modelHandler) {
        for (final String suffix : modelHandler.getSuffixes()) {
            AdvancedModelLoader.instances.put(suffix, modelHandler);
        }
    }
    
    public static IModelCustom loadModel(final String resourceName) throws IllegalArgumentException, ModelFormatException {
        final int i = resourceName.lastIndexOf(46);
        if (i == -1) {
            FMLLog.severe("The resource name %s is not valid", resourceName);
            throw new IllegalArgumentException("The resource name is not valid");
        }
        final String suffix = resourceName.substring(i + 1);
        final IModelCustomLoader loader = AdvancedModelLoader.instances.get(suffix);
        if (loader == null) {
            FMLLog.severe("The resource name %s is not supported", resourceName);
            throw new IllegalArgumentException("The resource name is not supported");
        }
        final URL resource = AdvancedModelLoader.class.getResource(resourceName);
        if (resource == null) {
            FMLLog.severe("The resource name %s could not be found", resourceName);
            throw new IllegalArgumentException("The resource name could not be found");
        }
        return loader.loadInstance(resourceName, resource);
    }
    
    public static Collection<String> getSupportedSuffixes() {
        return AdvancedModelLoader.instances.keySet();
    }
    
    static {
        AdvancedModelLoader.instances = Maps.newHashMap();
        registerModelHandler(new ObjModelLoader());
        registerModelHandler(new TechneModelLoader());
    }
}
