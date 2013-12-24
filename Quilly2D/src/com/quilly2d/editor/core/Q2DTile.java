package com.quilly2d.editor.core;

public class Q2DTile
{
	private int		indexX				= 0;
	private int		indexY				= 0;
	private int		layer				= 0;
	private int		tileIndex			= -1;
	private int		tileIndexX			= -1;
	private int		tileIndexY			= -1;
	private boolean	hasCollision		= false;
	private boolean	hasAnimation		= false;
	// attributes below are only used for animated tiles
	private String	animationSpritePath	= null;
	private int		animationsPerSecond	= 0;
	private int		frameWidth			= 0;
	private int		frameHeight			= 0;
	private int		numColumns			= 0;
	private int		numRows				= 0;

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

	public int getTileIndexX()
	{
		return tileIndexX;
	}

	public void setTileIndexX(int tileIndexX)
	{
		this.tileIndexX = tileIndexX;
	}

	public int getTileIndexY()
	{
		return tileIndexY;
	}

	public void setTileIndexY(int tileIndexY)
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

	public int getFrameWidth()
	{
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth)
	{
		this.frameWidth = frameWidth;
	}

	public int getFrameHeight()
	{
		return frameHeight;
	}

	public void setFrameHeight(int frameHeight)
	{
		this.frameHeight = frameHeight;
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
