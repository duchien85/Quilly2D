package com.quilly2d.tools;

import java.awt.Dimension;
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.quilly2d.editor.core.Q2DEditorMenubar;
import com.quilly2d.editor.core.Q2DEditorSplitPane;
import com.quilly2d.editor.core.Q2DEditorTestApplication;
import com.quilly2d.editor.core.Q2DPencil;
import com.quilly2d.editor.core.Q2DTile;
import com.quilly2d.editor.core.Q2DTileIndex;
import com.quilly2d.editor.core.Q2DWorld;
import com.quilly2d.editor.enums.Q2DPencilMode;
import com.quilly2d.graphics.Q2DSprite;

public enum Q2DEditor
{
	INSTANCE;

	// editor specific data
	public static final String			PROPERTY_PENCIL_SIZE_X		= "pencilSizeX";
	public static final String			PROPERTY_PENCIL_SIZE_Y		= "pencilSizeY";
	public static final String			PROPERTY_PENCIL_MODE		= "pencilMode";
	public static final String			PROPERTY_PENCIL_TILE_INDEX	= "pencilTileIndex";
	public static final String			PROPERTY_NUM_LAYERS			= "numLayers";
	public static final String			PROPERTY_TILE_SIZE			= "tileSize";
	public static final String			PROPERTY_TILESET_INDEX		= "tilesetIndex";
	public static final String			PROPERTY_MAP_NAME			= "mapName";
	public static final String			PROPERTY_MAP_WIDTH			= "mapWidth";
	public static final String			PROPERTY_MAP_HEIGHT			= "mapHeight";
	public static final String			PROPERTY_TILESET			= "tileset";
	public static final String			PROPERTY_CURRENT_LAYER		= "currentLayer";
	private Q2DEditorSplitPane			splitPane					= null;
	private Map<String, BufferedImage>	imgCache					= new HashMap<String, BufferedImage>();
	private Map<String, Integer>		tilesetAlphaKeys			= new HashMap<String, Integer>();
	private int							currentTileIndex			= 0;
	private int							currentLayer				= 0;
	private Q2DPencil					pencil						= new Q2DPencil();
	private Q2DPencilMode				pencilMode					= Q2DPencilMode.NORMAL;
	private boolean						isFillModeActive			= false;
	private boolean						isGroundTextureModeActive	= false;
	private int							currentPencilIndexX			= -1;
	private int							currentPencilIndexY			= -1;
	private PropertyChangeSupport		propChangeSupport			= new PropertyChangeSupport(this);
	public static final int				MAX_STEPS_TO_BE_REMEMBERED	= 200;
	List<Q2DWorld>						history						= new ArrayList<Q2DWorld>();
	// world specific data
	public static final String			DEFAULT_WORLD_NAME			= "Enter name";
	public static final int				MAX_NUM_LAYERS				= 6;
	public static final int				MAX_MAP_WIDTH				= 500 * 32;
	public static final int				MAX_MAP_HEIGHT				= 500 * 32;
	private final int					DEFAULT_WORLD_WIDTH			= 25 * 32;
	private final int					DEFAULT_WORLD_HEIGHT		= 20 * 32;
	private final int					DEFAULT_WORLD_TILESIZE		= 32;
	private final int					DEFAULT_NUM_LAYERS			= 3;
	private Q2DWorld					world						= null;
	// animation specific data
	public static final int				FRAMES_PER_SECOND			= 50;
	private String						animationSpritePath			= null;
	private int							animationsPerSecond			= 0;
	private int							animationWidth				= 0;
	private int							animationHeight				= 0;
	private int							animationNumColumns			= 0;
	private int							animationNumRows			= 0;
	private Map<String, Q2DSprite>		animations					= new HashMap<String, Q2DSprite>();

	public void initWorld(String name)
	{
		history.clear();
		imgCache.clear();
		tilesetAlphaKeys.clear();
		world = new Q2DWorld(name, DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT, DEFAULT_NUM_LAYERS, DEFAULT_WORLD_TILESIZE);
	}

