package com.quilly2d.tools;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.quilly2d.editor.core.Q2DEditorSplitPane;
import com.quilly2d.editor.core.Q2DPencil;
import com.quilly2d.editor.core.Q2DTile;
import com.quilly2d.editor.core.Q2DWorld;

public enum Q2DEditor
{
	INSTANCE;

	// editor specific data
	private Q2DEditorSplitPane		splitPane					= null;
	private Map<String, ImageIcon>	tileSetImageIcons			= new HashMap<String, ImageIcon>();
	private int						currentTileIndex			= 0;
	private int						currentLayer				= 0;
	private Q2DPencil				pencil						= new Q2DPencil();
	private boolean					isAdvancedPencilModeActive	= false;
	private int						currentPencilIndexX			= -1;
	private int						currentPencilIndexY			= -1;
	// world specific data
	public static final String		DEFAULT_WORLD_NAME			= "Enter name";
	public static final int			MAX_NUM_LAYERS				= 6;
	public static final int			MAX_MAP_WIDTH				= 6400;
	public static final int			MAX_MAP_HEIGHT				= 6400;
	private final int				DEFAULT_WORLD_WIDTH			= 25 * 32;
	private final int				DEFAULT_WORLD_HEIGHT		= 20 * 32;
	private final int				DEFAULT_WORLD_TILESIZE		= 32;
	private final int				DEFAULT_NUM_LAYERS			= 3;
	private Q2DWorld				world						= null;

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

	public boolean isAdvancedPencilModeActive()
	{
		return isAdvancedPencilModeActive;
	}

	public void setAdvancedPencilModeActive(boolean active)
	{
		this.isAdvancedPencilModeActive = active;
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

	public void pastePencil(int indexX, int indexY)
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
				if (tileIndexX != -1 && tileIndexY != -1 && (indexX + x) < maxX && (indexY + y) < maxY)
				{
					Q2DTile tile = new Q2DTile();
					tile.setIndexX(indexX + x);
					tile.setIndexY(indexY + y);
					tile.setLayer(currentLayer);
					tile.setTileIndexX(tileIndexX);
					tile.setTileIndexY(tileIndexY);
					tile.setTileIndex(tileIndex);
					world.getMap().setTile(tile);
				}
			}
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
			//Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(Toolkit.getDefaultToolkit().getScreenSize()));
			Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(MAX_MAP_WIDTH, MAX_MAP_HEIGHT));
			frame.add(Q2DEditor.INSTANCE.getSplitPane());

			// center frame
			frame.setLocationRelativeTo(null);

			frame.setVisible(true);
			//setResizable(false);

			frame.setMinimumSize(new Dimension(800, 600));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
