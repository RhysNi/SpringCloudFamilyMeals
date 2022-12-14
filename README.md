# SpringCloud

## SpringCloud替代实现

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909015116822.png" alt="image-20220909015116822" style="zoom:400%;" />

## SpringCloud NetFlix

> 文章涉及`Postman`接口脚本在线链接 ，可直接导进自己本地`Postman`

```http
https://www.getpostman.com/collections/8a0954746646f0dee9a6
```

### 前置环境搭建

> 创建一个空的Maven工程

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914013403666.png" alt="image-20220914013403666" style="zoom:400%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914013419210.png" alt="image-20220914013419210" style="zoom:400%;" />

> 把`src`文件夹删除掉作为一个空的总项目工程文件夹使用

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914013553217.png" alt="image-20220914013553217" style="zoom:400%;" />

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

> **续租**
>
> - Eureka客户需要每30秒发送一次心跳来续租
>
> - 10:00:00 第一次
>
>   > 间隔30s
>
> - 10:00:30 第二次
>
>   > 间隔30s
>
> - 10:01:00 第三次
>
>   > 间隔30s
>
> - 10:01:30 第四次
>
> - 最后`更新通知`Eureka服务器实例仍然是`活跃的`。如果服务器在`90秒内`没有看到更新，它将从其`注册表中删除`实例

#### FetchRegistry

> **拉取注册表**
>
> - Eureka`客户端`从`服务器`获取`注册表信息`并将其`缓存在本地`
> - `客户端`使用这些信息来`查找其他服务`
> - 通过获取`上一个获取周期`和`当前获取周期`之间的`增量更新`，可以`定期(每30秒)更新`此信息
> - `节点信息`在服务器中保存的时间`更长`(大约3分钟)，因此获取节点信息时可能会`再次返回相同的实例`。Eureka客户端`自动处理`重复的信息
> - 在获得`增量`之后，Eureka`客户端`通过比较`服务器返回的实例计数`来与服务器协调信息，如果由于某种原因信息`不匹配`，则再次获取`整个注册表信息`

#### Cancel

> Eureka`客户端`在`关闭时`向Eureka`服务端`发送取消请求。从`服务端`的`实例注册表`中`删除实例`，从而`有效地`将实例从`通信量`中取出。

#### TimeLag

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

<img src="https://i0.hdslb.com/bfs/album/faea852a8025b813b69e9aaabe7590e10859cc7d.png" alt="image-20220914014011033" style="zoom:400%;" />

> **📢注意：**
>
> 我们本次用的SpringCloud版本为  **Hoxton.SR12**
>
> 所以SpringBoot版本要选**2.3.12.RELEASE**
>
> **如果选用其他版本请至[Spring官网](https://spring.io/projects/spring-cloud#learn)查看对应的版本支持**
>
> <img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914014727123.png" alt="image-20220914014727123" style="zoom:400%;" />
>
> <img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914014746664.png" alt="image-20220914014746664" style="zoom:400%;" />
>
> 
>
> 

> 预选依赖
>
> **Release Train Version: Hoxton.SR12**
>
> **Supported Boot Version: 2.3.12.RELEASE**

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914015240503.png" alt="image-20220914015240503" style="zoom:400%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220914015307276.png" alt="image-20220914015307276" style="zoom:400%;" />

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

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909020427408.png" alt="image-20220909020427408" style="zoom:400%;" />

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

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220915005236658.png" alt="image-20220915005236658" style="zoom:400%;" />

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
      #向eureka发起注册请求
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

<img src="https://i0.hdslb.com/bfs/album/b0447fcc8311c6b14021447024ea0872cbbed654.png" alt="image-20220915035016111" style="zoom:400%;" />

> `Postman`调用接口进行验证
>
> 可直接复制到Postman进行import导入

> 调用`services`服务

```http
curl --location --request GET 'http://localhost:8081/consumer/services'
```

> 说明三个服务（包括Eureka自己）都成功注册并获取到了服务列表

<img src="https://i0.hdslb.com/bfs/album/409870749dd9f1d15bfe279627a193bf448098c9.png" alt="image-20220915035903736" style="zoom:400%;" />

> 调用`instances`服务

```http
curl --location --request GET 'http://localhost:8081/consumer/instances'
```

> 成功获取到了实例信息

<img src="https://i0.hdslb.com/bfs/album/5a2ba4ac861708b05842bd18dbb8b2dd796ff943.png" alt="image-20220915040248737" style="zoom:400%;" />

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

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909023333988.png" alt="image-20220909023333988" style="zoom:400%;" />

###### Windows修改方式

> `C:\Windows\System32\drivers\etc\hosts`
>
> 因为没有权限直接修改，我们需要先复制一份文件到桌面然后用`文本文档`打开修改保存后再替换调`C:\Windows\System32\drivers\etc`下的`hosts`文件
>
> - 修改内容同上
> - 最后分别`ping`一下两个域名，如果能`ping`通说明生效了
> - 如果`ping`不通则关闭杀毒软件等重新修改保存再试

<img src="https://i0.hdslb.com/bfs/album/86b2469657c90421bea0bf7c48392957cdc5a41e.png" alt="image-20220919112813266" style="zoom:400%;" />

##### 创建多个节点配置文件

> 新建`application-eureka1.yml`

```yaml
server:
  port: 7901

eureka:
  client:
    service-url:
      #向eureka节点发起请求
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

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909042440890.png" alt="image-20220909042440890" style="zoom:400%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909042505383.png" alt="image-20220909042505383" style="zoom:400%;" />

> 当然，可能有部分兄弟`IDEA`版本跟我的不同，界面布局可能跟我的不太一样，可能会是这样的

<img src="C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220919103703664.png" alt="image-20220919103703664" style="zoom:400%;" />

> 没关系，不用慌，该有的功能还是有的，只是放到了不同的地方而已，点击`Modify Options`

<img src="https://i0.hdslb.com/bfs/album/11cb2faf1976b873e796b75022fc229527b700e3.png" alt="image-20220919103849664" style="zoom:400%;" />

> 在弹出的下拉框中选择`Allow multiple instances`

<img src="https://i0.hdslb.com/bfs/album/f1c1b0fe7c665e39b272eeb0048a8946b1206a7a.png" alt="image-20220919103920154" style="zoom:400%;" />

> 点击保存设置，此时便可以继续往下操作了

<img src="https://i0.hdslb.com/bfs/album/021051817db284e8ea92ec2ff703a88ccef3aac9.png" alt="image-20220919104047252" style="zoom:400%;" />

> 启动两个Eureka服务

![20220914_024743_edit(1)](https://i0.hdslb.com/bfs/album/5bcc0240c8995e6e409845637e03b79ab2f40ca6.gif)

> 启动成功后进入浏览器查看`http://localhost:7901/`、`http://localhost:7902/`如下便是配置成功了

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909044617421.png" alt="image-20220909044617421" style="zoom:400%;" />

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220909044545408.png" alt="image-20220909044545408" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/a18a2b6ffb16eec45135cce8ce480c2d55426a4b.png" alt="image-20220916030929146" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/ba83e6e5dbdc3ad4c681e1902098486954aa0c06.png" alt="image-20220916001940004" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/65ad336c1256b47cc84c97bf23ffbbbfb7558aab.png" alt="image-20220916004443405" style="zoom:400%;" />

> 启动`Eureka-Provider`和`Eureka-Consumer`
>
> 启动成功后可在控制台看到`/actuator`路径打印在了日志中

<img src="https://i0.hdslb.com/bfs/album/4fe330fbecdfea6c6bdf13b7aee5abcd8e4e4273.png" alt="image-20220916010144428" style="zoom:400%;" />

> 访问`/actuator`

```http
curl --location --request GET 'localhost:8080/actuator'
```

<img src="https://i0.hdslb.com/bfs/album/4d67dc4d813a6e483cee6cc258acf059d8b2d4f6.png" alt="image-20220916014556303" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/d6fedc7d4ad4a666e00d1cb407f53ca793212065.png" alt="image-20220916015040443" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/daadb86634d7eb16f94691362c5b2755dd26246b.png" alt="image-20220916030618137" style="zoom:400%;" />

> 修改完成重启`Eureka-Provider`和`Eureka-Consumer`服务，使用Post方式请求

```http

```

<img src="https://i0.hdslb.com/bfs/album/2e67d9d6c6c65d783793353225219f48e1dc153b.png" style="zoom: 800%;" />

![image-20220916031719336](https://i0.hdslb.com/bfs/album/f734044a860b4a7ec90e505b72299ef51aca792d.png)

> 关闭成功后再去查看`eureka1.com:7901`服务列表中就没有`Eureka-Provider`服务了

<img src="https://i0.hdslb.com/bfs/album/c4cfd6239b43317e786e5031aae94667da33aa10.png" alt="image-20220916031745534" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/0fe1cbff2e910003fae45bfd3081000f1083e903.png" alt="image-20220916035130722" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/e69d42338956acda945452e0dbef2386ff2afcc0.png" alt="image-20220916041625968" style="zoom:400%;" />

> Mac系统在`pom`文件中按`Command`+`N`弹窗选择这个插件即可
>
> Window系统在`pom`文件中按`Alt`+`Insert`弹窗选择这个插件即可

<img src="https://i0.hdslb.com/bfs/album/a5eb11ef8dafd6ed96520beae1640f5b8dfdbc41.png" alt="image-20220916041923940" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/9ece531e4d59830499a51bccdfd32c809f948bf8.png" alt="image-20220916042019059" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/fb206549d0bbf047ed23755da22fd7422a90849e.png" alt="image-20220916041310730" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/c5da1ec5cf33fc96d8eaa6bbe3b5ef2ab7fea7b3.png" alt="image-20220916041337522" style="zoom:400%;" />

> 这样就会自动将依赖添加到`pom`文件中

<img src="https://i0.hdslb.com/bfs/album/4936ca3d3addccba3cc5afef91a4fafc4a5e1e04.png" alt="image-20220916042230459" style="zoom:400%;" />

> 添加完成依赖再到`Eureka-Provider`服务中的`application.yml`中添加以下配置

```yaml
spring:
  security:
    user:
      name: xxx
      password: 123456
```

<img src="https://i0.hdslb.com/bfs/album/a6a545568f377fe67a299f97f8e069c353e82b41.png" alt="image-20220916043622436" style="zoom:400%;" />

> 如果只是修改注册中心的配置是没有用的，其他服务是没法注册进来的，我们还需要去修改所有`application.yml`文件中`defaultZone`配置项
>
> 都要改成`xxxRhysNi:123456@xxx`这种带用户名密码的链接

```yaml
eureka:
  client:
    service-url:
      #向eureka发起注册请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/
```

> 在`Eureka-Server`服务中添加配置类关闭由`Security`提供的跨域攻击保护

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        http.headers().frameOptions().disable();
        http.csrf().disable().authorizeRequests().antMatchers("/actuator/**").permitAll().anyRequest().authenticated().and().httpBasic();
    }
}
```

> 重启所有服务并打开浏览器进入`http://localhost:7091/login`、`http://localhost:7092/login`输入用户名密码登录

