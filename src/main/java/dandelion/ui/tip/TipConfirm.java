package dandelion.ui.tip;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.component.DButton;
import dandelion.ui.component.DLabel;
import dandelion.ui.component.DScroll;
import dandelion.ui.gui.Gui;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;

import java.awt.*;

/**
 * 确认面板，需要进行确认后才会自动关闭。适用于通知提醒，公告显示等场景。
 * 支持多语言和颜色转换，你可以手动显示此提示或是使用Gui中的方法显示。
 *
 * @author Ketuer
 * @since 1.0
 */
public class TipConfirm extends Tip implements LanguageSwitch, ColorSwitch {

    private final DScroll message;
    public final DButton button;

    public TipConfirm(Gui parent, String text, String confirmText, int width, int height) {
        this(parent, new Text(text), confirmText, width, height);
    }

    public TipConfirm(Gui parent, Text text, String confirmText, int width, int height) {
        super(parent, width, height, true);
        message = new DScroll(width - 10, height - 40, new DLabel(text, width - 35));
        this.addComponent(message, 5);

        button = new DButton(confirmText, 60, 25, e -> close());
        button.registerColorConfig(ColorSwitch.LIGHT, new DButton.ButtonColorConfig(
                new Color(61, 137, 229), new Color(231, 231, 231),
                new Color(226, 226, 226), Color.black, new Color(222, 222, 222)
        ));
        this.addComponent(button, height - 30);
    }

    @Override
    public void switchLanguage(String local) {
        message.switchLanguage(local);
        button.switchLanguage(local);
    }

    @Override
    public void switchColor(ColorConfig config) {
        super.switchColor(config);
        this.message.switchColor(config);
    }

    /**
     * 调用此方法时，会暂时等待本窗口关闭，只有用户点击了确认之后，
     * 才能回到父界面进行操作。
     */
    @Override
    public void display() {
        super.display();
    }
}
