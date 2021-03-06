package com.quilly2d.editor.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Q2DWorld implements Serializable
{
	private static final long		serialVersionUID	= 1L;
	private String					name				= null;
	private String					music				= null;
	private List<String>			tilesets			= null;
	private List<Integer>			tilesetsWidth		= null;
	private List<Integer>			tilesetsHeight		= null;
	private Map<String, Integer>	tilesetsAlphaKeys	= null;
	private Q2DMap					map					= null;

	public Q2DWorld(String name, int width, int height, int numLayers, int tileSize)
	{
		this.name = name;
		tilesets = new ArrayList<String>();
		tilesetsWidth = new ArrayList<Integer>();
		tilesetsHeight = new ArrayList<Integer>();
		tilesetsAlphaKeys = new HashMap<String, Integer>();
		map = new Q2DMap(width, height, numLayers, tileSize);
	}

	public Q2DWorld(Q2DWorld toCopy)
	{
		name = toCopy.name;
		tilesets = new ArrayList<String>();
		for (String tileset : toCopy.tilesets)
			tilesets.add(tileset);
		tilesetsWidth = new ArrayList<Integer>();
		for (Integer tileset : toCopy.tilesetsWidth)
			tilesetsWidth.add(tileset);
		tilesetsHeight = new ArrayList<Integer>();
		for (Integer tileset : toCopy.tilesetsHeight)
			tilesetsHeight.add(tileset);
		tilesetsAlphaKeys = new HashMap<String, Integer>();
		for (String key : toCopy.tilesetsAlphaKeys.keySet())
			tilesetsAlphaKeys.put(key, toCopy.tilesetsAlphaKeys.get(key));
		map = new Q2DMap(toCopy.map);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getBackgroundMusic()
	{
		return music;
	}

	public void setBackgroundMusic(String music)
	{
		this.music = music;
	}

	public Q2DMap getMap()
	{
		return map;
	}

	public void setMap(Q2DMap map)
	{
		this.map = map;
	}

	public int getNumTilesets()
	{
		return tilesets.size();
	}

	public String getTileset(int index)
	{
		if (index < tilesets.size())
			return tilesets.get(index);
		return null;
	}

	public int getNumTilesetColumns(int index)
	{
		if (index < tilesets.size())
			return tilesetsWidth.get(index) / map.getTileSize();
		return -1;
	}

	public int getNumTilesetRows(int index)
	{
		if (index < tilesets.size())
			return tilesetsHeight.get(index) / map.getTileSize();
		return -1;
	}

	public Integer getTilesetAlphaKey(String tileset)
	{
		if (tilesetsAlphaKeys.containsKey(tileset))
			return tilesetsAlphaKeys.get(tileset);
		return null;
	}

	public void setTilesetAlphaKey(String tileset, Integer alphaKey)
	{
		tilesetsAlphaKeys.put(tileset, alphaKey);
	}

	public void setTileset(int index, String tileset, Integer alphaKey, int tilesetWidth, int tilesetHeight)
	{
		if (index < tilesets.size())
		{
			tilesets.remove(index);
			tilesetsWidth.remove(index);
			tilesetsHeight.remove(index);
			tilesetsAlphaKeys.remove(index);
		}
		tilesets.add(index, tileset);
		tilesetsWidth.add(index, tilesetWidth);
		tilesetsHeight.add(index, tilesetHeight);
		if (alphaKey != null)
			tilesetsAlphaKeys.put(tileset, alphaKey);
	}
}