<img src="https://i0.hdslb.com/bfs/album/9de72212d6d2212182620a79ab6dafec18580342.png" alt="image-20220916044004645" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/f0b0089736893dea1ab8d85effcec4063d18aabc.png" alt="image-20220916045815150" style="zoom:400%;" />

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

<img src="https://gitee.com/rhysni/PicGoImg/raw/master/typora-user-images/image-20220915012117166.png" alt="image-20220915012117166" style="zoom:400%;" />

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

> - 首先`EurekaProvider`（服务提供方）高可用服务得注册到`EurekaServer`（注册中心）
> - 由`EurekConsumer`（服务消费方）获取到注册列表
> - 使用`RestTemplate`发起`Http`请求前使用`Ribbon`进行`choose`根据负载均衡策略获取到一台机器的信息
> - 最后使用注册表中的节点信息去调用具体的服务

<img src="https://i0.hdslb.com/bfs/album/8f10944b8fc5637d09088ff9ca5fd6f343094695.png" alt="image-20221005213346446" style="zoom:200%;" />

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

<img src="https://i0.hdslb.com/bfs/album/ddec8f226656f28d2e0f2958e396ca88c0883b9d.png" alt="image-20220919005920269" style="zoom:400%;" />

> 进入`http://localhost:7901/`也可以看到服务注册信息

<img src="https://i0.hdslb.com/bfs/album/eaee45bbcd1387e545993b2ed8387031f5291799.png" alt="image-20220919005831038" style="zoom:400%;" />

> 接下来就是使用`Postman`调用`Eureka-Consumer`服务提供的接口了

```http
http://localhost:8090/consumer/testProviderForLB
```

> 我们调用3次看看每次返回的端口号是什么

<img src="https://i0.hdslb.com/bfs/album/104e4e6da5fbe8ffbb1cf763729035d40c4c41dd.png" alt="image-20220919011047734" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/a6f30e786d3529604237becb1f54777b1737a4ed.png" alt="image-20220919011122900" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/a3b9297a664778f251c01b70ec51debefe1179bf.gif" alt="20220920_013825_edit" style="zoom:400%;" />

##### Ribbon独立使用

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

<img src="https://i0.hdslb.com/bfs/album/8d38b18b1f1a91a92486962469eaae602c484c70.gif" alt="20220920_023648_edit" style="zoom:400%;" />

### Feign

> web调用客户端，能够简化HTTP接口的调用。

#### Feign和OpenFeign

> - Feign是`Netflix`开发的`声明式、模板化`的HTTP请求客户端。可以更加便捷、优雅地调用http api
> - Feign有一套自己的注解，`不支持`Spring MVC的注解
> - OpenFeign是Spring Cloud 在Feign的基础上支持了Spring MVC的注解，如@RequesMapping
> - OpenFeign的`@FeignClient`可以解析SpringMVC的`@RequestMapping`注解的接口，
>   并通过`动态代理`的方式产生实现类，实现类中做`负载均衡`并调用其他服务。
> - OpenFeign会根据带有注解的函数信息构建出网络请求的模板，在发送网络请求之前，OpenFeign会将函数的参数值设置到这些请求模板中
> - 使用OpenFeign提供的注解修饰定义网络请求的接口类，就可以使用该接口的实例发送RESTful的网络请求。还可以集成`Ribbon`和`Hystrix`，提`供负载均衡`和`断路器`

#### 工程搭建

> 为了对新手更友好，我就不往`Eureka-Provider`和`Eureka-Consumer`客户端中堆代码了，新建`Feign-Api`、`Feign-Provider`和`Feign-Consumer`工程来演示`Feign`的使用，现在我们开始创建工程，按以下顺序逐步创建
>
> - 新建`Service-Api`
> - 新建`Feign-Provider`
> - 新建`Feign-Consumer`

##### Service-Api搭建

<img src="https://i0.hdslb.com/bfs/album/47e58ac999d9c908d0d415ff0104d326fae29221.png" alt="image-20220921040753012" style="zoom:400%;" />

> `Service-Api`只需要添加一个`Spring Web`依赖即可

<img src="https://i0.hdslb.com/bfs/album/0988224ff570597a2862d4ed14693eafa8cc97c0.png" alt="image-20220921040927354" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/0e81bccd9d5c192d958edd1b3b685d006470b20d.png" alt="image-20220921003552664" style="zoom:400%;" />

> <a id="spring-boot-starter-parent.version">修改`pom`文件中`spring-boot-starter-parent - version` 为`2.3.12.RELEASE`</a>

<img src="https://i0.hdslb.com/bfs/album/c33905f923f2dd0050e35891c0817ee05a2c288c.png" alt="image-20220921004416205" style="zoom:400%;" />

> <a id="spring-cloud.version">修改`spring-cloud.version`为`Hoxton.SR12`</a>

<img src="https://i0.hdslb.com/bfs/album/5d8c67006632f9b6988aaf6bbd14e4b4cfcb60b9.png" alt="image-20220921020938975" style="zoom:400%;" />

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
      #向eureka发起注册请求
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

<img src="https://i0.hdslb.com/bfs/album/573f2b1006be7d570e0fc0a7bd65510cab6acd38.png" alt="image-20220921015646914" style="zoom:400%;" />

> 同样的在左侧栏里面找到并勾选`Spring Web`和`Eureka Discovery Client`、`OpenFeign`三个依赖

<img src="https://i0.hdslb.com/bfs/album/9abf14ee6f3ba38eff4272887df7c3f4637a7821.png" alt="image-20220921020135899" style="zoom:400%;" />

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
      #向eureka发起注册请求
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

<img src="https://i0.hdslb.com/bfs/album/a2faacc214e62b8d8ed3683c56b4c47e6dc93dfc.png" alt="image-20220921052812592" style="zoom:400%;" />

> 可以看到所有服务都在Eureka注册成功了

<img src="https://i0.hdslb.com/bfs/album/92540a797f83c42f3017c740545ed7033ca8b3f7.png" alt="image-20220921052836010" style="zoom:400%;" />

> 调用`http://localhost:9080/testOpenFeign`接口

<img src="https://i0.hdslb.com/bfs/album/9757877fe4098a36c711d54b05dc737ebf15a543.png" alt="image-20220921051749918" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/d10d49c9f239352b0d9eb7e431062ba876bce1e3.gif" alt="20220923_033735_edit(1)(1)" style="zoom:400%;" />

> 最后再调用`Feign-Consumer`的`/testOpenFeign`接口
>
> - 可以看到每次调用到`超时`的节点时都会进行一次重试，一共是调用两次该接口，所以控制台每次都会打印两行`模拟重试的提示信息`，在重试还是失败的情况下会去调用其他节点进行重试，如果其他节点调用成功则返回

