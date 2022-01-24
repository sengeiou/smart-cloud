# smart-cloud
> 特别说明：源码、JDK、MySQL、Redis、Nacos、Seata、Sentinel等存放路径禁止包含中文、空格、特殊字符等

## 环境要求
> 特别说明：`JDK 1.8.25` 版本无法正常使用，请使用推荐版本

软件  | 推荐版本  | 说明
-----|-------- | -------------
JDK  | 1.8.151 | JAVA环境依赖(需配置环境变量)
Maven  | 3.6.3 | 项目构建(需配置环境变量)
Redis  | 3.2.100(Windows)/6.0.x(Linux、Mac) |
MySQL  | 5.7.x |

# JAVA环境
- JDK1.8
- Redis 3.2.1
- Apache Maven 3.x

# 前端环境
- Node.js 12.18.2
- Yarn 1.22.0

# 数据库
- MySQL 5.7
- SQLServer 2012
- Oracle 11g


## 工具推荐
> 特别说明：`IDEA 2019.1`和`Maven 3.6.3`存在兼容性问题

## maven 私服配置

### SmartCloud官方仓库
server1
```xml
    <server>
        <id>maven-releases</id>
        <username>smart-user</username>
        <password>HLrQ0MA%S1nE</password>
    </server>
    <server>
        <id>maven-snapshots</id>
        <username>smart-user</username>
        <password>HLrQ0MA%S1nE</password>
    </server>
```
mirror1
```xml
	<mirror>
        <id>maven-snapshots</id>
        <mirrorOf>*</mirrorOf>
        <name>maven-snapshots</name>
        <url>https://repository.smartsoft.com/repository/maven-public/</url>
    </mirror>
```
### 内部搭建仓库
server2
```xml
    <server>
        <id>maven-releases</id>
        <username>admin</username>
        <password>123456</password>
    </server>
    <server>
        <id>maven-snapshots</id>
        <username>admin</username>
        <password>123456</password>
    </server>
```
mirror2
```xml
    <mirror>
        <id>maven-public</id>
        <mirrorOf>*</mirrorOf>
        <name>maven-public</name>
        <url>http://39.101.66.185:8899/repository/maven-public/</url>
    </mirror>
```

IDEA 2020.1+

