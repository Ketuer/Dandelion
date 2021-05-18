package dandelion.ui.component;

import dandelion.ui.color.ColorConfig;
import dandelion.ui.color.ColorSwitch;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * 全新编写的Image组件，支持显示图片，同时支持颜色转换，默认在深色模式下
 * 图片会变得稍暗一些。它还支持圆角显示，你可以为其设置圆角大小。
 *
 * @author Ketuer
 * @since 1.0
 */
public class DImage extends JComponent implements RoundBorder, ColorSwitch {
    private Image image;
    private final int u, v, uWidth, vHeight;
    private int arc = 0;
    private Color borderColor;
    private boolean paintBorder = false;
    private Color maskColor;
    private boolean paintMask = true;
    private final Map<String, ImageColorConfig> colorConfigMap = new HashMap<>();

    /**
     * 对图片按照width和height进行缩放显示
     * @param width 宽度
     * @param height 高度
     * @param image 图片
     */
    public DImage(int width, int height, Image image){
        this(0, 0, image.getWidth(null), image.getHeight(null), width, height, image);
    }

    /**
     * 对原图片进行处理并缩放显示。
     * @param u 源图片的x顶点
     * @param v 源图片的y顶点
     * @param uWidth 源图片的裁剪宽度
     * @param vHeight 源图片的裁剪高度
     * @param width 缩放宽度
     * @param height 缩放高度
     * @param image 图片
     */
    public DImage(int u, int v, int uWidth, int vHeight, int width, int height, Image image){
        this.image = image;
        this.setSize(width, height);
        this.u = u;
        this.v = v;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        ImageColorConfig def = new ImageColorConfig(Color.lightGray, new Color(255, 255, 255,0));
        this.registerColorConfig(ColorSwitch.LIGHT, def);
        this.registerColorConfig(ColorSwitch.DARK,
                new ImageColorConfig(Color.darkGray, new Color(1,1,1, 40)));
        this.borderColor = def.borderColor;
        this.maskColor = def.maskColor;
        this.setUI(new DImageUI());
        this.setOpaque(false);
        this.setBackground(new Color(0,0,0,0));
    }

    public void setImage(Image image){
        this.image = image;
        this.repaint();
    }

    public void setPaintBorder(boolean paintBorder) {
        this.paintBorder = paintBorder;
        this.repaint();
    }

    /**
     * 色彩遮罩层，默认在深色模式下会使图片稍稍变暗一些。
     * @param paintMask 遮罩
     */
    public void setPaintMask(boolean paintMask) {
        this.paintMask = paintMask;
        this.repaint();
    }

    @Override
    public void setArc(int arc) {
        this.arc = arc;
    }

    public void registerColorConfig(ColorConfig config, ImageColorConfig imageColorConfig){
        this.colorConfigMap.put(config.getName(), imageColorConfig);
    }

    @Override
    public void switchColor(ColorConfig config) {
        ImageColorConfig imageColorConfig = colorConfigMap.get(config.getName());
        if(imageColorConfig == null)
            throw new UnsupportedOperationException("未发现此配色方案的配置文件，请先为此实例注册配置文件！");
        this.borderColor = imageColorConfig.borderColor;
        this.maskColor = imageColorConfig.maskColor;
        this.repaint();
    }

    public static class ImageColorConfig{
        Color borderColor;
        Color maskColor;

        public ImageColorConfig(Color borderColor, Color maskColor) {
            this.borderColor = borderColor;
            this.maskColor = maskColor;
        }
    }

    private class DImageUI extends ComponentUI{
        Image cans = null;

        /**
         * 防止闪屏现象，采用缓冲+限制区域绘制。
         * @param g 图形
         * @param c 组件
         *
         * @since 1.1
         */
        @Override
        public void paint(Graphics g, JComponent c) {
            if(image != null){
                if(cans == null) cans = new BufferedImage(uWidth, vHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = (Graphics2D) cans.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setClip(new RoundRectangle2D.Float(0, 0, uWidth, vHeight, (float) arc * uWidth/getWidth(), (float) arc * vHeight/getHeight()));
                g2d.drawImage(image, 0, 0,null);
                if(paintMask){
                    g2d.setColor(maskColor);
                    g2d.fillRoundRect(0 ,0, uWidth, vHeight, arc * uWidth/getWidth(), arc * vHeight/getHeight());
                }
                if(paintBorder){
                    g2d.setColor(borderColor);
                    g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawRoundRect(1, 1, uWidth - 2, vHeight - 2, arc * uWidth/getWidth(), arc * vHeight/getHeight());
                }
                g.drawImage(cans, 0, 0, getWidth(), getHeight(), u, v, u+uWidth, v+vHeight,null);
            }
        }
    }
}
