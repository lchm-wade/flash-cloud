package com.foco.cloud.core.registy;
import com.foco.properties.AbstractProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lucoo
 * @version 1.0.0
 * @description TODO
 * @date 2022/02/09 13:41
 * @since foco2.3.1
 */
@Getter
@Setter
@ConfigurationProperties(prefix= RegisterLimitProperties.PREFIX)
public class RegisterLimitProperties extends AbstractProperties {
    public static RegisterLimitProperties getConfig(){
        return getConfig(RegisterLimitProperties.class);
    }
    public static final String PREFIX="foco.env-mapping";
    private Env local=new Env("local",false);
    private Env dev=new Env("dev",false);
    private Env test=new Env("test",true);
    private Env fix=new Env("fix",true);
    private Env prod=new Env("prod",true);
    class Env{
        private String namespace;
        private boolean limitRegister;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public boolean isLimitRegister() {
            return limitRegister;
        }

        public void setLimitRegister(boolean limitRegister) {
            this.limitRegister = limitRegister;
        }

        public Env(String namespace, boolean limitRegister) {
            this.namespace = namespace;
            this.limitRegister = limitRegister;
        }
    }
}