## 服务说明
服务名称  | 默认端口  | 描述
-----|-------- | -------------
nacos  | 30099 | 服务注册、发现和配置中心
sentinel-server   | 30098 | 流量控制、熔断降级、系统负载保护
seata-server  | 30095 | 分布式事务
boot-admin  | 30097 | 管理和监控SpringBoot应用
skywalking  | 30096 | 链路追踪监控
smart-gateway  | 30000 | 网关
smart-oauth  | 30001 | 认证服务
smart-system  | 30002 | 系统运营服务
smart-visualdev  | 30003 | 可视化开发(在线开发、代码生成、大屏设计、门户设计)
smart-workflow  | 30004 | 工作流
smart-file  | 30005 | 文件服务(上传、下载、预览等)
smart-tenant  | 30006 | 租户服务
smart-datareport  | 30007 | 报表服务
smart-extend  | 30019 | 扩展(系统内置示例)
smart-third  | 30020 | 第三方应用(如微信公众号、微信企业等）
smart-example  | 30100 | 子系统开发模板
smart-parking-device  | 30101 | 设备接入服务
smart-parking-trade  | 30102 | 系统交易服务

## 使用说明
### 创建库并导入数据库脚本

> 在使用Navicat等工具时，`运行SQL脚本`执行`jnpf_init.sql`可能会报错(初始数据含有JSON数据)，建议使用`新建查询`执行初始化脚本

- `smart-databae/MySQL/jnpf_init.sql`（项目主库）
- `smart-databae/java微服务/jnpf_nacos.sql`（项目配置库）

### 开发环境配置
#### `Nacos`配置

- 打开`smart-registry/nacos/conf/application.properties`
- 修改数据库配置(`第39-41行`)

#### `Seata`配置

- 打开`smart-registry/seata/conf/file.conf`，修改数据源配置(`第27行开始`)
- 打开数据库仓库的`smart-databae/java微服务/jnpf_seata_config.sql`修改`seata`配置SQL脚本，并在`jnpf_nacos`(项目配置库)中执行脚本

#### `Sentinel`配置

- 右击`smart-registry/sentinel-server/pom.xml` 选择`Add as Maven Project`
- 打开`sentinel-server/src/main/resoures/application.properties`,修改配置`nacos`服务地址(`第29行`)(nacos为本地地址时无需修改)

#### `Skywalking`配置

- 打开`skywalking/config/application.yml` ,修改下数据源配置(`第164-166行`)
- 创建`skywalking`初始表
    - 运行`skywalking/bin/oapServiceInit.bat`(windows环境)
    - 运行`skywalking/bin/oapServiceInit.sh`(Linux、Mac环境)
- `IDEA`启动项中的`VM options`中添加`skywalking/agent/run skywalking-agent.txt`中的`-javaagent:`、`-Dskywalking.agent.service_name`即可，每个启动类都需要添加

### 其他子系统配置

- 启动`nacos`,打开`配置管理`-`配置列表`- `dev`,修改`datasource.yaml`(Redis,数据库配置)和`resources.yaml`(静态资源配置)

### JVM配置(根据实际情况调整)

启动项  | 参考配置| 描述
-----|--------|--------
SmartExampleApplication  |    -Xmx100m -Xms100m -Xmn50m -Xss1024k  | 子系统开发模板
SmartExtendApplication  |      -Xmx200m -Xms200m -Xmn80m -Xss1024k  | 扩展服务
SmartFileAplication   |     -Xmx200m -Xms200m -Xmn100m -Xss1024k  |  文件服务
SmartGatewayApplication  |   -Xmx400m -Xms400m -Xmn150m -Xss1024k  | 网关
SmartOauthApplication   |      -Xmx500m -Xms500m -Xmn150m -Xss1024k  |  授权中心
SmartSystemApplication   |     -Xmx500m -Xms500m -Xmn180m -Xss1024k  |  系统
SmartThirdApplication    |       -Xmx200m -Xms200m -Xmn80m -Xss1024k  |  第三方应用
SmartVisualdevApplication  |  -Xmx800m -Xms800m -Xmn300m -Xss1024k  |  可视化开发
SmartWorkflowApplication  |  -Xmx800m -Xms800m -Xmn300m -Xss1024k  |  工作流
SmartDataReportApplication  |  -Xmx800m -Xms800m -Xmn300m -Xss1024k  |  报表
SmartTenantApplication  |   -Xmx200m -Xms200m -Xmn80m -Xss1024k  |  多租户

####　开发环境配置

在`Run/Debug Configurations`界面按上述表格配置

#### 测试生成环境配置

在启动命令加上配置`-Xmx500m -Xms500m -Xmn180m -Xss1024k`，如
```bash
nohup java -jar -Xmx500m -Xms500m -Xmn180m -Xss1024k smart-system-3.1.0-SNAPSHOT.jar > Log.log & 2>&1 &
```

### 项目启动

> 建议按照如下顺序进行启动，注意监控注册中心，确保每个服务都启动成功

#### `nacos`服务(优先启动等级1)

- 运行`smart-registry/nacos/bin/startup.cmd`(windows环境)
- 运行`smart-registry/nacos/bin/startup.sh`(Linux、Mac环境)
- 打开`http://localhost:30099/nacos/index.html`，默认账号密码为`nacos`

#### `sentinel`服务(优先启动等级2)

- 运行`smart-registry/sentinel-server/src/main/java/com/alibaba/csp/sentinel/dashboard/DashboardApplication.java`启动类

#### `seata`服务(优先启动等级2)

- 运行`smart-registry/seata/bin/startup.bat`(windows环境)
- 运行`smart-registry/seata/bin/startup.sh`(Linux、Mac环境)

#### `skywalking`服务(非必需)

- 运行`skywalking/bin/startup.bat`(windows环境)
- 运行`skywalking/bin/startup.sh`(Linux、Mac环境)

#### `boot-admin`管理和监控SpringBoot应用(非必需)

- 运行`smart-registry/boot-admin/src/main/java/smart/SmartAdminApplication.java`启动类

#### 其他服务(不分先后顺序)

- `smart-gateway`服务(网关)：运行`/smart-gateway/src/main/java/smart/system/base/SmartGatewayApplication.java`启动类

- `smart-oauth`服务(授权中心)：运行`smart-oauth/smart-oauth-server/src/main/java/smart/SmartOauthApplication.java`启动类

- `smart-file`服务(文件)：运行`smart-file/smart-file-server/src/main/java/smart/SmartFileAplication.java`启动类

- `smart-system`服务(系统基础)：运行`smart-system/smart-system-base/smart-system-base-server/src/main/java/smart/SmartSystemApplication.java`启动类

- `smart-visualdev`服务(可视化开发)：运行`smart-visualdev/smart-visualdev-server/src/main/java/smart/SmartVisualdevApplication.java`启动类

- `smart-workflow`服务(工作流)： 运行`smart-workflow/smart-workflow-server/src/main/java/smart/SmartWorkflowApplication.java`启动类

- `smart-datareport`服务(报表)： 运行`smart-report/smart-datareport/report-console/src/main/java/com/bstek/ureport/console/SmartDataReportApplication.java`启动类

- `smart-tenant`服务(多租户)： 运行`smart-tenant/smart-tenant-server/src/main/java/smart/SmartTenantApplication`启动类

- `smart-extend`服务(扩展)： 运行`smart-extend/smart-extend-server/src/main/java/smart/SmartExtendApplication.java`启动类

- `smart-third`服务(第三方应用)： 运行`smart-extend/smart-third-server/src/main/java/smart/SmartThirdApplication.java`启动类

### 服务组件

- Nacos
  - 版本：`1.4.0`
  - 控制台URL: `http://localhost:30099/nacos/index.html`
  - 用户密码：`nacos`/`nacos`

- Sentinel
  - 版本：`1.8.0`
  - 控制台URL: `http://localhost:30098`
  - 用户密码：`sentinel`/`sentinel`

- Spring Boot Admin(可选)
  - 版本：`2.3.0`
  - 控制台URL: `http://localhost:30097`
  - 用户密码：`admin`/`admin`

- Skywalking(可选)
  - 版本：`8.3.0`
  - 控制台URL: `http://localhost:30096`


#### 全局接口
- 打开`http://localhost:30000/swagger-ui.html`

#### maven lib

mvn install:install-file -Dfile=D:\JNPF\jnpf_java\code-java\back-end\smart-java-cloud\lib\aliyun-sdk-oss-3.13.2.jar -DgroupId=com.aliyun.oss -DartifactId=aliyun-sdk-oss -Dversion=3.13.2 -Dpackaging=jar

#### deploy to nexus
mvn deploy:deploy-file -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar -Dfile=D:\repository\com\sqlserver\sqljdbc4\4.0\sqljdbc4-4.0.jar -Durl=http://39.101.66.185:8899/repository/maven-snapshots/ -DrepositoryId=maven-snapshots/

mvn deploy:deploy-file -DgroupId=com.smart -DartifactId=smart-visualdev-base -Dversion=3.1.0-SNAPSHOT -Dpackaging=jar -Dfile=D:\repository_jnpf\com\smart\smart-visualdev-base-api\3.1.0-SNAPSHOT\smart-visualdev-base-api-3.1.0-SNAPSHOT.jar -Durl=http://39.101.66.185:8899/repository/maven-snapshots/ -DrepositoryId=maven-snapshots

