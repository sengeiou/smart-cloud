

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_tbl
-- ----------------------------
DROP TABLE IF EXISTS `account_tbl`;
CREATE TABLE `account_tbl`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `money` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of account_tbl
-- ----------------------------

-- ----------------------------
-- Table structure for branch_table
-- ----------------------------
DROP TABLE IF EXISTS `branch_table`;
CREATE TABLE `branch_table`  (
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `transaction_id` bigint(20) NULL DEFAULT NULL,
  `resource_group_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `resource_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `branch_type` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL,
  `client_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `application_data` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gmt_create` datetime(6) NULL DEFAULT NULL,
  `gmt_modified` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`branch_id`) USING BTREE,
  INDEX `idx_xid`(`xid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of branch_table
-- ----------------------------
INSERT INTO `branch_table` VALUES (91923320982736897, '172.28.224.1:8091:91923313995026432', 91923313995026432, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 0, 'seata-server:172.28.224.1:51538', NULL, '2021-01-11 15:50:22.490145', '2021-01-11 15:50:22.490145');
INSERT INTO `branch_table` VALUES (94452755749408769, '172.28.224.1:8091:94452732861091840', 94452732861091840, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 0, 'seata-server:172.28.224.1:52361', NULL, '2021-01-18 15:21:26.661334', '2021-01-18 15:21:26.661334');
INSERT INTO `branch_table` VALUES (94453680610217985, '172.28.224.1:8091:94453650704830464', 94453650704830464, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 0, 'seata-server:172.28.224.1:53104', NULL, '2021-01-18 15:25:07.328963', '2021-01-18 15:25:07.328963');
INSERT INTO `branch_table` VALUES (94804591803514881, '192.168.0.100:8091:94804583096139776', 94804583096139776, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 3, 'seata-server:192.168.0.100:65124', NULL, '2021-01-19 14:39:31.471222', '2021-01-19 14:39:31.715234');
INSERT INTO `branch_table` VALUES (94804591832875009, '192.168.0.100:8091:94804583096139776', 94804583096139776, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 3, 'seata-server:192.168.0.100:65124', NULL, '2021-01-19 14:39:31.579306', '2021-01-19 14:39:31.736130');
INSERT INTO `branch_table` VALUES (94805312724680705, '192.168.0.100:8091:94805304096997376', 94805304096997376, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 3, 'seata-server:192.168.0.100:51387', NULL, '2021-01-19 14:42:23.275055', '2021-01-19 14:42:23.455528');
INSERT INTO `branch_table` VALUES (94805312728875009, '192.168.0.100:8091:94805304096997376', 94805304096997376, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 3, 'seata-server:192.168.0.100:51387', NULL, '2021-01-19 14:42:23.358635', '2021-01-19 14:42:23.506040');
INSERT INTO `branch_table` VALUES (95616597074788353, '192.168.0.100:8091:95616587792793600', 95616587792793600, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 3, 'system:192.168.0.100:57075', NULL, '2021-01-21 20:26:07.816388', '2021-01-21 20:26:07.966236');
INSERT INTO `branch_table` VALUES (95616597074788355, '192.168.0.100:8091:95616587792793600', 95616587792793600, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 3, 'system:192.168.0.100:57075', NULL, '2021-01-21 20:26:07.761541', '2021-01-21 20:26:07.914667');
INSERT INTO `branch_table` VALUES (95930939196051457, '172.28.224.1:8091:95930927347142656', 95930927347142656, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 0, 'system:172.28.224.1:50943', NULL, '2021-01-22 17:15:12.459419', '2021-01-22 17:15:12.459419');
INSERT INTO `branch_table` VALUES (95930939204440065, '172.28.224.1:8091:95930927347142656', 95930927347142656, NULL, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'AT', 0, 'system:172.28.224.1:50943', NULL, '2021-01-22 17:15:12.460697', '2021-01-22 17:15:12.460697');

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `c_use` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `effect` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `c_schema` text CHARACTER SET utf8 COLLATE utf8_bin NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfo_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 324 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info
-- ----------------------------
INSERT INTO `config_info` VALUES (147, 'transport.type', 'SEATA_GROUP', 'TCP', 'b136ef5f6a01d816991fe3cf7a6ac763', '2021-01-07 06:33:10', '2021-01-07 06:33:10', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (148, 'transport.server', 'SEATA_GROUP', 'NIO', 'b6d9dfc0fb54277321cebc0fff55df2f', '2021-01-07 06:33:11', '2021-01-07 06:33:11', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (149, 'transport.heartbeat', 'SEATA_GROUP', 'true', 'b326b5062b2f0e69046810717534cb09', '2021-01-07 06:33:11', '2021-01-07 06:33:11', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (150, 'transport.enableClientBatchSendRequest', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:11', '2021-01-07 06:33:11', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (151, 'transport.threadFactory.bossThreadPrefix', 'SEATA_GROUP', 'NettyBoss', '0f8db59a3b7f2823f38a70c308361836', '2021-01-07 06:33:11', '2021-01-07 06:33:11', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (152, 'transport.threadFactory.workerThreadPrefix', 'SEATA_GROUP', 'NettyServerNIOWorker', 'a78ec7ef5d1631754c4e72ae8a3e9205', '2021-01-07 06:33:11', '2021-01-07 06:33:11', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (153, 'transport.threadFactory.serverExecutorThreadPrefix', 'SEATA_GROUP', 'NettyServerBizHandler', '11a36309f3d9df84fa8b59cf071fa2da', '2021-01-07 06:33:11', '2021-01-07 06:33:11', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (154, 'transport.threadFactory.shareBossWorker', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (155, 'transport.threadFactory.clientSelectorThreadPrefix', 'SEATA_GROUP', 'NettyClientSelector', 'cd7ec5a06541e75f5a7913752322c3af', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (156, 'transport.threadFactory.clientSelectorThreadSize', 'SEATA_GROUP', '1', 'c4ca4238a0b923820dcc509a6f75849b', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (157, 'transport.threadFactory.clientWorkerThreadPrefix', 'SEATA_GROUP', 'NettyClientWorkerThread', '61cf4e69a56354cf72f46dc86414a57e', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (158, 'transport.threadFactory.bossThreadSize', 'SEATA_GROUP', '1', 'c4ca4238a0b923820dcc509a6f75849b', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (159, 'transport.threadFactory.workerThreadSize', 'SEATA_GROUP', 'default', 'c21f969b5f03d33d43e04f8f136e7682', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (160, 'transport.shutdown.wait', 'SEATA_GROUP', '3', 'eccbc87e4b5ce2fe28308fd9f2a7baf3', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (161, 'service.vgroupMapping.my_test_tx_group', 'SEATA_GROUP', 'default', 'c21f969b5f03d33d43e04f8f136e7682', '2021-01-07 06:33:12', '2021-01-07 06:33:12', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (162, 'service.default.grouplist', 'SEATA_GROUP', '127.0.0.1:30095', 'c32ce0d3e264525dcdada751f98143a3', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (163, 'service.enableDegrade', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (164, 'service.disableGlobalTransaction', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (165, 'client.rm.asyncCommitBufferLimit', 'SEATA_GROUP', '10000', 'b7a782741f667201b54880c925faec4b', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (166, 'client.rm.lock.retryInterval', 'SEATA_GROUP', '10', 'd3d9446802a44259755d38e6d163e820', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (167, 'client.rm.lock.retryTimes', 'SEATA_GROUP', '30', '34173cb38f07f89ddbebc2ac9128303f', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (168, 'client.rm.lock.retryPolicyBranchRollbackOnConflict', 'SEATA_GROUP', 'true', 'b326b5062b2f0e69046810717534cb09', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (169, 'client.rm.reportRetryCount', 'SEATA_GROUP', '5', 'e4da3b7fbbce2345d7772b0674a318d5', '2021-01-07 06:33:13', '2021-01-07 06:33:13', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (170, 'client.rm.tableMetaCheckEnable', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:14', '2021-01-07 06:33:14', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (171, 'client.rm.sqlParserType', 'SEATA_GROUP', 'druid', '3d650fb8a5df01600281d48c47c9fa60', '2021-01-07 06:33:14', '2021-01-07 06:33:14', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (172, 'client.rm.reportSuccessEnable', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:14', '2021-01-07 06:33:14', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (173, 'client.rm.sagaBranchRegisterEnable', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:14', '2021-01-07 06:33:14', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (174, 'client.tm.commitRetryCount', 'SEATA_GROUP', '5', 'e4da3b7fbbce2345d7772b0674a318d5', '2021-01-07 06:33:14', '2021-01-07 06:33:14', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (175, 'client.tm.rollbackRetryCount', 'SEATA_GROUP', '5', 'e4da3b7fbbce2345d7772b0674a318d5', '2021-01-07 06:33:14', '2021-01-07 06:33:14', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (176, 'client.tm.defaultGlobalTransactionTimeout', 'SEATA_GROUP', '60000', '2b4226dd7ed6eb2d419b881f3ae9c97c', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (177, 'client.tm.degradeCheck', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (178, 'client.tm.degradeCheckAllowTimes', 'SEATA_GROUP', '10', 'd3d9446802a44259755d38e6d163e820', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (179, 'client.tm.degradeCheckPeriod', 'SEATA_GROUP', '2000', '08f90c1a417155361a5c4b8d297e0d78', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (180, 'store.mode', 'SEATA_GROUP', 'db', 'd77d5e503ad1439f585ac494268b351b', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (181, 'store.file.dir', 'SEATA_GROUP', 'file_store/data', '6a8dec07c44c33a8a9247cba5710bbb2', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (182, 'store.file.maxBranchSessionSize', 'SEATA_GROUP', '16384', 'c76fe1d8e08462434d800487585be217', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (183, 'store.file.maxGlobalSessionSize', 'SEATA_GROUP', '512', '10a7cdd970fe135cf4f7bb55c0e3b59f', '2021-01-07 06:33:15', '2021-01-07 06:33:15', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (184, 'store.file.fileWriteBufferCacheSize', 'SEATA_GROUP', '16384', 'c76fe1d8e08462434d800487585be217', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (185, 'store.file.flushDiskMode', 'SEATA_GROUP', 'async', '0df93e34273b367bb63bad28c94c78d5', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (186, 'store.file.sessionReloadReadSize', 'SEATA_GROUP', '100', 'f899139df5e1059396431415e770c6dd', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (187, 'store.db.datasource', 'SEATA_GROUP', 'druid', '3d650fb8a5df01600281d48c47c9fa60', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (188, 'store.db.dbType', 'SEATA_GROUP', 'mysql', '81c3b080dad537de7e10e0987a4bf52e', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (189, 'store.db.driverClassName', 'SEATA_GROUP', 'com.mysql.cj.jdbc.Driver', '33763409bb7f4838bde4fae9540433e4', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (190, 'store.db.url', 'SEATA_GROUP', 'jdbc:mysql://192.168.0.10:3306/java_nacos_test_31?serverTimezone=UTC', 'ba0f3f022d23074bf98ddfed5649ab58', '2021-01-07 06:33:16', '2021-01-07 06:33:16', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (191, 'store.db.user', 'SEATA_GROUP', 'java_nacos_test_31', '3d0c4f07f385223e305cb1695a263e53', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (192, 'store.db.password', 'SEATA_GROUP', 'yT3X5C2zn7SySip3', 'bfed192d5a596257206d29299c404a46', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (193, 'store.db.minConn', 'SEATA_GROUP', '5', 'e4da3b7fbbce2345d7772b0674a318d5', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (194, 'store.db.maxConn', 'SEATA_GROUP', '30', '34173cb38f07f89ddbebc2ac9128303f', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (195, 'store.db.globalTable', 'SEATA_GROUP', 'global_table', '8b28fb6bb4c4f984df2709381f8eba2b', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (196, 'store.db.branchTable', 'SEATA_GROUP', 'branch_table', '54bcdac38cf62e103fe115bcf46a660c', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (197, 'store.db.queryLimit', 'SEATA_GROUP', '100', 'f899139df5e1059396431415e770c6dd', '2021-01-07 06:33:17', '2021-01-07 06:33:17', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (198, 'store.db.lockTable', 'SEATA_GROUP', 'lock_table', '55e0cae3b6dc6696b768db90098b8f2f', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (199, 'store.db.maxWait', 'SEATA_GROUP', '5000', 'a35fe7f7fe8217b4369a0af4244d1fca', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (200, 'store.redis.host', 'SEATA_GROUP', '127.0.0.1', 'f528764d624db129b32c21fbca0cb8d6', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (201, 'store.redis.port', 'SEATA_GROUP', '6379', '92c3b916311a5517d9290576e3ea37ad', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (202, 'store.redis.maxConn', 'SEATA_GROUP', '10', 'd3d9446802a44259755d38e6d163e820', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (203, 'store.redis.minConn', 'SEATA_GROUP', '1', 'c4ca4238a0b923820dcc509a6f75849b', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (204, 'store.redis.database', 'SEATA_GROUP', '0', 'cfcd208495d565ef66e7dff9f98764da', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (205, 'store.redis.password', 'SEATA_GROUP', 'null', '37a6259cc0c1dae299a7866489dff0bd', '2021-01-07 06:33:18', '2021-01-07 06:33:18', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (206, 'store.redis.queryLimit', 'SEATA_GROUP', '100', 'f899139df5e1059396431415e770c6dd', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (207, 'server.recovery.committingRetryPeriod', 'SEATA_GROUP', '1000', 'a9b7ba70783b617e9998dc4dd82eb3c5', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (208, 'server.recovery.asynCommittingRetryPeriod', 'SEATA_GROUP', '1000', 'a9b7ba70783b617e9998dc4dd82eb3c5', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (209, 'server.recovery.rollbackingRetryPeriod', 'SEATA_GROUP', '1000', 'a9b7ba70783b617e9998dc4dd82eb3c5', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (210, 'server.recovery.timeoutRetryPeriod', 'SEATA_GROUP', '1000', 'a9b7ba70783b617e9998dc4dd82eb3c5', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (211, 'server.maxCommitRetryTimeout', 'SEATA_GROUP', '-1', '6bb61e3b7bce0931da574d19d1d82c88', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (212, 'server.maxRollbackRetryTimeout', 'SEATA_GROUP', '-1', '6bb61e3b7bce0931da574d19d1d82c88', '2021-01-07 06:33:19', '2021-01-07 06:33:19', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (213, 'server.rollbackRetryTimeoutUnlockEnable', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (214, 'client.undo.dataValidation', 'SEATA_GROUP', 'true', 'b326b5062b2f0e69046810717534cb09', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (215, 'client.undo.logSerialization', 'SEATA_GROUP', 'jackson', 'b41779690b83f182acc67d6388c7bac9', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (216, 'client.undo.onlyCareUpdateColumns', 'SEATA_GROUP', 'true', 'b326b5062b2f0e69046810717534cb09', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (217, 'server.undo.logSaveDays', 'SEATA_GROUP', '7', '8f14e45fceea167a5a36dedd4bea2543', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (218, 'server.undo.logDeletePeriod', 'SEATA_GROUP', '86400000', 'f4c122804fe9076cb2710f55c3c6e346', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (219, 'client.undo.logTable', 'SEATA_GROUP', 'undo_log', '2842d229c24afe9e61437135e8306614', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (220, 'log.exceptionRate', 'SEATA_GROUP', '100', 'f899139df5e1059396431415e770c6dd', '2021-01-07 06:33:20', '2021-01-07 06:33:20', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (221, 'transport.serialization', 'SEATA_GROUP', 'seata', 'b943081c423b9a5416a706524ee05d40', '2021-01-07 06:33:21', '2021-01-07 06:33:21', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (222, 'transport.compressor', 'SEATA_GROUP', 'none', '334c4a4c42fdb79d7ebc3e73b517e6f8', '2021-01-07 06:33:21', '2021-01-07 06:33:21', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (223, 'metrics.enabled', 'SEATA_GROUP', 'false', '68934a3e9455fa72420237eb05902327', '2021-01-07 06:33:21', '2021-01-07 06:33:21', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (224, 'metrics.registryType', 'SEATA_GROUP', 'compact', '7cf74ca49c304df8150205fc915cd465', '2021-01-07 06:33:21', '2021-01-07 06:33:21', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (225, 'metrics.exporterList', 'SEATA_GROUP', 'prometheus', 'e4f00638b8a10e6994e67af2f832d51c', '2021-01-07 06:33:22', '2021-01-07 06:33:22', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (226, 'metrics.exporterPrometheusPort', 'SEATA_GROUP', '9898', '7b9dc501afe4ee11c56a4831e20cee71', '2021-01-07 06:33:22', '2021-01-07 06:33:22', NULL, '0:0:0:0:0:0:0:1', '', '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `config_info` VALUES (234, 'sentinel-ruleflow-service', 'DEFAULT_GROUP', '[\n    {\n         \"resource\": \"oauth\",\n         \"limitApp\": \"default\",\n         \"grade\":   1,\n         \"count\":   1,\n         \"strategy\": 0,\n         \"controlBehavior\": 0,\n         \"clusterMode\": false\n    }\n]', 'edc1f08d0f664ab6df1dd305e9e17764', '2021-01-13 06:30:15', '2021-01-13 10:06:48', NULL, '0:0:0:0:0:0:0:1', '', '', 'sentinel限流', '', '', 'json', '');
INSERT INTO `config_info` VALUES (247, 'sentinel-degradeRule-service', 'DEFAULT_GROUP', '[\n    {\n        \"resource\":\"/System/SysConfig/getInfo\",\n        \"count\": 50,\n        \"grade\": 0,\n        \"passCount\": 0,\n        \"timeWindow\": 100,\n        \"slowRatioThreshold\":0.6,\n        \"minRequestAmount\":100,\n        \"statIntervalMs\":20000\n  }\n]', '459a8ae520d80b7cacf551c033a52164', '2021-01-18 06:17:45', '2021-01-18 07:58:33', NULL, '0:0:0:0:0:0:0:1', '', '', '', '', '', 'json', '');
INSERT INTO `config_info` VALUES (295, 'datasource.yaml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbname: java_cloud_test_31\n    url: jdbc:mysql://192.168.0.10:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: java_cloud_test_31\n    password: pa26EWdYr4Zkmbx4\n    driver-class-name: com.mysql.cj.jdbc.Driver', '27c90135e376fd5beb33d1947387d7ce', '2021-02-01 02:02:09', '2021-04-06 16:46:18', NULL, '0:0:0:0:0:0:0:1', '', '69c4eecb-05bd-4041-81fe-1473f95f578c', '数据源及Redis配置', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (300, 'resources.yaml', 'DEFAULT_GROUP', 'config:\n  # Windows配置（静态资源根目录和代码生成器临时目录）\n  Path: F:\\jnpf\\Code\\3.1\\develop\\jnpf-resources-v3.1\\\n  ServiceDirectoryPath: F:\\jnpf\\Code\\3.1\\develop\\jnpf-resources-v3.1\\CodeTemp\\\n\n  # Linux配置（静态资源根目录和代码生成器临时目录）\n  # Path: /www/wwwroot/jnpf-server/Resources/\n  # ServiceDirectoryPath: /www/wwwroot/jnpf-server/Resources/CodeTemp/\n  # 是否开启测试环境\n  TestVersion: false', '7c6fc82a94de8c5f0859ad082d7aeb06', '2021-02-01 02:57:10', '2021-04-06 16:51:16', NULL, '0:0:0:0:0:0:0:1', '', '69c4eecb-05bd-4041-81fe-1473f95f578c', '静态资源配置', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (312, 'router.yaml', 'DEFAULT_GROUP', 'spring:\n  cloud:\n    gateway:\n      routes:\n        # 认证中心\n        - id: jnpf-oauth\n          uri: lb://jnpf-oauth\n          predicates:\n            - Path=/api/oauth/**\n          filters:\n            - StripPrefix=2\n        # 系统基础服务\n        - id: jnpf-system\n          uri: lb://jnpf-system\n          predicates:\n            - Path=/api/system/**\n          filters:\n            - StripPrefix=2\n        # 示例\n        - id: jnpf-example\n          uri: lb://jnpf-example\n          predicates:\n            - Path=/api/example/**\n          filters:\n            - StripPrefix=2\n        # 扩展服务\n        - id: jnpf-extend\n          uri: lb://jnpf-extend\n          predicates:\n            - Path=/api/extend/**\n          filters:\n            - StripPrefix=2\n        # 报表服务\n        - id: jnpf-datareport\n          uri: lb://jnpf-datareport\n          predicates:\n            - Path=/api/datareport/**\n          filters:\n            - StripPrefix=2\n        # 可视化开发服务\n        - id: jnpf-visualdev\n          uri: lb://jnpf-visualdev\n          predicates:\n            - Path=/api/visualdev/**\n          filters:\n            - StripPrefix=2\n        # 工作流服务\n        - id: jnpf-workflow\n          uri: lb://jnpf-workflow\n          predicates:\n            - Path=/api/workflow/**\n          filters:\n            - StripPrefix=2\n        # 文件服务\n        - id: jnpf-file\n          uri: lb://jnpf-file\n          predicates:\n            - Path=/api/file/**\n          filters:\n            - StripPrefix=2\n        # 第三方\n        - id: jnpf-third\n          uri: lb://jnpf-third\n          predicates:\n            - Path=/api/third/**\n          filters:\n            - StripPrefix=2\n        # 多租户\n        - id: jnpf-tenant\n          uri: lb://jnpf-tenant\n          predicates:\n            - Path=/api/tenant/**\n          filters:\n            - StripPrefix=2', 'f0e83c86b9ebd7f1557e5b15f1f340e8', '2021-02-02 03:04:27', '2021-04-06 17:08:07', NULL, '0:0:0:0:0:0:0:1', '', '69c4eecb-05bd-4041-81fe-1473f95f578c', '网关路由配置', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (315, 'tenant.yaml', 'DEFAULT_GROUP', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbinit: jnpf_normal\n    dbname: jnpf_tenant\n    url: jdbc:mysql://192.168.0.80:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: root\n    password: root\n    driver-class-name: com.mysql.cj.jdbc.Driver\n# swagger开关\nswagger:\n  enable: true', 'fa900a28e733e8e0e4a2d462e9f2f273', '2021-04-01 01:50:48', '2021-04-06 16:45:44', NULL, '0:0:0:0:0:0:0:1', '', '69c4eecb-05bd-4041-81fe-1473f95f578c', '多租户配置', '', '', 'yaml', '');
INSERT INTO `config_info` VALUES (316, 'system-config.yaml', 'DEFAULT_GROUP', 'config:\n  # 静态资源目录映射\n  WebAnnexFilePath: WebAnnexFile\n  DataBackupFilePath: DataBackupFile\n  TemporaryFilePath: TemporaryFile\n  SystemFilePath: SystemFile\n  TemplateFilePath: TemplateFile\n  EmailFilePath: EmailFile\n  DocumentFilePath: DocumentFile\n  DocumentPreviewPath: DocumentPreview\n  UserAvatarFilePath: UserAvatar\n  IMContentFilePath: IMContentFile\n  MPMaterialFilePath: MPMaterial\n  TemplateCodePath: TemplateCode\n  BiVisualPath: BiVisualPath\n\n  MPUploadFileType: bmp,png,jpeg,jpg,gif,mp3,wma,wav,amr,mp4\n  WeChatUploadFileType: jpg,png,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv,amr,mp4\n  AllowUploadImageType: jpg,gif,png,bmp,jpeg,tiff,psd,swf,svg,pcx,dxf,wmf,emf,lic,eps,tga\n  AllowUploadFileType: jpg,gif,png,bmp,jpeg,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv\n  # 代码生成器命名空间\n  CodeAreasName: system,platForm,extend,test\n  WebDirectoryPath:\n  # ===============系统错误邮件报告反馈相关==================\n  SoftName: JNPF.JAVA\n  SoftFullName: JNPF软件开发平台\n  AppVersion: V3.1.0\n  AppUpdateContent: ;\n  # =====================================\n  RecordLog: true\n  ErrorReport: false\n  ErrorReportTo: surrpot@yinmaisoft.com\n  # 多租户是否开启\n  MultiTenancy: false\n  MultiTenancyUrl: http://127.0.0.1:30006/DbName/\n  IgexinEnabled: true\n  IgexinAppid: \n  IgexinAppkey: \n  IgexinMastersecret: \n  SoftVersion: V3.1.0\n  AccessKeyId: \n  AccessKeySecret: \n  # ===============跨域配置==================\n  Origins: http://127.0.0.1\n  Methods: GET,HEAD,POST,PUT,DELETE,OPTIONS', 'eb1752c8832bb18f54405c6bc9462dcf', '2021-04-01 01:51:48', '2021-04-06 16:48:21', NULL, '0:0:0:0:0:0:0:1', '', '69c4eecb-05bd-4041-81fe-1473f95f578c', '系统配置', '', '', 'yaml', '');

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'datum_id',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfoaggr_datagrouptenantdatum`(`data_id`, `group_id`, `tenant_id`, `datum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '增加租户字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_aggr
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_beta
-- ----------------------------
DROP TABLE IF EXISTS `config_info_beta`;
CREATE TABLE `config_info_beta`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfobeta_datagrouptenant`(`data_id`, `group_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_beta' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_beta
-- ----------------------------

-- ----------------------------
-- Table structure for config_info_tag
-- ----------------------------
DROP TABLE IF EXISTS `config_info_tag`;
CREATE TABLE `config_info_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'content',
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT 'source user',
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_configinfotag_datagrouptenanttag`(`data_id`, `group_id`, `tenant_id`, `tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_info_tag' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_info_tag
-- ----------------------------

-- ----------------------------
-- Table structure for config_tags_relation
-- ----------------------------
DROP TABLE IF EXISTS `config_tags_relation`;
CREATE TABLE `config_tags_relation`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`) USING BTREE,
  UNIQUE INDEX `uk_configtagrelation_configidtag`(`id`, `tag_name`, `tag_type`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'config_tag_relation' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of config_tags_relation
-- ----------------------------

-- ----------------------------
-- Table structure for global_table
-- ----------------------------
DROP TABLE IF EXISTS `global_table`;
CREATE TABLE `global_table`  (
  `xid` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `transaction_id` bigint(20) NULL DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `application_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `transaction_service_group` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `transaction_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `timeout` int(11) NULL DEFAULT NULL,
  `begin_time` bigint(20) NULL DEFAULT NULL,
  `application_data` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT NULL,
  `gmt_modified` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`xid`) USING BTREE,
  INDEX `idx_gmt_modified_status`(`gmt_modified`, `status`) USING BTREE,
  INDEX `idx_transaction_id`(`transaction_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of global_table
-- ----------------------------

-- ----------------------------
-- Table structure for group_capacity
-- ----------------------------
DROP TABLE IF EXISTS `group_capacity`;
CREATE TABLE `group_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_id`(`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '集群、各Group容量信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of group_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for his_config_info
-- ----------------------------
DROP TABLE IF EXISTS `his_config_info`;
CREATE TABLE `his_config_info`  (
  `id` bigint(64) UNSIGNED NOT NULL,
  `nid` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `group_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `app_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'app_name',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text CHARACTER SET utf8 COLLATE utf8_bin NULL,
  `src_ip` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `op_type` char(10) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`) USING BTREE,
  INDEX `idx_gmt_create`(`gmt_create`) USING BTREE,
  INDEX `idx_gmt_modified`(`gmt_modified`) USING BTREE,
  INDEX `idx_did`(`data_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1071 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '多租户改造' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of his_config_info
-- ----------------------------
INSERT INTO `his_config_info` VALUES (295, 1054, 'datasorce.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbname: jnpf_java_cloud_test\n    url: jdbc:mysql://192.168.0.31:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: jnpf_java_cloud_test\n    password: ALcAzendMc4X6psD\n    driver-class-name: com.mysql.cj.jdbc.Driver', 'd182a5f5e63b594617443ca920d31e64', '2021-03-23 17:47:40', '2021-03-23 09:47:40', NULL, '192.168.0.100', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (295, 1055, 'datasorce.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbname: java_nacos_test_31\n    url: jdbc:mysql://192.168.0.10:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: java_nacos_test_31\n    password: yT3X5C2zn7SySip3\n    driver-class-name: com.mysql.cj.jdbc.Driver', '48b9b98065e8fda032b974406ffd3974', '2021-03-24 19:05:18', '2021-03-24 11:05:18', NULL, '192.168.0.100', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (295, 1056, 'datasorce.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbname: java_cloud_init_31\n    url: jdbc:mysql://192.168.0.10:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: java_cloud_init_31\n    password: YA74MMdk7Tm8p2xe\n    driver-class-name: com.mysql.cj.jdbc.Driver', '2171fcf615af33306c003a56dc8f21dc', '2021-03-24 19:16:08', '2021-03-24 11:16:09', NULL, '192.168.0.100', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (295, 1057, 'datasorce.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbname: java_cloud_init_31\n    url: jdbc:mysql://192.168.0.10:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: java_cloud_init_31\n    password: YA74MMdk7Tm8p2xe\n    driver-class-name: com.mysql.cj.jdbc.Driver', '2171fcf615af33306c003a56dc8f21dc', '2021-03-24 19:19:29', '2021-03-24 11:19:29', NULL, '192.168.0.100', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (0, 1058, 'oracle-database.yaml', 'DEFAULT_GROUP', '', 'spring:  \n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源\n  # 表空间(Oracle)\n  tableSpace: JNPFCLOUD\n  dataType: oracle\n  datasource:\n    dbname: JNPFCLOUD\n    url: jdbc:oracle:thin:@192.168.0.19:1521/{dbName}\n    username: JNPFCLOUD\n    password: JNPFCLOUD\n    driver-class-name: oracle.jdbc.OracleDriver', '05f6000bca06aba592ee854e3211a3f0', '2021-03-26 22:15:42', '2021-03-26 14:15:43', NULL, '192.168.0.100', 'I', '1e017954-eb52-4d21-a843-0286d9013cf3');
INSERT INTO `his_config_info` VALUES (316, 1059, 'oracle-database.yaml', 'DEFAULT_GROUP', '', 'spring:  \n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源\n  # 表空间(Oracle)\n  tableSpace: JNPFCLOUD\n  dataType: oracle\n  datasource:\n    dbname: JNPFCLOUD\n    url: jdbc:oracle:thin:@192.168.0.19:1521/{dbName}\n    username: JNPFCLOUD\n    password: JNPFCLOUD\n    driver-class-name: oracle.jdbc.OracleDriver', '05f6000bca06aba592ee854e3211a3f0', '2021-03-26 22:17:25', '2021-03-26 14:17:26', NULL, '192.168.0.100', 'D', '1e017954-eb52-4d21-a843-0286d9013cf3');
INSERT INTO `his_config_info` VALUES (0, 1060, 'oracle-database.yaml', 'DEFAULT_GROUP', '', 'spring:  \n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源配置\n  # 表空间(Oracle)\n  tableSpace: JNPFCLOUD\n  # 数据库类型(mysql,sqlserver,oracle)\n  dataType: oracle\n  datasource:\n    dbname: JNPFCLOUD\n    url: jdbc:oracle:thin:@192.168.0.19:1521/{dbName}\n    username: JNPFCLOUD\n    password: JNPFCLOUD\n    driver-class-name: oracle.jdbc.OracleDriver', '10b82628fca4857d67a3c3809312c2c2', '2021-04-01 09:49:05', '2021-04-01 01:49:06', NULL, '192.168.0.100', 'I', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (313, 1061, 'oracle-database.yaml', 'DEFAULT_GROUP', '', 'spring:  \n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源配置\n  # 表空间(Oracle)\n  tableSpace: JNPFCLOUD\n  # 数据库类型(mysql,sqlserver,oracle)\n  dataType: oracle\n  datasource:\n    dbname: JNPFCLOUD\n    url: jdbc:oracle:thin:@192.168.0.19:1521/{dbName}\n    username: JNPFCLOUD\n    password: JNPFCLOUD\n    driver-class-name: oracle.jdbc.OracleDriver', '10b82628fca4857d67a3c3809312c2c2', '2021-04-01 09:49:30', '2021-04-01 01:49:31', NULL, '192.168.0.100', 'D', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (0, 1062, 'tenant.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbinit: jnpf_normal\n    dbname: jnpf_tenant\n    url: jdbc:mysql://192.168.0.80:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: root\n    password: root\n    driver-class-name: com.mysql.cj.jdbc.Driver\n# swagger开关\nswagger:\n  enable: true', 'fa900a28e733e8e0e4a2d462e9f2f273', '2021-04-01 09:50:48', '2021-04-01 01:50:48', NULL, '192.168.0.100', 'I', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (0, 1063, 'system-config.yaml', 'DEFAULT_GROUP', '', 'config:\n  # 静态资源目录映射\n  WebAnnexFilePath: WebAnnexFile\n  DataBackupFilePath: DataBackupFile\n  TemporaryFilePath: TemporaryFile\n  SystemFilePath: SystemFile\n  TemplateFilePath: TemplateFile\n  EmailFilePath: EmailFile\n  DocumentFilePath: DocumentFile\n  DocumentPreviewPath: DocumentPreview\n  UserAvatarFilePath: UserAvatar\n  IMContentFilePath: IMContentFile\n  MPMaterialFilePath: MPMaterial\n  TemplateCodePath: TemplateCode\n  BiVisualPath: BiVisualPath\n\n  MPUploadFileType: bmp,png,jpeg,jpg,gif,mp3,wma,wav,amr,mp4\n  WeChatUploadFileType: jpg,png,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv,amr,mp4\n  AllowUploadImageType: jpg,gif,png,bmp,jpeg,tiff,psd,swf,svg,pcx,dxf,wmf,emf,lic,eps,tga\n  AllowUploadFileType: jpg,gif,png,bmp,jpeg,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv\n  # 代码生成器命名空间\n  CodeAreasName: system,platForm,extend,test\n  WebDirectoryPath:\n  # ===============系统错误邮件报告反馈相关==================\n  SoftName: JNPF.JAVA\n  SoftFullName: JNPF软件开发平台\n  AppVersion: V3.1.0\n  AppUpdateContent: ;\n  # =====================================\n  RecordLog: true\n  ErrorReport: false\n  ErrorReportTo: surrpot@yinmaisoft.com\n  # 多租户是否开启\n  MultiTenancy: false\n  MultiTenancyUrl: http://127.0.0.1:30006/DbName/\n  IgexinEnabled: true\n  IgexinAppid: HLFY9T2d1z7MySY8hwGwh4\n  IgexinAppkey: 6Uiduugq648YDChhCjAt59\n  IgexinMastersecret: pEyQm156SJ9iS7PbyjLCZ6\n  SoftVersion: V3.0.0\n  AccessKeyId: LTAI4Fu4iJnKmDu5QG78ibfa\n  AccessKeySecret: pMSYlgOz3Tm6sYqWA8sKiyxgNY97J7\n  # ===============跨域配置==================\n  Origins: http://127.0.0.1\n  Methods: GET,HEAD,POST,PUT,DELETE,OPTIONS', 'ff3f16e85429ad5d7698c25c2fc4bc23', '2021-04-01 09:51:48', '2021-04-01 01:51:48', NULL, '192.168.0.100', 'I', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (316, 1064, 'system-config.yaml', 'DEFAULT_GROUP', '', 'config:\n  # 静态资源目录映射\n  WebAnnexFilePath: WebAnnexFile\n  DataBackupFilePath: DataBackupFile\n  TemporaryFilePath: TemporaryFile\n  SystemFilePath: SystemFile\n  TemplateFilePath: TemplateFile\n  EmailFilePath: EmailFile\n  DocumentFilePath: DocumentFile\n  DocumentPreviewPath: DocumentPreview\n  UserAvatarFilePath: UserAvatar\n  IMContentFilePath: IMContentFile\n  MPMaterialFilePath: MPMaterial\n  TemplateCodePath: TemplateCode\n  BiVisualPath: BiVisualPath\n\n  MPUploadFileType: bmp,png,jpeg,jpg,gif,mp3,wma,wav,amr,mp4\n  WeChatUploadFileType: jpg,png,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv,amr,mp4\n  AllowUploadImageType: jpg,gif,png,bmp,jpeg,tiff,psd,swf,svg,pcx,dxf,wmf,emf,lic,eps,tga\n  AllowUploadFileType: jpg,gif,png,bmp,jpeg,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv\n  # 代码生成器命名空间\n  CodeAreasName: system,platForm,extend,test\n  WebDirectoryPath:\n  # ===============系统错误邮件报告反馈相关==================\n  SoftName: JNPF.JAVA\n  SoftFullName: JNPF软件开发平台\n  AppVersion: V3.1.0\n  AppUpdateContent: ;\n  # =====================================\n  RecordLog: true\n  ErrorReport: false\n  ErrorReportTo: surrpot@yinmaisoft.com\n  # 多租户是否开启\n  MultiTenancy: false\n  MultiTenancyUrl: http://127.0.0.1:30006/DbName/\n  IgexinEnabled: true\n  IgexinAppid: HLFY9T2d1z7MySY8hwGwh4\n  IgexinAppkey: 6Uiduugq648YDChhCjAt59\n  IgexinMastersecret: pEyQm156SJ9iS7PbyjLCZ6\n  SoftVersion: V3.0.0\n  AccessKeyId: LTAI4Fu4iJnKmDu5QG78ibfa\n  AccessKeySecret: pMSYlgOz3Tm6sYqWA8sKiyxgNY97J7\n  # ===============跨域配置==================\n  Origins: http://127.0.0.1\n  Methods: GET,HEAD,POST,PUT,DELETE,OPTIONS', 'ff3f16e85429ad5d7698c25c2fc4bc23', '2021-04-07 00:45:30', '2021-04-06 16:45:30', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (315, 1065, 'tenant.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbinit: jnpf_normal\n    dbname: jnpf_tenant\n    url: jdbc:mysql://192.168.0.80:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: root\n    password: root\n    driver-class-name: com.mysql.cj.jdbc.Driver\n# swagger开关\nswagger:\n  enable: true', 'fa900a28e733e8e0e4a2d462e9f2f273', '2021-04-07 00:45:43', '2021-04-06 16:45:44', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (312, 1066, 'router.yaml', 'DEFAULT_GROUP', '', 'spring:\n  cloud:\n    gateway:\n      routes:\n        # 认证中心\n        - id: jnpf-oauth\n          uri: lb://jnpf-oauth\n          predicates:\n            - Path=/api/oauth/**\n          filters:\n            - StripPrefix=2\n        # 系统\n        - id: jnpf-system\n          uri: lb://jnpf-system\n          predicates:\n            - Path=/api/system/**\n          filters:\n            - StripPrefix=2\n        # 示例\n        - id: jnpf-example\n          uri: lb://jnpf-example\n          predicates:\n            - Path=/api/example/**\n          filters:\n            - StripPrefix=2\n        # 扩展\n        - id: jnpf-extend\n          uri: lb://jnpf-extend\n          predicates:\n            - Path=/api/extend/**\n          filters:\n            - StripPrefix=2\n        # 报表\n        - id: jnpf-report\n          uri: lb://jnpf-report\n          predicates:\n            - Path=/api/report/**\n          filters:\n            - StripPrefix=2\n        # 可视化\n        - id: jnpf-visualdev\n          uri: lb://jnpf-visualdev\n          predicates:\n            - Path=/api/visualdev/**\n          filters:\n            - StripPrefix=2\n        # 工作流\n        - id: jnpf-workflow\n          uri: lb://jnpf-workflow\n          predicates:\n            - Path=/api/workflow/**\n          filters:\n            - StripPrefix=2\n        # 公共接口\n        - id: jnpf-file\n          uri: lb://jnpf-file\n          predicates:\n            - Path=/api/file/**\n          filters:\n            - StripPrefix=2\n        # 公共接口\n        - id: jnpf-third\n          uri: lb://jnpf-third\n          predicates:\n            - Path=/api/third/**\n          filters:\n            - StripPrefix=2', 'ea7eeb8e5f9d629e3f144f192249a2e2', '2021-04-07 00:46:04', '2021-04-06 16:46:04', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (295, 1067, 'datasource.yaml', 'DEFAULT_GROUP', '', 'spring:\n  redis:\n    database: 1\n    host: 127.0.0.1\n    port: 6379\n    password:\n  # 数据源  \n  dataType: mysql\n  datasource:\n    dbname: java_cloud_test_31\n    url: jdbc:mysql://192.168.0.10:3306/{dbName}?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=UTC&nullCatalogMeansCurrent=true\n    username: java_cloud_test_31\n    password: pa26EWdYr4Zkmbx4\n    driver-class-name: com.mysql.cj.jdbc.Driver', '27c90135e376fd5beb33d1947387d7ce', '2021-04-07 00:46:17', '2021-04-06 16:46:18', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (316, 1068, 'system-config.yaml', 'DEFAULT_GROUP', '', 'config:\n  # 静态资源目录映射\n  WebAnnexFilePath: WebAnnexFile\n  DataBackupFilePath: DataBackupFile\n  TemporaryFilePath: TemporaryFile\n  SystemFilePath: SystemFile\n  TemplateFilePath: TemplateFile\n  EmailFilePath: EmailFile\n  DocumentFilePath: DocumentFile\n  DocumentPreviewPath: DocumentPreview\n  UserAvatarFilePath: UserAvatar\n  IMContentFilePath: IMContentFile\n  MPMaterialFilePath: MPMaterial\n  TemplateCodePath: TemplateCode\n  BiVisualPath: BiVisualPath\n\n  MPUploadFileType: bmp,png,jpeg,jpg,gif,mp3,wma,wav,amr,mp4\n  WeChatUploadFileType: jpg,png,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv,amr,mp4\n  AllowUploadImageType: jpg,gif,png,bmp,jpeg,tiff,psd,swf,svg,pcx,dxf,wmf,emf,lic,eps,tga\n  AllowUploadFileType: jpg,gif,png,bmp,jpeg,doc,docx,ppt,pptx,xls,xlsx,pdf,txt,rar,zip,csv\n  # 代码生成器命名空间\n  CodeAreasName: system,platForm,extend,test\n  WebDirectoryPath:\n  # ===============系统错误邮件报告反馈相关==================\n  SoftName: JNPF.JAVA\n  SoftFullName: JNPF软件开发平台\n  AppVersion: V3.1.0\n  AppUpdateContent: ;\n  # =====================================\n  RecordLog: true\n  ErrorReport: false\n  ErrorReportTo: surrpot@yinmaisoft.com\n  # 多租户是否开启\n  MultiTenancy: false\n  MultiTenancyUrl: http://127.0.0.1:30006/DbName/\n  IgexinEnabled: true\n  IgexinAppid: HLFY9T2d1z7MySY8hwGwh4\n  IgexinAppkey: 6Uiduugq648YDChhCjAt59\n  IgexinMastersecret: pEyQm156SJ9iS7PbyjLCZ6\n  SoftVersion: V3.0.0\n  AccessKeyId: LTAI4Fu4iJnKmDu5QG78ibfa\n  AccessKeySecret: pMSYlgOz3Tm6sYqWA8sKiyxgNY97J7\n  # ===============跨域配置==================\n  Origins: http://127.0.0.1\n  Methods: GET,HEAD,POST,PUT,DELETE,OPTIONS', 'ff3f16e85429ad5d7698c25c2fc4bc23', '2021-04-07 00:48:20', '2021-04-06 16:48:21', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (300, 1069, 'resources.yaml', 'DEFAULT_GROUP', '', 'config:\n  # Windows配置（静态资源根目录和代码生成器临时目录）\n  # Path: E:\\Resources\\\n  # ServiceDirectoryPath: D:\\工作\\\n\n  # Linux配置（静态资源根目录和代码生成器临时目录）\n  Path: /www/wwwroot/jnpf-server/Resources/\n  ServiceDirectoryPath: /www/wwwroot/jnpf-server/Resources/CodeTemp/\n  # 是否开启测试环境\n  TestVersion: false', '172e0ff8ab8eb28675c38cce25b0299c', '2021-04-07 00:51:16', '2021-04-06 16:51:16', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');
INSERT INTO `his_config_info` VALUES (312, 1070, 'router.yaml', 'DEFAULT_GROUP', '', 'spring:\n  cloud:\n    gateway:\n      routes:\n        # 认证中心\n        - id: jnpf-oauth\n          uri: lb://jnpf-oauth\n          predicates:\n            - Path=/api/oauth/**\n          filters:\n            - StripPrefix=2\n        # 系统\n        - id: jnpf-system\n          uri: lb://jnpf-system\n          predicates:\n            - Path=/api/system/**\n          filters:\n            - StripPrefix=2\n        # 示例\n        - id: jnpf-example\n          uri: lb://jnpf-example\n          predicates:\n            - Path=/api/example/**\n          filters:\n            - StripPrefix=2\n        # 扩展\n        - id: jnpf-extend\n          uri: lb://jnpf-extend\n          predicates:\n            - Path=/api/extend/**\n          filters:\n            - StripPrefix=2\n        # 报表\n        - id: jnpf-report\n          uri: lb://jnpf-report\n          predicates:\n            - Path=/api/report/**\n          filters:\n            - StripPrefix=2\n        # 可视化\n        - id: jnpf-visualdev\n          uri: lb://jnpf-visualdev\n          predicates:\n            - Path=/api/visualdev/**\n          filters:\n            - StripPrefix=2\n        # 工作流\n        - id: jnpf-workflow\n          uri: lb://jnpf-workflow\n          predicates:\n            - Path=/api/workflow/**\n          filters:\n            - StripPrefix=2\n        # 公共接口\n        - id: jnpf-file\n          uri: lb://jnpf-file\n          predicates:\n            - Path=/api/file/**\n          filters:\n            - StripPrefix=2\n        # 公共接口\n        - id: jnpf-third\n          uri: lb://jnpf-third\n          predicates:\n            - Path=/api/third/**\n          filters:\n            - StripPrefix=2', 'ea7eeb8e5f9d629e3f144f192249a2e2', '2021-04-07 01:08:06', '2021-04-06 17:08:07', NULL, '0:0:0:0:0:0:0:1', 'U', '69c4eecb-05bd-4041-81fe-1473f95f578c');

-- ----------------------------
-- Table structure for lock_table
-- ----------------------------
DROP TABLE IF EXISTS `lock_table`;
CREATE TABLE `lock_table`  (
  `row_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `xid` varchar(96) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `transaction_id` bigint(20) NULL DEFAULT NULL,
  `branch_id` bigint(20) NOT NULL,
  `resource_id` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `table_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pk` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `gmt_create` datetime NULL DEFAULT NULL,
  `gmt_modified` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`row_key`) USING BTREE,
  INDEX `idx_branch_id`(`branch_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lock_table
-- ----------------------------
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^151ae5ee1333465e9636d3fe56ae9a61', '192.168.0.100:8091:94804583096139776', 94804583096139776, 94804591832875009, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '151ae5ee1333465e9636d3fe56ae9a61', '2021-01-19 14:39:31', '2021-01-19 14:39:31');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^15703bc599df4b1bbf6f59c2f374dcbe', '172.28.224.1:8091:95930927347142656', 95930927347142656, 95930939204440065, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '15703bc599df4b1bbf6f59c2f374dcbe', '2021-01-22 17:15:12', '2021-01-22 17:15:12');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^25e8a9adeaba40f48da182c73782394c', '172.28.224.1:8091:95930927347142656', 95930927347142656, 95930939196051457, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '25e8a9adeaba40f48da182c73782394c', '2021-01-22 17:15:12', '2021-01-22 17:15:12');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^41cf9774426b4b4eaa21f9e9417b7c3a', '192.168.0.100:8091:94804583096139776', 94804583096139776, 94804591803514881, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '41cf9774426b4b4eaa21f9e9417b7c3a', '2021-01-19 14:39:31', '2021-01-19 14:39:31');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^5afeadbe479943e397d2817c513b0156', '192.168.0.100:8091:94805304096997376', 94805304096997376, 94805312724680705, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '5afeadbe479943e397d2817c513b0156', '2021-01-19 14:42:23', '2021-01-19 14:42:23');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^651f1c22ea334da9a115404611b92995', '172.28.224.1:8091:94452732861091840', 94452732861091840, 94452755749408769, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '651f1c22ea334da9a115404611b92995', '2021-01-18 15:21:26', '2021-01-18 15:21:26');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^7d05e561baa74eabbc76e0f183aedc31', '192.168.0.100:8091:95616587792793600', 95616587792793600, 95616597074788355, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '7d05e561baa74eabbc76e0f183aedc31', '2021-01-21 20:26:07', '2021-01-21 20:26:07');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^8c7406fe114a4d0cbd8f8589b994bdea', '192.168.0.100:8091:95616587792793600', 95616587792793600, 95616597074788353, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', '8c7406fe114a4d0cbd8f8589b994bdea', '2021-01-21 20:26:07', '2021-01-21 20:26:07');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^base_syslog^^^b4b6c6e80a0b45efa71b833876d77f4b', '192.168.0.100:8091:94805304096997376', 94805304096997376, 94805312728875009, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'base_syslog', 'b4b6c6e80a0b45efa71b833876d77f4b', '2021-01-19 14:42:23', '2021-01-19 14:42:23');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^undo_log^^^117', '172.28.224.1:8091:91923313995026432', 91923313995026432, 91923320982736897, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'undo_log', '117', '2021-01-11 15:50:22', '2021-01-11 15:50:22');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^undo_log^^^4436', '172.28.224.1:8091:94452732861091840', 94452732861091840, 94452755749408769, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'undo_log', '4436', '2021-01-18 15:21:26', '2021-01-18 15:21:26');
INSERT INTO `lock_table` VALUES ('jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev^^^undo_log^^^4470', '172.28.224.1:8091:94453650704830464', 94453650704830464, 94453680610217985, 'jdbc:mysql://192.168.0.31:3306/jnpf_cloud_dev', 'undo_log', '4470', '2021-01-18 15:25:06', '2021-01-18 15:25:06');

-- ----------------------------
-- Table structure for order_tbl
-- ----------------------------
DROP TABLE IF EXISTS `order_tbl`;
CREATE TABLE `order_tbl`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `commodity_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `count` int(11) NULL DEFAULT 0,
  `money` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_tbl
-- ----------------------------

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `role` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `resource` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `action` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  UNIQUE INDEX `uk_role_permission`(`role`, `resource`, `action`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of permissions
-- ----------------------------

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  UNIQUE INDEX `idx_user_role`(`username`, `role`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('nacos', 'ROLE_ADMIN');

-- ----------------------------
-- Table structure for storage_tbl
-- ----------------------------
DROP TABLE IF EXISTS `storage_tbl`;
CREATE TABLE `storage_tbl`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commodity_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `count` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `commodity_code`(`commodity_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of storage_tbl
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_capacity
-- ----------------------------
DROP TABLE IF EXISTS `tenant_capacity`;
CREATE TABLE `tenant_capacity`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '配额，0表示使用默认值',
  `usage` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用量',
  `max_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = '租户容量信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant_capacity
-- ----------------------------

-- ----------------------------
-- Table structure for tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `tenant_info`;
CREATE TABLE `tenant_info`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_info_kptenantid`(`kp`, `tenant_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_bin COMMENT = 'tenant_info' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tenant_info
-- ----------------------------
INSERT INTO `tenant_info` VALUES (1, '1', '69c4eecb-05bd-4041-81fe-1473f95f578c', 'dev', '开发环境', 'nacos', 1611922510618, 1612143973429);
INSERT INTO `tenant_info` VALUES (2, '1', '1e017954-eb52-4d21-a843-0286d9013cf3', 'test', '测试环境', 'nacos', 1612144105268, 1612144105268);
INSERT INTO `tenant_info` VALUES (3, '1', '3baec428-9669-486c-b359-a76f7a1f1ac7', 'pro', '生产环境', 'nacos', 1612144127024, 1612144127024);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES ('nacos', '$2a$10$EuWPZHzz32dJN7jexM34MOeYirDdFAZm2kuWj7VEOJhhZkDrxfvUu', 1);

SET FOREIGN_KEY_CHECKS = 1;
