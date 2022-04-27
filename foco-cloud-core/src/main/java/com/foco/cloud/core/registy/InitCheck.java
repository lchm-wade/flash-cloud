package com.foco.cloud.core.registy;

import com.foco.model.exception.ApiException;

public interface InitCheck {
    /**
     *容器启动检查先决条件
     * @return false 阻止容器启动
     * true 允许容器启动
     */
    boolean check();
    /**
     *
     * @return 当check()返回false时,调用throwException()方法来获取异常
     */
    ApiException throwException();
}
