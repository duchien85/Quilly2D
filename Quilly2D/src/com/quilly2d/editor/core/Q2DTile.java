package com.quilly2d.editor.core;

public class Q2DTile
{
	private int		indexX				= 0;
	private int		indexY				= 0;
	private int		layer				= 0;
	private int		tileIndex			= -1;
	private double	tileIndexX			= -1;
	private double	tileIndexY			= -1;
	private boolean	hasCollision		= false;
	private boolean	hasAnimation		= false;
	// attributes below are only used for animated tiles
	private String	animationSpritePath	= null;
	private int		animationsPerSecond	= 0;
	private int		width				= 0;
	private int		height				= 0;
	private int		numColumns			= 0;
	private int		numRows				= 0;

	public Q2DTile()
	{

	}

	public Q2DTile(Q2DTile toCopy)
	{
		indexX = toCopy.indexX;
		indexY = toCopy.indexY;
		layer = toCopy.layer;
		tileIndex = toCopy.tileIndex;
		tileIndexX = toCopy.tileIndexX;
		tileIndexY = toCopy.tileIndexY;
		hasCollision = toCopy.hasCollision;
		hasAnimation = toCopy.hasAnimation;
		animationSpritePath = toCopy.animationSpritePath;
		animationsPerSecond = toCopy.animationsPerSecond;
		width = toCopy.width;
		height = toCopy.height;
		numColumns = toCopy.numColumns;
		numRows = toCopy.numRows;
	}

	public int getIndexX()
	{
		return indexX;
	}

	public void setIndexX(int indexX)
	{
		this.indexX = indexX;
	}

	public int getIndexY()
	{
		return indexY;
	}

	public void setIndexY(int indexY)
	{
		this.indexY = indexY;
	}

	public int getLayer()
	{
		return layer;
	}

	public void setLayer(int layer)
	{
		this.layer = layer;
	}

	public int getTileIndex()
	{
		return tileIndex;
	}

	public void setTileIndex(int tileIndex)
	{
		this.tileIndex = tileIndex;
	}

	public double getTileIndexX()
	{
		return tileIndexX;
	}

	public void setTileIndexX(double tileIndexX)
	{
		this.tileIndexX = tileIndexX;
	}

	public double getTileIndexY()
	{
		return tileIndexY;
	}

	public void setTileIndexY(double tileIndexY)
	{
		this.tileIndexY = tileIndexY;
	}

	public boolean isHasCollision()
	{
		return hasCollision;
	}

	public void setHasCollision(boolean hasCollision)
	{
		this.hasCollision = hasCollision;
	}

	public boolean isHasAnimation()
	{
		return hasAnimation;
	}

	public void setHasAnimation(boolean hasAnimation)
	{
		this.hasAnimation = hasAnimation;
	}

	public String getAnimationSpritePath()
	{
		return animationSpritePath;
	}

	public void setAnimationSpritePath(String animationSpritePath)
	{
		this.animationSpritePath = animationSpritePath;
	}

	public int getAnimationsPerSecond()
	{
		return animationsPerSecond;
	}

	public void setAnimationsPerSecond(int animationsPerSecond)
	{
		this.animationsPerSecond = animationsPerSecond;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int frameWidth)
	{
		this.width = frameWidth;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int frameHeight)
	{
		this.height = frameHeight;
	}

	public int getNumColumns()
	{
		return numColumns;
	}

	public void setNumColumns(int numColumns)
	{
		this.numColumns = numColumns;
	}

	public int getNumRows()
	{
		return numRows;
	}

	public void setNumRows(int numRows)
	{
		this.numRows = numRows;
	}
}
