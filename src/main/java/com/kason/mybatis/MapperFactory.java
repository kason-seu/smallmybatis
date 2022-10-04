package com.kason.mybatis;

import com.kason.annotations.Mapper;
import com.kason.annotations.MapperScan;
import com.mysql.cj.util.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapperFactory {

    private Class<?> appConfig;

    private ConcurrentMap<String, Object> mapperBeanMap = new ConcurrentHashMap<>();
    public MapperFactory(Class<?> appConfig) {
        this.appConfig = appConfig;
        // 根据APPConfig读取配置
        boolean mapperScanAnnotationPresent = appConfig.isAnnotationPresent(MapperScan.class);
        if (mapperScanAnnotationPresent) {
            MapperScan mapperScanAnnotation = appConfig.getDeclaredAnnotation(MapperScan.class);
            String path = mapperScanAnnotation.value();
            // 扫描路径
            String scanPath = path.replace(".", "/");
            ClassLoader classLoader = MapperFactory.class.getClassLoader();
            URL resource = classLoader.getResource(scanPath);

            assert resource != null;
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                List<Object> beanResults = Arrays.stream(Objects.requireNonNull(file.listFiles()))
                        .filter(f -> {   //过滤出class文件
                            return f.getAbsolutePath().endsWith(".class");
                        })
                        .map(f -> { // 将文件目录的class名称转化为可装载的文件路径名
                            String classPath = f.getAbsolutePath();
                            classPath = classPath.substring(classPath.indexOf("com"), classPath.indexOf(".class"));
                            classPath = classPath.replace("/", ".");
                            return classPath;
                        })
                        .map(classPath -> {  // 装载bean
                            try {
                                return classLoader.loadClass(classPath);

                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(clazz -> {  // 留下Mapper注解的类，这些类是应该实例化bean的
                            return clazz.isAnnotationPresent(Mapper.class);
                        })
                        .map(clz -> {
                            Mapper declaredAnnotation = clz.getDeclaredAnnotation(Mapper.class);
                            String mapperBeanName = declaredAnnotation.value();
                            if (StringUtils.isNullOrEmpty(mapperBeanName)) {
                                mapperBeanName = Introspector.decapitalize(clz.getSimpleName());
                            }
                            Object bean = mapperBeanMap.get(mapperBeanName);
                            if (bean != null) {
                                return bean;
                            } else {
                                Object mapperBean = SqlSession.getMapper(clz);
                                mapperBeanMap.put(mapperBeanName, mapperBean);
                                return mapperBean;
                            }
                        }).toList();
            }
        }
    }

    public Object getMapper(String mapperName) {
        return mapperBeanMap.get(mapperName);
    }

}
