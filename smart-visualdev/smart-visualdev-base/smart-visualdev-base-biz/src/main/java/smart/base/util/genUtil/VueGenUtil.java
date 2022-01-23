package smart.base.util.genUtil;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VueGenUtil {
    /**
     * 获取文件名
     */
    public static String getFileName(String path, String template,String className,String packName,Boolean hasPage) {
        String modelPath = path + File.separator +"java"+ File.separator +"model"+ File.separator + packName.toLowerCase();
        String htmlPath = path + File.separator +"html"+ File.separator + packName.toLowerCase();
        File modelfile = new File(modelPath);
        File htmlfile = new File(htmlPath);
        if (!htmlfile.exists()) {
            htmlfile.mkdirs();
        }
        if (!modelfile.exists()) {
            modelfile.mkdirs();
        }
        if (template.contains("CrForm.java.vm" )) {
            return modelPath + File.separator + className+"CrForm.java";
        }
        if (template.contains("UpForm.java.vm" )) {
            return modelPath+ File.separator + className+"UpForm.java";
        }
        if (template.contains("ListVO.java.vm" )) {
            return modelPath+ File.separator+ className+"ListVO.java";
        }
        if (template.contains("InfoVO.java.vm" )) {
            return modelPath+ File.separator + className+"InfoVO.java";
        }
        if(hasPage==null||hasPage){
            if (template.contains("Pagination.java.vm" )) {
                return modelPath+ File.separator + className+"Pagination.java";
            }
        }else{
            if (template.contains("ListQuery.java.vm" )) {
                return modelPath+ File.separator + className+"ListQuery.java";
            }
        }
        if (template.contains("PaginationExportModel.java.vm" )) {
            return modelPath+ File.separator + className+"PaginationExportModel.java";
        }
        if (template.contains("Form.vue.vm" )) {
            return htmlPath+ File.separator + "Form.vue";
        }
        if (template.contains("index.vue.vm" )) {
            return htmlPath + File.separator + "index.vue";
        }
        if (template.contains("ExportBox.vue.vm" )) {
            return htmlPath+ File.separator + "ExportBox.vue";
        }
        return null;
    }

    //增加界面的模板
    private static List<String> getTemplates(String template,String type) {
        List<String> templates = new ArrayList<>();
        if("model".equals(type)){
            templates.add(template + File.separator + "java" + File.separator + "CrForm.java.vm" );
            templates.add(template + File.separator + "java" + File.separator + "UpForm.java.vm" );
            templates.add(template + File.separator + "java" + File.separator + "ListVO.java.vm" );
            templates.add(template + File.separator + "java" + File.separator + "InfoVO.java.vm" );
            templates.add(template + File.separator + "java" + File.separator + "ListQuery.java.vm" );
            templates.add(template + File.separator + "java" + File.separator + "Pagination.java.vm" );
            templates.add(template + File.separator + "java" + File.separator + "PaginationExportModel.java.vm" );
        }else if("vue".equals(type)){
            templates.add(template + File.separator + "html" + File.separator + "Form.vue.vm" );
            templates.add(template + File.separator + "html" + File.separator + "index.vue.vm" );
            templates.add(template + File.separator + "html" + File.separator + "ExportBox.vue.vm" );
        }
        return templates;
    }
    //渲染html模板
    public static void htmlTemplates(String path, Object object, String templatePath,String type,String packName,Boolean hasPage) {
        List<String> templates = getTemplates(templatePath,type);
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context", object);
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
            tpl.merge(context, sw);
            try {
                String fileNames = getFileName(path, template,packName,packName,hasPage);
                if (fileNames != null) {
                    File file = new File(fileNames);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.write(sw.toString(), fos, Constants.UTF_8);
                    IOUtils.closeQuietly(sw);
                    IOUtils.closeQuietly(fos);
                }
            } catch (IOException e) {
                log.error("渲染模板失败，" + e.getMessage());
            }
        }
    }
}
