# SpringCloud

## SpringCloud替代实现

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909015116822.png" alt="image-20220909015116822" style="zoom:200%;" />

## SpringCloud NetFlix

> 文章涉及`Postman`接口脚本在线链接 ，可直接导进自己本地`Postman`

```http
https://www.getpostman.com/collections/8a0954746646f0dee9a6
```

### 前置环境搭建

> 创建一个空的Maven工程

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914013403666.png" alt="image-20220914013403666" style="zoom:200%;" />

![image-20220914013419210](https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914013419210.png)

> 把`src`文件夹删除掉作为一个空的总项目工程文件夹使用

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914013553217.png" alt="image-20220914013553217" style="zoom:200%;" />

> 空的工程到此就搭建好了，后续会不断往里面集成模块

### Eureka

> 服务注册与发现，用于服务管理。
>
> - 在传统应用中，组件之间的调用，通过有规范的约束的接口来实现，从而实现不同模块间良好的协作。但是被拆分成微服务后，每个微服务实例的网络地址都可能动态变化，数量也会变化，使得原来硬编码的地址失去了作用。需要一个中心化的组件来进行服务的登记和管理。
> - 实现服务治理，即管理所有的服务信息和状态

#### 服务注册

> 想要参与`服务注册发现`的实例首先需要向`Eureka服务器注册信息`
>
> `注册`在`第一次心跳`发生时提交

#### Renew

> **续租，心跳**
>
> - Eureka客户需要每30秒发送一次心跳来续租
>
> - 10:00 00 第一次
>
>   > 间隔30s
>
> - 10:00 30 第二次
>
>   > 间隔30s
>
> - 10:01 00 第三次
>
>   > 间隔30s
>
> - 10:01 30 第四次
>
> - 最后`更新通知`Eureka服务器实例仍然是`活动的`。如果服务器在`90秒内`没有看到更新，它将从其`注册表中删除`实例

#### Fetch Registry

> **获取注册表**
>
> - Eureka`客户端`从`服务器`获取`注册表信息`并将其`缓存在本地`
> - `客户端`使用这些信息来`查找其他服务`
> - 通过获取`上一个获取周期`和`当前获取周期`之间的`增量更新`，可以`定期(每30秒)更新`此信息
> - `节点信息`在服务器中保存的时间`更长`(大约3分钟)，因此获取节点信息时可能会`再次返回相同的实例`。Eureka客户端`自动处理`重复的信息
> - 在获得`增量`之后，Eureka`客户端`通过比较`服务器返回的实例计数`来与服务器协调信息，如果由于某种原因信息`不匹配`，则再次获取`整个注册表信息`

#### Cancel

> Eureka`客户端`在`关闭时`向Eureka`服务端`发送取消请求。从`服务端`的`实例注册表`中`删除实例`，从而`有效地`将实例从`通信量`中取出。

#### Time Lag

> **同步时间延迟**
>
> - 来自`Eureka客户端`的`所有操作`可能需要`一段时间`才能反映到`Eureka服务器`上，然后反映到`其他Eureka客户端`上
>
> - 这是因为`eureka服务器`上的`有效负载缓存`，它会`定期刷新`以反映`新信息`。`Eureka客户端`还`定期`地获取`增量`
> - 因此，更改`传播`到`所有Eureka客户机`可能需要`2分钟`

#### Communication mechanism

> **kəˌmjuːnɪˈkeɪʃn ˈmekənɪzəm**
>
> **通讯机制 **
>
> - Http协议下的Rest请求
> - 默认情况下Eureka使用Jersey和Jackson以及JSON完成节点间的通讯

#### Eureka服务端搭建

> 创建Eureka模块，引入`Eureka Server`依赖

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914014011033.png" alt="image-20220914014011033" style="zoom:200%;" />

> **📢注意：**
>
> 我们本次用的SpringCloud版本为  **Hoxton.SR12**
>
> 所以SpringBoot版本要选**2.3.12.RELEASE**
>
> **如果选用其他版本请至[Spring官网](https://spring.io/projects/spring-cloud#learn)查看对应的版本支持**
>
> <img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914014727123.png" alt="image-20220914014727123" style="zoom:200%;" />
>
> <img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914014746664.png" alt="image-20220914014746664" style="zoom:200%;" />
>
> 
>
> 

> 预选依赖
>
> **Release Train Version: Hoxton.SR12**
>
> **Supported Boot Version: 2.3.12.RELEASE**

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914015240503.png" alt="image-20220914015240503" style="zoom:200%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914015307276.png" alt="image-20220914015307276" style="zoom:200%;" />

> 至此`Eureka-Server`模块就创建完成了
>
> 下面开始配置YAML文件

```yaml
server:
  port: 7900

eureka:
  client:
    #是否将自己注册到Eureka Server,默认为true，由于当前就是server，故而设置成false，表明该服务不会向eureka注册自己的信息
    register-with-eureka: false
    #是否从eureka server获取注册信息，由于单节点，不需要同步其他节点数据，用false
    fetch-registry: false
    #设置服务注册中心的URL，用于client和server端交流
    service-url:
      defaultZone: http://localhost:7900/eureka/
```

> 启动程序
>
> 注意要添加`@EnableEurekaServer`注解标识该服务为配置中心

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}  
```

> 启动并在浏览器输入`http://localhost:7900/`即可显示Eureka可视化页面

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909020427408.png" alt="image-20220909020427408" style="zoom:200%;" />

#### Eureka客户端

##### Provider搭建

> 新建一个项目/模块 引入依赖

