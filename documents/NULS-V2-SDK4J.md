# NULS-SDK-4J使用说明

`NULS-SDK-4J`是给用JAVA对接`NULS-API`模块的合作伙伴提供的开发组件。

## maven

此工程已上传到maven中央仓库，可在maven工程中使用：

JDK11的版本依赖：
```xml
<dependency>
    <groupId>io.nuls.v2</groupId>
    <artifactId>sdk4j</artifactId>
    <version>1.0.6.RELEASE</version>
</dependency>
```

JDK8的版本依赖：
```xml
<dependency>
    <groupId>io.nuls.v2</groupId>
    <artifactId>sdk4j-jdk8</artifactId>
    <version>1.0.6.RELEASE</version>
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
    // 模块NULS-API服务的IP和Port
    String nulsApiUrl = "http://127.0.0.1:18004/";
    NulsSDKBootStrap.initMain(nulsApiUrl);
}
```

## API接口文档

我们提供了包含离线、在线的接口的文档

[点击进入接口文档](https://github.com/nuls-io/nuls-v2-sdk4j/blob/master/documents/NULS-V2-SDK4J.md)

0.1 获取本链相关信息,其中共识资产为本链创建共识节点交易和创建委托共识交易时，需要用到的资产
================================================
Method: NulsSDKTool#getInfo
---------------------------
_**详细描述: 获取本链相关信息,其中共识资产为本链创建共识节点交易和创建委托共识交易时，需要用到的资产**_

参数列表
----
无参数

返回值
---
| 字段名             |  字段类型  | 参数描述         |
| --------------- |:------:| ------------ |
| chainId         | string | 本链的ID        |
| assetId         | string | 本链默认主资产的ID   |
| inflationAmount | string | 本链默认主资产的初始数量 |
| agentChainId    | string | 本链共识资产的链ID   |
| agentAssetId    | string | 本链共识资产的ID    |

1.1 批量创建账户
==========
Method: NulsSDKTool#createAccount
---------------------------------
_**详细描述: 创建的账户存在于本地钱包内**_

参数列表
----
| 参数名      |  参数类型  | 参数描述 | 是否必填 |
| -------- |:------:| ---- |:----:|
| count    |  int   | 创建数量 |  是   |
| password | string | 密码   |  是   |

返回值
---
| 字段名 |      字段类型       | 参数描述     |
| --- |:---------------:| -------- |
| 返回值 | list&lt;string> | 返回账户地址集合 |

1.2 修改账户密码
==========
Method: NulsSDKTool#resetPassword
---------------------------------
_**详细描述: 修改账户密码**_

参数列表
----
| 参数名         |  参数类型  | 参数描述 | 是否必填 |
| ----------- |:------:| ---- |:----:|
| address     | string | 账户地址 |  是   |
| oldPassword | string | 原密码  |  是   |
| newPassword | string | 新密码  |  是   |

返回值
---
| 字段名   |  字段类型   | 参数描述   |
| ----- |:-------:| ------ |
| value | boolean | 是否修改成功 |

1.3 导出账户私钥
==========
Method: NulsSDKTool#getPriKey
-----------------------------
_**详细描述: 只能导出本地钱包已存在账户的私钥**_

参数列表
----
| 参数名      |  参数类型  | 参数描述 | 是否必填 |
| -------- |:------:| ---- |:----:|
| address  | string | 账户地址 |  是   |
| password | string | 密码   |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述 |
| ----- |:------:| ---- |
| value | string | 私钥   |

1.4 根据私钥导入账户
============
Method: NulsSDKTool#importPriKey
--------------------------------
_**详细描述: 导入私钥时，需要输入密码给明文私钥加密**_

参数列表
----
| 参数名      |  参数类型  | 参数描述   | 是否必填 |
| -------- |:------:| ------ |:----:|
| priKey   | string | 账户明文私钥 |  是   |
| password | string | 密码     |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述 |
| ----- |:------:| ---- |
| value | string | 账户地址 |

1.5 根据keystore导入账户
==================
Method: NulsSDKTool#importKeystore
----------------------------------
_**详细描述: 根据keystore导入账户**_

参数列表
----
| 参数名             |  参数类型  | 参数描述   | 是否必填 |
| --------------- |:------:| ------ |:----:|
| address         | string | 账户地址   |  是   |
| pubKey          | string | 公钥     |  是   |
| encryptedPriKey | string | 加密后的私钥 |  是   |
| password        | string | 密码     |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述 |
| ----- |:------:| ---- |
| value | string | 账户地址 |

1.6 导出keystore到指定文件目录
=====================
Method: NulsSDKTool#exportKeyStore
----------------------------------
_**详细描述: 导出keystore到指定文件目录**_

参数列表
----
| 参数名      |  参数类型  | 参数描述 | 是否必填 |
| -------- |:------:| ---- |:----:|
| address  | string | 账户地址 |  是   |
| password | string | 密码   |  是   |
| filePath | string | 文件目录 |  是   |

返回值
---
| 字段名  |  字段类型  | 参数描述    |
| ---- |:------:| ------- |
| path | string | 导出的文件路径 |

1.7 查询账户余额
==========
Method: NulsSDKTool#getAccountBalance
-------------------------------------
_**详细描述: 根据资产链ID和资产ID，查询本链账户对应资产的余额与nonce值**_

参数列表
----
| 参数名      |  参数类型  | 参数描述   | 是否必填 |
| -------- |:------:| ------ |:----:|
| address  | string | 账户地址   |  是   |
| chainId  |  int   | 资产的链ID |  是   |
| assetsId |  int   | 资产ID   |  是   |

返回值
---
| 字段名           |  字段类型  | 参数描述                      |
| ------------- |:------:| ------------------------- |
| total         | string | 总余额                       |
| freeze        | string | 锁定金额                      |
| available     | string | 可用余额                      |
| timeLock      | string | 时间锁定金额                    |
| consensusLock | string |  共识锁定金额                   |
| nonce         | string | 账户资产nonce值                |
| nonceType     |  int   | 1：已确认的nonce值,0：未确认的nonce值 |

1.8 设置账户别名
==========
Method: NulsSDKTool#setAlias
----------------------------
_**详细描述: 别名格式为1-20位小写字母和数字的组合，设置别名会销毁1个NULS**_

参数列表
----
| 参数名      |  参数类型  | 参数描述 | 是否必填 |
| -------- |:------:| ---- |:----:|
| address  | string | 账户地址 |  是   |
| alias    | string | 别名   |  是   |
| password | string | 账户密码 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述        |
| ----- |:------:| ----------- |
| value | string | 设置别名交易的hash |

1.9 验证地址格式是否正确
==============
Method: NulsSDKTool#validateAddress
-----------------------------------
_**详细描述: 验证本链地址格式是否正确**_

参数列表
----
| 参数名     |  参数类型  | 参数描述 | 是否必填 |
| ------- |:------:| ---- |:----:|
| address | string | 账户地址 |  是   |

返回值
---
| 字段名 | 字段类型 | 参数描述 || --- |:----:| ---- |


1.10 验证地址格式是否正确
===============
Method: NulsSDKTool#validateAddress
-----------------------------------
_**详细描述: 根据chainId验证地址格式是否正确**_

参数列表
----
| 参数名     |  参数类型  | 参数描述 | 是否必填 |
| ------- |:------:| ---- |:----:|
| chainId |  int   | 链ID  |  是   |
| address | string | 账户地址 |  是   |

返回值
---
| 字段名 | 字段类型 | 参数描述 || --- |:----:| ---- |


1.11 离线 - 批量创建账户
================
Method: NulsSDKTool#createOffLineAccount
----------------------------------------
_**详细描述: 创建的账户不会保存到钱包中,接口直接返回账户的keystore信息**_

参数列表
----
| 参数名      |  参数类型  | 参数描述 | 是否必填 |
| -------- |:------:| ---- |:----:|
| count    |  int   | 创建数量 |  是   |
| password | string | 密码   |  是   |

返回值
---
| 字段名                 |  字段类型  | 参数描述   |
| ------------------- |:------:| ------ |
| address             | string | 账户地址   |
| pubKey              | string | 公钥     |
| prikey              | string | 明文私钥   |
| encryptedPrivateKey | string | 加密后的私钥 |

1.12 离线 - 批量创建地址带固定前缀的账户
========================
Method: NulsSDKTool#createOffLineAccount
----------------------------------------
_**详细描述: 创建的账户不会保存到钱包中,接口直接返回账户的keystore信息**_

参数列表
----
| 参数名      |  参数类型  | 参数描述 | 是否必填 |
| -------- |:------:| ---- |:----:|
| chainId  |  int   | 链ID     |  是   |
| count    |  int   | 创建数量 |  是   |
| prefix   | string | 地址前缀 |  否   |
| password | string | 密码   |  是   |

返回值
---
| 字段名                 |  字段类型  | 参数描述   |
| ------------------- |:------:| ------ |
| address             | string | 账户地址   |
| pubKey              | string | 公钥     |
| prikey              | string | 明文私钥   |
| encryptedPrivateKey | string | 加密后的私钥 |

1.13 离线修改账户密码
=============
Method: NulsSDKTool#resetPasswordOffline
----------------------------------------
_**详细描述: 离线修改账户密码**_

参数列表
----
| 参数名             |  参数类型  | 参数描述   | 是否必填 |
| --------------- |:------:| ------ |:----:|
| address         | string | 账户地址   |  是   |
| encryptedPriKey | string | 加密后的私钥 |  是   |
| oldPassword     | string | 原密码    |  是   |
| newPassword     | string | 新密码    |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述       |
| ----- |:------:| ---------- |
| value | string | 重置密码后的加密私钥 |

1.14 离线获取账户明文私钥
===============
Method: NulsSDKTool#getPriKeyOffline
------------------------------------
_**详细描述: 离线获取账户明文私钥**_

参数列表
----
| 参数名             |  参数类型  | 参数描述   | 是否必填 |
| --------------- |:------:| ------ |:----:|
| address         | string | 账户地址   |  是   |
| encryptedPriKey | string | 加密后的私钥 |  是   |
| password        | string | 密码     |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述 |
| ----- |:------:| ---- |
| value | string | 明文私钥 |

1.15 多账户摘要签名
============
Method: NulsSDKTool#sign
------------------------
_**详细描述: 用于签名离线组装的多账户转账交易，调用接口时，参数可以传地址和私钥，或者传地址和加密私钥和加密密码**_

参数列表
----
| 参数名                                                                 |  参数类型   | 参数描述         | 是否必填 |
| ------------------------------------------------------------------- |:-------:| ------------ |:----:|
| signDtoList                                                         | signdto | 摘要签名表单       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address             | string  | 地址           |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;priKey              | string  | 明文私钥         |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;encryptedPrivateKey | string  | 加密私钥         |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password            | string  | 密码           |  否   |
| txHex                                                               | string  | 交易序列化16进制字符串 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述          |
| ----- |:------:| ------------- |
| hash  | string | 交易hash        |
| txHex | string | 签名后的交易16进制字符串 |

1.16 多签账户摘要签名
=============
Method: NulsSDKTool#multiSign
-----------------------------
_**详细描述: 用于签名离线组装的多签账户转账交易，每次调用接口时，只能传入一个账户的私钥进行签名，签名成功后返回的交易字符串再交给第二个账户签名，依次类推**_

参数列表
----
| 参数名                                                                 |  参数类型   | 参数描述         | 是否必填 |
| ------------------------------------------------------------------- |:-------:| ------------ |:----:|
| signDto                                                             | signdto | 摘要签名表单       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address             | string  | 地址           |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;priKey              | string  | 明文私钥         |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;encryptedPrivateKey | string  | 加密私钥         |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password            | string  | 密码           |  否   |
| txHex                                                               | string  | 交易序列化16进制字符串 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述          |
| ----- |:------:| ------------- |
| hash  | string | 交易hash        |
| txHex | string | 签名后的交易16进制字符串 |

1.17 明文私钥摘要签名
=============
Method: NulsSDKTool#sign
------------------------
_**详细描述: 明文私钥摘要签名**_

参数列表
----
| 参数名        |  参数类型  | 参数描述         | 是否必填 |
| ---------- |:------:| ------------ |:----:|
| txHex      | string | 交易序列化16进制字符串 |  是   |
| address    | string | 账户地址         |  是   |
| privateKey | string | 账户明文私钥       |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述          |
| ----- |:------:| ------------- |
| hash  | string | 交易hash        |
| txHex | string | 签名后的交易16进制字符串 |

1.18 密文私钥摘要签名
=============
Method: NulsSDKTool#sign
------------------------
_**详细描述: 密文私钥摘要签名**_

参数列表
----
| 参数名                 |  参数类型  | 参数描述         | 是否必填 |
| ------------------- |:------:| ------------ |:----:|
| txHex               | string | 交易序列化16进制字符串 |  是   |
| address             | string | 账户地址         |  是   |
| encryptedPrivateKey | string | 账户密文私钥       |  是   |
| password            | string | 密码           |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述          |
| ----- |:------:| ------------- |
| hash  | string | 交易hash        |
| txHex | string | 签名后的交易16进制字符串 |

1.19 创建多签账户
===========
Method: NulsSDKTool#createMultiSignAccount
------------------------------------------
_**详细描述: 根据多个账户的公钥创建多签账户，minSigns为多签账户创建交易时需要的最小签名数**_

参数列表
----
| 参数名                                                     |      参数类型       | 参数描述   | 是否必填 |
| ------------------------------------------------------- |:---------------:| ------ |:----:|
| pubKeys                                                 |      list       | 账户公钥集合 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeys | list&lt;string> | 账户公钥集合 |  是   |
| minSigns                                                |       int       | 最小签名数  |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述  |
| ----- |:------:| ----- |
| value | string | 账户的地址 |

1.20 根据私钥获取地址
=============
Method: NulsSDKTool#getAddressByPriKey
--------------------------------------
_**详细描述: 根据传入的私钥，生成对应的地址，私钥不会存储在钱包里**_

参数列表
----
| 参数名    |  参数类型  | 参数描述 | 是否必填 |
| ------ |:------:| ---- |:----:|
| priKey | string | 原始私钥 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述  |
| ----- |:------:| ----- |
| value | string | 账户的地址 |

1.21 转换NULS1.0地址为NULS2.0地址
==========================
Method: NulsSDKTool#changeV1addressToV2address
----------------------------------------------
_**详细描述: 转换NULS1.0地址为NULS2.0地址**_

参数列表
----
| 参数名       |  参数类型  | 参数描述        | 是否必填 |
| --------- |:------:| ----------- |:----:|
| v1Address | string | NULS1.0账户地址 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述      |
| ----- |:------:| --------- |
| value | string | NULS2.0地址 |

2.1 根据区块高度查询区块头
===============
Method: NulsSDKTool#getBlockHeader
----------------------------------
_**详细描述: 根据区块高度查询区块头**_

参数列表
----
| 参数名    | 参数类型 | 参数描述 | 是否必填 |
| ------ |:----:| ---- |:----:|
| height | long | 区块高度 |  是   |

返回值
---
| 字段名                  |  字段类型  | 参数描述                 |
| -------------------- |:------:| -------------------- |
| hash                 | string | 区块的hash值             |
| preHash              | string | 上一个区块的hash值          |
| merkleHash           | string | 梅克尔hash              |
| time                 | string | 区块生成时间               |
| height               |  long  | 区块高度                 |
| txCount              |  int   | 区块打包交易数量             |
| blockSignature       | string | 签名Hex.encode(byte[]) |
| size                 |  int   | 大小                   |
| packingAddress       | string | 打包地址                 |
| roundIndex           |  long  | 共识轮次                 |
| consensusMemberCount |  int   | 参与共识成员数量             |
| roundStartTime       | string | 当前共识轮开始时间            |
| packingIndexOfRound  |  int   | 当前轮次打包出块的名次          |
| mainVersion          | short  | 主网当前生效的版本            |
| blockVersion         | short  | 区块的版本，可以理解为本地钱包的版本   |
| stateRoot            | string | 智能合约世界状态根            |

2.2 根据区块hash查询区块头
=================
Method: NulsSDKTool#getBlockHeader
----------------------------------
_**详细描述: 根据区块hash查询区块头**_

参数列表
----
| 参数名  |  参数类型  | 参数描述   | 是否必填 |
| ---- |:------:| ------ |:----:|
| hash | string | 区块hash |  是   |

返回值
---
| 字段名                  |  字段类型  | 参数描述                 |
| -------------------- |:------:| -------------------- |
| hash                 | string | 区块的hash值             |
| preHash              | string | 上一个区块的hash值          |
| merkleHash           | string | 梅克尔hash              |
| time                 | string | 区块生成时间               |
| height               |  long  | 区块高度                 |
| txCount              |  int   | 区块打包交易数量             |
| blockSignature       | string | 签名Hex.encode(byte[]) |
| size                 |  int   | 大小                   |
| packingAddress       | string | 打包地址                 |
| roundIndex           |  long  | 共识轮次                 |
| consensusMemberCount |  int   | 参与共识成员数量             |
| roundStartTime       | string | 当前共识轮开始时间            |
| packingIndexOfRound  |  int   | 当前轮次打包出块的名次          |
| mainVersion          | short  | 主网当前生效的版本            |
| blockVersion         | short  | 区块的版本，可以理解为本地钱包的版本   |
| stateRoot            | string | 智能合约世界状态根            |

2.3 根据区块高度查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用
============================================
Method: NulsSDKTool#getBlock
----------------------------
_**详细描述: 根据区块高度查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用**_

参数列表
----
| 参数名    | 参数类型 | 参数描述 | 是否必填 |
| ------ |:----:| ---- |:----:|
| height | long | 区块高度 |  是   |

返回值
---
| 字段名                  |  字段类型  | 参数描述                 |
| -------------------- |:------:| -------------------- |
| hash                 | string | 区块的hash值             |
| preHash              | string | 上一个区块的hash值          |
| merkleHash           | string | 梅克尔hash              |
| time                 | string | 区块生成时间               |
| height               |  long  | 区块高度                 |
| txCount              |  int   | 区块打包交易数量             |
| blockSignature       | string | 签名Hex.encode(byte[]) |
| size                 |  int   | 大小                   |
| packingAddress       | string | 打包地址                 |
| roundIndex           |  long  | 共识轮次                 |
| consensusMemberCount |  int   | 参与共识成员数量             |
| roundStartTime       | string | 当前共识轮开始时间            |
| packingIndexOfRound  |  int   | 当前轮次打包出块的名次          |
| mainVersion          | short  | 主网当前生效的版本            |
| blockVersion         | short  | 区块的版本，可以理解为本地钱包的版本   |
| stateRoot            | string | 智能合约世界状态根            |

2.4 根据区块hash查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用
==============================================
Method: NulsSDKTool#getBlock
----------------------------
_**详细描述: 根据区块hash查询区块，包含区块打包的所有交易信息，此接口返回数据量较多，谨慎调用**_

参数列表
----
| 参数名  |  参数类型  | 参数描述   | 是否必填 |
| ---- |:------:| ------ |:----:|
| hash | string | 区块hash |  是   |

返回值
---
| 字段名                  |  字段类型  | 参数描述                 |
| -------------------- |:------:| -------------------- |
| hash                 | string | 区块的hash值             |
| preHash              | string | 上一个区块的hash值          |
| merkleHash           | string | 梅克尔hash              |
| time                 | string | 区块生成时间               |
| height               |  long  | 区块高度                 |
| txCount              |  int   | 区块打包交易数量             |
| blockSignature       | string | 签名Hex.encode(byte[]) |
| size                 |  int   | 大小                   |
| packingAddress       | string | 打包地址                 |
| roundIndex           |  long  | 共识轮次                 |
| consensusMemberCount |  int   | 参与共识成员数量             |
| roundStartTime       | string | 当前共识轮开始时间            |
| packingIndexOfRound  |  int   | 当前轮次打包出块的名次          |
| mainVersion          | short  | 主网当前生效的版本            |
| blockVersion         | short  | 区块的版本，可以理解为本地钱包的版本   |
| stateRoot            | string | 智能合约世界状态根            |

2.5 查询最新区块头信息
=============
Method: NulsSDKTool#getBestBlockHeader
--------------------------------------
_**详细描述: 查询最新区块头信息**_

参数列表
----
无参数

返回值
---
| 字段名                  |  字段类型  | 参数描述                 |
| -------------------- |:------:| -------------------- |
| hash                 | string | 区块的hash值             |
| preHash              | string | 上一个区块的hash值          |
| merkleHash           | string | 梅克尔hash              |
| time                 | string | 区块生成时间               |
| height               |  long  | 区块高度                 |
| txCount              |  int   | 区块打包交易数量             |
| blockSignature       | string | 签名Hex.encode(byte[]) |
| size                 |  int   | 大小                   |
| packingAddress       | string | 打包地址                 |
| roundIndex           |  long  | 共识轮次                 |
| consensusMemberCount |  int   | 参与共识成员数量             |
| roundStartTime       | string | 当前共识轮开始时间            |
| packingIndexOfRound  |  int   | 当前轮次打包出块的名次          |
| mainVersion          | short  | 主网当前生效的版本            |
| blockVersion         | short  | 区块的版本，可以理解为本地钱包的版本   |
| stateRoot            | string | 智能合约世界状态根            |

2.6 查询最新区块
==========
Method: NulsSDKTool#getBestBlock
--------------------------------
_**详细描述: 查询最新区块**_

参数列表
----
无参数

返回值
---
| 字段名                                                                                                           |      字段类型       | 参数描述                                      |
| ------------------------------------------------------------------------------------------------------------- |:---------------:| ----------------------------------------- |
| header                                                                                                        |     object      | 区块头信息, 只返回对应的部分数据                         |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hash                                                          |     string      | 区块的hash值                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;preHash                                                       |     string      | 上一个区块的hash值                               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;merkleHash                                                    |     string      | 梅克尔hash                                   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;time                                                          |     string      | 区块生成时间                                    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;height                                                        |      long       | 区块高度                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;txCount                                                       |       int       | 区块打包交易数量                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;blockSignature                                                |     string      | 签名Hex.encode(byte[])                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;size                                                          |       int       | 大小                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;packingAddress                                                |     string      | 打包地址                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;roundIndex                                                    |      long       | 共识轮次                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;consensusMemberCount                                          |       int       | 参与共识成员数量                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;roundStartTime                                                |     string      | 当前共识轮开始时间                                 |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;packingIndexOfRound                                           |       int       | 当前轮次打包出块的名次                               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;mainVersion                                                   |      short      | 主网当前生效的版本                                 |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;blockVersion                                                  |      short      | 区块的版本，可以理解为本地钱包的版本                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;stateRoot                                                     |     string      | 智能合约世界状态根                                 |
| txs                                                                                                           | list&lt;object> | 交易列表                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hash                                                          |     string      | 交易的hash值                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;type                                                          |       int       | 交易类型                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;time                                                          |     string      | 交易时间                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;blockHeight                                                   |      long       | 区块高度                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark                                                        |     string      | 交易备注                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;transactionSignature                                          |     string      | 交易签名                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;txDataHex                                                     |     string      | 交易业务数据序列化字符串                              |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;status                                                        |       int       | 交易状态 0:unConfirm(待确认), 1:confirm(已确认)     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;size                                                          |       int       | 交易大小                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;inBlockIndex                                                  |       int       | 在区块中的顺序，存储在rocksDB中是无序的，保存区块时赋值，取出后根据此值排序 |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;form                                                          | list&lt;object> | 输入                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address       |     string      | 账户地址                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsChainId |       int       | 资产发行链的id                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsId      |       int       | 资产id                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount        |     string      | 数量                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce         |     string      | 账户nonce值                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;locked        |      byte       | 0普通交易，-1解锁金额交易（退出共识，退出委托）                 |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;to                                                            | list&lt;object> | 输出                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address       |     string      | 账户地址                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsChainId |       int       | 资产发行链的id                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsId      |       int       | 资产id                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount        |     string      | 数量                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;lockTime      |      long       | 解锁时间，-1为永久锁定                              |

3.1 根据hash查询交易详情
================
Method: NulsSDKTool#getTx
-------------------------
_**详细描述: 根据hash查询交易详情**_

参数列表
----
| 参数名  |  参数类型  | 参数描述   | 是否必填 |
| ---- |:------:| ------ |:----:|
| hash | string | 交易hash |  是   |

返回值
---
| 字段名                                                           |      字段类型       | 参数描述                                      |
| ------------------------------------------------------------- |:---------------:| ----------------------------------------- |
| hash                                                          |     string      | 交易的hash值                                  |
| type                                                          |       int       | 交易类型                                      |
| time                                                          |     string      | 交易时间                                      |
| blockHeight                                                   |      long       | 区块高度                                      |
| remark                                                        |     string      | 交易备注                                      |
| transactionSignature                                          |     string      | 交易签名                                      |
| txDataHex                                                     |     string      | 交易业务数据序列化字符串                              |
| status                                                        |       int       | 交易状态 0:unConfirm(待确认), 1:confirm(已确认)     |
| size                                                          |       int       | 交易大小                                      |
| inBlockIndex                                                  |       int       | 在区块中的顺序，存储在rocksDB中是无序的，保存区块时赋值，取出后根据此值排序 |
| form                                                          | list&lt;object> | 输入                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address       |     string      | 账户地址                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsChainId |       int       | 资产发行链的id                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsId      |       int       | 资产id                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount        |     string      | 数量                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce         |     string      | 账户nonce值                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;locked        |      byte       | 0普通交易，-1解锁金额交易（退出共识，退出委托）                 |
| to                                                            | list&lt;object> | 输出                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address       |     string      | 账户地址                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsChainId |       int       | 资产发行链的id                                  |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetsId      |       int       | 资产id                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount        |     string      | 数量                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;lockTime      |      long       | 解锁时间，-1为永久锁定                              |

3.2 验证交易
========
Method: NulsSDKTool#validateTx
------------------------------
_**详细描述: 验证离线组装的交易,验证成功返回交易hash值,失败返回错误提示信息**_

参数列表
----
| 参数名   |  参数类型  | 参数描述         | 是否必填 |
| ----- |:------:| ------------ |:----:|
| txHex | string | 交易序列化16进制字符串 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述   |
| ----- |:------:| ------ |
| value | string | 交易hash |

3.3 广播交易
========
Method: NulsSDKTool#broadcast
-----------------------------
_**详细描述: 广播离线组装的交易,成功返回true,失败返回错误提示信息**_

参数列表
----
| 参数名   |  参数类型  | 参数描述         | 是否必填 |
| ----- |:------:| ------------ |:----:|
| txHex | string | 交易序列化16进制字符串 |  是   |

返回值
---
| 字段名   |  字段类型   | 参数描述   |
| ----- |:-------:| ------ |
| value | boolean | 是否成功   |
| hash  | string  | 交易hash |

3.4 单笔转账
========
Method: NulsSDKTool#transfer
----------------------------
_**详细描述: 发起单账户单资产的转账交易**_

参数列表
----
| 参数名                                                       |     参数类型     | 参数描述   | 是否必填 |
| --------------------------------------------------------- |:------------:| ------ |:----:|
| transferForm                                              | transferform | 转账交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address   |    string    | 转账地址   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;toAddress |    string    | 接收者地址  |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password  |    string    | 密码     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount    |  biginteger  | 转账金额   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark    |    string    | 交易备注   |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述   |
| ----- |:------:| ------ |
| value | string | 交易hash |

3.5 离线组装转账交易
============
Method: NulsSDKTool#createTransferTxOffline
-------------------------------------------
_**详细描述: 根据inputs和outputs离线组装转账交易，用于单账户或多账户的转账交易。交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和**_

参数列表
----
| 参数名                                                                                                          |      参数类型       | 参数描述     | 是否必填 |
| ------------------------------------------------------------------------------------------------------------ |:---------------:| -------- |:----:|
| transferDto                                                                                                  |   transferdto   | 转账交易表单   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;inputs                                                       | list&lt;object> | 转账交易输入列表 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |     string      | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |       int       | 资产的链id   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |       int       | 资产id     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |   biginteger    | 资产金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |     string      | 资产nonce值 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;outputs                                                      | list&lt;object> | 转账交易输出列表 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |     string      | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |       int       | 资产的链id   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |       int       | 资产id     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |   biginteger    | 资产金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;lockTime     |      long       | 锁定时间     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;time                                                         |      long       | 创建时间     |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark                                                       |     string      | 交易备注     |  否   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

3.6 计算离线创建转账交易所需手续费
===================
Method: NulsSDKTool#calcTransferTxFee
-------------------------------------
_**详细描述: 计算离线创建转账交易所需手续费**_

参数列表
----
| 参数名                                                          |       参数类型       | 参数描述    | 是否必填 |
| ------------------------------------------------------------ |:----------------:| ------- |:----:|
| TransferTxFeeDto                                             | transfertxfeedto | 转账交易手续费 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;addressCount |       int        | 转账地址数量  |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fromLength   |       int        | 转账输入长度  |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;toLength     |       int        | 转账输出长度  |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark       |      string      | 交易备注    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price        |    biginteger    | 手续费单价   |  否   |

返回值
---
| 字段名 |    字段类型    | 参数描述  |
| --- |:----------:| ----- |
| 返回值 | biginteger | 手续费金额 |

3.7 离线组装多签账户转账交易
================
Method: NulsSDKTool#createMultiSignTransferTxOffline
----------------------------------------------------
_**详细描述: 根据inputs和outputs离线组装转账交易，用于单个多签账户转账。交易手续费为inputs里本链主资产金额总和，减去outputs里本链主资产总和**_

参数列表
----
| 参数名                                                                                                          |         参数类型         | 参数描述     | 是否必填 |
| ------------------------------------------------------------------------------------------------------------ |:--------------------:| -------- |:----:|
| transferDto                                                                                                  | multisigntransferdto | 转账交易表单   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeys                                                      |   list&lt;string>    | 公钥集合     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;minSigns                                                     |         int          | 最小签名数    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;inputs                                                       |   list&lt;object>    | 转账交易输入列表 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |        string        | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |         int          | 资产的链id   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |         int          | 资产id     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |      biginteger      | 资产金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |        string        | 资产nonce值 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;outputs                                                      |   list&lt;object>    | 转账交易输出列表 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |        string        | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |         int          | 资产的链id   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |         int          | 资产id     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |      biginteger      | 资产金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;lockTime     |         long         | 锁定时间     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark                                                       |        string        | 交易备注     |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

3.8 计算离线创建多签账户转账交易所需手续费
=======================
Method: NulsSDKTool#calcMultiSignTransferTxFee
----------------------------------------------
_**详细描述: 计算离线创建多签账户转账交易所需手续费**_

参数列表
----
| 参数名                                                         |           参数类型            | 参数描述       | 是否必填 |
| ----------------------------------------------------------- |:-------------------------:| ---------- |:----:|
| MultiSignTransferTxFeeDto                                   | multisigntransfertxfeedto | 转账交易手续费    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeyCount |            int            | 多签地址对应公钥数量 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fromLength  |            int            | 转账输入长度     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;toLength    |            int            | 转账输出长度     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark      |          string           | 交易备注       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price       |        biginteger         | 手续费单价      |  否   |

返回值
---
| 字段名 |    字段类型    | 参数描述  |
| --- |:----------:| ----- |
| 返回值 | biginteger | 手续费金额 |

3.9 离线创建设置别名交易
==============
Method: NulsSDKTool#createAliasTxOffline
----------------------------------------
_**详细描述: 离线创建设置别名交易**_

参数列表
----
| 参数名                                                     |   参数类型   | 参数描述     | 是否必填 |
| ------------------------------------------------------- |:--------:| -------- |:----:|
| AliasDto                                                | aliasdto | 创建别名交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address |  string  | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;alias   |  string  | 别名       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce   |  string  | 资产nonce值 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark  |  string  | 交易备注     |  否   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

3.10 离线创建多签账户设置别名交易
===================
Method: NulsSDKTool#createMultiSignAliasTxOffline
-------------------------------------------------
_**详细描述: 离线创建多签账户设置别名交易**_

参数列表
----
| 参数名                                                      |       参数类型        | 参数描述         | 是否必填 |
| -------------------------------------------------------- |:-----------------:| ------------ |:----:|
| MultiSignAliasDto                                        | multisignaliasdto | 多签账户创建别名交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeys  |  list&lt;string>  | 公钥集合         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;minSigns |        int        | 最小签名数        |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

4.1 发布合约
========
Method: NulsSDKTool#createContract
----------------------------------
_**详细描述: 发布合约**_

参数列表
----
| 参数名                                                          |        参数类型        | 参数描述                 | 是否必填 |
| ------------------------------------------------------------ |:------------------:| -------------------- |:----:|
| 发布合约                                                         | contractcreateform | 发布合约表单               |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractCode |       string       | 智能合约代码(字节码的Hex编码字符串) |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;alias        |       string       | 合约别名                 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args         |      object[]      | 参数列表                 |  否   |

返回值
---
| 字段名             |  字段类型  | 参数描述        |
| --------------- |:------:| ----------- |
| txHash          | string | 发布合约的交易hash |
| contractAddress | string | 生成的合约地址     |

4.2 调用合约
========
Method: NulsSDKTool#callContract
--------------------------------
_**详细描述: 调用合约**_

参数列表
----
| 参数名                                                             |       参数类型       | 参数描述               | 是否必填 |
| --------------------------------------------------------------- |:----------------:| ------------------ |:----:|
| 调用合约                                                            | contractcallform | 调用合约表单             |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |      string      | 智能合约地址             |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value           |       long       | 交易附带的货币量           |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodName      |      string      | 方法名                |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodDesc      |      string      | 方法签名，如果方法名不重复，可以不传 |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args            |     object[]     | 参数列表               |  否   |

返回值
---
| 字段名    |  字段类型  | 参数描述        |
| ------ |:------:| ----------- |
| txHash | string | 调用合约的交易hash |

4.3 删除合约
========
Method: NulsSDKTool#deleteContract
----------------------------------
_**详细描述: 删除合约**_

参数列表
----
| 参数名                                                             |        参数类型        | 参数描述      | 是否必填 |
| --------------------------------------------------------------- |:------------------:| --------- |:----:|
| 删除合约                                                            | contractdeleteform | 删除合约表单    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sender          |       string       | 交易创建者     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |       string       | 智能合约地址    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password        |       string       | 交易创建者账户密码 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark          |       string       | 备注        |  否   |

返回值
---
| 字段名    |  字段类型  | 参数描述        |
| ------ |:------:| ----------- |
| txHash | string | 删除合约的交易hash |

4.4 token转账
===========
Method: NulsSDKTool#tokentransfer
---------------------------------
_**详细描述: token转账**_

参数列表
----
| 参数名                                                             |           参数类型            | 参数描述         | 是否必填 |
| --------------------------------------------------------------- |:-------------------------:| ------------ |:----:|
| token转账                                                         | contracttokentransferform | token转账表单    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fromAddress     |          string           | 转出者账户地址      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password        |          string           | 转出者账户地址密码    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;toAddress       |          string           | 转入者账户地址      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |          string           | 合约地址         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount          |        biginteger         | 转出的token资产金额 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark          |          string           | 备注           |  否   |

返回值
---
| 字段名    |  字段类型  | 参数描述   |
| ------ |:------:| ------ |
| txHash | string | 交易hash |

4.5 从账户地址向合约地址转账(主链资产)的合约交易
===========================
Method: NulsSDKTool#transferTocontract
--------------------------------------
_**详细描述: 从账户地址向合约地址转账(主链资产)的合约交易**_

参数列表
----
| 参数名                                                         |         参数类型         | 参数描述      | 是否必填 |
| ----------------------------------------------------------- |:--------------------:| --------- |:----:|
| 向合约地址转账                                                     | contracttransferform | 向合约地址转账表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fromAddress |        string        | 转出者账户地址   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password    |        string        | 转出者账户地址密码 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;toAddress   |        string        | 转入的合约地址   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount      |      biginteger      | 转出的主链资产金额 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;remark      |        string        | 备注        |  否   |

返回值
---
| 字段名    |  字段类型  | 参数描述   |
| ------ |:------:| ------ |
| txHash | string | 交易hash |

4.6 获取账户地址的指定token余额
====================
Method: NulsSDKTool#getTokenBalance
-----------------------------------
_**详细描述: 获取账户地址的指定token余额**_

参数列表
----
| 参数名             |  参数类型  | 参数描述 | 是否必填 |
| --------------- |:------:| ---- |:----:|
| contractAddress | string | 合约地址 |  是   |
| address         | string | 账户地址 |  是   |

返回值
---
| 字段名             |  字段类型  | 参数描述                    |
| --------------- |:------:| ----------------------- |
| contractAddress | string | 合约地址                    |
| name            | string | token名称                 |
| symbol          | string | token符号                 |
| amount          | string | token数量                 |
| decimals        |  long  | token支持的小数位数            |
| blockHeight     |  long  | 合约创建时的区块高度              |
| status          |  int   | 合约状态(0-不存在, 1-正常, 2-终止) |

4.7 获取智能合约详细信息
==============
Method: NulsSDKTool#getContractInfo
-----------------------------------
_**详细描述: 获取智能合约详细信息**_

参数列表
----
| 参数名     |  参数类型  | 参数描述 | 是否必填 |
| ------- |:------:| ---- |:----:|
| address | string | 合约地址 |  是   |

返回值
---
| 字段名                                                                                                      |      字段类型       | 参数描述                                       |
| -------------------------------------------------------------------------------------------------------- |:---------------:| ------------------------------------------ |
| createTxHash                                                                                             |     string      | 发布合约的交易hash                                |
| address                                                                                                  |     string      | 合约地址                                       |
| creater                                                                                                  |     string      | 合约创建者地址                                    |
| alias                                                                                                    |     string      | 合约别名                                       |
| createTime                                                                                               |      long       | 合约创建时间（单位：秒）                               |
| blockHeight                                                                                              |      long       | 合约创建时的区块高度                                 |
| isDirectPayable                                                                                          |     boolean     | 是否接受直接转账                                   |
| tokenType                                                                                                |       int       | token类型, 0 - 非token, 1 - NRC20, 2 - NRC721 |
| isNrc20                                                                                                  |     boolean     | 是否是NRC20合约                                 |
| nrc20TokenName                                                                                           |     string      | NRC20-token名称                              |
| nrc20TokenSymbol                                                                                         |     string      | NRC20-token符号                              |
| decimals                                                                                                 |      long       | NRC20-token支持的小数位数                         |
| totalSupply                                                                                              |     string      | NRC20-token发行总量                            |
| status                                                                                                   |     string      | 合约状态（not_found, normal, stop）              |
| method                                                                                                   | list&lt;object> | 合约方法列表                                     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name                                                     |     string      | 方法名称                                       |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;desc                                                     |     string      | 方法描述                                       |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args                                                     | list&lt;object> | 方法参数列表                                     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;type     |     string      | 参数类型                                       |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name     |     string      | 参数名称                                       |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;required |     boolean     | 是否必填                                       |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;returnArg                                                |     string      | 返回值类型                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;view                                                     |     boolean     | 是否视图方法（调用此方法数据不上链）                         |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;event                                                    |     boolean     | 是否是事件                                      |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;payable                                                  |     boolean     | 是否是可接受主链资产转账的方法                            |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;jsonSerializable                                         |     boolean     | 方法返回值是否JSON序列化                             |

4.8 获取智能合约执行结果
==============
Method: NulsSDKTool#getContractResult
-------------------------------------
_**详细描述: 获取智能合约执行结果**_

参数列表
----
| 参数名  |  参数类型  | 参数描述   | 是否必填 |
| ---- |:------:| ------ |:----:|
| hash | string | 交易hash |  是   |

返回值
---
| 字段名                                                                                                   |      字段类型       | 参数描述                                        |
| ----------------------------------------------------------------------------------------------------- |:---------------:| ------------------------------------------- |
| success                                                                                               |     boolean     | 合约执行是否成功                                    |
| errorMessage                                                                                          |     string      | 执行失败信息                                      |
| contractAddress                                                                                       |     string      | 合约地址                                        |
| result                                                                                                |     string      | 合约执行结果                                      |
| gasLimit                                                                                              |      long       | GAS限制                                       |
| gasUsed                                                                                               |      long       | 已使用GAS                                      |
| price                                                                                                 |      long       | GAS单价                                       |
| totalFee                                                                                              |     string      | 交易总手续费                                      |
| txSizeFee                                                                                             |     string      | 交易大小手续费                                     |
| actualContractFee                                                                                     |     string      | 实际执行合约手续费                                   |
| refundFee                                                                                             |     string      | 合约返回的手续费                                    |
| value                                                                                                 |     string      | 调用者向合约地址转入的主网资产金额，没有此业务时则为0                 |
| stackTrace                                                                                            |     string      | 异常堆栈踪迹                                      |
| transfers                                                                                             | list&lt;object> | 合约转账列表（从合约转出）                               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;txHash                                                |     string      | 合约生成交易：合约转账交易hash                           |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;from                                                  |     string      | 转出的合约地址                                     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value                                                 |     string      | 转账金额                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;outputs                                               | list&lt;object> | 转入的地址列表                                     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;to    |     string      | 转入地址                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value |     string      | 转入金额                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;orginTxHash                                           |     string      | 调用合约交易hash（源交易hash，合约交易由调用合约交易派生而来）         |
| events                                                                                                | list&lt;string> | 合约事件列表                                      |
| tokenTransfers                                                                                        | list&lt;object> | 合约token转账列表                                 |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress                                       |     string      | 合约地址                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;from                                                  |     string      | 付款方                                         |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;to                                                    |     string      | 收款方                                         |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value                                                 |     string      | 转账金额                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name                                                  |     string      | token名称                                     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;symbol                                                |     string      | token符号                                     |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;decimals                                              |      long       | token支持的小数位数                                |
| invokeRegisterCmds                                                                                    | list&lt;object> | 合约调用外部命令的调用记录列表                             |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cmdName                                               |     string      | 命令名称                                        |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args                                                  |       map       | 命令参数，参数不固定，依据不同的命令而来，故此处不作描述，结构为 {参数名称=参数值} |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;cmdRegisterMode                                       |     string      | 注册的命令模式（QUERY\_DATA or NEW\_TX）             |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;newTxHash                                             |     string      | 生成的交易hash（当调用的命令模式是 NEW\_TX 时，会生成交易）        |
| contractTxList                                                                                        | list&lt;string> | 合约生成交易的序列化字符串列表                             |
| remark                                                                                                |     string      | 备注                                          |

4.9 根据合约代码获取合约构造函数详情
====================
Method: NulsSDKTool#getConstructor
----------------------------------
_**详细描述: 根据合约代码获取合约构造函数详情**_

参数列表
----
| 参数名          |  参数类型  | 参数描述                 | 是否必填 |
| ------------ |:------:| -------------------- |:----:|
| contractCode | string | 智能合约代码(字节码的Hex编码字符串) |  是   |

返回值
---
| 字段名                                                                                                      |      字段类型       | 参数描述               |
| -------------------------------------------------------------------------------------------------------- |:---------------:| ------------------ |
| constructor                                                                                              |     object      | 合约构造函数详情           |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name                                                     |     string      | 方法名称               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;desc                                                     |     string      | 方法描述               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args                                                     | list&lt;object> | 方法参数列表             |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;type     |     string      | 参数类型               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name     |     string      | 参数名称               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;required |     boolean     | 是否必填               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;returnArg                                                |     string      | 返回值类型              |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;view                                                     |     boolean     | 是否视图方法（调用此方法数据不上链） |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;event                                                    |     boolean     | 是否是事件              |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;payable                                                  |     boolean     | 是否是可接受主链资产转账的方法    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;jsonSerializable                                         |     boolean     | 方法返回值是否JSON序列化     |
| nrc20                                                                                                    |     boolean     | 是否是NRC20合约         |

4.10 获取已发布合约指定函数的信息
===================
Method: NulsSDKTool#getContractMethod
-------------------------------------
_**详细描述: 获取已发布合约指定函数的信息**_

参数列表
----
| 参数名                                                             |        参数类型        | 参数描述                     | 是否必填 |
| --------------------------------------------------------------- |:------------------:| ------------------------ |:----:|
| 获取已发布合约指定函数的信息                                                  | contractmethodform | 获取已发布合约指定函数的信息表单         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |       string       | 智能合约地址                   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodName      |       string       | 方法名                      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodDesc      |       string       | 方法描述，若合约内方法没有重载，则此参数可以为空 |  否   |

返回值
---
| 字段名                                                      |      字段类型       | 参数描述               |
| -------------------------------------------------------- |:---------------:| ------------------ |
| name                                                     |     string      | 方法名称               |
| desc                                                     |     string      | 方法描述               |
| args                                                     | list&lt;object> | 方法参数列表             |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;type     |     string      | 参数类型               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name     |     string      | 参数名称               |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;required |     boolean     | 是否必填               |
| returnArg                                                |     string      | 返回值类型              |
| view                                                     |     boolean     | 是否视图方法（调用此方法数据不上链） |
| event                                                    |     boolean     | 是否是事件              |
| payable                                                  |     boolean     | 是否是可接受主链资产转账的方法    |
| jsonSerializable                                         |     boolean     | 方法返回值是否JSON序列化     |

4.11 获取已发布合约指定函数的参数类型列表
=======================
Method: NulsSDKTool#getContractMethodArgsTypes
----------------------------------------------
_**详细描述: 获取已发布合约指定函数的参数类型列表**_

参数列表
----
| 参数名                                                             |        参数类型        | 参数描述                     | 是否必填 |
| --------------------------------------------------------------- |:------------------:| ------------------------ |:----:|
| 获取已发布合约指定函数的参数类型列表                                              | contractmethodform | 获取已发布合约指定函数的参数类型表单       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |       string       | 智能合约地址                   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodName      |       string       | 方法名                      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodDesc      |       string       | 方法描述，若合约内方法没有重载，则此参数可以为空 |  否   |

返回值
---
| 字段名 |      字段类型       | 参数描述 |
| --- |:---------------:| ---- |
| 返回值 | list&lt;string> |      |

4.12 验证发布合约
===========
Method: NulsSDKTool#validateContractCreate
------------------------------------------
_**详细描述: 验证发布合约**_

参数列表
----
| 参数名                                                          |            参数类型            | 参数描述                 | 是否必填 |
| ------------------------------------------------------------ |:--------------------------:| -------------------- |:----:|
| 验证发布合约                                                       | contractvalidatecreateform | 验证发布合约表单             |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sender       |           string           | 交易创建者                |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;gasLimit     |            long            | 最大gas消耗              |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price        |            long            | 执行合约单价               |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractCode |           string           | 智能合约代码(字节码的Hex编码字符串) |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args         |          object[]          | 参数列表                 |  否   |

返回值
---
| 字段名     |  字段类型   | 参数描述      |
| ------- |:-------:| --------- |
| success | boolean | 验证成功与否    |
| code    | string  | 验证失败的错误码  |
| msg     | string  | 验证失败的错误信息 |

4.13 验证调用合约
===========
Method: NulsSDKTool#validateContractCall
----------------------------------------
_**详细描述: 验证调用合约**_

参数列表
----
| 参数名                                                             |           参数类型           | 参数描述                       | 是否必填 |
| --------------------------------------------------------------- |:------------------------:| -------------------------- |:----:|
| 验证调用合约                                                          | contractvalidatecallform | 验证调用合约表单                   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sender          |          string          | 交易创建者                      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value           |           long           | 调用者向合约地址转入的主网资产金额，没有此业务时填0 |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;gasLimit        |           long           | 最大gas消耗                    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price           |           long           | 执行合约单价                     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |          string          | 智能合约地址                     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodName      |          string          | 方法名称                       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodDesc      |          string          | 方法描述，若合约内方法没有重载，则此参数可以为空   |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args            |         object[]         | 参数列表                       |  否   |

返回值
---
| 字段名     |  字段类型   | 参数描述      |
| ------- |:-------:| --------- |
| success | boolean | 验证成功与否    |
| code    | string  | 验证失败的错误码  |
| msg     | string  | 验证失败的错误信息 |

4.14 验证删除合约
===========
Method: NulsSDKTool#validateContractDelete
------------------------------------------
_**详细描述: 验证删除合约**_

参数列表
----
| 参数名                                                             |            参数类型            | 参数描述     | 是否必填 |
| --------------------------------------------------------------- |:--------------------------:| -------- |:----:|
| 验证删除合约                                                          | contractvalidatedeleteform | 验证删除合约表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sender          |           string           | 交易创建者    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |           string           | 智能合约地址   |  是   |

返回值
---
| 字段名     |  字段类型   | 参数描述      |
| ------- |:-------:| --------- |
| success | boolean | 验证成功与否    |
| code    | string  | 验证失败的错误码  |
| msg     | string  | 验证失败的错误信息 |

4.15 估算发布合约交易的GAS
=================
Method: NulsSDKTool#imputedContractCreateGas
--------------------------------------------
_**详细描述: 估算发布合约交易的GAS**_

参数列表
----
| 参数名                                                          |             参数类型             | 参数描述                 | 是否必填 |
| ------------------------------------------------------------ |:----------------------------:| -------------------- |:----:|
| 估算发布合约交易的GAS                                                 | imputedgascontractcreateform | 估算发布合约交易的GAS表单       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sender       |            string            | 交易创建者                |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractCode |            string            | 智能合约代码(字节码的Hex编码字符串) |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args         |           object[]           | 参数列表                 |  否   |

返回值
---
| 字段名      | 字段类型 | 参数描述              |
| -------- |:----:| ----------------- |
| gasLimit | long | 消耗的gas值，执行失败返回数值1 |

4.16 估算调用合约交易的GAS
=================
Method: NulsSDKTool#imputedContractCallGas
------------------------------------------
_**详细描述: 估算调用合约交易的GAS**_

参数列表
----
| 参数名                                                             |            参数类型            | 参数描述                       | 是否必填 |
| --------------------------------------------------------------- |:--------------------------:| -------------------------- |:----:|
| 估算调用合约交易的GAS                                                    | imputedgascontractcallform | 估算调用合约交易的GAS表单             |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sender          |           string           | 交易创建者                      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value           |         biginteger         | 调用者向合约地址转入的主网资产金额，没有此业务时填0 |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |           string           | 智能合约地址                     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodName      |           string           | 方法名称                       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodDesc      |           string           | 方法描述，若合约内方法没有重载，则此参数可以为空   |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args            |          object[]          | 参数列表                       |  否   |

返回值
---
| 字段名      | 字段类型 | 参数描述              |
| -------- |:----:| ----------------- |
| gasLimit | long | 消耗的gas值，执行失败返回数值1 |

4.17 调用合约不上链方法
==============
Method: NulsSDKTool#invokeView
------------------------------
_**详细描述: 调用合约不上链方法**_

参数列表
----
| 参数名                                                             |         参数类型         | 参数描述                     | 是否必填 |
| --------------------------------------------------------------- |:--------------------:| ------------------------ |:----:|
| 调用合约不上链方法                                                       | contractviewcallform | 调用合约不上链方法表单              |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;contractAddress |        string        | 智能合约地址                   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodName      |        string        | 方法名称                     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;methodDesc      |        string        | 方法描述，若合约内方法没有重载，则此参数可以为空 |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;args            |       object[]       | 参数列表                     |  否   |

返回值
---
| 字段名    |  字段类型  | 参数描述      |
| ------ |:------:| --------- |
| result | string | 视图方法的调用结果 |

4.18 离线组装 - 发布合约的交易
===================
Method: NulsSDKTool#createContractTxOffline
-------------------------------------------
_**详细描述: 离线组装 - 发布合约的交易**_

参数列表
----
| 参数名           |    参数类型    | 参数描述                 | 是否必填 |
| ------------- |:----------:| -------------------- |:----:|
| sender        |   string   | 交易创建者账户地址            |  是   |
| senderBalance | biginteger | 账户余额                 |  是   |
| nonce         |   string   | 账户nonce值             |  是   |
| alias         |   string   | 合约别名                 |  是   |
| contractCode  |   string   | 智能合约代码(字节码的Hex编码字符串) |  是   |
| gasLimit      |    long    | 设置合约执行消耗的gas上限       |  是   |
| args          |  object[]  | 参数列表                 |  否   |
| argsType      |  string[]  | 参数类型列表               |  否   |
| remark        |   string   | 交易备注                 |  否   |

返回值
---
| 字段名             |  字段类型  | 参数描述     |
| --------------- |:------:| -------- |
| hash            | string | 交易hash   |
| txHex           | string | 交易序列化字符串 |
| contractAddress | string | 生成的合约地址  |

4.19 离线组装 - 调用合约的交易
===================
Method: NulsSDKTool#callContractTxOffline
-----------------------------------------
_**详细描述: 离线组装 - 调用合约的交易**_

参数列表
----
| 参数名             |    参数类型    | 参数描述                                     | 是否必填 |
| --------------- |:----------:| ---------------------------------------- |:----:|
| sender          |   string   | 交易创建者账户地址                                |  是   |
| senderBalance   | biginteger | 账户余额                                     |  是   |
| nonce           |   string   | 账户nonce值                                 |  是   |
| value           | biginteger | 调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO |  是   |
| contractAddress |   string   | 合约地址                                     |  是   |
| gasLimit        |    long    | 设置合约执行消耗的gas上限                           |  是   |
| methodName      |   string   | 合约方法                                     |  是   |
| methodDesc      |   string   | 合约方法描述，若合约内方法没有重载，则此参数可以为空               |  否   |
| args            |  object[]  | 参数列表                                     |  否   |
| argsType        |  string[]  | 参数类型列表                                   |  否   |
| remark          |   string   | 交易备注                                     |  否   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

4.20 离线组装 - 删除合约的交易
===================
Method: NulsSDKTool#deleteContractTxOffline
-------------------------------------------
_**详细描述: 离线组装 - 删除合约的交易**_

参数列表
----
| 参数名             |    参数类型    | 参数描述      | 是否必填 |
| --------------- |:----------:| --------- |:----:|
| sender          |   string   | 交易创建者账户地址 |  是   |
| senderBalance   | biginteger | 账户余额      |  是   |
| nonce           |   string   | 账户nonce值  |  是   |
| contractAddress |   string   | 合约地址      |  是   |
| remark          |   string   | 交易备注      |  否   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

4.21 离线组装 - token转账交易
=====================
Method: NulsSDKTool#tokenTransferTxOffline
------------------------------------------
_**详细描述: 离线组装 - token转账交易**_

参数列表
----
| 参数名             |    参数类型    | 参数描述           | 是否必填 |
| --------------- |:----------:| -------------- |:----:|
| fromAddress     |   string   | 转出者账户地址        |  是   |
| senderBalance   | biginteger | 转出者账户余额        |  是   |
| nonce           |   string   | 转出者账户nonce值    |  是   |
| toAddress       |   string   | 转入者账户地址        |  是   |
| contractAddress |   string   | token合约地址      |  是   |
| gasLimit        |    long    | 设置合约执行消耗的gas上限 |  是   |
| amount          | biginteger | 转出的token资产金额   |  是   |
| remark          |   string   | 交易备注           |  否   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

4.22 离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易
===================================
Method: NulsSDKTool#transferToContractTxOffline
-----------------------------------------------
_**详细描述: 离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易**_

参数列表
----
| 参数名           |    参数类型    | 参数描述           | 是否必填 |
| ------------- |:----------:| -------------- |:----:|
| fromAddress   |   string   | 转出者账户地址        |  是   |
| senderBalance | biginteger | 转出者账户余额        |  是   |
| nonce         |   string   | 转出者账户nonce值    |  是   |
| toAddress     |   string   | 转入的合约地址        |  是   |
| gasLimit      |    long    | 设置合约执行消耗的gas上限 |  是   |
| amount        | biginteger | 转出的主链资产金额      |  是   |
| remark        |   string   | 交易备注           |  否   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

5.1  创建共识节点
===========
Method: NulsSDKTool#createAgent
-------------------------------
_**详细描述:  创建共识节点**_

参数列表
----
| 参数名                                                            |      参数类型       | 参数描述         | 是否必填 |
| -------------------------------------------------------------- |:---------------:| ------------ |:----:|
| 创建共识(代理)节点                                                     | createagentform | 创建共识(代理)节点表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentAddress   |     string      | 节点地址         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;packingAddress |     string      | 节点出块地址       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;rewardAddress  |     string      | 获取奖励地址       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;commissionRate |       int       | 佣金比例         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deposit        |     string      | 保证金          |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password       |     string      | 密码           |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述   |
| ----- |:------:| ------ |
| value | string | 交易hash |

5.2 注销共识节点
==========
Method: NulsSDKTool#stopAgent
-----------------------------
_**详细描述: 注销共识节点**_

参数列表
----
| 参数名                                                          |     参数类型      | 参数描述     | 是否必填 |
| ------------------------------------------------------------ |:-------------:| -------- |:----:|
| 注销共识节点                                                       | stopagentform | 注销共识节点表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentAddress |    string     | 节点创建地址   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password     |    string     | 密码       |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述   |
| ----- |:------:| ------ |
| value | string | 交易hash |

5.3 deposit nuls to a bank! 申请参与共识
==================================
Method: NulsSDKTool#depositToAgent
----------------------------------
_**详细描述: deposit nuls to a bank! 申请参与共识**_

参数列表
----
| 参数名                                                       |    参数类型     | 参数描述     | 是否必填 |
| --------------------------------------------------------- |:-----------:| -------- |:----:|
| 委托参与共识                                                    | depositform | 委托参与共识表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address   |   string    | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentHash |   string    | 共识节点hash |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deposit   |   string    | 委托金      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password  |   string    | 密码       |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述   |
| ----- |:------:| ------ |
| value | string | 交易hash |

5.4 退出共识
========
Method: NulsSDKTool#withdraw
----------------------------
_**详细描述: 退出共识**_

参数列表
----
| 参数名                                                      |     参数类型     | 参数描述        | 是否必填 |
| -------------------------------------------------------- |:------------:| ----------- |:----:|
| 退出共识                                                     | withdrawform | 退出共识表单      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address  |    string    | 地址          |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;txHash   |    string    | 委托共识的交易hash |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;password |    string    | 密码          |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述   |
| ----- |:------:| ------ |
| value | string | 交易hash |

5.5 查询节点的委托共识列表
===============
Method: NulsSDKTool#getDepositList
----------------------------------
_**详细描述: 查询节点的委托共识列表**_

参数列表
----
| 参数名       |  参数类型  | 参数描述          | 是否必填 |
| --------- |:------:| ------------- |:----:|
| agentHash | string | 创建共识节点的交易hash |  是   |

返回值
---
| 字段名         |  字段类型  | 参数描述      |
| ----------- |:------:| --------- |
| deposit     | string | 委托金额      |
| agentHash   | string | 节点hash    |
| address     | string | 账户地址      |
| time        |  long  | 委托时间      |
| txHash      | string | 委托交易hash  |
| blockHeight |  long  | 委托时的区块高度  |
| delHeight   |  long  | 退出委托的区块高度 |

5.6 离线组装创建共识节点交易
================
Method: NulsSDKTool#createConsensusTxOffline
--------------------------------------------
_**详细描述: 参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)**_

