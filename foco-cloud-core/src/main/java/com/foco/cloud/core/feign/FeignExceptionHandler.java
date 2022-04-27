package com.foco.cloud.core.feign;

import com.foco.model.ApiResult;
import com.foco.model.constant.ExceptionHandlerOrderConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * description: 对FeignException 异常的拦截
 *
 * @Author lucoo
 * @Date 2021/9/17 9:57
 */
@Slf4j
@Order(ExceptionHandlerOrderConstants.FEIGN)
@RestControllerAdvice
public class FeignExceptionHandler {
    @ExceptionHandler(value = FeignException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResult feignException(FeignException e) {
        log.error("---FeignException--exception", e);
        ApiResult<Object> result = ApiResult.error(e.getCode(), e.getMessage());
        return result;
    }
    @ExceptionHandler(value = NoFeignHandlerException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult NoFeignHandlerException(NoFeignHandlerException e) {
        log.error("---NoFeignHandlerException--exception", e);
        return ApiResult.error(e.getCode(), e.getMessage());
    }
}
