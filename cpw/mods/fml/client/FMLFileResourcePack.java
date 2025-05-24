// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLLog;
import java.util.logging.Level;
import java.io.InputStream;
import cpw.mods.fml.common.ModContainer;

public class FMLFileResourcePack extends bji
{
    private ModContainer container;
    
    public FMLFileResourcePack(final ModContainer container) {
        super(container.getSource());
        this.container = container;
    }
    
    public String b() {
        return "FMLFileResourcePack:" + this.container.getName();
    }
    
    protected InputStream a(final String resourceName) throws IOException {
        try {
            return super.a(resourceName);
        }
        catch (final IOException ioe) {
            if ("pack.mcmeta".equals(resourceName)) {
                FMLLog.log(this.container.getName(), Level.WARNING, "Mod %s is missing a pack.mcmeta file, things may not work well", this.container.getName());
                return new ByteArrayInputStream(("{\n \"pack\": {\n   \"description\": \"dummy FML pack for " + this.container.getName() + "\",\n" + "   \"pack_format\": 1\n" + "}\n" + "}").getBytes(Charsets.UTF_8));
            }
            throw ioe;
        }
    }
    
    public BufferedImage a() throws IOException {
        return ImageIO.read(this.a(this.container.getMetadata().logoFile));
    }
}
