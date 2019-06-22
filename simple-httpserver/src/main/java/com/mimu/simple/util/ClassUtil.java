package com.mimu.simple.util;

import com.mimu.simple.enums.ProtocolEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * author: mimu
 * date: 2018/10/22
 */
public class ClassUtil {
    private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);
    private static String dot = ".";
    private static String classSuffix = ".class";
    private static String fileSeparator = File.separator;
    private static String utf8 = "utf-8";

    /**
     * get the classLoader
     *
     * @return classLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * @param className
     * @return
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className, true);
    }

    /**
     * @param className
     * @param isInitialized
     * @return Class
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        try {
            return Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    /**
     * @param packages
     * @return
     */
    public static Set<Class<?>> getClasses(List<String> packages) {
        Set<Class<?>> classSet = new HashSet<>();
        if (CollectionUtils.isEmpty(packages)) {
            return classSet;
        }
        for (String packageName : packages) {
            Enumeration<URL> urlEnumeration = null;
            try {
                /*
                  here we use thread contextClassLoader to load resources,
                 */
                urlEnumeration = getClassLoader().getResources(packageNameToPath(packageName));
                logger.info("getClass packageName={}", packageName);
            } catch (IOException e) {
                logger.error("getClass error", e);
            }
            assert urlEnumeration != null;
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                logger.info("getClass url={}", url);
                String protocol = url.getProtocol();
                if (protocol.equalsIgnoreCase(ProtocolEnum.FILE.protocol())) {
                    try {
                        /*
                          here we should use urlDecoder decode the url.getfile()
                          because if the result of getfile() contains chinese it will be get unread code
                         */
                        String packagePath = URLDecoder.decode(url.getFile(), utf8);
                        findClass(packagePath, packageName, classSet);
                        logger.info("getClass packagePath={}", packagePath);
                    } catch (UnsupportedEncodingException e) {
                        logger.error("getClass error", e);
                    }
                } else if (protocol.equalsIgnoreCase(ProtocolEnum.JAR.protocol())) {
                    JarFile jarFile = null;
                    try {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        assert jarURLConnection != null;
                        jarFile = jarURLConnection.getJarFile();
                    } catch (IOException e) {
                        logger.error("getClass error", e);
                    }
                    assert jarFile != null;
                    Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
                    while (jarEntryEnumeration.hasMoreElements()) {
                        JarEntry jarEntry = jarEntryEnumeration.nextElement();
                        String jarEntryName = jarEntry.getName();
                        if (jarEntryName.endsWith(classSuffix)) {
                            String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(dot)).replaceAll(fileSeparator, dot);
                            loadClass(classSet, className);
                        }
                    }
                }
            }
        }
        return classSet;
    }

    /**
     * the reason why we explore the direction which is consist of the path of current class and the pacakge name we want to
     * scan is that we need build the the all namespace of a class and the class name. from the log info we can ensure this point;
     *
     * @param packagePath
     * @param packageName
     * @param classSet
     */
    private static void findClass(String packagePath, String packageName, Set<Class<?>> classSet) {
        File fileDir = new File(packagePath);
        logger.info("findClass fileDir={}", fileDir);
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            return;
        }
        File[] files = fileDir.listFiles(pathname -> (pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(classSuffix))));
        assert files != null;
        for (File file : files) {
            String fileName = file.getName();
            logger.info("findClass filePath={},fileName={}", file.getAbsolutePath(), fileName);
            if (file.isDirectory()) {
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath.concat(fileSeparator).concat(subPackagePath);
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName.concat(dot).concat(subPackageName);
                }
                findClass(subPackagePath, subPackageName, classSet);
                logger.info("findClass subPackagePath={},subPackageName={}", subPackagePath, subPackageName);
            } else {
                /*
                  here we should get the class name without .class suffix
                  so that classLoader can load it.
                 */
                String className = fileName.substring(0, fileName.lastIndexOf(dot));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName.concat(dot).concat(className);
                }
                logger.info("findClass packageName={},className={}", packageName, className);
                loadClass(classSet, className);
            }
        }
    }

    /**
     * @param classSet
     * @param className
     */
    private static void loadClass(Set<Class<?>> classSet, String className) {
        Class<?> clazz = loadClass(className, false);
        classSet.add(clazz);
    }

    /**
     * @param packageName
     * @return
     */
    private static String packageNameToPath(String packageName) {
        return packageName.replace(dot, fileSeparator);
    }
}
