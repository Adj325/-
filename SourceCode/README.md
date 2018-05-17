# 烤鸡翅介绍

[TOC]

## 1. 软件介绍

### 1.1 软件功能

**不用刷脸！**

每次刷脸时，随便拍就好，用于人脸验证的是用户使用**烤鸡翅**选择的照片，上传的也是本地的照片！

### 1.2 使用环境

Android，ROOT，Xposed

经测试，Android 4~7的都能用。

由于6，7系统的不同，虽然做了动态申请权限有些权限，但有可能还是需要自己主动去给权限。

### 1.3 操作介绍

在烤鸡翅界面，有4个开关，开关成功启用时，背景会变绿。

- Hook， 是否启用软件功能
- Toast，是否启用toast调试
- XposedLog，是否启用Xposed的日志输出功能
- KjcLog，是否把日志输出到/mnt/sdcard/Android/kjc/error.log

点击空白界面或按钮，可以调用相册选取一张照片，刷脸时会用它覆盖本地验证照片。照片选取后，会被裁剪成450×600的分辨率。

打开考勤软件后，软件启用时，会弹出一条Toast，告知用户开关的启用状态及照片的最后更新时间。

刷脸后，也会有条Toast告知用户，本地验证图片的id及覆盖照片的结果（成功或失败）。

> 如果不成功，应该就是你本地的照片有问题了！
>
> 你可以乱拍，成功的话就直接上传覆盖照片; 
>
> 否则，就是烤鸡翅没法正常使用(出Bug了)，这时，你就刷脸吧！

### 1.4 软件名称、图标及版本
- 名称

  考勤助手？烤你大爷啊！烤鸡翅！

- 图标

  就是一个中指手势

- 版本

  [砂锅粉](https://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie=gbk&word=%C9%B0%B9%F8%B7%DB&fr=ala&ala=1&alatpl=adress&pos=0&hs=2&xthttps=111111)，是一种很棒的粮食。

## 2. “破解”介绍

### 2.1 考勤软件用到的框架

考勤软件为了适配IOS及Android，该软件使用了Cordova，Angular，inoic。

### 2.2 JS解析引擎

之所以能"破解"它，达到前面的效果。

就是因为它使用了Cordova，Cordova通过js调用系统原生功能，而所有的js调用最后都会调用Cordova一个的Js解析引擎。

> Cordova提供了一组设备相关的API，通过这组API，移动应用能够以JavaScript访问原生的设备功能，如摄像头、麦克风等。
>
> Cordova还提供了一组统一的JavaScript类库，以及为这些类库所用的设备相关的原生后台代码。
>
> Cordova支持如下移动操作系统：iOS, Android,ubuntu phone os, Blackberry, Windows Phone, Palm WebOS, Bada 和 Symbian。

通过查看Cordova的源代码，可以发现在`org.apache.cordova.engine.SystemExposedJsApi`这个类下，有个`exec`方法，所有的js调用，最终都会调用`exec`。

```java
// 相关源代码
public String exec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
        return bridge.jsExec(bridgeSecret, service, action, callbackId, arguments);
    }
```

1. service - 类库/插件名
2. action - 类库/插件名中具体的方法名
3. arguments - 参数

### 2.3 刷脸时，做了什么

在调试输出时，发现了两个关键之处：

1. Face.verify("学生学号",  "刷脸时拍下的照片的路径")
2. FileTrasfer.upload("刷脸时拍下的照片的路径",  "签到url", **)

用户刷脸后，软件会调用Face.verify，在本地进行照片的验证。

若验证通过（存在人脸），就会通过FileTrasfer.upload把照片传到服务器。

### 2.4 破解思路

- 优雅

  只要拦截Face.verify，就可以通过arguments获取照片路径，在方法调用前，用本地照片覆盖它，就可以达到，无论咋拍，验证的都是本地照片，上传的都是本地的照片。

- 暴力

  优雅的方法，我们得本地存几类照片，不断地切换照片。照片最终还是会传到服务器，管理员还是能看到我们的照片！

  这样多不好啊！

  由于它只是本地验证人脸，服务器那边的检验，目前来说，应该就有人工验证而已。（我不会告诉你我为啥知道这点的！）

  所以，我们可以在“优雅”的基础上，拦截FileTrasfer.upload，在方法调用前，把照片给弄坏（如替换成0kb的文件）！

  服务器那边就无法看到我们的照片，可是服务器能看到我们的**验证通过**的状态。

  只要咬定软件出问题，谁也无法折腾你，谁让它验证通过了呢？
  
  软件并未提供此功能，有需要请自己实现！

### 2.5 一些对源代码的介绍

写了4个java文件：

- Data.java

  用它获取一些文件的路径

  对配置信息进行读取和写入

- Freedom.java

  Xp主程序，不描述了

- MainActivity.java

  页面程序

- Tool.java

  用于获取时间，输出日志，切割照片

## 3. 事外之言

### 3.1 使用环境的限制

由于这软件用了“腾讯乐固”加壳，目前的我，**没法脱壳**，也因此导致软件使用环境的限制。

没有ROOT的朋友，可以自行了解下VirtualXposed。

不过，成功率不高，我与我的同学测试后，仅有一台Android 6.0的z17 mini能在VirtualXposed的环境下使用正常Xposed，考勤助手及烤鸡翅。

### 3.2 Xposed用到的ClassLoader

XP hook method时， 需要提供一个classloader。

这个玩意，我也是取巧获得而已。

反编译它，由于有壳，只能看到用于加壳的代码，在`com.tencent.StubShell.TxAppEntry`的方法中，有个方法`c`，它被调用时接收到一个context，通过context.getClassLoader()就能拿到classloader。

### 3.3 蓝牙呢？

功力有限，没法脱壳，目前没把蓝牙折腾成功，软件使用时必须得在蓝牙范围内。

精力有限，不用刷脸已经满足了我的需求，最近或以后应该是不会再去折腾蓝牙的了。

> 没脱壳前，只能通过XP把与蓝牙有关的类的方法都给hook，在方法调用前后都输出下给方法参数及类的属性（可以通过反射获取），或许有某些类的属性，方法参数就是关键的！
>
> 脱壳后，直接看Face的源代码吧，壳都能脱了，具体怎么做也不用我说了！

### 3.4 建议、意见及其它

- 建议

  程序写得很一般，来啊，请把你的改进push进来！

- 意见

  请用MD写下你的意见，然后发给我！

- 其它

  折腾它吧，蓝牙也贼恶心的！

  折腾成功的话，发我一份，谢谢！
