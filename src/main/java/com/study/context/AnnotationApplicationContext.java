package com.study.context;

import com.study.beans.BeanDefinitionRegistry;

import java.util.Set;

public class AnnotationApplicationContext extends AbstractApplicationContext {

    private ClassPathBeanDefinitionScanner scanner;

    public AnnotationApplicationContext(String... basePackages) throws Exception {
        this.scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) this.beanFactory);
        scanner.scan(basePackages);
    }

    @Override
    public Resource getResource(String location) {
        return null;
    }
}
