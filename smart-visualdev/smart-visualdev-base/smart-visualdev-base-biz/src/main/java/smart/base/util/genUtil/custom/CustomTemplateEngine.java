package smart.base.util.genUtil.custom;


import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import smart.util.StringUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

public class CustomTemplateEngine extends AbstractTemplateEngine {

    private static final String DOT_VM = ".vm";
    private VelocityEngine velocityEngine;

    private Map<String, Object> customParams;

    private String path;

    public CustomTemplateEngine(String path) {
        this.path = path;
    }

    public CustomTemplateEngine(Map<String, Object> customParams, String path) {
        this.customParams = customParams;
        this.path = path;
    }

    @Override
    public CustomTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        if (null == this.velocityEngine) {
            Properties p = new Properties();
            p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, path);
            p.setProperty("ISO-8859-1", Constants.UTF_8);
            p.setProperty("output.encoding", Constants.UTF_8);
            this.velocityEngine = new VelocityEngine(p);
        }

        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        if (!StringUtil.isEmpty(templatePath)) {
            Template template = this.velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, ConstVal.UTF8));
            if(customParams!= null){
                objectMap.putAll(customParams);
            }
            template.merge(new VelocityContext(objectMap), writer);
            writer.close();
        }
    }

    @Override
    public String templateFilePath(String filePath) {
        if (null != filePath && !filePath.contains(".vm")) {
            StringBuilder fp = new StringBuilder();
            fp.append(filePath).append(".vm");
            return fp.toString();
        } else {
            return filePath;
        }
    }
}
