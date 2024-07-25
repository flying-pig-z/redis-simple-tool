## 一.缓存工具类

主要自定义Redis字符类型get方法，在原有的方法基础上进行封装。用Cache-Aside策略保证缓存的最终一致性，缓存空对象防止缓存击穿，双重判定锁防止缓存穿透。

使用泛型来定义要获取的数据，定义接口让我们可以结合Lambda表达式传入从数据库中获取数据的流程。

## 二.限流工具

使用Redis实现限流功能，主要参考了taptap的开源项目（这里在原有功能的基础上进行一些删减，同时增加了滑动窗口算法）。

自定义策略模式，实现固定窗口算法，滑动窗口算法，令牌桶算法三种限流算法。限流粒度方面，默认对方法进行限流，也可以自定义方法参数进行限流。

具体各种限流算法的实现逻辑：

https://ww0n3qrfu18.feishu.cn/docx/C5fwdBXbLoXrUCxne2HcRUgznif

## 三.分布式锁

主要解决了误删问题，并且加入了可重入，可重试，自动续期的功能。

具体实现逻辑：

https://ww0n3qrfu18.feishu.cn/docx/Gw9md7dp4oiZbfxTiUOcPIvcnfd?from=from_copylink
