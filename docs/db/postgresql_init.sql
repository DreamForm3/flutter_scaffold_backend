------------------------------------------- 脚手架相关 begin -------------------------------------------
create table if not exists foo_bar
(
  id bigserial primary key,
  name varchar(20) not null,
  foo varchar(20) null ,
  bar varchar(20) not null ,
  remark varchar(200) null ,
  state int default 1 not null ,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP null ,
  update_time timestamp null
);
comment on table foo_bar is 'FooBar';
comment on column foo_bar.id is '主键';
comment on column foo_bar.name is 'Name';
comment on column foo_bar.foo is 'Foo';
comment on column foo_bar.bar is 'Bar';
comment on column foo_bar.remark is 'Remark';
comment on column foo_bar.state is 'State，0：Disable，1：Enable';
comment on column foo_bar.version is 'Version';
comment on column foo_bar.create_time is 'Create Time';
comment on column foo_bar.update_time is 'Update Time';

create table if not exists example_order
(
  id bigserial primary key,
  name varchar(20) not null ,
  order_no varchar(100) null ,
  remark varchar(200) null ,
  state int default 1 not null ,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP null ,
  update_time timestamp null
);
comment on table example_order is '订单示例';
comment on column example_order.id is '主键';
comment on column example_order.name is '订单名称';
comment on column example_order.order_no is '订单编号';
comment on column example_order.remark is '备注';
comment on column example_order.state is '状态，0：禁用，1：启用';
comment on column example_order.version is '数据版本';
comment on column example_order.create_time is '创建时间';
comment on column example_order.update_time is '修改时间';

create table if not exists ip_address
(
  id bigserial primary key,
  ip_start varchar(15) not null,
  ip_end varchar(15) not null,
  area varchar(45) null,
  operator varchar(6) null,
  ip_start_num bigint not null,
  ip_end_num bigint not null
);

comment on table ip_address is 'IP地址';
comment on column ip_address.id is '主键';
comment on column ip_address.area is '区域';
comment on column ip_address.operator is '运营商';

create index ip_address_ip_end_num_index on ip_address (ip_end_num);
create index ip_address_ip_start_num_index on ip_address (ip_start_num);

-- 创建 IP 转换成 bigint 的函数
CREATE FUNCTION INET_ATON(text) RETURNS bigint AS $$
SELECT split_part($1,'.',1)::bigint*16777216 + split_part($1,'.',2)::bigint*65536 +
       split_part($1,'.',3)::bigint*256 + split_part($1,'.',4)::bigint;
$$ LANGUAGE SQL  IMMUTABLE RETURNS NULL ON NULL INPUT;

-- 部门
create table if not exists sys_department
(
  id bigserial primary key,
  name varchar(32) not null ,
  parent_id bigint null,
  level int null ,
  state int default 1 not null,
  sort int default 0 not null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP null,
  update_time timestamp null
);

comment on table sys_department is '部门';
comment on column sys_department.id is '主键';
comment on column sys_department.name is '部门名称';
comment on column sys_department.parent_id is '父id';
comment on column sys_department.level is '部门层级';
comment on column sys_department.state is '状态，0：禁用，1：启用';
comment on column sys_department.sort is '排序';
comment on column sys_department.remark is '备注';
comment on column sys_department.is_delete is '逻辑删除标记';
comment on column sys_department.version is '数据版本';
comment on column sys_department.create_time is '创建时间';
comment on column sys_department.update_time is '修改时间';

create unique index uidx_sys_department_name on sys_department (name);

-- 系统权限资源
create table if not exists sys_permission
(
  id bigserial primary key,
  name varchar(32) not null,
  code varchar(100) not null ,
  level int not null ,
  parent_id bigint null ,
  url varchar(200) null ,
  icon varchar(100) null,
  type varchar(100) not null,
  state int default 1 not null ,
  sort int default 0 not null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null ,
  update_time timestamp null ,
  constraint sys_permission_code_uindex unique (code)
);