![20220923_044253(1)](https://i0.hdslb.com/bfs/album/3c1b91f2a042eefc38b0f5ce9d69d43794a344cc.gif)

### Hystrix

> 熔断降级，防止服务雪崩。
>
> - 实现了超时机制和断路器模式
> - 用于隔离远程系统、服务或者第三方库，防止级联失败，从而提升系统的可用性与容错性。
> - 为系统提供保护机制。在依赖的服务出现高延迟或失败时，为系统提供保护和控制。
> - 防止雪崩。
> - 包裹请求：使用HystrixCommand（或HystrixObservableCommand）包裹对依赖的调用逻辑，每个命令在独立线程中运行。
> - 跳闸机制：当某服务失败率达到一定的阈值时，Hystrix可以自动跳闸，停止请求该服务一段时间。
> - 资源隔离：Hystrix为每个请求都的依赖都维护了一个小型线程池，如果该线程池已满，发往该依赖的请求就被立即拒绝，而不是排队等候，从而加速失败判定。防止级联失败。
> - 快速失败：Fail Fast。同时能快速恢复。侧重点是：（不去真正的请求服务，发生异常再返回），而是直接失败。
> - 监控：Hystrix可以实时监控运行指标和配置的变化，提供近实时的监控、报警、运维控制。
> - 回退机制：fallback，当请求失败、超时、被拒绝，或当断路器被打开时，执行回退逻辑。回退逻辑我们自定义，提供优雅的服务降级。
> - 自我修复：断路器打开一段时间后，会自动进入“半开”状态，可以进行打开，关闭，半开状态的转换。前面有介绍。

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

<img src="https://i0.hdslb.com/bfs/album/2ffc7c01adfab0c6830e9be30b837bc16f6f0468.png" alt="image-20220926012316920" style="zoom:400%;" />

> 当程序抛出异常时则会进入备用逻辑`getFallback`方法中，成功执行了备用方法里面的逻辑

<img src="https://i0.hdslb.com/bfs/album/cc171b4f67c506ebcaa8a0d419666c6da39b695d.png" alt="image-20220926012517392" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/07d35f3dbce7fe0a8a1809d3c57ca25f3853ce8d.png" alt="image-20220926020502535" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/7e2e6e9ad7afa92ed8d4117d2cb14631afad5c19.png" alt="image-20220926020407320" style="zoom:400%;" />

> 因此，我们需要去掉这个注解

<img src="https://i0.hdslb.com/bfs/album/28b587de4ebb30df2e4ef23f2a45dfe286d84047.png" alt="image-20220926020606456" style="zoom:400%;" />

> 我们调用一下`http://localhost:9080/testOpenFeign`接口
>
> - 可以看到确实走进了`FeignProviderBack`中的降级逻辑

<img src="https://i0.hdslb.com/bfs/album/c64acf8c0b10ec51314020618519a98c7557f553.png" alt="image-20220926021731825" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/c0f2c77981d64756d9ec80552e93960fcea5ae42.png" alt="image-20220926022802974" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/4065ad78c3f05d2cbc56462079fb08cc1e50a5b6.png" alt="image-20220926030209425" style="zoom:400%;" />

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

###### 监控线程池隔离开启dashboard

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
#开启所有Actuator Web访问端口
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

<img src="https://i0.hdslb.com/bfs/album/8d961f9e3ee11651cbc2ef9e8da1135aca92f382.gif" alt="soogif(3)(1)" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/30322704d5eb818bd24b04ab6df5a9f0ac975e6a.png" alt="image-20220926235806097" style="zoom:400%;" /> 

> 添加依赖

<img src="https://i0.hdslb.com/bfs/album/d08c8e978a8200adbfd9fff435e91013096e3b5f.png" alt="image-20220927000322346" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/42a7681b83eb8fa4b0b12b8638f48a5223cde3b8.png" alt="image-20220927032815307" style="zoom:400%;" />

> - 启动三个`Eureka-Server`
> - 启动三个`Feign-Provider`
> - 启动三个`Feign-Consumer`
> - 访问`http://localhost:7901/`查看服务列表是否所有启动的应用都注册到了`Eureka`

<img src="https://i0.hdslb.com/bfs/album/22a9db31c966485fdafbdede7fb23409c3410359.png" alt="image-20220927025606743" style="zoom:400%;" />

> 原来我们调用`Feign-Consumer`服务中的`/testOpenFeign`接口需要指定具体的`ip:port/api`才能匹配到对应URL，现在有了`Zuul`网关调用接口只需要`网关ip:port/serviceId/api`
>
> 📢注意：这里有一个`坑点`一不小心就会导致请求失败，返回`404 Not Found`
>
> - `zuul`会把你注册在注册中心的`serviceId`,例如我们这边的`FeignConsumer` 自动的转成小写去路由。所有我们需要把请求的http路径中`serviceId`转成小写如下

```http
localhost:8888/feignconsumer/testOpenFeign
```

> 可以看到成功从`ZuulServer`分发请求到下游`Feign-Consumer`服务，负载均衡也体现出来了

<img src="https://i0.hdslb.com/bfs/album/30ec7f0587435a85302c492b710d79bdc3d5591b.gif" alt="20220927_033255_edit" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/73c6810112bea26b566fe23b428e3bee2531450c.png" alt="image-20220927035058453" style="zoom:400%;" />

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

### SpringCloud Gateway

> - Gateway网关是一个服务，是访问内部系统的唯一入口，提供内部服务的路由中转
>
> - 还可以在此基础上提供如身份验证、监控、负载均衡、限流、降级与应用检测等功能。

#### 服务搭建

<img src="https://i0.hdslb.com/bfs/album/eaf8d60ba35f6ae7c52a772ca56beb30982de57e.png" alt="image-20221003175903336" style="zoom:200%;" />

> 勾选依赖

<img src="https://i0.hdslb.com/bfs/album/ddb8d6db07b2ba73c5ec1e71e421b32f670a56a0.png" alt="image-20221003190402933" style="zoom:200%;" />

> 我们这边`SpeingCloud`版本和`SpringBoot`版本选用`Hoxton.SR12 & 2.3.12.RELEASE`
>
> 修改`POM`文件中`spring-boot.version`和`spring-cloud.version`

```xml
<spring-boot.version>2.3.12.RELEASE</spring-boot.version>
<spring-cloud.version>Hoxton.SR12</spring-cloud.version>
```

#### 路由配置

##### 转发指定URI

> `SpringCloud GateWay`主要功能就是配置路由，那配置路由需要配置哪些配置项，怎么使用呢？一起来看看

###### **predicates**

> 断言，请求过来断定哪些请求可以匹配到uri上，可配多个

**Path**

> 路径断言，`- Path=/gateway/**`当匹配到`/gateway`这个路径的时候，不管后面是什么路径都转发到`uri`配置的地址
>
> - `- Path=`是固定格式，不能写成`- Path:`/`Path:`

```yaml 
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/gateway/**
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 在`Feign-Consumer`服务的`FeignConsumerController`类中新增以下测试接口

```java
@GetMapping("/gateway/testGateway")
public String testGateway() {
  return "success";
}
```

> 咱们启动服务测试一下配置了`Path`路径断言的效果
>
> 启动一个`Eureka-Server`、`Feign-Consumer`、`Gateway`这几个服务即可
>
> - 请求`http://localhost:6666/gateway/testGateway`接口

<img src="https://i0.hdslb.com/bfs/album/efc536eb10a5094e2172a2c504b740f557811545.png" alt="image-20221004233853693" style="zoom:200%;" />

> 如果把接口中`/gateway`去掉会出现什么情况

```java
@GetMapping("/testGateway")
public String testGateway() {
return "success";
}
```

> 可能是这样的

<img src="https://i0.hdslb.com/bfs/album/f55b415b62582c5d5531065368a505810b7cc493.png" alt="image-20221004234418810" style="zoom:200%;" />

> 也可能是这样的

<img src="https://i0.hdslb.com/bfs/album/8ef0c741edb889584d822d29c1c0b5c38ffc31d1.png" alt="image-20221004234455018" style="zoom:200%;" />

> 那怎么样才能不带前缀请求呢？
>
> - 配置`filters`
> - `StripPrefix=1`是去掉url路径中的一级前缀，也就是相当于把`http://localhost:6666/gateway/testGateway`中的`/gateway`给去掉了

```yaml
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/gateway/**
          filters:
            - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 重启`Gateway`服务再次调用`http://localhost:6666/gateway/testGateway`接口

<img src="https://i0.hdslb.com/bfs/album/5af8b8f86c2779f9bcd5bad2f0277fc942af08a0.png" alt="image-20221004235047859" style="zoom:200%;" />

**Query**

> 如果是查询请求，比如根据`name`查询用户信息的这种接口，那就要这样配了
>
> `- Query=name,rhys`如案例中【name,rhys】->【字段名，值】，这个值可以是具体的也可以写`正则匹配`

```yaml
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Query=name,rhys
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 在`Feign-Consumer`服务的`FeignConsumerController`类中新增以下测试接口

```java
@GetMapping("/testQueryPredicate")
public String testQueryPredicate(@RequestParam("name") String name) {
  return name;
}
```

> 这样配置的话那就要在具体的接口后面添加查询参数了

```http
http://localhost:6666/gateway/testGateway?name=rhys
```

<img src="https://i0.hdslb.com/bfs/album/755db360adc00176d74b36d85992765e7347b133.png" alt="image-20221005224617460" style="zoom:200%;" />

> 如果不传这个`name`字段，将是这样的

<img src="https://i0.hdslb.com/bfs/album/80db57911f45e61c605453842bc804766ed32b6a.png" alt="image-20221005224829728" style="zoom:200%;" />

> 如果我们字段值传的不是`rhys`,将是这样的

<img src="https://i0.hdslb.com/bfs/album/e60cc13bf7ab6c71357da2f7abbe44118f7b5fad.png" alt="image-20221005225015217" style="zoom:200%;" />

**Method**

> 也可以根据方法类型进行路由，这个断言配置决定了能够请求到`POST`接口还是`Get`接口

```yaml
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/gateway/**
            - Query=name,rhys
            - Method=get
          filters:
            - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 改完配置重启`Gateway`服务，再次调用`http://localhost:6666/gateway/testQueryPredicate?name=rhys`接口依然是可以请求通的

<img src="https://i0.hdslb.com/bfs/album/011d357adc575fcf7e8aa93290a488e919c049a1.png" alt="image-20221005225737344" style="zoom:200%;" />

> 如果我们将`- Method=get`配置项修改为`- Method=post`

```yaml
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/gateway/**
            - Query=name,rhys
            - Method=post
          filters:
            - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 在`Feign-Consumer`服务的`FeignConsumerController`类中新增以下测试接口

```java
@PostMapping("/testMethodPredicate")
public String testMethodPredicate(@RequestParam("name") String name) {
  return name;
}
```

> 重启`Feign-Consumer`和`Gateway`服务，调用`http://localhost:6666/gateway/testMethodPredicate?name=rhys`接口
>
> - 此时为`POST`请求的接口可以调用，为`Get`请求的接口则调不通了

<img src="https://i0.hdslb.com/bfs/album/05f3381c49be032a6c3e712647a551aa17582323.png" alt="image-20221005230736113" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/dc53c48289ab0ec071c3feb53e5a2cb734c778da.png" alt="image-20221005230850141" style="zoom:200%;" />

**Host**

> 也可以根据请求头中主机名（域名）断言来决定哪个域名的请求可以转发到对应目标地址
>
> - 适用于有多个网关各自拥有独立的域名，可以根据不同的域名进行不同服务的转发
>   - 网关1负责转发服务A相关的请求
>   - 网关2负责转发服务B相关的请求

```yaml
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/gateway/**
            - Query=name,rhys
            - Method=post
            - Host=www.rhys.vip
          filters:
            - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 请求脚本，该脚本可直接复制导入`Postman`

```http
curl --location --request POST 'http://localhost:6666/gateway/testMethodPredicate?name=rhys' \
--header 'Host: www.rhys.vip'
```

> 重启`Gateway`服务并再次调用`http://localhost:6666/gateway/testMethodPredicate?name=rhys`接口，别忘记在`请求头中设置Host`

<img src="https://i0.hdslb.com/bfs/album/1b6a3632afd52bdf2a7dd6c612246e5a6589da50.png" alt="image-20221005234824498" style="zoom:200%;" />

**Cookie**

> 也可以根据 Cookie进行断言判定，根据不同Cookie响应不同用户的请求

```yaml
server:
  port: 6666
# 应用名称
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      #可配多条路由规则
      routes:
        - id: rout1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/gateway/**
            - Query=name,rhys
            - Method=post
            - Host=www.rhys.vip
            - Cookie=name,rhys
          filters:
            - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: http://localhost:9080
```

> 重启`Gateway`服务并用`Postman`模拟一个`Cookie`信息

![20221006_000422_edit](https://i0.hdslb.com/bfs/album/b3b0fb72817057fb0019a18ca7df7ae3f2a401f4.gif)

<img src="https://i0.hdslb.com/bfs/album/c8ff2493dbbb398ffd6e0ff771767abe7c0ec5ec.png" alt="image-20221006000643128" style="zoom:200%;" />

> 如果`Cookie`中信息和断言配置对不上则请求无法转发到目标地址

<img src="https://i0.hdslb.com/bfs/album/0219df1ae1554c98b73d66c5ee8f66680d63a1dc.png" alt="image-20221006001152407" style="zoom:200%;" />

#### 负载均衡

> 集成Eureka后我们就不用手动指定具体的URI地址进行转发了，可以自动拉取注册服务列表，在根据规则配置`uri`则可以对具体的服务做负载均衡
>
> - 添加`Eureka Client`依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

> 添加&修改`Eureka`相关配置，这边我们修改一下之前随便配置的`id`、`Path`等配置项为贴近具体服务见名知意的值，当咱们用到项目里面肯定也不能随便配一下就完了，肯定是要根据一些具体的规则去配置
>
> - 读取配置时，以`lb://`开头的uri直接走负载均衡策略

```yaml
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      #可配多条路由规则
      routes:
        - id: feignConsumer
      #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/feignConsumer/**
            - Query=name,rhys
            - Method=post
            - Host=www.rhys.vip
            - Cookie=name,rhys
          filters:
          - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
#          uri: http://localhost:9080
          #走负载均衡策略
          uri: lb://FeignConsumer

eureka:
  client:
    service-url:
      #向eureka发起注册请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/
#      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/,http://RhysNi:123456@eureka2.com:7902/eureka/,http://RhysNi:123456@eureka3.com:7903/eureka/
  instance:
    #查找主机
    hostname: localhost
    instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}
```

> 为了更好的展示负载均衡生效了，我们在`/testMethodPredicate`接口上把端口号带上

```java
@Value("${server.port}")
private String port;

@PostMapping("/testMethodPredicate")
public String testMethodPredicate(@RequestParam("name") String name) {
  return name+" port:"+port;
}
```

> 停掉所有服务，重新启动`Eureka-Server`、`Feign-Consumer`（多实例）、`Gateway`服务，如下服务列表

<img src="https://i0.hdslb.com/bfs/album/4716db26662fe99ac5db02889307f2f5eb6c13df.png" alt="image-20221006035731059" style="zoom:200%;" />

> 调用以下接口（可直接复制导入`Postman`）

```http
curl --location --request POST 'http://localhost:6666/feignConsumer/testMethodPredicate?name=rhys' \
--header 'Host: www.rhys.vip' \
--header 'Cookie: name=rhys'
```

> 可以看到当配置了`uri=lb://xxx`的时候会自动对相关服务做负载均衡

<img src="https://i0.hdslb.com/bfs/album/25bc70af02d41b3e4df4cf0b83add7217ca9c980.gif" alt="20221006_034408_edit" style="zoom:200%;" />

> 当然这个负载均衡策略同样是支持自定义的，之前咱们在总结`Ribbon`的时候提到过怎么自定义负载均衡策略
>
> - 因为我们这边是在`Gateway`服务对`Feign-Consumer`服务做负载均衡，所以我们需要在`Gateway`服务中新增自定义负载均规则类

```java
public class CustomerRule extends AbstractLoadBalancerRule {
    @Override
    public Server choose(Object o) {
        List<Server> serverList = getLoadBalancer().getReachableServers();
        //TODO：实现符合项目需要的负载均衡算法
        return serverList.get(new Random().nextInt(serverList.size()));
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // TODO document why this method is empty
    }
}
```

> 在`Gateway`服务配置文件中指定对`FeignConsumer`服务做负载均衡的实现类

```yaml
FeignConsumer:
  ribbon:
    NFLoadBalancerRuleClassName: com.rhys.gateway.rule.CustomerRule
```

> 从Debug可以看到确实可以获取到某个节点，这里获取到了`9081`节点

<img src="https://i0.hdslb.com/bfs/album/192aea926577c846c925a35bd3d7ac5b3e22aefd.png" alt="image-20221006044342859" style="zoom:200%;" />

> 与接口返回的端口一致，说明自定义负载均衡生效了

<img src="https://i0.hdslb.com/bfs/album/baf31fc2e1c0cb00d41fce109e71773c2bc1952d.png" style="zoom: 200%;" />

#### 自定义路由规则

> 在`Gateway`服务中添加新的路由配置类
>
> **适用于项目中需要更复杂的路由规则去转发不同服务的时候**
>
> - 这边每一个Api其实对应的就是我们配置中的那些配置项
>
> - 我们目的是当调用`http://localhost:6666/jump`的时候自动跳转到百度首页

```java
@Component
public class CustomerRoute {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route(predicateSpec -> predicateSpec.path("/jump")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.stripPrefix(1))
                        .uri("http://www.baidu.com/"))
                .build();
    }
} 
```

> 当请求`http://localhost:6666/jump`时就会自动转发到百度首页

<img src="https://i0.hdslb.com/bfs/album/13d2614ea4864916c6e375f6cbfc1a296844ce74.png" alt="image-20221006050244104" style="zoom:200%;" />

#### 自定义过滤器

> 过滤器的使用场景非常多，例如权限认证、限流等操作都是在过滤器中进行
>
> - 自定义过滤器则需要新建过滤器类并实现`Ordered`接口对各个过滤器进行排序决定过滤的优先级，另外还要实现一个`GlobalFilter`接口实现`filter`方法
>
> - 我们现在想要对`http://localhost:6666/jump`接口进行条件过滤，让path为`/jump`的无法进行URL转发，返回一套自定义的信息

```java
@Component
public class CustomerFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        RequestPath path = exchange.getRequest().getPath();
        if ("/jump".equals(String.valueOf(path))) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().setRawStatusCode(HttpStatus.UNAUTHORIZED.value());
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap("被过滤器处理过了，跳转不了百度了".getBytes(StandardCharsets.UTF_8))));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```

> 重启`Gateway`服务，请求`http://localhost:6666/jump`接口

<img src="https://i0.hdslb.com/bfs/album/8140a7867770de3ef15435bb05899cd191b6a33f.png" alt="image-20221006055611447" style="zoom:200%;" />

> **总结一下**
>
> - 过滤器可以有`多个`，并且可以根据`order`设置优先级，`getOrder`返回的的值越小优先级越高
> - chain.filter(exchange); 可以把`exchange`传递给下一个优先级的过滤器

#### 权重/灰度发布

> 可以做灰度发布

<img src="https://i0.hdslb.com/bfs/album/f71517dc7695dbdf787c429a11f4a23347398b34.png" alt="image-20221006072053823" style="zoom:200%;" />

> 我们配置两套路由规则
>
> `Weight`可配置两个参数【分组，权重】
>
> - 我们这边把两组`Feign-Consumer`高可用服务分到`FCService`同一组中，做两层负载均衡，第一层根据权重算法在`FCService`组内进行随机
>   - 权重随机底层是一个数组，为我们这边配置的`FeignConsumer1`权重是`99%`而`FeignConsumer2`权重是`1%`，在请求过来的时候会随机生成一个数没然后去匹配数组中的权重区间
> - 第二层交由`Ribbon`在集群内做负载均衡

```yaml
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      #可配多条路由规则
      routes:
        - id: feignConsumer1
      		#请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/feignConsumer/**
            - Query=name,rhys
            - Method=post
            - Host=www.rhys.vip
            - Cookie=name,rhys
            - Weight=FCService,95
          filters:
          - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: lb://FeignConsumer1
        - id: feignConsumer2
      		#请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/feignConsumer/**
            - Query=name,rhys
            - Method=post
            - Host=www.rhys.vip
            - Cookie=name,rhys
            - Weight=FCService,5
          filters:
          - StripPrefix=1
          #请求过来路由匹配完成后转发的目标地址
          uri: lb://FeignConsumer2
```

> 修改`FeignConsumer`服务中的应用名为`FeignConsumer1`并启动三个节点作为高可用集群

```yaml
spring:
  application:
    name: FeignConsumer1
```

修改`FeignConsumer`服务中的应用名为`FeignConsumer2`再启动三个节点作为高可用集群

```yaml
spring:
  application:
    name: FeignConsumer2
```

> 停掉所有服务，重启启动如下图服务列表中服务
>
> - 单实例`Feign-Consumer`
> - 单实例`Gateway`
> - 三实例`FeignConsumer1`
> - 三实例`FeignConsumer2`

<img src="https://i0.hdslb.com/bfs/album/5fb349b824254acb7e223dd30bcd8cdaf6adc0d9.png" alt="image-20221006074456727" style="zoom:200%;" />

> 这边请求了大概45次，一共只出现2次属于`FeignConsumer2`服务的节点，45次的5%应该是2.25次，所以占比与配置相吻合，当然，数据不可能百分百刚好准确，肯定会有些许误差的，我这边也是一个大概值，想要更精准的数据需要用压力测试工具进行大请求量的测试才能得出更接近标准的值

![20221006_084105](https://i0.hdslb.com/bfs/album/17b64f2379cc0d12605c5d9942bd93c9e38f1ea6.gif)

#### 限流

##### 令牌桶 &Redis 限流

###### [Redis安装教程](https://blog.csdn.net/weixin_44977377/article/details/123343579?spm=1001.2014.3001.5501)

> 在`Gateway`服务中引入`Redis`依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

> 自定义执行限流算法的规则
>
> - 我们规定让参数为`name`的请求进入限流

```java
@Component
public class RateLimitConfig {
    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getQueryParams().getFirst("name")));
    }
}
```

>  在配置文件中添加下列配置
>
>  - 为了不影响接下来的操作，我们将之前配置的`灰度发布`相关的`- id: feignConsumer2`相关配置全部注释掉，只开启一个路由规则即可
>  - 将`Host`和`Cookie`断言注释掉
>  - 将`权重`配置注释掉，否则可能会有部分请求无返回
>
>  - `#{@keyResolver}`，可以在spring容器中拿到名为`keyResolver`的bean

```yaml
spring:
  application:
    name: GatewayServer
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      #可配多条路由规则
      routes:
        - id: feignConsumer1
          #请求过来断定哪些请求可以匹配到uri上，可配多个
          predicates:
            - Path=/feignConsumer/**
            - Query=name,rhys
            - Method=post
#            - Host=www.rhys.vip
#            - Cookie=name,rhys
#            - Weight=FCService,100
          filters:
          - StripPrefix=1
          - name: RequestRateLimiter
            args:
              #截取哪些规则进入限流算法
              key-resolver: "#{@keyResolver}"
              #发放令牌速度1个/s
              redis-rate-limiter.replenishRate: 1
              #总令牌数量
              redis-rate-limiter.burstCapacity: 5
          #请求过来路由匹配完成后转发的目标地址
#          uri: http://localhost:9080
          #读取配置时，以"lb://"开头的uri则直接走负载均衡策略
          uri: lb://FeignConsumer1
```

> 启动`Redis`并且重启`Gateway`服务
>
> - 我这里使用了`Jmeter`测试了一波，3s请求300次
> - 结果显示前5次符合配置中配的总令牌数量，用完后每秒生成一个新的令牌，只有拿到令牌的才请求成功，其余全部请求失败，这也说明我们限流成功

<img src="https://i0.hdslb.com/bfs/album/6c6328f23a68d6913566bc43c7e1bb03ce4ec4cd.png" alt="image-20221006110744076" style="zoom:200%;" />

##### GoogleGuava限流器

> 添加lombok依赖

```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
</dependency>
```

> 新建配置类
>
> - permitsPerSecond 每秒生成令牌数

```java
@Data
@Validated
public class Config {
    @DecimalMin("0.1")
    private Double permitsPerSecond;
}
```

> 自定义限流器
>
> - `@Primary`注解得加，标识当前`customerRateLimiter`为默认的Bean
> - 因为`RedisRateLimiter`也是实现了`AbstractRateLimiter`抽象类，所以会冲突，我们这边不用`RedisRateLimiter`

```java
@Primary
@Component
public class CustomerRateLimiter extends AbstractRateLimiter<Config> {

    /**
     * 每秒发一个令牌
     */
    private final RateLimiter limiter = RateLimiter.create(1);

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        Config config = getConfig().get(routeId);
        limiter.setRate(Objects.isNull(config.getPermitsPerSecond()) ? 1 : config.getPermitsPerSecond());
        boolean isAllow = limiter.tryAcquire();
        return Mono.just(new Response(isAllow, new HashMap<>()));
    }

    public CustomerRateLimiter() {
        super(Config.class, "default-rate-limit", new ConfigurationService());
    }
}
```

> 这里使用`Jmeter`2秒请求10次，两次调用成功测试限流成功

<img src="https://i0.hdslb.com/bfs/album/ecc701eba6bd100d945cf7ee09d7669591b2dfbc.png" alt="image-20221006114902856" style="zoom:200%;" />

### Sleuth

> 服务链路追踪
>
> - 跟踪每个请求，中间请求经过哪些微服务，请求耗时，网络延迟，业务逻辑耗时等。我们就能更好地分析系统瓶颈、解决系统问题。

#### 集成案例

> 在每个需要监控的服务中添加一下依赖，我们这里就在`Feign-Consumer`和`Feign-Provider`服务中添加相关依赖
>
> - sleuth是Spring cloud的分布式追踪解决方案
> - zipkin是twitter开源的分布式跟踪系统，收集系统的时序数据，从而追踪微服务架构中系统延时等问题，可以通过界面更加友好的展现给用户
>   - 收集系统的时序数据，从而追踪微服务架构中系统延时等问题
>   - 由Collector、Storage、Restful API、Web UI（采集器，存储器，接口，UI）四个部分组成
>   - sleuth 收集跟踪信息通过http请求发送给zipkin 服务，zipkin将跟踪信息存储（默认内存存储，可以用mysql，ES等存储）
>   - 提供RESTful API接口，zipkin ui通过调用api进行数据展示，

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
  	#微服务会收集信息上报到这个地址
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

<img src="https://i0.hdslb.com/bfs/album/d6528be608f62d30cacbcf24ba5465c80edd9ac7.png" alt="image-20220927193354990" style="zoom:400%;" />

> 然后再重启我们的`Feign-Consumer`和`Feign-Provider`服务调用`localhost:8888/api/v1/testFC/testOpenFeign`接口，这个接口是走的`Zuul`网关`别搞错了`
>
> - 会在被调用控制台看到多出以下信息出来，那这个一串信息代表什么呢？
> - 【服务名，traceId,spanId,是否向zipkin上报信息】

<img src="https://i0.hdslb.com/bfs/album/5cac05efc51e86543ef90853114f2d12b17ac5ef.png" alt="image-20220927162731497" style="zoom:400%;" />

> 调用完成后我们可以访问`Zipkin`来查看调用链路了

<img src="https://i0.hdslb.com/bfs/album/e3ea089b0cba0dfa0cb7cf3909f92b71f010cebd.png" alt="image-20220927194402983" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/715d49142baaf6bd19525e8f8ac94929035a245d.png" alt="image-20220927195211619" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/6e7f90d1149784ffdf3c49f9141176df8320e3ed.png" alt="image-20220927194531052" style="zoom:400%;" />

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

<img src="https://i0.hdslb.com/bfs/album/5b003450ea07e06635db20258dc8eaed6c3a6e58.png" alt="image-20220927195438917" style="zoom:400%;" />

### Spring Boot Admin

> 健康管理

#### 举个例子🌰

> 创建`Admin`工程

<img src="https://i0.hdslb.com/bfs/album/a84697d5e99288d7cadcedde06b9ad0d5fe2c918.png" alt="image-20220928105329659" style="zoom:400%;" />

> 选择`codecentric Spring Boot Admin`依赖

<img src="https://i0.hdslb.com/bfs/album/794f20d734808b17befbb23dba7c898829f8ffe2.png" alt="image-20220928110311680" style="zoom:400%;" />

> 除了上面的依赖我们还需要手动引一`Admin可视化页面`依赖

```xml
<!-- Admin可视化页面 -->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-server-ui</artifactId>
</dependency>
```

> 别忘记修改`POM`文件中`spring-boot.version`

```xml
<spring-boot.version>2.3.12.RELEASE</spring-boot.version>
```

> 在`Admin`工程的启动类添加`@EnableAdminServer`注解

```java
@EnableAdminServer
@SpringBootApplication
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
```

> 修改`Admin`的`application.yml`配置文件

```yaml
# 应用名称
spring:
  application:
    name: AdminServer
    
