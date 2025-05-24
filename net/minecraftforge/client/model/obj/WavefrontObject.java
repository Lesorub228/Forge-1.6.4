// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraftforge.client.model.obj;

import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import net.minecraftforge.client.model.ModelFormatException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class WavefrontObject implements IModelCustom
{
    private static Pattern vertexPattern;
    private static Pattern vertexNormalPattern;
    private static Pattern textureCoordinatePattern;
    private static Pattern face_V_VT_VN_Pattern;
    private static Pattern face_V_VT_Pattern;
    private static Pattern face_V_VN_Pattern;
    private static Pattern face_V_Pattern;
    private static Pattern groupObjectPattern;
    private static Matcher vertexMatcher;
    private static Matcher vertexNormalMatcher;
    private static Matcher textureCoordinateMatcher;
    private static Matcher face_V_VT_VN_Matcher;
    private static Matcher face_V_VT_Matcher;
    private static Matcher face_V_VN_Matcher;
    private static Matcher face_V_Matcher;
    private static Matcher groupObjectMatcher;
    public ArrayList<Vertex> vertices;
    public ArrayList<Vertex> vertexNormals;
    public ArrayList<TextureCoordinate> textureCoordinates;
    public ArrayList<GroupObject> groupObjects;
    private GroupObject currentGroupObject;
    private String fileName;
    
    public WavefrontObject(final String fileName, final URL resource) throws ModelFormatException {
        this.vertices = new ArrayList<Vertex>();
        this.vertexNormals = new ArrayList<Vertex>();
        this.textureCoordinates = new ArrayList<TextureCoordinate>();
        this.groupObjects = new ArrayList<GroupObject>();
        this.fileName = fileName;
        try {
            this.loadObjModel(resource.openStream());
        }
        catch (final IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
    }
    
    public WavefrontObject(final String filename, final InputStream inputStream) throws ModelFormatException {
        this.vertices = new ArrayList<Vertex>();
        this.vertexNormals = new ArrayList<Vertex>();
        this.textureCoordinates = new ArrayList<TextureCoordinate>();
        this.groupObjects = new ArrayList<GroupObject>();
        this.fileName = filename;
        this.loadObjModel(inputStream);
    }
    
    private void loadObjModel(final InputStream inputStream) throws ModelFormatException {
        BufferedReader reader = null;
        String currentLine = null;
        int lineCount = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((currentLine = reader.readLine()) != null) {
                ++lineCount;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();
                if (!currentLine.startsWith("#")) {
                    if (currentLine.length() == 0) {
                        continue;
                    }
                    if (currentLine.startsWith("v ")) {
                        final Vertex vertex = this.parseVertex(currentLine, lineCount);
                        if (vertex == null) {
                            continue;
                        }
                        this.vertices.add(vertex);
                    }
                    else if (currentLine.startsWith("vn ")) {
                        final Vertex vertex = this.parseVertexNormal(currentLine, lineCount);
                        if (vertex == null) {
                            continue;
                        }
                        this.vertexNormals.add(vertex);
                    }
                    else if (currentLine.startsWith("vt ")) {
                        final TextureCoordinate textureCoordinate = this.parseTextureCoordinate(currentLine, lineCount);
                        if (textureCoordinate == null) {
                            continue;
                        }
                        this.textureCoordinates.add(textureCoordinate);
                    }
                    else if (currentLine.startsWith("f ")) {
                        if (this.currentGroupObject == null) {
                            this.currentGroupObject = new GroupObject("Default");
                        }
                        final Face face = this.parseFace(currentLine, lineCount);
                        if (face == null) {
                            continue;
                        }
                        this.currentGroupObject.faces.add(face);
                    }
                    else {
                        if (!(currentLine.startsWith("g ") | currentLine.startsWith("o "))) {
                            continue;
                        }
                        final GroupObject group = this.parseGroupObject(currentLine, lineCount);
                        if (group != null && this.currentGroupObject != null) {
                            this.groupObjects.add(this.currentGroupObject);
                        }
                        this.currentGroupObject = group;
                    }
                }
            }
            this.groupObjects.add(this.currentGroupObject);
        }
        catch (final IOException e) {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException ex) {}
            try {
                inputStream.close();
            }
            catch (final IOException ex2) {}
        }
    }
    
    @Override
    public void renderAll() {
        final bfq tessellator = bfq.a;
        if (this.currentGroupObject != null) {
            tessellator.b(this.currentGroupObject.glDrawingMode);
        }
        else {
            tessellator.b(4);
        }
        this.tessellateAll(tessellator);
        tessellator.a();
    }
    
    public void tessellateAll(final bfq tessellator) {
        for (final GroupObject groupObject : this.groupObjects) {
            groupObject.render(tessellator);
        }
    }
    
    @Override
    public void renderOnly(final String... groupNames) {
        for (final GroupObject groupObject : this.groupObjects) {
            for (final String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(groupObject.name)) {
                    groupObject.render();
                }
            }
        }
    }
    
    public void tessellateOnly(final bfq tessellator, final String... groupNames) {
        for (final GroupObject groupObject : this.groupObjects) {
            for (final String groupName : groupNames) {
                if (groupName.equalsIgnoreCase(groupObject.name)) {
                    groupObject.render(tessellator);
                }
            }
        }
    }
    
    @Override
    public void renderPart(final String partName) {
        for (final GroupObject groupObject : this.groupObjects) {
            if (partName.equalsIgnoreCase(groupObject.name)) {
                groupObject.render();
            }
        }
    }
    
    public void tessellatePart(final bfq tessellator, final String partName) {
        for (final GroupObject groupObject : this.groupObjects) {
            if (partName.equalsIgnoreCase(groupObject.name)) {
                groupObject.render(tessellator);
            }
        }
    }
    
    @Override
    public void renderAllExcept(final String... excludedGroupNames) {
        for (final GroupObject groupObject : this.groupObjects) {
            boolean skipPart = false;
            for (final String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name)) {
                    skipPart = true;
                }
            }
            if (!skipPart) {
                groupObject.render();
            }
        }
    }
    
    public void tessellateAllExcept(final bfq tessellator, final String... excludedGroupNames) {
        for (final GroupObject groupObject : this.groupObjects) {
            boolean exclude = false;
            for (final String excludedGroupName : excludedGroupNames) {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name)) {
                    exclude = true;
                }
            }
            if (!exclude) {
                groupObject.render(tessellator);
            }
        }
    }
    
    private Vertex parseVertex(String line, final int lineCount) throws ModelFormatException {
        final Vertex vertex = null;
        if (isValidVertexLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            final String[] tokens = line.split(" ");
            try {
                if (tokens.length == 2) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
                }
                if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (final NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return vertex;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
    }
    
    private Vertex parseVertexNormal(String line, final int lineCount) throws ModelFormatException {
        final Vertex vertexNormal = null;
        if (isValidVertexNormalLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            final String[] tokens = line.split(" ");
            try {
                if (tokens.length == 3) {
                    return new Vertex(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (final NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return vertexNormal;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
    }
    
    private TextureCoordinate parseTextureCoordinate(String line, final int lineCount) throws ModelFormatException {
        final TextureCoordinate textureCoordinate = null;
        if (isValidTextureCoordinateLine(line)) {
            line = line.substring(line.indexOf(" ") + 1);
            final String[] tokens = line.split(" ");
            try {
                if (tokens.length == 2) {
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0f - Float.parseFloat(tokens[1]));
                }
                if (tokens.length == 3) {
                    return new TextureCoordinate(Float.parseFloat(tokens[0]), 1.0f - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (final NumberFormatException e) {
                throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
            }
            return textureCoordinate;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
    }
    
    private Face parseFace(final String line, final int lineCount) throws ModelFormatException {
        Face face = null;
        if (isValidFaceLine(line)) {
            face = new Face();
            final String trimmedLine = line.substring(line.indexOf(" ") + 1);
            final String[] tokens = trimmedLine.split(" ");
            String[] subTokens = null;
            if (tokens.length == 3) {
                if (this.currentGroupObject.glDrawingMode == -1) {
                    this.currentGroupObject.glDrawingMode = 4;
                }
                else if (this.currentGroupObject.glDrawingMode != 4) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
                }
            }
            else if (tokens.length == 4) {
                if (this.currentGroupObject.glDrawingMode == -1) {
                    this.currentGroupObject.glDrawingMode = 7;
                }
                else if (this.currentGroupObject.glDrawingMode != 7) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
                }
            }
            if (isValidFace_V_VT_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");
                    face.vertices[i] = this.vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = this.textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = this.vertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else if (isValidFace_V_VT_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.textureCoordinates = new TextureCoordinate[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("/");
                    face.vertices[i] = this.vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = this.textureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else if (isValidFace_V_VN_Line(line)) {
                face.vertices = new Vertex[tokens.length];
                face.vertexNormals = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    subTokens = tokens[i].split("//");
                    face.vertices[i] = this.vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = this.vertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            else {
                if (!isValidFace_V_Line(line)) {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
                }
                face.vertices = new Vertex[tokens.length];
                for (int i = 0; i < tokens.length; ++i) {
                    face.vertices[i] = this.vertices.get(Integer.parseInt(tokens[i]) - 1);
                }
                face.faceNormal = face.calculateFaceNormal();
            }
            return face;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
    }
    
    private GroupObject parseGroupObject(final String line, final int lineCount) throws ModelFormatException {
        GroupObject group = null;
        if (isValidGroupObjectLine(line)) {
            final String trimmedLine = line.substring(line.indexOf(" ") + 1);
            if (trimmedLine.length() > 0) {
                group = new GroupObject(trimmedLine);
            }
            return group;
        }
        throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + this.fileName + "' - Incorrect format");
    }
    
    private static boolean isValidVertexLine(final String line) {
        if (WavefrontObject.vertexMatcher != null) {
            WavefrontObject.vertexMatcher.reset();
        }
        WavefrontObject.vertexMatcher = WavefrontObject.vertexPattern.matcher(line);
        return WavefrontObject.vertexMatcher.matches();
    }
    
    private static boolean isValidVertexNormalLine(final String line) {
        if (WavefrontObject.vertexNormalMatcher != null) {
            WavefrontObject.vertexNormalMatcher.reset();
        }
        WavefrontObject.vertexNormalMatcher = WavefrontObject.vertexNormalPattern.matcher(line);
        return WavefrontObject.vertexNormalMatcher.matches();
    }
    
    private static boolean isValidTextureCoordinateLine(final String line) {
        if (WavefrontObject.textureCoordinateMatcher != null) {
            WavefrontObject.textureCoordinateMatcher.reset();
        }
        WavefrontObject.textureCoordinateMatcher = WavefrontObject.textureCoordinatePattern.matcher(line);
        return WavefrontObject.textureCoordinateMatcher.matches();
    }
    
    private static boolean isValidFace_V_VT_VN_Line(final String line) {
        if (WavefrontObject.face_V_VT_VN_Matcher != null) {
            WavefrontObject.face_V_VT_VN_Matcher.reset();
        }
        WavefrontObject.face_V_VT_VN_Matcher = WavefrontObject.face_V_VT_VN_Pattern.matcher(line);
        return WavefrontObject.face_V_VT_VN_Matcher.matches();
    }
    
    private static boolean isValidFace_V_VT_Line(final String line) {
        if (WavefrontObject.face_V_VT_Matcher != null) {
            WavefrontObject.face_V_VT_Matcher.reset();
        }
        WavefrontObject.face_V_VT_Matcher = WavefrontObject.face_V_VT_Pattern.matcher(line);
        return WavefrontObject.face_V_VT_Matcher.matches();
    }
    
    private static boolean isValidFace_V_VN_Line(final String line) {
        if (WavefrontObject.face_V_VN_Matcher != null) {
            WavefrontObject.face_V_VN_Matcher.reset();
        }
        WavefrontObject.face_V_VN_Matcher = WavefrontObject.face_V_VN_Pattern.matcher(line);
        return WavefrontObject.face_V_VN_Matcher.matches();
    }
    
    private static boolean isValidFace_V_Line(final String line) {
        if (WavefrontObject.face_V_Matcher != null) {
            WavefrontObject.face_V_Matcher.reset();
        }
        WavefrontObject.face_V_Matcher = WavefrontObject.face_V_Pattern.matcher(line);
        return WavefrontObject.face_V_Matcher.matches();
    }
    
    private static boolean isValidFaceLine(final String line) {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }
    
    private static boolean isValidGroupObjectLine(final String line) {
        if (WavefrontObject.groupObjectMatcher != null) {
            WavefrontObject.groupObjectMatcher.reset();
        }
        WavefrontObject.groupObjectMatcher = WavefrontObject.groupObjectPattern.matcher(line);
        return WavefrontObject.groupObjectMatcher.matches();
    }
    
    @Override
    public String getType() {
        return "obj";
    }
    
    static {
        WavefrontObject.vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
        WavefrontObject.vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
        WavefrontObject.textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
        WavefrontObject.face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
        WavefrontObject.face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
        WavefrontObject.face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
        WavefrontObject.face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
        WavefrontObject.groupObjectPattern = Pattern.compile("([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)");
    }
}
