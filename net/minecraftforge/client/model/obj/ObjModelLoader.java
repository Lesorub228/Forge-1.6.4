// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.obj;

import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.IModelCustom;
import java.net.URL;
import net.minecraftforge.client.model.IModelCustomLoader;

public class ObjModelLoader implements IModelCustomLoader
{
    private static final String[] types;
    
    @Override
    public String getType() {
        return "OBJ model";
    }
    
    @Override
    public String[] getSuffixes() {
        return ObjModelLoader.types;
    }
    
    @Override
    public IModelCustom loadInstance(final String resourceName, final URL resource) throws ModelFormatException {
        return new WavefrontObject(resourceName, resource);
    }
    
    static {
        types = new String[] { "obj" };
    }
}