server:
  port: 9999
```

> 接下来到我们需要接入的微服务工程中添加以下依赖，还是在`Feign-Consumer`和`Feign-Provider`工程里操作，对了`Eureka-Server`也是要做同样的操作
>
> 还有一个`actuator`依赖在[监控线程池隔离](#开启dashboard)的那部分我们已经集成过了，如果没有集成的话可以添加如下依赖

```xml
<!-- Spring Boot Admin 客户端-->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.2.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.3.12.RELEASE</version>
</dependency>
```

> 添加了`actuator`依赖后还需要在`Feign-Consumer`、`Feign-Provider`、`Eureka-Server`的`application.yml`配置文件中添加以下配置
>
> 🔈注意：我们从工程创建开始后续添加的依赖都是`增量配置`，有些配置其实在前面实战中已经配置好了，需要注意新增依赖的`格式缩进`是否合法

```yaml
spring:
  boot:
    admin:
      client:
      	#Springboot Admin的信息上报地址
        url: http://localhost:9999

eureka:
  client:
    #开启健康检查
    healthcheck:
      enabled: true
      
#开启所有Actuator Web访问端口
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

> 重启/启动所有相关服务
>

<img src="https://i0.hdslb.com/bfs/album/4c85b5ac53d946d18a2283640ec8fb2f623c4b64.png" alt="image-20220928215036276" style="zoom:400%;" />

