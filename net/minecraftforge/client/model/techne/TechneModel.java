// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.techne;

import java.util.Arrays;
import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.zip.ZipEntry;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.zip.ZipException;
import cpw.mods.fml.common.FMLLog;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.zip.ZipInputStream;
import net.minecraftforge.client.model.ModelFormatException;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.net.URL;
import java.util.Map;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class TechneModel extends bbo implements IModelCustom
{
    public static final List<String> cubeTypes;
    private String fileName;
    private Map<String, byte[]> zipContents;
    private Map<String, bcu> parts;
    private String texture;
    private int textureName;
    private boolean textureNameSet;
    
    public TechneModel(final String fileName, final URL resource) throws ModelFormatException {
        this.zipContents = new HashMap<String, byte[]>();
        this.parts = new LinkedHashMap<String, bcu>();
        this.texture = null;
        this.textureNameSet = false;
        this.fileName = fileName;
        this.loadTechneModel(resource);
    }
    
    private void loadTechneModel(final URL fileURL) throws ModelFormatException {
        try {
            final ZipInputStream zipInput = new ZipInputStream(fileURL.openStream());
            ZipEntry entry;
            while ((entry = zipInput.getNextEntry()) != null) {
                final byte[] data = new byte[(int)entry.getSize()];
                for (int i = 0; zipInput.available() > 0 && i < data.length; data[i++] = (byte)zipInput.read()) {}
                this.zipContents.put(entry.getName(), data);
            }
            final byte[] modelXml = this.zipContents.get("model.xml");
            if (modelXml == null) {
                throw new ModelFormatException("Model " + this.fileName + " contains no model.xml file");
            }
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            final Document document = documentBuilder.parse(new ByteArrayInputStream(modelXml));
            final NodeList nodeListTechne = document.getElementsByTagName("Techne");
            if (nodeListTechne.getLength() < 1) {
                throw new ModelFormatException("Model " + this.fileName + " contains no Techne tag");
            }
            final NodeList nodeListModel = document.getElementsByTagName("Model");
            if (nodeListModel.getLength() < 1) {
                throw new ModelFormatException("Model " + this.fileName + " contains no Model tag");
            }
            final NamedNodeMap modelAttributes = nodeListModel.item(0).getAttributes();
            if (modelAttributes == null) {
                throw new ModelFormatException("Model " + this.fileName + " contains a Model tag with no attributes");
            }
            final Node modelTexture = modelAttributes.getNamedItem("texture");
            if (modelTexture != null) {
                this.texture = modelTexture.getTextContent();
            }
            final NodeList shapes = document.getElementsByTagName("Shape");
            for (int j = 0; j < shapes.getLength(); ++j) {
                final Node shape = shapes.item(j);
                final NamedNodeMap shapeAttributes = shape.getAttributes();
                if (shapeAttributes == null) {
                    throw new ModelFormatException("Shape #" + (j + 1) + " in " + this.fileName + " has no attributes");
                }
                final Node name = shapeAttributes.getNamedItem("name");
                String shapeName = null;
                if (name != null) {
                    shapeName = name.getNodeValue();
                }
                if (shapeName == null) {
                    shapeName = "Shape #" + (j + 1);
                }
                String shapeType = null;
                final Node type = shapeAttributes.getNamedItem("type");
                if (type != null) {
                    shapeType = type.getNodeValue();
                }
                if (shapeType != null && !TechneModel.cubeTypes.contains(shapeType)) {
                    FMLLog.warning("Model shape [" + shapeName + "] in " + this.fileName + " is not a cube, ignoring", new Object[0]);
                }
                else {
                    try {
                        boolean mirrored = false;
                        String[] offset = new String[3];
                        String[] position = new String[3];
                        String[] rotation = new String[3];
                        String[] size = new String[3];
                        String[] textureOffset = new String[2];
                        final NodeList shapeChildren = shape.getChildNodes();
                        for (int k = 0; k < shapeChildren.getLength(); ++k) {
                            final Node shapeChild = shapeChildren.item(k);
                            final String shapeChildName = shapeChild.getNodeName();
                            String shapeChildValue = shapeChild.getTextContent();
                            if (shapeChildValue != null) {
                                shapeChildValue = shapeChildValue.trim();
                                if (shapeChildName.equals("IsMirrored")) {
                                    mirrored = !shapeChildValue.equals("False");
                                }
                                else if (shapeChildName.equals("Offset")) {
                                    offset = shapeChildValue.split(",");
                                }
                                else if (shapeChildName.equals("Position")) {
                                    position = shapeChildValue.split(",");
                                }
                                else if (shapeChildName.equals("Rotation")) {
                                    rotation = shapeChildValue.split(",");
                                }
                                else if (shapeChildName.equals("Size")) {
                                    size = shapeChildValue.split(",");
                                }
                                else if (shapeChildName.equals("TextureOffset")) {
                                    textureOffset = shapeChildValue.split(",");
                                }
                            }
                        }
                        final bcu cube = new bcu((bbo)this, Integer.parseInt(textureOffset[0]), Integer.parseInt(textureOffset[1]));
                        cube.i = mirrored;
                        cube.a(Float.parseFloat(offset[0]), Float.parseFloat(offset[1]), Float.parseFloat(offset[2]), Integer.parseInt(size[0]), Integer.parseInt(size[1]), Integer.parseInt(size[2]));
                        cube.a(Float.parseFloat(position[0]), Float.parseFloat(position[1]) - 23.4f, Float.parseFloat(position[2]));
                        cube.f = (float)Math.toRadians(Float.parseFloat(rotation[0]));
                        cube.g = (float)Math.toRadians(Float.parseFloat(rotation[1]));
                        cube.h = (float)Math.toRadians(Float.parseFloat(rotation[2]));
                        this.parts.put(shapeName, cube);
                    }
                    catch (final NumberFormatException e) {
                        FMLLog.warning("Model shape [" + shapeName + "] in " + this.fileName + " contains malformed integers within its data, ignoring", new Object[0]);
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (final ZipException e2) {
            throw new ModelFormatException("Model " + this.fileName + " is not a valid zip file");
        }
        catch (final IOException e3) {
            throw new ModelFormatException("Model " + this.fileName + " could not be read", e3);
        }
        catch (final ParserConfigurationException e4) {}
        catch (final SAXException e5) {
            throw new ModelFormatException("Model " + this.fileName + " contains invalid XML", e5);
        }
    }
    
    private void bindTexture() {
    }
    
    public String getType() {
        return "tcn";
    }
    
    public void renderAll() {
        this.bindTexture();
        for (final bcu part : this.parts.values()) {
            part.b(1.0f);
        }
    }
    
    public void renderPart(final String partName) {
        final bcu part = this.parts.get(partName);
        if (part != null) {
            this.bindTexture();
            part.b(1.0f);
        }
    }
    
    public void renderOnly(final String... groupNames) {
        this.bindTexture();
        for (final bcu part : this.parts.values()) {
            for (final String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(part.n)) {
                    part.a(1.0f);
                }
            }
        }
    }
    
    public void renderAllExcept(final String... excludedGroupNames) {
        for (final bcu part : this.parts.values()) {
            boolean skipPart = false;
            for (final String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(part.n)) {
                    skipPart = true;
                }
            }
            if (!skipPart) {
                part.a(1.0f);
            }
        }
    }
    
    static {
        cubeTypes = Arrays.asList("d9e621f7-957f-4b77-b1ae-20dcd0da7751", "de81aa14-bd60-4228-8d8d-5238bcd3caaa");
    }
}
