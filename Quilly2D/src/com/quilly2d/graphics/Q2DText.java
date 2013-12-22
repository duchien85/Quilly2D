package com.quilly2d.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Q2DText
{
	private FontMetrics	fontMetrics		= null;
	private Font		font			= null;
	private Color		color			= null;
	private char[]		text			= null;
	private int			numCharsPerRow	= 0;
	private int			textX			= 0;
	private int			textY			= 0;
	private int			charHeight		= 0;
	private boolean		isRemovable		= false;

	public Q2DText(String text, int lengthPerRow, Font font, Color color, Graphics graphics)
	{
		this.font = font;
		this.color = color;

		fontMetrics = graphics.getFontMetrics(font);
		charHeight = fontMetrics.getHeight();

		setText(text, lengthPerRow);
	}

	public void paint(Graphics2D graphics)
	{
		int currentY = textY + charHeight;

		graphics.setFont(font);
		graphics.setColor(color);
		for (int i = 0; i < text.length; i += numCharsPerRow)
		{
			int length = numCharsPerRow;
			if (i + length >= text.length)
				length = text.length - i;
			graphics.drawChars(text, i, length, textX, currentY);
			currentY += charHeight + 5;
		}
	}

	public void remove()
	{
		isRemovable = true;
	}

	public boolean isRemovable()
	{
		return isRemovable;
	}

	public int getWidth()
	{
		int max = Math.min(text.length, numCharsPerRow);
		String s = "";
		for (int i = 0; i < max; ++i)
			s += text[i];
		return fontMetrics.stringWidth(s);
	}

	public int getHeight()
	{
		if (numCharsPerRow > 0)
		{
			int numRows = ((text.length - 1) / numCharsPerRow) + 1;
			return charHeight * numRows;
		}
		return 0;
	}

	public void setLocation(int newX, int newY)
	{
		textX = newX;
		textY = newY;
	}

	public void setText(String newText, int numCharsPerRow)
	{
		String currentText = "";

		if (!newText.isEmpty())
		{
			String[] words = newText.split(" ");
			int currentRowIndex = 0;
			for (String word : words)
			{
				if (currentRowIndex > 0)
				{
					++currentRowIndex;
					if (currentRowIndex == numCharsPerRow)
						currentRowIndex = 0;
					currentText += " ";
				}

				final int wordLength = word.length();
				// calculate if word has enough space in current row
				int numSpaces = numCharsPerRow - currentRowIndex - wordLength;
				if (numSpaces < 0)
				{
					// if it does not have enough space 
					// then fill the current row with spaces and add word to next line
					numSpaces = numCharsPerRow - currentRowIndex;
					while (numSpaces > 0)
					{
						--numSpaces;
						currentText += " ";
					}
					currentRowIndex = 0;
				}
				currentText += word;
				currentRowIndex += wordLength;
				if (currentRowIndex == numCharsPerRow)
					currentRowIndex = 0;
			}
		}

		this.numCharsPerRow = numCharsPerRow;
		this.text = currentText.toCharArray();
	}

	public void setText(String newText)
	{
		setText(newText, newText.length());
	}
}