参数列表
----
| 参数名                                                                                                          |     参数类型     | 参数描述     | 是否必填 |
| ------------------------------------------------------------------------------------------------------------ |:------------:| -------- |:----:|
| consensusDto                                                                                                 | consensusdto | 创建节点交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentAddress                                                 |    string    | 节点创建地址   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;packingAddress                                               |    string    | 节点出块地址   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;rewardAddress                                                |    string    | 获取共识奖励地址 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;commissionRate                                               |     int      | 节点佣金比例   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deposit                                                      |  biginteger  | 创建节点保证金  |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input                                                        |    object    | 交易输入信息   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |    string    | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |     int      | 资产的链id   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |     int      | 资产id     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |  biginteger  | 资产金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |    string    | 资产nonce值 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.7 离线组装委托共识交易
==============
Method: NulsSDKTool#createDepositTxOffline
------------------------------------------
_**详细描述: 参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)**_

参数列表
----
| 参数名                                                                                                          |    参数类型    | 参数描述     | 是否必填 |
| ------------------------------------------------------------------------------------------------------------ |:----------:| -------- |:----:|
| depositDto                                                                                                   | depositdto | 委托共识交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address                                                      |   string   | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deposit                                                      | biginteger | 委托金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentHash                                                    |   string   | 共识节点hash |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input                                                        |   object   | 交易输入信息   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |   string   | 账户地址     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |    int     | 资产的链id   |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |    int     | 资产id     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       | biginteger | 资产金额     |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |   string   | 资产nonce值 |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.8 离线组装退出委托共识交易
================
Method: NulsSDKTool#createWithdrawDepositTxOffline
--------------------------------------------------
_**详细描述: 接口的input数据，则是委托共识交易的output数据，nonce值可为空**_

