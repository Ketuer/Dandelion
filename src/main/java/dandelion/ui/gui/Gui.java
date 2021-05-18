package dandelion.ui.gui;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.component.DButton;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;
import dandelion.ui.lang.i18n;
import dandelion.ui.tip.Loading;
import dandelion.ui.tip.TipConfirm;
import dandelion.ui.tip.TipLoad;
import dandelion.ui.tip.TipSelect;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 基于<code>java.awt.Frame</code>改进的界面，我们称为Gui界面，
 * 我们为开发者预先进行了大量的界面配置，包括设置大小、自动居中、默认
 * 空布局等。我们还为其增加了一个新的机制，Gui界面会在打开之前调用生
 * 命周期中的<code>onCreate</code>和<code>onLoad</code>方法，
 * 在调用这些方法的时候，会展示一个载入界面（可自定义），只有当这些方
 * 法返回时，才会显示出Gui界面。我们尽可能的让<code>Gui</code>类
 * 更适合继承式编写。
 *
 * <p>
 * 我们为Gui设计了基本的生命周期，包括onCreate（创建时）、onLoad
 * （载入时）、onClose（关闭前）、onEnd（关闭后），这些方法会在对应
 * 的时候由系统调用，你可以通过重写这些方法来让你更灵活地控制Gui界面。
 *
 * <p>
 * 它实现了ColorSwitch接口，不仅Gui的背景颜色可以进行切换，只要实
 * 现了ColorSwitch接口的组件，都能自动同步到对应的颜色样式。如果你
 * 需要自定义在颜色切换时修改其他的参数，也可以重写switchColor方法。
 * @see ColorSwitch
 *
 * <p>
 * 它实现了LanguageSwitch接口，支持切换窗口标题的语言，在切换时，
 * 也会为实现了LanguageSwitch接口的组件同步切换语言。如果你需要自
 * 定义在颜色切换时修改其他的参数，也可以重写changeLanguage()方法。
 * @see LanguageSwitch
 *
 * <p>
 * 你可以直接为Gui设置背景图片，支持包内图片或是网络链接。如果你需要
 * 在颜色切换或是语言切换时更改背景图片，也可以重写对应的方法。
 *
 * @author Ketuer
 * @since 1.0
 */
public class Gui extends JFrame implements ColorSwitch, LanguageSwitch {

    private Image backgroundImg;
    private Loading load = new TipLoad(this);
    private final Text title;
    private ColorConfig colorConfig = ColorSwitch.LIGHT;
    private String language = i18n.getDefaultLanguage();

    public Gui(Text title, int width, int height){
        this(title, width, height, false);
    }

    public Gui(Text title, int width, int height, boolean undecorated){
        super(i18n.format(title));
        this.title = title;
        if(undecorated){
            this.setUndecorated(true);
            this.setBackground(new Color(0,0,0,0));
            this.setResizable(false);
            this.addMouseMotionListener(new MouseMotionAdapter() {
                int oldX, oldY;
                public void mouseDragged(MouseEvent e) {
                    setLocation(e.getXOnScreen() - oldX, e.getYOnScreen() - oldY);
                }

                public void mouseMoved(MouseEvent e) {
                    oldX = e.getX();
                    oldY = e.getY();
                }
            });
        }
        this.pack();
        this.setLayout(null);
        this.getContentPane().setBackground(colorConfig.getBackground());
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setBounds((screenWidth - width) / 2, (screenHeight - height) / 2, width, height);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.onCreate();
    }

    public Gui(String title, int width, int height){
        this(new Text(title), width, height);
    }

    public Gui(Image icon, Text title, int width, int height){
        this(title, width, height);
        this.setIconImage(icon);
    }

    /**
     * 创建一个指定大小的Gui界面。
     * @param icon 图标
     * @param title 标题
     * @param width 宽度
     * @param height 高度
     * @param loading 载入界面
     */
    public Gui(Image icon, Text title, int width, int height, Loading loading){
        this(icon, title, width, height);
        this.load = loading;
    }

