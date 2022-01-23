package smart.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import smart.base.entity.EmailConfigEntity;
import smart.base.model.MailAccount;
import smart.model.email.*;
import smart.service.EmailReceiveService;
import smart.util.Pop3Util;
import smart.entity.EmailReceiveEntity;
import smart.entity.EmailSendEntity;
import smart.util.JsonUtil;
import smart.base.ActionResult;
import smart.base.vo.PaginationVO;
import smart.exception.DataException;
import smart.util.type.StringNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 邮件配置
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月26日 上午9:18
 */
@Api(tags = "邮件收发", value = "Email")
@RestController
@RequestMapping("/Email")
public class EmailController {

    @Autowired
    private EmailReceiveService emailReceiveService;
    @Autowired
    private Pop3Util pop3Util;

    /**
     * 获取邮件列表(收件箱、标星件、草稿箱、已发送)
     *
     * @param paginationEmail
     * @return
     */
    @ApiOperation("获取邮件列表(收件箱、标星件、草稿箱、已发送)")
    @GetMapping
    public ActionResult receiveList(PaginationEmail paginationEmail) {
        String type = paginationEmail.getType() != null ? paginationEmail.getType() : "inBox";
        switch (type) {
            case "inBox":
                List<EmailReceiveEntity> entity = emailReceiveService.getReceiveList(paginationEmail);
                PaginationVO paginationVO = JsonUtil.getJsonToBean(paginationEmail, PaginationVO.class);
                List<EmailReceiveListVO> listVO = JsonUtil.getJsonToList(entity, EmailReceiveListVO.class);
                return ActionResult.page(listVO,paginationVO);
            case "star":
                List<EmailReceiveEntity> entity1 = emailReceiveService.getStarredList(paginationEmail);
                PaginationVO paginationVo1 = JsonUtil.getJsonToBean(paginationEmail, PaginationVO.class);
                List<EmailStarredListVO> listVo1 = JsonUtil.getJsonToList(entity1, EmailStarredListVO.class);
                return ActionResult.page(listVo1,paginationVo1);
            case "draft":
                List<EmailSendEntity> entity2 = emailReceiveService.getDraftList(paginationEmail);
                PaginationVO paginationVo2 = JsonUtil.getJsonToBean(paginationEmail, PaginationVO.class);
                List<EmailDraftListVO> listVo2 = JsonUtil.getJsonToList(entity2, EmailDraftListVO.class);
                return ActionResult.page(listVo2,paginationVo2);
            case "sent":
                List<EmailSendEntity> entity3 = emailReceiveService.getSentList(paginationEmail);
                PaginationVO paginationVo3 = JsonUtil.getJsonToBean(paginationEmail, PaginationVO.class);
                List<EmailSentListVO> listVo3 = JsonUtil.getJsonToList(entity3, EmailSentListVO.class);
                return ActionResult.page(listVo3,paginationVo3);
            default:
                return ActionResult.fail("获取失败");
        }
    }

    /**
     * 获取邮箱配置
     *
     * @return
     */
    @ApiOperation("获取邮箱配置")
    @GetMapping("/Config")
    public ActionResult configInfo() {
        EmailConfigEntity entity = emailReceiveService.getConfigInfo();
        EmailCofigInfoVO vo = JsonUtil.getJsonToBean(entity, EmailCofigInfoVO.class);
        if(vo==null){
            vo=new EmailCofigInfoVO();
        }
        return ActionResult.success(vo);
    }

