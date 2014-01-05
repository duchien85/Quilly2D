package com.quilly2d.core;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;

import com.quilly2d.enums.Q2DGameState;
import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.graphics.Q2DText;

@SuppressWarnings("serial")
public class Q2DMainPanel extends JFXPanel implements Runnable, PropertyChangeListener
{
	private Q2DApplication			parent				= null;
	private long					lastLoopTime		= 0L;
	private int						targetFPS			= 0;
	private long					optimalTime			= 0L;
	private Q2DGameState			gameState			= Q2DGameState.RUNNIG;
	private int						maxLayers			= 3;
	private List<List<Q2DSprite>>	spritesToBeRendered	= null;
	private List<Q2DText>			textsToBeRendered	= null;
	private List<Q2DTimer>			timersToBeUpdated	= null;
	private Q2DCamera				camera				= null;
	private BufferStrategy			bufferStrategy		= null;
	private boolean					useTrippleBuffering	= false;
	private double					scaleX				= 1.0;
	private double					scaleY				= 1.0;

	public Q2DMainPanel(Q2DApplication parent, int fps, int numLayers, boolean useTrippleBuffering)
	{
		this.parent = parent;
		targetFPS = fps;
		optimalTime = 1000000000 / targetFPS;
		maxLayers = numLayers;
		this.useTrippleBuffering = useTrippleBuffering;

		timersToBeUpdated = new ArrayList<Q2DTimer>();
		textsToBeRendered = new ArrayList<Q2DText>();
		spritesToBeRendered = new ArrayList<List<Q2DSprite>>();
		for (int i = 0; i < maxLayers; ++i)
			spritesToBeRendered.add(new ArrayList<Q2DSprite>());
	}

