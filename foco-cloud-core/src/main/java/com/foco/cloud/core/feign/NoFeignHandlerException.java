package com.foco.cloud.core.feign;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2021/09/17 17:01
 */
public class NoFeignHandlerException extends RuntimeException{
    /***错误码*/
    private String code;
    public NoFeignHandlerException(String code, String errorMsg) {
        super(errorMsg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
