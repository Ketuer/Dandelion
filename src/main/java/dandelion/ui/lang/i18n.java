package dandelion.ui.lang;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 国际化操作类，使用此类来实现国际化，一般只用作内部实现调用，
 * 开发者无需关心此类的工作模式，所有支持语言切换的组件都以此
 * 类作为内部调用实现多语言切换。
 *
 * @author Ketuer
 * @since 1.0
 *
 * @see LanguageSwitch
 */
public class i18n {
    private static String defaultLanguage = "zh_cn";

    public static String getDefaultLanguage(){
        return defaultLanguage;
    }

    public static void setDefaultLanguage(String language){
        defaultLanguage = language;
    }

    public static String format(Text text){
        return format(text.text, defaultLanguage, text.objects);
    }

    public static String format(Text text, String lang){
        return format(text.text, lang, text.objects);
    }

    public static String format(String text, Object... params){
        return format(text, defaultLanguage, params);
    }

    /**
     * 根据本地化文件和对应参数，对字符串进行格式化
     * @param text 待格式化字符串
     * @param params 参数
     * @return 完整内容
     */
    public static String format(String text, String lang, Object... params){
        Locale locale = new Locale(lang);
        try{
            ResourceBundle resourceBundle = ResourceBundle.getBundle("language", locale);
            String str = resourceBundle.getString(text);
            return String.format(str, params);
        }catch (MissingResourceException e){
            return text;
        }
    }
}
