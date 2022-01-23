## 静态资源配置说明

### smart-boot配置说明

- 打开`smart-admin/src/main/resources/application-*.yml`,拉到最后

> 其中`*`对应`application.yml`中的`spring-profiles-active`的值

> 特别注意：配置中的格式(yaml格式)及目录最后的结束符号

```yml

config:
  # Windows环境配置（静态资源根目录和代码生成器临时目录）
  Path: E:\Code\Resources\
  ServiceDirectoryPath: E:\Code\Resources\CodeTemp\

  # Linux、MacOS环境配置（静态资源根目录和代码生成器临时目录）
  # Path: /www/wwwroot/JNPF.Admin.test/Resources/
  # ServiceDirectoryPath: /www/wwwroot/JNPF.Admin.test/Resources/CodeTemp/
  
  # 是否开启测试环境
  TestVersion: false

```

### smart-cloud配置说明

> 特别注意：配置中的格式(yaml格式)及目录最后的结束符号

- 打开`Nacos`控制台，依次选择`配置管理-配置列表-dev`中的`resources.yaml`

```yml

config:
# Windows配置（静态资源根目录和代码生成器临时目录）
Path: E:\Code\Resources\
ServiceDirectoryPath: E:\Code\Resources\CodeTemp\

# Linux、Mac配置（静态资源根目录和代码生成器临时目录）
# Path: /www/wwwroot/JNPF.Admin.test/Resources/
#ServiceDirectoryPath: /www/wwwroot/JNPF.Admin.test/Resources/CodeTemp/

# 是否开启测试环境
TestVersion: false

```

## 静态资源目录结构

```bash
├── BiVisualPath               # 大屏设计
├── CodeTemp                   # 代码生成器临时目录
├── DataBackupFile             # 数据备份 
├── DocumentFile               # 文档
├── DocumentPreview            # 文档预览
├── EmailFile                  # 邮箱附件
├── IMContentFile              # IM聊天
├── MPMaterial                 # 企业微信
├── SystemFile                 # 系统
├── TemplateCode               # 代码生成器模板
│   ├── TemplateCode6          # 功能表单模板
│   ├── TemplateCode7          # 流程表单模板
│   └── TemplateCode8          # 移动表单模板
├── TemplateFile               # 其他模板文档
├── TemporaryFile              # 临时存放目录
├── UserAvatar                 # 用户头像
└── WebAnnexFile               # 

```

