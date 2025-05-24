// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.Iterator;
import org.objectweb.asm.Type;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.discovery.ModCandidate;
import java.io.File;
import cpw.mods.fml.common.discovery.asm.ASMModParser;
import java.util.regex.Pattern;

public class ModContainerFactory
{
    private static Pattern modClass;
    private static ModContainerFactory INSTANCE;
    
    public static ModContainerFactory instance() {
        return ModContainerFactory.INSTANCE;
    }
    
    public ModContainer build(final ASMModParser modParser, final File modSource, final ModCandidate container) {
        final String className = modParser.getASMType().getClassName();
        if (modParser.isBaseMod(container.getRememberedBaseMods()) && ModContainerFactory.modClass.matcher(className).find()) {
            FMLLog.fine("Identified a BaseMod type mod %s", className);
            return new ModLoaderModContainer(className, modSource, modParser.getBaseModProperties());
        }
        if (ModContainerFactory.modClass.matcher(className).find()) {
            FMLLog.fine("Identified a class %s following modloader naming convention but not directly a BaseMod or currently seen subclass", className);
            container.rememberModCandidateType(modParser);
        }
        else if (modParser.isBaseMod(container.getRememberedBaseMods())) {
            FMLLog.fine("Found a basemod %s of non-standard naming format", className);
            container.rememberBaseModType(className);
        }
        if (className.startsWith("net.minecraft.src.") && container.isClasspath() && !container.isMinecraftJar()) {
            FMLLog.severe("FML has detected a mod that is using a package name based on 'net.minecraft.src' : %s. This is generally a severe programming error.  There should be no mod code in the minecraft namespace. MOVE YOUR MOD! If you're in eclipse, select your source code and 'refactor' it into a new package. Go on. DO IT NOW!", className);
        }
        for (final ModAnnotation ann : modParser.getAnnotations()) {
            if (ann.getASMType().equals((Object)Type.getType((Class)Mod.class))) {
                FMLLog.fine("Identified an FMLMod type mod %s", className);
                return new FMLModContainer(className, container, ann.getValues());
            }
        }
        return null;
    }
    
    static {
        ModContainerFactory.modClass = Pattern.compile(".*(\\.|)(mod\\_[^\\s$]+)$");
        ModContainerFactory.INSTANCE = new ModContainerFactory();
    }
}
