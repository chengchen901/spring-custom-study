package com.study.context;

import java.io.IOException;

/**
 * 配置资源加载接口。<br/>
 * 不同的配置方式，有不同的加载过程，故需要抽象一个接口来拥抱变化的部分。
 * 虽然加载方式不同，但是返回的资源结果是一样的Resource。
 */
public interface ResourceLoader {

    Resource getResource(String location) throws IOException;
}
