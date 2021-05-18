package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 大面积重新编写的JComboBox，更加清爽的界面，支持组件、文本形式，
 * 采用圆角设置，其余内容与XList大致相同。
 *
 * @param <E> 元素类型
 */
public class DSelect<E> extends JComboBox<E> implements RoundBorder , ColorSwitch, LanguageSwitch {
    private String lang = i18n.getDefaultLanguage();
    private final Map<String, SelectColorConfig> colorConfigMap = new HashMap<>();
    private Color fontColor;
    private Color selectColor;
    private Color arrowColor;
    private Color borderColor;
    private final DComboPopup popup;
    private int arc = 10;

    @SafeVarargs
    public DSelect(int width, int height, E... elements){
        super(elements);
        DSelectUI ui = new DSelectUI();
        this.setUI(ui);
        this.setSize(width, height);
        this.setRenderer(new XListItemRender());
        popup = ui.comboPopup;
        this.setOpaque(false);

        SelectColorConfig def =
                new SelectColorConfig(Color.white, Color.black,
                        new Color(45, 151, 231), Color.gray, new Color(193, 193, 193));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new SelectColorConfig(new Color(90, 90, 90), Color.white,
                        new Color(36, 125, 193), Color.white, Color.darkGray));
        this.setBackground(def.backgroundColor);
        this.fontColor = def.fontColor;
        this.arrowColor = def.arrowColor;
        this.selectColor = def.selectColor;
        this.borderColor = def.borderColor;
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
        popup.arc = arc;
    }

    public void registerColorConfig(ColorConfig config, SelectColorConfig selectColorConfig){
        this.colorConfigMap.put(config.getName(), selectColorConfig);
    }

    @Override
    public void switchColor(ColorConfig config) {
        SelectColorConfig selectColorConfig = colorConfigMap.get(config.getName());
        if(selectColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.setBackground(selectColorConfig.backgroundColor);
        this.fontColor = selectColorConfig.fontColor;
        this.arrowColor = selectColorConfig.arrowColor;
        this.selectColor = selectColorConfig.selectColor;
        this.borderColor = selectColorConfig.borderColor;
        for (int i = 0; i < this.getModel().getSize(); i++) {
            if(this.getModel().getElementAt(i) instanceof ColorConfig){
                ColorSwitch reverse = (ColorSwitch) this.getModel().getElementAt(i);
                reverse.switchColor(config);
            }
        }
        popup.getList().setBackground(getBackground());
        popup.getScrollPanel().switchColor(config);
        this.repaint();
    }

    @Override
    public void switchLanguage(String language) {
        this.lang = language;
        for (int i = 0; i < this.getModel().getSize(); i++) {
            if(this.getModel().getElementAt(i) instanceof LanguageSwitch){
                LanguageSwitch reverse = (LanguageSwitch) this.getModel().getElementAt(i);
                reverse.switchLanguage(language);
            }
        }
        this.repaint();
    }

    public static class SelectColorConfig{
        Color backgroundColor;
        Color borderColor;
        Color fontColor;
        Color selectColor;
        Color arrowColor;

        public SelectColorConfig(Color backgroundColor, Color fontColor, Color selectColor, Color arrowColor, Color borderColor) {
            this.backgroundColor = backgroundColor;
            this.fontColor = fontColor;
            this.selectColor = selectColor;
            this.arrowColor = arrowColor;
            this.borderColor = borderColor;
        }
    }

    private class XListItemRender implements ListCellRenderer<E>{

        @Override
        public Component getListCellRendererComponent(JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
            final int height;
            if(value instanceof String){
                height = (int) this.getTextBounds(list.getFont(), value.toString()).getHeight() + 6;
            }else if(value instanceof Text){
                height = (int) this.getTextBounds(list.getFont(), i18n.format((Text) value, lang)).getHeight() + 6;
            }else if(value instanceof JComponent){
                height = ((JComponent)value).getHeight();
            }else {
                height = 20;
            }
            JComponent component = new JComponent() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if(isSelected){
                        g2d.setColor(selectColor);
                        g2d.fillRoundRect(0, 0, getWidth(), height, arc, arc);
                        g2d.setColor(Color.white);
                    }else {
                        g2d.setColor(fontColor);
                    }
                    if(value instanceof String){
                        int h = (int) getTextBounds(list.getFont(), value.toString()).getHeight();
                        g2d.drawString(value.toString(), 20, (h + height)/2 - h/5);
                    }else if(value instanceof Text){
                        String str = i18n.format((Text) value, lang);
                        int h = (int) getTextBounds(list.getFont(), str).getHeight();
                        g2d.drawString(str, 20, (h + height)/2 - h/5);
                    }else if(value instanceof JComponent){
                        g2d.translate(10, 0);
                        ((JComponent)value).paint(g);
                    }
                    if(getSelectedIndex() == index){
                        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawPolyline(new int[]{5, 8, 14}, new int[]{10, 14, 7}, 3);
                    }
                }
            };
            component.setPreferredSize(new Dimension(0, height));
            return component;
        }

        private Rectangle2D getTextBounds(Font font, String text){
            return font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
        }
    }

    private class DSelectUI extends BasicComboBoxUI{
        DComboPopup comboPopup;

        protected ComboPopup createPopup() {
            if(comboPopup == null) comboPopup = new DComboPopup(comboBox);
            return comboPopup;
        }

        @Override
        protected JButton createArrowButton() {
            JButton b =  new JButton(){
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(arrowColor);
                    int y = (getHeight() - 5)/2;
                    g2d.translate(0, y);
                    g2d.fillPolygon(new int[]{3, 13, 8}, new int[]{0, 0, 5}, 3); // BUG 不居中
                }
            };
            b.setOpaque(false);
            return b;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Object value = comboBox.getSelectedItem();
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(borderColor);
            g2d.fillRoundRect(0 ,0, c.getWidth(), c.getHeight(), comboPopup.arc,comboPopup.arc);
            if(comboBox.isEnabled()){
                g2d.setColor(comboBox.getBackground());
            }else {
                g2d.setColor(new Color(217, 217, 217));
            }
            g2d.fillRoundRect(1 ,1, c.getWidth()-2, c.getHeight()-2, comboPopup.arc,comboPopup.arc);
            if(comboBox.isEnabled()){
                g2d.setColor(fontColor);
            }else {
                g2d.setColor(Color.gray);
            }
            if(value instanceof String){
                int h = (int) getTextBounds(comboBox.getFont(), value.toString()).getHeight();
                g2d.drawString(value.toString(), 5, (h + comboBox.getHeight())/2 - h/5);
            }else if(value instanceof Text){
                String str = i18n.format((Text) value, lang);
                int h = (int) getTextBounds(comboBox.getFont(), str).getHeight();
                g2d.drawString(str, 5, (h + comboBox.getHeight())/2 - h/5);
            }else if(value instanceof JComponent){
                g2d.translate(10, 0);
                ((JComponent)value).paint(g);
            }
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) { }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) { }

        private Rectangle2D getTextBounds(Font font, String text){
            return font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
        }
    }

    private static class DComboPopup extends BasicComboPopup{

        public int arc = 10;

        public DComboPopup(JComboBox combo) {
            super(combo);
            this.setOpaque(false);
            this.setBorder(new Border() {
                @Override
                public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) { }

                @Override
                public Insets getBorderInsets(Component c) {
                    return new Insets(3,3,3,3);
                }

                @Override
                public boolean isBorderOpaque() {
                    return false;
                }
            });
        }

        @Override
        protected JScrollPane createScroller() {
            DScroll sp = new DScroll(getWidth(), getHeight(), list);
            sp.setHorizontalScrollBar(null);
            return sp;
        }

        @Override
        protected void configureList() {
            super.configureList();
            list.setBackground(comboBox.getBackground());
        }


        public DScroll getScrollPanel(){
            return (DScroll)scroller;
        }

        @Override
        public void paint(Graphics g) {
            list.setBackground(comboBox.getBackground());
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.lightGray);
            g2d.fillRect(0,0,getWidth(),getHeight());
            g2d.setColor(comboBox.getBackground());
            g2d.fillRect(1,1,getWidth()-2,getHeight()-2);
            super.paintChildren(g);
        }
    }
}
