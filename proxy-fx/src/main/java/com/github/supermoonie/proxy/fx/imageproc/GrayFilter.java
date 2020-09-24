package com.github.supermoonie.proxy.fx.imageproc;

import com.github.supermoonie.proxy.fx.util.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * @author supermoonie
 * @date 2018/10/9 17:41
 */
public class GrayFilter extends AbstractBufferedImageOp {

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        int width = src.getWidth();
        int height = src.getHeight();
        if (dest == null) {
            dest = createCompatibleDestImage(src, null);
        }
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        ImageUtil.getRGB(src, 0, 0, width, height, inPixels);
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                index = row * width + col;
                int a = (inPixels[index] >> 24) & 0xff;
                int r = (inPixels[index] >> 16) & 0xff;
                int g = (inPixels[index] >> 8) & 0xff;
                int b = inPixels[index] & 0xff;
                int gray = ImageUtil.saturateCast((int) (0.299 * r + 0.587 * g + 0.114 * b));
                outPixels[index] = (a << 24) | ((gray & 0xff) << 16)
                        | ((gray & 0xff) << 8) | gray & 0xff;
            }
        }
        ImageUtil.setRGB(dest, 0, 0, width, height, outPixels);
        return dest;
    }
}
