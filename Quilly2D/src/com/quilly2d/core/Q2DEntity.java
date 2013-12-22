package com.quilly2d.core;

import java.awt.Image;

import com.quilly2d.graphics.Q2DSprite;

public abstract class Q2DEntity extends Q2DSprite
{
	private double	x				= 0;
	private double	y				= 0;
	private int		velocity		= 0;
	private double	direction		= 0.0;
	private double	translationX	= 0;
	private double	translationY	= 0;

	public Q2DEntity(Image img, int width, int height, int numColumns, int numRows, double animationsPerSecond, int layer)
	{
		super(img, width, height, numColumns, numRows, animationsPerSecond, layer);
	}

	@Override
	public void update(double deltaTime)
	{
		super.update(deltaTime);
		if (isRemovable)
			return;

		beforeUpdate();

		if (velocity != 0)
		{
			x = x + translationX * deltaTime;
			y = y - translationY * deltaTime;

			setLocation((int) x, (int) y);
		}

		afterUpdate();
	}

	public void setVelocity(int newVelocity)
	{
		velocity = newVelocity;
		translationX = velocity * Math.cos(direction);
		translationY = velocity * Math.sin(direction);
	}

	public int getVelocity()
	{
		return velocity;
	}

	public void setDirection(int angle)
	{
		direction = Math.toRadians(angle);
		translationX = velocity * Math.cos(direction);
		translationY = velocity * Math.sin(direction);
	}

	public int getDirection()
	{
		return (int) (180.0 / Math.PI * direction);
	}

	@Override
	public void setLocation(int newX, int newY)
	{
		super.setLocation(newX, newY);
		x = boundaries.x;
		y = boundaries.y;
	}

	abstract public void beforeUpdate();

	abstract public void afterUpdate();

	abstract public void onRemove();
}
