package smart.base.util.genUtil.custom;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.util.Properties;

public enum VelocityEnum {
    init;

    public void initVelocity(String path){
        Properties p = new Properties();
        p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, path);
        p.setProperty("ISO-8859-1", Constants.UTF_8);
        p.setProperty("output.encoding", Constants.UTF_8);
        Velocity.init(p);
    }

}