	public void addNewWorldVersion()
	{
		if (history.size() > MAX_STEPS_TO_BE_REMEMBERED)
			history.remove(0);
		history.add(world);
		world = new Q2DWorld(world);
	}

	public void setPreviousWorldVersion()
	{
		if (history.size() > 0)
		{
			world = history.get(history.size() - 1);
			history.remove(history.size() - 1);
			if (splitPane != null)
				splitPane.repaint();
		}
	}

	public Q2DEditorSplitPane getSplitPane()
	{
		return splitPane;
	}

	public void setSplitPane(Q2DEditorSplitPane splitPane)
	{
		this.splitPane = splitPane;
	}

	public int getCurrentTileSetIndex()
	{
		return currentTileIndex;
	}

	public void setCurrentTileSetIndex(int index)
	{
		propChangeSupport.firePropertyChange(PROPERTY_TILESET_INDEX, currentTileIndex, index);
		currentTileIndex = index;
	}

	public int getCurrentLayer()
	{
		return currentLayer;
	}

	public void setCurrentLayer(int layer)
	{
		propChangeSupport.firePropertyChange(PROPERTY_CURRENT_LAYER, currentLayer, layer);
		currentLayer = layer;
	}

	public Q2DTile getMapTile(int indexX, int indexY, int layer)
	{
		return world.getMap().getTile(indexX, indexY, layer);
	}

	public int getMapWidth()
	{
		return world.getMap().getWidth();
	}

	public void setMapWidth(int width)
	{
		width = new Double(Math.ceil(1.0 * width / getTileSize())).intValue() * getTileSize();
		propChangeSupport.firePropertyChange(PROPERTY_MAP_WIDTH, world.getMap().getWidth(), width);
		world.getMap().setSize(width, world.getMap().getHeight());
	}

	public int getMapHeight()
	{
		return world.getMap().getHeight();
	}

	public void setMapHeight(int height)
	{
		height = new Double(Math.ceil(1.0 * height / getTileSize())).intValue() * getTileSize();
		propChangeSupport.firePropertyChange(PROPERTY_MAP_HEIGHT, world.getMap().getHeight(), height);
		world.getMap().setSize(world.getMap().getWidth(), height);
	}

	public int getTileSize()
	{
		return world.getMap().getTileSize();
	}

	public void setTileSize(int tileSize)
	{
		propChangeSupport.firePropertyChange(PROPERTY_TILE_SIZE, world.getMap().getTileSize(), tileSize);
		world.getMap().setTileSize(tileSize);
	}

	public int getNumLayers()
	{
		return world.getMap().getNumLayers();
	}

	public void setNumLayers(int numLayers)
	{
		propChangeSupport.firePropertyChange(PROPERTY_NUM_LAYERS, world.getMap().getNumLayers(), numLayers);
		world.getMap().setNumLayers(numLayers);
	}

	public String getWorldName()
	{
		return world.getName();
	}

	public void setWorldName(String name)
	{
		world.setName(name);
	}

	public String getWorldBackgroundMusic()
	{
		return world.getBackgroundMusic();
	}

	public void setWorldBackgroundMusic(String musicPath)
	{
		world.setBackgroundMusic(musicPath);
	}

	public String getTileSet(int index)
	{
		return world.getTileset(index);
	}

	public void setTileset(int index, String tileset)
	{
		propChangeSupport.firePropertyChange(PROPERTY_TILESET, index, tileset);
		getImage(tileset);
		if (tilesetAlphaKeys.containsKey(tileset))
			world.setTileset(index, tileset, tilesetAlphaKeys.get(tileset), imgCache.get(tileset).getWidth(), imgCache.get(tileset).getHeight());
		else
			world.setTileset(index, tileset, null, imgCache.get(tileset).getWidth(), imgCache.get(tileset).getHeight());
	}

