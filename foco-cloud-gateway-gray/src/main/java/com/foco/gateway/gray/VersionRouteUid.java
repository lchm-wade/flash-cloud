package com.foco.gateway.gray;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ChenMing
 * @date 2021/9/18
 */
@Getter
@Setter
public class VersionRouteUid extends RouteUid {
    /**
     * 前端的版本
     * Todo 请求的版本为此version时才生效，如果没有则不判断
     */
    private String version;

}
