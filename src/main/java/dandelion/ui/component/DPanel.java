package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;
import dandelion.ui.lang.i18n;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 改进的JPanel组件，支持多语言和颜色切换，采用圆角样式。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DPanel extends JPanel implements LanguageSwitch, ColorSwitch, RoundBorder {
    private int arc = 15;
    private boolean paintBackground = true;
    private ColorConfig selectColorConfig = ColorSwitch.LIGHT;
    private String language = i18n.getDefaultLanguage();
    private Color borderColor;
    private final Map<String, PanelColorConfig> colorConfigMap = new HashMap<>();
    private Text rawText;

    public DPanel(){
        this("");
    }

    public DPanel(String title){
        this(new Text(title));
    }

    public DPanel(Text title){
        this.rawText = title;
        this.setName(i18n.getDefaultLanguage());
        this.setLayout(null);
        PanelColorConfig def = new PanelColorConfig(new Color(234, 234, 234), new Color(222, 222, 222));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new PanelColorConfig(new Color(24, 24, 24), new Color(69, 69, 69)));
        this.borderColor = def.borderColor;
        this.setBackground(def.backgroundColor);
        this.setBorder(null);
    }

    public DPanel(String title, int width, int height){
        this(title);
        this.setSize(width, height);
    }

    public void setPaintBackground(boolean paintBackground) {
        this.paintBackground = paintBackground;
    }

    /**
     * 推荐采用此方法设置名称，会自动进行语言切换。
     * @param name 名称
     */
    public void setName(Text name) {
        this.rawText = name;
        super.setName(i18n.format(name, language));
    }

    @Override
    public void switchLanguage(String language) {
        this.language = language;
        for (Component component : this.getComponents()) {
            if(component instanceof LanguageSwitch){
                LanguageSwitch c = (LanguageSwitch) component;
                c.switchLanguage(language);
            }
        }
        this.setName(i18n.format(rawText, language));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(paintBackground){
            g2d.setColor(borderColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2d.setColor(getBackground());
            g2d.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);
        }
        super.paintChildren(g);
    }

    public void add(Component component, int x, int y){
        component.setLocation(x, y);
        if(component instanceof ColorSwitch) ((ColorSwitch) component).switchColor(selectColorConfig);
        if(component instanceof LanguageSwitch) ((LanguageSwitch) component).switchLanguage(language);
        this.add(component);
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    @Override
    public void switchColor(ColorConfig config) {
        this.selectColorConfig = config;
        PanelColorConfig panelColorConfig = colorConfigMap.get(config.getName());
        if(panelColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.setBackground(panelColorConfig.backgroundColor);
        this.borderColor = panelColorConfig.borderColor;
        for(Component component : this.getComponents()){
            if(component instanceof ColorSwitch){
                ColorSwitch reverse = (ColorSwitch) component;
                reverse.switchColor(config);
            }
        }
    }

    public void registerColorConfig(ColorConfig config, PanelColorConfig panelColorConfig){
        this.colorConfigMap.put(config.getName(), panelColorConfig);
    }

    public static class PanelColorConfig{
        Color backgroundColor;
        Color borderColor;

        public PanelColorConfig(Color backgroundColor, Color borderColor) {
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
        }
    }
}
