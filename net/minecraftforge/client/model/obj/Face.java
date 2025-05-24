// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.obj;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Face
{
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;
    
    public void addFaceForRender(final bfq tessellator) {
        this.addFaceForRender(tessellator, 5.0E-4f);
    }
    
    public void addFaceForRender(final bfq tessellator, final float textureOffset) {
        if (this.faceNormal == null) {
            this.faceNormal = this.calculateFaceNormal();
        }
        tessellator.b(this.faceNormal.x, this.faceNormal.y, this.faceNormal.z);
        float averageU = 0.0f;
        float averageV = 0.0f;
        if (this.textureCoordinates != null && this.textureCoordinates.length > 0) {
            for (int i = 0; i < this.textureCoordinates.length; ++i) {
                averageU += this.textureCoordinates[i].u;
                averageV += this.textureCoordinates[i].v;
            }
            averageU /= this.textureCoordinates.length;
            averageV /= this.textureCoordinates.length;
        }
        for (int j = 0; j < this.vertices.length; ++j) {
            if (this.textureCoordinates != null && this.textureCoordinates.length > 0) {
                float offsetU = textureOffset;
                float offsetV = textureOffset;
                if (this.textureCoordinates[j].u > averageU) {
                    offsetU = -offsetU;
                }
                if (this.textureCoordinates[j].v > averageV) {
                    offsetV = -offsetV;
                }
                tessellator.a((double)this.vertices[j].x, (double)this.vertices[j].y, (double)this.vertices[j].z, (double)(this.textureCoordinates[j].u + offsetU), (double)(this.textureCoordinates[j].v + offsetV));
            }
            else {
                tessellator.a((double)this.vertices[j].x, (double)this.vertices[j].y, (double)this.vertices[j].z);
            }
        }
    }
    
    public Vertex calculateFaceNormal() {
        final atc v1 = atc.a((double)(this.vertices[1].x - this.vertices[0].x), (double)(this.vertices[1].y - this.vertices[0].y), (double)(this.vertices[1].z - this.vertices[0].z));
        final atc v2 = atc.a((double)(this.vertices[2].x - this.vertices[0].x), (double)(this.vertices[2].y - this.vertices[0].y), (double)(this.vertices[2].z - this.vertices[0].z));
        atc normalVector = null;
        normalVector = v1.c(v2).a();
        return new Vertex((float)normalVector.c, (float)normalVector.d, (float)normalVector.e);
    }
}
