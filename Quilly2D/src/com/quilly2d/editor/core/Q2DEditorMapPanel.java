package com.quilly2d.editor.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.quilly2d.editor.enums.Q2DPencilMode;
import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DEditorMapPanel extends JPanel implements MouseListener, MouseMotionListener
{
	private static final int	MAP_OFFSET_X			= 0;
	private static final int	MAP_OFFSET_Y			= 288;
	private static final int	PENCIL_PREVIEW_OFFSET_X	= 16;
	private static final int	PENCIL_PREVIEW_OFFSET_Y	= 27;
	private static final int	PENCIL_PREVIEW_SIZE_X	= 256;
	private static final int	PENCIL_PREVIEW_SIZE_Y	= 256;

	private JLabel				lblPencil				= null;
	private JLabel				lblPencilType			= null;
	private JLabel				lblPencilSize			= null;
	private JRadioButton		btnPencilNormal			= null;
	private JRadioButton		btnPencilAnimation		= null;
	private JRadioButton		btnPencilCollision		= null;
	private JCheckBox			btnPencilFill			= null;
	private JRadioButton		btnPencilAdvanced		= null;
	private JSlider				sliderPencilSize		= null;
	private JLabel				lblLayer				= null;
	private List<JRadioButton>	btnSelectLayer			= null;
	private JRadioButton		btnShowAllLayer			= null;
	private int					mouseX					= -1;
	private int					mouseY					= -1;

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
		sliderPencilSize = createSlider(1, 10, 1, 1.0);
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
		addComponent(lblPencilSize, 300, 180);
		addComponent(sliderPencilSize, 300, 200);

		addComponent(lblLayer, 660, 5);
		for (int i = 0; i < btnSelectLayer.size(); ++i)
			addComponent(btnSelectLayer.get(i), 660 + i * 60, 30);
		addComponent(btnShowAllLayer, 660 + btnSelectLayer.size() * 60, 30);
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
			comp.setSize(100, 20);
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
					Q2DEditor.INSTANCE.setCurrentLayer(-1);
				else
					Q2DEditor.INSTANCE.setCurrentLayer(layer);
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
						Q2DEditor.INSTANCE.setPencilMode(Q2DPencilMode.COLLISION);
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
					slider.setValue(slider.getMinimum());
					JOptionPane.showMessageDialog(slider, "The current tilesize does not allow a bigger advanced pencil.", "Wrong pencil size", JOptionPane.ERROR_MESSAGE);
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

	public void updateNumLayers(int numLayers)
	{
		for (int i = 0; i < btnSelectLayer.size(); ++i)
		{
			if (i < numLayers)
				btnSelectLayer.get(i).setEnabled(true);
			else
				btnSelectLayer.get(i).setEnabled(false);
		}
		btnSelectLayer.get(0).setSelected(true);
	}

	public void updateTileSize(int tileSize)
	{
		sliderPencilSize.setValue(sliderPencilSize.getMinimum());
	}

	public void updatePencilSize(int sizeX, int sizeY)
	{
		sliderPencilSize.setValue(sizeX);
	}

	private void drawMap(Graphics graphics)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxY = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapHeight() / tileSize)).intValue();
		int maxX = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapWidth() / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		for (int z = 0; z < Q2DEditor.INSTANCE.getNumLayers(); ++z)
			for (int y = 0; y < maxY; ++y)
			{
				for (int x = 0; x < maxX; ++x)
				{
					if (Q2DEditor.INSTANCE.getCurrentLayer() == -1 || z == Q2DEditor.INSTANCE.getCurrentLayer())
					{
						Q2DTile mapTile = Q2DEditor.INSTANCE.getMapTile(x, y, z);
						if (mapTile != null)
						{
							int drawX = MAP_OFFSET_X + x * tileSize;
							int drawY = MAP_OFFSET_Y + y * tileSize;
							if (mapTile.isHasAnimation())
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
								String tileSet = Q2DEditor.INSTANCE.getTileSet(mapTile.getTileIndex());
								ImageIcon imgIcon = Q2DEditor.INSTANCE.getTileSetImageIcon(tileSet);

								int srcX = mapTile.getTileIndexX() * tileSize;
								int srcY = mapTile.getTileIndexY() * tileSize;
								graphics.drawImage(imgIcon.getImage(), drawX, drawY, drawX + tileSize, drawY + tileSize, srcX, srcY, srcX + tileSize, srcY + tileSize, null);
							}
						}
						graphics.drawRect(MAP_OFFSET_X + x * tileSize, MAP_OFFSET_Y + y * tileSize, tileSize, tileSize);
					}
				}
			}
	}

	private void drawGrid(Graphics graphics)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		int maxY = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapHeight() / tileSize)).intValue();
		int maxX = new Double(Math.ceil(1.0 * Q2DEditor.INSTANCE.getMapWidth() / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		for (int y = 0; y < maxY; ++y)
		{
			for (int x = 0; x < maxX; ++x)
			{
				graphics.drawRect(MAP_OFFSET_X + x * tileSize, MAP_OFFSET_Y + y * tileSize, tileSize, tileSize);
			}
		}

		maxX = MAP_OFFSET_X + Q2DEditor.INSTANCE.getMapWidth();
		maxY = MAP_OFFSET_Y + Q2DEditor.INSTANCE.getMapHeight();
		if (mouseX >= MAP_OFFSET_X && mouseX < maxX && mouseY >= MAP_OFFSET_Y && mouseY < maxY)
		{
			int indexX = (mouseX - MAP_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
			int indexY = (mouseY - MAP_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
			Q2DEditor.INSTANCE.drawPencilSelection(graphics, MAP_OFFSET_X + indexX * Q2DEditor.INSTANCE.getTileSize(), MAP_OFFSET_Y + indexY * Q2DEditor.INSTANCE.getTileSize(), maxX, maxY);
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
		Q2DEditor.INSTANCE.drawPencilPreview(graphics, PENCIL_PREVIEW_OFFSET_X, PENCIL_PREVIEW_OFFSET_Y, PENCIL_PREVIEW_SIZE_X, PENCIL_PREVIEW_SIZE_Y);
		drawMap(graphics);
		drawGrid(graphics);
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
			Q2DEditor.INSTANCE.pastePencil(indexX, indexY);
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
			int indexX = (mouseX - MAP_OFFSET_X) / tileSize;
			int indexY = (mouseY - MAP_OFFSET_Y) / tileSize;
			Q2DEditor.INSTANCE.pastePencil(indexX, indexY);
		}

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event)
	{
	}
}
