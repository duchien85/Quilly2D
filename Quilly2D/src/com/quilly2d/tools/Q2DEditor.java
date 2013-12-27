package com.quilly2d.tools;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.quilly2d.editor.core.Q2DEditorSplitPane;
import com.quilly2d.editor.core.Q2DPencil;
import com.quilly2d.editor.core.Q2DTile;
import com.quilly2d.editor.core.Q2DWorld;
import com.quilly2d.editor.enums.Q2DPencilMode;
import com.quilly2d.graphics.Q2DSprite;

public enum Q2DEditor
{
	INSTANCE;

	// editor specific data
	private Q2DEditorSplitPane		splitPane				= null;
	private Map<String, ImageIcon>	tileSetImageIcons		= new HashMap<String, ImageIcon>();
	private int						currentTileIndex		= 0;
	private int						currentLayer			= 0;
	private Q2DPencil				pencil					= new Q2DPencil();
	private Q2DPencilMode			pencilMode				= Q2DPencilMode.NORMAL;
	private boolean					isFillModeActive		= false;
	private int						currentPencilIndexX		= -1;
	private int						currentPencilIndexY		= -1;
	// world specific data
	public static final String		DEFAULT_WORLD_NAME		= "Enter name";
	public static final int			MAX_NUM_LAYERS			= 6;
	public static final int			MAX_MAP_WIDTH			= 500 * 32;
	public static final int			MAX_MAP_HEIGHT			= 500 * 32;
	private final int				DEFAULT_WORLD_WIDTH		= 25 * 32;
	private final int				DEFAULT_WORLD_HEIGHT	= 20 * 32;
	private final int				DEFAULT_WORLD_TILESIZE	= 32;
	private final int				DEFAULT_NUM_LAYERS		= 3;
	private Q2DWorld				world					= null;
	// animation specific data
	public static final int			FRAMES_PER_SECOND		= 50;
	private String					animationSpritePath		= null;
	private int						animationsPerSecond		= 0;
	private int						animationWidth			= 0;
	private int						animationHeight			= 0;
	private int						animationNumColumns		= 0;
	private int						animationNumRows		= 0;
	private Map<String, Q2DSprite>	animations				= new HashMap<String, Q2DSprite>();

	public void initPencilSelection(int tileIndexX, int tileIndexY)
	{
		String tileSet = getTileSet(getCurrentTileSetIndex());
		ImageIcon tileSetIcon = getTileSetImageIcon(tileSet);
		if (tileSetIcon != null)
		{
			pencil.setSize(1, 1);
			pencil.setTileIndex(0, 0, currentTileIndex, tileIndexX, tileIndexY);

			if (splitPane != null)
				splitPane.repaint();
		}
	}

