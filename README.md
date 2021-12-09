### 1.测试服务器地址 http://101.132.164.244:9999/

### 2.nexus http://124.71.227.239:8081/repository/maven-public/ 私服地址(通常不开）



### 3.rocketmq-console地址：http://121.36.76.162:8081/

### 4.swagger 
### qm https://app.swaggerhub.com/apis/mingqcn/OOMALL/1.1.6

### 我们组 https://app.swaggerhub.com/apis/ooad2-01/2-01ooad/1.0.0#/

### 5.nacos界面 http://101.132.164.244:8888/nacos/index.html

**（要看是否在nacos打开   账号：nacos   密码：ooad_javaee201）**



```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://101.132.164.244:3306/ooad?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF8
    username: dbuser
    password: 12345678

  cloud:
    nacos:
    discovery:
    server-addr: 101.132.164.244:8888
    username: nacos
    password: ooad_javaee201

  redis:
    host: 101.132.164.244
    database: 0
    password: 123456
    port: 6889
    
  application:
    name: privilege-service（这个为当前微服务的名字，找的话根据这个去找）
  
#rocketmq:
#  # 指定namesrv地址
#  name-server: 121.36.76.162:9876
#  producer:
#    #生产者group名称
#    group: producer_group
#    #一次发送多少数量消息
#    max-message-size: 4096
#    #发送消息超时时间,默认3000
#    send-message-timeout: 3000
#    #发送消息失败重试次数，默认2
#    retry-times-when-send-async-failed: 2
```

