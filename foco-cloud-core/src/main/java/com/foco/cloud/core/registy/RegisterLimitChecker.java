package com.foco.cloud.core.registy;

import com.foco.model.exception.ApiException;
import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author lucoo
 * @version 1.0.0
 * @description 阻止本地环境注册到nacos的dev,test等环境
 * @date 2022/02/09 13:32
 * @since foco2.3.1
 */
public class RegisterLimitChecker{
    private List<InitCheck> initChecks;

    public RegisterLimitChecker(List<InitCheck> initChecks) {
        this.initChecks = initChecks;
    }
    @PostConstruct
    public void init(){
        initChecks.forEach((initCheck)->{
            boolean check = initCheck.check();
            if(!check){
                ApiException apiException = initCheck.throwException();
                throw apiException;
            }
        });
    }
}
