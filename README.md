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

# 重要！

**需要对项目重构**
**参考项目** [https://github.com/skydoves/MovieCompose]()
**重构将会在 Refactor-hilt进行**

重构中出现的问题

Protobuf v3中的重复 无法针对单项去删除或去重
暂定的解决办法为 在代码中处理，将所有的全部删除后写入
但是好像还是有解决办法的
[https://stackoverflow.com/questions/13603878/how-to-delete-arbitrary-objects-in-repeated-field-protobuf](https://stackoverflow.com/questions/13603878/how-to-delete-arbitrary-objects-in-repeated-field-protobuf)