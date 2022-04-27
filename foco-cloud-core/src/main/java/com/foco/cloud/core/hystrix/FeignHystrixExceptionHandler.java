package com.foco.cloud.core.hystrix;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.foco.cloud.core.feign.FeignException;
import com.foco.model.ApiResult;
import com.foco.model.constant.ExceptionHandlerOrderConstants;
import com.foco.model.constant.FocoErrorCode;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * description: 增加对HystrixRuntimeException 异常的拦截
 *
 * @Author lucoo
 * @Date 2021/6/2 9:57
 */
@Slf4j
@Order(ExceptionHandlerOrderConstants.FEIGN_HYSTRIX)
@RestControllerAdvice
public class FeignHystrixExceptionHandler {
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = {HystrixRuntimeException.class})
    public ApiResult hystrixRuntimeExceptionHandler(HystrixRuntimeException e) {
        log.error("---HystrixRuntimeException Handler ", e);
        return ApiResult.error(FocoErrorCode.SYSTEM_ERROR.getCode(),e.getMessage());
    }
    @ExceptionHandler(value = {HystrixBadRequestException.class})
    @ResponseStatus(HttpStatus.OK)
    public ApiResult HystrixBadRequestException(HystrixBadRequestException e) {
        log.error("---HystrixBadRequestException Handler ", e);
        if(e.getCause()!=null && e.getCause() instanceof FeignException){
            FeignException feignException = (FeignException) e.getCause();
            ApiResult<Object> result = ApiResult.error(feignException.getCode(), feignException.getMessage());
            return result;
        }
        return ApiResult.error(FocoErrorCode.SYSTEM_ERROR.getCode(),e.getMessage());
    }
}
