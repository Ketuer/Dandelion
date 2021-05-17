package dandelion.ui.lang;

/**
 * 语言切换，实现此接口的类都支持进行实时语言切换，切换语言
 * 会使得界面内所有的文字内容发生相应变化，只要实现此接口的
 * Gui或组件，都能进行语言切换。
 *
 * @author Ketuer
 * @since 1.0
 */
public interface LanguageSwitch {

    void switchLanguage(String language);
}
