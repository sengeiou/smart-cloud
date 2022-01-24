
-- ----------------------------
-- Table structure for base_tenant
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[base_tenant]') AND type IN ('U'))
	DROP TABLE [dbo].[base_tenant]
GO

CREATE TABLE [dbo].[base_tenant] (
  [F_Id] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [F_EnCode] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_FullName] nvarchar(200) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_CompanyName] nvarchar(200) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_ExpiresTime] datetime2(7)  NULL,
  [F_DbName] text COLLATE Chinese_PRC_CI_AS  NULL,
  [F_IPAddress] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_IPAddressName] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_SourceWebsite] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_Description] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_SortCode] bigint  NULL,
  [F_EnabledMark] int  NULL,
  [F_CreatorTime] datetime2(7)  NULL,
  [F_CreatorUserId] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_LastModifyTime] datetime2(7)  NULL,
  [F_LastModifyUserId] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_DeleteMark] int  NULL,
  [F_DeleteTime] datetime2(7)  NULL,
  [F_DeleteUserId] nvarchar(50) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[base_tenant] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'自然主键',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_Id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'编号',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_EnCode'
GO

EXEC sp_addextendedproperty
'MS_Description', N'名称',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_FullName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'公司',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_CompanyName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'过期时间',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_ExpiresTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'服务名称',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_DbName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'IP地址',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_IPAddress'
GO

EXEC sp_addextendedproperty
'MS_Description', N'IP所在城市',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_IPAddressName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'来源网站',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_SourceWebsite'
GO

EXEC sp_addextendedproperty
'MS_Description', N'描述',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_Description'
GO

EXEC sp_addextendedproperty
'MS_Description', N'排序',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_SortCode'
GO

EXEC sp_addextendedproperty
'MS_Description', N'有效标志',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_EnabledMark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建时间',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_CreatorTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'创建用户',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_CreatorUserId'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改时间',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_LastModifyTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'修改用户',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_LastModifyUserId'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除标志',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_DeleteMark'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除时间',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_DeleteTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'删除用户',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant',
'COLUMN', N'F_DeleteUserId'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户信息',
'SCHEMA', N'dbo',
'TABLE', N'base_tenant'
GO


-- ----------------------------
-- Table structure for base_tenantLog
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[base_tenantLog]') AND type IN ('U'))
	DROP TABLE [dbo].[base_tenantLog]
GO

CREATE TABLE [dbo].[base_tenantLog] (
  [F_Id] varchar(50) COLLATE Chinese_PRC_CI_AS  NOT NULL,
  [F_TenantId] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_LoginAccount] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_LoginIPAddress] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_LoginIPAddressName] varchar(50) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_LoginSourceWebsite] varchar(500) COLLATE Chinese_PRC_CI_AS  NULL,
  [F_LoginTime] datetime  NULL,
  [F_Description] varchar(500) COLLATE Chinese_PRC_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[base_tenantLog] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'自然主键',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_Id'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户主键',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_TenantId'
GO

EXEC sp_addextendedproperty
'MS_Description', N'登录账户',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_LoginAccount'
GO

EXEC sp_addextendedproperty
'MS_Description', N'IP地址',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_LoginIPAddress'
GO

EXEC sp_addextendedproperty
'MS_Description', N'IP所在城市',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_LoginIPAddressName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'来源网站',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_LoginSourceWebsite'
GO

EXEC sp_addextendedproperty
'MS_Description', N'登录时间',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_LoginTime'
GO

EXEC sp_addextendedproperty
'MS_Description', N'描述',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog',
'COLUMN', N'F_Description'
GO

EXEC sp_addextendedproperty
'MS_Description', N'租户日志',
'SCHEMA', N'dbo',
'TABLE', N'base_tenantLog'
GO


-- ----------------------------
-- Primary Key structure for table base_tenant
-- ----------------------------
ALTER TABLE [dbo].[base_tenant] ADD CONSTRAINT [PK__Base_Ten__2C6EC7233C8AB4D8] PRIMARY KEY CLUSTERED ([F_Id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table base_tenantLog
-- ----------------------------
ALTER TABLE [dbo].[base_tenantLog] ADD CONSTRAINT [PK__Base_Ten__2C6EC723AE0AFAEE] PRIMARY KEY CLUSTERED ([F_Id])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

