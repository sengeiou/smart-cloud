package smart.service.impl;

import com.alibaba.fastjson.JSONObject;
import smart.model.BaseSystemInfo;
import smart.util.JsonUtil;
import smart.util.Md5Util;

import smart.base.SysConfigApi;
import smart.exception.WxErrorException;
import smart.model.mpmenu.MPMenuButtonModel;
import smart.model.mpmenu.MPMenuModel;
import smart.MPEventContentEntity;
import smart.model.mpmenu.MPMenuSubButtonModel;
import smart.service.MPEventContentService;
import smart.service.MPMenuService;
import smart.util.WxApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公众号菜单实现类
 *
 * @author 开发平台组
 * @version V3.1.0
 * @copyright 智慧停车公司
 * @date 2019年9月27日 上午9:18
 */
@Slf4j
@Service
public class MPMenuServiceImpl implements MPMenuService {

    @Autowired
    private SysConfigApi sysConfigApi;
    @Autowired
    private MPEventContentService mpEventContentService;

    @Override
    public List<MPMenuModel> getList() throws WxErrorException {
        List<MPMenuModel> menuList = new ArrayList<>();
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        //获取token
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        //菜单列表
        JSONObject result = WxApiClient.menuList(tokenObject.getString("access_token"));
        JSONObject menuJson = result.getJSONObject("menu");
        if (null != menuJson) {
            List<MPMenuButtonModel> menuButtonList = JsonUtil.getJsonToList(menuJson.getString("button"), MPMenuButtonModel.class);
            for (MPMenuButtonModel menuButton : menuButtonList) {
                MPMenuModel menu = new MPMenuModel();
                menu.setId(Md5Util.getStringMd5(menuButton.getName()));
                menu.setKey(menuButton.getKey());
                menu.setFullName(menuButton.getName());
                menu.setParentId("0");
                menu.setType(menuButton.getType());
                menu.setUrl(menuButton.getUrl());
                menu.setSortCode(Long.parseLong(menuButtonList.indexOf(menuButton) + ""));
                menuList.add(menu);
                if (menuButton.getSub_button().size() > 0) {
                    for (MPMenuSubButtonModel menuSubButton : menuButton.getSub_button()) {
                        MPMenuModel button = new MPMenuModel();
                        button.setId(Md5Util.getStringMd5(menuSubButton.getName()));
                        button.setKey(menuSubButton.getKey());
                        button.setFullName(menuSubButton.getName());
                        button.setParentId(menu.getId());
                        button.setType(menuSubButton.getType());
                        button.setUrl(menuSubButton.getUrl());
                        button.setSortCode(Long.parseLong(menuButton.getSub_button().indexOf(menuSubButton) + ""));
                        menuList.add(button);
                    }
                }
            }
        }
        return menuList;
    }

    @Override
    public void SyncMenu(List<MPMenuModel> menuList) throws WxErrorException {
        BaseSystemInfo config = sysConfigApi.getWeChatInfo();
        List<MPMenuButtonModel> menuButtonList = new ArrayList<>();
        if (menuList.size() > 0) {
            List<MPMenuModel> menuParentList = menuList.stream().filter(m -> "0".equals(m.getParentId())).collect(Collectors.toList());
            for (MPMenuModel item : menuParentList) {
                if (menuList.stream().filter(m -> m.getParentId().equals(item.getId())).count() > 0) {
                    item.setType("");
                } else {
                    if (menuList.stream().filter(m -> m.getParentId().equals(item.getId())).count() == 0 && "view".equals(item.getType())) {
                        item.setType("view");
                    } else {
                        item.setType("click");
                        item.setKey(Md5Util.getStringMd5(item.getFullName()));
                    }
                }
                MPMenuButtonModel menu = new MPMenuButtonModel();
                menu.setKey("view".equals(item.getType()) ? null : item.getKey());
                menu.setName(item.getFullName());
                menu.setType(item.getType());
                menu.setUrl(item.getUrl());
                if (StringUtils.isEmpty(item.getType())) {
                    List<MPMenuModel> menuSubList = menuList.stream().filter(m -> m.getParentId().equals(item.getId())).collect(Collectors.toList());
                    List<MPMenuSubButtonModel> sub_button = new ArrayList<>();
                    for (MPMenuModel subItem : menuSubList) {
                        if ("click".equals(subItem.getType())) {
                            subItem.setKey(Md5Util.getStringMd5(subItem.getFullName()));
                        }
                        MPMenuSubButtonModel button = new MPMenuSubButtonModel();
                        button.setKey("view".equals(subItem.getType()) ? null : subItem.getKey());
                        button.setName(subItem.getFullName());
                        button.setType(subItem.getType());
                        button.setUrl(subItem.getUrl());
                        sub_button.add(button);
                    }
                    menu.setSub_button(sub_button);
                }
                menuButtonList.add(menu);
            }
        }
        //旧数据防止创建失败回滚到旧数据
        JSONObject tokenObject = WxApiClient.getAccessToken(config.getWxGzhAppId(), config.getWxGzhAppSecret());
        String token = tokenObject.getString("access_token");
        JSONObject result = WxApiClient.menuList(token);
        JSONObject menu = result.getJSONObject("menu");
        //删除菜单
        WxApiClient.deleteMenu(token);
        //创建菜单
        if (menuButtonList.size() > 0) {
            JSONObject object = new JSONObject();
            object.put("button", menuButtonList);
            JSONObject returnObject = WxApiClient.publishMenus(object.toString(), token);
            if (returnObject.getInteger("errcode") != 0 && menu != null) {
                //回滚老数据
                WxApiClient.publishMenus(menu.toJSONString(), token);
            }
        }
    }

