package com.quilly2d.editor.core;

import java.util.HashMap;
import java.util.Map;

public class Q2DPencil
{
	private int										sizeX		= 1;
	private int										sizeY		= 1;
	private Map<Integer, Map<Integer, TileIndex>>	indexMap	= null;

	public Q2DPencil()
	{
		indexMap = new HashMap<Integer, Map<Integer, TileIndex>>();
	}

	public void setSize(int sizeX, int sizeY)
	{
		indexMap.clear();

		for (int x = 0; x < sizeX; ++x)
		{
			Map<Integer, TileIndex> values = new HashMap<Integer, TileIndex>();
			indexMap.put(x, values);
			for (int y = 0; y < sizeY; ++y)
				values.put(y, new TileIndex());
		}
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public int getTileIndexX(int indexX, int indexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
				return map.get(indexY).X;
		}
		return -1;
	}

	public void setTileIndex(int indexX, int indexY, int tileIndexX, int tileIndexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
			{
				TileIndex tileIndex = map.get(indexY);
				tileIndex.X = tileIndexX;
				tileIndex.Y = tileIndexY;
			}
		}
	}

	public int getTileIndexY(int indexX, int indexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
				return map.get(indexY).Y;
		}
		return -1;
	}

	public void drawPreview()
	{
		//TODO
	}

	public void drawSelection()
	{
		//TODO
	}

	private class TileIndex
	{
		public int	X	= -1;
		public int	Y	= -1;
	}
}
