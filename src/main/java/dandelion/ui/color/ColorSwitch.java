package dandelion.ui.color;

import java.awt.*;

/**
 * 实现此接口的组件或是Gui都支持颜色切换，只需要告诉实现此接口的类
 * 要转换的配色方案，对应的Gui或组件会自动进行颜色转换，本接口默认
 * 提供亮色和暗色两种配色方案。
 *
 * @author Ketuer
 * @since 1.0
 */
public interface ColorSwitch {

    ColorConfig LIGHT = new ColorConfig("Light", new Color(240, 240, 240));
    ColorConfig DARK = new ColorConfig("Dark", new Color(19, 19, 19));

    void switchColor(ColorConfig config);
}
