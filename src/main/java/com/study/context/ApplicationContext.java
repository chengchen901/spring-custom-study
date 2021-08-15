package com.study.context;

import com.study.beans.BeanFactory;

import java.io.IOException;

public interface ApplicationContext extends BeanFactory, ResourceLoader {

    void close() throws IOException;
}
