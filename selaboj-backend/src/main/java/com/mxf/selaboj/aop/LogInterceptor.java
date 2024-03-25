package com.mxf.selaboj.aop;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求响应日志 AOP
 * 该拦截器可以帮助开发者监控和分析系统中的方法执行情况，
 * 例如方法的执行时间、请求参数等信息，从而进行性能优化或调试。
 **/
// 定义一个切面（Aspect）
@Aspect
// 声明该类为Spring组件，使其可以被Spring容器管理
@Component
// 使用SLF4J日志框架
@Slf4j
public class LogInterceptor {

    // 定义一个环绕通知（Around Advice），拦截方法执行
    @Around("execution(* com.mxf.selaboj.controller.*.*(..))")
    // 定义doInterceptor方法，该方法将作为拦截器被调用
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 创建一个StopWatch对象，用于计时
        StopWatch stopWatch = new StopWatch();
        // 开始计时
        stopWatch.start();
        // 获取当前请求的属性，包括路径、参数等
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 将请求属性转换为HttpServletRequest对象，以便获取更多关于请求的信息
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 生成一个唯一的请求ID
        String requestId = UUID.randomUUID().toString();
        // 获取请求的URL
        String url = httpServletRequest.getRequestURI();
        // 获取被拦截方法的参数
        Object[] args = point.getArgs();
        // 将参数转换为字符串形式，并用逗号分隔
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求开始日志，记录请求的ID、URL、IP地址和参数
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);
        // 执行原来的方法（即被拦截的方法）
        Object result = point.proceed();
        // 停止计时并获取总耗时（毫秒）
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        // 输出请求结束日志，记录请求的ID和总耗时（毫秒）
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        // 返回原来的方法的执行结果
        return result;
    }
}

