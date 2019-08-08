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
import io.nuls.core.exception.BeanStatusException;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 系统默认的服务拦截器
 * System default service interceptor.
 *
 * @author Niels
 */
public class ModularServiceMethodInterceptor implements MethodInterceptor {
    /**
     * 线程安全的拦截器执行进度标识
     * Thread-safe interceptors perform progress identification.
     */
    private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

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
        threadLocal.set(0);
        Throwable throwable = null;
        while (threadLocal.get() < 100) {
            try {
                return this.doIntercept(obj, method, params, methodProxy);
            } catch (BeanStatusException e) {
                threadLocal.set(threadLocal.get() + 1);
                throwable = e;
                Thread.sleep(200L);
            }
        }
        throw throwable;
    }

    /**
     * 实际的拦截方法
     * The actual intercept method
     *
     * @param obj         方法所属对象/Method owner
     * @param method      方法定义/Method definition
     * @param params      方法参数列表/Method parameter list
     * @param methodProxy 方法代理器
     * @return 返回拦截的方法的返回值，可以对该值进行处理和替换/Returns the return value of the intercepting method, which can be processed and replaced.
     * @throws Throwable 该方法可能抛出异常，请谨慎处理/This method may throw an exception, handle with care.
     */
    private Object doIntercept(Object obj, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        List<Annotation> annotationList = new ArrayList<>();
        if (!method.getDeclaringClass().equals(Object.class)) {
            String className = obj.getClass().getCanonicalName();
            className = className.substring(0, className.indexOf("$$"));
            Class clazz = Class.forName(className);
            fillAnnotationList(annotationList, clazz, method);
        }
        if (annotationList.isEmpty()) {
            return methodProxy.invokeSuper(obj, params);
        }
        return BeanMethodInterceptorManager.doInterceptor(annotationList.toArray(new Annotation[0]), obj, method, params, methodProxy);

    }

    /**
     * 组装拦截器需要的注解实例列表
     * A list of annotated instances needed to assemble the interceptor.
     *
     * @param annotationList 全部注解实例列表/Full annotation instance list.
     * @param clazz          方法所属对象的类型/The type of the object that the method belongs to.
     * @param method         方法定义/Method definition
     */
    private void fillAnnotationList(List<Annotation> annotationList, Class clazz, Method method) {
        Set<Class> classSet = new HashSet<>();
        for (Annotation ann : method.getDeclaredAnnotations()) {
            annotationList.add(ann);
            classSet.add(ann.annotationType());
        }
        for (Annotation ann : clazz.getDeclaredAnnotations()) {
            if (classSet.add(ann.annotationType())) {
                annotationList.add(0, ann);
            }
        }
        for (Annotation ann : clazz.getAnnotations()) {
            if (classSet.add(ann.annotationType())) {
                annotationList.add(0, ann);
            }
        }
    }
}
