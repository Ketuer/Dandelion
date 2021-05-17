package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTextAreaUI;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 重新设计的文本域组件，自动换行，支持文本字数限制、提示文本和
 * 默认文本设置，支持暗黑模式切换。
 *
 * @author Ketuer
 * @since 1.0
 */
public class XTextArea extends JTextArea implements RoundBorder, ColorSwitch, LanguageSwitch {
    private final String rawHint;
    private String language = i18n.getDefaultLanguage();
    private int arc = 10;
    private static Font defaultFont = new Font("", Font.PLAIN, 13);
    private final Map<String, TextAreaColorConfig> colorConfigMap = new HashMap<>();
    private Color borderColor, hintColor, disabledColor;

    /**
     * 设置默认字体。
     * @param font 字体
     */
    public static void setDefaultFont(Font font){
        defaultFont = font;
    }


    public XTextArea(int width, int height){
        this(width, height, "", "", defaultFont, 0);
    }

    public XTextArea(int width, int height, String hint){
        this(width, height, "", hint, defaultFont, 0);
    }

    public XTextArea(int width, int height, String hint, Font font){
        this(width, height, "", hint, font, 0);
    }

    public XTextArea(int width, int height, String str, String hint, Font font){
        this(width, height, str, hint, font, 0);
    }

    public XTextArea(int width, int height, String str, String hint, Font font, int maxLength){
        this.rawHint = hint;
        this.setText(str);
        this.setFont(font);
        this.setSize(width, height);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(maxLength > 0 && getText().length() > maxLength) e.consume();
            }
        });
        this.setBorder(new XAreaBorder());
        this.setUI(new XAreaUI());
        this.setOpaque(false);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);

        TextAreaColorConfig def =
                new TextAreaColorConfig(Color.black, Color.white, Color.lightGray,
                        Color.lightGray, new Color(219, 219, 219));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new TextAreaColorConfig(Color.white, new Color(42, 42, 42),
                        Color.gray, Color.gray, new Color(118, 118, 118)));
        this.resetColor(def);
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(borderColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        if(this.isEnabled()){
            g2d.setColor(getBackground());
        }else {
            g2d.setColor(disabledColor);
        }
        g2d.fillRoundRect(1 ,1, getWidth()-2, getHeight()-2, arc, arc);
        if(getText().isEmpty()){
            g2d.setColor(hintColor);
            String str = i18n.format(rawHint, language);
            Rectangle2D.Float r = (Rectangle2D.Float) getTextBounds(str);
            g2d.drawString(str, 5, 5 - r.y);
        }
        super.paint(g);
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchColor(ColorConfig config) {
        TextAreaColorConfig areaColorConfig = colorConfigMap.get(config.getName());
        if(areaColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(areaColorConfig);
        this.repaint();
    }

    private void resetColor(TextAreaColorConfig config){
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

    public void registerColorConfig(ColorConfig config, TextAreaColorConfig areaColorConfig){
        this.colorConfigMap.put(config.getName(), areaColorConfig);
    }

    public static class TextAreaColorConfig{
        Color fontColor;
        Color backgroundColor;
        Color borderColor;
        Color hintColor;
        Color disabledColor;

        public TextAreaColorConfig(Color fontColor, Color backgroundColor, Color borderColor, Color hintColor, Color disabledColor) {
            this.fontColor = fontColor;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.hintColor = hintColor;
            this.disabledColor = disabledColor;
        }
    }
    private static class XAreaUI extends BasicTextAreaUI {
        @Override
        protected void paintBackground(Graphics g) { }
    }

    private static class XAreaBorder implements Border{

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5,5,5);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