    /**
     * 获取邮件信息
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("获取邮件信息")
    @GetMapping("/{id}")
    public ActionResult info(@PathVariable("id") String id) throws DataException {
        Object entity = emailReceiveService.getInfo(id);
        EmailInfoVO vo = JsonUtil.getJsonToBeanEx(entity, EmailInfoVO.class);
        return ActionResult.success(vo);
    }

    /**
     * 删除
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("删除邮件")
    @DeleteMapping("/{id}")
    public ActionResult delete(@PathVariable("id") String id) {
       boolean flag= emailReceiveService.delete(id);
        if(flag==false){
            return ActionResult.fail("删除失败，邮件不存在");
        }
        return ActionResult.success("删除成功");
    }

    /**
     * 设置已读邮件
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("设置已读邮件")
    @PutMapping("/{id}/Actions/Read")
    public ActionResult receiveRead(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveRead(id, 1);
        if(flag==false){
            return ActionResult.fail("操作失败，邮件不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 设置未读邮件
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("设置未读邮件")
    @PutMapping("/{id}/Actions/Unread")
    public ActionResult receiveUnread(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveRead(id, 0);
        if(flag==false){
            return ActionResult.fail("操作失败，邮件不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 设置星标邮件
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("设置星标邮件")
    @PutMapping("/{id}/Actions/Star")
    public ActionResult receiveYesStarred(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveStarred(id, 1);
        if(flag==false){
            return ActionResult.fail("操作失败，邮件不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 设置取消星标
     *
     * @param id 主键值
     * @return
     */
    @ApiOperation("设置取消星标")
    @PutMapping("/{id}/Actions/Unstar")
    public ActionResult receiveNoStarred(@PathVariable("id") String id) {
        boolean flag= emailReceiveService.receiveStarred(id, 0);
        if(flag==false){
            return ActionResult.fail("操作失败，邮件不存在");
        }
        return ActionResult.success("操作成功");
    }

    /**
     * 收邮件
     *
     * @return
     */
    @ApiOperation("收邮件")
    @PostMapping("/Receive")
    public ActionResult receive() {
        EmailConfigEntity configEntity = emailReceiveService.getConfigInfo();
        if (configEntity != null) {
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
            if (pop3Util.checkConnected(mailAccount)) {
                int mailCount = emailReceiveService.receive(configEntity);
                return ActionResult.success("操作成功", mailCount);
            } else {
                return ActionResult.fail("账户认证错误");
            }
        } else {
            return ActionResult.fail("你还没有设置邮件的帐户");
        }
    }

    /**
     * 存草稿
     *
     * @return
     */
    @ApiOperation("存草稿")
    @PostMapping("/Actions/SaveDraft")
    public ActionResult saveDraft(@RequestBody @Valid EmailSendCrForm emailSendCrForm) {
        EmailSendEntity entity = JsonUtil.getJsonToBean(emailSendCrForm, EmailSendEntity.class);
        emailReceiveService.saveDraft(entity);
        return ActionResult.success("保存成功");
    }

    /**
     * 发邮件
     *
     * @return
     */
    @ApiOperation("发邮件")
    @PostMapping
    public ActionResult saveSent(@RequestBody @Valid EmailCrForm emailCrForm) {
        EmailSendEntity entity = JsonUtil.getJsonToBean(emailCrForm, EmailSendEntity.class);
        EmailConfigEntity configEntity = emailReceiveService.getConfigInfo();
        if (configEntity != null) {
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
            int flag = emailReceiveService.saveSent(entity, configEntity);
            if (flag == 0) {
                return ActionResult.success("发送成功");
            } else {
                return ActionResult.fail("账户认证错误");
            }
        } else {
            return ActionResult.fail("你还没有设置邮件的帐户");
        }
    }

    /**
     * 更新邮件配置
     *
     * @return
     */
    @ApiOperation("更新邮件配置")
    @PutMapping("/Config")
    public ActionResult saveConfig(@RequestBody @Valid EmailCheckForm emailCheckForm) throws DataException {
        EmailConfigEntity entity = JsonUtil.getJsonToBean(emailCheckForm, EmailConfigEntity.class);
        emailReceiveService.saveConfig(entity);
        return ActionResult.success("保存成功");
    }

    /**
     * 邮箱配置-测试连接
     *
     * @return
     */
    @ApiOperation("邮箱配置-测试连接")
    @PostMapping("/Config/Actions/CheckMail")
    public ActionResult checkLogin(@RequestBody @Valid EmailCheckForm emailCheckForm) {
        EmailConfigEntity entity = JsonUtil.getJsonToBean(emailCheckForm, EmailConfigEntity.class);
        boolean result = emailReceiveService.checkLogin(entity);
        if (result) {
            return ActionResult.success("验证成功");
        } else {
            return ActionResult.fail("账户认证错误");
        }
    }

    /**
     * 列表（收件箱）
     *
     * @param
     * @return
     */
    @GetMapping("/GetReceiveList")
    public List<EmailReceiveEntity> getReceiveList(){
        return emailReceiveService.getReceiveList();
    }

}
