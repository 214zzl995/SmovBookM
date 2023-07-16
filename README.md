# SmovBookM

- [x] 添加抽屉形式 缓存多个location地址功能 点击切换
- [x] 修改http请求包为Retrofit
- [x] 其他的文件 可能需要修改proguard-rules.pro文件实现
- [x] 当前的二维码扫描有问题 会出现多个同时存入的问题 需要弹出框确定需要选择的链接 同时需要停止二维码的扫描
- [x] 完成详情页面数据显示
- [ ] 播放器器继续优化界面 当前需要优化的界面为 手势控制的三个dialog 锁定按钮位置及ui 字幕显示与选择 需要将控制的框也按照dialog的实现 参考 [https://www.jianshu.com/p/e1d2cc82e756](https://www.jianshu.com/p/e1d2cc82e756)
- [ ] 主页优化
- [ ] 影片过滤 重要组件
- [ ] 网址选择 修改为手风琴 覆盖所有界面
- [ ] 网址要是还出问题 需要其他解决方案
- [ ] 添加设置界面 已确定需要的 视频一级打开方式 软件打开
- [ ] 添加边界滚动条 滚动条提示 收录日期与首字母等根据排序条件来
- [ ] 添加视频排序 按照收录时间与影片名称 

# 重要！

**需要对项目重构**
**参考项目** [https://github.com/skydoves/MovieCompose]()
**重构将会在 Refactor-hilt进行**

重构中出现的问题 

1. [x]  Protobuf v3中的重复 无法针对单项去删除或去重
   暂定的解决办法为 在代码中处理，将所有的全部删除后写入
   但是好像还是有解决办法的
   [https://stackoverflow.com/questions/13603878/how-to-delete-arbitrary-objects-in-repeated-field-protobuf](https://stackoverflow.com/questions/13603878/how-to-delete-arbitrary-objects-in-repeated-field-protobuf)

MutableStateFlow MutableSharedFlow的区别
[https://blog.csdn.net/rikkatheworld/article/details/125665002](https://blog.csdn.net/rikkatheworld/article/details/125665002)
[https://blog.csdn.net/fly_with_24/article/details/120300290](https://blog.csdn.net/fly_with_24/article/details/120300290)
自己的一些理解

state 一般适用于多次读取修改的数据 数据一般用于主页 此为状态 状态在你使用软件时 一直存在

shared一般用于一次性读取的 第二次需要重新读取的数据 那个重构测试类跳转页 面会重新读取数据可能也是因为这个原因 此为事件
事件只有在特定条件下 会存在

明天有时间要对这个分页的获取数据进行测试 请求已经成功
需要对当前的错误进行返回
实现方法的设想

1. 当请求错误时抛出错误 viewModel中 捕获错误 存入errormessage 较难
2. 当请求错误时 抛出Toast 不返回数据给前台 较简单

需要对消息类型进行分类 当出现error这类时 错误应该要为Toast抛出 而错误在报文中时 应该要以errormessage 显示
对viewModel

#### 分离mainViewModel中的 多个相互影响的参数

1. 服务器url 和 历史url 当服务器url变更 应当刷新历史url
2. 页码与所有影片 当下拉页码时 应当将新的插入到list里
3. 等待状态 应当分为多个状态 请求 错误等
4. 注意！！！只分离几个参数 并不分离list等数据
5. 对errormessage 可以设置单独的按钮字符

主页中的状态

1. 页码 flow, newSmovs flow , smovs 就是homeUiState State 是最终给前台看的数据 但是需要单独写出get 给前台使用
2. ServerUrl flow ，配合 collectAsState()在前台显示 ， hisUrl State ， newHisUrl flow 将hisUrl清理后写入
   那么需要重新获取的new数据 应该为 hisUrl 和 newSmovs 获取到值后 将 值插入到 state中 所以 new hisUrl 和 newHisUrl
   一个应该为flow 一个为state 给前台使用

要看 [https://github.com/topics/datastore-android](https://github.com/topics/datastore-android) 案例

Bug 已经定位 当当前这次请求时间过长就会导致这个问题 将修改url的位置提前 好像没有这个问题了 但是不知道是不是好的

### BUG

1. 因为StateFlow的防抖导致的 多页面数据获取的bug

2. 扫描二维码界面中间不知道为什么会重组一次

3. url 还是会有丢失的问题



开屏动画设计思路

进入动画 



颜色参考 SmovDetailScreen


