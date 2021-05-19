package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 添加了颜色切换和语言切换支持JTable组件，后期会继续深度定制。
 *
 * @author Ketuer
 * @since 1.1
 */
public class DTable extends JTable implements ColorSwitch, LanguageSwitch {

    private final Map<String, TableColorConfig> colorConfigMap = new HashMap<>();
    private boolean editable = false;
    private final DTableCellRender render = new DTableCellRender();
    private String language = i18n.getDefaultLanguage();

    public DTable(int width, int height){
        this();
        this.setSize(width, height);
    }

    public DTable(){
        this.setUI(new BasicTableUI());
        this.setOpaque(false);
        this.getTableHeader().setDefaultRenderer(render);
        TableColorConfig def =
                new TableColorConfig(new Color(76, 152, 250), Color.white, Color.lightGray,
                        Color.black, Color.white, Color.white);
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new TableColorConfig(new Color(56, 127, 215), Color.white, Color.lightGray,
                        Color.white, Color.black, Color.darkGray));
        this.resetColor(def);
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    private void resetColor(TableColorConfig config){
        this.setBackground(config.backgroundColor);
        this.setForeground(config.fontColor);
        this.setSelectionBackground(config.selectColor);
        this.setSelectionForeground(config.selectFontColor);
        this.setGridColor(config.gridColor);
        render.setBackground(config.titleColor);
        render.setForeground(config.fontColor);
    }

    @Override
    public void switchColor(ColorConfig config) {
        TableColorConfig tableColorConfig = colorConfigMap.get(config.getName());
        if(tableColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.resetColor(tableColorConfig);
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
    }

    public void registerColorConfig(ColorConfig config, TableColorConfig tableColorConfig){
        this.colorConfigMap.put(config.getName(), tableColorConfig);
    }

    public TableColorConfig getColorConfig(ColorConfig config){
        return colorConfigMap.get(config.getName());
    }

    /**
     * 直接设置数据
     * @param data 数据
     * @param columnNames 列名
     */
    public void setData(Object[][] data, Object[] columnNames){
        DTableModel model = new DTableModel(data, columnNames);
        this.setModel(model);
    }

    public static class TableColorConfig {
        public Color selectColor;
        public Color selectFontColor;
        public Color gridColor;
        public Color fontColor;
        public Color backgroundColor;
        public Color titleColor;

        public TableColorConfig(Color selectColor, Color selectFontColor, Color gridColor, Color fontColor, Color backgroundColor, Color titleColor) {
            this.selectColor = selectColor;
            this.selectFontColor = selectFontColor;
            this.gridColor = gridColor;
            this.fontColor = fontColor;
            this.backgroundColor = backgroundColor;
            this.titleColor = titleColor;
        }
    }

    private class DTableCellRender extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            l.setText(i18n.format(value.toString(), language));
            return l;
        }
    }

    private class DTableModel extends DefaultTableModel{

        public DTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return editable;
        }
    }
}
