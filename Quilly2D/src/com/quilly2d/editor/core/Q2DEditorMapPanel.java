package com.quilly2d.editor.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DEditorMapPanel extends JPanel
{
	private static final int	MAP_OFFSET_X	= 0;
	private static final int	MAP_OFFSET_Y	= 288;

	private JLabel				lblPencil		= null;

	public Q2DEditorMapPanel(Dimension dimension)
	{
		super();
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setBackground(Color.WHITE);
		setLayout(null);

		lblPencil = new JLabel("Pencil:");

		addComponent(lblPencil, 15, 5);
	}

	private void addComponent(JComponent comp, int x, int y)
	{
		add(comp);
		comp.setLocation(x, y);
		if (comp instanceof JLabel)
			comp.setSize(50, 20);
		else if (comp instanceof JTextField)
			comp.setSize(200, 20);
		else if (comp instanceof JSlider)
			comp.setSize(200, 55);
		else if (comp instanceof JButton)
			comp.setSize(70, 20);
	}

	private void drawGrid(Graphics graphics, ImageIcon tileSet)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		final int maxY = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapHeight() / tileSize)).intValue();
		final int maxX = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapWidth() / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		for (int y = 0; y < maxY; ++y)
		{
			for (int x = 0; x < maxX; ++x)
			{
				graphics.drawRect(MAP_OFFSET_X + x * tileSize, MAP_OFFSET_Y + y * tileSize, tileSize, tileSize);
			}
		}

		//		if (selectedTiles != null)
		//		{
		//			graphics.setColor(new Color(180, 160, 0, 128));
		//			int x = (selectedTiles.x * tileSize) + TILESET_OFFSET_X;
		//			int y = (selectedTiles.y * tileSize) + TILESET_OFFSET_Y;
		//			int width = ((selectedTiles.width * tileSize) + TILESET_OFFSET_X) - x;
		//			int height = ((selectedTiles.height * tileSize) + TILESET_OFFSET_Y) - y;
		//			graphics.fillRect(x, y, width, height);
		//		}
	}

	private void drawPencil(Graphics graphics, ImageIcon tileSet)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int sourceX = Q2DEditor.INSTANCE.getSelectionStartIndexX();
		int sourceY = Q2DEditor.INSTANCE.getSelectionStartIndexY();
		int pencilSizeX = Q2DEditor.INSTANCE.getSelectionFinishIndexX() - sourceX;
		int pencilSizeY = Q2DEditor.INSTANCE.getSelectionFinishIndexY() - sourceY;

		if (pencilSizeX > 0 && pencilSizeY > 0)
		{

			BufferedImage pencilImg = new BufferedImage(pencilSizeX * tileSize, pencilSizeY * tileSize, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = pencilImg.createGraphics();

			for (int y = 0; y < pencilSizeY; ++y)
			{
				for (int x = 0; x < pencilSizeX; ++x)
				{
					int drawX = x * tileSize;
					int drawY = y * tileSize;
					int srcX = sourceX + x;
					int srcY = sourceY + y;
					g2.drawRect(drawX, drawY, tileSize, tileSize);
					g2.drawImage(tileSet.getImage(), drawX, drawY, drawX + tileSize, drawY + tileSize, srcX * tileSize, srcY * tileSize, (srcX + 1) * tileSize, (srcY + 1) * tileSize, null);
				}
			}

			g2.dispose();
			graphics.drawImage(pencilImg, 16, 27, Math.min(256, pencilImg.getWidth()), Math.min(256, pencilImg.getHeight()), null);
			graphics.drawRect(15, 26, 257, 257);
		}
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		//TODO richtigen index übergeben
		String tileSet = Q2DEditor.INSTANCE.getTileSet(0);
		ImageIcon tileSetIcon = Q2DEditor.INSTANCE.getTileSetImageIcon(tileSet);
		if (tileSetIcon != null)
		{
			//			graphics.drawImage(tileSet.getImage(), TILESET_OFFSET_X, TILESET_OFFSET_Y, null);
			drawGrid(graphics, tileSetIcon);
			drawPencil(graphics, tileSetIcon);
		}
	}
}
