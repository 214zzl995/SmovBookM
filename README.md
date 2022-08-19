# SmovBookM

1. [ ] 关于影片界面的想法<br>
   顶部需要一个返回栏 这个返回栏 需要监听返回 返回主界面 中间 需要播放器 底部是影片信息
2. [ ] 当点击s了全屏按钮后 网页翻转屏幕 隐藏导航栏状态栏 还有返回栏 还有 影片详情 这个状态只需要保存在这个页面 这里需要监听返回
   这里的返回监听应该为 退出全屏 这里做动画待定 应该超出我的技术范畴了
3. [ ] 添加抽屉形式 缓存多个location地址功能 点击切换
4. [ ] 修改http请求包为Retrofit
5. [ ] 优化资源 当前的资源太多 导致打包体积已经到了 13mb
6. [ ] 经测试 那张图片打包后的体积为2.5mb
7. [ ] 其他的文件 可能需要修改proguard-rules.pro文件实现
8. [ ] 当前的二维码扫描有问题 会出现多个同时存入的问题 需要弹出框确定需要选择的链接 同时需要停止二维码的扫描
9. [ ] 二维码的界面看着有点不爽 还是需要修改的 现在的框感觉太硬了 考虑无框模式 无框模式很清爽 可以的

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


明天有时间要对这个分页的获取数据进行测试，还有错误情况的返回测试，错误应该要传输到请求的地方 然后存进errormessage中 