package com.study.context;

import com.study.beans.BeanDefinitionRegistry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlApplicationContext extends AbstractApplicationContext {

    private List<Resource> resources;

    private final BeanDefinitionReader bdr;

    public XmlApplicationContext(String... location) throws Exception {
        load(location);
        // 资源解析成BeanDefinition，外派给BeanDefinitionReader接口来实现
        this.bdr = new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory);
        // 将解读后的BeanDefinition装载到BeanFactory中
        bdr.loadBeanDefinitions(resources.toArray(new Resource[0]));
    }

    @Override
    public Resource getResource(String location) throws IOException {
        if (StringUtils.isNotBlank(location)) {
            if (location.startsWith(Resource.CLASS_PATH_PREFIX)) {
                return new ClassPathResource(location.substring(Resource.CLASS_PATH_PREFIX.length()));
            } else if (location.startsWith(Resource.FILE_SYSTEM_PREFIX)) {
                return new FileSystemResource(location.substring(Resource.FILE_SYSTEM_PREFIX.length()));
            } else {
                return new UrlResource(location);
            }
        }
        return null;
    }

    private void load(String... location) throws IOException {
        if (resources == null) {
            resources = new ArrayList<>();
        }

        if (location != null && location.length > 0) {
            for (String l : location) {
                final Resource resource = getResource(l);
                if (resource != null) {
                    resources.add(resource);
                }
            }
        }
    }
}
