package com.fly.video.downloader.core.contract;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSingleton {
    private static Map<String, AbstractSingleton> registryMap = new HashMap<String, AbstractSingleton>();

    public AbstractSingleton() throws SingletonException {
        String clazzName = this.getClass().getName();
        if (registryMap.containsKey(clazzName)){
            throw new SingletonException("Cannot construct instance for class " + clazzName + ", since an instance already exists!");
        } else {
            synchronized(registryMap){
                if (registryMap.containsKey(clazzName)){
                    throw new SingletonException("Cannot construct instance for class " + clazzName + ", since an instance already exists!");
                } else {
                    registryMap.put(clazzName, this);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractSingleton> T getInstance(final Class<T> clazz) throws InstantiationException, IllegalAccessException{
        String clazzName = clazz.getName();
        if(!registryMap.containsKey(clazzName)){
            synchronized(registryMap){
                if(!registryMap.containsKey(clazzName)){
                    T instance = clazz.newInstance();
                    return instance;
                }
            }
        }
        return (T) registryMap.get(clazzName);
    }

    public static AbstractSingleton getInstance(final String clazzName)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        if(!registryMap.containsKey(clazzName)){
            Class<? extends AbstractSingleton> clazz = Class.forName(clazzName).asSubclass(AbstractSingleton.class);
            synchronized(registryMap){
                if(!registryMap.containsKey(clazzName)){
                    AbstractSingleton instance = clazz.newInstance();
                    return instance;
                }
            }
        }
        return registryMap.get(clazzName);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractSingleton> T getInstance(final Class<T> clazz, Class<?>[] parameterTypes, Object[] initargs)
            throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException, InstantiationException, IllegalAccessException{
        String clazzName = clazz.getName();
        if(!registryMap.containsKey(clazzName)){
            synchronized(registryMap){
                if(!registryMap.containsKey(clazzName)){
                    Constructor<T> constructor = clazz.getConstructor(parameterTypes);
                    T instance = constructor.newInstance(initargs);
                    return instance;
                }
            }
        }
        return (T) registryMap.get(clazzName);
    }

    protected static class SingletonException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = -8633183690442262445L;

        private SingletonException(String message){
            super(message);
        }
    }

}