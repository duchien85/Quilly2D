package com.quilly2d.editor.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Q2DMap implements Serializable
{
	private static final long		serialVersionUID	= 1L;
	private int						numLayers			= 0;
	private int						width				= 0;
	private int						height				= 0;
	private int						tileSize			= 0;
	private Map<String, Q2DTile>	tiles				= null;

	public Q2DMap(int width, int height, int numLayers, int tileSize)
	{
		this.width = width;
		this.height = height;
		this.numLayers = numLayers;
		this.tileSize = tileSize;
		tiles = new HashMap<String, Q2DTile>();
	}

	public Q2DMap(Q2DMap toCopy)
	{
		this(toCopy.width, toCopy.height, toCopy.numLayers, toCopy.tileSize);
		for (String key : toCopy.tiles.keySet())
			tiles.put(key, new Q2DTile(toCopy.tiles.get(key)));
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setSize(int width, int height)
	{
		this.width = width;
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

	private String getMapKey(int indexX, int indexY, int layer)
	{
		return indexX + "#" + indexY + "#" + layer;
	}

	public Q2DTile getTile(int indexX, int indexY, int layer)
	{
		String key = getMapKey(indexX, indexY, layer);
		if (tiles.containsKey(key))
			return tiles.get(key);
		return null;
	}

	public Collection<Q2DTile> getTiles()
	{
		return tiles.values();
	}

	public void setTile(Q2DTile tile)
	{
		String key = getMapKey(tile.getIndexX(), tile.getIndexY(), tile.getLayer());
		tiles.put(key, tile);
	}

	public void removeTile(int indexX, int indexY, int layer)
	{
		String key = getMapKey(indexX, indexY, layer);
		if (tiles.containsKey(key))
			tiles.remove(key);
	}
}
