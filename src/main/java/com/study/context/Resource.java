package com.study.context;

import java.io.File;

public interface Resource extends InputStreamSource {
    /** classpath形式的xml配置文件*/
    String CLASS_PATH_PREFIX = "classpath:";
    /** 系统文件形式的xml配置文件*/
    String FILE_SYSTEM_PREFIX = "file:";

    boolean exists();

    boolean isReadable();

    boolean isOpen();

    File getFile();
}
