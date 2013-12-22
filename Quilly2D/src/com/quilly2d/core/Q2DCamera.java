package com.quilly2d.core;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.quilly2d.graphics.Q2DSprite;

public class Q2DCamera implements PropertyChangeListener
{
	private Rectangle	boundaries		= null;
	private double		currentX		= 0.0;
	private double		currentY		= 0.0;
	private int			radiusX			= 0;
	private int			radiusY			= 0;
	private int			maxWidth		= 0;
	private int			maxHeight		= 0;
	private double		translationX	= 0.0;
	private double		translationY	= 0.0;
	private Q2DEntity	focusedEntity	= null;

	public Q2DCamera(int posX, int posY, int windowWidth, int windowHeight, int maxWidth, int maxHeight)
	{
		boundaries = new Rectangle(posX, posY, windowWidth, windowHeight);
		radiusX = windowWidth / 2;
		radiusY = windowHeight / 2;
		this.maxWidth = maxWidth - windowWidth;
		this.maxHeight = maxHeight - windowHeight;
	}

	public boolean isSpriteVisible(Q2DSprite sprite)
	{
		if (!sprite.isHidden())
			return boundaries.intersects(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		return false;
	}

	public void setLocation(int newX, int newY)
	{
		if (newX < 0)
			newX = 0;
		else if (newX > maxWidth)
			newX = maxWidth;

		if (newY < 0)
			newY = 0;
		else if (newY > maxHeight)
			newY = maxHeight;

		boundaries.x = newX;
		boundaries.y = newY;
	}

	public int getX()
	{
		return boundaries.x;
	}

	public int getY()
	{
		return boundaries.y;
	}

	public void setMaximumBoundaries(int maxWidth, int maxHeight)
	{
		this.maxWidth = maxWidth - boundaries.width;
		this.maxHeight = maxHeight - boundaries.height;
	}

	public void setFocusOnEntity(Q2DEntity entity, boolean focus)
	{
		if (focus)
		{
			translationX = 0;
			translationY = 0;
			setLocation(entity.getX() - radiusX, entity.getY() - radiusY);
			focusedEntity = entity;
			entity.addMoveListener(this);
		}
		else
		{
			focusedEntity = null;
			entity.removeMoveListener(this);
		}
	}

	public void pan(int newX, int newY, double duration)
	{
		if (newX < 0)
			newX = 0;
		else if (newX > maxWidth)
			newX = maxWidth;

		if (newY < 0)
			newY = 0;
		else if (newY > maxHeight)
			newY = maxHeight;

		int distX = newX - boundaries.x;
		int distY = newY - boundaries.y;
		double dist = Math.sqrt(distX * distX + distY * distY);
		double direction = Math.atan2(distY, distX);
		double velocity = (int) (dist / duration);

		translationX = velocity * Math.cos(direction);
		translationY = velocity * Math.sin(direction);

		currentX = boundaries.x;
		currentY = boundaries.y;
	}

	public void update(double deltaTime)
	{
		if (translationX != 0 || translationY != 0)
		{
			currentX = currentX + translationX * deltaTime;
			currentY = currentY + translationY * deltaTime;

			setLocation((int) currentX, (int) currentY);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		final String propertyName = event.getPropertyName();

		if (Q2DSprite.PROPERTY_NAME_X.equals(propertyName) || Q2DSprite.PROPERTY_NAME_Y.equals(propertyName))
		{
			setLocation(focusedEntity.getX() - radiusX, focusedEntity.getY() - radiusY);
		}
	}
}