参数列表
----
| 参数名                                                                                                          |    参数类型     | 参数描述        | 是否必填 |
| ------------------------------------------------------------------------------------------------------------ |:-----------:| ----------- |:----:|
| withDrawDto                                                                                                  | withdrawdto | 退出委托交易表单    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address                                                      |   string    | 地址          |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;depositHash                                                  |   string    | 委托共识交易的hash |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price                                                        | biginteger  | 手续费单价       |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input                                                        |   object    | 交易输入信息      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |   string    | 账户地址        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |     int     | 资产的链id      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |     int     | 资产id        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       | biginteger  | 资产金额        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |   string    | 资产nonce值    |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.9 离线组装注销共识节点交易
================
Method: NulsSDKTool#createStopConsensusTxOffline
------------------------------------------------
_**详细描述: 组装交易的StopDepositDto信息，可通过查询节点的委托共识列表获取，input的nonce值可为空**_

参数列表
----
| 参数名                                                                                                                                                          |       参数类型       | 参数描述        | 是否必填 |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------ |:----------------:| ----------- |:----:|
| stopConsensusDto                                                                                                                                             | stopconsensusdto | 注销共识节点交易表单  |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentHash                                                                                                    |      string      | 创建节点的交易hash |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentAddress                                                                                                 |      string      | 节点地址        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deposit                                                                                                      |    biginteger    | 创建节点的保证金    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price                                                                                                        |    biginteger    | 手续费单价       |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;depositList                                                                                                  | list&lt;object>  | 停止委托列表      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;depositHash                                                  |      string      | 委托共识的交易hash |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input                                                        |      object      | 交易输入信息      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |      string      | 账户地址        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |       int        | 资产的链id      |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |       int        | 资产id        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |    biginteger    | 资产金额        |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |      string      | 资产nonce值    |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.10 离线组装多签账户创建共识节点交易
=====================
Method: NulsSDKTool#createMultiSignConsensusTx
----------------------------------------------
_**详细描述: 参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)**_

