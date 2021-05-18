package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 改进后的JLabel组件，它会自动计算文字占有大小，你也可以手动为其设
 * 置宽度显示限制，你只需要填入默认文本内容，组件就能自动更新自己的大
 * 小。它还支持多语言和颜色切换，操作非常方便。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DLabel extends JLabel implements ColorSwitch, LanguageSwitch {

    private final Map<String, LabelColorConfig> colorConfigMap = new HashMap<>();
    private Text rawText;
    private String language = i18n.getDefaultLanguage();
    private static Font defaultFont = new Font("", Font.PLAIN, 13);
    private int maxWidth;
    private boolean autoResize = true;

    /**
     * 设置默认字体。
     * @param font 字体
     */
    public static void setDefaultFont(Font font){
        defaultFont = font;
    }

    public DLabel(String text){
        this(new Text(text), 0);
    }

    public DLabel(String text, int maxWidth){
        this(new Text(text), maxWidth);
    }

    public DLabel(Text text, int maxWidth){
        super(i18n.format(text));
        this.maxWidth = maxWidth;
        if(maxWidth > 0) this.setText("<html>"+getText()+"</html>");
        this.setFont(defaultFont);
        this.rawText = text;
        this.colorConfigMap.put(ColorSwitch.LIGHT.getName(), new LabelColorConfig(Color.black));
        this.colorConfigMap.put(ColorSwitch.DARK.getName(), new LabelColorConfig(new Color(230, 230, 230)));
        this.setForeground(colorConfigMap.get(ColorSwitch.LIGHT.getName()).fontColor);
        this.adjustSize();
        this.setOpaque(false);
    }

    /**
     * 是否启用自动设置大小（实验性功能，由于目前此组件的自动大小计算还不准确）
     * 关闭后，宽度限制将失效，需要手动设置组件的大小。
     * @param autoResize 自动设置
     */
    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    @Override
    public void setIcon(Icon icon) {
        if(getFont() != null){
            super.setIcon(icon);
            this.adjustSize();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if(autoResize) return getSize();
        return super.getPreferredSize();
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    private void adjustSize(){
        if(!autoResize) return;
        if(maxWidth > 0){
            String text = this.getText();
            double totalHeight = 0;
            int start = 0, end = 0;
            while (end < text.length()){
                boolean flag = false;
                while (getTextBounds(text.substring(start, end)).getWidth() < maxWidth){
                    if(end >= text.length()){
                        flag = true;
                        break;
                    }
                    end++;
                }
                Rectangle2D.Float r = (Rectangle2D.Float) getTextBounds(text.substring(start, end));
                double height = r.getHeight();
                totalHeight += (height + (r.y + r.height)/4);
                if(!flag){
                    start = end;
                }else {
                    break;
                }
            }
            this.setSize(maxWidth, (int) Math.ceil(totalHeight));
        }else {
            Rectangle2D rectangle = this.getTextBounds(getText());
            this.setSize((int) Math.ceil(rectangle.getWidth()), (int) Math.ceil(rectangle.getHeight()));
        }
        Icon icon = getIcon();
        if(icon != null){
            int h = Math.max(getHeight(), icon.getIconHeight());
            this.setSize(icon.getIconWidth() + getWidth(), h);
        }
    }

    /**
     * 自动处理为相应语言的文件，并重新调整大小。建议开发者使用
     * 此方法进行文本内容的更新。
     * @param text 文本
     */
    public void setText(Text text) {
        super.setText(i18n.format(text, language));
        this.rawText = text;
        if(maxWidth > 0) this.setText("<html>"+getText()+"</html>");
        this.adjustSize();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.adjustSize();
    }

    /**
     * 为此实例注册对应的颜色配置文件。
     * @param config 配置文件
     * @param labelColorConfig Label颜色配置文件
     */
    public void registerColorConfig(ColorConfig config, LabelColorConfig labelColorConfig){
        this.colorConfigMap.put(config.getName(), labelColorConfig);
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
        super.setText(i18n.format(rawText, language));
        if(maxWidth > 0) this.setText("<html>"+getText()+"</html>");
        this.adjustSize();
    }

    @Override
    public void switchColor(ColorConfig config) {
        LabelColorConfig labelColorConfig = colorConfigMap.get(config.getName());
        if(labelColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.setForeground(labelColorConfig.fontColor);
        this.repaint();
    }

    private Rectangle2D getTextBounds(String text){
        return this.getFont().getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
    }

    public static class LabelColorConfig{
        Color fontColor;

        public LabelColorConfig(Color fontColor){
            this.fontColor = fontColor;
        }
    }
}
