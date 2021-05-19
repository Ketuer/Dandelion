package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 改进的JScrollPane滚动面板，采用圆角设计，包括滚动滑块和滑道都进行了
 * 重新绘制，支持颜色切换和语言切换，所有设置会同步切换到内部组件。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DScroll extends JScrollPane implements ColorSwitch, RoundBorder, LanguageSwitch {

    private int arc = 15;
    private final Map<String, ScrollColorConfig> colorConfigMap = new HashMap<>();
    private ColorConfig config = ColorSwitch.LIGHT;
    private Color thumbBarColor;
    private String language = i18n.getDefaultLanguage();

    public DScroll(int width, int height, Component content){
        super(content);
        this.setSize(width, height);
        this.getHorizontalScrollBar().setUI(new DScrollBarUI());
        this.getVerticalScrollBar().setUI(new DScrollBarUI());
        this.setBorder(new DScrollBorder(new Insets(3,3,3,3)));
        this.getHorizontalScrollBar().setBorder(new DScrollBorder(new Insets(3, 0, 0, 0)));
        this.getVerticalScrollBar().setBorder(new DScrollBorder(new Insets(0, 3, 0, 0)));
        this.setOpaque(false);
        ScrollColorConfig def = new ScrollColorConfig(new Color(241, 241, 241),
                new Color(154, 154, 154), Color.white);
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new ScrollColorConfig(new Color(208, 208, 208), new Color(154, 154, 154),
                        new Color(66, 66, 66)));
    }

    @Override
    public void switchColor(ColorConfig config) {
        this.config = config;
        ScrollColorConfig scrollColorConfig = colorConfigMap.get(config.getName());
        if(scrollColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(scrollColorConfig);

        for(Component component : this.getViewport().getComponents()){
            if(component instanceof ColorSwitch){
                ColorSwitch reverse = (ColorSwitch) component;
                reverse.switchColor(config);
            }
        }
    }

    /**
     * 推荐使用此方法，会同步所有颜色配置和语言设置
     * @param comp 组件
     * @return 组件
     */
    @Override
    public Component add(Component comp) {
        if(comp instanceof ColorSwitch) ((ColorSwitch) comp).switchColor(config);
        if(comp instanceof LanguageSwitch) ((LanguageSwitch) comp).switchLanguage(language);
        return super.add(comp);
    }

    private void resetColor(ScrollColorConfig config){
        this.setBackground(config.background);
        if(this.getVerticalScrollBar() != null) this.getVerticalScrollBar().setBackground(config.background);
        if(this.getHorizontalScrollBar() != null) this.getHorizontalScrollBar().setBackground(config.background);
        this.getViewport().setBackground(config.background);
        this.setForeground(config.trackColor);
        this.thumbBarColor = config.thumbColor;
    }

    public void registerColorConfig(ColorConfig config, ScrollColorConfig scrollColorConfig){
        this.colorConfigMap.put(config.getName(), scrollColorConfig);
    }

    public ScrollColorConfig getColorConfig(ColorConfig config){
        return colorConfigMap.get(config.getName());
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
        for(Component component : this.getViewport().getComponents()){
            if(component instanceof LanguageSwitch){
                LanguageSwitch l = (LanguageSwitch) component;
                l.switchLanguage(language);
            }
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0,0,getWidth(),getHeight(), arc,arc);
    }

    public static class ScrollColorConfig{
        public Color trackColor;
        public Color thumbColor;
        public Color background;

        public ScrollColorConfig(Color trackColor, Color thumbColor, Color background) {
            this.trackColor = trackColor;
            this.thumbColor = thumbColor;
            this.background = background;
        }
    }

    private class DScrollBarUI extends BasicScrollBarUI {

        @Override
        public Dimension getPreferredSize(JComponent c) {
            JScrollPane.ScrollBar scrollBar = (JScrollPane.ScrollBar) c;
            if(scrollBar.getOrientation() == JScrollBar.HORIZONTAL){
                return new Dimension(scrollBarWidth, 15);
            }else{
                return new Dimension(15, scrollBar.getHeight());
            }
        }

        private JButton createZeroButton() {
            JButton jbutton = new JButton();
            jbutton.setPreferredSize(new Dimension(0, 0));
            jbutton.setMinimumSize(new Dimension(0, 0));
            jbutton.setMaximumSize(new Dimension(0, 0));
            return jbutton;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(thumbBarColor);
            g2d.fillRoundRect(thumbBounds.x+1, thumbBounds.y+1, thumbBounds.width-2, thumbBounds.height-2, 10, 10);
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(getForeground());
            g.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
        }
    }

    private static class DScrollBorder implements Border {

        Insets insets;
        public DScrollBorder(Insets insets){
            this.insets = insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
