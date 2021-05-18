package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 改进的JButton，更加美观的圆角设计，支持暗黑颜色切换，语言切换等，支持添加Icon，
 * 若为DIcon，还支持图标随颜色切换。当文本长度超出按钮时，会完整显示，而不是显示...
 * 它和DLabel一样，会根据文本内容自动设置大小，因此你可以使用无需尺寸的构造方法。
 *
 * @since 1.0
 * @author Ketuer
 */
public class DButton extends JButton implements RoundBorder, ColorSwitch, LanguageSwitch {

    private final Map<String, ButtonColorConfig> colorConfigMap = new HashMap<>();
    private int arc = 10;
    private Text rawText;
    private String language = i18n.getDefaultLanguage();
    private Color borderColor, disableBackgroundColor, pressedColor;
    private static Font defaultFont = new Font("", Font.PLAIN, 13);
    private boolean isDefaultSize = true;

    public DButton(String name){
        this(new Text(name));
    }

    public DButton(String name, ActionListener listener){
        this(new Text(name));
        this.addActionListener(listener);
    }

    public DButton(Text name){
        super(i18n.format(name));
        this.rawText = name;
        this.setUI(new DButtonUI());
        this.setOpaque(false);
        this.setFont(defaultFont);
        ButtonColorConfig def = new ButtonColorConfig(Color.white, new Color(231, 231, 231),
                new Color(226, 226, 226), Color.black, new Color(222, 222, 222));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK, new ButtonColorConfig(new Color(83, 83, 83),
                new Color(147, 147, 147), new Color(104, 104, 104),
                new Color(230, 230, 230), new Color(137, 137, 137)));
        this.resetColor(def);
        this.setBorder(null);
        this.defaultSize(false);
    }

    public DButton(String name, int width, int height){
        this(new Text(name), width, height);
    }

    public DButton(Text name, int width, int height){
        this(name);
        this.setSize(width, height);
        this.isDefaultSize = false;
    }

    /**
     * 构造一个自定义大小、自定义功能的按钮。
     * @param name 名称
     * @param width 宽度
     * @param height 高度
     */
    public DButton(String name, int width, int height, ActionListener listener){
        this(name, width, height);
        this.addActionListener(listener);
    }

    @Override
    public void setIcon(Icon defaultIcon) {
        super.setIcon(defaultIcon);
        if(isDefaultSize) defaultSize(true);
    }

    public void setText(Text text) {
        super.setText(i18n.format(text, language));
        this.rawText = text;
        this.defaultSize(getIcon() != null);
    }

    public static void setDefaultFont(Font defaultFont) {
        DButton.defaultFont = defaultFont;
    }

    private void defaultSize(boolean hasIcon){
        Rectangle2D r = getTextBounds(getText());
        int extra = hasIcon ? 4 : 0;
        this.setSize((int) r.getWidth() + 8 + extra, (int) (r.getHeight() + 8));
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    /**
     * 获取配置文件，按钮的配置文件更多情况下可能需要修改。
     * @param config 配置文件
     * @return 按钮颜色配置
     *
     * @since 1.1
     */
    public ButtonColorConfig getColorConfig(ColorConfig config){
        return colorConfigMap.get(config.getName());
    }

    @Override
    public void switchColor(ColorConfig config) {
        ButtonColorConfig buttonColorConfig = colorConfigMap.get(config.getName());
        if(buttonColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        Icon icon = this.getIcon();
        if(icon instanceof ColorSwitch) ((ColorSwitch) icon).switchColor(config);
        this.resetColor(buttonColorConfig);
        this.repaint();
    }

    private void resetColor(ButtonColorConfig config){
        this.setBackground(config.backgroundColor);
        this.setForeground(config.fontColor);
        this.borderColor = config.borderColor;
        this.disableBackgroundColor = config.disableBackgroundColor;
        this.pressedColor = config.pressedColor;
    }

    @Override
    public void switchLanguage(String language) {
        this.language =language;
        super.setText(i18n.format(rawText, language));
    }

    public void registerColorConfig(ColorConfig config, ButtonColorConfig buttonColorConfig){
        this.colorConfigMap.put(config.getName(), buttonColorConfig);
    }

    private Rectangle2D getTextBounds(String text){
        return getFont().getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
    }

    public static class ButtonColorConfig{
        public Color backgroundColor;
        public Color disableBackgroundColor;
        public Color borderColor;
        public Color fontColor;
        public Color pressedColor;

        public ButtonColorConfig(Color backgroundColor, Color disableBackgroundColor, Color borderColor, Color fontColor, Color pressedColor) {
            this.backgroundColor = backgroundColor;
            this.disableBackgroundColor = disableBackgroundColor;
            this.borderColor = borderColor;
            this.fontColor = fontColor;
            this.pressedColor = pressedColor;
        }
    }

    private class DButtonUI extends BasicButtonUI{
        @Override
        public void paint(Graphics g, JComponent c) {
            ButtonModel model = getModel();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if(!model.isPressed()){
                if(model.isEnabled()){
                    g2d.setColor(borderColor);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);
                }else{
                    g2d.setColor(borderColor);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                    g2d.setColor(disableBackgroundColor);
                    g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);
                }
            }else {
                this.paintButtonPressed(g, (AbstractButton) c);
            }

            if(model.isEnabled()){
                g2d.setColor(getForeground());
            }else {
                g2d.setColor(new Color(167, 167, 167));
            }

            Rectangle2D.Float r = (Rectangle2D.Float) g2d.getFont().getStringBounds(getText(), g2d.getFontRenderContext());
            float x, y = (getHeight() - r.height) / 2;
            if(getIcon() != null){
                int iconSize = getHeight() > getWidth() ? getWidth() - 8 : getHeight() - 8;
                Icon icon = getIcon();
                Image image = null;
                if(icon instanceof DIcon){
                    image = ((DIcon) icon).getImage();
                }else if(icon instanceof ImageIcon) {
                    image = ((ImageIcon) icon).getImage();
                }
                if(image != null)
                    g2d.drawImage(image, 4, 4, iconSize, iconSize, null);
                x = iconSize+8;
            }else{
                x = (getWidth() - r.width)/2;
            }
            g2d.drawString(getText(), x, y - r.y);
        }

        @Override
        protected void paintButtonPressed(Graphics g, AbstractButton b) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(borderColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2d.setColor(pressedColor);
            g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);
        }
    }
}
