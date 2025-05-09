![image](https://github.com/user-attachments/assets/cf448d61-aa92-44de-90da-89851c969ce2)
1. 关键表说明
  表名	           核心功能	                            关联关系
​​student​​	        存储学生基本信息	              通过 class 字段关联到 class_history.class_id
​​essay​​	         记录学生提交的作业	              通过 sid 关联到 student.id，通过中间表 student_essay 维护多对多关系
​​homework​​	       定义作业模板	                通过 pid 被 essay_history 和 homework_peer_comment 引用
​​homework_peer_comment​​	管理学生互评数据	        通过 homework_id 关联到 homework.pid，commenter_id/commentee_id 关联到 student.id
​​class_history​​	  记录学生加入/退出班级的时间线	  通过 sid 关联到 student.id
​​essay_history​​	  存档作业提交状态与最终得分	    通过 pid 关联到 homework.pid
2. 服务分层架构：controller层，service层，mapper层。
各层职责：
​​Controller 层​​：接收前端请求，处理参数校验，返回统一响应格式（如 EssayDetailVO）。
​​Service 层​​：实现核心业务逻辑（如防止重复提交、事务控制），调用 Mapper 操作数据库。
​​Mapper 层​​：直接操作 SQL，完成数据增删改查，通过注解或 XML 定义查询语句。
