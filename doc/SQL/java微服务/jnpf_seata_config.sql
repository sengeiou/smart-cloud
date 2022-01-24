-- 数据库配置说明

-- 数据库类型 
UPDATE config_info SET content='mysql' WHERE data_id='store.db.dbType';

-- 数据库驱动
UPDATE config_info SET content='com.mysql.cj.jdbc.Driver' WHERE data_id='store.db.driverClassName';

-- 数据库连接串
UPDATE config_info SET content='jdbc:mysql://localhost:3306/jnpf_nacos?serverTimezone=UTC' WHERE data_id='store.db.url';

-- 数据库用户名
UPDATE config_info SET content='root' WHERE data_id='store.db.user';

-- 数据库密码
UPDATE config_info SET content='123456' WHERE data_id='store.db.password';

