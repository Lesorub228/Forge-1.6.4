// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import com.google.common.collect.Maps;
import java.util.Map;

public class TextureFXManager
{
    private static final TextureFXManager INSTANCE;
    private atv client;
    private Map<Integer, TextureHolder> texturesById;
    private Map<String, TextureHolder> texturesByName;
    
    public TextureFXManager() {
        this.texturesById = Maps.newHashMap();
        this.texturesByName = Maps.newHashMap();
    }
    
    void setClient(final atv client) {
        this.client = client;
    }
    
    public static TextureFXManager instance() {
        return TextureFXManager.INSTANCE;
    }
    
    public void fixTransparency(final BufferedImage loadedImage, final String textureName) {
        if (textureName.matches("^/mob/.*_eyes.*.png$")) {
            for (int x = 0; x < loadedImage.getWidth(); ++x) {
                for (int y = 0; y < loadedImage.getHeight(); ++y) {
                    final int argb = loadedImage.getRGB(x, y);
                    if ((argb & 0xFF000000) == 0x0 && argb != 0) {
                        loadedImage.setRGB(x, y, 0);
                    }
                }
            }
        }
    }
    
    public void bindTextureToName(final String name, final int index) {
        final TextureHolder holder = new TextureHolder();
        holder.textureId = index;
        holder.textureName = name;
        this.texturesById.put(index, holder);
        this.texturesByName.put(name, holder);
    }
    
    public void setTextureDimensions(final int index, final int j, final int k) {
        final TextureHolder holder = this.texturesById.get(index);
        if (holder == null) {
            return;
        }
        holder.x = j;
        holder.y = k;
    }
    
    public Dimension getTextureDimensions(final String texture) {
        return this.texturesByName.containsKey(texture) ? new Dimension(this.texturesByName.get(texture).x, this.texturesByName.get(texture).y) : new Dimension(1, 1);
    }
    
    static {
        INSTANCE = new TextureFXManager();
    }
    
    private class TextureHolder
    {
        private int textureId;
        private String textureName;
        private int x;
        private int y;
    }
}
