package com.foco.gateway.exception;

import com.foco.context.core.SpringContextHolder;
import com.foco.internation.LocaleEntity;
import com.foco.internation.resolver.MessageResolver;
import com.foco.model.ApiResult;
import com.foco.model.constant.FocoConstants;
import com.foco.model.constant.FocoErrorCode;
import com.foco.model.constant.MainClassConstant;
import com.foco.model.exception.SystemException;
import com.foco.properties.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ClassUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

/**
 * @description: 网关服务异常处理
 * @Author lucoo
 * @Date 2021/6/2 14:27
 */
@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                        ResourceProperties resourceProperties,
                                        ErrorProperties errorProperties,
                                        ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        //异常
        Throwable throwable = super.getError(request);
        ServerHttpRequest serverHttpRequest = request.exchange().getRequest();
        ErrorAttributeOptions errorAttributeOptions = super.getErrorAttributeOptions(request, MediaType.ALL);
        //获取原始的HttpStatus
        int status = getHttpStatus(super.getErrorAttributes(request, errorAttributeOptions));
        if (throwable instanceof SystemException) {
            status = HttpStatus.OK.value();
        }
        return ServerResponse.status(status).
                contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customerErrorAttributes(throwable, serverHttpRequest)));
    }

    /**
     * 自定义返回的错误消息
     */
    private ApiResult customerErrorAttributes(Throwable throwable, ServerHttpRequest serverHttpRequest) {
        //定义错误返回值
        if (throwable instanceof SystemException) {
            log.warn("网关异常",throwable);
            SystemException systemException = ((SystemException) throwable);
            ApiResult apiResult = buildResponse(systemException.getCode());
            HttpHeaders headers = serverHttpRequest.getHeaders();
            // 1.从请求头中获取
            String locale = headers.getFirst(SystemConfig.getConfig().getLocaleHead());
            if(ClassUtils.isPresent(MainClassConstant.FOCO_INTERNATIONAL,this.getClass().getClassLoader())){
                apiResult.setMsg(SpringContextHolder.getBean(MessageResolver.class).resolveMessage(
                        new LocaleEntity().setLocale(locale).setType(FocoConstants.ERROR_CODE_FILE)
                        , systemException.getCode(), systemException.getMessage()));
            }else {
                apiResult.setMsg(systemException.getMessage());
            }
            return apiResult;
        } else if (throwable instanceof NotFoundException) {
            log.error("路径错误",throwable);
            ApiResult apiResult = buildResponse(FocoErrorCode.PATH_ERROR.getCode());
            apiResult.setMsg(((NotFoundException) throwable).getReason());
            return apiResult;
        } else {
            log.error("网关异常",throwable);
            ApiResult apiResult = buildResponse(FocoErrorCode.GATE_WAY_ERROR.getCode());
            apiResult.setMsg(FocoErrorCode.GATE_WAY_ERROR.getMsg());
            return apiResult;
        }
    }

    private ApiResult buildResponse(String code) {
        ApiResult response = new ApiResult();
        response.setSuccess(false);
        response.setCode(code);
        response.setTraceId(TraceContext.traceId());
        return response;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }
}
