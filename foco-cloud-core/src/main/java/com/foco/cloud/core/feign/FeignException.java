package com.foco.cloud.core.feign;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/09/17 16:38
 */
public class FeignException extends RuntimeException {
    /***错误码*/
    private String code;
    public FeignException(String code, String errorMsg) {
        super(errorMsg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
