package com.quilly2d.editor.core;

import java.util.ArrayList;
import java.util.List;

public class Q2DWorld
{
	private String			name		= null;
	private String			music		= null;
	private List<String>	tileSets	= null;
	private Q2DMap			map			= null;

	public Q2DWorld(String name, int width, int height, int numLayers, int tileSize)
	{
		this.name = name;
		tileSets = new ArrayList<String>();
		map = new Q2DMap(width, height, numLayers, tileSize);
	}

	public Q2DWorld(Q2DWorld toCopy)
	{
		name = toCopy.name;
		tileSets = new ArrayList<String>();
		for (String tileset : toCopy.tileSets)
			tileSets.add(tileset);
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

	public int getNumTileSets()
	{
		return tileSets.size();
	}

	public String getTileSet(int index)
	{
		if (index < tileSets.size())
			return tileSets.get(index);
		return null;
	}

	public void setTileSet(int index, String tileSet)
	{
		if (index < tileSets.size())
			tileSets.remove(index);
		tileSets.add(index, tileSet);
	}
}