comment on table sys_permission is '系统权限资源';
comment on column sys_permission.id is '主键';
comment on column sys_permission.name is '权限资源名称';
comment on column sys_permission.code is '唯一编码';
comment on column sys_permission.level is '层级，1：第一级，2：第二级，N：第N级';
comment on column sys_permission.parent_id is '父id';
comment on column sys_permission.url is '路径';
comment on column sys_permission.icon is '图标';
comment on column sys_permission.type is '类型，MENU：菜单，BUTTON：按钮，SERVICE：服务';
comment on column sys_permission.state is '状态，0：禁用，1：启用';
comment on column sys_permission.sort is '排序';
comment on column sys_permission.remark is '备注';
comment on column sys_permission.is_delete is '逻辑删除标记';
comment on column sys_permission.version is '数据版本';
comment on column sys_permission.create_time is '创建时间';
comment on column sys_department.update_time is '修改时间';



-- 系统角色
create table if not exists sys_role
(
  id bigserial primary key,
  name varchar(32) not null ,
  code varchar(100) null ,
  type varchar(100) null ,
  state int default 1 not null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null ,
  update_time timestamp null ,
  constraint sys_role_code_uindex unique (code)
);

comment on table sys_role is '系统角色';
comment on column sys_role.id is '主键';
comment on column sys_role.name is '角色名称';
comment on column sys_role.code is '角色唯一编码';
comment on column sys_role.type is '角色类型';
comment on column sys_role.state is '角色状态，0：禁用，1：启用';
comment on column sys_role.remark is '备注';
comment on column sys_role.is_delete is '逻辑删除标记';
comment on column sys_role.version is '数据版本';
comment on column sys_role.create_time is '创建时间';
comment on column sys_role.update_time is '修改时间';



-- 角色权限关系
create table if not exists sys_role_permission
(
  id bigserial primary key,
  role_id bigint not null ,
  permission_id bigint not null ,
  state int default 1 not null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null ,
  update_time timestamp null
);

create index permission_id on sys_role_permission (permission_id);
create index role_id on sys_role_permission (role_id);

comment on table sys_role_permission is '角色权限关系';
comment on column sys_role_permission.id is '主键';
comment on column sys_role_permission.role_id is '角色id';
comment on column sys_role_permission.permission_id is '权限id';
comment on column sys_role_permission.state is '状态，0：禁用，1：启用';
comment on column sys_role_permission.remark is '备注';
comment on column sys_role_permission.is_delete is '逻辑删除标记';
comment on column sys_role_permission.version is '数据版本';
comment on column sys_role_permission.create_time is '创建时间';
comment on column sys_role_permission.update_time is '修改时间';



-- 系统用户
create table if not exists sys_user
(
  id bigserial primary key,
  username varchar(50) not null ,
  nickname varchar(200) null ,
  password varchar(200) not null ,
  salt varchar(32) null ,
  phone varchar(20) null ,
  email varchar(200) null,
  gender int default 1 not null ,
  avatar varchar(500) null ,
  remark varchar(200) null ,
  state int default 1 not null ,
  department_id bigint null ,
  role_id bigint not null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP null ,
  update_time timestamp null ,
  constraint sys_user_username_uindex unique (username)
);

create index sys_user_department_id on sys_user (department_id);
create index sys_user_role_id on sys_user (role_id);

comment on table sys_user is '系统用户';
comment on column sys_user.id is '主键';
comment on column sys_user.username is '用户名';
comment on column sys_user.nickname is '昵称';
comment on column sys_user.password is '密码';
comment on column sys_user.salt is '盐值';
comment on column sys_user.phone is '手机号码';
comment on column sys_user.email is '邮箱';
comment on column sys_user.gender is '性别，0：女，1：男，默认男';
comment on column sys_user.avatar is '头像';
comment on column sys_user.role_id is '角色id';
comment on column sys_user.department_id is '部门id';
comment on column sys_user.state is '状态，0：禁用，1：启用';
comment on column sys_user.remark is '备注';
comment on column sys_user.is_delete is '逻辑删除标记';
comment on column sys_user.version is '数据版本';
comment on column sys_user.create_time is '创建时间';
comment on column sys_user.update_time is '修改时间';



