package com.quilly2d.tools;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.quilly2d.editor.core.Q2DEditorSplitPane;

public enum Q2DEditor
{
	INSTANCE;

	// editor specific data
	private int					startTileIndexX		= -1;
	private int					startTileIndexY		= -1;
	private int					finishTileIndexX	= -1;
	private int					finishTileIndexY	= -1;
	// map specific data
	private String				mapName				= null;
	private int					mapWidth			= 640;
	private int					mapHeight			= 480;
	private int					tileSize			= 32;
	private int					numLayers			= 3;
	private String				tileSetPath			= null;
	private ImageIcon			tileSet				= null;
	private String				musicPath			= null;
	private Q2DEditorSplitPane	splitPane			= null;

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

	public void setSelectionEndIndex(int x, int y)
	{
		finishTileIndexX = x;
		finishTileIndexY = y;
		if (splitPane != null)
			splitPane.repaint();
	}

	public String getMapName()
	{
		return mapName;
	}

	public void setMapName(String mapName)
	{
		this.mapName = mapName;
	}

	public int getMapWidth()
	{
		return mapWidth;
	}

	public void setMapWidth(int mapWidth)
	{
		this.mapWidth = mapWidth;

		if (splitPane != null)
			splitPane.repaint();
	}

	public int getMapHeight()
	{
		return mapHeight;
	}

	public void setMapHeight(int mapHeight)
	{
		this.mapHeight = mapHeight;

		if (splitPane != null)
			splitPane.repaint();
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;

		if (splitPane != null)
			splitPane.repaint();
	}

	public int getNumLayers()
	{
		return numLayers;
	}

	public void setNumLayers(int numLayers)
	{
		this.numLayers = numLayers;

		if (splitPane != null)
			splitPane.repaint();
	}

	public String getTileSetPath()
	{
		return tileSetPath;
	}

	public ImageIcon getTileSetImageIcon()
	{
		return tileSet;
	}

	public void setTileSetPath(String tileSetPath)
	{
		this.tileSetPath = tileSetPath;
		tileSet = new ImageIcon(this.getClass().getResource("/" + tileSetPath));

		if (splitPane != null)
			splitPane.repaint();
	}

	public String getMusicPath()
	{
		return musicPath;
	}

	public void setMusicPath(String musicPath)
	{
		this.musicPath = musicPath;
	}

	public Q2DEditorSplitPane getSplitPane()
	{
		return splitPane;
	}

	public void setSplitPane(Q2DEditorSplitPane splitPane)
	{
		this.splitPane = splitPane;
	}

	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			JFrame frame = new JFrame("Editor");
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			Q2DEditorSplitPane mainPane = new Q2DEditorSplitPane(Toolkit.getDefaultToolkit().getScreenSize());
			Q2DEditor.INSTANCE.setSplitPane(mainPane);
			frame.add(mainPane);

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
