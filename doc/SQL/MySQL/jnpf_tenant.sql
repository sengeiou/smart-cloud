
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for base_tenant
-- ----------------------------
DROP TABLE IF EXISTS `base_tenant`;
CREATE TABLE `base_tenant`  (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自然主键',
  `F_EnCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编号',
  `F_FullName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `F_CompanyName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公司',
  `F_ExpiresTime` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `F_DbName` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '服务名称',
  `F_IPAddress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `F_IPAddressName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP所在城市',
  `F_SourceWebsite` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '来源网站',
  `F_Description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  `F_SortCode` bigint(20) NULL DEFAULT NULL COMMENT '排序',
  `F_EnabledMark` int(11) NULL DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int(11) NULL DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '删除用户',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for base_tenantlog
-- ----------------------------
DROP TABLE IF EXISTS `base_tenantlog`;
CREATE TABLE `base_tenantlog`  (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '自然主键',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '租户主键',
  `F_LoginAccount` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录账户',
  `F_LoginIPAddress` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `F_LoginIPAddressName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP所在城市',
  `F_LoginSourceWebsite` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '来源网站',
  `F_LoginTime` datetime(0) NULL DEFAULT NULL COMMENT '登录时间',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '描述',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '租户日志' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
