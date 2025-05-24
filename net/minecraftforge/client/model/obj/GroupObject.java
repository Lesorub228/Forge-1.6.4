// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.obj;

import java.util.Iterator;
import java.util.ArrayList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GroupObject
{
    public String name;
    public ArrayList<Face> faces;
    public int glDrawingMode;
    
    public GroupObject() {
        this("");
    }
    
    public GroupObject(final String name) {
        this(name, -1);
    }
    
    public GroupObject(final String name, final int glDrawingMode) {
        this.faces = new ArrayList<Face>();
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }
    
    public void render() {
        if (this.faces.size() > 0) {
            final bfq tessellator = bfq.a;
            tessellator.b(this.glDrawingMode);
            this.render(tessellator);
            tessellator.a();
        }
    }
    
    public void render(final bfq tessellator) {
        if (this.faces.size() > 0) {
            for (final Face face : this.faces) {
                face.addFaceForRender(tessellator);
            }
        }
    }
}