-- 系统登录日志
create table if not exists sys_login_log
(
  id bigserial primary key,
  request_id varchar(32) null ,
  username varchar(32) null ,
  ip varchar(15) null ,
  area varchar(45) null ,
  operator varchar(6) null ,
  token varchar(32) null ,
  type int null ,
  success boolean default false not null ,
  code int null ,
  exception_message varchar(300) null ,
  user_agent varchar(300) null ,
  browser_name varchar(100) null ,
  browser_version varchar(100) null ,
  engine_name varchar(100) null ,
  engine_version varchar(100) null ,
  os_name varchar(100) null ,
  platform_name varchar(100) null ,
  mobile boolean null ,
  device_name varchar(100) null ,
  device_model varchar(100) null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP null ,
  update_time timestamp null
);

comment on table sys_login_log is '系统登录日志';
comment on column sys_login_log.id is '主键';
comment on column sys_login_log.request_id is '请求ID';
comment on column sys_login_log.username is '用户名';
comment on column sys_login_log.ip is 'IP';
comment on column sys_login_log.area is '区域';
comment on column sys_login_log.operator is '运营商';
comment on column sys_login_log.token is 'tokenMd5值';
comment on column sys_login_log.type is '1:登录，2：登出';
comment on column sys_login_log.success is '是否成功 true:成功/false:失败';
comment on column sys_login_log.code is '响应码';
comment on column sys_login_log.exception_message is '失败消息记录';
comment on column sys_login_log.user_agent is '状态，0：禁用，1：启用';
comment on column sys_login_log.browser_name is '浏览器名称';
comment on column sys_login_log.browser_version is '浏览器版本';
comment on column sys_login_log.engine_name is '浏览器引擎名称';
comment on column sys_login_log.engine_version is '浏览器引擎版本';
comment on column sys_login_log.os_name is '系统名称';
comment on column sys_login_log.platform_name is '平台名称';
comment on column sys_login_log.mobile is '是否是手机,0:否,1:是';
comment on column sys_login_log.device_name is '移动端设备名称';
comment on column sys_login_log.device_model is '移动端设备型号';
comment on column sys_login_log.is_delete is '逻辑删除标记';
comment on column sys_login_log.version is '数据版本';
comment on column sys_login_log.create_time is '创建时间';
comment on column sys_login_log.update_time is '修改时间';

-- 系统操作日志
create table if not exists sys_operation_log
(
  id bigserial primary key,
  request_id varchar(32) null ,
  user_id bigint null ,
  user_name varchar(32) null ,
  name varchar(200) null ,
  ip varchar(15) null ,
  area varchar(45) null ,
  operator varchar(6) null ,
  path varchar(500) null ,
  module varchar(100) null ,
  class_name varchar(100) null ,
  method_name varchar(100) null ,
  request_method varchar(10) null ,
  content_type varchar(100) null ,
  request_body boolean null ,
  param text null ,
  token varchar(32) null ,
  type int null ,
  success boolean null ,
  code int null ,
  message varchar(100) null ,
  exception_name varchar(200) null ,
  exception_message varchar(300) null ,
  browser_name varchar(100) null ,
  browser_version varchar(100) null ,
  engine_name varchar(100) null ,
  engine_version varchar(100) null ,
  os_name varchar(100) null ,
  platform_name varchar(100) null ,
  mobile boolean null ,
  device_name varchar(100) null ,
  device_model varchar(100) null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP null ,
  update_time timestamp null
);