    /**
     * 设置Gui界面的背景图片，图片会被拉伸至Gui的长宽大小。
     * @param url 网络URL或是包内路径
     */
    public void setBackgroundImage(String url){
        try {
            if(url.contains("http")){
                this.backgroundImg = ImageIO.read(new URL(url));
            }else {
                InputStream stream = this.getClass().getResourceAsStream(url);
                if(stream != null) this.backgroundImg = ImageIO.read(stream);
                else throw new IOException("无法读取resource下的资源 "+url+"，请确定文件是否存在！");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        if(backgroundImg != null){
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        }
        super.paint(g);
    }

    /**
     * 添加组件到指定位置，同时会为组件继承颜色配置和语言配置。
     * @param component 组件
     * @param x x坐标
     * @param y y坐标
     * @return 组件
     */
    public Component add(Component component, int x, int y) {
        component.setLocation(x, y);
        if(component instanceof ColorSwitch) ((ColorSwitch) component).switchColor(colorConfig);
        if(component instanceof LanguageSwitch) ((LanguageSwitch) component).switchLanguage(language);
        return super.add(component);
    }

    /**
     * 载入并显示此界面（本方法会进行某些参数调整，请不要使
     * 用<code>setVisible()</code>方法）
     */
    public void display(){
        if (this.load != null){
            this.load.start();
            if(load instanceof ColorSwitch) ((ColorSwitch) load).switchColor(colorConfig);
            if(load instanceof LanguageSwitch) ((LanguageSwitch) load).switchLanguage(language);
        }
        if(onLoad(load)){
            if (this.load != null) this.load.end();
            Insets insets = this.getInsets();
            this.setSize(getWidth() + insets.left + insets.right, getHeight() + insets.top + insets.bottom);
            super.setVisible(true);
        }
    }

    /**
     * 此方法包含生命周期处理，请不要使用<code>dispose()</code>方法进行关闭。
     */
    public void close(){
        if(onClose()){
            super.dispose();
            this.onEnd();
        }
    }

    /**
     * 进行颜色切换，它会修改Gui的背景颜色，同时会按照内
     * 部组件设置，修改内部组件的颜色。
     * @param config 颜色配置
     */
    @Override
    public void switchColor(ColorConfig config) {
        this.colorConfig = config;
        this.getContentPane().setBackground(config.getBackground());
        for(Component c : this.getContentPane().getComponents()){
            if(c instanceof ColorSwitch)
                ((ColorSwitch) c).switchColor(config);
        }
        this.repaint();
    }

    /**
     * 进行窗口标题语言切换，会同时切换内部组件的语言。
     * @param language 语言
     */
    @Override
    public void switchLanguage(String language) {
        this.language = language;
        this.setTitle(i18n.format(title, language));
        for(Component c : this.getContentPane().getComponents()){
            if(c instanceof LanguageSwitch)
                ((LanguageSwitch) c).switchLanguage(language);
        }
        this.repaint();
    }

    /**
     * 显示一个基于本窗口的提示确认框。
     * @param text 提示文本
     * @param confirmText 确认按钮
     * @param width 宽度
     * @param height 高度
     */
    public void showConfirmTip(String text, String confirmText, int width, int height){
        TipConfirm confirm = new TipConfirm(this, text, confirmText, width, height);
        confirm.switchColor(colorConfig);
        confirm.switchLanguage(language);
        confirm.display();
    }

    /**
     * 显示一个基于本窗口的按钮选择框，用户选择后才会返回。
     * @param text 文本内容
     * @param width 宽度
     * @param height 高度
     * @param buttons 可供选择的按钮
     * @return 选择的按钮序号（按添加顺序）
     */
    public int showSelectTip(String text, int width, int height, DButton... buttons){
        TipSelect select = new TipSelect(this, width, height, text, buttons);
        select.switchColor(colorConfig);
        select.switchLanguage(language);
        return select.displayAndGetResult();
    }

    /**
     * 在对象创建时调用。
     */
    protected void onCreate(){ }

    /**
     * 在载入过程中调用，若返回false表示载入失败，将直接进入到close()方法。
     * 你可以通过Loading实例来实时更新载入状态。
     * @param loading 载入过程
     * @return 是否载入成功
     */
    protected boolean onLoad(Loading loading){
        return true;
    }

    /**
     * 在关闭过程中调用，若返回false表示取消关闭，否则会进入到onEnd()方法。
     * @return 是否关闭
     */
    protected boolean onClose(){
        return true;
    }

    /**
     * 进行Gui关闭后的最后任务。
     */
    protected void onEnd(){
        System.exit(0);
    }

    /**
     * 禁止使用可见设置，请使用<code>display()</code>方法进行显示操作。
     * @param b 可见
     */
    @Override
    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("无法对可见性进行直接操作，请使用display()方法！");
    }

    /**
     * 禁止使用此选项进行窗口关闭，请使用<code>close()</code>
     */
    @Override
    public void dispose() {
        throw new UnsupportedOperationException("无法对可见性进行直接操作，请使用display()方法！");
    }
}
