package org.greenleaf.api;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InjectedServiceLoader {
    public static final HashMap<String, Map<String, String>> serviceListMap = new HashMap();
    private static final HashMap<String, Map<String, Object>> serviceMap = new HashMap<>();
    private static volatile AtomicBoolean inited = new AtomicBoolean();

    private static void init() {
        try {
            Class<?> clazz = Class.forName("org.greenleaf.api.ServiceClassRegistry");
            Field field = clazz.getField("serviceListMap");
            HashMap<String, Map<String, String>> stringMapHashMap = (HashMap<String, Map<String, String>>) field.get(null);
            System.out.println(stringMapHashMap);
            serviceListMap.putAll(stringMapHashMap);
//            serviceListMap.putAll(InjectedServiceRegistry.serviceListMap);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static <T> T getService(Class<T> interfaceClass) {
        return getService(interfaceClass, null);
    }

    public static <T> T getService(Class<T> interfaceClass, String key) {
        if (!inited.get()) {
            init();
            inited.set(true);
        }
        String serviceName = interfaceClass.getCanonicalName();

        Map<String, Object> serviceKeyObjectMap = serviceMap.get(serviceName);
        String serviceKey = key;
        if (serviceKeyObjectMap == null) {
            serviceKeyObjectMap = new HashMap<>();
            serviceMap.put(serviceName, serviceKeyObjectMap);
        }

        try {
            if (key == null && !serviceKeyObjectMap.isEmpty()) {
                serviceKey = serviceKeyObjectMap.keySet().iterator().next();
            }
            Object object = serviceKeyObjectMap.get(serviceKey);
            if (object != null) {
                return (T) object;
            }
        } catch (Exception e) {
            // do nothing
        }


        Map<String, String> serviceKeyClassMap = serviceListMap.get(serviceName);
        if (serviceKeyClassMap != null) {
            for (Map.Entry<String, String> entry : serviceKeyClassMap.entrySet()) {
                String clazzName = entry.getValue();
                try {
                    Class<?> c = Class.forName(clazzName);
                    if (interfaceClass.isAssignableFrom(c)) {
                        T t = interfaceClass.cast(c.newInstance());
                        serviceKeyObjectMap.put(serviceKey, t);
                        return t;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException x) {
                    x.printStackTrace();
                }
            }
        }
        return null;
    }
}