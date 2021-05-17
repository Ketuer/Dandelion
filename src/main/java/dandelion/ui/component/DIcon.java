package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持颜色变换的图标，切换颜色时会自动变换当前图标。
 */
public class DIcon implements ColorSwitch, Icon {
    private final Map<String, Image> imageMap = new HashMap<>();
    private ImageObserver observer;
    private Image image;

    public static final int NETWORK = 0;
    public static final int JAR = 1;
    public static final int FILE = 2;

    public DIcon(String path, int type){
        try {
            switch (type){
                case NETWORK:
                    image = ImageIO.read(new URL(path));
                    break;
                case JAR:
                    InputStream stream = this.getClass().getResourceAsStream(path);
                    if(stream != null) image = ImageIO.read(stream);
                    break;
                case FILE:
                    image = ImageIO.read(new File(path));
                    break;
                default:
                    throw new UnsupportedOperationException("错误的路径类型！");
            }
            this.registerColorConfig(ColorSwitch.LIGHT, image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DIcon(Image image){
        this.image = image;
        this.registerColorConfig(ColorSwitch.LIGHT, image);
    }

    public void registerColorConfig(ColorConfig config, String path, int type){
        Image image = null;
        try {
            switch (type){
                case NETWORK:
                    image = ImageIO.read(new URL(path));
                    break;
                case JAR:
                    InputStream stream = this.getClass().getResourceAsStream(path);
                    if(stream != null) image = ImageIO.read(stream);
                    break;
                case FILE:
                    image = ImageIO.read(new File(path));
                    break;
                default:
                    throw new UnsupportedOperationException("错误的路径类型！");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(image != null) this.imageMap.put(config.getName(), image);
    }

    public void registerColorConfig(ColorConfig config, Image image){
        this.imageMap.put(config.getName(), image);
    }

    /**
     * 在没有对应的已注册的颜色变换配置文件时，会自动采用默认配色。
     * @param config 颜色配置
     */
    @Override
    public void switchColor(ColorConfig config) {
        Image image = imageMap.get(config.getName());
        if(image != null) this.image = image;
    }

    public void setObserver(ImageObserver observer) {
        this.observer = observer;
    }

    public ImageObserver getObserver() {
        return observer;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.drawImage(image, x, y, c.getWidth(), c.getHeight(), observer);
    }

    @Override
    public int getIconWidth() {
        return image.getWidth(observer);
    }

    @Override
    public int getIconHeight() {
        return image.getHeight(observer);
    }
}
