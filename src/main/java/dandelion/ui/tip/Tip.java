package dandelion.ui.tip;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.component.RoundBorder;
import dandelion.ui.gui.Gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Tip extends JDialog implements ColorSwitch, RoundBorder {

    int arc = 19;
    private final Map<String, TipColorConfig> colorConfigMap = new HashMap<>();
    private Color borderColor;

    public Tip(Gui parent, int width, int height, boolean modal){
        super(parent, modal);
        this.setResizable(false);
        this.setUndecorated(true);
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setBounds((screenWidth - width) / 2, (screenHeight - height) / 2, width, height);
        this.getRootPane().setOpaque (false);
        this.setBackground(new Color (0, 0, 0, 0));
        this.setContentPane(new TipPanel());
        TipColorConfig def = new TipColorConfig(Color.lightGray, ColorSwitch.LIGHT.getBackground());
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK, new TipColorConfig(Color.darkGray, ColorSwitch.DARK.getBackground()));

        this.getContentPane().setBackground(def.backgroundColor);
        this.borderColor = def.borderColor;
        this.getContentPane().setLayout(null);
    }

    /**
     * 添加一个组件，组件会自动居中
     * @param component 组件
     */
    protected void addComponent(Component component){
        this.addComponent(component, (this.getWidth() - component.getWidth())/2, (this.getHeight() - component.getHeight())/2);
    }

    /**
     * 添加一个组件，组件会自动横向居中
     * @param component 组件
     * @param y y坐标
     */
    protected void addComponent(Component component, int y){
        this.addComponent(component, (this.getWidth() - component.getWidth())/2, y);
    }

    /**
     * 添加一个组件到指定位置
     * @param component 组件
     * @param x x坐标
     * @param y y坐标
     */
    protected void addComponent(Component component, int x, int y){
        this.adjustLocation(component, x, y);
        this.add(component);
    }

    protected void adjustLocation(Component component){
        adjustLocation(component, (this.getWidth() - component.getWidth())/2, (this.getHeight() - component.getHeight())/2);
    }

    protected void adjustLocation(Component component, int y){
        adjustLocation(component, (this.getWidth() - component.getWidth())/2, y);
    }

    /**
     * 重新调整组件位置
     * @param component 组件
     * @param x x坐标
     * @param y y坐标
     */
    protected void adjustLocation(Component component, int x, int y){
        component.setLocation(x, y);
        component.repaint();
    }

    public void registerColorConfig(ColorConfig config, TipColorConfig tipColorConfig){
        this.colorConfigMap.put(config.getName(), tipColorConfig);
    }

    /**
     * 显示提示界面
     */
    public void display() {
        super.setVisible(true);
    }

    /**
     * 关闭提示界面
     */
    public void close() {
        super.dispose();
    }

    @Override
    public void switchColor(ColorConfig config) {
        TipColorConfig colorConfig = colorConfigMap.get(config.getName());
        this.getContentPane().setBackground(colorConfig.backgroundColor);
        borderColor = colorConfig.borderColor;
        for (Component c : this.getContentPane().getComponents()){
            if(c instanceof ColorSwitch) ((ColorSwitch) c).switchColor(config);
        }
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    public static class TipColorConfig{
        Color borderColor;
        Color backgroundColor;

        public TipColorConfig(Color borderColor, Color backgroundColor) {
            this.borderColor = borderColor;
            this.backgroundColor = backgroundColor;
        }
    }

    private class TipPanel extends JPanel{
        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(borderColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);
            super.paintChildren(g);
        }
    }
}
