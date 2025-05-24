// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.techne;

import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.IModelCustom;
import java.net.URL;
import net.minecraftforge.client.model.IModelCustomLoader;

public class TechneModelLoader implements IModelCustomLoader
{
    private static final String[] types;
    
    @Override
    public String getType() {
        return "Techne model";
    }
    
    @Override
    public String[] getSuffixes() {
        return TechneModelLoader.types;
    }
    
    @Override
    public IModelCustom loadInstance(final String resourceName, final URL resource) throws ModelFormatException {
        return new TechneModel(resourceName, resource);
    }
    
    static {
        types = new String[] { "tcn" };
    }
}
