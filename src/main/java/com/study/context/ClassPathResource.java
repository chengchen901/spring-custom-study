package com.study.context;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClassPathResource implements Resource {

    private String path;
    private Class<?> clazz;
    private ClassLoader classLoader;

    public ClassPathResource(String path) {
        this(path, null);
    }

    public ClassPathResource(String path, Class<?> clazz) {
        this(path, clazz, null);
    }

    public ClassPathResource(String path, Class<?> clazz, ClassLoader classLoader) {
        this.path = path;
        this.clazz = clazz;
        this.classLoader = classLoader;
    }

    @Override
    public boolean exists() {
        if (StringUtils.isNotBlank(path)) {
            if (clazz != null) {
                return clazz.getResource(path) != null;
            }

            if (classLoader != null) {
                return classLoader.getResource(path.startsWith("/") ? path.substring(1) : path) != null;
            }

            return this.getClass().getResource(path) != null;
        }
        return false;
    }

    @Override
    public boolean isReadable() {
        return exists();
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        /*
         * Class.getResourceAsStream(String path) ： path 不以’/'开头时默认是从此类所在的包下取资源，以’/'开头则是从
         * ClassPath根下获取。其只是通过path构造一个绝对路径，最终还是由ClassLoader获取资源。
         *
         * Class.getClassLoader.getResourceAsStream(String path) ：默认则是从ClassPath根下获取，path不能以’/'开头，最终是由
         * ClassLoader获取资源。
         */
        if (StringUtils.isNotBlank(path)) {
            if (clazz != null) {
                return clazz.getResourceAsStream(path);
            }

            if (classLoader != null) {
                return classLoader.getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
            }

            return this.getClass().getResourceAsStream(path);
        }
        return null;
    }
}