> 进入`http://localhost:9999`

<img src="https://i0.hdslb.com/bfs/album/ec4539017b12ec3fc77b331724ddead1b40345c5.png" alt="image-20220928215121348" style="zoom:400%;" />

> - 这里可以查看JVM、端点接口查看等，其实就是把我们最开始提到的那些[端点接口](#Api端点功能)的数据可视化了
> - 关于Admin可视化具体的功能可以自己挨个点了看看，研究一下

![20220928_232457_edit(1)](https://i0.hdslb.com/bfs/album/61b5e4ba8f4b19181aa7ed0e049a4d68a7d9a88d.gif)

#### 邮件告警

> 在`Admin`服务引入`mail`依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

> 在`Admin`服务的配置文件中添加以下关于邮箱的配置

```yaml
spring:
  application:
    name: AdminServer
  boot:
    admin:
      notify:
        mail:
          to: 1138596996@qq.com
          from: 1138596996@qq.com
          
# 邮件设置
mail:
	#发送协议
  host: smtp.qq.com
  username: 1138596996
  #授权码
  password: 看下方获取步骤
  properties:
    mail:
      smpt:
        auth: true
        starttls:
          enable: true
          required: true
```

> 点击`设置 -> 账户`

<img src="https://i0.hdslb.com/bfs/album/0187b24a9a72452979d0dec658187f33166785e8.png" alt="image-20220929012219702" style="zoom:400%;" />

> 找到`POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务`，开启`POP3/SMTP服务`
>
> - SMTP发信
> - POP3收信

<img src="https://i0.hdslb.com/bfs/album/56c0d21ba108771b83db4ee9b645f97e8ccb1b88.png" alt="image-20220929004400495" style="zoom:400%;" />

> 发送短信验证

<img src="https://i0.hdslb.com/bfs/album/94d63bc168bd5fb16c3f9e47f0cfc21258ec3102.png" alt="image-20220929004506256" style="zoom:400%;" />

> 验证成功会给一个授权码，把这个授权码配到配置中`password`配置项

<img src="https://i0.hdslb.com/bfs/album/cea82746c8638b5a02ef4be613b12df65f06801e.png" alt="image-20220929004720177" style="zoom:400%;" />

> 有好兄弟问：到这就结束了吗？
>
> - 是的，只要配置一下就可以了，这也是最简单的配置方法
> - 重启`Admin`服务，然后我们在服务列表中随便停掉一个/多个服务看看会发生什么
> - 我这里将`Feign-Provider`的其中一个节点停掉了

<img src="https://i0.hdslb.com/bfs/album/55680dc26748d906b261c441d6ca6a5cf937a77f.png" alt="image-20220929011628488" style="zoom:400%;" />

> 紧接着邮箱就会收到一封相关节点下线的告警通知

<img src="https://i0.hdslb.com/bfs/album/e3a72d65625bb72949b88c575bad73b42ff98593.png" alt="image-20220929023841870" style="zoom:400%;" />

#### [钉钉告警](https://open.dingtalk.com/document/group/custom-robot-access)

> 首先我们先操作钉钉获取一个`Token`

<img src="https://i0.hdslb.com/bfs/album/2fff13522650b9176954e51184e0b0117c1e97ae.png" alt="image-20220929015714981" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/1c90298e507dc815a4d3afb1fdf8640dc47b0b0b.png" alt="image-20220929015757492" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/c286d4b76bb7e47e819390395e7e170d6cf76188.png" alt="image-20220929015836095" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/7ab782320bee027647438f5497ad02f157199652.png" alt="image-20220929015907542" style="zoom:400%;" />

<img src="https://i0.hdslb.com/bfs/album/bcbb656e9ebb918b64be765f561a76a268c13f59.png" alt="image-20220929015954597" style="zoom:400%;" />

> `关键词`非常重要，可以记录一下，待会儿咱们要用到

<img src="https://i0.hdslb.com/bfs/album/7ad927cc51817bdd9c67f3d169505f971cd0f70e.png" alt="image-20220929020147716" style="zoom:400%;" />

> 复制该链接，自己保存好，下面流程要用到

<img src="https://i0.hdslb.com/bfs/album/401c08e8d8475b48ed763f7ab906882a04e3364f.png" alt="image-20220929020325295" style="zoom:400%;" />

> 新建`Message`和`Content`两个实体类

```java
@Data
@AllArgsConstructor
public class Content {
    private String content;
}
```

```java
@Data
@Builder
public class Message {
    private String msgtype;
    private Content text;
}
```

> 新建`DingDingMessageSender`发送类

```java
public class DingDingMessageSender {
    public static void sendTextMessage(String msg) {
        try {
            URL url = new URL("替换成刚才钉钉生成的链接");
            // 建立 http 连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
            conn.connect();

            OutputStream out = conn.getOutputStream();
            String textMessage = JSONObject.toJSONString(Message.builder().msgtype("text").text(new Content(msg)).build());
            byte[] textMessageBytes = textMessage.getBytes();
            out.write(textMessageBytes);
            out.flush();
            out.close();

            InputStream in = conn.getInputStream();
            byte[] data = new byte[in.available()];
            in.read(data);
            System.out.println(new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

> - 新建一个`DingDingNotifier `通知类继承`AbstractStatusChangeNotifier`
> - 实现`doNotify`方法
> - 代码中`系统服务告警`替换成你自定义的关键词，如没自定义则不需要改

```java
public class DingDingNotifier extends AbstractStatusChangeNotifier {

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        String serviceName = instance.getRegistration().getName();
        String serviceUrl = instance.getRegistration().getServiceUrl();
        String status = instance.getStatusInfo().getStatus();
        Map<String, Object> details = instance.getStatusInfo().getDetails();
        return Mono.fromRunnable(() -> DingDingMessageSender.sendTextMessage("系统服务告警 : 【" + serviceName + "】" + "【服务地址】" + serviceUrl + "【状态】" + status + "【详情】" + JSONObject.toJSONString(details)));
    }

    public DingDingNotifier(InstanceRepository repository) {
        super(repository);
    }
}
```

> 在`Admin`的启动类中将`DingDingNotifier`通知类声明为`Bean`交由`Spring`管理

```java
@EnableAdminServer
@SpringBootApplication
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public DingDingNotifier dingDingNotifier(InstanceRepository repository){
        return new DingDingNotifier(repository);
    }
}
```

> 重启`Admin`服务，再次操作服务下线，这时邮件和钉钉应该都可以收到服务下线告警了

<img src="https://i0.hdslb.com/bfs/album/9127995d7411073f19a76106bded269b0f851ee3.png" alt="image-20220929023122573" style="zoom:400%;" />

### Config

> - 单体应用，配置写在配置文件中，切换环境时可以切换不同的配置文件
>
> - 微服务中成百上千的服务比较多，配置很多，需要集中管理不同环境的配置，需要动态调整配置参数，更改配置不停服。

#### 配置中心组成

> 分布式配置中心包括3个部分：
>
> - git ：存放配置的地方，存放本地文件等。
>- config 服务端: 从git读取配置。
> - config客户端：是config服务端的客户端消费配置。

#### 配置中心原理

> - 首先得有一个`Config-Center`客户端，把所需要的配置文件传到`GitHub`上
> - `Config-Center`配置中心服务可以实时去`GitHub`拉取这些配置文件到配置中心本地
> - 我们所有的微服务都可以跟这个配置中心建立连接去拉取各个服务所需要的配置文件
> - 微服务怎么知道自己要用什么配置文件？
>   - 一种是保持服务名和配置文件名一致，就会自动根据服务名去配置中心本地找这个服务名对应的环境配置文件
>   - 第二种就是服务名和配置文件名不一致，这时就要通过`spring.cloud.config.name`配置项配置好你的配置文件名

<img src="https://i0.hdslb.com/bfs/album/1354d9d71a8212c96bee6d9b50ac78cb50715365.png" alt="image-20220929041458411" style="zoom:400%;" />

#### Gitee仓库搭建

> **推荐**使用这种，因为`GitHub`经常连接超时

<img src="https://i0.hdslb.com/bfs/album/5df1e7d9e2bc025cf31dc8bd4d954eb81243fa09.png" alt="image-20220930152015921" style="zoom:200%;" />

#### GitHub仓库搭建

<img src="https://i0.hdslb.com/bfs/album/dc3def30fa605f52149e188248ca9469840b19d4.png" alt="image-20220929025639343" style="zoom:400%;" />



> 拉取配置中心到本地IDEA

<img src="https://i0.hdslb.com/bfs/album/aa4e45057c126a51d381b05d3d9c40eacc0a05b7.png" alt="image-20220929025943771" style="zoom:400%;" />

> 贴入刚创建的`Gitee`仓库地址进行克隆

<img src="https://i0.hdslb.com/bfs/album/c91ff8edc44a72e24c0afc8f4694657f93bfa4e3.png" alt="image-20220930152530315" style="zoom:200%;" />

> 或者用`GitHub`

<img src="https://i0.hdslb.com/bfs/album/e01dd81126040357f29f9384760d53806ac6b367.png" alt="image-20220929030425911" style="zoom: 200%;" />

> 克隆完成在工程中添加`dev`、`test`、`uat`、`prod`四个配置文件

<img src="https://i0.hdslb.com/bfs/album/4ef503eedbcb5c9fc2fe6212ff2974693e51fbe7.png" alt="image-20220929033509002" style="zoom:400%;" />

> 提交到远程仓库

<img src="https://i0.hdslb.com/bfs/album/13d86de1fdf2168ea747f3c13b1c0ea0925cd616.png" style="zoom:400%;" />

> `Gitte`版

<img src="C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220930152622903.png" alt="image-20220930152622903" style="zoom:200%;" />

> `GitHub`版

<img src="https://i0.hdslb.com/bfs/album/c6034089a583507bb708b3844de92fe6437543dc.png" alt="image-20220929033920767" style="zoom:400%;" />



#### 配置中心服务端搭建

> 创建`Config-Center`工程

<img src="https://i0.hdslb.com/bfs/album/bda0352800cf1baf878599100bfc161c58d73b03.png" alt="image-20220929030703019" style="zoom:400%;" />

>  添加以下依赖

<img src="https://i0.hdslb.com/bfs/album/b91a6f66db5e1f7d689b8674f9e28137030be259.png" style="zoom:400%;" />

> 修改`pom`中`spring-boot.version`和`spring-cloud.version`

```xml
<spring-boot.version>2.3.12.RELEASE</spring-boot.version>
<spring-cloud.version>Hoxton.SR12</spring-cloud.version>
```

> `ConfigCenter`服务启动类添加`@EnableConfigServer`注解标识为配置中心服务

```java
@EnableConfigServer
@SpringBootApplication
public class ConfigCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterApplication.class, args);
    }
}
```

> 在`Config-Center`服务的`application.yml`配置文件中添加以下配置
>
> - `uri`根据你选用的仓库自行修改

```yaml
server:
  port: 7777

