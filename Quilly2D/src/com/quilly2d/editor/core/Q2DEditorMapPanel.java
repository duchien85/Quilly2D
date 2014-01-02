package com.quilly2d.editor.core;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.quilly2d.editor.enums.Q2DPencilMode;
import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DEditorMapPanel extends JPanel implements MouseListener, MouseMotionListener, PropertyChangeListener
{
	private static final int	MAP_OFFSET_X				= 0;
	private static final int	MAP_OFFSET_Y				= 288;
	private static final int	PENCIL_PREVIEW_OFFSET_X		= 16;
	private static final int	PENCIL_PREVIEW_OFFSET_Y		= 27;
	private static final int	PENCIL_PREVIEW_SIZE_X		= 256;
	private static final int	PENCIL_PREVIEW_SIZE_Y		= 256;

	private JLabel				lblPencil					= null;
	private JLabel				lblPencilType				= null;
	private JLabel				lblPencilSize				= null;
	private JRadioButton		btnPencilNormal				= null;
	private JRadioButton		btnPencilAnimation			= null;
	private JRadioButton		btnPencilCollision			= null;
	private JCheckBox			btnPencilFill				= null;
	private JCheckBox			btnPencilGroundTexture		= null;
	private JRadioButton		btnPencilAdvanced			= null;
	private JSlider				sliderPencilSize			= null;
	private JLabel				lblLayer					= null;
	private List<JRadioButton>	btnSelectLayer				= null;
	private JRadioButton		btnShowAllLayer				= null;
	private int					mouseX						= -1;
	private int					mouseY						= -1;
	private int					lastIndexX					= -1;
	private int					lastIndexY					= -1;
	private int					groundTextureStartIndexX	= -1;
	private int					groundTextureStartIndexY	= -1;

	public Q2DEditorMapPanel(Dimension dimension)
	{
		super();
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setBackground(Color.WHITE);
		setLayout(null);
		addMouseListener(this);
		addMouseMotionListener(this);

		lblPencil = new JLabel("Pencil preview:");
		lblPencilType = new JLabel("Pencil type:");
		lblPencilSize = new JLabel("Pencil size:");
		btnPencilNormal = createPencilButton("Normal");
		btnPencilNormal.setSelected(true);
		btnPencilAnimation = createPencilButton("Animation");
		btnPencilCollision = createPencilButton("Collision");
		btnPencilAdvanced = createPencilButton("Advanced");
		ButtonGroup groupPencil = new ButtonGroup();
		groupPencil.add(btnPencilNormal);
		groupPencil.add(btnPencilAnimation);
		groupPencil.add(btnPencilCollision);
		groupPencil.add(btnPencilAdvanced);
		btnPencilFill = new JCheckBox("Fill");
		btnPencilFill.setBackground(Color.WHITE);
		btnPencilFill.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				Q2DEditor.INSTANCE.setFillModeEnabled(btnPencilFill.isSelected());
			}
		});
		btnPencilGroundTexture = new JCheckBox("Ground Texture");
		btnPencilGroundTexture.setBackground(Color.WHITE);
		btnPencilGroundTexture.setEnabled(false);
		btnPencilGroundTexture.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				boolean isSelected = btnPencilGroundTexture.isSelected();
				Q2DEditor.INSTANCE.setGroundTextureModeEnabled(isSelected);
				btnPencilFill.setEnabled(!isSelected);
				btnPencilFill.setSelected(false);
			}
		});
		sliderPencilSize = createSlider(1, PENCIL_PREVIEW_SIZE_X / Q2DEditor.INSTANCE.getTileSize(), 1, 1.0);
		lblPencilSize.setEnabled(false);
		sliderPencilSize.setEnabled(false);

		lblLayer = new JLabel("Layer:");
		btnShowAllLayer = createLayerSelectionButton(-1);
		btnSelectLayer = new ArrayList<JRadioButton>();
		for (int i = 0; i < Q2DEditor.MAX_NUM_LAYERS; ++i)
			btnSelectLayer.add(createLayerSelectionButton(i));
		btnSelectLayer.get(0).setSelected(true);
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < btnSelectLayer.size(); ++i)
			group.add(btnSelectLayer.get(i));
		group.add(btnShowAllLayer);
		for (int i = Q2DEditor.INSTANCE.getNumLayers(); i < btnSelectLayer.size(); ++i)
			btnSelectLayer.get(i).setEnabled(false);

		addComponent(lblPencil, 15, 5);
		addComponent(lblPencilType, 300, 5);
		addComponent(btnPencilNormal, 300, 30);
		addComponent(btnPencilAnimation, 300, 60);
		addComponent(btnPencilCollision, 300, 90);
		addComponent(btnPencilAdvanced, 300, 120);
		addComponent(btnPencilFill, 300, 150);
		addComponent(btnPencilGroundTexture, 360, 150);
		btnPencilGroundTexture.setSize(140, btnPencilGroundTexture.getHeight());
		addComponent(lblPencilSize, 300, 180);
		addComponent(sliderPencilSize, 300, 200);

		addComponent(lblLayer, 540, 5);
		for (int i = 0; i < btnSelectLayer.size(); ++i)
			addComponent(btnSelectLayer.get(i), 540 + i * 60, 30);
		addComponent(btnShowAllLayer, 540 + btnSelectLayer.size() * 60, 30);
	}

	private void addComponent(JComponent comp, int x, int y)
	{
		add(comp);
		comp.setLocation(x, y);
		if (comp instanceof JLabel)
			comp.setSize(100, 20);
		else if (comp instanceof JTextField)
			comp.setSize(200, 20);
		else if (comp instanceof JSlider)
			comp.setSize(200, 55);
		else if (comp instanceof JCheckBox)
			comp.setSize(50, 20);
		else if (comp instanceof JRadioButton)
		{
			for (int i = 0; i < btnSelectLayer.size(); ++i)
			{
				if (comp.equals(btnSelectLayer.get(i)))
				{
					comp.setSize(40, 20);
					return;
				}
			}
			comp.setSize(100, 20);
		}
		else if (comp instanceof JButton)
			comp.setSize(70, 20);
	}

	private JRadioButton createLayerSelectionButton(final int layer)
	{
		JRadioButton btn = null;
		if (layer == -1)
			btn = new JRadioButton("All");
		else
			btn = new JRadioButton("" + (layer + 1));
		btn.setBackground(Color.WHITE);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				JRadioButton source = (JRadioButton) event.getSource();
				if (source.equals(btnShowAllLayer))
				{
					Q2DEditor.INSTANCE.setCurrentLayer(-1);
					btnPencilNormal.setEnabled(false);
					btnPencilAnimation.setEnabled(false);
					btnPencilCollision.setEnabled(false);
					btnPencilAdvanced.setEnabled(false);
					btnPencilFill.setEnabled(false);
					btnPencilGroundTexture.setEnabled(false);
					sliderPencilSize.setEnabled(false);
					lblPencilSize.setEnabled(false);
				}
				else
				{
					Q2DEditor.INSTANCE.setCurrentLayer(layer);
					btnPencilNormal.setEnabled(true);
					btnPencilAnimation.setEnabled(true);
					btnPencilCollision.setEnabled(true);
					btnPencilAdvanced.setEnabled(true);
					btnPencilFill.setEnabled(true);
					if (Q2DEditor.INSTANCE.getPencilSizeX() == 2 && Q2DEditor.INSTANCE.getPencilSizeY() == 2 && Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.ADVANCED)
						btnPencilGroundTexture.setEnabled(true);
					if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ADVANCED || Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.COLLISION)
					{
						lblPencilSize.setEnabled(true);
						sliderPencilSize.setEnabled(true);
					}
				}
				repaint();
			}
		});

		return btn;
	}

	private JRadioButton createPencilButton(String name)
	{
		JRadioButton btn = new JRadioButton(name);
		btn.setBackground(Color.WHITE);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				JRadioButton source = (JRadioButton) event.getSource();
				if (source.equals(btnPencilAdvanced))
				{
					lblPencilSize.setEnabled(true);
					sliderPencilSize.setEnabled(true);
					Q2DEditor.INSTANCE.setPencilMode(Q2DPencilMode.ADVANCED);
					resetPencil(sliderPencilSize.getValue());
				}
				else
				{
					lblPencilSize.setEnabled(false);
					sliderPencilSize.setEnabled(false);
					resetPencil(1);

					if (source.equals(btnPencilNormal))
						Q2DEditor.INSTANCE.setPencilMode(Q2DPencilMode.NORMAL);
					else if (source.equals(btnPencilAnimation))
					{
						Q2DAnimationDialog dialog = new Q2DAnimationDialog();
						if (dialog.getAnimationPath() != null)
						{
							// animated sprite was selected
							Q2DEditor.INSTANCE.setPencilMode(Q2DPencilMode.ANIMATION);
							Q2DEditor.INSTANCE.setPencilSize(dialog.getAnimationWidth() / Q2DEditor.INSTANCE.getTileSize(), dialog.getAnimationHeight() / Q2DEditor.INSTANCE.getTileSize());
							Q2DEditor.INSTANCE.setAnimationData(dialog.getAnimationPath(), dialog.getAnimationWidth(), dialog.getAnimationHeight(), dialog.getNumColumns(), dialog.getNumRows(), dialog.getAnimationsPerSecond());
						}
						else
						{
							// dialog canceled
							Q2DEditor.INSTANCE.setPencilMode(Q2DPencilMode.NORMAL);
							btnPencilNormal.setSelected(true);
						}
					}
					else if (source.equals(btnPencilCollision))
					{
						Q2DEditor.INSTANCE.setPencilMode(Q2DPencilMode.COLLISION);
						lblPencilSize.setEnabled(true);
						sliderPencilSize.setEnabled(true);
					}
				}
			}
		});

		return btn;
	}

	private JSlider createSlider(int min, int max, int current, final double increment)
	{
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, current);
		Hashtable<Integer, Object> labelTable = new Hashtable<Integer, Object>();
		labelTable.put(new Integer(min), new JLabel("" + min));
		labelTable.put(new Integer(max), new JLabel("" + max));
		slider.setMajorTickSpacing(new Double(increment).intValue());
		slider.setPaintTicks(true);
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		slider.setBackground(Color.WHITE);

		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent event)
			{
				final JSlider slider = (JSlider) event.getSource();
				int val = slider.getValue();
				val = (int) (Math.round(val / increment) * increment);
				slider.setValue(val);

				int size = val * Q2DEditor.INSTANCE.getTileSize();
				if (size > PENCIL_PREVIEW_SIZE_X)
				{
					slider.setValue((int) (val - increment));
				}
				else
				{
					Q2DEditor.INSTANCE.setPencilSize(val, val);
				}
				slider.setToolTipText("" + slider.getValue());
			}
		});

		return slider;
	}

	private void resetPencil(int size)
	{
		Q2DEditor.INSTANCE.setPencilSize(size, size);
	}

	private void drawMap(Graphics graphics)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxY = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapHeight() / tileSize)).intValue();
		int maxX = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapWidth() / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		for (int z = 0; z < Q2DEditor.INSTANCE.getNumLayers(); ++z)
		{
			for (int y = 0; y < maxY; ++y)
			{
				for (int x = 0; x < maxX; ++x)
				{
					if (Q2DEditor.INSTANCE.getCurrentLayer() != -1 && z != Q2DEditor.INSTANCE.getCurrentLayer())
						((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
					else
						((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

					Q2DTile mapTile = Q2DEditor.INSTANCE.getMapTile(x, y, z);
					if (mapTile != null)
					{
						int drawX = MAP_OFFSET_X + x * tileSize;
						int drawY = MAP_OFFSET_Y + y * tileSize;
						if (mapTile.hasAnimation())
						{
							// animated tile
							int fps = mapTile.getAnimationsPerSecond();
							int width = mapTile.getWidth();
							int height = mapTile.getHeight();
							Q2DSprite sprite = Q2DEditor.INSTANCE.getAnimation(mapTile.getAnimationSpritePath(), width, height, fps);
							sprite.paint((Graphics2D) graphics, -drawX, -drawY);
						}
						else if (mapTile.getTileIndexX() != -1 && mapTile.getTileIndexY() != -1)
						{
							String tileset = Q2DEditor.INSTANCE.getTileSet(mapTile.getTileIndex());
							BufferedImage img = Q2DEditor.INSTANCE.getImage(tileset);

							double srcX = mapTile.getTileIndexX() * tileSize;
							double srcY = mapTile.getTileIndexY() * tileSize;
							graphics.drawImage(img, drawX, drawY, drawX + tileSize, drawY + tileSize, (int) srcX, (int) srcY, (int) (srcX + tileSize), (int) (srcY + tileSize), null);
						}
						if (mapTile.hasCollision())
						{
							((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
							Color currentColor = graphics.getColor();
							graphics.setColor(new Color(180, 0, 0, 150));
							graphics.fillRect(drawX, drawY, tileSize, tileSize);
							graphics.setColor(currentColor);
						}
					}
				}
			}
		}

		((Graphics2D) graphics).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}

	private void drawGrid(Graphics graphics)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxY = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapHeight() / tileSize)).intValue();
		int maxX = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapWidth() / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		if (btnShowAllLayer.isSelected())
		{
			graphics.drawRect(MAP_OFFSET_X, MAP_OFFSET_Y, maxX * tileSize, maxY * tileSize);
		}
		else
		{
			for (int y = 0; y < maxY; ++y)
			{
				for (int x = 0; x < maxX; ++x)
				{
					graphics.drawRect(MAP_OFFSET_X + x * tileSize, MAP_OFFSET_Y + y * tileSize, tileSize, tileSize);
				}
			}
		}
	}

	private void drawPencilPreview(Graphics graphics, int offsetX, int offsetY, int previewSizeX, int previewSizeY)
	{
		int sizeX = Q2DEditor.INSTANCE.getPencilSizeX();
		int sizeY = Q2DEditor.INSTANCE.getPencilSizeY();
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
						Q2DTileIndex tileIndex = Q2DEditor.INSTANCE.getPencilTileIndex(x, y);
						if (tileIndex != null)
						{
							String tileset = null;
							if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ADVANCED)
								tileset = Q2DEditor.INSTANCE.getTileSet(tileIndex.TILESET_INDEX);
							else
								tileset = Q2DEditor.INSTANCE.getTileSet(Q2DEditor.INSTANCE.getCurrentTileSetIndex());
							BufferedImage img = Q2DEditor.INSTANCE.getImage(tileset);
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

	private void drawPencilSelection(Graphics graphics, int offsetX, int offsetY, int maxX, int maxY)
	{
		int sizeX = Q2DEditor.INSTANCE.getPencilSizeX();
		int sizeY = Q2DEditor.INSTANCE.getPencilSizeY();
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

	private void drawPencil(Graphics graphics)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxX = MAP_OFFSET_X + Q2DEditor.INSTANCE.getMapWidth();
		int maxY = MAP_OFFSET_Y + Q2DEditor.INSTANCE.getMapHeight();
		if (mouseX >= MAP_OFFSET_X && mouseX < maxX && mouseY >= MAP_OFFSET_Y && mouseY < maxY)
		{
			int indexX = (mouseX - MAP_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
			int indexY = (mouseY - MAP_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
			drawPencilSelection(graphics, MAP_OFFSET_X + indexX * Q2DEditor.INSTANCE.getTileSize(), MAP_OFFSET_Y + indexY * Q2DEditor.INSTANCE.getTileSize(), maxX, maxY);
		}
		if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ADVANCED)
		{
			// draw pencil grid
			for (int y = 0; y < sliderPencilSize.getValue(); ++y)
			{
				for (int x = 0; x < sliderPencilSize.getValue(); ++x)
				{
					if (x == Q2DEditor.INSTANCE.getPencilIndexX() && y == Q2DEditor.INSTANCE.getPencilIndexY())
					{
						graphics.setColor(new Color(180, 160, 0, 128));
						graphics.fillRect(PENCIL_PREVIEW_OFFSET_X + x * tileSize, PENCIL_PREVIEW_OFFSET_Y + y * tileSize, tileSize, tileSize);
						graphics.setColor(Color.DARK_GRAY);
					}
					else
						graphics.drawRect(PENCIL_PREVIEW_OFFSET_X + x * tileSize, PENCIL_PREVIEW_OFFSET_Y + y * tileSize, tileSize, tileSize);
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		drawPencilPreview(graphics, PENCIL_PREVIEW_OFFSET_X, PENCIL_PREVIEW_OFFSET_Y, PENCIL_PREVIEW_SIZE_X, PENCIL_PREVIEW_SIZE_Y);
		drawMap(graphics);
		if (!btnShowAllLayer.isSelected())
			drawPencil(graphics);
		drawGrid(graphics);
	}

	public int getCurrentMapPencilIndexX()
	{
		int maxX = MAP_OFFSET_X + Q2DEditor.INSTANCE.getMapWidth();
		int maxY = MAP_OFFSET_Y + Q2DEditor.INSTANCE.getMapHeight();

		if (mouseX >= MAP_OFFSET_X && mouseX < maxX && mouseY >= MAP_OFFSET_Y && mouseY < maxY)
		{
			final int tileSize = Q2DEditor.INSTANCE.getTileSize();
			return (mouseX - MAP_OFFSET_X) / tileSize;

		}
		return -1;
	}

	public int getCurrentMapPencilIndexY()
	{
		int maxX = MAP_OFFSET_X + Q2DEditor.INSTANCE.getMapWidth();
		int maxY = MAP_OFFSET_Y + Q2DEditor.INSTANCE.getMapHeight();

		if (mouseX >= MAP_OFFSET_X && mouseX < maxX && mouseY >= MAP_OFFSET_Y && mouseY < maxY)
		{
			final int tileSize = Q2DEditor.INSTANCE.getTileSize();
			return (mouseY - MAP_OFFSET_Y) / tileSize;
		}
		return -1;
	}

	@Override
	public void mouseDragged(MouseEvent event)
	{
		mouseX = event.getX();
		mouseY = event.getY();
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxX = MAP_OFFSET_X + Q2DEditor.INSTANCE.getMapWidth();
		int maxY = MAP_OFFSET_Y + Q2DEditor.INSTANCE.getMapHeight();

		if (mouseX >= MAP_OFFSET_X && mouseX < maxX && mouseY >= MAP_OFFSET_Y && mouseY < maxY)
		{
			int indexX = (mouseX - MAP_OFFSET_X) / tileSize;
			int indexY = (mouseY - MAP_OFFSET_Y) / tileSize;
			if (lastIndexX != indexX || lastIndexY != indexY)
			{
				lastIndexX = indexX;
				lastIndexY = indexY;
				if (Q2DEditor.INSTANCE.isGroundTextureModeEnabled())
					Q2DEditor.INSTANCE.pasteGroundTexturePencil(indexX, indexY, groundTextureStartIndexX, groundTextureStartIndexY);
				else
					Q2DEditor.INSTANCE.pastePencil(indexX, indexY);
			}
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent event)
	{
		mouseX = event.getX();
		mouseY = event.getY();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent event)
	{
	}

	@Override
	public void mouseEntered(MouseEvent event)
	{
	}

	@Override
	public void mouseExited(MouseEvent event)
	{
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ADVANCED)
		{
			final int maxX = PENCIL_PREVIEW_OFFSET_X + Q2DEditor.INSTANCE.getTileSize() * sliderPencilSize.getValue();
			final int maxY = PENCIL_PREVIEW_OFFSET_Y + Q2DEditor.INSTANCE.getTileSize() * sliderPencilSize.getValue();

			if (mouseX >= PENCIL_PREVIEW_OFFSET_X && mouseX < maxX && mouseY >= PENCIL_PREVIEW_OFFSET_Y && mouseY < maxY)
			{
				int indexX = (mouseX - PENCIL_PREVIEW_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
				int indexY = (mouseY - PENCIL_PREVIEW_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
				Q2DEditor.INSTANCE.setPencilIndex(indexX, indexY);
			}
		}

		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxX = MAP_OFFSET_X + Q2DEditor.INSTANCE.getMapWidth();
		int maxY = MAP_OFFSET_Y + Q2DEditor.INSTANCE.getMapHeight();

		if (mouseX >= MAP_OFFSET_X && mouseX < maxX && mouseY >= MAP_OFFSET_Y && mouseY < maxY)
		{
			lastIndexX = (mouseX - MAP_OFFSET_X) / tileSize;
			lastIndexY = (mouseY - MAP_OFFSET_Y) / tileSize;
			if (Q2DEditor.INSTANCE.isGroundTextureModeEnabled())
			{
				groundTextureStartIndexX = lastIndexX;
				groundTextureStartIndexY = lastIndexY;
			}
			else
				Q2DEditor.INSTANCE.pastePencil(lastIndexX, lastIndexY);
		}

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if (evt.getPropertyName().equals(Q2DEditor.PROPERTY_NUM_LAYERS))
		{
			for (int i = 0; i < btnSelectLayer.size(); ++i)
			{
				if (i < (int) evt.getNewValue())
					btnSelectLayer.get(i).setEnabled(true);
				else
				{
					if (btnSelectLayer.get(i).isSelected())
						btnSelectLayer.get(0).setSelected(true);
					btnSelectLayer.get(i).setEnabled(false);
				}
			}
		}
		else if (evt.getPropertyName().equals(Q2DEditor.PROPERTY_TILE_SIZE))
		{
			sliderPencilSize.setValue(sliderPencilSize.getMinimum());
			sliderPencilSize.setMaximum(PENCIL_PREVIEW_SIZE_X / (int) evt.getNewValue());
			Hashtable<Integer, Object> labelTable = new Hashtable<Integer, Object>();
			labelTable.put(new Integer(sliderPencilSize.getMinimum()), new JLabel("" + sliderPencilSize.getMinimum()));
			labelTable.put(new Integer(sliderPencilSize.getMaximum()), new JLabel("" + sliderPencilSize.getMaximum()));
			sliderPencilSize.setLabelTable(labelTable);
		}
		else if (evt.getPropertyName().equals(Q2DEditor.PROPERTY_PENCIL_SIZE_X) || evt.getPropertyName().equals(Q2DEditor.PROPERTY_PENCIL_SIZE_Y))
		{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run()
				{
					btnPencilFill.setEnabled(true);
					if (Q2DEditor.INSTANCE.getPencilSizeX() == 2 && Q2DEditor.INSTANCE.getPencilSizeY() == 2 && Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.ADVANCED)
						btnPencilGroundTexture.setEnabled(true);
					else
					{
						Q2DEditor.INSTANCE.setGroundTextureModeEnabled(false);
						btnPencilGroundTexture.setSelected(false);
						btnPencilGroundTexture.setEnabled(false);
					}
				}
			});

			// sliderPencilSize.setValue((int) evt.getNewValue());
		}
		else if (evt.getPropertyName().equals(Q2DEditor.PROPERTY_CURRENT_LAYER))
		{
			if ((int) evt.getNewValue() == -1)
				btnShowAllLayer.setSelected(true);
			else
				btnSelectLayer.get((int) evt.getNewValue()).setSelected(true);
		}
	}
}
