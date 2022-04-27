package com.foco.gateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.foco.model.constant.FocoErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucoo
 * @version 1.0.0
 * @Description TODO
 * @date 2021-06-24 10:32
 */
public class FocoGatewaySentinelExceptionHandler implements BlockRequestHandler {
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
        Map<String,Object> map=new HashMap<>();
        map.put("code",FocoErrorCode.SENTINEL_FLOW_ERROR.getCode());
        map.put("msg",FocoErrorCode.SENTINEL_FLOW_ERROR.getMsg());
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(map));
    }
}
