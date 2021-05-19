package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.gui.Gui;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

/**
 * 改进的JTabbedPane，样式几乎被完全重写，全部采用圆角设计
 * 同时支持颜色切换、语言切换等。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DTab extends JTabbedPane implements RoundBorder, ColorSwitch, LanguageSwitch {

    private final Map<String, TabColorConfig> colorConfigMap = new HashMap<>();
    private final Dimension tabSize = new Dimension(100, 30);
    private Color mainColor;
    private Color borderColor;
    private Color subColor;
    private Color mainFontColor;
    private Color subFontColor;
    private ColorConfig selectedColor = ColorSwitch.LIGHT;
    private String language = i18n.getDefaultLanguage();
    private int arc = 15;

    public DTab(Gui parent){
        DTabUI ui = new DTabUI();
        this.setUI(ui);
        this.setOpaque(false);
        this.setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(5, 5, 5, 5);
            }
            @Override
            public boolean isBorderOpaque() { return false;}
        });
        TabColorConfig def = new TabColorConfig(new Color(248, 248, 248),
                Color.lightGray, Color.lightGray, Color.black, Color.darkGray);
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new TabColorConfig(new Color(54, 54, 54), Color.black,
                        Color.black, Color.white, Color.lightGray));
        this.resetColor(def);
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                for(MouseMotionListener l : parent.getMouseMotionListeners()){
                    l.mouseDragged(e);
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                for(MouseMotionListener l : parent.getMouseMotionListeners()){
                    l.mouseMoved(e);
                }
            }
        });
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchColor(ColorConfig config) {
        this.selectedColor = config;
        TabColorConfig tabColorConfig = colorConfigMap.get(config.getName());
        if(tabColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        for (int i = 0; i < getTabCount(); i++) {
            Component component = getComponentAt(i);
            if(component instanceof ColorSwitch) ((ColorSwitch) component).switchColor(config);
            Icon icon = getIconAt(i);
            if(icon instanceof ColorSwitch) ((ColorSwitch) icon).switchColor(config);
        }
        this.resetColor(tabColorConfig);
    }

    public void registerColorConfig(ColorConfig config, TabColorConfig tabColorConfig){
        this.colorConfigMap.put(config.getName(), tabColorConfig);
    }

    /**
     * 建议使用此方法添加Tab面板内容，会自动同步语言和颜色设置。
     * @param component 组件
     * @return 组件
     */
    @Override
    public Component add(Component component) {
        if(component instanceof ColorSwitch) ((ColorSwitch) component).switchColor(selectedColor);
        if(component instanceof LanguageSwitch) ((LanguageSwitch) component).switchLanguage(language);
        return super.add(component);
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
        for (int i = 0; i < getTabCount(); i++) {
            Component component = getComponentAt(i);
            if(component instanceof LanguageSwitch)
                ((LanguageSwitch) component).switchLanguage(language);
            this.setTitleAt(i, component.getName());
        }
    }

    private void resetColor(TabColorConfig config){
        this.mainColor = config.mainColor;
        this.borderColor = config.borderColor;
        this.subColor = config.subColor;
        this.mainFontColor = config.mainFontColor;
        this.subFontColor = config.subFontColor;
    }

    public TabColorConfig getColorConfig(ColorConfig config){
        return colorConfigMap.get(config.getName());
    }

    public static class TabColorConfig{
        public Color mainColor;
        public Color borderColor;
        public Color subColor;
        public Color mainFontColor;
        public Color subFontColor;

        public TabColorConfig(Color mainColor, Color borderColor, Color subColor, Color mainFontColor, Color subFontColor) {
            this.mainColor = mainColor;
            this.borderColor = borderColor;
            this.subColor = subColor;
            this.mainFontColor = mainFontColor;
            this.subFontColor = subFontColor;
        }
    }

    private class DTabUI extends BasicTabbedPaneUI {

        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return tabSize.height;
        }

        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return tabSize.width;
        }

        @Override
        protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
            Rectangle tabRect = rects[tabIndex];
            int selectedIndex = tabPane.getSelectedIndex();
            boolean isSelected = selectedIndex == tabIndex;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int top = 0, left = 10;

            if(isSelected){
                g2d.setColor(borderColor);
                g2d.fillRoundRect(tabRect.x + 1, tabRect.y, tabRect.width - 2, tabRect.height, arc, arc);
                g2d.fillRect(tabRect.x + 1, (int) (tabRect.y + tabRect.getHeight() - arc), tabRect.width - 2, arc);
                g2d.setColor(mainColor);
                g2d.fillRoundRect(tabRect.x + 2, tabRect.y + 1, tabRect.width - 4, tabRect.height - 2, arc, arc);
                g2d.fillRect(tabRect.x + 2, (int) (tabRect.y + tabRect.getHeight() - arc), tabRect.width - 4, arc);
            }else {
                g2d.setColor(subColor);
                g2d.fillRoundRect(tabRect.x + 1, tabRect.y + 3, tabRect.width - 3, tabRect.height - 3, arc, arc);
                g2d.fillRect(tabRect.x + 1, (int) (tabRect.y + tabRect.getHeight() - arc), tabRect.width - 3, arc);
                top += 3;
            }

            Icon icon = getIconAt(tabIndex);
            if(icon != null){
                int size = tabRect.height - 12;
                Image image = null;
                ImageObserver observer = null;
                if (icon instanceof DIcon) {
                    image = ((DIcon) icon).getImage();
                    observer = ((DIcon) icon).getObserver();
                }
                if (icon instanceof ImageIcon) {
                    image = ((ImageIcon) icon).getImage();
                    observer = ((ImageIcon) icon).getImageObserver();
                }
                if(image != null) g2d.drawImage(image, tabRect.x + 6, tabRect.y + 6 + top, size, size, observer);
                left += size + 2;
            }

            g2d.setColor(isSelected ? mainFontColor : subFontColor);
            Rectangle2D.Float r = (Rectangle2D.Float) getTextBounds(getTitleAt(tabIndex));
            float x = left + tabRect.x, y = (tabRect.height - r.height)/2 - r.y + tabRect.y + top;
            g2d.drawString(getTitleAt(tabIndex), x, y);
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Insets insets = tabPane.getInsets();
            int x = insets.left, y = insets.top,
                    width = getWidth() - insets.right - insets.left,
                    height = getHeight() - insets.top - insets.bottom;
            int extra = calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
            y += extra;
            g2d.setColor(borderColor);
            g2d.fillRoundRect(x, y, width, height - extra, arc, arc);
            g2d.setColor(mainColor);
            g2d.fillRoundRect(x + 1, y + 1, width - 2, height - extra - 2, arc, arc);

            g2d.setColor(mainColor);
            Rectangle r = rects[selectedIndex];
            g2d.fillRect(r.x + 2, y, r.width - 4, 1);
        }

        @Override
        protected Insets getContentBorderInsets(int tabPlacement) {
            return new Insets(5, 5, 5, 5);
        }

        @Override
        protected Insets getTabAreaInsets(int tabPlacement) {
            return new Insets(5, 10, 0, 10);
        }

        private Rectangle2D getTextBounds(String text){
            return getFont().getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
        }
    }
}
