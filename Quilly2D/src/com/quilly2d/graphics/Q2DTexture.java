package com.quilly2d.graphics;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.swing.ImageIcon;

public class Q2DTexture
{
	private BufferedImage	image	= null;

	public Q2DTexture(String filePath)
	{
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/" + filePath));
		image = getOptimizedImage(icon.getImage());
	}

	public void setAlphaColor(final int alphaRGB)
	{
		ImageFilter filter = new RGBImageFilter() {
			public int	markerRGB	= alphaRGB | 0xFF000000;

			public final int filterRGB(int x, int y, int rgb)
			{
				if ((rgb | 0xFF000000) == markerRGB)
					// make pixel of specified color transparent
					return 0x00FFFFFF & rgb;
				else
					return rgb;
			}
		};

		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		image = getOptimizedImage(Toolkit.getDefaultToolkit().createImage(ip));
	}

	public int getWidth()
	{
		return image.getWidth();
	}

	public int getHeight()
	{
		return image.getHeight();
	}

	public int getRGB(int posX, int posY)
	{
		return image.getRGB(posX, posY);
	}

	public void paint(Graphics2D graphics, int x, int y, int width, int height, int frameX, int frameY, int frameWidth, int frameHeight)
	{
		graphics.drawImage(image, x, y, x + width, y + height, frameX, frameY, frameX + frameWidth, frameY + frameHeight, null);
	}

	private BufferedImage getOptimizedImage(Image img)
	{
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		// check if image is already optimized
		if (img instanceof BufferedImage && ((BufferedImage) img).getColorModel().equals(gfx_config.getColorModel()))
			return (BufferedImage) img;
		else
		{
			BufferedImage new_image = gfx_config.createCompatibleImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) new_image.getGraphics();
			g2.drawImage(img, 0, 0, null);
			g2.dispose();

			return new_image;
		}
	}
}
