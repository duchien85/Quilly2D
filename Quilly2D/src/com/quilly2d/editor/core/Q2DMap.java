package com.quilly2d.editor.core;

import java.util.HashMap;
import java.util.Map;

public class Q2DMap
{
	private int									numLayers	= 0;
	private int									width		= 0;
	private int									height		= 0;
	private int									tileSize	= 0;
	private Map<Integer, Map<Integer, Q2DTile>>	tiles		= null;

	public Q2DMap(int width, int height, int numLayers, int tileSize)
	{
		this.setMapWidth(width);
		this.setMapHeight(height);
		this.setNumLayers(numLayers);
		this.setTileSize(tileSize);
		tiles = new HashMap<Integer, Map<Integer, Q2DTile>>();
	}

	public int getMapWidth()
	{
		return width;
	}

	public void setMapWidth(int width)
	{
		this.width = width;
	}

	public int getMapHeight()
	{
		return height;
	}

	public void setMapHeight(int height)
	{
		this.height = height;
	}

	public int getNumLayers()
	{
		return numLayers;
	}

	public void setNumLayers(int numLayers)
	{
		this.numLayers = numLayers;
	}

	public int getTileSize()
	{
		return tileSize;
	}

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
	}

	public Q2DTile getTile(int indexX, int indexY)
	{
		if (tiles.containsKey(indexX))
		{
			Map<Integer, Q2DTile> map = tiles.get(indexX);
			if (map.containsKey(indexY))
				return map.get(indexY);
		}
		return null;
	}

	public void setTile(Q2DTile tile)
	{
		Map<Integer, Q2DTile> map = null;
		if (tiles.containsKey(tile.getIndexX()))
			map = tiles.get(tile.getIndexX());
		else
		{
			map = new HashMap<Integer, Q2DTile>();
			tiles.put(tile.getIndexX(), map);
		}

		map.put(tile.getIndexY(), tile);
	}
}
