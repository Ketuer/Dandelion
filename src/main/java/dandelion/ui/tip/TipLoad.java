package dandelion.ui.tip;

import dandelion.ui.component.DLabel;
import dandelion.ui.component.DProgress;
import dandelion.ui.gui.Gui;
import dandelion.ui.lang.LanguageSwitch;
import dandelion.ui.lang.Text;

import java.awt.*;

public class TipLoad extends Tip implements Loading, LanguageSwitch {

    DProgress progress = new DProgress(270, 5);
    DLabel title = new DLabel("正在载入中...");

    public TipLoad(Gui parent) {
        super(parent, 300, 50, false);
        this.addComponent(title, 10);
        this.addComponent(progress, 35);
    }

    @Override
    public void start() {
        this.display();
    }

    @Override
    public void end() {
        this.close();
    }

    @Override
    public void setFont(Font f) {
        super.setFont(f);
        title.setFont(f);
        this.adjustLocation(title, title.getY());
    }

    @Override
    public void updateState(Text text, double value) {
        progress.setValue(value);
        title.setText(text);
        this.adjustLocation(title, title.getY());
    }

    @Override
    public void switchLanguage(String language) {
        title.switchLanguage(language);
        this.adjustLocation(title, title.getY());
    }
}
