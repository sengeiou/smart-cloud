package smart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import smart.base.entity.EmailConfigEntity;
import smart.base.model.MailAccount;
import smart.emnus.FileTypeEnum;
import smart.entity.EmailReceiveEntity;
import smart.entity.EmailSendEntity;
import smart.file.FileApi;
import smart.mapper.EmailReceiveMapper;
import smart.service.EmailConfigService;
import smart.service.EmailReceiveService;
import smart.service.EmailSendService;
import smart.util.*;
import smart.model.MailFile;
import smart.model.MailModel;
import smart.base.PaginationTime;
import smart.exception.DataException;
import smart.util.type.StringNumber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邮件接收
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Slf4j
@Service
public class EmailReceiveServiceImpl extends ServiceImpl<EmailReceiveMapper, EmailReceiveEntity> implements EmailReceiveService {

    @Autowired
    private UserProvider userProvider;
    @Autowired
    private EmailSendService emailSendService;
    @Autowired
    private EmailConfigService emailConfigService;
    @Autowired
    private Pop3Util pop3Util;
    @Autowired
    private FileApi fileApi;

    @Override
    public List<EmailReceiveEntity> getReceiveList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailReceiveEntity::getFdate, startTimes).le(EmailReceiveEntity::getFdate, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailReceiveEntity::getSender, word)
                            .or().like(EmailReceiveEntity::getSubject, word)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmailReceiveEntity::getFdate);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<EmailReceiveEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailReceiveEntity> userIPage = this.page(page, queryWrapper);
        return paginationTime.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public List<EmailReceiveEntity> getReceiveList() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId);

        return this.baseMapper.selectList(queryWrapper);
    }


    @Override
    public List<EmailReceiveEntity> getStarredList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailReceiveEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailReceiveEntity::getCreatorUserId, userId).eq(EmailReceiveEntity::getStarred, 1);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailReceiveEntity::getCreatorTime, startTimes).le(EmailReceiveEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailReceiveEntity::getSender, word)
                            .or().like(EmailReceiveEntity::getSubject, word)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmailReceiveEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<EmailReceiveEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailReceiveEntity> userIPage = this.page(page, queryWrapper);
        return paginationTime.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public List<EmailSendEntity> getDraftList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailSendEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailSendEntity::getCreatorUserId, userId).eq(EmailSendEntity::getState, -1);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getEndTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailSendEntity::getCreatorTime, startTimes).le(EmailSendEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? paginationTime.getKeyword() : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailSendEntity::getSender, word)
                            .or().like(EmailSendEntity::getSubject, word)
            );
        }
        //排序
        if (StringUtils.isEmpty(paginationTime.getSidx())) {
            queryWrapper.lambda().orderByDesc(EmailSendEntity::getCreatorTime);
        } else {
            queryWrapper = "asc".equals(paginationTime.getSort().toLowerCase()) ? queryWrapper.orderByAsc(paginationTime.getSidx()) : queryWrapper.orderByDesc(paginationTime.getSidx());
        }
        Page<EmailSendEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailSendEntity> userIPage = emailSendService.page(page, queryWrapper);
        return paginationTime.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public List<EmailSendEntity> getSentList(PaginationTime paginationTime) {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailSendEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailSendEntity::getCreatorUserId, userId).ne(EmailSendEntity::getState, -1);
        //日期范围（近7天、近1月、近3月、自定义）
        String startTime = paginationTime.getStartTime() != null ? paginationTime.getStartTime() : null;
        String endTime = paginationTime.getEndTime() != null ? paginationTime.getEndTime() : null;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            Date startTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(startTime)) + " 00:00:00");
            Date endTimes = DateUtil.stringToDate(DateUtil.daFormat(Long.parseLong(endTime)) + " 23:59:59");
            queryWrapper.lambda().ge(EmailSendEntity::getCreatorTime, startTimes).le(EmailSendEntity::getCreatorTime, endTimes);
        }
        //关键字（用户、IP地址、功能名称）
        String keyWord = paginationTime.getKeyword() != null ? String.valueOf(paginationTime.getKeyword()) : null;
        //关键字（发件人、主题）
        if (!StringUtils.isEmpty(keyWord)) {
            String word = keyWord;
            queryWrapper.lambda().and(
                    t -> t.like(EmailSendEntity::getSender, word)
                            .or().like(EmailSendEntity::getSubject, word)
            );
        }
        //排序
        String sidx = paginationTime.getSidx() != null ? paginationTime.getSidx() : null;
        if (!StringUtils.isEmpty(sidx)) {
            queryWrapper.lambda().orderByDesc(EmailSendEntity::getCreatorTime);
        }
        Page<EmailSendEntity> page = new Page<>(paginationTime.getCurrentPage(), paginationTime.getPageSize());
        IPage<EmailSendEntity> userIPage = emailSendService.page(page, queryWrapper);
        return paginationTime.setData(userIPage.getRecords(), page.getTotal());
    }

    @Override
    public EmailConfigEntity getConfigInfo() {
        String userId = userProvider.get().getUserId();
        QueryWrapper<EmailConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailConfigEntity::getCreatorUserId, userId);
        return emailConfigService.getOne(queryWrapper);
    }

    @Override
    public EmailConfigEntity getConfigInfo(String userId) {
        QueryWrapper<EmailConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EmailConfigEntity::getCreatorUserId, userId);
        return emailConfigService.getOne(queryWrapper);
    }

    @Override
    public Object getInfo(String id) {
        EmailReceiveEntity receiveInfo = this.getById(id);
        Object object;
        if (receiveInfo != null) {
            //解析内容
            receiveInfo.setBodyText(receiveInfo.getBodyText());
            //更新已读
            receiveInfo.setIsRead(1);
            receiveInfo.setLastModifyTime(new Date());
            receiveInfo.setLastModifyUserId(userProvider.get().getUserId());
            this.updateById(receiveInfo);
            object = receiveInfo;
        } else {
            EmailSendEntity sendInfo = emailSendService.getById(id);
            object = sendInfo;
        }
        return object;
    }

    @Override
    public boolean delete(String id) {
        Object object = getInfo(id);
        if (object != null && object instanceof EmailReceiveEntity) {
            //删除邮件
            EmailConfigEntity mailConfig = getConfigInfo();
            EmailReceiveEntity mailReceiveEntity = (EmailReceiveEntity) object;
            MailAccount mailAccount = new MailAccount();
            mailAccount.setAccount(mailConfig.getAccount());
            mailAccount.setPassword(mailConfig.getPassword());
            mailAccount.setPop3Port(mailConfig.getPop3Port());
            mailAccount.setPop3Host(mailConfig.getPop3Host());
            pop3Util.deleteMessage(mailAccount, mailReceiveEntity.getMID());
            this.removeById(mailReceiveEntity.getId());
            return true;
        } else if (object != null) {
            //删除数据
            EmailSendEntity entity = (EmailSendEntity) object;
            emailSendService.removeById(entity.getId());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void saveDraft(EmailSendEntity entity) {
        entity.setState(-1);
        if (entity.getId() != null) {
            entity.setLastModifyTime(new Date());
            entity.setLastModifyUserId(userProvider.get().getUserId());
            emailSendService.updateById(entity);
        } else {
            entity.setId(RandomUtil.uuId());
            entity.setCreatorUserId(userProvider.get().getUserId());
            emailSendService.save(entity);
        }
    }

    @Override
    public boolean receiveRead(String id, int isRead) {
        EmailReceiveEntity entity = (EmailReceiveEntity) getInfo(id);
        if (entity != null) {
            entity.setIsRead(isRead);
            return this.updateById(entity);
        }
        return false;
    }

    @Override
    public boolean receiveStarred(String id, int isStarred) {
        EmailReceiveEntity entity = (EmailReceiveEntity) getInfo(id);
        if (entity != null) {
            entity.setStarred(isStarred);
            return this.updateById(entity);
        }
        return false;
    }

    @Override
    public boolean checkLogin(EmailConfigEntity configEntity) {
        MailAccount mailAccount = new MailAccount();
        mailAccount.setAccount(configEntity.getAccount());
        mailAccount.setPassword(configEntity.getPassword());
        mailAccount.setPop3Host(configEntity.getPop3Host());
        mailAccount.setPop3Port(configEntity.getPop3Port());
        mailAccount.setSmtpHost(configEntity.getSmtpHost());
        mailAccount.setSmtpPort(configEntity.getSmtpPort());
        if (StringNumber.ONE.equals(String.valueOf(configEntity.getEmailSsl()))) {
            mailAccount.setSsl(true);
        } else {
            mailAccount.setSsl(false);
        }
        if (mailAccount.getSmtpHost() != null) {
            return SmtpUtil.checkConnected(mailAccount);
        }
        if (mailAccount.getPop3Host() != null) {
            return pop3Util.checkConnected(mailAccount);
        }
        return false;
    }

    @Override
    public void saveConfig(EmailConfigEntity configEntity) throws DataException {
        if (getConfigInfo(userProvider.get().getUserId()) == null && userProvider.get().getUserId() != null) {
            configEntity.setId(RandomUtil.uuId());
            configEntity.setCreatorTime(new Date());
            configEntity.setCreatorUserId(userProvider.get().getUserId());
            emailConfigService.save(configEntity);
        } else if (userProvider.get().getUserId() != null) {
            emailConfigService.updateById(configEntity);
        } else {
            throw new DataException("保存失败，请重新登陆");
        }
    }

    @Override
    @Transactional
    public int saveSent(EmailSendEntity entity, EmailConfigEntity mailConfig) {
        int flag = 1;
        //拷贝文件,注意：从临时文件夹拷贝到邮件文件夹
        List<MailFile> attachmentList = JsonUtil.getJsonToList(entity.getAttachment(), MailFile.class);
        //临时文件路径
        String temporaryFile = fileApi.getPath(FileTypeEnum.TEMPORARY);
        //邮件路径
        String mailFilePath = fileApi.getPath(FileTypeEnum.MAIL);
        for (MailFile mailFile : attachmentList) {
            FileUtil.copyFile(temporaryFile + mailFile.getFileId(), mailFilePath + mailFile.getFileId());
        }
        try {
            //写入数据
            //发送邮件
            //邮件发送信息
            MailModel mailModel = new MailModel();
            mailModel.setFrom(entity.getSender());
            mailModel.setRecipient(entity.getRecipient());
            mailModel.setCc(entity.getCc());
            mailModel.setBcc(entity.getBcc());
            mailModel.setSubject(entity.getSubject());
            mailModel.setBodyText(entity.getBodyText());
            mailModel.setAttachment(attachmentList);
            mailModel.setFromName(mailConfig.getSenderName());
            //账号验证信息
            MailAccount mailAccount = new MailAccount();
            mailAccount.setAccount(mailConfig.getAccount());
            mailAccount.setPassword(mailConfig.getPassword());
            mailAccount.setPop3Host(mailConfig.getPop3Host());
            mailAccount.setPop3Port(mailConfig.getPop3Port());
            mailAccount.setSmtpHost(mailConfig.getSmtpHost());
            mailAccount.setSmtpPort(mailConfig.getSmtpPort());
            mailAccount.setSsl(mailConfig.getEmailSsl() == 1 ? true : false);
            mailAccount.setAccountName(mailConfig.getSenderName());
            SmtpUtil smtpUtil = new SmtpUtil(mailAccount);
            smtpUtil.sendMail(mailFilePath, mailModel);
            flag = 0;
            //插入数据库
            if (entity.getId() != null) {
                entity.setState(1);
                emailSendService.updateById(entity);
            } else {
                entity.setId(RandomUtil.uuId());
                entity.setCreatorUserId(userProvider.get().getUserId());
                if (mailConfig.getAccount() != null) {
                    entity.setSender(mailConfig.getAccount());
                }
                entity.setState(1);
                emailSendService.save(entity);
            }
        } catch (Exception e) {
            for (MailFile mailFile : attachmentList) {
                FileUtil.deleteFile(mailFilePath + mailFile.getFileId());
            }
            log.error(e.getMessage());
        }
        return flag;
    }

    @Override
    @Transactional
    public int receive(EmailConfigEntity mailConfig) {
        //账号验证信息
        MailAccount mailAccount = new MailAccount();
        mailAccount.setAccount(mailConfig.getAccount());
        mailAccount.setPassword(mailConfig.getPassword());
        mailAccount.setPop3Host(mailConfig.getPop3Host());
        mailAccount.setPop3Port(mailConfig.getPop3Port());
        mailAccount.setSmtpHost(mailConfig.getSmtpHost());
        mailAccount.setSmtpPort(mailConfig.getSmtpPort());
        if (StringNumber.ONE.equals(mailConfig.getEmailSsl().toString())) {
            mailAccount.setSsl(true);
        } else {
            mailAccount.setSsl(false);
        }
        Map<String, Object> map = pop3Util.popMail(mailAccount);
        int receiveCount = 0;
        if (map.get("receiveCount") != null) {
            receiveCount = (int) map.get("receiveCount");
        }
        List<EmailReceiveEntity> mailList = new ArrayList<>();
        if (map.get("mailList") != null) {
            mailList = (List<EmailReceiveEntity>) map.get("mailList");
        }
        if (mailList.size() > 0) {
            List<String> mids = mailList.stream().map(u -> u.getMID()).collect(Collectors.toList());
            //查询数据库状态
            QueryWrapper<EmailReceiveEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda().in(EmailReceiveEntity::getMID, mids);
            List<EmailReceiveEntity> emails = this.list(wrapper);
            this.remove(wrapper);
            //邮件赋值状态
            for (int i = 0; i < mailList.size(); i++) {
                EmailReceiveEntity entity = mailList.get(i);
                entity.setCreatorUserId(userProvider.get().getUserId());
                //通过数据库进行赋值，没有就默认0
                int stat = emails.stream().filter(m -> m.getMID().equals(entity.getMID())).findFirst().isPresent() ? emails.stream().filter(m -> m.getMID().equals(entity.getMID())).findFirst().get().getIsRead() : 0;
                long count = emails.stream().filter(m -> m.getMID().equals(entity.getMID())).count();
                entity.setIsRead(stat);
                if (count != 0) {
                    receiveCount--;
                }
                this.save(entity);
            }
        }
        return receiveCount;
    }
}
