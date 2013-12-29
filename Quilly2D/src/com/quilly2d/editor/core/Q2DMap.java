package com.quilly2d.editor.core;

import java.util.Map;
import java.util.TreeMap;

public class Q2DMap
{
	private int													numLayers	= 0;
	private int													width		= 0;
	private int													height		= 0;
	private int													tileSize	= 0;
	private Map<Integer, Map<Integer, Map<Integer, Q2DTile>>>	tiles		= null;

	public Q2DMap(int width, int height, int numLayers, int tileSize)
	{
		this.setMapWidth(width);
		this.setMapHeight(height);
		this.setNumLayers(numLayers);
		this.setTileSize(tileSize);
		tiles = new TreeMap<Integer, Map<Integer, Map<Integer, Q2DTile>>>();
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

	public Q2DTile getTile(int indexX, int indexY, int layer)
	{
		if (tiles.containsKey(indexX))
		{
			Map<Integer, Map<Integer, Q2DTile>> map = tiles.get(indexX);
			if (map.containsKey(indexY))
			{
				Map<Integer, Q2DTile> map2 = map.get(indexY);
				if (map2.containsKey(layer))
					return map2.get(layer);
			}
		}
		return null;
	}

	public void setTile(Q2DTile tile)
	{
		Map<Integer, Map<Integer, Q2DTile>> map = null;
		if (tiles.containsKey(tile.getIndexX()))
			map = tiles.get(tile.getIndexX());
		else
		{
			map = new TreeMap<Integer, Map<Integer, Q2DTile>>();
			tiles.put(tile.getIndexX(), map);
		}

		Map<Integer, Q2DTile> result = null;
		if (map.containsKey(tile.getIndexY()))
			result = map.get(tile.getIndexY());
		else
		{
			result = new TreeMap<Integer, Q2DTile>();
			map.put(tile.getIndexY(), result);
		}

		result.put(tile.getLayer(), tile);
	}

	public void removeTile(int indexX, int indexY, int layer)
	{
		Map<Integer, Map<Integer, Q2DTile>> map = null;
		if (tiles.containsKey(indexX))
			map = tiles.get(indexX);
		else
		{
			map = new TreeMap<Integer, Map<Integer, Q2DTile>>();
			tiles.put(indexX, map);
		}

		Map<Integer, Q2DTile> result = null;
		if (map.containsKey(indexY))
			result = map.get(indexY);
		else
		{
			result = new TreeMap<Integer, Q2DTile>();
			map.put(indexY, result);
		}

		result.remove(layer);
	}
}
