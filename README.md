# NULS-SDK-4J使用说明

`NULS-SDK-4J`是给用JAVA对接`NULS-API`模块的合作伙伴提供的开发组件。

## maven

此工程已上传到maven中央仓库，可在maven工程中使用：

JDK11的版本依赖：
```xml
<dependency>
    <groupId>io.nuls.v2</groupId>
    <artifactId>sdk4j</artifactId>
    <version>1.0.0-beta6</version>
</dependency>
```

JDK8的版本依赖：
```xml
<dependency>
    <groupId>io.nuls.v2</groupId>
    <artifactId>sdk4j-jdk8</artifactId>
    <version>1.0.0-beta6</version>
</dependency>
```

注：调用`NULS-SDK-4J`提供的在线接口时，必须先运行`NULS2.0`的`NULS-API`模块。[点击进入NULS-API文档](https://github.com/nuls-io/nuls-v2/blob/release/module/nuls-api/README.md)

## 初始化

`NULS-SDK-4J`正常使用之前，需要先初始化，提供当前对接链的链ID和钱包NULS-API模块的url访问地址。SDK提供了三种初始化接口：

NulsSDKBootStrap.init(chianId, httpUrl);     //NULS-SDK工具根据chainId和Url初始化

NulsSDKBootStrap.initMain(httpUrl);           //NULS-SDK工具连接NULS主网钱包初始化

NulsSDKBootStrap.initTest(httpUrl);            //NULS-SDK工具连接NULS测试网钱包初始化

```
//示例
public void initialNulsSDK() {
    // 模块NULS-SDK-Provider服务的IP和Port
    String sdkProviderUrl = "http://127.0.0.1:9898/";
    NulsSDKBootStrap.initMain(sdkProviderUrl);
}
```

## API接口文档

我们提供了包含离线、在线的接口的文档

[点击进入接口文档](https://github.com/nuls-io/nuls-v2-sdk4j/blob/master/documents/NULS-V2-SDK4J.md)
