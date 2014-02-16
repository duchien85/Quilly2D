package com.quilly2d.core;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.quilly2d.editor.core.Q2DTile;
import com.quilly2d.editor.core.Q2DWorld;
import com.quilly2d.enums.Q2DGameState;
import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.graphics.Q2DText;
import com.quilly2d.graphics.Q2DTexture;
import com.quilly2d.sound.Q2DSound;

public abstract class Q2DApplication
{
	private JFrame					frame			= null;
	private Q2DMainPanel			mainPanel		= null;
	private Map<String, Q2DTexture>	textureCache	= new HashMap<String, Q2DTexture>();
	private Map<String, Font>		fontCache		= new HashMap<String, Font>();
	private boolean					isFullScreen	= false;
	private int						mouseX			= 0;
	private int						mouseY			= 0;

	public Q2DApplication(String windowTitle, int width, int height, boolean fullscreen, int fps, int numLayers, boolean useTrippleBuffering)
	{
		frame = new JFrame(windowTitle);

		frame.setSize(width, height);
		frame.setIgnoreRepaint(true);
		this.isFullScreen = fullscreen;
		if (fullscreen)
		{
			frame.setUndecorated(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}

		mainPanel = new Q2DMainPanel(this, fps, numLayers, useTrippleBuffering);
		frame.add(mainPanel);

		// center frame
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);
		frame.setResizable(false);

		addKeyEventDispatcher();
		addMouseListener();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Q2DApplication(String windowTitle, int width, int height, boolean fullscreen, int fps, int numLayers)
	{
		this(windowTitle, width, height, fullscreen, fps, numLayers, false);
	}

	public Q2DApplication(String windowTitle, int width, int height, boolean fullscreen, int fps, boolean useTrippleBuffering)
	{
		this(windowTitle, width, height, fullscreen, fps, 4, useTrippleBuffering);
	}

	public Q2DApplication(String windowTitle, int width, int height, boolean fullscreen, int fps)
	{
		this(windowTitle, width, height, fullscreen, fps, 4, false);
	}

	public void setWindowIcon(String iconPath)
	{
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/" + iconPath));
		frame.setIconImage(icon.getImage());
	}

	public boolean isFullscreen()
	{
		return isFullScreen;
	}

	public int getWindowWidth()
	{
		return frame.getWidth();
	}

	public int getWindowHeight()
	{
		return frame.getHeight();
	}

	public void showCursor(boolean show)
	{
		if (show)
			frame.setCursor(Cursor.getDefaultCursor());

		else
			frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(), "trans"));
	}

	public void pauseGame(boolean pause)
	{
		Q2DSound.pauseAllSounds(pause);
		if (pause)
			mainPanel.setGameState(Q2DGameState.PAUSED);
		else
			mainPanel.setGameState(Q2DGameState.RUNNIG);
	}

	public boolean isGamePaused()
	{
		return mainPanel.getGameState() == Q2DGameState.PAUSED;
	}

	public void stopGame()
	{
		//TODO register window close listener and execute this logic always when the window is closing
		mainPanel.setGameState(Q2DGameState.STOPPED);
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	private Q2DTexture getTexture(String filePath)
	{
		if (textureCache.containsKey(filePath))
		{
			return textureCache.get(filePath);
		}
		else
		{
			Q2DTexture texture = new Q2DTexture(filePath);
			textureCache.put(filePath, texture);
			return texture;
		}
	}

	public Q2DSprite createSprite(String filePath, int numColumns, int numRows, double animationsPerSecond, int layer)
	{
		Q2DTexture texture = getTexture(filePath);
		Q2DSprite result = new Q2DSprite(texture, numColumns, numRows, animationsPerSecond, layer);
		mainPanel.addSprite(result, layer);

		return result;
	}

	public Q2DSprite createSprite(String filePath, int numColumns, int numRows, double animationsPerFrame)
	{
		return createSprite(filePath, numColumns, numRows, animationsPerFrame, mainPanel.getMaxLayers() / 2);
	}

	public Q2DSprite createSprite(String filePath, int layer)
	{
		return this.createSprite(filePath, 1, 1, 0, layer);
	}

	public Q2DSprite createSprite(String filePath)
	{
		return this.createSprite(filePath, 1, 1, 0);
	}

	public List<Q2DSprite> getCollisionSprites(Q2DSprite source)
	{
		return mainPanel.getCollisionSprites(source);
	}

	public Q2DSound createSound(String filePath)
	{
		return new Q2DSound(filePath);
	}

	public Q2DText createText(String text, int numCharsPerRow, Font font, Color color)
	{
		Q2DText result = new Q2DText(text, numCharsPerRow, font, color, frame.getGraphics());
		mainPanel.addText(result);

		return result;
	}

	public Q2DText createText(String text, Font font, Color color)
	{
		return this.createText(text, text.length(), font, color);
	}

	public Q2DTimer createTimer(double delayTime, double periodicTime, double endTime, Class<? extends Q2DTimer> type)
	{
		try
		{
			Constructor<? extends Q2DTimer> constructor = type.getConstructor(double.class, double.class, double.class);
			Q2DTimer newInstance = constructor.newInstance(delayTime, periodicTime, endTime);

			mainPanel.addTimer(newInstance);
			return newInstance;
		}
		catch (Exception e)
		{
			// TODO errormsg
			e.printStackTrace();
		}

		return null;
	}

	public Q2DTimer createTimer(double delayTime, Class<? extends Q2DTimer> type)
	{
		return this.createTimer(delayTime, 0, 0, type);
	}

	public Q2DEntity createEntity(String filePath, int numColumns, int numRows, double animationsPerSecond, int layer, Class<? extends Q2DEntity> type)
	{
		try
		{
			Q2DTexture texture = getTexture(filePath);
			Constructor<? extends Q2DEntity> constructor = type.getConstructor(Q2DTexture.class, int.class, int.class, double.class, int.class);
			Q2DEntity newInstance = constructor.newInstance(texture, numColumns, numRows, animationsPerSecond, layer);

			mainPanel.addSprite(newInstance, layer);
			return newInstance;
		}
		catch (Exception e)
		{
			// TODO errormsg
			e.printStackTrace();
		}

		return null;
	}

	public Q2DEntity createEntity(String filePath, int numColumns, int numRows, double animationsPerFrame, Class<? extends Q2DEntity> type)
	{
		return createEntity(filePath, numColumns, numRows, animationsPerFrame, mainPanel.getMaxLayers() / 2, type);
	}

	public Q2DEntity createEntity(String filePath, int layer, Class<? extends Q2DEntity> type)
	{
		return createEntity(filePath, 1, 1, 0, layer, type);
	}

	public Q2DEntity createEntity(String filePath, Class<? extends Q2DEntity> type)
	{
		return createEntity(filePath, 1, 1, 0, type);
	}

	public List<Q2DEntity> getEntitiesInRange(Q2DEntity source, int range)
	{
		return mainPanel.getEntitiesInRange(source, range);
	}

	public Font getFont(String fontName, int size, boolean bold)
	{
		String key = fontName + "_" + size;
		if (bold)
			key += "_bold";

		if (fontCache.containsKey(key))
		{
			return fontCache.get(key);
		}

		Font result = null;
		if (bold)
			result = new Font(fontName, Font.BOLD, size);
		else
			result = new Font(fontName, Font.PLAIN, size);

		fontCache.put(key, result);
		return result;
	}

	public int getMouseX()
	{
		return mouseX;
	}

	public int getMouseY()
	{
		return mouseY;
	}

	public abstract void onInit();

	public abstract void onKeyPressed(int keyCode);

	public abstract void onKeyReleased(int keyCode);

	public abstract void onMousePressed(int mouseButton, int mouseX, int mouseY);

	public abstract void onMouseReleased(int mouseButton, int mouseX, int mouseY);

	private void addKeyEventDispatcher()
	{
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent event)
			{
				if (event.getID() == KeyEvent.KEY_PRESSED)
					onKeyPressed(event.getKeyCode());
				else
					onKeyReleased(event.getKeyCode());

				return true;
			}
		});
	}

	private void addMouseListener()
	{
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event)
			{
				if (event instanceof MouseEvent)
				{
					if (event.getID() == MouseEvent.MOUSE_PRESSED)
						onMousePressed(((MouseEvent) event).getButton(), mouseX, mouseY);
					else if (event.getID() == MouseEvent.MOUSE_RELEASED)
						onMouseReleased(((MouseEvent) event).getButton(), mouseX, mouseY);

				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event)
			{
				if (event instanceof MouseEvent)
				{
					mouseX = (int) (((MouseEvent) event).getX() / mainPanel.getScaleX());
					mouseY = (int) (((MouseEvent) event).getY() / mainPanel.getScaleY());
				}
			}
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	public void setCameraLocation(int newX, int newY)
	{
		mainPanel.setCameraLocation(newX, newY);
	}

	public void panCamera(int newX, int newY, double panDuration)
	{
		mainPanel.panCamera(newX, newY, panDuration);
	}

	public void setCameraMaximumBoundaries(int maxWidth, int maxHeight)
	{
		mainPanel.setCameraMaximumBoundaries(maxWidth, maxHeight);
	}

	public void setCameraFocusOnEntity(Q2DEntity entity, boolean focus)
	{
		mainPanel.setCameraFocusOnEntity(entity, focus);
	}

	public int getCameraX()
	{
		return mainPanel.getCameraX();
	}

	public int getCameraY()
	{
		return mainPanel.getCameraY();
	}

	public void loadQ2DWorld(String filePath)
	{
		try
		{
			InputStream fileIn = this.getClass().getResource("/" + filePath).openStream();
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Q2DWorld loadedWorld = (Q2DWorld) in.readObject();
			in.close();
			fileIn.close();

			setCameraMaximumBoundaries(loadedWorld.getMap().getWidth(), loadedWorld.getMap().getHeight());
			// TODO setMaxLayers zu Q2DApplication hinzufügen -> muss vor mapladen gesetzt werden
			// TODO errormsg, wenn nicht genügend layers für map vorhanden sind
			// TODO load collision correctly
			mainPanel.setMaxLayers(loadedWorld.getMap().getNumLayers());
			Collection<Q2DTile> tiles = loadedWorld.getMap().getTiles();
			Iterator<Q2DTile> iterator = tiles.iterator();
			final int tileSize = loadedWorld.getMap().getTileSize();
			final int max = tiles.size();
			int current = 0;
			while (iterator.hasNext())
			{
				++current;
				System.out.println("Processing: " + current + " / " + max);
				Q2DTile tile = iterator.next();
				Q2DSprite sprite = null;
				if (tile.hasAnimation())
					// TODO test animation sprites
					sprite = createSprite(tile.getAnimationSpritePath(), tile.getNumColumns(), tile.getNumRows(), tile.getAnimationsPerSecond(), tile.getLayer());
				else
				{
					String tileset = loadedWorld.getTileset(tile.getTileIndex());
					sprite = createSprite(tileset, loadedWorld.getNumTilesetColumns(tile.getTileIndex()), loadedWorld.getNumTilesetRows(tile.getTileIndex()), 0.0, tile.getLayer());
					sprite.stopAnimation();
					sprite.setAnimationIndex(tile.getTileIndexX(), tile.getTileIndexY());
					sprite.setAnimationFrameSize(tile.getWidth(), tile.getHeight());
				}
				sprite.setLocation(tile.getIndexX() * tileSize, tile.getIndexY() * tileSize);
				sprite.setSize(tile.getWidth(), tile.getHeight());
			}

			int i = 0;
			String tileset = loadedWorld.getTileset(i);
			while (tileset != null)
			{
				// TODO set alphakey for tilesets
				if (loadedWorld.getTilesetAlphaKey(tileset) != null)
					textureCache.get(tileset).setAlphaColor(loadedWorld.getTilesetAlphaKey(tileset));
				++i;
				tileset = loadedWorld.getTileset(i);
			}

			if (loadedWorld.getBackgroundMusic() != null)
			{
				// TODO stop sound when test editor frame is closed
				Q2DSound bgdsnd = createSound(loadedWorld.getBackgroundMusic());
				bgdsnd.setLoop(true);
				bgdsnd.play();
			}
		}
		catch (Exception e)
		{
			// TODO errormsg
			e.printStackTrace();
		}
	}
}
