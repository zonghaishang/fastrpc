
针对tree树形存储注册中心(类似zookeeper、sofa registry)数据结构：

```
key => app@protocol
value => 
ip:port?app=xxx&timout=3000&warmup=30000&zip=xx&payload=encoded_string
// zip压缩扩展名称, none不压缩；其他留给未来扩展
// payload接口级别数据，采用竖线分隔，包含应用需要发布的接口数据
// 固定interface、uniqueId作为key, 其余参数可扩展，比如方法级别超时method.timeout
interface=,uniqueId=,method.timout=2000,_type=rpc|interface=,uniqueId=,method.timout=2000
```

订阅key，会推送对应value的全量集合。

针对key value键值对存储注册中心(类似etcd3)数据接口：

```
prefix key => app/protocol/
full key => app/protocol/ip:port?app=xxx&timout=3000&warmup=30000&zip=xx&payload=encoded_string
```

为了进一步简化注册中心压力，消费方元数据(比如消费哪个接口，消费者app、消费者ip)等信息，应该剥离到元数据中心。
另外类似rpc元数据(接口包含的方法、方法参数)也应该存储到元数据中心。

基于以下几点考虑这种结构设计：
- 可能存在重复的接口，但是可以通过zip算法解决掉
- 避免把服务rpc核心数据抽离，简化应用程序设计，只需要与注册中心交互一次就能拿到完整provider数据