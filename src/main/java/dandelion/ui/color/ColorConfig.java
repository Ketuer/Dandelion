package dandelion.ui.color;

import java.awt.*;
import java.util.Objects;

/**
 * 配色方案，告诉对应的ColorSwitch实现类即可实现颜色切换。
 * 需要对应的实现类支持对应的配色方案，不同类型的颜色配置名
 * 称不允许相同！
 */
public class ColorConfig {
    private final String name;
    private final Color background;

    public ColorConfig(String name, Color background){
        if(name == null || background == null)
            throw new NullPointerException("颜色配置名称和颜色不能为空！");
        this.name = name;
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public Color getBackground() {
        return background;
    }

    boolean is(String name){
        return this.name.equals(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorConfig that = (ColorConfig) o;
        return name.equals(that.name);
    }
}