参数列表
----
| 参数名                                                      |         参数类型          | 参数描述         | 是否必填 |
| -------------------------------------------------------- |:---------------------:| ------------ |:----:|
| consensusDto                                             | multisignconsensusdto | 多签账户创建节点交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeys  |    list&lt;string>    | 公钥集合         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;minSigns |          int          | 最小签名数        |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.11 离线组装多签账户委托共识交易
===================
Method: NulsSDKTool#createMultiSignDepositTxOffline
---------------------------------------------------
_**详细描述: 参与共识所需资产可通过查询链信息接口获取(agentChainId和agentAssetId)**_

参数列表
----
| 参数名                                                      |        参数类型         | 参数描述         | 是否必填 |
| -------------------------------------------------------- |:-------------------:| ------------ |:----:|
| depositDto                                               | multisigndepositdto | 多签账户委托共识交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeys  |   list&lt;string>   | 公钥集合         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;minSigns |         int         | 最小签名数        |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.12 离线组装多签账户退出委托共识交易
=====================
Method: NulsSDKTool#createMultiSignWithdrawDepositTxOffline
-----------------------------------------------------------
_**详细描述: 接口的input数据，则是委托共识交易的output数据，nonce值可为空**_

参数列表
----
| 参数名                                                      |         参数类型         | 参数描述         | 是否必填 |
| -------------------------------------------------------- |:--------------------:| ------------ |:----:|
| withDrawDto                                              | multisignwithdrawdto | 多签账户退出委托交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pubKeys  |   list&lt;string>    | 公钥集合         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;minSigns |         int          | 最小签名数        |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

