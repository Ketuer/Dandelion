package dandelion.ui.lang;

/**
 * 包含参数替换的文本类型，用于i18n多语言切换。某些组件可以使用
 * 该类型作为参数。
 *
 * @author Ketuer
 * @since 1.0
 */
public class Text {
    String text;
    Object[] objects;

    public Text(String text, Object... objects){
        this.objects = objects;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
