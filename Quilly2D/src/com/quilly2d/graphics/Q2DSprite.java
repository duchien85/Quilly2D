package com.quilly2d.graphics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Q2DSprite implements Comparable<Q2DSprite>
{
	public static final String		PROPERTY_NAME_X			= "spriteX";
	public static final String		PROPERTY_NAME_Y			= "spriteY";
	public static final String		PROPERTY_NAME_WIDTH		= "spriteWidth";
	public static final String		PROPERTY_NAME_HEIGHT	= "spriteHeight";

	private BufferedImage			img;
	private final int				imgWidth;
	private final int				imgHeight;
	private int						numColumns				= 1;
	private int						numRows					= 1;
	private double					animationsPerSecond		= 0;
	private double					currentAnimation		= 0;
	private int						animationStartRow		= 0;
	private int						animationStartColumn	= 0;
	private int						animationEndRow			= 0;
	private int						animationEndColumn		= 0;
	private double					currentRow				= 0.0;
	private double					currentColumn			= 0.0;
	private int						frameWidth				= 0;
	private int						frameHeight				= 0;
	private float					transparency			= 1.0f;
	protected Rectangle				boundaries				= null;
	private int						layer					= 0;
	private boolean					isFlippedHorizontal		= false;
	private boolean					isAnimationStopped		= false;
	private boolean					hasCollisionDetection	= false;
	private boolean					isHidden				= false;
	private PropertyChangeSupport	propChangeSupport		= new PropertyChangeSupport(this);
	protected boolean				isRemovable				= false;

	public Q2DSprite(BufferedImage img, int width, int height, int numColumns, int numRows, double animationsPerSecond, int layer)
	{
		this.img = img;

		imgWidth = width;
		imgHeight = height;
		frameWidth = imgWidth / numColumns;
		frameHeight = imgHeight / numRows;
		boundaries = new Rectangle(0, 0, frameWidth, frameHeight);
		this.layer = layer;
		this.numColumns = numColumns;
		this.numRows = numRows;
		this.animationsPerSecond = 1.0 / animationsPerSecond;
		animationStartRow = animationStartColumn = 0;
		animationEndRow = animationEndColumn = (numColumns * numRows) - 1;
	}

	public void update(double deltaTime)
	{
		if (isRemovable)
			return;

		if (img != null && (numRows > 1 || numColumns > 1) && animationsPerSecond > 0 && !isAnimationStopped)
		{
			currentAnimation += deltaTime;
			if (currentAnimation < 0)
				currentAnimation = 0;
			if (currentAnimation >= animationsPerSecond)
			{
				if (currentColumn >= animationEndColumn && currentRow >= animationEndRow)
				{
					currentColumn = animationStartColumn;
					currentRow = animationStartRow;
				}
				else
				{
					++currentColumn;
					if (currentColumn >= numColumns)
					{
						currentColumn = 0;
						++currentRow;
						if (currentRow >= numRows)
						{
							currentRow = 0;
						}
					}
				}

				currentAnimation = 0;
			}
		}
	}

	public void paint(Graphics2D graphics, int offsetX, int offsetY)
	{
		final int sourceX = (int) (currentColumn * frameWidth);
		final int sourceY = (int) (currentRow * frameHeight);

		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getTransparency()));
		if (isFlippedHorizontal)
			graphics.drawImage(img, boundaries.x - offsetX, boundaries.y - offsetY, boundaries.x + boundaries.width - offsetX, boundaries.y + boundaries.height - offsetY, sourceX + frameWidth, sourceY, sourceX, sourceY + frameHeight, null);
		else
			graphics.drawImage(img, boundaries.x - offsetX, boundaries.y - offsetY, boundaries.x + boundaries.width - offsetX, boundaries.y + boundaries.height - offsetY, sourceX, sourceY, sourceX + frameWidth, sourceY + frameHeight, null);
	}

	public void remove()
	{
		isRemovable = true;
	}

	public boolean isRemovable()
	{
		return isRemovable;
	}

	public void setLocation(int newX, int newY)
	{
		propChangeSupport.firePropertyChange(PROPERTY_NAME_X, boundaries.x, newX);
		propChangeSupport.firePropertyChange(PROPERTY_NAME_Y, boundaries.y, newY);
		boundaries.x = newX;
		boundaries.y = newY;
	}

	public void setSize(int newWidth, int newHeight)
	{
		propChangeSupport.firePropertyChange(PROPERTY_NAME_WIDTH, boundaries.width, newWidth);
		propChangeSupport.firePropertyChange(PROPERTY_NAME_HEIGHT, boundaries.height, newHeight);
		boundaries.width = newWidth;
		boundaries.height = newHeight;
	}

	public int getX()
	{
		return boundaries.x;
	}

	public int getY()
	{
		return boundaries.y;
	}

	public double getCenterX()
	{
		return boundaries.getCenterX();
	}

	public double getCenterY()
	{
		return boundaries.getCenterY();
	}

	public int getImageWidth()
	{
		return imgWidth;
	}

	public int getImageHeight()
	{
		return imgHeight;
	}

	public int getWidth()
	{
		return boundaries.width;
	}

	public float getTransparency()
	{
		return transparency;
	}

	public void setTransparency(float transparency)
	{
		this.transparency = transparency;
	}

	public int getHeight()
	{
		return boundaries.height;
	}

	public int getLayer()
	{
		return layer;
	}

	public BufferedImage getImage()
	{
		return img;
	}

	public void setFlippedHorizontal(boolean flipHorizontal)
	{
		isFlippedHorizontal = flipHorizontal;
	}

	public void hide()
	{
		isHidden = true;
	}

	public void show()
	{
		isHidden = false;
	}

	public boolean isHidden()
	{
		return isHidden;
	}

	public void setAnimation(int animation)
	{
		animationStartRow = animation / numColumns;
		animationStartColumn = animation % numColumns;

		animationEndRow = animationStartRow;
		animationEndColumn = animationStartColumn;

		currentRow = animationStartRow;
		currentColumn = animationStartColumn;
	}

	public void setAnimationIndex(double indexX, double indexY)
	{
		currentRow = indexY;
		currentColumn = indexX;
	}

	public void setAnimationFrameSize(int width, int height)
	{
		frameWidth = width;
		frameHeight = height;
	}

	public void setLoopAnimations(int startAnimation, int endAnimation)
	{
		int newAnimationStartRow = startAnimation / numColumns;
		int newAnimationStartColumn = startAnimation % numColumns;

		int newAnimationEndRow = endAnimation / numColumns;
		int newAnimationEndColumn = endAnimation % numColumns;

		if (newAnimationStartRow == animationStartRow && newAnimationStartColumn == animationStartColumn && newAnimationEndRow == animationEndRow && newAnimationEndColumn == animationEndColumn)
			return;

		animationStartRow = newAnimationStartRow;
		animationStartColumn = newAnimationStartColumn;

		animationEndRow = newAnimationEndRow;
		animationEndColumn = newAnimationEndColumn;

		currentRow = animationStartRow;
		currentColumn = animationStartColumn;
	}

	public void stopAnimation()
	{
		isAnimationStopped = true;
	}

	public void startAnimation()
	{
		isAnimationStopped = false;
	}

	public boolean isAnimationStarted()
	{
		return !isAnimationStopped;
	}

	public void addMoveListener(PropertyChangeListener listener)
	{
		propChangeSupport.addPropertyChangeListener(PROPERTY_NAME_X, listener);
		propChangeSupport.addPropertyChangeListener(PROPERTY_NAME_Y, listener);

		propChangeSupport.addPropertyChangeListener(PROPERTY_NAME_WIDTH, listener);
		propChangeSupport.addPropertyChangeListener(PROPERTY_NAME_HEIGHT, listener);
	}

	public void removeMoveListener(PropertyChangeListener listener)
	{
		propChangeSupport.removePropertyChangeListener(PROPERTY_NAME_X, listener);
		propChangeSupport.removePropertyChangeListener(PROPERTY_NAME_Y, listener);

		propChangeSupport.removePropertyChangeListener(PROPERTY_NAME_WIDTH, listener);
		propChangeSupport.removePropertyChangeListener(PROPERTY_NAME_HEIGHT, listener);
	}

	public void enableCollision()
	{
		hasCollisionDetection = true;
	}

	public void disableCollision()
	{
		hasCollisionDetection = false;
	}

	public boolean isInRange(Q2DSprite otherSprite, int range)
	{
		double distX = otherSprite.getCenterX() - boundaries.getCenterX();
		double distY = otherSprite.getCenterY() - boundaries.getCenterY();

		range *= range;
		return (distX * distX + distY * distY) <= range;
	}

	public boolean isLocationInside(int locX, int locY)
	{
		return boundaries.contains(locX, locY);
	}

	public boolean hasCollisionDetection()
	{
		return hasCollisionDetection;
	}

	public boolean isColliding(Q2DSprite otherSprite)
	{
		return boundaries.intersects(otherSprite.boundaries);
	}

	// TODO pixelperfect collision
	public boolean isColliding(Q2DSprite otherSprite, boolean pixelPerfect)
	{
		boolean result = boundaries.intersects(otherSprite.boundaries);
		if (result && pixelPerfect)
			return pixelPerfectCollision(otherSprite);
		return result;
	}

	private boolean pixelPerfectCollision(Q2DSprite otherSprite)
	{
		int leftX = Math.max(boundaries.x, otherSprite.getX());
		int rightX = Math.min(boundaries.x + boundaries.width, otherSprite.getX() + otherSprite.getWidth());

		int topY = Math.max(boundaries.y, otherSprite.getY());
		int bottomY = Math.min(boundaries.y + boundaries.height, otherSprite.getY() + otherSprite.getHeight());

		// Determine the width and height of the collision rectangle
		int width = rightX - leftX;
		int height = bottomY - topY;

		// arrays to hold the pixels
		int[] pixels1 = new int[width * height];
		int[] pixels2 = new int[width * height];

		try
		{
			// Grab the pixels
			PixelGrabber pg1 = new PixelGrabber(img, leftX - boundaries.x, topY - boundaries.y, width, height, pixels1, 0, width);
			PixelGrabber pg2 = new PixelGrabber(otherSprite.getImage(), leftX - otherSprite.getX(), topY - otherSprite.getY(), width, height, pixels2, 0, width);

			pg1.grabPixels();
			pg2.grabPixels();

			// Check if pixels at the same spot from both arrays are not transparent.
			for (int i = 0; i < pixels1.length; i++)
			{
				int a = (pixels1[i] >>> 24) & 0xff;
				int a2 = (pixels2[i] >>> 24) & 0xff;

				// found two pixels in the same spot that aren't completely transparent -> sprites are colliding!
				if (a > 0 && a2 > 0)
					return true;

			}
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}

		return false;
	}

	@Override
	public int compareTo(Q2DSprite param)
	{
		Integer paramY = param.getY() + param.getHeight();
		Integer y = boundaries.y + boundaries.height;

		return y.compareTo(paramY);
	}
}
