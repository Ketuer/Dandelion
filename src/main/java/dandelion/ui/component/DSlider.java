package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 全新设计的圆形滚动按钮，仅支持主刻度显示，支持颜色变换和多语言。
 * 支持更加快速的设置Label名称。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DSlider extends JSlider implements ColorSwitch, LanguageSwitch {

    private final Map<String, SliderColorConfig> colorConfigMap = new HashMap<>();
    private Color borderColor;

    /**
     * 构造一个滑动按钮
     * @param orientation 方向
     * @param width 宽度
     * @param height 高度
     * @param min 最小值
     * @param max 最大值
     * @param value 默认值
     * @param step 刻度显示步长
     */
    public DSlider(int orientation, int width, int height, int min, int max, int value, int step){
        super(orientation, min, max, value);
        this.setSize(width, height);
        this.setMajorTickSpacing(step);
        this.setPaintTicks(true);
        this.setPaintTrack(true);
        this.setPaintLabels(true);
        this.setUI(new XSliderUI(this));
        this.setOpaque(false);
        SliderColorConfig def =
                new SliderColorConfig(Color.lightGray, Color.white, Color.black);
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new SliderColorConfig(new Color(111, 111, 111), new Color(61, 61, 61), Color.white));
        this.setBackground(def.backgroundColor);
        this.setForeground(def.fontColor);
        this.borderColor = def.borderColor;
    }

    public DSlider(int width, int height, int min, int max, int value, int step){
        this(HORIZONTAL, width, height, min, max, value, step);
    }

    public DSlider(int orientation, int width, int height){
        this(orientation, width, height, 0,0,0,0);
    }

    public DSlider(int width, int height){
        this(width, height, 0,0,0,0);
    }

    /**
     * 手动设置刻度信息
     * @param min 最小值
     * @param max 最大值
     * @param value 默认值
     * @param step 刻度显示步长
     */
    public void setTick(int min, int max, int value, int step){
        this.setMajorTickSpacing(step);
        this.getModel().setMinimum(min);
        this.getModel().setMaximum(max);
        this.getModel().setValue(value);
    }

    @Override
    public void switchColor(ColorConfig config) {
        SliderColorConfig sliderColorConfig = colorConfigMap.get(config.getName());
        if(sliderColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.setBackground(sliderColorConfig.backgroundColor);
        this.setForeground(sliderColorConfig.fontColor);
        this.borderColor = sliderColorConfig.borderColor;
        if(this.getLabelTable() != null){
            Enumeration e = this.getLabelTable().elements();
            while (e.hasMoreElements()){
                Object o = e.nextElement();
                if(o instanceof ColorSwitch){
                    ColorSwitch reverse = (ColorSwitch) o;
                    reverse.switchColor(config);
                }
            }
        }
    }

    @Override
    public void switchLanguage(String language) {
        if(this.getLabelTable() != null){
            Enumeration e = this.getLabelTable().elements();
            while (e.hasMoreElements()){
                Object o = e.nextElement();
                if(o instanceof ColorSwitch){
                    LanguageSwitch reverse = (LanguageSwitch) o;
                    reverse.switchLanguage(language);
                }
            }
        }
    }

    public void registerColorConfig(ColorConfig config, SliderColorConfig sliderColorConfig){
        this.colorConfigMap.put(config.getName(), sliderColorConfig);
    }

    public static class SliderColorConfig{
        Color borderColor;
        Color backgroundColor;
        Color fontColor;

        public SliderColorConfig(Color borderColor, Color backgroundColor, Color fontColor) {
            this.borderColor = borderColor;
            this.backgroundColor = backgroundColor;
            this.fontColor = fontColor;
        }
    }

    private class XSliderUI extends BasicSliderUI{
        public XSliderUI(JSlider b) {
            super(b);
        }

        @Override
        protected Dimension getThumbSize() {
            return new Dimension(17,17);
        }

        @Override
        public void paintFocus(Graphics g) { }

        @Override
        protected void calculateTickRect() {
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                tickRect.x = trackRect.x;
                tickRect.y = trackRect.y;
                tickRect.width = trackRect.width;
                tickRect.height = (slider.getPaintTicks()) ? getTickLength() : 0;
            } else {
                tickRect.width = (slider.getPaintTicks()) ? getTickLength() : 0;
                tickRect.x = trackRect.x;
                tickRect.y = trackRect.y;
                tickRect.height = trackRect.height;
            }
        }

        @Override
        protected void calculateTrackRect() {
            super.calculateTrackRect();
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                trackRect.y = Math.max(trackRect.y, 0);
            }else {
                trackRect.x = Math.max(trackRect.x, 0);
            }
        }

        @Override
        protected void calculateLabelRect() {
            super.calculateLabelRect();
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                labelRect.y = trackRect.y + 18;
            }else {
                labelRect.x = trackRect.x + 18;
            }
        }

        @Override
        protected void paintMinorTickForVertSlider(Graphics g, Rectangle tickBounds, int y) { }

        @Override
        protected void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) { }

        @Override
        protected void paintMajorTickForVertSlider(Graphics g, Rectangle tickBounds, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(borderColor);
            g2d.fillRoundRect(3, y - 2, 11, 4, 4, 4);
        }

        @Override
        protected void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(borderColor);
            g2d.fillRoundRect(x - 2, 3, 4, 11, 4, 4);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = thumbRect.x, y = thumbRect.y;
            g2d.setColor(borderColor);
            g2d.fillOval(x, y, thumbRect.width, thumbRect.height);
            g2d.setColor(slider.getBackground());
            g2d.fillOval(x + 1, y + 1, thumbRect.width-2, thumbRect.height-2);
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = trackRect.x, y = trackRect.y;
            if(slider.getOrientation() == HORIZONTAL){
                y = y + 6;
                g2d.setColor(borderColor);
                g2d.fillRect(x, y, trackRect.width, 5);
                g2d.setColor(slider.getBackground().brighter());
                g2d.fillRect(x+1, y+1, trackRect.width - 2, 3);
            }else {
                x = x + 6;
                g2d.setColor(borderColor);
                g2d.fillRoundRect(x, y, 5, trackRect.height, 5, 5);
                g2d.setColor(slider.getBackground().brighter());
                g2d.fillRoundRect(x+1, y+1, 3, trackRect.height -2, 5, 5);
            }
        }
    }
}