    @Override
    public boolean IsExistByFullName(String fullName, String id) throws WxErrorException {
        List<MPMenuModel> menuList = this.getList();
        menuList = menuList.stream().filter(m -> String.valueOf(m.getFullName()).equals(String.valueOf(fullName))).collect(Collectors.toList());
        if (!StringUtils.isEmpty(id)) {
            menuList = menuList.stream().filter(t -> !t.getId().equals(id)).collect(Collectors.toList());
        }
        return menuList.size() > 0 ? true : false;
    }

    @Override
    public void create(MPMenuModel model) {
        MPEventContentEntity entity = new MPEventContentEntity();
        entity.setEventKey(Md5Util.getStringMd5(model.getFullName()));
        entity.setContent(model.getContent());
        mpEventContentService.create(entity);
    }

    @Override
    public boolean update(String id, MPMenuModel model) {
        MPEventContentEntity entity = mpEventContentService.getInfo(id);
        if (entity != null) {
            entity.setEventKey(id);
            entity.setContent(model.getContent());
            return  mpEventContentService.update(entity);
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        MPEventContentEntity entity = mpEventContentService.getInfo(id);
        if (entity != null) {
           return  mpEventContentService.delete(entity);
        }
        return false;
    }

    @Override
    public void First(String id) throws WxErrorException {
        List<MPMenuModel> menuList = this.getList();
        MPMenuModel currentEntity = menuList.stream().filter(m -> m.getId().equals(id)).findFirst().get();
        Long currentSortCode = currentEntity.getSortCode() == null ? 0 : currentEntity.getSortCode();
        List<MPMenuModel> upEntitys = menuList.stream()
                .filter(t -> t.getParentId().equals(currentEntity.getParentId()))
                .filter(n -> n.getSortCode() < currentSortCode).collect(Collectors.toList());
        upEntitys.sort(Comparator.comparing(MPMenuModel::getSortCode).reversed());
        MPMenuModel upEntity = new MPMenuModel();
        if (upEntitys.size() != 0) {
            upEntity = upEntitys.get(0);
            currentEntity.setSortCode(upEntity.getSortCode());
            upEntity.setSortCode(currentSortCode);
            menuList.remove(currentEntity);
            menuList.remove(upEntity);
            menuList.add(currentEntity);
            menuList.add(upEntity);
            this.SyncMenu(menuList);
        }
    }

    @Override
    public void Next(String id) throws WxErrorException {
        List<MPMenuModel> menuList = this.getList();
        MPMenuModel currentEntity = menuList.stream().filter(m -> m.getId().equals(id)).findFirst().get();
        Long currentSortCode = currentEntity.getSortCode() == null ? 0 : currentEntity.getSortCode();
        List<MPMenuModel> upEntitys = menuList.stream()
                .filter(t -> t.getParentId().equals(currentEntity.getParentId()))
                .filter(n -> n.getSortCode() > currentSortCode).collect(Collectors.toList());
        upEntitys.sort(Comparator.comparing(MPMenuModel::getSortCode));
        if (upEntitys.size() != 0) {
            MPMenuModel nextEntity = upEntitys.get(0);
            currentEntity.setSortCode(nextEntity.getSortCode());
            nextEntity.setSortCode(currentSortCode);
            menuList.remove(currentEntity);
            menuList.remove(nextEntity);
            menuList.add(currentEntity);
            menuList.add(nextEntity);
            this.SyncMenu(menuList);
        }
    }
}
