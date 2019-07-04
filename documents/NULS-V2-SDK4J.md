# nuls-v2-sdk4j
根据合约代码获取合约构造函数详情
================
Method: NulsSDKTool#getConstructor
----------------------------------


参数列表
----
| 参数名          |  参数类型  | 参数描述                 | 是否非空 |
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
| isNrc20                                                                                                  |     boolean     | 是否是NRC20合约         |

离线组装 - 发布合约的交易
==============
Method: NulsSDKTool#createTxOffline
-----------------------------------


参数列表
----
| 参数名          |   参数类型   | 参数描述                 | 是否非空 |
| ------------ |:--------:| -------------------- |:----:|
| sender       |  string  | 交易创建者账户地址            |  是   |
| alias        |  string  | 合约别名                 |  是   |
| contractCode |  string  | 智能合约代码(字节码的Hex编码字符串) |  是   |
| args         | object[] | 参数列表                 |  是   |
| remark       |  string  | 交易备注                 |  是   |

返回值
---
| 字段名             |  字段类型  | 参数描述     |
| --------------- |:------:| -------- |
| hash            | string | 交易hash   |
| txHex           | string | 交易序列化字符串 |
| contractAddress | string | 生成的合约地址  |

离线组装 - 调用合约的交易
==============
Method: NulsSDKTool#callTxOffline
---------------------------------


参数列表
----
| 参数名             |    参数类型    | 参数描述                                     | 是否非空 |
| --------------- |:----------:| ---------------------------------------- |:----:|
| sender          |   string   | 交易创建者账户地址                                |  是   |
| value           | biginteger | 调用者向合约地址转入的主网资产金额，没有此业务时填BigInteger.ZERO |  是   |
| contractAddress |   string   | 合约地址                                     |  是   |
| methodName      |   string   | 合约方法                                     |  是   |
| methodDesc      |   string   | 合约方法描述，若合约内方法没有重载，则此参数可以为空               |  是   |
| args            |  object[]  | 参数列表                                     |  是   |
| remark          |   string   | 交易备注                                     |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

离线组装 - 删除合约的交易
==============
Method: NulsSDKTool#deleteTxOffline
-----------------------------------


参数列表
----
| 参数名             |  参数类型  | 参数描述      | 是否非空 |
| --------------- |:------:| --------- |:----:|
| sender          | string | 交易创建者账户地址 |  是   |
| contractAddress | string | 合约地址      |  是   |
| remark          | string | 交易备注      |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

离线组装 - token转账交易
================
Method: NulsSDKTool#tokenTransfer
---------------------------------


参数列表
----
| 参数名             |    参数类型    | 参数描述         | 是否非空 |
| --------------- |:----------:| ------------ |:----:|
| fromAddress     |   string   | 转出者账户地址      |  是   |
| toAddress       |   string   | 转入地址         |  是   |
| contractAddress |   string   | token合约地址    |  是   |
| amount          | biginteger | 转出的token资产金额 |  是   |
| remark          |   string   | 交易备注         |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

离线组装 - 从账户地址向合约地址转账(主链资产)的合约交易
==============================
Method: NulsSDKTool#tokenToContract
-----------------------------------


参数列表
----
| 参数名         |    参数类型    | 参数描述      | 是否非空 |
| ----------- |:----------:| --------- |:----:|
| fromAddress |   string   | 转出者账户地址   |  是   |
| toAddress   |   string   | 转入的合约地址   |  是   |
| amount      | biginteger | 转出的主链资产金额 |  是   |
| remark      |   string   | 交易备注      |  是   |

返回值
---
| 字段名   |  字段类型  | 参数描述     |
| ----- |:------:| -------- |
| hash  | string | 交易hash   |
| txHex | string | 交易序列化字符串 |