	public void updatePencilSelection(int startIndexX, int startIndexY, int currentIndexX, int currentIndexY)
	{
		int diffX = currentIndexX - startIndexX;
		int diffY = currentIndexY - startIndexY;
		if (diffX != 0 || diffY != 0)
		{
			pencil.setSize(Math.abs(diffX) + 1, Math.abs(diffY) + 1);
			if (diffX <= 0 && diffY <= 0)
				pencil.setTileIndex(0, 0, currentTileIndex, currentIndexX, currentIndexY);
			else if (diffX > 0 && diffY < 0)
				pencil.setTileIndex(0, 0, currentTileIndex, startIndexX, currentIndexY);
			else if (diffX < 0 && diffY > 0)
				pencil.setTileIndex(0, 0, currentTileIndex, currentIndexX, startIndexY);
			else
				pencil.setTileIndex(0, 0, currentTileIndex, startIndexX, startIndexY);
			pencil.initPencilIndex();

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
		currentTileIndex = index;

		if (splitPane != null)
			splitPane.repaint();
	}

	public int getCurrentLayer()
	{
		return currentLayer;
	}

	public void setCurrentLayer(int layer)
	{
		currentLayer = layer;
	}

	public void initWorld(String name)
	{
		world = new Q2DWorld(name, DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT, DEFAULT_NUM_LAYERS, DEFAULT_WORLD_TILESIZE);
	}

	public Q2DTile getMapTile(int indexX, int indexY, int layer)
	{
		return world.getMap().getTile(indexX, indexY, layer);
	}

	public int getMapWidth()
	{
		return world.getMap().getMapWidth();
	}

	public void setMapWidth(int width)
	{
		world.getMap().setMapWidth(width);

		if (splitPane != null)
			splitPane.repaint();
	}

	public int getMapHeight()
	{
		return world.getMap().getMapHeight();
	}

	public void setMapHeight(int height)
	{
		world.getMap().setMapHeight(height);

		if (splitPane != null)
			splitPane.repaint();
	}

	public int getTileSize()
	{
		return world.getMap().getTileSize();
	}

	public void setTileSize(int tileSize)
	{
		world.getMap().setTileSize(tileSize);

		if (splitPane != null)
		{
			splitPane.updateTileSize(tileSize);
			splitPane.repaint();
		}
	}

	public int getNumLayers()
	{
		return world.getMap().getNumLayers();
	}

	public void setNumLayers(int numLayers)
	{
		world.getMap().setNumLayers(numLayers);

		if (splitPane != null)
		{
			splitPane.updateNumLayers(numLayers);
			splitPane.repaint();
		}
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
		return world.getTileSet(index);
	}

	public ImageIcon getTileSetImageIcon(String tileSet)
	{
		if (tileSetImageIcons.containsKey(tileSet))
			return tileSetImageIcons.get(tileSet);
		return null;
	}

	public void setTileSet(int index, String tileSet)
	{
		world.setTileSet(index, tileSet);
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/" + tileSet));
		tileSetImageIcons.put(tileSet, icon);

		if (splitPane != null)
			splitPane.repaint();
	}

	public void setPencilSize(int sizeX, int sizeY)
	{
		currentPencilIndexX = currentPencilIndexY = -1;
		pencil.setSize(sizeX, sizeY);
		if (splitPane != null)
		{
			splitPane.updatePencilSize(sizeX, sizeY);
			splitPane.repaint();
		}
	}

	public void setPencilTilesetIndex(int indexX, int indexY)
	{
		if (currentPencilIndexX != -1 && currentPencilIndexY != -1)
		{
			pencil.setTileIndex(currentPencilIndexX, currentPencilIndexY, currentTileIndex, indexX, indexY);
			if (splitPane != null)
				splitPane.repaint();
		}
	}

	public void drawSelectedPencilTiles(Graphics graphics, int offsetX, int offsetY)
	{
		pencil.drawSelectedTiles(graphics, offsetX, offsetY);
	}

	public void drawPencilPreview(Graphics graphics, int offsetX, int offsetY, int previewSizeX, int previewSizeY)
	{
		pencil.drawPreview(graphics, offsetX, offsetY, previewSizeX, previewSizeY);
	}

	public void drawPencilSelection(Graphics graphics, int offsetX, int offsetY, int maxX, int maxY)
	{
		pencil.drawSelection(graphics, offsetX, offsetY, maxX, maxY);
	}

	public Q2DPencilMode getPencilMode()
	{
		return pencilMode;
	}

	public void setPencilMode(Q2DPencilMode newMode)
	{
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

	public int getPencilIndexX()
	{
		return currentPencilIndexX;
	}

	public int getPencilIndexY()
	{
		return currentPencilIndexY;
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
				int tileIndex = pencil.getTilesetIndex(x, y);
				int tileIndexX = pencil.getTileIndexX(x, y);
				int tileIndexY = pencil.getTileIndexY(x, y);
				int maxY = new Double(Math.ceil(1.0 * getMapHeight() / getTileSize())).intValue();
				int maxX = new Double(Math.ceil(1.0 * getMapWidth() / getTileSize())).intValue();
				if (tileIndexX != -1 && tileIndexY != -1 && (leftIndex + x) < maxX && (topIndex + y) < maxY)
				{
					Q2DTile tile = new Q2DTile();
					tile.setIndexX(leftIndex + x);
					tile.setIndexY(topIndex + y);
					tile.setLayer(currentLayer);
					tile.setTileIndexX(tileIndexX);
					tile.setTileIndexY(tileIndexY);
					tile.setTileIndex(tileIndex);
					world.getMap().setTile(tile);
				}
			}
		}
	}

	public void pastePencil(int indexX, int indexY)
	{
		if (currentLayer == -1)
			return;

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
			break;
		case ANIMATION:
			int maxY = new Double(Math.ceil(1.0 * getMapHeight() / getTileSize())).intValue();
			int maxX = new Double(Math.ceil(1.0 * getMapWidth() / getTileSize())).intValue();
			int sizeX = animationWidth / getTileSize() - 1;
			int sizeY = animationWidth / getTileSize() - 1;
			if (animationSpritePath != null && indexX + sizeX < maxX && indexY + sizeY < maxY)
			{
				Q2DTile tile = new Q2DTile();
				tile.setIndexX(indexX);
				tile.setIndexY(indexY);
				tile.setLayer(currentLayer);
				tile.setAnimationSpritePath(animationSpritePath);
				tile.setNumColumns(animationNumColumns);
				tile.setNumRows(animationNumRows);
				tile.setAnimationsPerSecond(animationsPerSecond);
				tile.setWidth(animationWidth);
				tile.setHeight(animationHeight);
				tile.setHasAnimation(true);
				world.getMap().setTile(tile);
			}
			break;
		}
	}

	public void setAnimationData(String path, int width, int height, int numColumns, int numRows, int fps)
	{
		animationSpritePath = path;
		animationsPerSecond = fps;
		animationWidth = width;
		animationHeight = height;
		animationNumColumns = numColumns;
		animationNumRows = numRows;

		ImageIcon animation = new ImageIcon(this.getClass().getResource("/" + path));
		if (!animations.containsKey(path + "#" + width + "#" + height + "#" + fps))
		{
			Q2DSprite sprite = new Q2DSprite(animation.getImage(), animation.getIconWidth(), animation.getIconHeight(), numColumns, numRows, fps, 0);
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
		timer.schedule(new TimerTask() {
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

	public static void main(String[] args)
	{
		try
		{
			Q2DEditor.INSTANCE.initWorld(DEFAULT_WORLD_NAME);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			JFrame frame = new JFrame("Q2DEditor");
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			//Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(Toolkit.getDefaultToolkit().getScreenSize()));
			Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(MAX_MAP_WIDTH, MAX_MAP_HEIGHT));
			frame.add(Q2DEditor.INSTANCE.getSplitPane());

			// center frame
			frame.setLocationRelativeTo(null);

			frame.setVisible(true);
			//setResizable(false);

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
