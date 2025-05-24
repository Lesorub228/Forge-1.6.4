// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model;

import java.net.URL;

public interface IModelCustomLoader
{
    String getType();
    
    String[] getSuffixes();
    
    IModelCustom loadInstance(final String p0, final URL p1) throws ModelFormatException;
}
