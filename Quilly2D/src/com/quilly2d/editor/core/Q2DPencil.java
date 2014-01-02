package com.quilly2d.editor.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.quilly2d.tools.Q2DEditor;

public class Q2DPencil
{
	private int							sizeX		= 0;
	private int							sizeY		= 0;
	private Map<String, Q2DTileIndex>	indexMap	= null;

	public Q2DPencil()
	{
		indexMap = new HashMap<String, Q2DTileIndex>();
		setSize(1, 1);
	}

	public int getSizeX()
	{
		return sizeX;
	}

	public int getSizeY()
	{
		return sizeY;
	}

	public void setSize(int sizeX, int sizeY)
	{
		indexMap.clear();
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	private String getMapKey(int indexX, int indexY)
	{
		return indexX + "#" + indexY;
	}

	public Q2DTileIndex getTileIndex(int indexX, int indexY)
	{
		String key = getMapKey(indexX, indexY);
		if (indexMap.containsKey(key))
			return indexMap.get(key);
		return null;
	}

	public void setTileIndex(int indexX, int indexY, int tileIndex, double tileIndexX, double tileIndexY)
	{
		String key = getMapKey(indexX, indexY);
		Q2DTileIndex index = null;
		if (indexMap.containsKey(key))
			index = indexMap.get(key);
		else
			index = new Q2DTileIndex();
		index.TILESET_INDEX = tileIndex;
		index.X = tileIndexX;
		index.Y = tileIndexY;
		indexMap.put(getMapKey(indexX, indexY), index);
	}

	public Collection<Q2DTileIndex> getSelectedTileIndex()
	{
		return indexMap.values();
	}

	public void initPencilIndex()
	{
		if (sizeX != 0 && sizeY != 0)
		{
			Q2DTileIndex startIndex = indexMap.get(getMapKey(0, 0));
			for (int x = 0; x < sizeX; ++x)
				for (int y = 0; y < sizeY; ++y)
					setTileIndex(x, y, Q2DEditor.INSTANCE.getCurrentTileSetIndex(), startIndex.X + x, startIndex.Y + y);
		}
	}
}
