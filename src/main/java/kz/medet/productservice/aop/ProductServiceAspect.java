package kz.medet.productservice.aop;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProductServiceAspect {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceAspect.class);

    @Before("execution(* kz.medet.productservice.service.ProductService.*(..))")
    public void logBeforeMethod() {
        log.info("Method in ProductService is about to execute");
    }

    @AfterReturning(pointcut = "execution(* kz.medet.productservice.service.ProductService.*(..))", returning = "result")
    public void logAfterReturning(Object result) {
        log.info("Method in ProductService executed successfully. Result: {}", result);
    }

    @AfterThrowing(pointcut = "execution(* kz.medet.productservice.service.ProductService.*(..))", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        log.error("Method in ProductService threw an exception: {}", ex.getMessage());
    }

    @Around("execution(* kz.medet.productservice.service.ProductService.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Executing method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            log.info("Method executed successfully: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Exception in method: {} - {}", joinPoint.getSignature(), e.getMessage());
            throw e;
        }
    }
}