	private BufferedImage getOptimizedImage(Image img)
	{
		GraphicsConfiguration gfx_config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

		// check if image is already optimized
		if (img instanceof BufferedImage && ((BufferedImage) img).getColorModel().equals(gfx_config.getColorModel()))
		{
			return (BufferedImage) img;
		}
		else
		{
			BufferedImage new_image = gfx_config.createCompatibleImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) new_image.getGraphics();
			g2.drawImage(img, 0, 0, null);
			g2.dispose();

			return new_image;
		}
	}

	public BufferedImage getImage(String filePath)
	{
		if (filePath == null)
			return null;

		if (imgCache.containsKey(filePath))
		{
			return imgCache.get(filePath);
		}
		else
		{
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/" + filePath));
			BufferedImage img = getOptimizedImage(icon.getImage());
			imgCache.put(filePath, img);
			return img;
		}
	}

	private BufferedImage makeColorTransparent(BufferedImage im, final int alphaRGB)
	{
		ImageFilter filter = new RGBImageFilter()
		{
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

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		Image alphaImg = Toolkit.getDefaultToolkit().createImage(ip);
		return getOptimizedImage(alphaImg);
	}

	private void setAlphaColor(String tileset, int rgb)
	{
		if (!imgCache.containsKey(tileset + "_original"))
			imgCache.put(tileset + "_original", getImage(tileset));

		BufferedImage img = makeColorTransparent(getImage(tileset + "_original"), rgb);
		imgCache.put(tileset, img);
		tilesetAlphaKeys.put(tileset, rgb);
		world.setTilesetAlphaKey(tileset, rgb);
		if (splitPane != null)
			splitPane.repaint();
	}

	public void setAlphaColor(String tileset, int posX, int posY)
	{
		if (!imgCache.containsKey(tileset + "_original"))
			imgCache.put(tileset + "_original", getImage(tileset));

		BufferedImage img = getImage(tileset + "_original");
		setAlphaColor(tileset, img.getRGB(posX, posY));
	}

	public void initPencilSelection(int tileIndexX, int tileIndexY)
	{
		String tileset = getTileSet(getCurrentTileSetIndex());
		BufferedImage img = getImage(tileset);
		if (img != null)
		{
			propChangeSupport.firePropertyChange(PROPERTY_PENCIL_SIZE_X, pencil.getSizeX(), 1);
			propChangeSupport.firePropertyChange(PROPERTY_PENCIL_SIZE_Y, pencil.getSizeY(), 1);
			pencil.setSize(1, 1);
			pencil.setTileIndex(0, 0, currentTileIndex, tileIndexX, tileIndexY);
			propChangeSupport.firePropertyChange(PROPERTY_PENCIL_TILE_INDEX, null, pencil.getTileIndex(0, 0));
		}
	}

	public void updatePencilSelection(int startIndexX, int startIndexY, int currentIndexX, int currentIndexY)
	{
		int diffX = currentIndexX - startIndexX;
		int diffY = currentIndexY - startIndexY;
		if (diffX != 0 || diffY != 0)
		{
			propChangeSupport.firePropertyChange(PROPERTY_PENCIL_SIZE_X, pencil.getSizeX(), Math.abs(diffX) + 1);
			propChangeSupport.firePropertyChange(PROPERTY_PENCIL_SIZE_Y, pencil.getSizeY(), Math.abs(diffY) + 1);
			pencil.setSize(Math.abs(diffX) + 1, Math.abs(diffY) + 1);

			if (diffX <= 0 && diffY <= 0)
			{
				pencil.setTileIndex(0, 0, currentTileIndex, currentIndexX, currentIndexY);
				propChangeSupport.firePropertyChange(PROPERTY_PENCIL_TILE_INDEX, null, pencil.getTileIndex(0, 0));
			}
			else if (diffX > 0 && diffY < 0)
			{
				pencil.setTileIndex(0, 0, currentTileIndex, startIndexX, currentIndexY);
				propChangeSupport.firePropertyChange(PROPERTY_PENCIL_TILE_INDEX, null, pencil.getTileIndex(0, 0));
			}
			else if (diffX < 0 && diffY > 0)
			{
				pencil.setTileIndex(0, 0, currentTileIndex, currentIndexX, startIndexY);
				propChangeSupport.firePropertyChange(PROPERTY_PENCIL_TILE_INDEX, null, pencil.getTileIndex(0, 0));
			}
			else
			{
				pencil.setTileIndex(0, 0, currentTileIndex, startIndexX, startIndexY);
				propChangeSupport.firePropertyChange(PROPERTY_PENCIL_TILE_INDEX, null, pencil.getTileIndex(0, 0));
			}
			pencil.initPencilIndex();
		}
	}

	public void setPencilSize(int sizeX, int sizeY)
	{
		currentPencilIndexX = currentPencilIndexY = -1;
		propChangeSupport.firePropertyChange(PROPERTY_PENCIL_SIZE_X, pencil.getSizeX(), sizeX);
		propChangeSupport.firePropertyChange(PROPERTY_PENCIL_SIZE_Y, pencil.getSizeY(), sizeY);
		pencil.setSize(sizeX, sizeY);
	}

	public void setPencilTilesetIndex(int indexX, int indexY)
	{
		if (currentPencilIndexX != -1 && currentPencilIndexY != -1)
		{
			pencil.setTileIndex(currentPencilIndexX, currentPencilIndexY, currentTileIndex, indexX, indexY);
			propChangeSupport.firePropertyChange(PROPERTY_PENCIL_TILE_INDEX, null, pencil.getTileIndex(currentPencilIndexX, currentPencilIndexY));
		}
	}

	public Q2DPencilMode getPencilMode()
	{
		return pencilMode;
	}

	public void setPencilMode(Q2DPencilMode newMode)
	{
		propChangeSupport.firePropertyChange(PROPERTY_PENCIL_MODE, pencilMode, newMode);
		pencilMode = newMode;
	}

	public boolean isFillModeEnabled()
	{
		return isFillModeActive;
	}

	public void setFillModeEnabled(boolean enable)
	{
		this.isFillModeActive = enable;
	}

	public boolean isGroundTextureModeEnabled()
	{
		return isGroundTextureModeActive;
	}

	public void setGroundTextureModeEnabled(boolean enable)
	{
		this.isGroundTextureModeActive = enable;
	}

	public int getPencilIndexX()
	{
		return currentPencilIndexX;
	}

	public int getPencilIndexY()
	{
		return currentPencilIndexY;
	}

	public int getPencilSizeX()
	{
		return pencil.getSizeX();
	}

	public int getPencilSizeY()
	{
		return pencil.getSizeY();
	}

	public Q2DTileIndex getPencilTileIndex(int indexX, int indexY)
	{
		return pencil.getTileIndex(indexX, indexY);
	}

	public Collection<Q2DTileIndex> getPencilSelectedTiles()
	{
		return pencil.getSelectedTileIndex();
	}

	public void setPencilIndex(int indexX, int indexY)
	{
		currentPencilIndexX = indexX;
		currentPencilIndexY = indexY;
	}

	private void initMapTilesFromPencil(int leftIndex, int topIndex)
	{
		for (int x = 0; x < pencil.getSizeX(); x++)
		{
			for (int y = 0; y < pencil.getSizeY(); ++y)
			{
				Q2DTileIndex tileIndex = pencil.getTileIndex(x, y);
				int maxY = new Double(Math.ceil(1.0 * getMapHeight() / getTileSize())).intValue();
				int maxX = new Double(Math.ceil(1.0 * getMapWidth() / getTileSize())).intValue();
				if (tileIndex != null && (leftIndex + x) < maxX && (topIndex + y) < maxY)
				{
					Q2DTile tile = world.getMap().getTile(leftIndex + x, topIndex + y, currentLayer);
					if (tile == null)
						tile = new Q2DTile();
					tile.setIndex(leftIndex + x, topIndex + y);
					tile.setLayer(currentLayer);
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X, tileIndex.Y);
					tile.setSize(world.getMap().getTileSize(), world.getMap().getTileSize());
					world.getMap().setTile(tile);
				}
			}
		}
	}

	public boolean pasteGroundTexturePencil(int startIndexX, int startIndexY, int endIndexX, int endIndexY)
	{
		if (startIndexX == endIndexX || startIndexY == endIndexY)
			return false;

		addNewWorldVersion();

		Q2DTileIndex tileIndex = pencil.getTileIndex(0, 0);
		int startX = Math.min(startIndexX, endIndexX);
		int endX = Math.max(startIndexX, endIndexX);
		int startY = Math.min(startIndexY, endIndexY);
		int endY = Math.max(startIndexY, endIndexY);
		for (int x = startX; x <= endX; x++)
		{
			for (int y = startY; y <= endY; ++y)
			{
				Q2DTile tile = world.getMap().getTile(x, y, currentLayer);
				if (tile == null)
					tile = new Q2DTile();
				tile.setIndex(x, y);
				tile.setLayer(currentLayer);
				tile.setSize(world.getMap().getTileSize(), world.getMap().getTileSize());
				if (x > startX && x < endX && y == startY)
				{
					// first row
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X + 0.5, tileIndex.Y);
				}
				else if (x > startX && x < endX && y == endY)
				{
					// last row
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X + 0.5, tileIndex.Y + 1.0);
				}
				else if (y > startY && y < endY && x == startX)
				{
					// left column
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X, tileIndex.Y + 0.5);
				}
				else if (y > startY && y < endY && x == endX)
				{
					// right column
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X + 1.0, tileIndex.Y + 0.5);
				}
				else if (x > startX && x < endX && y > startY && y < endY)
				{
					// center
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X + 0.5, tileIndex.Y + 0.5);
				}
				else if (x == startX && y == startY)
				{
					// top left corner
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X, tileIndex.Y);
				}
				else if (x == endX && y == startY)
				{
					// top right corner
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X + 1.0, tileIndex.Y);
				}
				else if (x == startX && y == endY)
				{
					// bottom left corner
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X, tileIndex.Y + 1.0);
				}
				else if (x == endX && y == endY)
				{
					// bottom right corner
					tile.setTileIndex(tileIndex.TILESET_INDEX, tileIndex.X + 1.0, tileIndex.Y + 1.0);
				}
				world.getMap().setTile(tile);
			}
		}

		return true;
	}

	public void pastePencil(int indexX, int indexY)
	{
		if (currentLayer == -1)
			return;

		addNewWorldVersion();

		switch (pencilMode)
		{
			case NORMAL:
			case ADVANCED:
				if (isFillModeActive)
				{
					int maxX = new Double(Math.ceil(1.0 * getMapWidth() / getTileSize())).intValue();
					int maxY = new Double(Math.ceil(1.0 * getMapHeight() / getTileSize())).intValue();
					for (int x = 0; x < maxX; x += pencil.getSizeX())
						for (int y = 0; y < maxY; y += pencil.getSizeY())
							initMapTilesFromPencil(x, y);
				}
				else
					initMapTilesFromPencil(indexX, indexY);
				break;
			case COLLISION:
				for (int x = 0; x < pencil.getSizeX(); ++x)
					for (int y = 0; y < pencil.getSizeY(); ++y)
					{
						int maxY = new Double(Math.ceil(1.0 * getMapHeight() / getTileSize())).intValue();
						int maxX = new Double(Math.ceil(1.0 * getMapWidth() / getTileSize())).intValue();
						if ((indexX + x) < maxX && (indexY + y) < maxY)
						{
							Q2DTile result = world.getMap().getTile(indexX + x, indexY + y, currentLayer);
							if (result == null)
							{
								result = new Q2DTile();
								result.setIndex(indexX + x, indexY + y);
								result.setLayer(currentLayer);
								result.setSize(world.getMap().getTileSize(), world.getMap().getTileSize());
							}
							result.setCollision(true);
							world.getMap().setTile(result);
						}
					}
				break;
			case ANIMATION:
				int maxY = new Double(Math.ceil(1.0 * getMapHeight() / getTileSize())).intValue();
				int maxX = new Double(Math.ceil(1.0 * getMapWidth() / getTileSize())).intValue();
				int sizeX = animationWidth / getTileSize() - 1;
				int sizeY = animationWidth / getTileSize() - 1;
				if (animationSpritePath != null && indexX + sizeX < maxX && indexY + sizeY < maxY)
				{
					Q2DTile tile = world.getMap().getTile(indexX, indexY, currentLayer);
					if (tile == null)
						tile = new Q2DTile();
					tile.setIndex(indexX, indexY);
					tile.setLayer(currentLayer);
					tile.setAnimationSpritePath(animationSpritePath);
					tile.setNumColumns(animationNumColumns);
					tile.setNumRows(animationNumRows);
					tile.setAnimationsPerSecond(animationsPerSecond);
					tile.setSize(animationWidth, animationHeight);
					tile.setAnimation(true);
					world.getMap().setTile(tile);
				}
				break;
		}

		splitPane.onPencilPaste();
	}

	public void deletePencil(int leftIndex, int topIndex)
	{
		addNewWorldVersion();

		for (int x = 0; x < pencil.getSizeX(); ++x)
			for (int y = 0; y < pencil.getSizeY(); ++y)
			{
				int maxY = getMapHeight() / getTileSize();
				int maxX = getMapWidth() / getTileSize();
				if ((leftIndex + x) < maxX && (topIndex + y) < maxY)
				{
					if (pencilMode == Q2DPencilMode.COLLISION)
					{
						Q2DTile result = world.getMap().getTile(leftIndex + x, topIndex + y, currentLayer);
						if (result != null)
							result.setCollision(false);
						world.getMap().setTile(result);
					}
					else
						world.getMap().removeTile(leftIndex + x, topIndex + y, currentLayer);
				}
			}

		if (splitPane != null)
			splitPane.repaint();
	}

	public void setAnimationData(String path, int width, int height, int numColumns, int numRows, int fps)
	{
		animationSpritePath = path;
		animationsPerSecond = fps;
		animationWidth = width;
		animationHeight = height;
		animationNumColumns = numColumns;
		animationNumRows = numRows;

		if (!animations.containsKey(path + "#" + width + "#" + height + "#" + fps))
		{
			BufferedImage img = getImage(path);
			Q2DSprite sprite = new Q2DSprite(img, img.getWidth(null), img.getHeight(null), numColumns, numRows, fps, 0);
			sprite.setSize(width, height);
			animations.put(path + "#" + width + "#" + height + "#" + fps, sprite);
		}
	}

	public String getCurrentAnimationPath()
	{
		return animationSpritePath;
	}

	public int getCurrentAnimationWidth()
	{
		return animationWidth;
	}

	public int getCurrentAnimationHeight()
	{
		return animationHeight;
	}

	public int getCurrentAnimationFPS()
	{
		return animationsPerSecond;
	}

	public Q2DSprite getAnimation(String path, int width, int height, int fps)
	{
		if (animations.containsKey(path + "#" + width + "#" + height + "#" + fps))
			return animations.get(path + "#" + width + "#" + height + "#" + fps);
		return null;
	}

	private void initAnimationTimer()
	{
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if (animations.size() > 0)
				{
					for (Q2DSprite sprite : animations.values())
						sprite.update(1000.0 / Q2DEditor.FRAMES_PER_SECOND * 0.001);
					if (splitPane != null)
						splitPane.repaint();
				}
			}
		}, 0, 1000 / FRAMES_PER_SECOND);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propChangeSupport.addPropertyChangeListener(listener);
	}

	public void save()
	{
		try
		{
			final JFileChooser chooser = new JFileChooser("resources");
			chooser.setSelectedFile(new File(getWorldName() + ".q2dmap"));
			int returnVal = chooser.showSaveDialog(splitPane);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				FileOutputStream fileOut = new FileOutputStream(chooser.getSelectedFile().getAbsolutePath());
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(world);
				out.close();
				fileOut.close();
			}
		}
		catch (IOException i)
		{
			// TODO errormsg
			i.printStackTrace();
		}
	}

	public void test()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO load current loaded map
				Q2DEditorTestApplication testAppl = new Q2DEditorTestApplication("Test: " + world.getName(), 800, 600, 60, world.getMap().getNumLayers());
				testAppl.loadQ2DWorld("graphics/maps/Enter name.q2dmap");
			}
		});
	}

	public void load()
	{
		try
		{
			// TODO laden und speichern von untersch. mapversionen funktioniert nicht
			final JFileChooser chooser = new JFileChooser("resources");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.q2dmap", "q2dmap");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(splitPane);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				FileInputStream fileIn = new FileInputStream(chooser.getSelectedFile().getAbsolutePath());
				ObjectInputStream in = new ObjectInputStream(fileIn);
				Q2DWorld loadedWorld = (Q2DWorld) in.readObject();
				in.close();
				fileIn.close();

				history.clear();
				imgCache.clear();
				tilesetAlphaKeys.clear();
				propChangeSupport.firePropertyChange(PROPERTY_MAP_NAME, "", loadedWorld.getName());
				setMapWidth(loadedWorld.getMap().getWidth());
				setMapHeight(loadedWorld.getMap().getHeight());
				setNumLayers(loadedWorld.getMap().getNumLayers());
				Collection<Q2DTile> tiles = loadedWorld.getMap().getTiles();
				Iterator<Q2DTile> iterator = tiles.iterator();
				while (iterator.hasNext())
				{
					Q2DTile tile = iterator.next();
					if (tile.hasAnimation())
						setAnimationData(tile.getAnimationSpritePath(), tile.getWidth(), tile.getHeight(), tile.getNumColumns(), tile.getNumRows(), tile.getAnimationsPerSecond());
				}
				setCurrentLayer(-1);
				int i = 0;
				String tileset = loadedWorld.getTileset(i);
				while (tileset != null)
				{
					setTileset(i, tileset);
					if (loadedWorld.getTilesetAlphaKey(tileset) != null)
						setAlphaColor(tileset, loadedWorld.getTilesetAlphaKey(tileset));
					++i;
					tileset = loadedWorld.getTileset(i);
				}
				setCurrentTileSetIndex(0);
				setPencilSize(1, 1);
				setPencilMode(Q2DPencilMode.NORMAL);
				setTileSize(loadedWorld.getMap().getTileSize());
				setWorldBackgroundMusic(loadedWorld.getBackgroundMusic());
				setWorldName(loadedWorld.getName());

				world = loadedWorld;
				if (splitPane != null)
				{
					splitPane.onPencilPaste();
					splitPane.repaint();
				}
			}
		}
		catch (IOException i)
		{
			// TODO errormsg
			i.printStackTrace();
		}
		catch (ClassNotFoundException c)
		{
			// TODO errormsg
			c.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		try
		{
			Q2DEditor.INSTANCE.initWorld(DEFAULT_WORLD_NAME);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			JFrame frame = new JFrame("Q2DEditor");
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(Toolkit.getDefaultToolkit().getScreenSize()));
			Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(MAX_MAP_WIDTH, MAX_MAP_HEIGHT));
			frame.add(Q2DEditor.INSTANCE.getSplitPane());

			new Q2DEditorMenubar(frame);

			// center frame
			frame.setLocationRelativeTo(null);

			frame.setVisible(true);
			// setResizable(false);

			frame.setMinimumSize(new Dimension(800, 600));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Q2DEditor.INSTANCE.initAnimationTimer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
