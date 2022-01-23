package smart.util;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author SmartCloud项目开发组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2021/3/16 10:57
 */
public class UpUtil {

    /**
     * 获取上传文件
     */
    public static List<MultipartFile> getFileAll(){
        MultipartResolver resolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest mRequest = resolver.resolveMultipart(ServletUtil.getRequest());
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        List<MultipartFile> list = new ArrayList<>();
        for(Map.Entry<String,MultipartFile> map : fileMap.entrySet()){
            list.add(map.getValue());
        }
        return list;
    }

    /**
     * 获取文件大小
     */
    public static long getFileSize(MultipartFile multipartFile){
       return multipartFile.getSize();
    }

    /**
     * 获取文件类型
     */
    public static String getFileType(MultipartFile multipartFile){
        if (multipartFile.getContentType()!=null) {
            int begin = multipartFile.getOriginalFilename().indexOf(".");
            int last = multipartFile.getOriginalFilename().length();
            String type = multipartFile.getOriginalFilename().substring(begin+1, last);
            return type;
        }
        return null;
    }

    /**
     * 上传文件
     */
    public static String upLoad(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        String fileName = file.getOriginalFilename();
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String time = formatter.format(date);
        String uuidFileName = time+"_"+ RandomUtil.uuId()+"@"+fileName;
        File dest = new File(uuidFileName);
        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest);
            return uuidFileName;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
