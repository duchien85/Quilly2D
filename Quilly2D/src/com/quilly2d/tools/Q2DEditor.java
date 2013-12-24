package com.quilly2d.tools;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.quilly2d.editor.core.Q2DEditorSplitPane;
import com.quilly2d.editor.core.Q2DWorld;

public enum Q2DEditor
{
	INSTANCE;

	// editor specific data
	private int						startTileIndexX			= -1;
	private int						startTileIndexY			= -1;
	private int						finishTileIndexX		= -1;
	private int						finishTileIndexY		= -1;
	private Q2DEditorSplitPane		splitPane				= null;
	private Map<String, ImageIcon>	tileSetImageIcons		= new HashMap<String, ImageIcon>();
	// world specific data
	public static final String		DEFAULT_WORLD_NAME		= "Enter name";
	private final int				DEFAULT_WORLD_WIDTH		= 800;
	private final int				DEFAULT_WORLD_HEIGHT	= 600;
	private final int				DEFAULT_WORLD_TILESIZE	= 32;
	private final int				DEFAULT_NUM_LAYERS		= 3;
	private Q2DWorld				world					= null;

	public void setSelectionStartIndex(int x, int y)
	{
		startTileIndexX = x;
		startTileIndexY = y;
	}

	public int getSelectionStartIndexX()
	{
		return startTileIndexX;
	}

	public int getSelectionStartIndexY()
	{
		return startTileIndexY;
	}

	public int getSelectionFinishIndexX()
	{
		return finishTileIndexX;
	}

	public int getSelectionFinishIndexY()
	{
		return finishTileIndexY;
	}

	public void setSelectionFinishIndex(int x, int y)
	{
		finishTileIndexX = x;
		finishTileIndexY = y;

		if (splitPane != null)
			splitPane.repaint();
	}

	public Q2DEditorSplitPane getSplitPane()
	{
		return splitPane;
	}

	public void setSplitPane(Q2DEditorSplitPane splitPane)
	{
		this.splitPane = splitPane;
	}

	public void initWorld(String name)
	{
		world = new Q2DWorld(name, DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT, DEFAULT_NUM_LAYERS, DEFAULT_WORLD_TILESIZE);
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
			splitPane.repaint();
	}

	public int getNumLayers()
	{
		return world.getMap().getNumLayers();
	}

	public void setNumLayers(int numLayers)
	{
		world.getMap().setNumLayers(numLayers);

		if (splitPane != null)
			splitPane.repaint();
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

	public static void main(String[] args)
	{
		try
		{
			Q2DEditor.INSTANCE.initWorld(DEFAULT_WORLD_NAME);
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			JFrame frame = new JFrame("Q2DEditor");
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			Q2DEditor.INSTANCE.setSplitPane(new Q2DEditorSplitPane(Toolkit.getDefaultToolkit().getScreenSize()));
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
