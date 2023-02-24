## 建表

例子: 
```sql
create table $database.$tableName(
    id int primary key auto_increment,
    name varchar not null,
    gender varchar default '张三' not null,
    age int comment '年龄',
    id_card varchar UNIQUE,
    ) comment ='用户表'
```


注:
 目前未实现类型长度
 
