# simple-component

## simple-httpserver
  该项目是一款基于 Spring和netty 的高性能 httpServer 该server类似于 servlet 容器 tomcat 或 jetty ，不同的是 该 server 使用 netty reactor 线程模型进行 http 请求的处理和应答操作
  
  
## simple-httpserver-skywalking-plugin
  该项目是为 分布式追踪服务 skywalking 开发的一款插件，该插件支持 skywalking 分布式追踪服务中使用 simple-httpserver作为服务端的 服务追踪服务

## simple-zookeeper-reference 
  该项目 提供一个 以 zookeeper 作为配置中心，在工程中快速引用的 一个工具，在 项目中指定配置中心中 zookeeper 的地址和 路径 即可使用 @ZKReference 进行配置的读取
## simple-zookeeper-reference-spring-boot-starter
  该项目提供 springboot项目的快速配置功能，只需 在 application.xml 中指定 zookeeper的地址和路径，即可使用 @ZKReference 的使用