# 应用名称
spring:
  application:
    name: ConfigCenter
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/rhysni/config-center.git
          username: xxxxxx
          password: xxxxxx
      label: master

eureka:
  client:
    service-url:
      #向eureka发起注册请求
      defaultZone: http://RhysNi:123456@eureka1.com:7901/eureka/,http://RhysNi:123456@eureka2.com:7902/eureka/,http://RhysNi:123456@eureka3.com:7903/eureka/
  instance:
    #查找主机
    hostname: localhost
    instance-id: ${eureka.instance.hostname}:${spring.application.name}:${server.port}

# Actuator Web 访问端口
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

> 启动`Eureka-Server`和`Config-Center`服务测试能否读取到远程配置文件
>
> **获取配置规则**
>
> 默认`master`分支，不指定`lable`时自动去`master`分支前缀匹配对应配置文件
>
> - 根据前缀匹配
>   - `/文件名-环境标识.json`
>   - `/文件名-环境标识.properties`
>   - `/文件名-环境标识.yml`
> - 由于这个`lable`默认是`master`分支，但是我们现在如果是用`GitHub`创建的仓库，他的分支现在默认是`main`分支（有兴趣可以嗑一下[为什么GitHub取消`master`分支](https://blog.csdn.net/csdnsevenn/article/details/108898305)），所以目前只能通过下面这种`指定lable`的方式去访问，当然，你也可以自己建立一个`master`分支
>   - `/label(git仓库分支)/文件名-环境标识.yml`

```http
#Github版
http://localhost:7777/main/feign-consumer-dev.yml
#Gitee版 & Github版手动创建Master分支后也可以用这个url
http://localhost:7777/feign-consumer-dev.yml
```

> 经测试是可以正常读取到的

<img src="https://i0.hdslb.com/bfs/album/53ed83e1a80b7f74cc6dddf3660cafccb8596dd9.png" alt="image-20220930154726742" style="zoom:200%;" />

![image-20220930154656202](https://i0.hdslb.com/bfs/album/6e8bcaf93fe4431420ff5618b712f6af6be68b50.png)

> `GitHub`版

<img src="https://i0.hdslb.com/bfs/album/afe22b173f78c98a199afb370d45bfe64b362765.png" alt="image-20220929040738399" style="zoom:400%;" />

##### 按分支读取

> 有了上面的基础配置之后咱们对`Config-Center`仓库中的配置文件内容做些许修改
>
> - 到`GitHub`仓库再创建三个分支，分别是`dev`、`test`、`uat`

<img src="https://i0.hdslb.com/bfs/album/f9ab39dbcff133e030820c6e73723718bc991262.png" style="zoom:200%;" />

<img src="https://i0.hdslb.com/bfs/album/5b03ffb00d1cfb1086d596fd79519c4280d5f076.png" alt="image-20220929153440061" style="zoom:400%;" />

> 创建完成的话这样我们就有四个分支了，`main`、`dev`、`test`、`uat`

<img src="https://i0.hdslb.com/bfs/album/d73c64d515cdd1ec52f607ae80465525c389231b.png" alt="image-20220930150409292" style="zoom:200%;" />

> `GitHub`版

<img src="https://i0.hdslb.com/bfs/album/2e91c67889b1b5a32b66aaeeb68edf66a3a54c0b.png" alt="image-20220929153625881" style="zoom:400%;" />

> **分支创建好将每个环境对应的配置文件分别放入对应的分支**
>
> - 如下图这样，依次操作

![image-20220930155201272](https://i0.hdslb.com/bfs/album/01019c7f27fc1872c02c0bc7a80cfc706da8aa10.png)

> `GitHub`版本也是如此依次操作

<img src="https://i0.hdslb.com/bfs/album/4861071ac01f1159664218d90c7b23060c7a9d9d.png" alt="image-20220930160621227" style="zoom:200%;" />

> 访问以下四个链接看看效果

```http
http://localhost:7777/master/feign-consumer-prod.yml
http://localhost:7777/test/feign-consumer-test.yml
http://localhost:7777/dev/feign-consumer-dev.yml
http://localhost:7777/uat/feign-consumer-uat.yml
#Github版，Gitee版可忽略
http://localhost:7777/main/feign-consumer-prod.yml
```

> 有可能会由于网络不好导致下图中`404`问题，多试几次即可，出不来也没关系，就是看个效果，没什么影响

<img src="https://i0.hdslb.com/bfs/album/990214617532e1ef9e0a84029bf1536874b7e8b3.png" style="zoom:400%;" />

> 最后出来就是这个效果了

<img src="https://i0.hdslb.com/bfs/album/1b54962a5fb83c99f9d69f2c46befcd598299dd6.png" alt="image-20220929160311964" style="zoom:400%;" />

#### 配置中心客户端应用

> 配置中心服务端已经搭建完成并且测试过可以根据不同的`lable`去读取不同环境下的配置文件了，那么现在就要在服务中应用上
>
> - 首先在`Feign-Consumer`服务中添加相关依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>
```

> 有个问题，就是关于Config客户端必须将配置文件更名为`bootstrap.xxx`,不然启动不了，我充满疑惑，众里寻他千百度：
>
> - 因为bootstrap.xxx的加载是先于application.xxx的
>
> - 因为这是Config客户端的硬性约定，别问，问就必须这样😳
> - 所以不管怎么样，我们要将`application.yml`更名为`bootstrap.yml`，感兴的兄弟可以先不改，等配置完成自己启动一下试试看看会发生什么，试完了别忘记回来把改一下🔈。。。
> - 再配置文件中添加以下配置

```yaml
spring:
  cloud:
    config:
      #通过URL方式查找配置中心
      uri: http://localhost:7777/
      profile: dev
      label: dev
      #文件名
      name: feign-consumer
```

<img src="https://i0.hdslb.com/bfs/album/c9387dc165a4f328d616a1036aba57f9a59c40f8.png" alt="image-20220929203353939" style="zoom:400%;" />

> 在`Feign-Consumer`服务的`FeignConsumerController`中添加以下代码
>
> - 读取远程配置文件中的`ROSTemplateFormatVersion`属性
> - 添加`/getRemoteConfig`接口

```java
@Value("${ROSTemplateFormatVersion}")
private String version;

@GetMapping("/getRemoteConfig")
public Object getRemoteConfig() {
    return version;
}
```

> 启动`Feign-Consumer`服务
>
> 友情提醒🔈:如果你在这之前把`注册中心`、`配置中心服务端`关了记得`重新启动服务`
>
> - 调用`http://localhost:9080/getRemoteConfig`接口

<img src="https://i0.hdslb.com/bfs/album/f8d0250fa7c9e3390bd1719660d3ad114d94f4ce.png" alt="image-20220929204644334" style="zoom:400%;" />

#### 配置中心高可用

> 高可用场景下再根据具体的ip:port去找就不行了，万一我那台服务挂了就读取不到其他机器中的配置文件了
>
> - 我们可以通过注册中心去找服务，在`bootstrap.yml`中修改`uri: http://localhost:7777/`为以下配置

```yaml
spring:
  cloud:
    config:
      #通过URL方式查找配置中心
#      uri: http://localhost:7777/
      #通过注册中心查找
      discovery:
        enabled: true
        service-id: ConfigCenter
      profile: dev
      label: dev
      #文件名
      name: feign-consumer
```

> 那我们`Config-Center`服务是不是也该起多实例了，我们起2-3个实例试试效果吧
>
> - 记得同时重启`Feign-Consumer`服务，因为我们刚改过配置了
>
> 多实例怎么启动还记得吧😳，忘记的[看这里](#idea)

<img src="https://i0.hdslb.com/bfs/album/b8c773e3544b1b15bd2df8aeadb4a3763170f491.png" alt="image-20220929211113312" style="zoom:400%;" />

再次调用`http://localhost:9080/getRemoteConfig`接口，依然是可以获取到配置数据的，说明从Eureka获取`Config-Server`服务是成功的

<img src="https://i0.hdslb.com/bfs/album/008ac91a3255512140da83f36d080c47d5dec833.png" alt="image-20220929211640067" style="zoom:400%;" />

#### 配置刷新

##### 针对服务更新配置

> 当我们配置文件内容被修改了，那么多微服务不可能全部重启一遍，那怎么让服务在不重启的前提下重新加载配置文件呢？
>
> - 在我们读取配置文件内容的类中添加`@RefreshScope`注解

```java
@RefreshScope
@RestController
public class FeignConsumerController {

    @Value("${ROSTemplateFormatVersion}")
    private String version;


    @GetMapping("/getRemoteConfig")
    public Object getRemoteConfig() {
        return version;
    }
}
```

> 需要有以下依赖和配置,因为我们之前都已经配置过了，这边了解一下就可以了

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
#开启所有Actuator Web访问端口
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

> 这时候需要重启一下`Feign-Consumer`服务，然后到修改`feign-consumer-dev`配置文件内容并提交到远程仓库

<img src="https://i0.hdslb.com/bfs/album/ef434a25e5ffa7da13128df4f95581152bcba0ca.png" style="zoom:400%;" />

###### 单体服务刷新配置

> 这个时候不会自动刷新配置，需要我们自己调用一下`http://localhost:9080/actuator/refresh`接口进行刷新

![image-20220929213326913](https://i0.hdslb.com/bfs/album/51b24ca9415eafe70a62ecc723f2100f55f4107b.png)

> 这是个`POST`请求

<img src="https://i0.hdslb.com/bfs/album/4c74a797f21120e1f541c69c98df093ce9148b2d.png" alt="image-20220929213702230" style="zoom:400%;" />

> 这时再请求`http://localhost:9080/getRemoteConfig`就会发现配置数据刷新成功，变成我们刚才修改的数据了

<img src="https://i0.hdslb.com/bfs/album/94f9c8dadb42838a9bed22a37fc01bf2e822e5d2.png" alt="image-20220929213900586" style="zoom:400%;" />

###### 高可用服务刷新配置

> 如果我们`Feign-Consuemr`起了多个实例，这样操作每次只能刷新一个，那怎么才能全部刷新呢？
>
> - 我们需要接入一个`企业服务消息总线`，简称`BUS`
> - `BUS`需要基于`amqp`协议接入`RabbitMQ`进行消息集成

**[Windows10安装Erlang和RabbitMQ](https://blog.csdn.net/weixin_44232093/article/details/124967852)**

**MacOS安装[HomeBrew](https://blog.csdn.net/m0_37781271/article/details/122549949)&[RabbitMQ](https://blog.csdn.net/m0_55613022/article/details/124295120)**

> 在`Feign-Consumer`服务添加`bus-amqp`依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

> 在`Feign-Consumer`服务添加RabbitMQ配置

```yaml
spring:
  #RabbitMQ
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

> 咱们将原来的`Feign-Consumer`停掉重启多个实例，服务启动列表如下图
>
> - 不出意外的话第一次启动会自动在rabbitmq中创建类似于`springCloudBus.anonymous.bcv5EJJJQaixOKdEUTXSXA`命名规则的队列

<img src="https://i0.hdslb.com/bfs/album/64105a22915bcc24c64ab8b6c8b20a509932e474.png" alt="image-20220929235457153" style="zoom:200%;" />

> 当添加了`bus`相关依赖后，`http://localhost:9080/actuator/`中会多出一个`http://localhost:9080/actuator/bus-refresh`端点

<img src="https://i0.hdslb.com/bfs/album/a587bc8f9a000084610809b19bdfe6c2af040400.png" alt="image-20220929225015059" style="zoom:400%;" />

> 对了，这时在`RabbitMQ`中应该也可以看到自动创建了名为`springCloudBus`的交换机，和用于刷新配置的三个队列

<img src="https://i0.hdslb.com/bfs/album/02951517c48436d0caab60c6a2a66f40e790c619.png" alt="image-20220930172052015" />

<img src="https://i0.hdslb.com/bfs/album/dae894db453317db58842ee1b2e27fb026277fd0.png" alt="image-20221001215457718" style="zoom:200%;" />

> 确认当前配置值为`dev-2015-09-01 update`

<img src="https://i0.hdslb.com/bfs/album/db81d728940d5436a570f2903cda8c58dcb9978b.png" alt="image-20220930165240575" style="zoom:200%;" />

> 修改配置文件的值为`dev-2015-09-01 BUS-Refresh`并提交到远程仓

<img src="https://i0.hdslb.com/bfs/album/587ddd5513d18d51d9187918fb8a203503ed3957.png" alt="image-20220930165315636" style="zoom:200%;" />

> - 调用`http://localhost:9080/actuator/bus-refresh`端点进行刷新
> - 重新调用三个`Feign-Consuemr`节点的`/getRemoteConfig`接口
>
> 注意🔈：如果有好兄弟是用`GitHub`搭建的配置中心仓库，那这边很有可能会因为网络原因，在刷新配置文件重新拉取的时候出现某个节点没有拉取成功的问题，导致最终`某些节点配置刷新成功，某些节点配置没有刷新`

![soogif](https://i0.hdslb.com/bfs/album/97eff27a16dfd25bef27e0aa8ec0df39e0410f56.gif)

###### 整体服务器刷新配置

> 就是把所有节点的配置文件全部刷新了，这个就应该在我们`Config-Center`服务中进行配置了
>
> 同样的在`Config-Center`服务中引入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 添加配置

```yaml
# 应用名称
spring:
  application:
    name: ConfigCenter
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/rhysni/config-center.git
      label: dev
  #RabbitMQ
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

# Actuator Web 访问端口
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

> 重启`Config-Center`高可用多实例
>
> - 修改配置文件值为`dev-2015-09-01 BUS-Refresh By ConfigCenter`，并`提交到远程仓库`
>- 调用`http://localhost:7777/actuator/bus-refresh`端点刷新所有服务器配置

<img src="https://i0.hdslb.com/bfs/album/ded2680e8bc896f774f700d35fa8a56f8760e9f1.gif" alt="20221001_222024" style="zoom:200%;" />



 
