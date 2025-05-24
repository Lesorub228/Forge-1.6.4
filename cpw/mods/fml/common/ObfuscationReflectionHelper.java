// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.util.Arrays;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import java.util.logging.Level;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ObfuscationReflectionHelper
{
    public static <T, E> T getPrivateValue(final Class<? super E> classToAccess, final E instance, final int fieldIndex) {
        try {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, fieldIndex);
        }
        catch (final ReflectionHelper.UnableToAccessFieldException e) {
            FMLLog.log(Level.SEVERE, e, "There was a problem getting field index %d from %s", fieldIndex, classToAccess.getName());
            throw e;
        }
    }
    
    public static String[] remapFieldNames(final String className, final String... fieldNames) {
        final String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        final String[] mappedNames = new String[fieldNames.length];
        int i = 0;
        for (final String fName : fieldNames) {
            mappedNames[i++] = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, fName, null);
        }
        return mappedNames;
    }
    
    public static <T, E> T getPrivateValue(final Class<? super E> classToAccess, final E instance, final String... fieldNames) {
        try {
            return ReflectionHelper.getPrivateValue(classToAccess, instance, remapFieldNames(classToAccess.getName(), fieldNames));
        }
        catch (final ReflectionHelper.UnableToFindFieldException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
        catch (final ReflectionHelper.UnableToAccessFieldException e2) {
            FMLLog.log(Level.SEVERE, e2, "Unable to access any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e2;
        }
    }
    
    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final int fieldIndex, final E value) {
        setPrivateValue(classToAccess, instance, value, fieldIndex);
    }
    
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final int fieldIndex) {
        try {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, fieldIndex);
        }
        catch (final ReflectionHelper.UnableToAccessFieldException e) {
            FMLLog.log(Level.SEVERE, e, "There was a problem setting field index %d on type %s", fieldIndex, classToAccess.getName());
            throw e;
        }
    }
    
    @Deprecated
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final String fieldName, final E value) {
        setPrivateValue(classToAccess, instance, value, fieldName);
    }
    
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final String... fieldNames) {
        try {
            ReflectionHelper.setPrivateValue(classToAccess, instance, value, remapFieldNames(classToAccess.getName(), fieldNames));
        }
        catch (final ReflectionHelper.UnableToFindFieldException e) {
            FMLLog.log(Level.SEVERE, e, "Unable to locate any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e;
        }
        catch (final ReflectionHelper.UnableToAccessFieldException e2) {
            FMLLog.log(Level.SEVERE, e2, "Unable to set any field %s on type %s", Arrays.toString(fieldNames), classToAccess.getName());
            throw e2;
        }
    }
}
