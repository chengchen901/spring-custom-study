package com.study.scan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class ScanClassFile {

    public static final Logger LOG = LoggerFactory.getLogger(ScanClassFile.class);

    private static PathMatcher pathMatcher = new AntPathMatcher();

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            LOG.warn("need scan packages args");
            return;
        }

        scan(args);
    }

    public static void scan(String[] packages) throws IOException {
        for (String p : packages) {
            doScan(p);
        }
    }

    public static void doScan(String p) throws IOException {
        // 构造初步匹配模式串
        // 例如：com.study => com/study/**/*.class
        String pathPattern = p.replace(".", "/") + "/**/*.class";

        // 找出模式的根包路径
        // 例如：com/study/**/*.class => com/study/
        String rootPath = pathPattern.substring(0, pathPattern.indexOf('*'));

        // 根据根包理解得到根包对应的目录
        File rootDir = null;
        final Enumeration<URL> rootPathResources = ScanClassFile.class.getClassLoader().getResources(rootPath);
        while (rootPathResources.hasMoreElements()) {
            final String rooDirPath = decodeUtf8(rootPathResources.nextElement().getFile());
            rootDir = new File(rooDirPath);
        }

        // 得到文件名匹配的绝对路径模式
        String fullPattern = null;
        final Enumeration<URL> fullPatternResources = ScanClassFile.class.getClassLoader().getResources("");
        while (fullPatternResources.hasMoreElements()) {
            fullPattern = decodeUtf8(fullPatternResources.nextElement().getFile() + pathPattern).substring(1);
        }

        LOG.info("package [{}] ,pathPattern [{}] ,rootPath [{}] ,fullPattern [{}] ,rootDir [{}]", p, pathPattern, rootPath,
                fullPattern, rootDir);

        Set<File> files = new HashSet<>();
        // 这种方式对jar包中的类是无法扫描到的
        doMatchingFiles(fullPattern, rootDir, files);

        LOG.info("scans package [{}] get files [{}]", p, files.size());
        for (File file : files) {
            System.out.println(file);
        }
    }

    public static void doMatchingFiles(String fullPattern, File rootDir, Set<File> fileSet) {
        final File[] files = listDirectory(rootDir);
        for (File f : files) {
            final String absolutePath = f.getAbsolutePath().replace(File.separator, "/");
            if (f.isDirectory() && pathMatcher.matchStart(fullPattern, absolutePath)) {
                doMatchingFiles(fullPattern, f, fileSet);
            }

            if (pathMatcher.match(fullPattern, absolutePath)) {
                fileSet.add(f);
            }
        }
    }

    public static File[] listDirectory(File dir) {
        final File[] files = dir.listFiles();
        if (files == null) {
            return new File[0];
        }

        Arrays.sort(files, Comparator.comparing(File::getName));
        return files;
    }

    public static String decodeUtf8(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "UTF-8");
    }
}