![20220914_034911_edit(2)](https://i0.hdslb.com/bfs/album/e414001e78524ac778ad7242efc0540204ca26e7.gif)

> 搭建好客户端后修改客户端的`application.yml`配置文件

```yaml
server:
  port: 8080

spring:
  application:
    name: EurekaProvider

eureka:
  client:
    service-url:
      #注册到eureka1节点
      defaultZone: http://eureka1.com:7901/eureka/
```

> 再将服务端的`application.yml`配置文件修改一下，只启动一个节点就可以了

```yaml
spring:
#  profiles:
#    active: eureka2
  application:
    name: EurekaServer

server:
  port: 7901

eureka:
  client:
    service-url:
      #自我注册
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
      #是否将自己注册到其他Eureka Server,默认为true
#      register-with-eureka: true
    #是否从eureka server获取注册信息
  #    fetch-registry: true
  instance:
    #查找主机
    hostname: eureka1.com
```

> 新建`ProviderController`类并写好`pingProvider`接口供后续`Eureka-Consumer`模块测试调用

```java
@RestController
public class ProviderController {

    @GetMapping("/pingProvider")
    public String ping() {
        return "Ping Provider Success";
    }
}
```

> **配置完成先启动服务端再启动客户端**
>
> - 启动完成后如下状态便是客户端注册成功了

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220915005236658.png" alt="image-20220915005236658" style="zoom:200%;" />

> 📢特殊说明：`有兄弟可能会问，前面提到Eureka-Server`不是需要在启动类里面添加`@EnableEurekaServer`注解标识为服务端吗，那`Eureka-Provider`不需要在`@EnableEurekaClient`来标识为客户端吗？
>
> - 不需要了 我们这里用的是 `Hoxton.SR12`新版本
> - 往前推`Hoxton`版本也是不用加这个注解了
> - 只有老版本需要加这个注解标识

##### Consumer搭建

> 按照`Provider搭建`的相同操作再创建一个`Eureka-Consumer`模块

![20220914_034911_edit11](https://i0.hdslb.com/bfs/album/030a313fab1128a525d52b22d56cf6ae803922f7.gif)

> 配置`Eureka-Consumer`的`application.yml`配置文件

```yaml
server:
  port: 8081

spring:
  application:
    name: EurekaConsumer

eureka:
  client:
    service-url:
      #向eureka1点发起请求
      defaultZone: http://eureka1.com:7901/eureka/

```

> 新建`ConsumerController`类
>
> - 主要提供`getClient`服务,查询所有注册节点的信息

```java
@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    /**
     * org.springframework.cloud.client.discovery.DiscoveryClient （Spring官方指定的标准）
     */
    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private EurekaClient eurekaClient;

    /**
     * 心跳接口
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/ping")
    public String ping() {
        return "ping success";
    }


    /**
     * 获取服务列表
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/services")
    public String getServices() {
        List<String> services = discoveryClient.getServices();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        for (String service : services) {
            stringBuffer.append(service + " ");
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    /**
     * 获取应用实例信息
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/instances")
    public Object getInstances() {
        return discoveryClient.getInstances("EurekaProvider");
    }


    /**
     * 获取应用实例信息
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/testProvider")
    public Object pingProvider() {
        //根据服务名找注册列表
        List<InstanceInfo> instances = eurekaClient.getInstancesByVipAddress("EurekaProvider", false);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[ ");
        for (InstanceInfo instanceInfo : instances) {
            stringBuffer.append(instanceInfo + " ");
        }
        stringBuffer.append("]");
        System.out.println(stringBuffer);

        if (instances.size() > 0) {
            InstanceInfo instanceInfo = instances.get(0);
            //如果服务处于活跃状态下则取URL
            if (instanceInfo.getStatus().equals(InstanceInfo.InstanceStatus.UP)) {
                String url = "http://" + instanceInfo.getHostName() + ":" + instanceInfo.getPort() + "/pingProvider";
                ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(url, String.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    String s = responseEntity.getBody();
                    return s;
                } else {
                    return "Bad Request";
                }
            }
        }
        return "Bad Request";
    }
}
```

> 启动`Eureka-Consumer`客户端并关注 `http://eureka1.com:7901/`页面中`Eureka-Consumer`是否如下样例成功注册

<img src="https://i0.hdslb.com/bfs/album/b0447fcc8311c6b14021447024ea0872cbbed654.png" alt="image-20220915035016111" style="zoom:200%;" />

> `Postman`调用接口进行验证
>
> 可直接复制到Postman进行import导入

> 调用`services`服务

```http
curl --location --request GET 'http://localhost:8081/consumer/services'
```

> 说明三个服务（包括Eureka自己）都成功注册并获取到了服务列表

<img src="https://i0.hdslb.com/bfs/album/409870749dd9f1d15bfe279627a193bf448098c9.png" alt="image-20220915035903736" style="zoom:200%;" />

> 调用`instances`服务

```http
curl --location --request GET 'http://localhost:8081/consumer/instances'
```

> 成功获取到了实例信息

<img src="https://i0.hdslb.com/bfs/album/5a2ba4ac861708b05842bd18dbb8b2dd796ff943.png" alt="image-20220915040248737" style="zoom:200%;" />

> 调用`testProvider`接口验证远程调用`Eureka-Provider`客户端的`pingProvider`接口

```http
curl --location --request GET 'http://localhost:8081/consumer/testProvider'
```

![](https://i0.hdslb.com/bfs/album/2540f334537cc2422f255a6d662c68f613344fa7.png)

#### Eureka服务端高可用搭建

> 可以通过运行多个Eureka server实例并相互注册的方式实现。Server节点之间会彼此增量地同步信息，从而确保节点中数据一致。

##### 本地IDE启动修改`hosts`

> 首先修改`hosts`文件对`127.0.0.1`映射主机名区分两台机器

###### Mac修改方式

> 先修改`/etc/hosts`文件权限，默认只读，无法修改

```apl
sudo chmod -R 777 /etc/hosts
#输入开机密码
```

> 然后再对文件进行修改

```apl
vim /etc/hosts
```

> 将以下配置贴入文件中保存即可

```apl
#Eureka
127.0.0.1 eureka1.com
127.0.0.1 eureka2.com
```

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909023333988.png" alt="image-20220909023333988" style="zoom:200%;" />

###### Windows修改方式

> `C:\Windows\System32\drivers\etc\hosts`
>
> 因为没有权限直接修改，我们需要先复制一份文件到桌面然后用`文本文档`打开修改保存后再替换调`C:\Windows\System32\drivers\etc`下的`hosts`文件
>
> - 修改内容同上
> - 最后分别`ping`一下两个域名，如果能`ping`通说明生效了
> - 如果`ping`不通则关闭杀毒软件等重新修改保存再试

<img src="https://i0.hdslb.com/bfs/album/86b2469657c90421bea0bf7c48392957cdc5a41e.png" alt="image-20220919112813266" style="zoom:200%;" />

##### 创建多个节点配置文件

> 新建`application-eureka1.yml`

```yaml
server:
  port: 7901

eureka:
  client:
    service-url:
      #向eureka2节点发起请求
      defaultZone: http://eureka2.com:7902/eureka/
    #是否将自己注册到其他Eureka Server,默认为true
    register-with-eureka: true
    #是否从eureka server获取注册信息
    fetch-registry: true
  instance:
    #查找主机
    hostname: eureka1.com
```

> 新建`application-eureka2.yml`

```yaml
server:
  port: 7902

eureka:
  client:
    service-url:
      #向eureka1节点发起请求
      defaultZone: http://eureka1.com:7901/eureka/
    #是否将自己注册到其他Eureka Server,默认为true
    register-with-eureka: true
    #是否从eureka server获取注册信息
    fetch-registry: true
  instance:
    hostname: eureka2.com
```

> 最后将`application.yml`文件内容修改成一下内容

```yaml
spring:
  profiles:
  	#因为我们有多个配置文件 这个能决定使用哪个配置文件
    active: eureka1
    
  application:
  	#应用名称
    name: EurekaServer
```

> 准备启动，启动前需要知道
>
> - <a id="idea">想要在IDEA启动多个实例需要配置一下</a>

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909042440890.png" alt="image-20220909042440890" style="zoom:200%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909042505383.png" alt="image-20220909042505383" style="zoom:200%;" />

> 当然，可能有部分兄弟`IDEA`版本跟我的不同，界面布局可能跟我的不太一样，可能会是这样的

<img src="C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220919103703664.png" alt="image-20220919103703664" style="zoom:200%;" />

> 没关系，不用慌，该有的功能还是有的，只是放到了不同的地方而已，点击`Modify Options`

<img src="https://i0.hdslb.com/bfs/album/11cb2faf1976b873e796b75022fc229527b700e3.png" alt="image-20220919103849664" style="zoom:200%;" />

> 在弹出的下拉框中选择`Allow multiple instances`

<img src="https://i0.hdslb.com/bfs/album/f1c1b0fe7c665e39b272eeb0048a8946b1206a7a.png" alt="image-20220919103920154" style="zoom:200%;" />

> 点击保存设置，此时便可以继续往下操作了

<img src="https://i0.hdslb.com/bfs/album/021051817db284e8ea92ec2ff703a88ccef3aac9.png" alt="image-20220919104047252" style="zoom:200%;" />

> 启动两个Eureka服务

![20220914_024743_edit(1)](https://i0.hdslb.com/bfs/album/5bcc0240c8995e6e409845637e03b79ab2f40ca6.gif)

> 启动成功后进入浏览器查看`http://localhost:7901/`、`http://localhost:7902/`如下便是配置成功了

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909044617421.png" alt="image-20220909044617421" style="zoom:200%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909044545408.png" alt="image-20220909044545408" style="zoom:200%;" />

##### 自我保护机制

> Eureka在CAP理论当中是属于AP ， 也就说当产生网络分区时，Eureka保证系统的可用性，但不保证系统里面数据的一致性
>
> 默认开启，服务器端容错的一种方式，即短时间心跳不到达仍不剔除服务列表里的节点
>
> 默认情况下，Eureka Server在一定时间内（90S），没有接收到某个微服务心跳，会将这个微服务注销。但是当网络故障时，微服务与Server之间无法正常通信，上述行为就非常危险，因为微服务正常，不应该注销。
>
> Eureka Server通过`自我保护模式`来解决整个问题，当Server在短时间内丢失过多客户端时，那么Server会进入自我保护模式，`会保护注册表中的微服务不被注销掉`。当网络故障恢复后，退出自我保护模式。

##### 触发自我保护

> **客户端每分钟续约数量小于客户端总数的85%时会触发保护机制**

- 当每分钟心跳次数`renewsLastMin `小于 `numberOfRenewsPerMinThreshold` 时，并且`开启`自动保护模式开关( eureka.server.enable-self-preservation = true ) 时，触发自我保护机制，不再自动过期租约。
- `numberOfRenewsPerMinThreshold` = `expectedNumberOfRenewsPerMin` * `续租百分比`( eureka.server.renewalPercentThreshold, 默认0.85 )

- `expectedNumberOfRenewsPerMin` = 当前注册的应用实例数` x 2`(默认情况下，注册的应用实例每半分钟续租一次，那么一分钟心跳两次，因此 x 2 )

- 假设有`10`个服务实例数，期望每分钟续约数`10*2=20`，期望阈值`20*0.85=17`，自我保护`少于17`时 触发。

> **EMERGENCY! EUREKA MAY BE  INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE  LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.**
>
> - 意思是Eureka可能会错误地声称实例已启动，而实际上它们并没有。更新时间小于阈值，因此为了安全起见，实例不会过期。

<img src="https://i0.hdslb.com/bfs/album/a18a2b6ffb16eec45135cce8ce480c2d55426a4b.png" alt="image-20220916030929146" style="zoom:200%;" />

> 关闭自我保护，这个配置默认是true 也就是自我保护机制默认是开启的

```yaml
eureka:
  server:
    #关闭自我保护机制
    enable-self-preservation: false
```

> 关闭后就会出现以下提示
>
> **THE SELF PRESERVATION MODE IS TURNED OFF. THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.**
>
> - 意思是自保模式已关闭。可能不会在出现网络/其他问题时保护实例过期。

<img src="https://i0.hdslb.com/bfs/album/ba83e6e5dbdc3ad4c681e1902098486954aa0c06.png" alt="image-20220916001940004" style="zoom:200%;" />

#### Eureka 健康检查

> 如果我们现在这个自我保护机制没有关闭，就让他是开启的状态，现在我们已经有服务处于不可用状态了，我想手动把他`Dowm`掉,从服务列表中移除掉应该怎么做？
>
> - 当服务抛了某些异常，我能够Catch住异常给这个服务进行手动下线，从服务列表中移除
> - 由于server和client通过心跳保持 服务状态，而只有状态为UP的服务才能被访问。看eureka界面中的status。
> - 有一种场景比如：心跳一直正常，服务一直UP，但是此服务DB连不上了，无法正常提供服务。
> - 这个时候我们就要将微服务的健康状态也同步到server
> - 使用`Actuator`监控应用，启动eureka的健康检查,这样微服务就会将自己的健康状态同步到eureka

##### 开启手动控制

> 在`Eureka-Provider`和`Eureka-Consumer`端配置，将客户端的健康状态传播到服务端 

> 添加依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 在`Eureka-Provider`和`Eureka-Consumer`端的`application.yml`中添加以下配置

```yaml
eureka:
  client:
    healthcheck:
      enabled: true
```

<img src="https://i0.hdslb.com/bfs/album/65ad336c1256b47cc84c97bf23ffbbbfb7558aab.png" alt="image-20220916004443405" style="zoom:200%;" />

> 启动`Eureka-Provider`和`Eureka-Consumer`
>
> 启动成功后可在控制台看到`/actuator`路径打印在了日志中

<img src="https://i0.hdslb.com/bfs/album/4fe330fbecdfea6c6bdf13b7aee5abcd8e4e4273.png" alt="image-20220916010144428" style="zoom:200%;" />

> 访问`/actuator`

```http
curl --location --request GET 'localhost:8080/actuator'
```

<img src="https://i0.hdslb.com/bfs/album/4d67dc4d813a6e483cee6cc258acf059d8b2d4f6.png" alt="image-20220916014556303" style="zoom:200%;" />

> 这个接口也会带一些其他接口URL可供访问

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8080/actuator",
            "templated": false
        },
        "health": {
            "href": "http://localhost:8080/actuator/health",
            "templated": false
        },
        "health-path": {
            "href": "http://localhost:8080/actuator/health/{*path}",
            "templated": true
        }
    }
}
```

#### Api端点功能

> 开启所有端点，在`Eureka-Provider`和`Eureka-Consumer`服务中的`application.yml`中添加以下配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

<img src="https://i0.hdslb.com/bfs/album/d6fedc7d4ad4a666e00d1cb407f53ca793212065.png" alt="image-20220916015040443" style="zoom:200%;" />

> 重启`Eureka-Provider`和`Eureka-Consumer`服务并重新调用`/actuator`接口
>
> 可以看到返回来了所有端点信息

```json
{
    "_links": {
        "self": {
            "href": "http://localhost:8080/actuator",
            "templated": false
        },
        "beans": {
            "href": "http://localhost:8080/actuator/beans",
            "templated": false
        },
        "caches-cache": {
            "href": "http://localhost:8080/actuator/caches/{cache}",
            "templated": true
        },
        "caches": {
            "href": "http://localhost:8080/actuator/caches",
            "templated": false
        },
        "health": {
            "href": "http://localhost:8080/actuator/health",
            "templated": false
        },
        "health-path": {
            "href": "http://localhost:8080/actuator/health/{*path}",
            "templated": true
        },
        "info": {
            "href": "http://localhost:8080/actuator/info",
            "templated": false
        },
        "conditions": {
            "href": "http://localhost:8080/actuator/conditions",
            "templated": false
        },
        "configprops": {
            "href": "http://localhost:8080/actuator/configprops",
            "templated": false
        },
        "configprops-prefix": {
            "href": "http://localhost:8080/actuator/configprops/{prefix}",
            "templated": true
        },
        "env": {
            "href": "http://localhost:8080/actuator/env",
            "templated": false
        },
        "env-toMatch": {
            "href": "http://localhost:8080/actuator/env/{toMatch}",
            "templated": true
        },
        "loggers-name": {
            "href": "http://localhost:8080/actuator/loggers/{name}",
            "templated": true
        },
        "loggers": {
            "href": "http://localhost:8080/actuator/loggers",
            "templated": false
        },
        "heapdump": {
            "href": "http://localhost:8080/actuator/heapdump",
            "templated": false
        },
        "threaddump": {
            "href": "http://localhost:8080/actuator/threaddump",
            "templated": false
        },
        "metrics": {
            "href": "http://localhost:8080/actuator/metrics",
            "templated": false
        },
        "metrics-requiredMetricName": {
            "href": "http://localhost:8080/actuator/metrics/{requiredMetricName}",
            "templated": true
        },
        "scheduledtasks": {
            "href": "http://localhost:8080/actuator/scheduledtasks",
            "templated": false
        },
        "mappings": {
            "href": "http://localhost:8080/actuator/mappings",
            "templated": false
        },
        "refresh": {
            "href": "http://localhost:8080/actuator/refresh",
            "templated": false
        },
        "features": {
            "href": "http://localhost:8080/actuator/features",
            "templated": false
        },
        "serviceregistry": {
            "href": "http://localhost:8080/actuator/serviceregistry",
            "templated": false
        }
    }
}
```

|                       部分接口                        |                             描述                             |
| :---------------------------------------------------: | :----------------------------------------------------------: |
|         http://localhost:8080/actuator/health         |                会显示系统状态 {"status":"UP"}                |
|      http://localhost:8080/actuator/configprops       |                 获取应用中配置的属性信息报告                 |
|         http://localhost:8080/actuator/beans          |                获取应用上下文中创建的所有Bean                |
|          http://localhost:8080/actuator/env           |                获取应用所有可用的环境属性报告                |
|        http://localhost:8080/actuator/mappings        |          获取应用所有Spring Web的控制器映射关系报告          |
|          http://localhost:8080/actuator/info          |                     获取应用自定义的信息                     |
|        http://localhost:8080/actuator/metrics         |                返回应用的各类重要度量指标信息                |
| http://localhost:8080/actuator/metrics/jvm.memory.max | **Metrics**节点并没有返回全量信息，我们可以通过不同的**key**去加载我们想要的值 |
|       http://localhost:8080/actuator/threaddump       |                   返回程序运行中的线程信息                   |

##### ShutDown Api

> 用来关闭节点，需要在`Eureka-Provider`和`Eureka-Consumer`服务中的`application.yml`中添加以下配置

```yaml
management:
  endpoint:
    shutdown:
      enabled: true
```

<img src="https://i0.hdslb.com/bfs/album/daadb86634d7eb16f94691362c5b2755dd26246b.png" alt="image-20220916030618137" style="zoom:200%;" />

> 修改完成重启`Eureka-Provider`和`Eureka-Consumer`服务，使用Post方式请求

```http

```

<img src="https://i0.hdslb.com/bfs/album/2e67d9d6c6c65d783793353225219f48e1dc153b.png" style="zoom: 200%;" />

![image-20220916031719336](https://i0.hdslb.com/bfs/album/f734044a860b4a7ec90e505b72299ef51aca792d.png)

> 关闭成功后再去查看`eureka1.com:7901`服务列表中就没有`Eureka-Provider`服务了

<img src="https://i0.hdslb.com/bfs/album/c4cfd6239b43317e786e5031aae94667da33aa10.png" alt="image-20220916031745534" style="zoom:200%;" />

> 上面了解了这么多健康检查相关的内容，那我现在想修改健康状态的服务该怎么操作呢？
>
> - 在`Eureka-Provider`和`Eureka-Consumer`服务中新建健康状态服务类实现`HealthIndicator`接口

```java
@Service
public class HealthStatusService implements HealthIndicator {
    private Boolean status = true;

    public void setStatus(Boolean status) {
        this.status = status;
    }


    @Override
    public Health health() {
        if (status) {
            return new Health.Builder().up().build();
        }
        return new Health.Builder().down().build();
    }

    public String getStatus() {
        return this.status.toString();
    }
}
```

> 在controller中添加`/health`接口

```java
@GetMapping("/health")
public String health(@RequestParam("status") Boolean status) {
  healthStatusService.setStatus(status);
  return healthStatusService.getStatus();
}
```

> 重启两个服务，我们这边调用`Eureka-Provider`服务的`health`接口,这是我们服务都是启动的，即 "status": "UP"状态

```http
curl --location --request GET 'localhost:8080/health?status=false
```

> 在调用`/actuator/health`接口查看服务状态

```http
curl --location --request GET 'http://localhost:8080/actuator/health
```

<img src="https://i0.hdslb.com/bfs/album/0fe1cbff2e910003fae45bfd3081000f1083e903.png" alt="image-20220916035130722" style="zoom:200%;" />

#### SpringSecurity安全配置

> 开启Eureka安全连接	
>
> 在`Eureka-Server`服务中添加`Spring-Security`依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

> 也可以安装一个`EditStarters`插件，在开发过程中也能方便添加修改spring boot starter.

<img src="https://i0.hdslb.com/bfs/album/e69d42338956acda945452e0dbef2386ff2afcc0.png" alt="image-20220916041625968" style="zoom:200%;" />

> Mac系统在`pom`文件中按`Command`+`N`弹窗选择这个插件即可
>
> Window系统在`pom`文件中按`Alt`+`Insert`弹窗选择这个插件即可

<img src="https://i0.hdslb.com/bfs/album/a5eb11ef8dafd6ed96520beae1640f5b8dfdbc41.png" alt="image-20220916041923940" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/9ece531e4d59830499a51bccdfd32c809f948bf8.png" alt="image-20220916042019059" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/fb206549d0bbf047ed23755da22fd7422a90849e.png" alt="image-20220916041310730" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/c5da1ec5cf33fc96d8eaa6bbe3b5ef2ab7fea7b3.png" alt="image-20220916041337522" style="zoom:200%;" />

> 这样就会自动将依赖添加到`pom`文件中

<img src="https://i0.hdslb.com/bfs/album/4936ca3d3addccba3cc5afef91a4fafc4a5e1e04.png" alt="image-20220916042230459" style="zoom:200%;" />

> 添加完成依赖再到`Eureka-Provider`服务中的`application.yml`中添加以下配置

```yaml
spring:
  security:
    user:
      name: xxx
      password: 123456
```

<img src="https://i0.hdslb.com/bfs/album/a6a545568f377fe67a299f97f8e069c353e82b41.png" alt="image-20220916043622436" style="zoom:200%;" />

> 如果只是修改注册中心的配置是没有用的，其他服务是没法注册进来的，我们还需要去修改所有`application.yml`文件中`defaultZone`配置项
>
> 都要改成`xxxRhysNi:123456@xxx`这种带用户名密码的链接

```yaml
eureka:
  client:
    service-url:
      #向eureka1点发起请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/
```

> 在`Eureka-Server`服务中添加配置类关闭由`Security`提供的跨域攻击保护

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		http.csrf().disable();
		super.configure(http);
	}

}
```

> 重启所有服务并打开浏览器进入`http://localhost:7091/login`、`http://localhost:7092/login`输入用户名密码登录

<img src="https://i0.hdslb.com/bfs/album/9de72212d6d2212182620a79ab6dafec18580342.png" alt="image-20220916044004645" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/f0b0089736893dea1ab8d85effcec4063d18aabc.png" alt="image-20220916045815150" style="zoom:200%;" />

#### [Eureka-Rest服务调用](https://github.com/Netflix/eureka/wiki/Eureka-REST-operations)

> 本地服务调用Url**（官方文档可点击上方链接跳转阅读）**

|                                                **Operation** | **HTTP action**                                              | **Description**                                              |
| -----------------------------------------------------------: | ------------------------------------------------------------ | ------------------------------------------------------------ |
|                            Register new application instance | POST http://eureka1.com:7901/eureka/apps/**appID**           | Input: JSON/XMLpayload HTTPCode: 204 on success              |
|                             De-register application instance | DELETE http://eureka1.com:7901/eureka/apps/**appID**/**instanceID** | HTTP Code: 200 on success                                    |
|                          Send application instance heartbeat | PUT http://eureka1.com:7901/eureka/apps/**appID**/**instanceID** | HTTP Code: * 200 on success * 404 if **instanceID**doesn’t exist |
|                                      Query for all instances | GET http://eureka1.com:7901/eureka/apps                      | HTTP Code: 200 on success Output: JSON/XML                   |
|                            Query for all **appID** instances | GET http://eureka1.com:7901/eureka/apps/**appID**            | HTTP Code: 200 on success Output: JSON/XML                   |
|                Query for a specific **appID**/**instanceID** | GET http://eureka1.com:7901/eureka/apps/**appID**/**instanceID** | HTTP Code: 200 on success Output: JSON/XML                   |
|                          Query for a specific **instanceID** | GET http://eureka1.com:7901/eureka/instances/**instanceID**  | HTTP Code: 200 on success Output: JSON/XML                   |
|                                 Take instance out of service | PUT http://eureka1.com:7901/eureka/apps/**appID**/**instanceID**/status?value=OUT_OF_SERVICE | HTTP Code: * 200 on success * 500 on failure                 |
|            Move instance back into service (remove override) | DELETE http://eureka1.com:7901/eureka/apps/**appID**/**instanceID**/status?value=UP (The value=UP is optional, it is used as a suggestion for the fallback status due to removal of the override) | HTTP Code: * 200 on success * 500 on failure                 |
|                                              Update metadata | PUT http://eureka1.com:7901/eureka/apps/**appID**/**instanceID**/metadata?key=value | HTTP Code: * 200 on success * 500 on failure                 |
|   Query for all instances under a particular **vip address** | GET http://eureka1.com:7901/eureka/vips/**vipAddress**       | * HTTP Code: 200 on success Output: JSON/XML  * 404 if the **vipAddress**does not exist. |
| Query for all instances under a particular **secure vip address** | GET http://eureka1.com:7901/eureka/svips/**svipAddress**     | * HTTP Code: 200 on success Output: JSON/XML  * 404 if the **svipAddress**does not exist. |

> 获取所有节点信息

```http
http://eureka1.com:7901/eureka/apps
```

> 这里面就是我们上面注册的两个实例的节点信息了

![image-20220915010913533](https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220915010913533.png)

> `XML`格式看起来属实让人头疼是吧，我们可以用`Postman`调用
>
> - 在请求头中添加`Accept:application/json`参数即可返回`JSON`格式

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220915012117166.png" alt="image-20220915012117166" style="zoom:200%;" />

```json
{
    "applications": {
        "versions__delta": "1",
        "apps__hashcode": "UP_2_",
        "application": [
            {
                "name": "EUREKASERVER",
                "instance": [
                    {
                        "instanceId": "rhysni.lan:EurekaServer:7901",
                        "hostName": "eureka1.com",
                        "app": "EUREKASERVER",
                        "ipAddr": "192.168.2.237",
                        "status": "UP",
                        "overriddenStatus": "UNKNOWN",
                        "port": {
                            "$": 7901,
                            "@enabled": "true"
                        },
                        "securePort": {
                            "$": 443,
                            "@enabled": "false"
                        },
                        "countryId": 1,
                        "dataCenterInfo": {
                            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                            "name": "MyOwn"
                        },
                        "leaseInfo": {
                            "renewalIntervalInSecs": 30,
                            "durationInSecs": 90,
                            "registrationTimestamp": 1663174216001,
                            "lastRenewalTimestamp": 1663175866438,
                            "evictionTimestamp": 0,
                            "serviceUpTimestamp": 1663174185946
                        },
                        "metadata": {
                            "management.port": "7901"
                        },
                        "homePageUrl": "http://eureka1.com:7901/",
                        "statusPageUrl": "http://eureka1.com:7901/actuator/info",
                        "healthCheckUrl": "http://eureka1.com:7901/actuator/health",
                        "vipAddress": "EurekaServer",
                        "secureVipAddress": "EurekaServer",
                        "isCoordinatingDiscoveryServer": "true",
                        "lastUpdatedTimestamp": "1663174216001",
                        "lastDirtyTimestamp": "1663174185937",
                        "actionType": "ADDED"
                    }
                ]
            },
            {
                "name": "EUREKAPROVIDER",
                "instance": [
                    {
                        "instanceId": "rhysni.lan:EurekaProvider:8080",
                        "hostName": "rhysni.lan",
                        "app": "EUREKAPROVIDER",
                        "ipAddr": "192.168.2.237",
                        "status": "UP",
                        "overriddenStatus": "UNKNOWN",
                        "port": {
                            "$": 8080,
                            "@enabled": "true"
                        },
                        "securePort": {
                            "$": 443,
                            "@enabled": "false"
                        },
                        "countryId": 1,
                        "dataCenterInfo": {
                            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                            "name": "MyOwn"
                        },
                        "leaseInfo": {
                            "renewalIntervalInSecs": 30,
                            "durationInSecs": 90,
                            "registrationTimestamp": 1663174502470,
                            "lastRenewalTimestamp": 1663175852885,
                            "evictionTimestamp": 0,
                            "serviceUpTimestamp": 1663174178276
                        },
                        "metadata": {
                            "management.port": "8080"
                        },
                        "homePageUrl": "http://rhysni.lan:8080/",
                        "statusPageUrl": "http://rhysni.lan:8080/actuator/info",
                        "healthCheckUrl": "http://rhysni.lan:8080/actuator/health",
                        "vipAddress": "EurekaProvider",
                        "secureVipAddress": "EurekaProvider",
                        "isCoordinatingDiscoveryServer": "false",
                        "lastUpdatedTimestamp": "1663174502470",
                        "lastDirtyTimestamp": "1663174502438",
                        "actionType": "ADDED"
                    }
                ]
            }
        ]
    }
}
```

> 服务状态

```http
http://eureka1.com:7901/eureka/status
```

```json
{
    "generalStats": {
        "num-of-cpus": "8",
        //总的可使用内存数
        "total-avail-memory": "693mb",
      	//当前占用多少内存
        "current-memory-usage": "398mb (57%)",
        "server-uptime": "00:35"
    },
    "applicationStats": {
        "registered-replicas": "",
        "available-replicas": "",
        "unavailable-replicas": ""
    },
    "instanceInfo": {
        "instanceId": "rhysni.lan:EurekaServer:7901",
        "hostName": "eureka1.com",
        "app": "EUREKASERVER",
        "ipAddr": "192.168.2.237",
        "status": "UP",
        "overriddenStatus": "UNKNOWN",
        "port": {
            "$": 7901,
            "@enabled": "true"
        },
        "securePort": {
            "$": 443,
            "@enabled": "false"
        },
        "countryId": 1,
        "dataCenterInfo": {
            "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
            "name": "MyOwn"
        },
        "leaseInfo": {
            "renewalIntervalInSecs": 30,
            "durationInSecs": 90,
            "registrationTimestamp": 0,
            "lastRenewalTimestamp": 0,
            "evictionTimestamp": 0,
            "serviceUpTimestamp": 0
        },
        "metadata": {
            "management.port": "7901"
        },
        "homePageUrl": "http://eureka1.com:7901/",
        "statusPageUrl": "http://eureka1.com:7901/actuator/info",
        "healthCheckUrl": "http://eureka1.com:7901/actuator/health",
        "vipAddress": "EurekaServer",
        "secureVipAddress": "EurekaServer",
        "isCoordinatingDiscoveryServer": "false",
        "lastUpdatedTimestamp": "1663174155691",
        "lastDirtyTimestamp": "1663174185937"
    }
```

> 注册到eureka的服务信息查看
>
> `Get`

```http
{ip:port}/eureka/apps
```

> 注册到eureka的具体的服务查看
>
> `Get`

```http
{ip:port}/eureka/apps/{appname}/{id}
```

> 服务续约
>
> `Put`

```http
{ip:port}/eureka/apps/{appname}/{id}?lastDirtyTimestamp={}&status=up
```

> 更改服务状态
>
> - 对应eureka源码的：InstanceResource.statusUpdate
>
> `Put`

```http
{ip:port}/eureka/apps/{appname}/{id}/status?lastDirtyTimestamp={}&value={UP/DOWN}
```

> 删除状态更新
>
> `Delete`

```http
{ip:port}/eureka/apps/{appname}/{id}/status?lastDirtyTimestamp={}&value={UP/DOWN}
```

> 删除服务
>
> `Delete`

```http
{ip:port}/eureka/apps/{appname}/{id}
```

### Ribbon

> 基于客户端的负载均衡。

#### Ribbon调用原理

> - 首先服务得注册到`EurekaServer`（注册中心）
> - 由`EurekaClient`（客户端）获取到注册列表
> - 使用`RestTemplate`发起`Http`请求前使用`Ribbon`进行`choose`根据负载均衡策略获取到一台机器的信息
> - 最后使用注册表中的节点信息去调用具体的服务

<img src="https://i0.hdslb.com/bfs/album/53f94cfb4f38a9878ab1ee4057373557d6fa8d21.png" alt="image-20220919000843949" style="zoom:200%;" />

#### 两种负载均衡

> 当系统面临大量的用户访问，负载过高的时候，通常会增加服务器数量来进行横向扩展（集群），多个服务器的负载需要均衡，以免出现服务器负载不均衡，部分服务器负载较大，部分服务器负载较小的情况。通过负载均衡，使得集群中服务器的负载保持在稳定高效的状态，从而提高整个系统的处理能力。

##### 软负载

- nginx
- lvs

> 第一层可以用DNS，配置多个A记录，让DNS做第一层分发。
>
> 第二层用比较流行的是反向代理，核心原理：代理根据一定规则，将http请求转发到服务器集群的单一服务器上。

##### 硬负载

- F5

#### 负载均衡策略

##### 默认实现

###### ZoneAvoidanceRule

> 区域权衡策略
>
> - 复合判断Server所在区域的性能和Server的可用性，轮询选择服务器

##### 其他规则

###### BestAvailableRule

> 最低并发策略
>
> - 会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务，然后选择一个并发量最小的服务。逐个找服务，如果断路器打开，则忽略

###### RoundRobinRule

> 轮询策略
>
> - 以简单轮询选择一个服务器。按顺序循环选择一个server

###### RandomRule

> 随机策略
>
> - 随机选择一个服务器

###### AvailabilityFilteringRule

> 可用过滤策略
>
> - 会先过滤掉多次访问故障而处于断路器跳闸状态的服务和过滤并发的连接数量超过阀值得服务，然后对剩余的服务列表安装轮询策略进行访问

###### WeightedResponseTimeRule

> 响应时间加权策略
>
> - 据平均响应时间计算所有的服务的权重，响应时间越快服务权重越大，容易被选中的概率就越高。刚启动时，如果统计信息不中，则使用RoundRobinRule(轮询)策略，等统计的信息足够了会自动的切换到WeightedResponseTimeRule。响应时间长，权重低，被选择的概率低。反之，同样道理。此策略综合了各种因素（网络，磁盘，IO等），这些因素直接影响响应时间

###### RetryRule

> 重试策略
>
> - 先按照RoundRobinRule(轮询)的策略获取服务，如果获取的服务失败则在指定的时间会进行重试，进行获取可用的服务。如多次获取某个服务失败，就不会再次获取该服务。主要是在一个时间段内，如果选择一个服务不成功，就继续找可用的服务，直到超时

#### 举个例子🌰

##### 负载均衡

> Ribbon完成客户端的负载均衡并调用`Eureka-Provider`的`/pingProvider`接口

```java
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Resource
    private LoadBalancerClient loadBalancer;


    /**
     * Ribbon完成客户端的负载均衡并过滤掉DOWN了的节点
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/15
     */
    @GetMapping("/testProviderForLB")
    public Object testProviderForLB() {
        //根据服务名找注册列表
        ServiceInstance instance = loadBalancer.choose("EurekaProvider");
        //如果服务处于活跃状态下则取URL
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/pingProvider";
        ResponseEntity<String> responseEntity = new RestTemplate().getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }
```

> 接口uRL

```http
curl --location --request GET 'http://localhost:8081/consumer/testProviderForLB'
```

> 成功选取一个节点并调用成功

![image-20220915045716331](https://i0.hdslb.com/bfs/album/ceffa426748d4b85ed6800e16bb0595d74cf74c8.png)

###### RestTemplate开启负载均衡

> 我们还是在`Eureka-Consumer`中用`RestTemplate`调用`EurekaProvider`
>
> - 首先在`EurekaConsumerApplication`启动类中声明单例`RestTemplate`
> - `@LoadBalanced`注解：org.springframework.cloud.client.loadbalancer.LoadBalanced;

```java
@SpringBootApplication
public class EurekaConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

   // 开启负载均衡
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

> 在`Eureka-Provider`的`/pingProvider`接口中打印日志监控调用的端口号
>
> - 使用`@Value("${server.port}")`获取配置文件中的端口号

```java
@RestController
public class ProviderController {
    @Value("${server.port}")
    private String port;

    @GetMapping("/pingProvider")
    public String ping() {
        return "Ping Provider Success Port:" + port;
    }
}
```

> `Eureka-Consumer`服务接口
>
> - 当`restTemplate`开启了`@LoadBalanced`注解后
> - `EurekaProvider`的主机信息将会被获取，并且请求将委派给LoadBalancerClient调用,就不需要手动写`LoadBalancerClient.choose`去获取解析`host`和`port`了	

```java
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/testProviderForLB")
    public Object testProviderForLB() {
        String url = "http://EurekaProvider/pingProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }
}
```

> 这个时候我们开两个`Eureka-Server`、三`个Eureka-Provider`和一个`Eureka-Consumer`
>
> IDEA启动多个实例操作流程案例👉🏻[点击跳转](#idea)
>
> - 按以上操作启动了两个`Eureka-Server`之后我们这边就只需要修改`Eureka-Provider`服务`application.yml`配置文件中的端口号即可，我这边采用`8080`、`8081`、`8082`
> - 再将`Eureka-Consumer`服务中`application.yml`配置文件的端口号修改为`8090`，规避端口占用
> - 如下图便是启动成功了，可在服务列表看到所有启动的服务信息

<img src="https://i0.hdslb.com/bfs/album/ddec8f226656f28d2e0f2958e396ca88c0883b9d.png" alt="image-20220919005920269" style="zoom:200%;" />

> 进入`http://localhost:7901/`也可以看到服务注册信息

<img src="https://i0.hdslb.com/bfs/album/eaee45bbcd1387e545993b2ed8387031f5291799.png" alt="image-20220919005831038" style="zoom:200%;" />

> 接下来就是使用`Postman`调用`Eureka-Consumer`服务提供的接口了

```http
http://localhost:8090/consumer/testProviderForLB
```

> 我们调用3次看看每次返回的端口号是什么

<img src="https://i0.hdslb.com/bfs/album/104e4e6da5fbe8ffbb1cf763729035d40c4c41dd.png" alt="image-20220919011047734" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/a6f30e786d3529604237becb1f54777b1737a4ed.png" alt="image-20220919011122900" style="zoom:200%;" />

![image-20220919011203134](https://i0.hdslb.com/bfs/album/f11caf96109d0c2b47923ee692265e4eb109f825.png)

> 从以上三次请求可以说明我们负载均衡是生效了，分别请求到了`Eureka-Provider`的`8080`、`8081`、`8082`端口上

##### 切换负载均衡策略

> - 在`EurekaConsumerApplication`启动类中声明单例`IRule`
> - `IRule`：com.netflix.loadbalancer.IRule

```java
@SpringBootApplication
public class EurekaConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    // 开启负载均衡
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public IRule myRule() {
        //return new RoundRobinRule();
        return new RandomRule();
        //return new RetryRule();
    }
}
```

> 可以看到访问的节点完全是随机的，和上面提到的默认策略还是有明显区别的

![soogif(2)(1)](https://i0.hdslb.com/bfs/album/82c0b689cb1a1e86ab76b0bbae7c70826c71ba95.gif)

##### 自定义负载均衡算法

##### 代码方式

> - 首先将`RestTemplate`单例上的`@LoadBalanced`注解注释掉，否则不能走我们的自定义策略
> - 在`Eureka-Consumer`中新增`/testCustomerLB`接口
> - 通过`DiscoveryClient.getInstances("EurekaProvider")`获取到`EurekaProvider`的所有节点信息
> - 自定义算法去获取其中一个节点进行解析调用

```java
    @GetMapping("/testCustomerLB")
    public Object testCustomerLB() {
        //获取所有节点信息
        List<ServiceInstance> instances = discoveryClient.getInstances("EurekaProvider");

        //自定义随机算法
        int nextInt = new Random().nextInt(instances.size());
        ServiceInstance instance = instances.get(nextInt);

        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/pingProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }
```

> 可以看到我们自定义的随机策略效果跟上面`IRule`的`new RandomRule()`算法效果差不多的

![20220920_001330_edit](https://i0.hdslb.com/bfs/album/5e20dc2d51e0f545e21c1951fca9ee47e9e48c04.gif)

##### 配置文件定义负载均衡策略

> 这次我们要走`Ribbon`的负载均衡，不用自定义了，首先注释掉`EurekaConsumerApplication`类中的`IRule`单例

```java
@SpringBootApplication
public class EurekaConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

    /**
     * `@LoadBalanced`:开启负载均衡
     * @author Rhys.Ni
     * @date 2022/9/19
     * @return org.springframework.web.client.RestTemplate
     */
    // @LoadBalanced
  	@Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 修改负载均衡策略
     * @author Rhys.Ni
     * @date 2022/9/19
     * @return com.netflix.loadbalancer.IRule
     */
    // @Bean
    // public IRule myRule() {
    //     // return new RoundRobinRule();
    //     // return new RetryRule();
    //     return new RandomRule();
    // }
}
```

> 然后在`Eureka-Consumer`客户端的`application.yml`添加以下配置

```yaml
#针对EurekaProvider服务定ribbon策略
EurekaProvider:
    ribbon:
      NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule
```

> 再到`ConsumerController`类中新增接口`/testLBConfig`

```java
@GetMapping("/testLBConfig")
public Object testLBConfig() {
  //获取所有节点信息
  ServiceInstance instance = loadBalancer.choose("EurekaProvider");
  String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/pingProvider";
  ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
  if (responseEntity.getStatusCode() == HttpStatus.OK) {
    String result = responseEntity.getBody();
    return result;
  } else {
    return "Bad Request";
  }
}
```

> 重启服务调用`/testLBConfig`接口

```http
curl --location --request GET 'http://localhost:8090/consumer/testLBConfig'
```

> 可以看到，通过配置文件针对具体服务定制负载均衡策略也是同样的效果

<img src="https://i0.hdslb.com/bfs/album/a3b9297a664778f251c01b70ec51debefe1179bf.gif" alt="20220920_013825_edit" style="zoom:200%;" />

##### Ribbon脱离Eureka

> 如果我们不想从Eureka去自动拉取服务注册列表，那我们也可以手动配置好各个节点的`host:port`，也是可以实现负载均的
>
> - 首先我们得往`Eureka-Consumer`中添加以下配置，这边我们把刚刚针对`Eureka-Provider`服务配置得负载均衡策略一并修改为给所有服务配置负载均衡策略看下效果

```yaml
ribbon:
  eureka:
    #脱离Eureka，不会再从Eureka去获取服务列表
    enabled: false
  #让Ribbon使用这个本地服务列表做负载均衡
  listOfServers: localhost:8080,localhost:8081,localhost:8082
```

> 重启`Eureka-Consumer`服务，再次调用`/testLBConfig`接口
>
> 可以看到还是同样的效果，所以综上所述，这几种方式都可以实现Ribbon负载均衡

<img src="https://i0.hdslb.com/bfs/album/8d38b18b1f1a91a92486962469eaae602c484c70.gif" alt="20220920_023648_edit" style="zoom:200%;" />

### Feign

> web调用客户端，能够简化HTTP接口的调用。

#### Feign和OpenFeign

> - OpenFeign是`Netflix`开发的`声明式、模板化`的HTTP请求客户端。可以更加便捷、优雅地调用http api
> - OpenFeign会根据带有注解的函数信息构建出网络请求的模板，在发送网络请求之前，OpenFeign会将函数的参数值设置到这些请求模板中
> - OpenFeign是Spring Cloud 在Feign的基础上支持了Spring MVC的注解，如@RequesMapping
> - OpenFeign的`@FeignClient`可以解析SpringMVC的`@RequestMapping`注解的接口，
>   并通过`动态代理`的方式产生实现类，实现类中做`负载均衡`并调用其他服务。
> - Feign有一套自己的注解，`不支持`Spring MVC的注解
> - Feign主要是构建微服务消费端。只要使用OpenFeign提供的注解修饰定义网络请求的接口类，就可以使用该接口的实例发送RESTful的网络请求。还可以集成`Ribbon`和`Hystrix`，提`供负载均衡`和`断路器`

#### 工程搭建

> 为了对新手更友好，我就不往`Eureka-Provider`和`Eureka-Consumer`客户端中堆代码了，新建`Feign-Api`、`Feign-Provider`和`Feign-Consumer`工程来演示`Feign`的使用，现在我们开始创建工程，按一下顺序逐步创建
>
> - 新建`Service-Api`
> - 新建`Feign-Provider`
> - 新建`Feign-Consumer`

##### Service-Api搭建

<img src="https://i0.hdslb.com/bfs/album/47e58ac999d9c908d0d415ff0104d326fae29221.png" alt="image-20220921040753012" style="zoom:200%;" />

> `Service-Api`只需要添加一个`Spring Web`依赖即可

<img src="https://i0.hdslb.com/bfs/album/0988224ff570597a2862d4ed14693eafa8cc97c0.png" alt="image-20220921040927354" style="zoom:200%;" />

> 新建`ServiceApi`接口，暴露一个`/pingFeignProvider`

```java
@RequestMapping("")
public interface ServiceApi {
    
    @GetMapping("/pingFeignProvider")
    String pingFeignProvider();
}
```

##### FeignProvider搭建

![image-20220921002204196](https://i0.hdslb.com/bfs/album/7baf896825b9de53b7b7f104ea94be3aac7e326d.png)

> `Feign-Provider`这边我们选上`Spring Web`和`Eureka Discovery Client`两个依赖即可，这边的`SpringBoot`版本不用管，本次用的SpringCloud版本为  **Hoxton.SR12**, 所以SpringBoot版本要选**2.3.12.RELEASE** 这里是没有，我们稍后手动到pom文件里面修改版本即可

<img src="https://i0.hdslb.com/bfs/album/0e81bccd9d5c192d958edd1b3b685d006470b20d.png" alt="image-20220921003552664" style="zoom:200%;" />

> <a id="spring-boot-starter-parent.version">修改`pom`文件中`spring-boot-starter-parent - version` 为`2.3.12.RELEASE`</a>

<img src="https://i0.hdslb.com/bfs/album/c33905f923f2dd0050e35891c0817ee05a2c288c.png" alt="image-20220921004416205" style="zoom:200%;" />

> <a id="spring-cloud.version">修改`spring-cloud.version`为`Hoxton.SR12`</a>

<img src="https://i0.hdslb.com/bfs/album/5d8c67006632f9b6988aaf6bbd14e4b4cfcb60b9.png" alt="image-20220921020938975" style="zoom:200%;" />

> 新建`application.yml`文件并添加以下配置
>
> **特殊说明：**这边的`defaultZone`配置项里面我带了一个`RhysNi:123456`这个是因为我的`Eureka-Server`开启了`Spring Security`安全认证，所以需要用户名密码才能成功注册到`Eureka`，如果没有集成过`Spring Security`把这个`defaultZone`配置的值改为`http://eureka1.com:7901/eureka/`即可

```yaml
server:
  port: 9090

spring:
  application:
    name: FeignProvider

eureka:
  client:
    service-url:
      #向eureka1点发起请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/
  healthcheck:
    enabled: true
  instance:
    #查找主机
    hostname: localhost
    instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}
```

> 将`Service-Api`作为依赖添加到`Feign-provider`
>
> - `groupId`记得根据自己定义的进行修改

```xml
<dependency>
  <groupId>com.rhys</groupId>
  <artifactId>Service-Api</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> 新建`FeignProviderController`,由于我们将`Service-Api`作为依赖添加到`Feign-provider`了，可以直接实现`ServiceApi`接口
>
> 那有兄弟要问了："这样的话我如果想像以前一样直接调用这个接口该怎么调用呢？"
>
> - http://localhost:9090/pingFeignProvider 这样就可以了，照常调用就可以了，接口路径保持`ServiceApi`中的路径相同即可

```java
@RestController
public class FeignProviderController implements ServiceApi {

    @Override
    public String pingFeignProvider() {
        return "Ping Feign Provider Success";
    }
}
```

##### Feign-Consumer搭建

<img src="https://i0.hdslb.com/bfs/album/573f2b1006be7d570e0fc0a7bd65510cab6acd38.png" alt="image-20220921015646914" style="zoom:200%;" />

> 同样的在左侧栏里面找到并勾选`Spring Web`和`Eureka Discovery Client`、`OpenFeign`三个依赖

<img src="https://i0.hdslb.com/bfs/album/9abf14ee6f3ba38eff4272887df7c3f4637a7821.png" alt="image-20220921020135899" style="zoom:200%;" />

> 同`Feign-Provider`一样方式修改[`Cloud`](#spring-cloud.version)和[`Boot`](#spring-boot-starter-parent.version)版本 （点击前方关键词跳转查看）

> 新建`application.yml`文件并添加以下配置

```yaml
server:
  port: 9080

spring:
  application:
    name: FeignConsumer

eureka:
  client:
    service-url:
      #向eureka1点发起请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/
  healthcheck:
    enabled: true
  instance:
    #查找主机
    hostname: localhost
    instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}
```

> 由于我们接下来要用`Feign`调用接口，所以要在`Feign-Consumer`的启动类上添加`@EnableFeignClients`注解以支持

```java
@SpringBootApplication
@EnableFeignClients
public class FeignConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }
}
```

> 将`Service-Api`作为依赖添加到`Feign-provider`
>
> - `groupId`记得根据自己定义的进行修改

```xml
<dependency>
  <groupId>com.rhys</groupId>
  <artifactId>Service-Api</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> 新建`FeignConsumerApi`接口，在接口上添加`@FeignClient`注解并继承`ServiceApi`接口（这样就不用我们再次声明一次接口了，减少冗余代码），`@FeignClient`注解的`name`属性填我们要调用的`Feign-Provider`的服务名，我们刚设置的是`FeignProvider`,这样就可以走`Ribbon`进行负载均衡了
>
> 此时便可以在需要调用的类中注入`ServiceApi`调用`Feign-Provider`的接口了

```java
@FeignClient(name = "FeignProvider")
public interface ServiceApi {
  
    @GetMapping("/pingFeignProvider")
    String pingFeignProvider();
}
```

> 新建`FeignConsumerController`并注入`FeignConsumerApi`

```java
@RestController
public class FeignConsumerController {
    @Resource
    private FeignConsumerApi feignConsumerApi;

    @GetMapping("/testOpenFeign")
    public String testOpenFeign() {
        return feignConsumerApi.pingFeignProvider();
    }
}
```

> - 启动`Eureka-Server`服务，启动集群还是单体在本地无所谓，爱动手的兄弟们可以玩玩高可用，[Eureka服务端搭建](#Eureka服务端搭建) 、[Eureka服务端高可用搭建](#Eureka服务端高可用搭建)
> - 启动两个`Feign-Provider`服务，[IDEA应用开启多实例](#idea)
> - 启动一个`Feign-Consumer`服务
> - 调用`Feign-Consumer`中的`/testOpenFeign`接口

<img src="https://i0.hdslb.com/bfs/album/a2faacc214e62b8d8ed3683c56b4c47e6dc93dfc.png" alt="image-20220921052812592" style="zoom:200%;" />

> 可以看到所有服务都在Eureka注册成功了

<img src="https://i0.hdslb.com/bfs/album/92540a797f83c42f3017c740545ed7033ca8b3f7.png" alt="image-20220921052836010" style="zoom:200%;" />

> 调用`http://localhost:9080/testOpenFeign`接口

<img src="https://i0.hdslb.com/bfs/album/9757877fe4098a36c711d54b05dc737ebf15a543.png" alt="image-20220921051749918" style="zoom:200%;" />

#### 超时案例

> 在`Feign-Consumer`服务调用方模块中添加如下配置

```yaml
ribbon:
  #连接超时(ms)
  ConnectTimout: 1000
  #逻辑响应超时(ms)
  ReadTimout: 3000
```

> 我们模拟一下超时场景，在`FeignProviderController`中让这个接口睡上5秒

```java
@RestController
public class FeignProviderController implements ServiceApi {
    private AtomicInteger requestCount = new AtomicInteger();

    @Value("${server.port}")
    private String port;

    @Override
    public String pingFeignProvider() {
        try {
            System.out.println("开始模拟超时");
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
        return "Ping Feign Provider Port:" + port + " Success Count:"+requestCount.incrementAndGet();
    }
}
```

> 我们配置的逻辑响应超时是3s，所以当`Feign-Consumer`调用`Feign-Provider`的`/pingFeignProvider`接口时就会认为接口响应超时了

![image-20220922114222007](https://i0.hdslb.com/bfs/album/308f579908e4427badb62be7ef4ea094bfec6b5b.png)

> Ribbon对于超时还有重试机制，比如咱们起的`Feign-Provider`服务为多实例，Ribbon则会基于负载均衡对其余节点依次执行重试机制

#### 重试机制

> 关于重试机制也是有配置项可配的，一起来看看怎么配
>
> - Feign默认支持Ribbon
> - Ribbon的重试机制和Feign的重试机制有冲突，所以源码中默认关闭Feign的重试机制,使用Ribbon的重试机制
>
> - ribbon重试机制，请求失败后，每6秒会重新尝试

```yaml
ribbon:
  #同一台实例最大重试次数,不包括首次调用
  MaxAutoRetries: 1
  #重试负载均衡其他的实例最大重试次数,不包括首次调用
  MaxAutoRetriesNextServer: 1
  #是否所有操作都重试
  OkToRetryOnAllOperations: false
```

> 注意,接下来启动服务才是本次演示讲究的地方
>
> - 用`睡眠`的那套代码启动一个实例
> - 在注释掉`睡眠`代码，启动两个实例
> - 总共三个实例，模拟Ribbon的重试和`服务降级`（非熔断降级），这边降级意思就是重试一定次数后，在`一定时间内`（一般6秒）就不会再去调这个服务节点，6秒后再有请求过来会再次尝试去调用该服务节点

<img src="https://i0.hdslb.com/bfs/album/d10d49c9f239352b0d9eb7e431062ba876bce1e3.gif" alt="20220923_033735_edit(1)(1)" style="zoom:200%;" />

> 最后再调用`Feign-Consumer`的`/testOpenFeign`接口
>
> - 可以看到每次调用到`超时`的节点时都会进行一次重试，一共是调用两次该接口，所以控制台每次都会打印两行`模拟重试的提示信息`，在重试还是失败的情况下会去调用其他节点进行重试，如果其他节点调用成功则返回

![20220923_044253(1)](https://i0.hdslb.com/bfs/album/3c1b91f2a042eefc38b0f5ce9d69d43794a344cc.gif)

### Hystrix

> 熔断降级，防止服务雪崩。
>
> - 实现了 超时机制和断路器模式
> - 用于隔离远程系统、服务或者第三方库，防止级联失败，从而提升系统的可用性与容错性。
>   - 为系统提供保护机制。在依赖的服务出现高延迟或失败时，为系统提供保护和控制。
>   - 防止雪崩。
>   - 包裹请求：使用HystrixCommand（或HystrixObservableCommand）包裹对依赖的调用逻辑，每个命令在独立线程中运行。
>   - 跳闸机制：当某服务失败率达到一定的阈值时，Hystrix可以自动跳闸，停止请求该服务一段时间。
>   - 资源隔离：Hystrix为每个请求都的依赖都维护了一个小型线程池，如果该线程池已满，发往该依赖的请求就被立即拒绝，而不是排队等候，从而加速失败判定。防止级联失败。
>   - 快速失败：Fail Fast。同时能快速恢复。侧重点是：（不去真正的请求服务，发生异常再返回），而是直接失败。
>   - 监控：Hystrix可以实时监控运行指标和配置的变化，提供近实时的监控、报警、运维控制。
>   - 回退机制：fallback，当请求失败、超时、被拒绝，或当断路器被打开时，执行回退逻辑。回退逻辑我们自定义，提供优雅的服务降级。
>   - 自我修复：断路器打开一段时间后，会自动进入“半开”状态，可以进行打开，关闭，半开状态的转换。前面有介绍。

#### 降级

> 向服务方发起请求，判断连接超时
>
> - 将这次请求记录到服务
> - 尝试向其他服务器发起请求，还是没有请求成功
> - catch异常
>   - 可以返回重试页面，提供重试入口
>   - 返回提示信息

#### 隔离

> **线程隔离（限流）**
>
> 每发起一个Http请求都会开一个独立线程去处理业务，涉及到了线程消耗的问题，为了避免造成线程任务积压
>
> - 当线程数达到线程池线程数上限的时候直接抛出异常，后面来的任务全部不处理，只处理之前还没处理完的请求，这叫隔离/限流

#### 熔断

> 我们在向服务方发起请求失败了，给连续失败次数计数
>
> - 达到阈值的时候抛出异常进入异常处理逻辑

#### 依赖集成

> 在`Feign-Consume`服务请求方添加如下依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

#### 举个例子🌰

##### 直接使用

> new `HystrixTest`类，继承`HystrixCommand`抽象类，实现`run`、`getFallback`方法

```java
public class HystrixTest extends HystrixCommand {
    public static void main(String[] args) {
        Future<String> future = new HystrixTest(HystrixCommandGroupKey.Factory.asKey("doProcess")).queue();
        String reslut = "";
        try {
            reslut = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("reslut:" + reslut);
    }


    @Override
    protected Object run() throws Exception {
        System.out.println("开始执行...");
        //制造异常场景，都知道1/0在java中会抛出ArithmeticException（算术异常）对吧
        int i = 1 / 0;
        return "执行成功...";
    }

    /**
     * 备用逻辑
     *
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2022/9/26
     */
    @Override
    protected Object getFallback() {
        return "异常了，走到了getFallback逻辑";
    }

  	//有很多种构造函数，我们只需要一种就可以了
    public HystrixTest(HystrixCommandGroupKey group) {
        super(group);
    }
}
```

> 运行从结果可以看出程序进了`run`方法并执行算法抛出了异常，那么异常了程序后续该怎么执行呢？

<img src="https://i0.hdslb.com/bfs/album/2ffc7c01adfab0c6830e9be30b837bc16f6f0468.png" alt="image-20220926012316920" style="zoom:200%;" />

> 当程序抛出异常时则会进入备用逻辑`getFallback`方法中，成功执行了备用方法里面的逻辑

<img src="https://i0.hdslb.com/bfs/album/cc171b4f67c506ebcaa8a0d419666c6da39b695d.png" alt="image-20220926012517392" style="zoom:200%;" />

##### Feign整合Hystrix

###### Fallback

> 在`Feign-Consumer`服务`application.yml`配置文件中添加以下配置
>
> - 默认是关闭的

```yaml
feign:
  hystrix:
    enabled: true
```

> new `FeignProviderBack`类，实现`FeignConsumerApi`接口，将接口内所有方法都重新实现一遍，这种形式的降级策略就是针对于每一个独立请求的降级

```java
@Component
public class FeignProviderBack implements FeignConsumerApi {

    @Override
    public String pingFeignProvider() {
        return "降级了,返回了兜底数据";
    }
}
```

>  在`@FeignClient`注解中添加属性`fallback = FeignProviderBack.class`

```java
@FeignClient(name = "FeignProvider",fallback = FeignProviderBack.class)
public interface FeignConsumerApi extends ServiceApi {
}
```

> 在`ServiceApi`中有一点需要注意
>
> - 不能在类上加`@RequestMapping`注解，否则启动的时候会`重复`创建两次`相同的方法`并`且抛异常启动失败`

<img src="https://i0.hdslb.com/bfs/album/07d35f3dbce7fe0a8a1809d3c57ca25f3853ce8d.png" alt="image-20220926020502535" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/7e2e6e9ad7afa92ed8d4117d2cb14631afad5c19.png" alt="image-20220926020407320" style="zoom:200%;" />

> 因此，我们需要去掉这个注解

<img src="https://i0.hdslb.com/bfs/album/28b587de4ebb30df2e4ef23f2a45dfe286d84047.png" alt="image-20220926020606456" style="zoom:200%;" />

> 我们调用一下`http://localhost:9080/testOpenFeign`接口
>
> - 可以看到确实走进了`FeignProviderBack`中的降级逻辑

<img src="https://i0.hdslb.com/bfs/album/c64acf8c0b10ec51314020618519a98c7557f553.png" alt="image-20220926021731825" style="zoom:200%;" />

###### FallbackFactory

>  在`@FeignClient`注解中替换属性`fallback = FeignProviderBack.class`为`fallbackFactory = FeignProviderBackFactory.class`

```java
@FeignClient(name = "FeignProvider",fallbackFactory = FeignProviderBackFactory.class)
public interface FeignConsumerApi extends ServiceApi {
}
```

> new `FeignProviderBackFactory`类，实现`FallbackFactory`接口
>
> - 这边可以针对具体业务的Api使用,这里我针对`FeignConsumerApi`来使用

```java
public class FeignProviderBackFactory implements FallbackFactory<FeignConsumerApi> {
    @Override
    public FeignConsumerApi create(Throwable throwable) {
        return new FeignConsumerApi() {
            @Override
            public String pingFeignProvider() {
                return "FallbackFactory 实现降级了,返回了兜底数据";
            }
        };
    }
}
```

> 重启`Feign-Consumer`服务，再次调用`http://localhost:9080/testOpenFeign`接口
>
> - 可以看到也是生效了的

<img src="https://i0.hdslb.com/bfs/album/c0f2c77981d64756d9ec80552e93960fcea5ae42.png" alt="image-20220926022802974" style="zoom:200%;" />

> 除此以外我们还可以根据异常类型进行判断执行不同的处理逻辑

```java
@Component
public class FeignProviderBackFactory implements FallbackFactory<FeignConsumerApi> {
    @Override
    public FeignConsumerApi create(Throwable throwable) {
        return new FeignConsumerApi() {
            @Override
            public String pingFeignProvider() {
                if (throwable instanceof RuntimeException) {
                    return "请求时异常：" + throwable;
                } else {
                    return "FallbackFactory 实现降级了,返回了兜底数据";
                }
            }
        };
    }
}
```

##### RestTemplate整合Hystrix

> - 在启动类`FeignConsumerApplication`加上`@EnableCircuitBreaker`注解支持`Hystrix`
>   - 有的兄弟可能会问那刚刚Feign集成Hystrix的时候为什么没加这个注解也可以实现？
>     - 因为Feign默认支持Hystrix，只需要在配置文件中控制配置开关即可
> - 在启动类声明`RestTemplate`为单例Bean

```java
@EnableFeignClients
@EnableCircuitBreaker
@SpringBootApplication
public class FeignConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

> new `TestRestService`类
>
> - @HystrixCommand(defaultFallback = "testFallBack")
>   - testFallBack为方法名，所以要新增方法`testFallBack`

```java
@Service
public class TestRestService {

    @Resource
    private RestTemplate restTemplate;

    @HystrixCommand(defaultFallback = "testFallBack")
    public String testOpenFeignWithRest() {
        String url = "http://FeignProvider/pingFeignProvider";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String result = responseEntity.getBody();
            return result;
        } else {
            return "Bad Request";
        }
    }

    private String testFallBack() {
        return "@HystrixCommand 实现了降级，返回了兜底数据";
    }
}
```

> `FeignConsumerController`中新增接口`/testOpenFeignWithRest`
>
> - 注入`TestRestService`服务

```java
@RestController
public class FeignConsumerController {
    @Resource
    private FeignConsumerApi feignConsumerApi;

    @Resource
    private TestRestService restService;

    @GetMapping("/testOpenFeign")
    public String testOpenFeign() {
        return feignConsumerApi.pingFeignProvider();
    }

    @GetMapping("/testOpenFeignWithRest")
    public Object testOpenFeignWithRest() {
        return restService.testOpenFeignWithRest();
    }
}
```

> 重启服务调用`/testOpenFeignWithRest`接口

```http
http://localhost:9080/testOpenFeignWithRest
```

> sa

<img src="https://i0.hdslb.com/bfs/album/4065ad78c3f05d2cbc56462079fb08cc1e50a5b6.png" alt="image-20220926030209425" style="zoom:200%;" />

#### 线程隔离&信号量隔离

> 默认情况下hystrix使用线程池控制请求隔离
>
> - 线程池隔离技术，是用 Hystrix 自己的线程去执行调用；
>
>   ![image-20220926102847104](https://i0.hdslb.com/bfs/album/8f10e6a92daecb842e64851b7c7a0d82a9454ccf.png)
>
> - 信号量隔离技术，是直接让 tomcat 线程去调用依赖服务
>   - 信号量隔离,只是一道关卡,信号量有多少,就允许多少个 tomcat 线程通过它,然后去执行
>   - 信号量隔离主要维护的是Tomcat的线程，不需要内部线程池，更加轻量级。
>   
>   ![image-20220926104124397](https://i0.hdslb.com/bfs/album/85b00f3f486fdb8631e717b8ce10d4c14730ad3d.png)

##### 监控线程池隔离

###### 开启dashboard

> 在`Feign-Consumer`服务调用方添加如下依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 在`Feign-Consumer`启动类添加`@EnableHystrixDashboard`注解

```java
@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
@SpringBootApplication
public class FeignConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignConsumerApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

> 在`Feign-Consumer`配置文件`application.yml`中添加以下配置开启所有端点

```yaml
#开启所有端点
management:
  endpoints:
    web:
      exposure:
        include: "*"

#配置主机地址白名单
hystrix:
  dashboard:
    proxy-stream-allow-list: "localhost"
```

> - 重启`Feign-Consumer`服务，打开图形监控页面

```http
localhost:9080/hystrix
```

> 输入监控上报接口地址

```http
http://localhost:9080/actuator/hystrix.stream
```

![image-20220926123305957](https://i0.hdslb.com/bfs/album/31896daef1cae4b72034dcecef6ed94512ec63c4.png)

![image-20220926115845228](https://i0.hdslb.com/bfs/album/4794c71e37cf67c3d72aff253bc1fe4bd7c8021e.png)

> 进入页面后得调用其他接口让数据进行统计，让页面`动起来`
>
> `Pool Size`：线程池大小，有多少个线程

<img src="https://i0.hdslb.com/bfs/album/8d961f9e3ee11651cbc2ef9e8da1135aca92f382.gif" alt="soogif(3)(1)" style="zoom:200%;" />

##### 监控信号量隔离

> 想要在`dashboard`中监控信号量隔离状态，需要在配置文件中修改隔离策略配置
>
> - 默认`Thread`策略,  `Thread`｜`Semaphore`
> - `Thread` 通过`线程数量`来限制并发请求数，可以提供`额外的保护`，但有一定的`延迟`。一般用于网络调用
> - `SEMAPHORE` 通过`Semaphore Count`来限制并发请求数，适用于`无网络`的`高并发`请求

##### 隔离策略配置

```yaml
hystrix:
  #隔离策略配置
  command:
    default:
      execution:
        isolation:
          strategy: Semaphore
```

> 重启再次刷新页面，调用其他接口增加信息上报数据流动
>
> 因为我们这次切换成了`SEMAPHORE`信号量隔离，可以看到`Thread Pools`栏里是没有相关信息数据的

![image-20220926153443448](https://i0.hdslb.com/bfs/album/43b69cd1441ea62828dfa66f98a5745c0a862877.png)

##### 其他配置

```yaml
hystrix:
  dashboard:
    #配置主机地址白名单
    proxy-stream-allow-list: "localhost"
  #隔离策略配置
  command:
    default:
      execution:
        isolation:
          strategy: Semaphore
          thread:
            #命令执行超时时间，默认1000ms
            timeoutInMilliseconds: 1000
            #发生超时是是否中断
            interruptOnTimeout: true
          semaphore:
            #最大并发请求数
            #默认10，该参数当使用ExecutionIsolationStrategy.SEMAPHORE策略时才有效
            #如果达到最大并发请求数，请求会被拒绝
            #理论上选择semaphore size的原则和选择thread size一致
            #但选用semaphore时每次执行的单元要比较小且执行速度快（ms级别），否则的话应该用thread
            maxConcurrentRequests: 10
        #执行是否启用超时，默认启用true
        timeout:
          enabled: true
```

### Zuul

> 网关路由，提供路由转发、请求过滤、限流降级等功能
>
> Zuul是Netflix开源的微服务网关，核心是一系列过滤器。这些过滤器可以完成以下功能
>
> - 作为所有微服务入口，进行请求分发
> - 可以集成身份认证与安全。识别合法的请求，拦截不合法的请求
> - 可在入口处监控，信息更全面
> - 动态路由，动态将请求分发到不同的后端集群
> - 压力测试,可以逐渐增加对后端服务的流量，进行测试
> - 负载均衡（ribbon）
> - 限流（望京超市）。比如我每秒只要1000次，10001次就不让访问了
> - 服务熔断

#### 网关搭建

> 创建新项目`Zuul`

<img src="https://i0.hdslb.com/bfs/album/30322704d5eb818bd24b04ab6df5a9f0ac975e6a.png" alt="image-20220926235806097" style="zoom:200%;" /> 

> 添加依赖

<img src="https://i0.hdslb.com/bfs/album/d08c8e978a8200adbfd9fff435e91013096e3b5f.png" alt="image-20220927000322346" style="zoom:200%;" />

> 修改`POM`文件中`spring-boot.version`和`spring-cloud.version`

```xml
<spring-boot.version>2.3.12.RELEASE</spring-boot.version>
<spring-cloud.version>Hoxton.SR12</spring-cloud.version>
```

> 在启动类添加`@EnableZuulProxy`注解

```java
@EnableZuulProxy
@SpringBootApplication
public class ZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulApplication.class, args);
    }
}
```

> 添加`application.yml`

```yaml
# 应用名称
spring:
  application:
    name: ZuulServer

server:
  port: 8888

eureka:
  client:
    service-url:
      #向eureka节点发起注册请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/,http://RhysNi:123456@eureka2.com:7902/eureka/,http://RhysNi:123456@eureka3.com:7903/eureka/
  healthcheck:
    enabled: true
  instance:
    #查找主机
    hostname: localhost
    instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}
```

> 由于我们`FeignConsumer`起了多实例，我们需要对`Feign-Consumer`服务中的`/testOpenFeign`接口稍加改造，加上调用端口输出，方便我们观察负载均衡策略是否生效,在`FeignConsumerController`添加`server.port`配置读取

```java
    @Value("${server.port}")
    private String port;

    @GetMapping("/testOpenFeign")
    public String testOpenFeign() {
        return "Consumer:" + port + "-" + feignConsumerApi.pingFeignProvider();
    }
```

> 在正式开始动手之前我们再来思考一个问题，在上文中涉及的调用都是由一台`Xxx-Consumer`去调用多台`Xxx-Provider`，所以我们可以直接使用Feign/Ribbon去做负载均衡，那如果我现在`Xxx-Consumer`也要启动多实例，我们该怎么对多实例`Consumer`服务做负载均衡呢？
>
> - 这也正是`Zuul`存在的意义了,因为Zuul默认集成了 ribbon 和 hystrix对吧，所以可以直接通过`Zuul`网关对下游服务层做负载均衡和熔断

<img src="https://i0.hdslb.com/bfs/album/42a7681b83eb8fa4b0b12b8638f48a5223cde3b8.png" alt="image-20220927032815307" style="zoom:200%;" />

> - 启动三个`Eureka-Server`
> - 启动三个`Feign-Provider`
> - 启动三个`Feign-Consumer`
> - 访问`http://localhost:7901/`查看服务列表是否所有启动的应用都注册到了`Eureka`

<img src="https://i0.hdslb.com/bfs/album/22a9db31c966485fdafbdede7fb23409c3410359.png" alt="image-20220927025606743" style="zoom:200%;" />

> 原来我们调用`Feign-Consumer`服务中的`/testOpenFeign`接口需要指定具体的`ip:port/api`才能匹配到对应URL，现在有了`Zuul`网关调用接口只需要`网关ip:port/serviceId/api`
>
> 📢注意：这里有一个`坑点`一不小心就会导致请求失败，返回`404 Not Found`
>
> - `zuul`会把你注册在注册中心的`serviceId`,例如我们这边的`FeignConsumer` 自动的转成小写去路由。所有我们需要把请求的http路径中`serviceId`转成小写如下

```http
localhost:8888/feignconsumer/testOpenFeign
```

> 可以看到成功从`ZuulServer`分发请求到下游`Feign-Consumer`服务，负载均衡也体现出来了

<img src="https://i0.hdslb.com/bfs/album/30ec7f0587435a85302c492b710d79bdc3d5591b.gif" alt="20220927_033255_edit" style="zoom:200%;" />

#### 服务路由配置

##### 指定微服务访问路径

###### 通过服务名配置

> 在配置文件中加入以下配置

```yaml
zuul:
  routes:
    FeignConsumer: /testFC/**
```

> 重启`ZuulServer`测试
>
> - 将`localhost:8888/feignconsumer/testOpenFeign`替换成`localhost:8888/testFC/testOpenFeign`

<img src="https://i0.hdslb.com/bfs/album/73c6810112bea26b566fe23b428e3bee2531450c.png" alt="image-20220927035058453" style="zoom:200%;" />

##### 请求前缀

> 添加以下配置
>
> - 请求时需将`/api/v1`加在URL前方`localhost:8888/testFC/testOpenFeign`改为`localhost:8888/api/v1/testFC/testOpenFeign`

```yaml
zuul:
  #请求前缀
  prefix: /api/v1
  #是否带上前缀请求
  strip-prefix: true
```

### Sleuth

> 服务链路追踪

#### 集成案例

> 在每个需要监控的服务中添加一下依赖，我们这里就在`Feign-Consumer`和`Feign-Provider`服务中添加相关依赖
>
> - sleuth是Spring cloud的分布式追踪解决方案
> - zipkin是twitter开源的分布式跟踪系统，收集系统的时序数据，从而追踪微服务架构中系统延时等问题，可以通过界面更加友好的展现给用户

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

> 在上述两个服务`application.yml`中添加配置

```yaml
spring:
  #zipkin
  zipkin:
    base-url: http://localhost:9411/
  #采样比例1
  sleuth:
    sampler:
      rate: 1
```

> 到这里代码层面的已经简单配置好了，我们还需要去下载一个`zipkin`的`jar`包，因为`Zipkin`的管理界面不是跑在我们微服务应用中的，是需要他官网的`zipkin.jar`独立运行在服务器中的

##### zipkin下载

> jar包我已经下载下来传到CSDN了,复制以下链接进行下载（免积分）
>
> - 如果是Linux系统的也可以用他官网的`curl -sSL https://zipkin.io/quickstart.sh | bash -s`脚本在命令行执行即可下载

```http
https://download.csdn.net/download/weixin_44977377/86723976
```

> 启动`Zipkin`

```shell
java -jar zipkin.jar
```

<img src="https://i0.hdslb.com/bfs/album/d6528be608f62d30cacbcf24ba5465c80edd9ac7.png" alt="image-20220927193354990" style="zoom:200%;" />

> 然后再重启我们的`Feign-Consumer`和`Feign-Provider`服务调用`localhost:8888/api/v1/testFC/testOpenFeign`接口，这个接口是走的`Zuul`网关`别搞错了`
>
> - 会在被调用控制台看到多出以下信息出来，那这个一串信息代表什么呢？
> - 【服务名，traceId,spanId,是否向zipkin上报信息】

<img src="https://i0.hdslb.com/bfs/album/5cac05efc51e86543ef90853114f2d12b17ac5ef.png" alt="image-20220927162731497" style="zoom:200%;" />

> 调用完成后我们可以访问`Zipkin`来查看调用链路了

<img src="https://i0.hdslb.com/bfs/album/e3ea089b0cba0dfa0cb7cf3909f92b71f010cebd.png" alt="image-20220927194402983" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/715d49142baaf6bd19525e8f8ac94929035a245d.png" alt="image-20220927195211619" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/6e7f90d1149784ffdf3c49f9141176df8320e3ed.png" alt="image-20220927194531052" style="zoom:200%;" />

> 再看看调用出错是什么样子的，我们把`Feign-Provider`服务的`FeignProviderController`中`/pingFeignProvider`接口模拟超时的代码放开

```java
public String pingFeignProvider() {
    try {
        System.out.println("开始模拟超时");
        TimeUnit.MILLISECONDS.sleep(5000);
    } catch (InterruptedException e) {
        throw new RuntimeException();
    }
    return "Ping Feign Provider Port:" + port + " Success Count:"+requestCount.incrementAndGet();
}
```

> 重启`Feign-Provider`服务再次调用`localhost:8888/api/v1/testFC/testOpenFeign`接口

![image-20220927195324220](https://i0.hdslb.com/bfs/album/41544675c20cbf952462f72223305e133ad00210.png)

> 可以清晰看到调用信息和耗时所在

<img src="https://i0.hdslb.com/bfs/album/5b003450ea07e06635db20258dc8eaed6c3a6e58.png" alt="image-20220927195438917" style="zoom:200%;" />

### Admind

> 健康管理

### Config

> 配置中心，分布式配置管理。

## SpringCloud Alibaba

## SpringCloud Apatch
