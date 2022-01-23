package smart.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.file.FileApi;
import smart.mapper.DocumentMapper;
import smart.service.DocumentService;
import smart.service.DocumentShareService;
import smart.entity.DocumentEntity;
import smart.entity.DocumentShareEntity;
import smart.emnus.FileTypeEnum;
import smart.util.FileUtil;
import smart.util.RandomUtil;
import smart.util.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 知识文档
 *
 * @author 开发平台组
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, DocumentEntity> implements DocumentService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private DocumentShareService documentShareService;
    @Autowired
    private FileApi fileApi;

    @Override
    public List<DocumentEntity> getFolderList() {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getType, 0)
                .eq(DocumentEntity::getDeleteMark, 0)
                .orderByAsc(DocumentEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getAllList(String parentId) {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getDeleteMark, 0)
                .eq(DocumentEntity::getParentId, parentId)
                .orderByAsc(DocumentEntity::getType)
                .orderByAsc(DocumentEntity::getCreatorTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getTrashList() {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getDeleteMark, 1)
                .orderByAsc(DocumentEntity::getType)
                .orderByDesc(DocumentEntity::getDeleteTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getShareOutList() {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(DocumentEntity::getCreatorUserId, userProvider.get().getUserId())
                .eq(DocumentEntity::getDeleteMark, 0)
                .gt(DocumentEntity::getIsShare, 0)
                .orderByAsc(DocumentEntity::getType)
                .orderByDesc(DocumentEntity::getDeleteTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<DocumentEntity> getShareTomeList() {
        return this.baseMapper.getShareTomeList(userProvider.get().getUserId());
    }

    @Override
    public List<DocumentShareEntity> getShareUserList(String documentId) {
        QueryWrapper<DocumentShareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentShareEntity::getDocumentId, documentId);
        return documentShareService.list(queryWrapper);
    }

    @Override
    public DocumentEntity getInfo(String id) {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentEntity::getId, id);
        return this.getOne(queryWrapper);
    }

    @Override
    public void delete(DocumentEntity entity) {
        entity.setDeleteTime(new Date());
        entity.setDeleteUserId(userProvider.get().getUserId());
        entity.setDeleteMark(1);
        this.updateById(entity);
    }

    @Override
    public void create(DocumentEntity entity) {
        entity.setId(RandomUtil.uuId());
        entity.setCreatorUserId(userProvider.get().getUserId());
        entity.setDeleteMark(0);
        this.save(entity);
    }

    @Override
    public boolean update(String id, DocumentEntity entity) {
        entity.setId(id);
        entity.setLastModifyTime(new Date());
        entity.setLastModifyUserId(userProvider.get().getUserId());
        return this.updateById(entity);
    }

    @Override
    @Transactional
    public boolean sharecreate(String documentId, String[] shareUserId) {
        List<DocumentShareEntity> entitys = new ArrayList<>();
        for (String item : shareUserId) {
            DocumentShareEntity entity = new DocumentShareEntity();
            entity.setId(RandomUtil.uuId());
            entity.setDocumentId(documentId);
            entity.setShareUserId(item);
            entity.setShareTime(new Date());
            entitys.add(entity);
        }
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        DocumentEntity entity = this.getOne(queryWrapper.lambda().eq(DocumentEntity::getId, documentId));
        if (entity != null) {
            entity.setIsShare(entitys.size());
            entity.setShareTime(new Date());
            this.updateById(entity);
            QueryWrapper<DocumentShareEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(DocumentShareEntity::getDocumentId, documentId);
            documentShareService.remove(wrapper);
            for (DocumentShareEntity shareEntity : entitys) {
                documentShareService.save(shareEntity);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean shareCancel(String documentId) {
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentEntity::getId, documentId);
        DocumentEntity entity = this.getOne(queryWrapper);
        if (entity != null) {
            entity.setIsShare(0);
            entity.setShareTime(new Date());
            this.updateById(entity);
            QueryWrapper<DocumentShareEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(DocumentShareEntity::getDocumentId, documentId);
            documentShareService.remove(wrapper);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void trashdelete(String folderId) {
        DocumentEntity entity = this.getInfo(folderId);
        if(entity!=null){
            this.removeById(folderId);
            //文件保存路径
            String path = fileApi.getPath(FileTypeEnum.DOCUMENT);
            FileUtil.deleteFile(path + entity.getFilePath());
        }
//        List<DocumentEntity> list = this.baseMapper.GetChildList(folderId);
//        List<String> deleteId = new ArrayList<>();
//        for (DocumentEntity entity : list) {
//            if(!StringUtil.isEmpty(entity.getFilePath())){
//                FileUtil.deleteFile(configValueUtil.getDocumentFilePath() + entity.getFilePath());
//            }
//            deleteId.add(entity.getId());
//        }
//        this.removeByIds(deleteId);
    }

    @Override
    public boolean trashRecovery(String id) {
       return retBool(this.baseMapper.trashRecovery(id));
    }

    @Override
    public boolean moveTo(String id, String toId) {
        DocumentEntity entity = this.getInfo(id);
        if(entity!=null){
            entity.setParentId(toId);
            this.updateById(entity);
           return true;
        }
       return false;
    }

    @Override
    public boolean isExistByFullName(String fullName, String id) {
        String userId=userProvider.get().getUserId();
        QueryWrapper<DocumentEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DocumentEntity::getFullName,fullName).eq(DocumentEntity::getDeleteMark,0).eq(DocumentEntity::getCreatorUserId,userId);
        if(!StringUtils.isEmpty(id)){
            queryWrapper.lambda().ne(DocumentEntity::getId, id);
        }
        return this.count(queryWrapper) > 0 ? true : false;
    }
}
