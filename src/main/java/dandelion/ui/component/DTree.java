package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 进行了颜色调整和语言支持的JTree组件，在之后的版本还会深度
 * 定制样式。
 *
 * @author Ketuer
 * @since 1.1
 */
public class DTree extends JTree implements RoundBorder, ColorSwitch, LanguageSwitch {

    private int arc = 15;
    private final Map<String, TreeColorConfig> colorConfigMap = new HashMap<>();
    private Color lineColor;
    private Color selectFontColor;
    private Color fontColor;
    private Color selectBackground;
    private String language = i18n.getDefaultLanguage();

    public DTree(TreeNode top, int width, int height){
        super(top);
        this.setOpaque(false);
        this.setSize(width, height);
        this.setUI(new DTreeUI());
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

        DTreeCellRender render = new DTreeCellRender();
        this.setCellRenderer(render);

        TreeColorConfig def =
                new TreeColorConfig(Color.black, Color.black, Color.white,
                        new Color(25, 142, 238), Color.white);
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new TreeColorConfig(Color.white, Color.white, Color.white,
                        new Color(34, 128, 201), new Color(61, 61, 61)));
        this.resetColor(def);
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchColor(ColorConfig config) {
        TreeColorConfig treeColorConfig = colorConfigMap.get(config.getName());
        if(treeColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(treeColorConfig);
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
    }

    public void registerColorConfig(ColorConfig config, TreeColorConfig treeColorConfig){
        this.colorConfigMap.put(config.getName(), treeColorConfig);
    }

    public TreeColorConfig getColorConfig(ColorConfig config){
        return colorConfigMap.get(config.getName());
    }

    public void resetColor(TreeColorConfig config){
        this.lineColor = config.lineColor;
        this.selectBackground = config.selectBackground;
        this.selectFontColor = config.selectFontColor;
        this.fontColor = config.fontColor;
        this.setBackground(config.backgroundColor);
    }

    public static class TreeColorConfig{
        public Color lineColor;
        public Color fontColor;
        public Color selectFontColor;
        public Color selectBackground;
        public Color backgroundColor;

        public TreeColorConfig(Color lineColor, Color fontColor, Color selectFontColor, Color selectBackground, Color backgroundColor) {
            this.lineColor = lineColor;
            this.fontColor = fontColor;
            this.selectFontColor = selectFontColor;
            this.selectBackground = selectBackground;
            this.backgroundColor = backgroundColor;
        }
    }

    private class DTreeCellRender extends DefaultTreeCellRenderer{
        public DTreeCellRender(){
            this.setBorderSelectionColor(new Color(0,0,0,0));
            this.setBackgroundNonSelectionColor(new Color(0,0,0,0));
        }

        @Override
        public Color getTextNonSelectionColor() {
            return fontColor;
        }

        @Override
        public Color getTextSelectionColor() {
            return selectFontColor;
        }

        @Override
        public Color getBackgroundSelectionColor() {
            return selectBackground;
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            label.setText(i18n.format(value.toString(), language));
            return label;
        }
    }

    private class DTreeUI extends BasicTreeUI{
        @Override
        protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
            g.setColor(lineColor);
            super.paintVerticalLine(g, c, x, top, bottom);
        }

        @Override
        protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
            g.setColor(lineColor);
            super.paintHorizontalLine(g, c, y, left, right);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            super.paint(g, c);
        }
    }
}