comment on table sys_operation_log is '系统操作日志';
comment on column sys_operation_log.id is '主键';
comment on column sys_operation_log.request_id is '请求ID';
comment on column sys_operation_log.user_id is '用户ID';
comment on column sys_operation_log.user_name is '用户名称';
comment on column sys_operation_log.name is '日志名称';
comment on column sys_operation_log.ip is 'IP';
comment on column sys_operation_log.area is '区域';
comment on column sys_operation_log.operator is '运营商';
comment on column sys_operation_log.path is '全路径';
comment on column sys_operation_log.module is '模块名称';
comment on column sys_operation_log.class_name is '类名';
comment on column sys_operation_log.method_name is '方法名称';
comment on column sys_operation_log.request_method is '请求方式，GET/POST/PUT/DELETE';
comment on column sys_operation_log.content_type is '内容类型';
comment on column sys_operation_log.request_body is '是否是JSON请求映射参数';
comment on column sys_operation_log.param is '请求参数';
comment on column sys_operation_log.token is 'tokenMd5值';
comment on column sys_operation_log.type is '0:其它,1:新增,2:修改,3:删除,4:详情查询,5:所有列表,6:分页列表,7:其它查询,8:上传文件';
comment on column sys_operation_log.success is '是否成功 true:成功/false:失败';
comment on column sys_operation_log.code is '响应结果状态码';
comment on column sys_operation_log.message is '响应结果消息';
comment on column sys_operation_log.exception_name is '异常类名称';
comment on column sys_operation_log.exception_message is '异常信息';
comment on column sys_operation_log.browser_name is '浏览器名称';
comment on column sys_operation_log.browser_version is '浏览器版本';
comment on column sys_operation_log.engine_name is '浏览器引擎名称';
comment on column sys_operation_log.engine_version is '浏览器引擎版本';
comment on column sys_operation_log.os_name is '系统名称';
comment on column sys_operation_log.platform_name is '平台名称';
comment on column sys_operation_log.mobile is '是否是手机,0:否,1:是';
comment on column sys_operation_log.device_name is '移动端设备名称';
comment on column sys_operation_log.device_model is '移动端设备型号';
comment on column sys_operation_log.remark is '备注';
comment on column sys_operation_log.is_delete is '逻辑删除标记';
comment on column sys_operation_log.version is '数据版本';
comment on column sys_operation_log.create_time is '创建时间';
comment on column sys_operation_log.update_time is '修改时间';


-- 初始话系统用户
INSERT INTO sys_user (id, username, nickname, password, salt, phone, gender, avatar, remark, state, department_id, role_id, is_delete, version, create_time, update_time) VALUES (1, 'admin', '管理员', '11a254dab80d52bc4a347e030e54d861a9d2cdb2af2185a9ca4a7318e830d04d', '666', '15888889900', 1, 'http://localhost:8888/api/resource/201908201013068.png', 'Administrator Account', 1, 1, 1, false, 1, '2020-02-26 00:00:00', '2019-10-27 23:32:29');
INSERT INTO sys_user (id, username, nickname, password, salt, phone, gender, avatar, remark, state, department_id, role_id, is_delete, version, create_time, update_time) VALUES (2, 'test', '测试人员', '34783fb724b259beb71a1279f7cd93bdcfd92a273d566f926419a37825c500df', '087c2e9857f35f1e243367f3b89b81c1', '15888889901', 1, 'http://localhost:8888/api/resource/201908201013068.png', 'Tester Account', 1, 1, 2, false, 1, '2020-02-26 00:00:01', '2020-02-15 19:31:50');

