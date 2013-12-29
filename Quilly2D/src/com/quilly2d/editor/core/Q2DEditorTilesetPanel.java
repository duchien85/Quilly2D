package com.quilly2d.editor.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.quilly2d.editor.enums.Q2DPencilMode;
import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DEditorTilesetPanel extends JPanel implements MouseListener, MouseMotionListener, PropertyChangeListener
{
	private static final int	TILESET_OFFSET_X	= 15;
	private static final int	TILESET_OFFSET_Y	= 288;

	private int					startIndexX			= -1;
	private int					startIndexY			= -1;

	private JLabel				lblName				= null;
	private JLabel				lblWidth			= null;
	private JLabel				lblHeight			= null;
	private JLabel				lblTileSize			= null;
	private JLabel				lblNumLayers		= null;
	private JLabel				lblNumTilesets		= null;
	private JLabel				lblTilesetPath		= null;
	private JLabel				lblMusicPath		= null;
	private JTextField			txtName				= null;
	private JTextField			txtWidth			= null;
	private JTextField			txtHeight			= null;
	private JSlider				sliderTileSize		= null;
	private JSlider				sliderNumLayers		= null;
	private JSlider				sliderNumTilesets	= null;
	private JButton				btnTilesetPath		= null;
	private JButton				btnMusicPath		= null;
	private List<JRadioButton>	btnSelectTileset	= null;
	private boolean				isSelectingTiles	= false;

	public Q2DEditorTilesetPanel(Dimension dimension)
	{
		super();
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
		setLayout(null);

		lblName = new JLabel("Name:");
		txtName = createTextField();
		String worldName = Q2DEditor.INSTANCE.getWorldName();
		txtName.setText(worldName);

		lblWidth = new JLabel("Width:");
		txtWidth = createTextField();
		txtWidth.setText("" + Q2DEditor.INSTANCE.getMapWidth());

		lblHeight = new JLabel("Height:");
		txtHeight = createTextField();
		txtHeight.setText("" + Q2DEditor.INSTANCE.getMapHeight());

		lblTileSize = new JLabel("Tilesize:");
		sliderTileSize = createSlider(16, 128, Q2DEditor.INSTANCE.getTileSize(), 16.0);

		lblNumLayers = new JLabel("Layers:");
		sliderNumLayers = createSlider(1, Q2DEditor.MAX_NUM_LAYERS, Q2DEditor.INSTANCE.getNumLayers(), 1.0);

		lblNumTilesets = new JLabel("Tilesets:");
		sliderNumTilesets = createSlider(1, 6, 1, 1.0);

		lblTilesetPath = new JLabel("Tileset:");
		btnTilesetPath = createButton();

		lblMusicPath = new JLabel("Music:");
		btnMusicPath = createButton();

		btnSelectTileset = new ArrayList<JRadioButton>();
		for (int i = 0; i < sliderNumTilesets.getMaximum(); ++i)
			btnSelectTileset.add(createTilesetSelectionButton(i));
		btnSelectTileset.get(0).setSelected(true);
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < btnSelectTileset.size(); ++i)
			group.add(btnSelectTileset.get(i));
		for (int i = sliderNumTilesets.getValue(); i < btnSelectTileset.size(); ++i)
			btnSelectTileset.get(i).setEnabled(false);
		final int padX = 15;
		final int padY = 15;
		addComponent(lblName, padX, padY);
		addComponent(txtName, 50 + padX, padY);
		addComponent(lblWidth, padX, 3 * padY);
		addComponent(txtWidth, 50 + padX, 3 * padY);
		addComponent(lblHeight, 275 + padX, 3 * padY);
		addComponent(txtHeight, 275 + 50 + padX, 3 * padY);
		addComponent(lblTileSize, padX, 6 * padY);
		addComponent(sliderTileSize, 50 + padX, 6 * padY);
		addComponent(lblNumLayers, 275 + padX, 6 * padY);
		addComponent(sliderNumLayers, 275 + 50 + padX, 6 * padY);
		addComponent(lblNumTilesets, padX, 10 * padY);
		addComponent(sliderNumTilesets, 50 + padX, 10 * padY);
		addComponent(lblTilesetPath, padX, 14 * padY);
		addComponent(btnTilesetPath, 150 + padX, 14 * padY);
		addComponent(lblMusicPath, 275 + padX, 14 * padY);
		addComponent(btnMusicPath, 275 + 150 + padX, 14 * padY);
		for (int i = 0; i < btnSelectTileset.size(); ++i)
			addComponent(btnSelectTileset.get(i), padX + i * 60, 17 * padY);
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
		else if (comp instanceof JRadioButton)
			comp.setSize(40, 20);
		else if (comp instanceof JButton)
			comp.setSize(70, 20);
	}

	private JTextField createTextField()
	{
		final JTextField txt = new JTextField(20);

		txt.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent event)
			{
			}

			@Override
			public void insertUpdate(DocumentEvent event)
			{
				onUpdate(event);
			}

			@Override
			public void changedUpdate(DocumentEvent event)
			{
				onUpdate(event);
			}

			private void onUpdate(DocumentEvent event)
			{
				try
				{
					if (txt.equals(txtName))
					{
						Q2DEditor.INSTANCE.setWorldName(txtName.getText());
					}
					else if (txt.equals(txtWidth))
					{
						Integer val = Integer.parseInt(txtWidth.getText());
						if (val <= 0 || val > Q2DEditor.MAX_MAP_WIDTH)
							throw new NumberFormatException();
						Q2DEditor.INSTANCE.setMapWidth(val);
					}
					else if (txt.equals(txtHeight))
					{
						Integer val = Integer.parseInt(txtHeight.getText());
						if (val <= 0 || val > Q2DEditor.MAX_MAP_HEIGHT)
							throw new NumberFormatException();
						Q2DEditor.INSTANCE.setMapHeight(val);
					}
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(txtWidth, "You must enter a correct numeric value for \"width\" and \"height\"", "Wrong numeric value", JOptionPane.ERROR_MESSAGE);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run()
						{
							if (txt.equals(txtWidth))
								txtWidth.setText("" + Q2DEditor.INSTANCE.getMapWidth());
							else if (txt.equals(txtHeight))
								txtHeight.setText("" + Q2DEditor.INSTANCE.getMapHeight());
						}
					});
				}
			}
		});

		txt.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent event)
			{
				JTextField txt = (JTextField) event.getSource();
				if (txt.equals(txtName))
				{
					if (txt.getText().equals(""))
					{
						txt.setText(Q2DEditor.DEFAULT_WORLD_NAME);
						txt.setForeground(Color.GRAY);
					}
				}
				else if (txt.equals(txtWidth))
					txt.setText("" + Q2DEditor.INSTANCE.getMapWidth());
				else if (txt.equals(txtHeight))
					txt.setText("" + Q2DEditor.INSTANCE.getMapHeight());
			}

			@Override
			public void focusGained(FocusEvent event)
			{
				JTextField txt = (JTextField) event.getSource();
				if (txt.equals(txtName))
				{
					if (txt.getText().equals(Q2DEditor.DEFAULT_WORLD_NAME))
					{
						txt.setText("");
						txt.setForeground(Color.BLACK);
					}
				}
			}
		});

		return txt;
	}

	private JButton createButton()
	{
		final JButton btn = new JButton("Browse");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				final JFileChooser chooser = new JFileChooser("resources");
				FileNameExtensionFilter filter = null;
				if (btn.equals(btnMusicPath))
					filter = new FileNameExtensionFilter("*.mp3", "mp3");
				else
					filter = new FileNameExtensionFilter("*.jpg; *.png", "jpg", "png");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(lblTilesetPath);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = chooser.getSelectedFile();
					String path = file.getPath();
					if (path.contains("resources"))
					{
						path = path.substring(path.indexOf("resources") + "resources".length() + 1);
						if (btn.equals(btnMusicPath))
						{
							Q2DEditor.INSTANCE.setWorldBackgroundMusic(path);
							lblMusicPath.setSize(150, lblTilesetPath.getHeight());
							lblMusicPath.setText("Music: " + chooser.getSelectedFile().getName());
						}
						else
						{
							Q2DEditor.INSTANCE.setTileSet(Q2DEditor.INSTANCE.getCurrentTileSetIndex(), path);
							lblTilesetPath.setSize(150, lblTilesetPath.getHeight());
							lblTilesetPath.setText("Tileset: " + chooser.getSelectedFile().getName());
							resetPencil();
						}
					}
					else
					{
						JOptionPane.showMessageDialog(lblTilesetPath, "You must select a file from the \"resources\" folder of the project", "Wrong file location", JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		});

		return btn;
	}

	private JRadioButton createTilesetSelectionButton(final int tileSet)
	{
		final JRadioButton btn = new JRadioButton("" + (tileSet + 1));
		btn.setBackground(Color.WHITE);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event)
			{
				Q2DEditor.INSTANCE.setCurrentTileSetIndex(tileSet);
				String tilesetName = Q2DEditor.INSTANCE.getTileSet(tileSet);
				if (tilesetName != null)
					lblTilesetPath.setText("Tileset: " + tilesetName.substring(tilesetName.lastIndexOf("\\") + 1));
				else
					lblTilesetPath.setText("Tileset: ");

				if (Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.ADVANCED)
					resetPencil();
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
				JSlider slider = (JSlider) event.getSource();
				int val = slider.getValue();
				val = (int) (Math.round(val / increment) * increment);
				slider.setValue(val);
				slider.setToolTipText("" + val);
				if (slider.equals(sliderNumLayers))
					Q2DEditor.INSTANCE.setNumLayers(val);
				else if (slider.equals(sliderTileSize))
				{
					Q2DEditor.INSTANCE.setTileSize(val);
					resetPencil();
				}
				else if (slider.equals(sliderNumTilesets))
				{
					for (int i = 0; i < btnSelectTileset.size(); ++i)
					{
						if (i < val)
							btnSelectTileset.get(i).setEnabled(true);
						else
						{
							if (btnSelectTileset.get(i).isSelected())
								btnSelectTileset.get(0).setSelected(true);
							btnSelectTileset.get(i).setEnabled(false);
						}
					}
				}
			}

		});

		return slider;
	}

	private void resetPencil()
	{
		Q2DEditor.INSTANCE.setPencilSize(1, 1);
	}

	private void drawGrid(Graphics graphics, Image img)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		final int maxY = new Double(Math.ceil(1.0 * img.getHeight(null) / tileSize)).intValue();
		final int maxX = new Double(Math.ceil(1.0 * img.getWidth(null) / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		for (int y = 0; y < maxY; ++y)
		{
			for (int x = 0; x < maxX; ++x)
			{
				graphics.drawRect(TILESET_OFFSET_X + x * tileSize, TILESET_OFFSET_Y + y * tileSize, tileSize, tileSize);
			}
		}

		Q2DEditor.INSTANCE.drawSelectedPencilTiles(graphics, TILESET_OFFSET_X, TILESET_OFFSET_Y);
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		String tileSet = Q2DEditor.INSTANCE.getTileSet(Q2DEditor.INSTANCE.getCurrentTileSetIndex());
		Image img = Q2DEditor.INSTANCE.getTileSetImage(tileSet);
		if (img != null)
		{
			graphics.drawImage(img, TILESET_OFFSET_X, TILESET_OFFSET_Y, null);
			drawGrid(graphics, img);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ANIMATION || Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.COLLISION)
			return;

		String tileSet = Q2DEditor.INSTANCE.getTileSet(Q2DEditor.INSTANCE.getCurrentTileSetIndex());
		Image img = Q2DEditor.INSTANCE.getTileSetImage(tileSet);
		if (img != null)
		{
			final int maxX = img.getWidth(null) + TILESET_OFFSET_X;
			final int maxY = img.getHeight(null) + TILESET_OFFSET_Y;

			if (e.getX() >= TILESET_OFFSET_X && e.getX() < maxX && e.getY() >= TILESET_OFFSET_Y && e.getY() < maxY)
			{
				if (e.isControlDown())
				{
					Q2DEditor.INSTANCE.setAlphaColor(tileSet, e.getX() - TILESET_OFFSET_X, e.getY() - TILESET_OFFSET_Y);
				}
				else
				{
					if (Q2DEditor.INSTANCE.getPencilMode() == Q2DPencilMode.ADVANCED)
					{
						int indexX = (e.getX() - TILESET_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
						int indexY = (e.getY() - TILESET_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
						Q2DEditor.INSTANCE.setPencilTilesetIndex(indexX, indexY);
					}
					else
					{
						isSelectingTiles = true;
						startIndexX = (e.getX() - TILESET_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
						startIndexY = (e.getY() - TILESET_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
						Q2DEditor.INSTANCE.initPencilSelection(startIndexX, startIndexY);
					}
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		isSelectingTiles = false;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.ADVANCED && Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.ANIMATION && Q2DEditor.INSTANCE.getPencilMode() != Q2DPencilMode.ANIMATION)
		{
			if (isSelectingTiles)
			{
				String tileSet = Q2DEditor.INSTANCE.getTileSet(Q2DEditor.INSTANCE.getCurrentTileSetIndex());
				Image img = Q2DEditor.INSTANCE.getTileSetImage(tileSet);
				if (img != null)
				{
					final int maxX = img.getWidth(null) + TILESET_OFFSET_X;
					final int maxY = img.getHeight(null) + TILESET_OFFSET_Y;

					if (e.getX() >= TILESET_OFFSET_X && e.getX() < maxX && e.getY() >= TILESET_OFFSET_Y && e.getY() < maxY)
					{
						int tileIndexX = (e.getX() - TILESET_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
						int tileIndexY = (e.getY() - TILESET_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
						Q2DEditor.INSTANCE.updatePencilSelection(startIndexX, startIndexY, tileIndexX, tileIndexY);
					}
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		//		if (evt.getPropertyName().equals(Q2DEditor.PROPERTY_MAP_WIDTH))
		//			txtWidth.setText("" + evt.getNewValue());
		//		else if (evt.getPropertyName().equals(Q2DEditor.PROPERTY_MAP_HEIGHT))
		//			txtHeight.setText("" + evt.getNewValue());
	}

	public void onPencilPaste()
	{
		sliderTileSize.setEnabled(false);
		sliderNumLayers.setMinimum(sliderNumLayers.getValue());
		Hashtable<Integer, Object> labelTable = new Hashtable<Integer, Object>();
		labelTable.put(new Integer(sliderNumLayers.getMinimum()), new JLabel("" + sliderNumLayers.getMinimum()));
		labelTable.put(new Integer(sliderNumLayers.getMaximum()), new JLabel("" + sliderNumLayers.getMaximum()));
		sliderNumLayers.setLabelTable(labelTable);
		sliderNumTilesets.setMinimum(sliderNumTilesets.getValue());
		labelTable = new Hashtable<Integer, Object>();
		labelTable.put(new Integer(sliderNumTilesets.getMinimum()), new JLabel("" + sliderNumTilesets.getMinimum()));
		labelTable.put(new Integer(sliderNumTilesets.getMaximum()), new JLabel("" + sliderNumTilesets.getMaximum()));
		sliderNumTilesets.setLabelTable(labelTable);
	}
}
