package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 全新设计的勾选框，有圆形和方形两种样式，支持显示文本信息。支持颜色切换、
 * 多语言切换等操作。它不支持设置长和宽，但是支持设置勾选框的大小，也就是
 * 长和宽统一使用勾选框大小。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DCheck extends JCheckBox implements RoundBorder, ColorSwitch, LanguageSwitch {

    public static final int ROUND = 0;
    public static final int RECTANGLE = 1;

    private int arc = 10;
    private final int type;

    private Color selectedColor;
    private Color centerColor;
    private Color disabledColor;
    private Color borderColor;
    private Color fontColor = Color.black;
    private Text rawText;
    private int size;
    private final Map<String, CheckColorConfig> colorConfigMap = new HashMap<>();

    public DCheck(){
        this(RECTANGLE, "");
    }

    public DCheck(String name){
        this(RECTANGLE, name);
    }

    public DCheck(Text name){
        this(RECTANGLE, name, 17);
    }

    public DCheck(int type){
        this(type, "");
    }

    public DCheck(int type, String name){
        this(type, new Text(name), 17);
    }

    public DCheck(int type, Text name, int size){
        this.size = size;
        this.setSize(size, size);
        this.type = type;
        this.rawText = name;
        this.setText(rawText);
        this.setUI(new XCheckUI());
        this.setOpaque(false);

        CheckColorConfig def =
                new CheckColorConfig(new Color(43, 137, 213), Color.white, Color.black,
                        Color.white, new Color(220, 220, 220), new Color(220, 220, 220));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new CheckColorConfig(new Color(37, 119, 186), new Color(75, 75, 75), Color.white,
                        Color.white, new Color(220, 220, 220), new Color(220, 220, 220)));
        this.resetColor(def);
    }

    /**
     * 设置大小，长和宽是一致的
     * @param size 大小
     */
    public void setSize(int size) {
        this.size = size;
        this.adjustSize();
    }

    @Override
    public void setText(String text) {
        rawText = new Text(text);
        super.setText(i18n.format(rawText));
        this.adjustSize();
    }

    public void setText(Text text){
        rawText = text;
        super.setText(i18n.format(rawText));
        this.adjustSize();
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.adjustSize();
    }

    @Override
    public void switchLanguage(String local) {
        super.setText(i18n.format(rawText, local));
        this.adjustSize();
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchColor(ColorConfig config) {
        CheckColorConfig checkColorConfig = colorConfigMap.get(config.getName());
        if(checkColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(checkColorConfig);

        this.repaint();
    }

    public CheckColorConfig getColorConfig(ColorConfig config){
        return colorConfigMap.get(config.getName());
    }

    private void resetColor(CheckColorConfig config){
        this.fontColor = config.fontColor;
        this.borderColor = config.borderColor;
        this.centerColor = config.centerColor;
        this.disabledColor = config.disabledColor;
        this.selectedColor = config.selectedColor;
        this.setBackground(config.backgroundColor);
    }

    public void registerColorConfig(ColorConfig config, CheckColorConfig checkColorConfig){
        this.colorConfigMap.put(config.getName(), checkColorConfig);
    }

    private void adjustSize(){
        int width = (int) this.getTextBounds(this.getText()).getWidth();
        this.setSize(size+width+1, this.getHeight());
    }

    private Rectangle2D getTextBounds(String text){
        return this.getFont().getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
    }

    public static class CheckColorConfig{
        public Color selectedColor;
        public Color backgroundColor;
        public Color fontColor;
        public Color centerColor;
        public Color disabledColor;
        public Color borderColor;

        public CheckColorConfig(Color selectedColor, Color backgroundColor, Color fontColor, Color centerColor, Color disabledColor, Color borderColor) {
            this.selectedColor = selectedColor;
            this.backgroundColor = backgroundColor;
            this.fontColor = fontColor;
            this.centerColor = centerColor;
            this.disabledColor = disabledColor;
            this.borderColor = borderColor;
        }
    }

    private class XCheckUI extends BasicCheckBoxUI{

        @Override
        public synchronized void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if(isEnabled()){
                g2d.setColor(fontColor);
            }else {
                g2d.setColor(Color.lightGray);
            }
            int h = (int) getTextBounds(getText()).getHeight();
            g2d.drawString(getText(), getHeight() + 1, (float) ((h + getHeight())/2 - h/5));

            switch (type){
                case ROUND:
                    if(isEnabled()){
                        if(isSelected()){
                            g2d.setColor(selectedColor.brighter());
                        }else {
                            g2d.setColor(borderColor);
                        }
                        g2d.fillOval(0, 0, getHeight(), getHeight());
                        g2d.setColor(getBackground());
                        g2d.fillOval(2, 2, getHeight()-4, getHeight()-4);

                        if(isSelected()){
                            g2d.setColor(selectedColor);
                            g2d.fillOval(3, 3, getHeight()-6, getHeight()-6);
                        }
                    }else {
                        g2d.setColor(borderColor);
                        g2d.fillOval(0, 0, getHeight(), getHeight());
                        g2d.setColor(disabledColor);
                        g2d.fillOval(2, 2, getHeight()-4, getHeight()-4);
                    }
                    break;
                default:
                case RECTANGLE:
                    if(isEnabled()){
                        if(isSelected()){
                            g2d.setColor(selectedColor.brighter());
                            g2d.fillRoundRect(0 , 0, getHeight(), getHeight(), arc, arc);
                            g2d.setColor(selectedColor);
                            g2d.fillRoundRect(1 , 1, getHeight()-2, getHeight()-2, arc, arc);
                            g2d.setColor(centerColor);
                            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            g2d.drawPolyline(new int[]{4, 7, 13}, new int[]{8, 12, 5}, 3);
                        }else {
                            g2d.setColor(borderColor);
                            g2d.fillRoundRect(0 , 0, getHeight(), getHeight(), arc, arc);
                            g2d.setColor(getBackground());
                            g2d.fillRoundRect(1 , 1, getHeight()-2, getHeight()-2, arc, arc);
                        }
                    }else {
                        g2d.setColor(borderColor);
                        g2d.fillRoundRect(0 , 0, getHeight(), getHeight(), arc, arc);
                        g2d.setColor(disabledColor);
                        g2d.fillRoundRect(1 , 1, getHeight()-2, getHeight()-2, arc, arc);
                    }
                    break;
            }
        }
    }
}
