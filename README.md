### bunny-rabbit

[![Build Status](https://travis-ci.org/CharLemAznable/bunny-rabbit.svg?branch=master)](https://travis-ci.org/CharLemAznable/bunny-rabbit)
[![codecov](https://codecov.io/gh/CharLemAznable/bunny-rabbit/branch/master/graph/badge.svg)](https://codecov.io/gh/CharLemAznable/bunny-rabbit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.charlemaznable/bunny-rabbit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.charlemaznable/bunny-rabbit/)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)
![GitHub code size](https://img.shields.io/github/languages/code-size/CharLemAznable/bunny-rabbit)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=alert_status)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=bugs)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=security_rating)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=sqale_index)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=code_smells)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)

[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=ncloc)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=coverage)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=CharLemAznable_bunny-rabbit&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=CharLemAznable_bunny-rabbit)

计费/扣费模块.

#### 概述

使用```vert.x```框架开发, 实现高并发/非堵塞线程安全/轻量化, 支持嵌入server应用服务部署/单独服务部署, 支持应用服务内使用发布订阅模式调用/应用服务间使用REST接口模式调用.

```
项目代号起源:

In a perfect world,
there will be no war or hunger,
all APIs will be written asynchronously
and bunny rabbits will skip hand-in-hand
with baby lambs across sunny green meadows.

在一个完美的世界中，
不存在战争和饥饿，
所有的API都将使用异步方式编写，
兔兔和小羊羔将会手牵手地跳舞
在阳光明媚的绿色草地上。

                        -- vert.x Core document
```

#### 接口列表

以下接口应包含内容:

1. 发布订阅模式, 包含发布订阅主题/消息格式/应答格式
2. REST接口模式, 包含请求相对路径/请求格式/响应格式

##### 计费接口

* 发布订阅主题/请求相对路径
```text
"/calculate"
```

* 消息格式/请求格式
```json
{
  "服务类型": "枚举值, 如: 短信/流量",
  "计费参数": {
    "key": "value, 依据服务类型, 选择计费实现插件, 在插件中解析计费参数, 实现实际的计费功能"
  }
}
```

* 应答格式/响应格式
```json
{
  "响应编码": "字符串, 成功或失败编码",
  "响应描述": "字符串, 成功或失败描述",
  "服务类型": "枚举值, 如: 短信/流量",
  "费用计量": "数字, 表示需使用服务的计量, 如短信条数/流量数值",
  "计量单位": "字符串, 表示服务计量的单位, 如条/兆字节"
}
```

##### 查询接口

* 发布订阅主题/请求相对路径
```text
"/query"
```

* 消息格式/请求格式
```json
{
  "服务类型": "枚举值, 如: 短信/流量"
}
```

* 应答格式/响应格式
```json
{
  "响应编码": "字符串, 成功或失败编码",
  "响应描述": "字符串, 成功或失败描述",
  "服务类型": "枚举值, 如: 短信/流量",
  "余额计量": "数字, 表示当前服务余额的计量, 如短信条数/流量数值",
  "计量单位": "字符串, 表示服务计量的单位, 如条/兆字节"
}
```

##### 充值接口

* 发布订阅主题/请求相对路径
```text
"/charge"
```

* 消息格式/请求格式
```json
{
  "服务类型": "枚举值, 如: 短信/流量",
  "充值计量": "数字, 表示需充值服务的计量, 如短信条数/流量数值"
}
```

* 应答格式/响应格式
```json
{
  "响应编码": "字符串, 成功或失败编码",
  "响应描述": "字符串, 成功或失败描述",
  "服务类型": "枚举值, 如: 短信/流量"
}
```

##### 预扣费接口

* 发布订阅主题/请求相对路径
```text
"/payment/advance"
```

* 消息格式/请求格式
```json
{
  "服务类型": "枚举值, 如: 短信/流量",
  "扣费计量": "数字, 表示预扣减的服务计量, 如短信条数/流量数值"
}
```

* 应答格式/响应格式
```json
{
  "响应编码": "字符串, 成功或失败编码",
  "响应描述": "字符串, 成功或失败描述",
  "服务类型": "枚举值, 如: 短信/流量",
  "预扣费流水号": "字符串, 用于扣费确认/扣费回退接口参数"
}
```

##### 扣费确认接口

* 发布订阅主题/请求相对路径
```text
"/payment/commit"
```

* 消息格式/请求格式
```json
{
  "服务类型": "枚举值, 如: 短信/流量",
  "预扣费流水号": "字符串, 由预扣费接口返回"
}
```

* 应答格式/响应格式
```json
{
  "响应编码": "字符串, 成功或失败编码",
  "响应描述": "字符串, 成功或失败描述",
  "服务类型": "枚举值, 如: 短信/流量",
  "扣费计量": "数字, 表示实际扣减的服务计量, 如短信条数/流量数值",
  "计量单位": "字符串, 表示服务计量的单位, 如条/兆字节"
}
```

##### 扣费回退接口

* 发布订阅主题/请求相对路径
```text
"/payment/rollback"
```

* 消息格式/请求格式
```json
{
  "服务类型": "枚举值, 如: 短信/流量",
  "预扣费流水号": "字符串, 由预扣费接口返回"
}
```

* 应答格式/响应格式
```json
{
  "响应编码": "字符串, 成功或失败编码",
  "响应描述": "字符串, 成功或失败描述",
  "服务类型": "枚举值, 如: 短信/流量",
  "回退计量": "数字, 表示回退预扣减的服务计量, 如短信条数/流量数值",
  "计量单位": "字符串, 表示服务计量的单位, 如条/兆字节"
}
```

#### 接口规范

1. 通用安全规范

所有接口的请求与应答报文, 均需包含随机字符串字段和报文全文签名字段:

* 随机字符串, 字段名称```nonsense```, 用于对签名进行扰码
* 报文全文签名, 字段名称```signature```, 将其他报文字段按字段名ASCII码排序, 按url参数格式拼接, 使用SHA256算法进行签名

2. 发布订阅模式

在应用服务内, 获取单例的```vert.x EventBus```对象, 调用```send```方法发送消息, 由异步回调获取应答

P.S. 提供SDK

3. REST接口模式

调用其他server开启的服务, 异步/同步获取HTTP响应

P.S. 提供SDK
