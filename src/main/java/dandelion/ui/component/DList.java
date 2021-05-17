package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 改进的JList组件，预置Cell组件作为单元格显示，采用更加美观
 * 的圆角设计。
 *
 */
public class DList<E> extends JList<E> implements RoundBorder, ColorSwitch, LanguageSwitch {

    private int arc = 10;
    private final Map<String, ListColorConfig> colorConfigMap = new HashMap<>();
    private Color selectedColor, selectedFontColor;
    private String language = i18n.getDefaultLanguage();
    private boolean paintBackground = true;

    public DList(int width, int height, E... elements){
        super(elements);
        this.setSize(width, height);
        this.setOpaque(false);
        this.setUI(new DListUI());
        this.setCellRenderer(new DListRender());
        this.setBorder(new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(5, 5, 5, 5);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        });
        ListColorConfig def =
                new ListColorConfig(new Color(42, 137, 215), Color.white,
                        Color.white, Color.black);
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new ListColorConfig(new Color(32, 108, 170), Color.white,
                        new Color(54, 54, 54), Color.white));
        this.resetColor(def);
    }

    public void setPaintBackground(boolean paintBackground) {
        this.paintBackground = paintBackground;
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    public void registerColorConfig(ColorConfig config, ListColorConfig listColorConfig){
        this.colorConfigMap.put(config.getName(), listColorConfig);
    }

    @Override
    public void switchColor(ColorConfig config) {
        ListColorConfig listColorConfig = colorConfigMap.get(config.getName());
        if(listColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(listColorConfig);
    }

    private void resetColor(ListColorConfig config){
        this.setForeground(config.fontColor);
        this.setBackground(config.backgroundColor);
        this.selectedColor = config.selectedColor;
        this.selectedFontColor = config.selectedFontColor;
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
    }

    private Rectangle2D getTextBounds(String text){
        return getFont().getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
    }

    public static class ListColorConfig{
        Color selectedColor;
        Color selectedFontColor;
        Color backgroundColor;
        Color fontColor;

        public ListColorConfig(Color selectedColor, Color selectedFontColor, Color backgroundColor, Color fontColor) {
            this.selectedColor = selectedColor;
            this.selectedFontColor = selectedFontColor;
            this.backgroundColor = backgroundColor;
            this.fontColor = fontColor;
        }
    }

    private class Cell extends JComponent{
        private boolean isSelect = false;

        public Cell(String text, int width, int height){
            this.setName(i18n.format(text, language));
            this.setPreferredSize(new Dimension(width, height));
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if(isSelect){
                g2d.setColor(selectedColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2d.setColor(selectedFontColor);
            }else {
                g2d.setColor(getForeground());
            }

            Rectangle2D.Float r = (Rectangle2D.Float) getTextBounds(getName());
            float y = (getHeight() - r.height)/2 - r.y;
            g2d.drawString(getName(), 10, y);

        }
    }

    private class DListRender implements ListCellRenderer<E>{
        @Override
        public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
            int h = getFixedCellHeight();
            if(h == -1) h = (int) (getTextBounds(value.toString()).getHeight() + 4);
            Insets insets = getInsets();
            Cell cell = new Cell(value.toString(), getWidth() - insets.left - insets.right, h);
            cell.setSelect(isSelected);
            return cell;
        }
    }

    private class DListUI extends BasicListUI{
        @Override
        public void paint(Graphics g, JComponent c) {
            if(paintBackground){
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            }
            super.paint(g, c);
        }
    }
}
