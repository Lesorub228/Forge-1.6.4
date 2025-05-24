// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import cpw.mods.fml.relauncher.Side;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ILanguageAdapter
{
    Object getNewInstance(final FMLModContainer p0, final Class<?> p1, final ClassLoader p2, final Method p3) throws Exception;
    
    boolean supportsStatics();
    
    void setProxy(final Field p0, final Class<?> p1, final Object p2) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException;
    
    void setInternalProxies(final ModContainer p0, final Side p1, final ClassLoader p2);
    
    public static class ScalaAdapter implements ILanguageAdapter
    {
        @Override
        public Object getNewInstance(final FMLModContainer container, final Class<?> scalaObjectClass, final ClassLoader classLoader, final Method factoryMarkedAnnotation) throws Exception {
            final Class<?> sObjectClass = Class.forName(scalaObjectClass.getName() + "$", true, classLoader);
            return sObjectClass.getField("MODULE$").get(null);
        }
        
        @Override
        public boolean supportsStatics() {
            return false;
        }
        
        @Override
        public void setProxy(final Field target, Class<?> proxyTarget, final Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
            try {
                if (!proxyTarget.getName().endsWith("$")) {
                    proxyTarget = Class.forName(proxyTarget.getName() + "$", true, proxyTarget.getClassLoader());
                }
            }
            catch (final ClassNotFoundException e) {
                FMLLog.log(Level.INFO, e, "An error occured trying to load a proxy into %s.%s. Did you declare your mod as 'class' instead of 'object'?", proxyTarget.getSimpleName(), target.getName());
                return;
            }
            final Object targetInstance = proxyTarget.getField("MODULE$").get(null);
            try {
                final String setterName = target.getName() + "_$eq";
                for (final Method setter : proxyTarget.getMethods()) {
                    final Class<?>[] setterParameters = setter.getParameterTypes();
                    if (setterName.equals(setter.getName()) && setterParameters.length == 1 && setterParameters[0].isAssignableFrom(proxy.getClass())) {
                        setter.invoke(targetInstance, proxy);
                        return;
                    }
                }
            }
            catch (final InvocationTargetException e2) {
                FMLLog.log(Level.SEVERE, e2, "An error occured trying to load a proxy into %s.%s", proxyTarget.getSimpleName(), target.getName());
                throw new LoaderException(e2);
            }
            FMLLog.severe("Failed loading proxy into %s.%s, could not find setter function. Did you declare the field with 'val' instead of 'var'?", proxyTarget.getSimpleName(), target.getName());
            throw new LoaderException();
        }
        
        @Override
        public void setInternalProxies(final ModContainer mod, final Side side, final ClassLoader loader) {
            final Class<?> proxyTarget = mod.getMod().getClass();
            if (proxyTarget.getName().endsWith("$")) {
                for (final Field target : proxyTarget.getDeclaredFields()) {
                    if (target.getAnnotation(SidedProxy.class) != null) {
                        final String targetType = side.isClient() ? target.getAnnotation(SidedProxy.class).clientSide() : target.getAnnotation(SidedProxy.class).serverSide();
                        try {
                            final Object proxy = Class.forName(targetType, true, loader).newInstance();
                            if (!target.getType().isAssignableFrom(proxy.getClass())) {
                                FMLLog.severe("Attempted to load a proxy type %s into %s.%s, but the types don't match", targetType, proxyTarget.getSimpleName(), target.getName());
                                throw new LoaderException();
                            }
                            this.setProxy(target, proxyTarget, proxy);
                        }
                        catch (final Exception e) {
                            FMLLog.log(Level.SEVERE, e, "An error occured trying to load a proxy into %s.%s", proxyTarget.getSimpleName(), target.getName());
                            throw new LoaderException(e);
                        }
                    }
                }
            }
            else {
                FMLLog.finer("Mod does not appear to be a singleton.", new Object[0]);
            }
        }
    }
    
    public static class JavaAdapter implements ILanguageAdapter
    {
        @Override
        public Object getNewInstance(final FMLModContainer container, final Class<?> objectClass, final ClassLoader classLoader, final Method factoryMarkedMethod) throws Exception {
            if (factoryMarkedMethod != null) {
                return factoryMarkedMethod.invoke(null, new Object[0]);
            }
            return objectClass.newInstance();
        }
        
        @Override
        public boolean supportsStatics() {
            return true;
        }
        
        @Override
        public void setProxy(final Field target, final Class<?> proxyTarget, final Object proxy) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
            target.set(null, proxy);
        }
        
        @Override
        public void setInternalProxies(final ModContainer mod, final Side side, final ClassLoader loader) {
        }
    }
}
