package cn.edu.xmu.oomall.annotation;

import cn.edu.xmu.oomall.util.JwtHelper;
import cn.edu.xmu.oomall.util.ResponseCode;
import cn.edu.xmu.oomall.util.ResponseUtil;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Json返回值过滤
 *
 * @author wwc
 * @date 2020/11/22 09:42
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class JsonFieldFilterAspect {
    //Controller层切点
    @Pointcut("@annotation(cn.edu.xmu.oomall.annotation.JsonFieldFilter)")
    public void auditAspect() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("auditAspect()")
    public void before(JoinPoint joinPoint) {
    }

    /**
     *
     * 环绕通知,使用在方法aspect()上注册的切入点
     *
     * @param joinPoint 切点
     */
    @Around("auditAspect()")
    public Object around(JoinPoint joinPoint) {
        log.debug("after: joinPoint = "+ joinPoint);
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        Object obj = null;
        try {
            obj = ((ProceedingJoinPoint) joinPoint).proceed(joinPoint.getArgs());
            JsonFieldFilter jsonFilter = method.getAnnotation(JsonFieldFilter.class);
            //调用过滤方法
            JsonFilterSerializer serializer = new JsonFilterSerializer();
            serializer.filter(jsonFilter.type() == null ? obj.getClass() : jsonFilter.type(), jsonFilter.include(), jsonFilter.exclude());
            return serializer.toJson(obj);
        } catch (Throwable e) {

        }
        return obj;
    }

    @After("auditAspect()")
    public void after() {
    }

}
