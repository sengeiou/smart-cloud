-- 停车项目sql表
CREATE TABLE `p_customer_car`
(
    `F_Id`               varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_CUId`             varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属车主用户id',
    `F_PlateNumber`      varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '车牌号',
    `F_IsDefaultPlate`   tinyint(4)                                      DEFAULT '0' COMMENT '是否默认车牌 0：否 1：是',
    `F_PlateType`        tinyint(4)                                      DEFAULT '0' COMMENT '号牌类型 plateType 系统字典',
    `F_CarType`          tinyint(4)                                      DEFAULT '0' COMMENT '车辆类型 carType 系统字典',
    `F_VIN`              varchar(17) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '车辆识别代号VIN',
    `F_LimitedUse`       tinyint(4)                                      DEFAULT '0' COMMENT '使用限制 0：无限制 1：黑名单 2：其它',
    `F_EnabledMark`      tinyint(4)                                      DEFAULT '1' COMMENT '有效标志',
    `F_CreatorTime`      datetime                               NOT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                                        DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`F_Id`) USING BTREE,
    KEY `index_F_PlateNumber` (`F_PlateNumber`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='车辆信息表';

CREATE TABLE `p_customer_user`
(
    `F_Id`               varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
    `F_UserName`         varchar(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '名称',
    `F_Password`         varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '密码',
    `F_Mobile`           varchar(11) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '手机号',
    `F_NickName`         varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
    `F_Avatar`           varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
    `F_WalletBalance`    decimal(15, 2)                          DEFAULT NULL COMMENT '钱包余额',
    `F_OpenIdSmall`      varchar(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '小程序openid',
    `F_OpenIdPublic`     varchar(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '公众号openid',
    `F_UnionId`          varchar(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '公众号小程序关联ID',
    `F_Gender`           int(1)                                  DEFAULT '0' COMMENT '性别 1：男 2：女 0：未知',
    `F_IsFollow`         smallint(6)                             DEFAULT '0' COMMENT '是否关注 0：否 1：是',
    `F_Followtime`       datetime                                DEFAULT NULL COMMENT '关注时间',
    `F_UnfollowTime`     datetime                                DEFAULT NULL COMMENT '取关时间',
    `F_Country`          varchar(100) CHARACTER SET utf8         DEFAULT NULL COMMENT '所在国家',
    `F_Province`         varchar(100) CHARACTER SET utf8         DEFAULT NULL COMMENT '省份',
    `F_City`             varchar(100) CHARACTER SET utf8         DEFAULT '' COMMENT '城市',
    `F_UserType`         smallint(6)                             DEFAULT '0' COMMENT '用户类型 0：普通用户 1：其它',
    `F_RegistSource`     smallint(6)                             DEFAULT '0' COMMENT '注册来源 0：微信小程序 1：其它',
    `F_Version`          int(11)                                 DEFAULT NULL COMMENT '乐观索版本号',
    `F_EnabledMark`      tinyint(4)                              DEFAULT '1' COMMENT '有效标志',
    `F_CreatorTime`      datetime                               NOT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                                DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`F_Id`) USING BTREE,
    KEY `index_F_UserName` (`F_UserName`) USING BTREE,
    KEY `index_F_Mobile` (`F_Mobile`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  ROW_FORMAT = DYNAMIC COMMENT ='客户用户表';

CREATE TABLE `p_device`
(
    `F_Id`                    varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_Name`                  varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
    `F_Code`                  varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统编号',
    `F_SN`                    varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备SN',
    `F_Type`                  tinyint(4)                             NOT NULL DEFAULT '1' COMMENT '设备类型',
    `F_HeartbeatCycle`        tinyint(4)                                      DEFAULT '1' COMMENT '心跳周期',
    `F_NetworkType`           varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT '1' COMMENT '上网方式',
    `F_DeviceStatus`          varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT '1' COMMENT '设备状态',
    `F_OnlineStatus`          varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT '1' COMMENT '在线状态',
    `F_AlarmStatus`           varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT '1' COMMENT '告警状态',
    `F_DevicePropertyJson`    tinyint(4)                                      DEFAULT '1' COMMENT '设备属性JSON',
    `F_LastTelemetryDataJson` tinyint(4)                                      DEFAULT '1' COMMENT '设备最后一次遥测数据JSON',
    `F_LastHeartbeatDataJson` tinyint(4)                                      DEFAULT '1' COMMENT '设备最后一次心跳数据JSON',
    `F_EnabledMark`           tinyint(4)                                      DEFAULT '1' COMMENT '有效标志',
    `F_CreatorTime`           datetime                               NOT NULL COMMENT '创建时间',
    `F_CreatorUserId`         varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建用户',
    `F_LastModifyTime`        datetime                                        DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId`      varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`F_Id`) USING BTREE,
    KEY `F_SN` (`F_SN`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='设备表';

CREATE TABLE `p_parking`
(
    `F_Id`                      varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
    `F_PAId`                    varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT '100' COMMENT '所属片区ID',
    `F_Name`                    varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
    `F_ShowPictures`            varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '停车场实景图片',
    `F_PlanePicture`            varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '停车场平面图片',
    `F_Type`                    varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT '0' COMMENT '0:路侧停车场 1：商业园区 2：住宅社区 3：写字楼 4：交通枢纽',
    `F_Leveladdress`            varchar(30) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '省份',
    `F_Address`                 varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '停车点地址（详细地址）',
    `F_Lng`                     decimal(15, 7)                          DEFAULT '113.5166700' COMMENT '经度',
    `F_Lat`                     decimal(15, 7)                          DEFAULT '22.3000000' COMMENT '纬度',
    `F_ContactUserId`           varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '车场管理员',
    `F_SpaceTotal`              int(30)                                 DEFAULT '0' COMMENT '泊位总数量',
    `F_LongRentalSpaceTotal`    int(30)                                 DEFAULT '0' COMMENT '开放长租泊位数量',
    `F_ReservedSpaceTotal`      int(30)                                 DEFAULT '0' COMMENT '开放预约车位数',
    `F_ChargingPileTotal`       int(30)                                 DEFAULT '0' COMMENT '充电桩数量',
    `F_IsSelfSupport`           tinyint(4)                              DEFAULT '0' COMMENT '是否自营 0：否 1：是',
    `F_IsSupportAdvancePayment` tinyint(4)                              DEFAULT '0' COMMENT '是否支持预支付 0：否 1：是',
    `F_ParkingInfoQRCode`       varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '车场信息二维码',
    `F_Description`             text COLLATE utf8mb4_unicode_ci COMMENT '停车场介绍',
    `F_EnabledMark`             tinyint(4)                              DEFAULT '0' COMMENT '有效标志',
    `F_CreatorTime`             datetime                                DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`           varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '创建用户',
    `F_LastModifyTime`          datetime                                DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId`        varchar(32) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`F_Id`) USING BTREE,
    KEY `F_Id` (`F_Id`, `F_EnabledMark`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='停车场';

CREATE TABLE `p_parking_area`
(
    `F_Id`               varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
    `F_Name`             varchar(50) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '名称',
    `F_Address`          varchar(500) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '片区地址（详细地址）',
    `F_Floor`            varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '区层 默认0：地面停车场，楼层：n或-n层表示',
    `F_lon`              decimal(15, 7)                                  DEFAULT '113.5166700' COMMENT '经度',
    `F_Lat`              decimal(15, 7)                                  DEFAULT '22.3000000' COMMENT '纬度',
    `F_PunchinRange`     int(30)                                         DEFAULT '100' COMMENT '片区巡检员打卡签到范围',
    `F_ContactUserId`    varchar(100) COLLATE utf8mb4_unicode_ci         DEFAULT NULL COMMENT '片区巡检员',
    `F_Description`      text COLLATE utf8mb4_unicode_ci COMMENT '片区介绍',
    `F_EnabledMark`      tinyint(4)                                      DEFAULT '0' COMMENT '有效标志',
    `F_CreatorTime`      datetime                                        DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                                        DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(32) COLLATE utf8mb4_unicode_ci          DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`F_Id`) USING BTREE,
    KEY `F_Id` (`F_Id`, `F_EnabledMark`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='停车场片区';

CREATE TABLE `p_parking_space`
(
    `F_Id`               varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '编号',
    `F_PId`              varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '停车场地ID',
    `F_Device`           varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '设备序列号',
    `F_Name`             varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '泊位名称',
    `F_Type`             tinyint(11)                            DEFAULT '0' COMMENT '车位类型：0:普通车位 1：VIP车位 2:临时车位 3：私人车位',
    `F_IsCharging`       tinyint(11)                            DEFAULT '0' COMMENT '是否充电桩车位 0:否 1：是',
    `F_lon`              varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '经度',
    `F_Lat`              varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '纬度',
    `F_EnabledMark`      tinyint(11)                            DEFAULT '1' COMMENT '有效标志',
    `F_CreatorTime`      datetime                               DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                               DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
    PRIMARY KEY (`F_Id`) USING BTREE,
    KEY `F_Id` (`F_Id`, `F_PId`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='车位表';
