<img src="https://i2.tiimg.com/604599/ace24c6b85ab0be7.png" width="100" height="100" alt=""> 

# Dandelion（蒲公英）
愿风带走希望的种子。

## 🍀 简介
Dandelion是基于Swing编写的改进版跨平台UI框架，它更符合现代化开发设计理念，我们为其中大量内容设计了
更加美观的样式和更加快捷的操作方式。它唯一的目的就是能够帮助开发者快速地制作一个美观精致
同时又操作方便的UI界面。我们为其设计了包括但不只是以下功能：Gui载入界面、主题颜色切换、多语言
切换、生命周期。

## 🧩 添加依赖
添加我们的依赖即可开始编写基于Dandelion的UI界面。
```html
<repositories>
    <repository>
        <id>dandelion-mvn-repo</id>
        <url>https://raw.githubusercontent.com/Ketuer/Dandelion/main/repo</url>
    </repository>
</repositories>

<dependencies> 
    <dependency>
        <groupId>dandelion.ui</groupId>
        <artifactId>dandelion</artifactId>
        <version>1.0-Release</version>
    </dependency>
</dependencies>
```

## ⏰ 生命周期
我们为每一个GUI界面设置了一个完整的生命周期，在开发者继承Gui类后编写自己的的Gui时，
只需将事务逻辑编写到对应的生命周期中即可，无需关心其他的逻辑结构。

![Markdown](http://i2.tiimg.com/604599/a1801bc027b79331.png)

同时，在<code>onLoad()</code>方法执行期间，会为用户展示载入界面（也可以不使用），
你可以通过传入的<code>Loading</code>对象实时更新当前的载入进度，载入界面也会同步更新
当前的载入情况，展示给用户。

## 🍭 主题颜色
我们为所有适用于颜色切换的组件都设计了对应的主题颜色，框架预置明亮和暗黑模式，当Gui的主题颜色
变化时，所有的组件颜色会跟随变化（需要提前注册对应的颜色配置文件）

![Markdown](http://i2.tiimg.com/604599/4bde471c17b7edbf.png)

开发者编写对应的颜色配置文件后，需要为组件实例注册配置文件，注册后，即可切换对应的颜色样式。

## 🇨🇳 多语言
内置i18n国际化组件，任何显示文字或是能够包含文字显示的组件都进行了多语言支持，开发者只需
编写对应的语言配置文件，放入resources文件夹并在程序中手动切换语言即可。

语言文件的命名格式为：
* zh_cn 应为 language_zh_cn.properties
* en_us 应为 language_en_us.properties
* ...

调用Gui或是组件的<code>switchLanguage()</code>方法，即可立即切换显示语言，Gui会同
步其所有组件一起更新语言设置。

## 🕙 载入界面
载入界面是本框架实现的一种全新的理念，由于许多情况下Gui的启动需要提前加载非常多或是有非常耗时的内
容，这会给用户一种是否打开失败的质疑，在Gui载入完成前引入一个进度显示界面，能够更好地反馈当前的Gui
载入状态，避免用户在不知道程序是否正常运行的情况下等待。预置的载入框像这样：

<img src="https://i2.tiimg.com/604599/cd5cd65b2fa00221.png" width="300" alt="">

这是一个<code>Tip</code>类型的提示框，它不仅仅可以作为载入界面，也可以被开发者用作其
他需要显示载入进度的 地方。Gui载入界面若设置为<code>null</code>，则表示不显示载入界面，
而是仅等待<code>onLoad()</code>方法执行完成。

## 🛠 框架结构
本框架由以下内容组成：
* 继承自JFrame的Gui类
* 大量被重写的JComponent组件类
* i18n国际化类和对应的接口
* 颜色主题切换接口
* 继承自JDialog的Tip类型提示框类

![Markdown](http://i2.tiimg.com/604599/8ed131141f4b4458.png)

## 🖥 平台兼容性
目前经过测试，能够正常进行图形绘制的平台:
* MacOSX Apple Silicon(arm)/Intel(x86)
    * arm - AzulJDK 1.8+
    * x86 - OracleJDK 1.8+
* Windows 10 arm/x86
    * arm/x86 - OracleJDK 1.8+
* Ubuntu 20.04 arm/x86
    * arm/x86 - OpenJDK 1.8+
