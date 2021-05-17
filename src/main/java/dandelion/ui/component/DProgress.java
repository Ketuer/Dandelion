package dandelion.ui.component;


import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 全新设计的进度条组件，更加美观的圆角设计，它包含以下样式:
 * <ul>
 *     <li> LINE - 直线型（高度不建议太大）
 *     <li> ROUND - 圆形（建议长宽相同）
 * </ul>
 * 此组件的最大值和最小值默认为100和0，而且它支持double类型，你可以
 * 更加灵活的操控它。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DProgress extends JComponent implements ColorSwitch, RoundBorder{

    public static int LINE = 0;
    public static int ROUND = 1;

    private final Map<String, ProgressColorConfig> colorConfigMap = new HashMap<>();
    private Color borderColor;
    private double max = 100.0;
    private double min = 0.0;
    private double value = 0;
    private int arc = 5;
    private final int shape;


    public DProgress(int width, int height){
        this(width, height, LINE);
    }

    public DProgress(int width, int height, int shape){
        this.setSize(width, height);
        this.shape = shape;
        this.setOpaque(false);
        this.setUI(new DProgressUI());
        ProgressColorConfig def = new ProgressColorConfig(
                new Color(222, 222, 222),
                new Color(231, 231, 231),
                new Color(29, 139, 236));
        this.colorConfigMap.put(ColorSwitch.LIGHT.getName(), def);
        this.colorConfigMap.put(ColorSwitch.DARK.getName(), new ProgressColorConfig(
                new Color(75, 75, 75),
                new Color(59, 59, 59),
                new Color(32, 134, 226)));
        this.resetColor(def);
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    public void setRange(double max, double min){
        this.max = max;
        this.min = min;
        this.repaint();
    }

    public void setValue(double value) {
        if(value < min) value = min;
        if(value > max) value = max;
        this.value = value;
        this.repaint();
    }

    /**
     * 为此实例注册对应的颜色配置文件。
     * @param config 配置文件
     * @param progressColorConfig 颜色配置文件
     */
    public void registerColorConfig(ColorConfig config, ProgressColorConfig progressColorConfig){
        this.colorConfigMap.put(config.getName(), progressColorConfig);
    }

    @Override
    public void switchColor(ColorConfig config) {
        ProgressColorConfig progressColorConfig = colorConfigMap.get(config.getName());
        if(progressColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(progressColorConfig);
        this.repaint();
    }

    private void resetColor(ProgressColorConfig colorConfig){
        this.borderColor = colorConfig.borderColor;
        this.setBackground(colorConfig.backgroundColor);
        this.setForeground(colorConfig.fillColor);
    }

    public static class ProgressColorConfig{

        Color backgroundColor;
        Color borderColor;
        Color fillColor;

        public ProgressColorConfig(Color borderColor, Color backgroundColor, Color fillColor) {
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.fillColor = fillColor;
        }
    }

    private class DProgressUI extends ComponentUI {

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if(shape == LINE){
                g2d.setColor(borderColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
                g2d.setColor(getForeground());
                g2d.fillRoundRect(0, 0, (int) (getWidth() * value / max), getHeight(), arc, arc);
                super.paint(g, c);
            }else if(shape == ROUND){
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.translate(2, 2);
                g2d.setColor(getBackground());
                Shape back = new Arc2D.Double(new Rectangle(getWidth() - 8,getHeight() - 8),0,360,Arc2D.OPEN);
                g2d.draw(back);
                g2d.setColor(getForeground());
                Shape shape=new Arc2D.Double(new Rectangle(getWidth() - 8,getHeight() - 8),90,-360 * value / max,Arc2D.OPEN);
                g2d.draw(shape);
            }
        }
    }
}
