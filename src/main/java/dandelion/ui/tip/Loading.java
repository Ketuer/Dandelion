package dandelion.ui.tip;

import dandelion.ui.lang.Text;

/**
 * 载入接口，实现此接口来使得Tip可以作为载入界面使用。
 */
public interface Loading {
    void start();
    void end();
    void updateState(Text text, double value);
}