-- 初始化系统部门
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (1, '技术部', null, 1, 1, 359544077, 'fe8c9cbac0c54395ac411335a31f4888', 15, '2019-10-25 09:46:49', '2019-11-13 19:56:07');
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (2, '研发部', null, 1, 1, 0, null, 0, '2019-11-01 20:45:43', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (20, '前端开发部', 2, 2, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (21, '后台开发部', 2, 2, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (22, '测试部', 2, 2, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (201, '前端一组', 20, 3, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (202, '前端二组', 20, 3, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (203, '后台一组', 21, 3, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (204, '后台二组', 21, 3, 1, 0, null, 0, '2019-11-01 20:48:38', null);
INSERT INTO sys_department (id, name, parent_id, level, state, sort, remark, version, create_time, update_time) VALUES (205, '测试一组', 22, 3, 1, 0, null, 0, '2019-11-01 20:48:38', null);

-- 初始化系统角色
INSERT INTO sys_role (id, name, code, type, state, remark, version, create_time, update_time) VALUES (1, '管理员', 'admin', null, 1, '管理员拥有所有权限', 0, '2019-10-25 09:47:21', null);
INSERT INTO sys_role (id, name, code, type, state, remark, version, create_time, update_time) VALUES (2, 'test', 'test', null, 1, '测试人员拥有部分权限', 0, '2019-10-25 09:48:02', null);

-- 初始化系统权限资源
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1, '系统管理', null, null, 'system:management', 'el-icon-s-unfold', 1, 1, 1, 0, '1权限备注', 0, '2019-10-26 11:12:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (100, '用户管理', 1, null, 'sys:user:management', 'el-icon-s-unfold', 1, 2, 1, 0, '100权限备注', 0, '2019-10-26 11:15:48', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (200, '角色管理', 1, null, 'sys:role:management', 'el-icon-s-unfold', 1, 2, 1, 0, '200权限备注', 0, '2019-10-26 11:15:48', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (300, '权限管理', 1, null, 'sys:permission:management', 'el-icon-s-unfold', 1, 2, 1, 0, '300权限备注', 0, '2019-10-26 11:15:48', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (400, '部门管理', 1, null, 'sys:department:management', 'el-icon-s-unfold', 1, 2, 1, 0, '400权限备注', 0, '2019-10-26 11:15:48', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (500, '日志管理', 1, null, 'sys:log:manager', 'el-icon-s-custom', 1, 2, 1, 0, '500权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1000, '用户新增', 100, null, 'sys:user:add', 'el-icon-s-custom', 2, 3, 1, 0, '1000权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1001, '用户修改', 100, null, 'sys:user:update', 'el-icon-s-custom', 2, 3, 1, 0, '1001权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1002, '用户删除', 100, null, 'sys:user:delete', 'el-icon-s-custom', 2, 3, 1, 0, '1002权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1003, '用户详情', 100, null, 'sys:user:info', 'el-icon-s-custom', 2, 3, 1, 0, '1003权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1004, '用户分页列表', 100, null, 'sys:user:page', 'el-icon-s-custom', 2, 3, 1, 0, '1004权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1005, '用户修改密码', 100, null, 'sys:user:update:password', 'el-icon-s-custom', 2, 3, 1, 0, '1005权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1006, '用户修改头像', 100, null, 'sys:user:update:avatar', 'el-icon-s-custom', 2, 3, 1, 0, '1006权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (1007, '用户重置密码', 100, null, 'sys:user:reset:password', 'el-icon-s-custom', 2, 3, 1, 0, '1007权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2000, '角色新增', 200, null, 'sys:role:add', 'el-icon-s-custom', 2, 3, 1, 0, '2000权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2001, '角色修改', 200, null, 'sys:role:update', 'el-icon-s-custom', 2, 3, 1, 0, '2001权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2002, '角色删除', 200, null, 'sys:role:delete', 'el-icon-s-custom', 2, 3, 1, 0, '2002权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2003, '角色详情', 200, null, 'sys:role:info', 'el-icon-s-custom', 2, 3, 1, 0, '2003权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2004, '角色分页列表', 200, null, 'sys:role:page', 'el-icon-s-custom', 2, 3, 1, 0, '2004权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2005, '角色列表', 200, null, 'sys:role:list', 'el-icon-s-custom', 2, 3, 1, 0, '2005权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (2006, '角色权限ID列表', 200, null, 'sys:permission:three-ids-by-role-id', 'el-icon-s-custom', 2, 3, 1, 0, '2006权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3000, '权限新增', 300, null, 'sys:permission:add', 'el-icon-s-custom', 2, 3, 1, 0, '3000权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3001, '权限修改', 300, null, 'sys:permission:update', 'el-icon-s-custom', 2, 3, 1, 0, '3001权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3002, '权限删除', 300, null, 'sys:permission:delete', 'el-icon-s-custom', 2, 3, 1, 0, '3002权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3003, '权限详情', 300, null, 'sys:permission:info', 'el-icon-s-custom', 2, 3, 1, 0, '3003权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3004, '权限分页列表', 300, null, 'sys:permission:page', 'el-icon-s-custom', 2, 3, 1, 0, '3004权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3005, '权限所有列表', 300, null, 'sys:permission:all:menu:list', 'el-icon-s-custom', 2, 3, 1, 0, '3005权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3006, '权限所有树形列表', 300, null, 'sys:permission:all:menu:tree', 'el-icon-s-custom', 2, 3, 1, 0, '3006权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3007, '权限用户列表', 300, null, 'sys:permission:menu:list', 'el-icon-s-custom', 2, 3, 1, 0, '3007权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3008, '权限用户树形列表', 300, null, 'sys:permission:menu:tree', 'el-icon-s-custom', 2, 3, 1, 0, '3008权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3009, '权限用户代码列表', 300, null, 'sys:permission:codes', 'el-icon-s-custom', 2, 3, 1, 0, '3009权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3010, '导航菜单', 300, null, 'sys:permission:nav-menu', 'el-icon-s-custom', 2, 3, 1, 0, '3010权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (3011, '角色权限修改', 300, null, 'sys:role-permission:update', 'el-icon-s-custom', 2, 3, 1, 0, '3011权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4000, '部门新增', 400, null, 'sys:department:add', 'el-icon-s-custom', 2, 3, 1, 0, '4000权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4001, '部门修改', 400, null, 'sys:department:update', 'el-icon-s-custom', 2, 3, 1, 0, '4001权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4002, '部门删除', 400, null, 'sys:department:delete', 'el-icon-s-custom', 2, 3, 1, 0, '4002权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4003, '部门详情', 400, null, 'sys:department:info', 'el-icon-s-custom', 2, 3, 1, 0, '4003权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4004, '部门分页列表', 400, null, 'sys:department:page', 'el-icon-s-custom', 2, 3, 1, 0, '4004权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4005, '部门列表', 400, null, 'sys:department:list', 'el-icon-s-custom', 2, 3, 1, 0, '4005权限备注', 1, '2019-10-26 11:18:40', '2020-03-09 00:50:13');
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (4006, '部门树形列表', 400, null, 'sys:department:all:tree', 'el-icon-s-custom', 2, 3, 1, 0, '4006权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (5001, '操作日志列表', 500, null, 'sys:operation:log:page', 'el-icon-s-custom', 2, 3, 1, 0, '5001权限备注', 0, '2019-10-26 11:18:40', null);
INSERT INTO sys_permission (id, name, parent_id, url, code, icon, type, level, state, sort, remark, version, create_time, update_time) VALUES (5002, '登录日志列表', 500, null, 'sys:login:log:page', 'el-icon-s-custom', 2, 3, 1, 0, '5002权限备注', 0, '2019-10-26 11:18:40', null);

-- 初始化角色的权限资源
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (303, 1, 3008, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (304, 1, 1, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (305, 1, 3009, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (306, 1, 3010, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (307, 1, 3011, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (308, 1, 200, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (309, 1, 5001, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (310, 1, 5002, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (311, 1, 2000, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (312, 1, 400, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (313, 1, 2001, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (314, 1, 2002, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (315, 1, 2003, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (316, 1, 2004, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (317, 1, 2005, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (318, 1, 2006, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (319, 1, 4000, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (320, 1, 4001, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (321, 1, 4002, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (322, 1, 4003, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (323, 1, 100, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (324, 1, 4004, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (325, 1, 4005, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (326, 1, 4006, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (327, 1, 1000, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (328, 1, 1001, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (329, 1, 1002, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (330, 1, 1003, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (331, 1, 1004, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (332, 1, 300, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (333, 1, 1005, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (334, 1, 1006, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (335, 1, 1007, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (336, 1, 500, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (337, 1, 3000, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (338, 1, 3001, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (339, 1, 3002, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (340, 1, 3003, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (341, 1, 3004, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (342, 1, 3005, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (343, 1, 3006, 1, null, 0, '2020-04-01 00:14:36', null);
INSERT INTO sys_role_permission (id, role_id, permission_id, state, remark, version, create_time, update_time) VALUES (344, 1, 3007, 1, null, 0, '2020-04-01 00:14:36', null);


-- 其它示例数据初始化
INSERT INTO foo_bar (id, name, foo, bar, remark, state, version, create_time, update_time) VALUES (1, 'test add', 'hello', 'world', '备注', 1, 0, '2020-03-20 11:22:35', null);

INSERT INTO example_order (id, name, order_no, remark, state, version, create_time, update_time) VALUES (1, 'AAA', null, null, 1, 0, '2020-03-12 22:25:35', null);
INSERT INTO example_order (id, name, order_no, remark, state, version, create_time, update_time) VALUES (2, 'BBB', null, null, 1, 0, '2020-03-12 22:25:35', null);
INSERT INTO example_order (id, name, order_no, remark, state, version, create_time, update_time) VALUES (3, 'CCC', null, null, 1, 0, '2020-03-12 22:25:35', null);

INSERT INTO sys_login_log (id, request_id, username, ip, area, operator, token, type, success, code, exception_message, user_agent, browser_name, browser_version, engine_name, engine_version, os_name, platform_name, mobile, device_name, device_model, remark, create_time, update_time) VALUES (1, '1242813712335691777', 'admin', '127.0.0.1', '本机地址', null, 'c87aaffa35dadafb066cf18679eab36e', 1, true, 200, null, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36', 'Chrome', '80.0.3987.149', 'Webkit', '537.36', 'OSX', 'Mac', false, null, null, null, '2020-03-25 22:01:11', null);
INSERT INTO sys_login_log (id, request_id, username, ip, area, operator, token, type, success, code, exception_message, user_agent, browser_name, browser_version, engine_name, engine_version, os_name, platform_name, mobile, device_name, device_model, remark, create_time, update_time) VALUES (2, '1242813887884091393', 'admin', '127.0.0.1', '本机地址', null, 'c87aaffa35dadafb066cf18679eab36e', 2, true, 200, null, 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36', 'Chrome', '80.0.3987.149', 'Webkit', '537.36', 'OSX', 'Mac', false, null, null, null, '2020-03-25 22:01:48', null);
INSERT INTO sys_login_log (id, request_id, username, ip, area, operator, token, type, success, code, exception_message, user_agent, browser_name, browser_version, engine_name, engine_version, os_name, platform_name, mobile, device_name, device_model, remark, create_time, update_time) VALUES (3, '1242814069371625474', 'admin', '127.0.0.1', '本机地址', null, null, 1, false, null, '用户名或密码错误', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36', 'Chrome', '80.0.3987.149', 'Webkit', '537.36', 'OSX', 'Mac', false, null, null, null, '2020-03-25 22:02:25', null);
INSERT INTO sys_login_log (id, request_id, username, ip, area, operator, token, type, success, code, exception_message, user_agent, browser_name, browser_version, engine_name, engine_version, os_name, platform_name, mobile, device_name, device_model, remark, create_time, update_time) VALUES (4, '1242814192096960513', null, '127.0.0.1', '本机地址', null, null, 2, false, null, 'token不能为空', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36', 'Chrome', '80.0.3987.149', 'Webkit', '537.36', 'OSX', 'Mac', false, null, null, null, '2020-03-25 22:02:54', null);

INSERT INTO sys_operation_log (id, request_id, user_id, user_name, name, ip, area, operator, path, module, class_name, method_name, request_method, content_type, request_body, param, token, type, success, code, message, exception_name, exception_message, browser_name, browser_version, engine_name, engine_version, os_name, platform_name, mobile, device_name, device_model, remark, create_time, update_time) VALUES (1, '1242805276474634241', null, null, 'helloWorld', '127.0.0.1', '本机地址', null, '/api/hello/world', null, 'io.geekidea.springbootplus.system.controller.HelloWorldController', 'helloWorld', 'GET', null, false, null, null, 0, true, 200, '操作成功', null, null, 'Chrome', '80.0.3987.149', 'Webkit', '537.36', 'OSX', 'Mac', false, null, null, '', '2020-03-25 21:27:22', null);
INSERT INTO sys_operation_log (id, request_id, user_id, user_name, name, ip, area, operator, path, module, class_name, method_name, request_method, content_type, request_body, param, token, type, success, code, message, exception_name, exception_message, browser_name, browser_version, engine_name, engine_version, os_name, platform_name, mobile, device_name, device_model, remark, create_time, update_time) VALUES (2, '1242820418688049153', null, null, 'FooBar分页列表', '127.0.0.1', '本机地址', null, '/api/fooBar/getPageList', 'foobar', 'com.example.foobar.controller.FooBarController', 'getFooBarPageList', 'POST', 'application/json', true, '{"pageIndex":1,"pageSize":10}', null, 7, true, 200, '操作成功', null, null, 'Chrome', '80.0.3987.149', 'Webkit', '537.36', 'OSX', 'Mac', false, null, null, '', '2020-03-25 22:27:33', null);


-- 调整表主键序列起始值
alter sequence sys_department_id_seq restart with 10000;
alter sequence sys_permission_id_seq restart with 10000;
alter sequence sys_role_id_seq restart with 10000;
alter sequence sys_role_permission_id_seq restart with 10000;
alter sequence sys_user_id_seq restart with 10000;

------------------------------------------- 脚手架相关 end -------------------------------------------


------------------------------------------- 业务相关表 start -------------------------------------------

-- 客户表
create table customer
(
  id bigserial primary key,
  name varchar(200) not null,
  contact_info varchar(100) null,
  create_user bigint not null,
  state int default 1 not null ,
  remark varchar(200) null ,
  is_delete boolean default false,
  version int default 0 not null ,
  create_time timestamp default CURRENT_TIMESTAMP not null ,
  update_time timestamp null
);
comment on table customer is '客户';
comment on column customer.id is '主键';
comment on column customer.name is '客户姓名';
comment on column customer.contact_info is '联系方式';
comment on column customer.create_user is '创建用户';
comment on column customer.state is '状态，0：禁用，1：启用';
comment on column customer.remark is '备注';
comment on column customer.is_delete is '逻辑删除标记';
comment on column customer.version is '数据版本';
comment on column customer.create_time is '创建时间';
comment on column customer.update_time is '修改时间';


-- 前端用户表
create table member
(
    id bigserial primary key,
    username varchar(200) not null,
    password varchar(200) not null,
    salt varchar(200) not null,
    nickname varchar(200) null,
    phone varchar(200) null,
    email varchar(200) not null,
    avatar varchar(500)  null,
    gender int default 1 not null ,
    state int default 1 not null ,
    remark varchar(200) null ,
    is_delete boolean default false,
    version int default 0 not null ,
    create_time timestamp default CURRENT_TIMESTAMP not null ,
    update_time timestamp null
);
comment on table member is '会员';
comment on column member.id is '主键';
comment on column member.username is '用户名';
comment on column member.nickname is '昵称';
comment on column member.password is '密码';
comment on column member.salt is '盐值';
comment on column member.phone is '手机号码';
comment on column member.email is '邮箱';
comment on column member.gender is '性别，0：女，1：男，默认男';
comment on column member.avatar is '头像';
comment on column member.state is '状态，0：禁用，1：启用';
comment on column member.remark is '备注';
comment on column member.is_delete is '逻辑删除标记';
comment on column member.version is '数据版本';
comment on column member.create_time is '创建时间';
comment on column member.update_time is '修改时间';


-- 用户消息表
CREATE TABLE user_msg
(
    id bigserial primary key,

    send_user bigserial not null,
    receive_user bigserial not null,
    title varchar(200) not null,
    content text ,
    status varchar(20) not null,
    reply_id bigserial,
    sent_time timestamp default CURRENT_TIMESTAMP not null ,
    receive_time timestamp,
    reply_time timestamp,
    bak1 varchar,
    bak2 varchar,
    bak3 varchar,
    bak4 varchar,
    bak5 varchar,

    is_delete boolean default false,
    version int default 0 not null ,
    create_time timestamp default CURRENT_TIMESTAMP not null ,
    update_time timestamp null
);
comment on table user_msg is '用户消息';
comment on column user_msg.id is '主键';
comment on column user_msg.send_user is '发送用户';
comment on column user_msg.receive_user is '接收用户';
comment on column user_msg.title is '消息标题';
comment on column user_msg.content is '消息正文';
comment on column user_msg.status is '消息状态，UNREAD：未读；READ：已读；REPLIED：已回复';
comment on column user_msg.reply_id is '回复消息ID';
comment on column user_msg.sent_time is '消息发送时间';
comment on column user_msg.receive_time is '接收方查看时间';
comment on column user_msg.reply_time is '接收方回复时间';

comment on column user_msg.is_delete is '逻辑删除标记';
comment on column user_msg.version is '数据版本';
comment on column user_msg.create_time is '创建时间';
comment on column user_msg.update_time is '修改时间';
-- 增加索引
create index idx_user_msg_receive_user on user_msg(receive_user);
create index idx_user_msg_send_user on user_msg(send_user);
create index idx_user_msg_sent_time on user_msg(sent_time);

------------------------------------------- 业务相关表 end -------------------------------------------