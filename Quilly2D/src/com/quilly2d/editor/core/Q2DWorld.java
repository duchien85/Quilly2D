package com.quilly2d.editor.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Q2DWorld implements Serializable
{
	private String			name				= null;
	private String			music				= null;
	private List<String>	tilesets			= null;
	private List<String>	tilesetsAlphaKeys	= null;
	private Q2DMap			map					= null;

	public Q2DWorld(String name, int width, int height, int numLayers, int tileSize)
	{
		this.name = name;
		tilesets = new ArrayList<String>();
		tilesetsAlphaKeys = new ArrayList<String>();
		map = new Q2DMap(width, height, numLayers, tileSize);
	}

	public Q2DWorld(Q2DWorld toCopy)
	{
		name = toCopy.name;
		tilesets = new ArrayList<String>();
		for (String tileset : toCopy.tilesets)
			tilesets.add(tileset);
		tilesetsAlphaKeys = new ArrayList<String>();
		for (String tilesetAlphaKey : toCopy.tilesetsAlphaKeys)
			tilesetsAlphaKeys.add(tilesetAlphaKey);
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

	public void setTileset(int index, String tileset, String alphaKey)
	{
		if (index < tilesets.size())
		{
			tilesets.remove(index);
			tilesetsAlphaKeys.remove(index);
		}
		tilesets.add(index, tileset);
		tilesetsAlphaKeys.add(index, alphaKey);
	}
}
