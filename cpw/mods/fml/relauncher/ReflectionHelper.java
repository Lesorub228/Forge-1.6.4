// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.relauncher;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class ReflectionHelper
{
    public static Field findField(final Class<?> clazz, final String... fieldNames) {
        Exception failed = null;
        final String[] arr$ = fieldNames;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final String fieldName = arr$[i$];
            try {
                final Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (final Exception e) {
                failed = e;
                ++i$;
                continue;
            }
            break;
        }
        throw new UnableToFindFieldException(fieldNames, failed);
    }
    
    public static <T, E> T getPrivateValue(final Class<? super E> classToAccess, final E instance, final int fieldIndex) {
        try {
            final Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            return (T)f.get(instance);
        }
        catch (final Exception e) {
            throw new UnableToAccessFieldException(new String[0], e);
        }
    }
    
    public static <T, E> T getPrivateValue(final Class<? super E> classToAccess, final E instance, final String... fieldNames) {
        try {
            return (T)findField(classToAccess, fieldNames).get(instance);
        }
        catch (final Exception e) {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }
    
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final int fieldIndex) {
        try {
            final Field f = classToAccess.getDeclaredFields()[fieldIndex];
            f.setAccessible(true);
            f.set(instance, value);
        }
        catch (final Exception e) {
            throw new UnableToAccessFieldException(new String[0], e);
        }
    }
    
    public static <T, E> void setPrivateValue(final Class<? super T> classToAccess, final T instance, final E value, final String... fieldNames) {
        try {
            findField(classToAccess, fieldNames).set(instance, value);
        }
        catch (final Exception e) {
            throw new UnableToAccessFieldException(fieldNames, e);
        }
    }
    
    public static Class<? super Object> getClass(final ClassLoader loader, final String... classNames) {
        Exception err = null;
        final String[] arr$ = classNames;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final String className = arr$[i$];
            try {
                return (Class<? super Object>)Class.forName(className, false, loader);
            }
            catch (final Exception e) {
                err = e;
                ++i$;
                continue;
            }
            break;
        }
        throw new UnableToFindClassException(classNames, err);
    }
    
    public static <E> Method findMethod(final Class<? super E> clazz, final E instance, final String[] methodNames, final Class<?>... methodTypes) {
        Exception failed = null;
        final String[] arr$ = methodNames;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final String methodName = arr$[i$];
            try {
                final Method m = clazz.getDeclaredMethod(methodName, methodTypes);
                m.setAccessible(true);
                return m;
            }
            catch (final Exception e) {
                failed = e;
                ++i$;
                continue;
            }
            break;
        }
        throw new UnableToFindMethodException(methodNames, failed);
    }
    
    public static class UnableToFindMethodException extends RuntimeException
    {
        private String[] methodNames;
        
        public UnableToFindMethodException(final String[] methodNames, final Exception failed) {
            super(failed);
            this.methodNames = methodNames;
        }
    }
    
    public static class UnableToFindClassException extends RuntimeException
    {
        private String[] classNames;
        
        public UnableToFindClassException(final String[] classNames, final Exception err) {
            super(err);
            this.classNames = classNames;
        }
    }
    
    public static class UnableToAccessFieldException extends RuntimeException
    {
        private String[] fieldNameList;
        
        public UnableToAccessFieldException(final String[] fieldNames, final Exception e) {
            super(e);
            this.fieldNameList = fieldNames;
        }
    }
    
    public static class UnableToFindFieldException extends RuntimeException
    {
        private String[] fieldNameList;
        
        public UnableToFindFieldException(final String[] fieldNameList, final Exception e) {
            super(e);
            this.fieldNameList = fieldNameList;
        }
    }
}