5.13 离线组装多签账户注销共识节点交易
=====================
Method: NulsSDKTool#createMultiSignStopConsensusTx
--------------------------------------------------
_**详细描述: 组装交易的StopDepositDto信息，可通过查询节点的委托共识列表获取，input的nonce值可为空**_

参数列表
----
| 参数名                                                                                                                                                          |       参数类型       | 参数描述           | 是否必填 |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------ |:----------------:| -------------- |:----:|
| stopConsensusDto                                                                                                                                             | stopconsensusdto | 多签账户注销共识节点交易表单 |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentHash                                                                                                    |      string      | 创建节点的交易hash    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;agentAddress                                                                                                 |      string      | 节点地址           |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;deposit                                                                                                      |    biginteger    | 创建节点的保证金       |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;price                                                                                                        |    biginteger    | 手续费单价          |  否   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;depositList                                                                                                  | list&lt;object>  | 停止委托列表         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;depositHash                                                  |      string      | 委托共识的交易hash    |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;input                                                        |      object      | 交易输入信息         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;address      |      string      | 账户地址           |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetChainId |       int        | 资产的链id         |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;assetId      |       int        | 资产id           |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;amount       |    biginteger    | 资产金额           |  是   |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;nonce        |      string      | 资产nonce值       |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述         |
| ----- |:------:| ------------ |
| hash  | string | 交易hash       |
| txHex | string | 交易序列化16进制字符串 |

