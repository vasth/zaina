zaina
=====
该项目是改编自环信即时通讯云的demo改编   

如果你使用demo请在应用商城里搜索“微话聊”或者安装bin文件下的apk。  
 
ios客户端也有，只不过没有增加朋友圈功能 下载地址http://www.pgyer.com/zaina  

项目简单使用说明：

me/maxwin/view	      下拉刷新组件
uk/co/senab/photoview  android图像预览播放组件
loopj/android/http  android http请求组件
/src/com/ccxt/whl/Constant.java   配置文件，所有的http请求的静态变量都在这里
src/com/ccxt/whl/DemoApplication.java  预加载文件，百度等第三方组件一般在这里加载，代码注释
 	
activity	界面控制器集合
adapter	     适配器集合
db	数据库集合
domain	 
gushi	相册界面逻辑相关文件
service	 
task	任务集合
utils	工具集合
video/util 媒体集合
widget  控件相关

####################################
使用说明：由于时间较长所以有写配置会忘掉---一般看libs文件夹下的第三方包和AndroidManifest.xml文件里面的注释

配置说明：百度社会化登录和统计
文档说明http://developer.baidu.com/wiki/index.php?title=docs/frontia

如果有什么问题可以到http://www.imgeek.org/forum.php?mod=viewthread&tid=663 这里来发帖子
