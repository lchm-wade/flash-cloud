package com.foco.cloud.core.hystrix;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.foco.cloud.core.feign.FeignException;
import com.foco.cloud.core.feign.NoFeignHandlerException;
import com.foco.model.ApiResult;
import com.foco.model.constant.FocoErrorCode;
import com.foco.model.exception.SystemException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * description:  feign client把大于等于200小于300的都算成error。 需要
 * 自定义feign错误解析器,把部分异常，适配为HystrixBadRequestException，
 * 避开circuit breaker的统计，否则就容易误判，造成非预期的服务熔断
 *
 * @Author lucoo
 * @Date 2021/6/2 10:17
 */
@Slf4j
public class FeignHystrixErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody;
        log.info("decoder feign error......");
        try {
            if (response == null || response.body() == null) {
                return new HystrixBadRequestException("feign response is null");
            }
            responseBody = IoUtil.read(response.body().asInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ie) {
            log.error(ie.getMessage(), ie);
            return new SystemException(FocoErrorCode.SYSTEM_ERROR);
        }
        log.info("feign request response:{}",responseBody);
        if(response.status()==HttpStatus.NOT_FOUND.value()){
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            return new NoFeignHandlerException(FocoErrorCode.PATH_ERROR.getCode(),String.format("No handler found for %s",jsonObject.get("path")));
        }
        ApiResult apiResult = JSON.parseObject(responseBody, ApiResult.class);
        String url = response.request().url();
        String errorMsg = " feign request fail--> " + url + ", status: " + apiResult.getCode() + ", error: " + apiResult.getMsg();
        return new HystrixBadRequestException(errorMsg, new FeignException(apiResult.getCode(), apiResult.getMsg()));
    }
}
