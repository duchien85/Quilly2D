package com.quilly2d.editor.core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeMap;

import com.quilly2d.editor.enums.Q2DPencilMode;
import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.tools.Q2DEditor;

public class Q2DPencil
{
	private int										sizeX		= 0;
	private int										sizeY		= 0;
	private Map<Integer, Map<Integer, TileIndex>>	indexMap	= null;

	public Q2DPencil()
	{
		indexMap = new TreeMap<Integer, Map<Integer, TileIndex>>();
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

		for (int x = 0; x < sizeX; ++x)
		{
			Map<Integer, TileIndex> values = new TreeMap<Integer, TileIndex>();
			indexMap.put(x, values);
			for (int y = 0; y < sizeY; ++y)
				values.put(y, new TileIndex());
		}
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public int getTilesetIndex(int indexX, int indexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
				return map.get(indexY).TILESET_INDEX;
		}
		return -1;
	}

	public double getTileIndexX(int indexX, int indexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
				return map.get(indexY).X;
		}
		return -1;
	}

	public double getTileIndexY(int indexX, int indexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
				return map.get(indexY).Y;
		}
		return -1;
	}

	public void setTileIndex(int indexX, int indexY, int tileIndex, double tileIndexX, double tileIndexY)
	{
		if (indexMap.containsKey(indexX))
		{
			Map<Integer, TileIndex> map = indexMap.get(indexX);
			if (map.containsKey(indexY))
			{
				TileIndex index = map.get(indexY);
				index.TILESET_INDEX = tileIndex;
				index.X = tileIndexX;
				index.Y = tileIndexY;
			}
		}
	}

	public void initPencilIndex()
	{
		if (sizeX != 0 && sizeY != 0)
		{
			TileIndex startIndex = indexMap.get(0).get(0);
			for (int x = 0; x < sizeX; ++x)
			{
				for (int y = 0; y < sizeY; ++y)
				{
					setTileIndex(x, y, Q2DEditor.INSTANCE.getCurrentTileSetIndex(), startIndex.X + x, startIndex.Y + y);
				}
			}
		}
	}

	public void drawPreview(Graphics graphics, int offsetX, int offsetY, int previewSizeX, int previewSizeY)
	{
		if (sizeX != 0 && sizeY != 0)
		{
			if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ANIMATION)
			{
				Q2DSprite animation = Q2DEditor.INSTANCE.getAnimation(Q2DEditor.INSTANCE.getCurrentAnimationPath(), Q2DEditor.INSTANCE.getCurrentAnimationWidth(), Q2DEditor.INSTANCE.getCurrentAnimationHeight(), Q2DEditor.INSTANCE.getCurrentAnimationFPS());
				animation.paint((Graphics2D) graphics, -offsetX, -offsetY);
			}
			else
			{
				int tileSize = Q2DEditor.INSTANCE.getTileSize();
				BufferedImage pencilImg = new BufferedImage(sizeX * tileSize, sizeY * tileSize, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = pencilImg.createGraphics();
				g2.setColor(Color.BLACK);
				for (int x = 0; x < sizeX; ++x)
				{
					for (int y = 0; y < sizeY; ++y)
					{
						int drawX = x * tileSize;
						int drawY = y * tileSize;
						TileIndex tileIndex = indexMap.get(x).get(y);
						if (tileIndex.X != -1 && tileIndex.Y != -1)
						{
							String tileSet = null;
							if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ADVANCED)
								tileSet = Q2DEditor.INSTANCE.getTileSet(tileIndex.TILESET_INDEX);
							else
								tileSet = Q2DEditor.INSTANCE.getTileSet(Q2DEditor.INSTANCE.getCurrentTileSetIndex());
							BufferedImage img = Q2DEditor.INSTANCE.getImage(tileSet);
							g2.drawImage(img, drawX, drawY, drawX + tileSize, drawY + tileSize, (int) (tileIndex.X * tileSize), (int) (tileIndex.Y * tileSize), (int) ((tileIndex.X + 1) * tileSize), (int) ((tileIndex.Y + 1) * tileSize), null);
						}
					}
				}

				g2.dispose();
				graphics.drawImage(pencilImg, offsetX, offsetY, Math.min(previewSizeX, pencilImg.getWidth()), Math.min(previewSizeY, pencilImg.getHeight()), null);
			}

		}

		graphics.drawRect(offsetX - 1, offsetY - 1, previewSizeX + 1, previewSizeY + 1);
	}

	public void drawSelection(Graphics graphics, int offsetX, int offsetY, int maxX, int maxY)
	{
		if (sizeX > 0 && sizeY > 0)
		{
			int tileSize = Q2DEditor.INSTANCE.getTileSize();
			Color currentColor = graphics.getColor();

			graphics.setColor(new Color(180, 160, 0, 128));
			int width = offsetX + sizeX * tileSize;
			int height = offsetY + sizeY * tileSize;
			if (width > maxX)
				maxX = maxX - offsetX;
			else
				maxX = width - offsetX;
			if (height > maxY)
				maxY = maxY - offsetY;
			else
				maxY = height - offsetY;
			if (Q2DEditor.INSTANCE.isGroundTextureModeEnabled())
				graphics.fillRect(offsetX, offsetY, Q2DEditor.INSTANCE.getTileSize(), Q2DEditor.INSTANCE.getTileSize());
			else
				graphics.fillRect(offsetX, offsetY, maxX, maxY);
			graphics.setColor(currentColor);
		}
	}

	public void drawSelectedTiles(Graphics graphics, int offsetX, int offsetY)
	{
		if (sizeX != 0 && sizeY != 0)
		{
			int tileSize = Q2DEditor.INSTANCE.getTileSize();
			Color currentColor = graphics.getColor();

			graphics.setColor(new Color(180, 160, 0, 128));
			for (Integer x : indexMap.keySet())
			{
				Map<Integer, TileIndex> map = indexMap.get(x);
				for (Integer y : map.keySet())
				{
					TileIndex tileIndex = map.get(y);
					if (tileIndex.X != -1 && tileIndex.Y != -1 && (Q2DEditor.INSTANCE.getCurrentTileSetIndex() == tileIndex.TILESET_INDEX || tileIndex.TILESET_INDEX == -1))
					{
						graphics.fillRect((int) (offsetX + tileIndex.X * tileSize), (int) (offsetY + tileIndex.Y * tileSize), tileSize, tileSize);
					}
				}
			}

			graphics.setColor(currentColor);
		}
	}

	private class TileIndex
	{
		public double	X				= -1;
		public double	Y				= -1;
		public int		TILESET_INDEX	= -1;
	}
}
