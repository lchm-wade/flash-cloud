package com.foco.cloud.core.feign;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.foco.model.ApiResult;
import com.foco.model.constant.FocoErrorCode;
import com.foco.model.exception.SystemException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * description:
 * @Author lucoo
 * @Date 2021/6/2 10:17
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody;
        log.info("decoder feign error......");
        try {
            if (response == null || response.body() == null) {
                return new SystemException("feign response is null");
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
        // 返回框架可识别的异常
        return new FeignException(apiResult.getCode(),apiResult.getMsg());
    }
}