	@Override
	public void addNotify()
	{
		// use this method for initialization purpose
		super.addNotify();

		final int windowWidth = parent.getWindowWidth();
		final int windowHeight = parent.getWindowHeight();

		Canvas canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		this.add(canvas);
		canvas.setSize(windowWidth, windowHeight);
		canvas.setBackground(Color.BLACK);
		if (useTrippleBuffering)
			canvas.createBufferStrategy(3);
		else
			canvas.createBufferStrategy(2);
		bufferStrategy = canvas.getBufferStrategy();

		camera = new Q2DCamera(0, 0, windowWidth, windowHeight, windowWidth, windowHeight);

		Thread updateThread = new Thread(this);
		updateThread.start();

		if (parent.isFullscreen())
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			canvas.setSize(screenSize);
			scaleX = (double) screenSize.width / windowWidth;
			scaleY = (double) screenSize.height / windowHeight;
		}
	}

	private void paint()
	{
		Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();

		// clear buffer
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, getWidth(), getHeight());

		graphics.scale(scaleX, scaleY);

		Iterator<List<Q2DSprite>> spriteListIterator = spritesToBeRendered.iterator();
		while (spriteListIterator.hasNext())
		{
			List<Q2DSprite> spritesList = spriteListIterator.next();
			Iterator<Q2DSprite> spritesIterator = spritesList.iterator();
			while (spritesIterator.hasNext())
			{
				Q2DSprite sprite = spritesIterator.next();
				if (camera.isSpriteVisible(sprite))
					sprite.paint(graphics, camera.getX(), camera.getY());
			}

		}

		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		Iterator<Q2DText> textsIterator = textsToBeRendered.iterator();
		while (textsIterator.hasNext())
		{
			Q2DText text = textsIterator.next();
			if (text.isRemovable())
				textsIterator.remove();
			else
				text.paint(graphics);
		}

		graphics.dispose();
		if (!bufferStrategy.contentsLost())
			bufferStrategy.show();

		// needed for smooth animations on Linux systems
		Toolkit.getDefaultToolkit().sync();
	}

	private void update(double deltaTime)
	{
		camera.update(deltaTime);

		Iterator<List<Q2DSprite>> spriteListIterator = spritesToBeRendered.iterator();
		while (spriteListIterator.hasNext())
		{
			List<Q2DSprite> spritesList = spriteListIterator.next();
			Iterator<Q2DSprite> spritesIterator = spritesList.iterator();
			while (spritesIterator.hasNext())
			{
				Q2DSprite sprite = spritesIterator.next();
				if (sprite.isRemovable())
				{
					if (sprite instanceof Q2DEntity)
						((Q2DEntity) sprite).onRemove();
					spritesIterator.remove();
				}
				else
					sprite.update(deltaTime);
			}
		}

		Iterator<Q2DTimer> timerIterator = timersToBeUpdated.iterator();
		while (timerIterator.hasNext())
		{
			Q2DTimer timer = timerIterator.next();
			if (timer.isFinished())
			{
				timer.onFinish();
				timerIterator.remove();
			}
			else
				timer.update(deltaTime);
		}
	}

	@Override
	public void run()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run()
				{
					// initialize Q2DApplication
					parent.onInit();
				}

			});

			lastLoopTime = System.nanoTime();

			while ((gameState == Q2DGameState.RUNNIG || gameState == Q2DGameState.PAUSED) && isDisplayable())
			{
				long now = System.nanoTime();
				final double delta = (now - lastLoopTime) / 1000000000.0;
				lastLoopTime = now;

				if (gameState != Q2DGameState.PAUSED)
				{
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run()
						{
							update(delta);
							if (isDisplayable())
								paint();
						}
					});
				}

				long sleepTime = (lastLoopTime - System.nanoTime() + optimalTime) / 1000000;
				if (sleepTime <= 0L)
					sleepTime = optimalTime / 1000000;

				Thread.sleep(sleepTime);
			}
		}
		catch (Exception e)
		{
			//TODO errormessage
			e.printStackTrace();
		}
	}

	public void setGameState(Q2DGameState newState)
	{
		gameState = newState;
	}

	public Q2DGameState getGameState()
	{
		return gameState;
	}

	public int getMaxLayers()
	{
		return maxLayers;
	}

	public void addSprite(Q2DSprite sprite, int layer)
	{
		List<Q2DSprite> sprites = spritesToBeRendered.get(layer);
		sprites.add(sprite);

		// add move listener to sort sprites correctly for rendering by their Y-position
		sprite.addMoveListener(this);
		Collections.sort(sprites);
	}

	public List<Q2DSprite> getCollisionSprites(Q2DSprite source)
	{
		List<Q2DSprite> result = new ArrayList<Q2DSprite>();

		List<Q2DSprite> sprites = spritesToBeRendered.get(source.getLayer());

		Iterator<Q2DSprite> spritesIterator = sprites.iterator();
		while (spritesIterator.hasNext())
		{
			Q2DSprite sprite = spritesIterator.next();
			if (sprite != source && sprite.hasCollisionDetection() && source.isColliding(sprite))
				result.add(sprite);
		}

		return result;
	}

	public List<Q2DEntity> getEntitiesInRange(Q2DEntity source, int range)
	{
		List<Q2DEntity> result = new ArrayList<Q2DEntity>();

		Iterator<List<Q2DSprite>> spriteListIterator = spritesToBeRendered.iterator();
		while (spriteListIterator.hasNext())
		{
			List<Q2DSprite> spritesList = spriteListIterator.next();
			Iterator<Q2DSprite> spritesIterator = spritesList.iterator();
			while (spritesIterator.hasNext())
			{
				Q2DSprite sprite = spritesIterator.next();
				if (sprite != source && sprite instanceof Q2DEntity && sprite.isInRange(source, range))
				{
					result.add((Q2DEntity) sprite);
				}
			}
		}

		return result;
	}

	public void addText(Q2DText text)
	{
		textsToBeRendered.add(text);
	}

	public void addTimer(Q2DTimer timer)
	{
		timersToBeUpdated.add(timer);
	}

	public void setCameraLocation(int newX, int newY)
	{
		camera.setLocation(newX, newY);
	}

	public void panCamera(int newX, int newY, double panDuration)
	{
		camera.pan(newX, newY, panDuration);
	}

	public void setCameraMaximumBoundaries(int maxWidth, int maxHeight)
	{
		camera.setMaximumBoundaries(maxWidth, maxHeight);
	}

	public void setCameraFocusOnEntity(Q2DEntity entity, boolean focus)
	{
		camera.setFocusOnEntity(entity, focus);
	}

	public double getScaleX()
	{
		return scaleX;
	}

	public double getScaleY()
	{
		return scaleY;
	}

	public int getCameraX()
	{
		return camera.getX();
	}

	public int getCameraY()
	{
		return camera.getY();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		final String propertyName = event.getPropertyName();

		if (Q2DSprite.PROPERTY_NAME_Y.equals(propertyName) || Q2DSprite.PROPERTY_NAME_HEIGHT.equals(propertyName))
			Collections.sort(spritesToBeRendered.get(((Q2DSprite) event.getSource()).getLayer()));
	}
}
