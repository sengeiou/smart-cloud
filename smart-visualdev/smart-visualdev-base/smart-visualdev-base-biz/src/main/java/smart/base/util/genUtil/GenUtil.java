package smart.base.util.genUtil;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import smart.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GenUtil {

    /**
     * 获取文件名
     */
    public static String getFileName(String path, String template) {
        String htmlPath = path + File.separator;
        File file = new File(htmlPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (template.contains("Form.js.vm" )) {
            return htmlPath + File.separator + "Form.js";
        }
        if (template.contains("Form.html.vm" )) {
            return htmlPath + File.separator + "Form.html";
        }
        if (template.contains("Index.html.vm" )) {
            return htmlPath + File.separator + "Index.html";
        }
        if (template.contains("Index.js.vm" )) {
            return htmlPath + File.separator + "Index.js";
        }
        if (template.contains("Form.vue.vm" )) {
            return htmlPath + File.separator + "form.vue";
        }
        if (template.contains("Index.vue.vm" )) {
            return htmlPath + File.separator + "index.vue";
        }
        if (template.contains("tem.vue.vm" )) {
            return htmlPath + File.separator + "tem.vue";
        }
        return null;
    }

    //读取文件
    public static String readFileContent(File fileNam) {
        String str = "";
        StringBuilder builder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileNam.getAbsolutePath()), "UTF-8" ));
            while ((str = bufferedReader.readLine()) != null) {
                builder.append(str.replaceAll("<textarea", "&lt;textarea" ).replaceAll("</textarea>", "&lt;/textarea&gt;" ) + "\n" );
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return builder.toString();
    }

    //增加界面的模板
    private static List<String> getTemplates(String template) {
        List<String> templates = new ArrayList<>();
        templates.add(template + File.separator + "html" + File.separator + "Form.html.vm" );
        templates.add(template + File.separator + "html" + File.separator + "Form.js.vm" );
        templates.add(template + File.separator + "html" + File.separator + "Index.html.vm" );
        templates.add(template + File.separator + "html" + File.separator + "Index.js.vm" );
        return templates;
    }

    //渲染html模板
    public static void htmlTemplates(String path, Object object, String templatePath) {
        List<String> templates = getTemplates(templatePath);
        //界面模板
        VelocityContext context = new VelocityContext();
        context.put("context", object);
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF_8);
            tpl.merge(context, sw);
            try {
                Map<String, Object> map = JsonUtil.stringToMap(JsonUtil.getObjectToString(object));
                String fileNames = GenUtil.getFileName(path + File.separator + map.get("areasName" ) + File.separator + map.get("className" ), template);
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
                System.out.println("渲染模板失败，表名：" + e);
            }
        }
    }

    //读取文件的file
    public static JSONObject readFile(String path, String className, String modleName, String fileName, List<String> entitys, List<String> vues) {
        JSONObject object = new JSONObject();
        Map<String, Object> displayTabs = new LinkedHashMap<>();
        //代码的模块
        File entity = new File(path + File.separator + "java" + File.separator + "entity" + File.separator + className + "Entity.java" );
        if (entity.exists()) {
            object.put("EntityCode", readFileContent(entity));
            displayTabs.put("EntityCode", "Entity.java" );
        }
        //显示多个实体类
        entityList(path, object, displayTabs, entitys, entity);
        File mapper = new File(path + File.separator + "java" + File.separator + "mapper" + File.separator + className + "Mapper.java" );
        if (mapper.exists()) {
            object.put("MapperCode", readFileContent(mapper));
            displayTabs.put("MapperCode", "Mapper.java" );
        }
        File service = new File(path + File.separator + "java" + File.separator + "service" + File.separator + className + "Service.java" );
        if (service.exists()) {
            object.put("ServiceCode", readFileContent(service));
            displayTabs.put("ServiceCode", "Service.java" );
        }
        File serviceImpl = new File(path + File.separator + "java" + File.separator + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java" );
        if (serviceImpl.exists()) {
            object.put("ServiceImplCode", readFileContent(serviceImpl));
            displayTabs.put("ServiceImplCode", "ServiceImpl.java" );
        }
        File controller = new File(path + File.separator + "java" + File.separator + "controller" + File.separator + className + "Controller.java" );
        if (controller.exists()) {
            object.put("ControllerCode", readFileContent(controller));
            displayTabs.put("ControllerCode", "Controller.java" );
        }
        //xml文件
        File XML = new File(path + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml" );
        if (controller.exists()) {
            object.put("XMLCode", readFileContent(XML));
            displayTabs.put("XMLCode", "mapper.xml" );
        }


        //界面模块
        File indexHtml = new File(path + File.separator + modleName + File.separator + className + File.separator + "Index.html" );
        if (indexHtml.exists()) {
            object.put("IndexPageCode", readFileContent(indexHtml));
            displayTabs.put("IndexPageCode", "Index.html" );
        }
        File indexJs = new File(path + File.separator + modleName + File.separator + className + File.separator + "Index.js" );
        if (indexJs.exists()) {
            object.put("IndexJsCode", readFileContent(indexJs));
            displayTabs.put("IndexJsCode", "Index.js" );
        }
        File formHtml = new File(path + File.separator + modleName + File.separator + className + File.separator + "Form.html" );
        if (formHtml.exists()) {
            object.put("FormPageCode", readFileContent(formHtml));
            displayTabs.put("FormPageCode", "Form.html" );
        }
        File formJs = new File(path + File.separator + modleName + File.separator + className + File.separator + "Form.js" );
        if (formJs.exists()) {
            object.put("FormJsCode", readFileContent(formJs));
            displayTabs.put("FormJsCode", "Form.js" );
        }
        File form = new File(path + File.separator + modleName + File.separator + className + File.separator + "Form.vue" );
        if (form.exists()) {
            object.put("FormVueCode", readFileContent(form));
            displayTabs.put("FormVueCode", "Form.vue" );
        }
        File index = new File(path + File.separator + modleName + File.separator + className + File.separator + "Index.vue" );
        if (index.exists()) {
            object.put("IndexVueCode", readFileContent(index));
            displayTabs.put("IndexVueCode", "Index.vue" );
        }
        //显示vue子表
        vueList(path, object, displayTabs, vues);
        object.put("displayTabs", displayTabs);
        object.put("displayType", "brush: java" );
        object.put("fileName", fileName);
        return object;
    }

    //多个实体
    private static void entityList(String path, JSONObject object, Map<String, Object> displayTabs, List<String> entitys, File entity) {
        //显示多个实体类
        for (String classNames : entitys) {
            File names = new File(path + File.separator + "java" + File.separator + "entity" + File.separator + classNames + "Entity" + StringPool.DOT_JAVA);
            if (entity.exists()) {
                object.put("EntityCode", object.get("EntityCode" ) + "\r\n" + readFileContent(names));
            }
        }
    }

    //多个vue
    private static void vueList(String path, JSONObject object, Map<String, Object> displayTabs, List<String> vues) {
        //显示vue子表
        for (String name : vues) {
            File names = new File(path + File.separator + "html" + File.separator + name + "List.vue" );
            if (names.exists()) {
                object.put(name + "Code", readFileContent(names));
                displayTabs.put(name + "Code", name + ".vue" );
            }
        }
    }

}
