/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.nuls.core.core.inteceptor;

import io.nuls.core.core.inteceptor.base.BeanMethodInterceptorManager;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 系统默认的方法拦截器，用于aop底层实现
 * The system's default method interceptor is used for the aop underlying implementation.
 *
 * @author: Niels Wang
 */
public class DefaultMethodInterceptor implements MethodInterceptor {
    /**
     * 拦截方法
     * Intercept method
     *
     * @param obj         方法所属对象/Method owner
     * @param method      方法定义/Method definition
     * @param params      方法参数列表/Method parameter list
     * @param methodProxy 方法代理器
     * @return 返回拦截的方法的返回值，可以对该值进行处理和替换/Returns the return value of the intercepting method, which can be processed and replaced.
     * @throws Throwable 该方法可能抛出异常，请谨慎处理/This method may throw an exception, handle with care.
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        Annotation[] clsAnns = obj.getClass().getSuperclass().getDeclaredAnnotations();
        Annotation[] methodAnns = method.getDeclaredAnnotations();
        if ((null == method.getDeclaredAnnotations() || method.getDeclaredAnnotations().length == 0) &&
                (obj.getClass().getSuperclass().getDeclaredAnnotations() == null || obj.getClass().getSuperclass().getDeclaredAnnotations().length == 0 )) {
            return methodProxy.invokeSuper(obj, params);
        }
        Annotation[] anns = Arrays.copyOf(methodAnns,methodAnns.length + clsAnns.length);
        System.arraycopy(clsAnns,0,anns,methodAnns.length,clsAnns.length);
        return BeanMethodInterceptorManager.doInterceptor(anns, obj, method, params, methodProxy);
    }
}
