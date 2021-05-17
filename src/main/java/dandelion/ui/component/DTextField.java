package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTextFieldUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 重新设计的文本域，你可以直接设置它的最大输入数量，以及最常用
 * 的提示文本、初始文本，支持暗黑模式切换。你可以为搜索框设定一
 * 个图标(仅支持DIcon)，会显示在搜索框的最左边。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DTextField extends JTextField implements RoundBorder, ColorSwitch, LanguageSwitch {
    private final String rawHint;
    private String language = i18n.getDefaultLanguage();
    private final DIcon icon;
    private static Font defaultFont = new Font("", Font.PLAIN, 13);
    private int arc = 10;
    private final Map<String, TextFieldColorConfig> colorConfigMap = new HashMap<>();
    private Color borderColor, hintColor, disabledColor;

    /**
     * 设置默认字体。
     * @param font 字体
     */
    public static void setDefaultFont(Font font){
        defaultFont = font;
    }

    public DTextField(int width, int height){
        this(null, width, height, "", "", defaultFont, 0);
    }

    public DTextField(DIcon icon, int width, int height){
        this(icon, width, height, "", "", defaultFont, 0);
    }

    public DTextField(DIcon icon, int width, int height, String hint){
        this(icon, width, height, "", hint, defaultFont, 0);
    }

    public DTextField(DIcon icon, int width, int height, String hint, Font font){
        this(icon, width, height, "", hint, font, 0);
    }

    public DTextField(DIcon icon, int width, int height, String str, String hint, Font font){
        this(icon, width, height, str, hint, font, 0);
    }

    /**
     * 构造一个文本域，你可以直接指定其初始值或是提示文字，
     * 你也可以限制它的最大输入文字数量。
     *
     * @param icon 图标（没有则填null）
     * @param width 宽度
     * @param height 高度
     * @param str 初始字符串
     * @param hint 提示字符串
     * @param font 字体
     * @param maxLength 最大字符数量
     */
    public DTextField(DIcon icon, int width, int height, String str, String hint, Font font, int maxLength){
        this.icon = icon;
        this.setSize(width, height);
        this.setText(str);
        this.rawHint = hint;
        this.setFont(font);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(maxLength > 0 && getText().length() > maxLength) e.consume();
            }
        });
        this.setUI(new XFieldUI());
        this.setBorder(new XFieldBorder());
        this.setOpaque(false);

        TextFieldColorConfig def =
                new TextFieldColorConfig(Color.black, Color.white, Color.lightGray,
                        Color.lightGray, new Color(219, 219, 219));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new TextFieldColorConfig(Color.white, new Color(42, 42, 42),
                        Color.gray, Color.gray, new Color(118, 118, 118)));
        this.resetColor(def);
    }

    @Override
    protected void paintBorder(Graphics g) { }

    @Override
    public void paint(Graphics g) {
        int iconSize = getHeight() - 8;
        int offset = icon == null ? 7 : iconSize + 10;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(borderColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        if(this.isEnabled()){
            g2d.setColor(getBackground());
        }else {
            g2d.setColor(disabledColor);
        }
        g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);
        if(icon != null){
            int y = (getHeight() - iconSize)/2;
            g2d.drawImage(icon.getImage(), 4, y, iconSize, iconSize, null);
        }
        if(getText().isEmpty()){
            g2d.setColor(hintColor);
            String str = i18n.format(rawHint, language);
            Rectangle2D.Float r = (Rectangle2D.Float) getTextBounds(str);
            g2d.drawString(str, offset, (getHeight() - r.height)/2 - r.y);
        }
        super.paint(g);
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
        this.repaint();
    }

    @Override
    public void switchColor(ColorConfig config) {
        TextFieldColorConfig textFieldColorConfig = colorConfigMap.get(config.getName());
        if(textFieldColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(textFieldColorConfig);
        if(icon != null) icon.switchColor(config);
        this.repaint();
    }

    private void resetColor(TextFieldColorConfig config){
        this.setBackground(config.backgroundColor);
        this.setForeground(config.fontColor);
        this.setCaretColor(config.fontColor);
        this.hintColor = config.hintColor;
        this.borderColor = config.borderColor;
        this.disabledColor = config.disabledColor;
    }

    private Rectangle2D getTextBounds(String text){
        return this.getFont().getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
    }

    public void registerColorConfig(ColorConfig config, TextFieldColorConfig textFieldColorConfig){
        this.colorConfigMap.put(config.getName(), textFieldColorConfig);
    }

    public static class TextFieldColorConfig{
        Color fontColor;
        Color backgroundColor;
        Color borderColor;
        Color hintColor;
        Color disabledColor;

        public TextFieldColorConfig(Color fontColor, Color backgroundColor, Color borderColor, Color hintColor, Color disabledColor) {
            this.fontColor = fontColor;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.hintColor = hintColor;
            this.disabledColor = disabledColor;
        }
    }

    private class XFieldBorder implements Border{
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, icon == null ? 7 : getHeight() + 2, 2, 7);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private static class XFieldUI extends BasicTextFieldUI{
        @Override
        protected void paintBackground(Graphics g) { }
    }
}
