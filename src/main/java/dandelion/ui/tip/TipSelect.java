package dandelion.ui.tip;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;
import dandelion.ui.component.DButton;
import dandelion.ui.component.DLabel;
import dandelion.ui.component.DScroll;
import dandelion.ui.gui.Gui;
import dandelion.ui.lang.LanguageSwitch;
import java.awt.*;

public class TipSelect extends Tip implements LanguageSwitch, ColorSwitch {

    private final DScroll message;
    private int result;
    public TipSelect(Gui parent, int width, int height, String text, DButton... buttons) {
        super(parent, width, height, true);
        message = new DScroll(width - 10, height - 40, new DLabel(text, width - 35));
        this.addComponent(message, 5);

        int x = (width - buttons.length*65 + 5)/2;
        for (int i = 0; i < buttons.length; i++) {
            int index = i;
            buttons[i].addActionListener(e -> {
                result = index;
                close();
            });
            buttons[i].setSize(60, 25);
            this.addComponent(buttons[i], x + 65*i, height - 30);
        }

        this.getContentPane().setBackground(new Color(238,238,238, 240));
    }

    @Override
    public void switchLanguage(String local) {
        for (Component c : this.getContentPane().getComponents()){
            if(c instanceof LanguageSwitch) ((LanguageSwitch) c).switchLanguage(local);
        }
        message.switchLanguage(local);
    }

    @Override
    public void switchColor(ColorConfig config) {
        super.switchColor(config);
        for (Component c : this.getContentPane().getComponents()){
            if(c instanceof ColorSwitch) ((ColorSwitch) c).switchColor(config);
        }
        this.message.switchColor(config);
    }

    /**
     * 在用户选择出一个结果之前，不会返回结果。
     * @return 选择的按钮序号（添加顺序）
     */
    public int displayAndGetResult(){
        this.display();
        return result;
    }
}
