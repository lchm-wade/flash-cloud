package com.foco.cloud.core.trace;

import com.foco.model.ApiResult;
import com.foco.model.spi.TraceIdBuilder;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/10/09 11:30
 */
public class TraceApiResultBuilder implements TraceIdBuilder {
    @Override
    public void buildTraceId(ApiResult result) {
        result.setTraceId(TraceContext.traceId());
    }
}
