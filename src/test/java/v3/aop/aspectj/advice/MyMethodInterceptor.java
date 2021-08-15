package v3.aop.aspectj.advice;


import com.study.aop.advice.MethodInterceptor;

import java.lang.reflect.Method;

public class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(Method method, Object[] args, Object target) throws Throwable {
        System.out.println(this + "对 " + target + "进行了环绕--前增强");
        Object ret = method.invoke(target, args);
        System.out.println(this + "对 " + target + "进行了环绕 --后增强。方法的返回值为：" + ret);
        return ret;
    }
}
