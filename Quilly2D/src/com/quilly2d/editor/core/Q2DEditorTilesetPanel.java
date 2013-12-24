package com.quilly2d.editor.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DEditorTilesetPanel extends JPanel implements MouseListener, MouseMotionListener
{
	private static final int	TILESET_OFFSET_X		= 15;
	private static final int	TILESET_OFFSET_Y		= 288;

	private JLabel				lblName					= null;
	private JLabel				lblWidth				= null;
	private JLabel				lblHeight				= null;
	private JLabel				lblTileSize				= null;
	private JLabel				lblNumLayers			= null;
	private JLabel				lblTilesetPath			= null;
	private JLabel				lblMusicPath			= null;
	private JTextField			txtName					= null;
	private JTextField			txtWidth				= null;
	private JTextField			txtHeight				= null;
	private JSlider				sliderTileSize			= null;
	private JSlider				sliderNumLayers			= null;
	private JButton				btnTilesetPath			= null;
	private JButton				btnMusicPath			= null;
	private Rectangle			selectedTiles			= new Rectangle(-1, -1, -1, -1);
	private boolean				isSelectingTiles		= false;
	private int					selectionStartIndexX	= -1;
	private int					selectionStartIndexY	= -1;

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
		if (Q2DEditor.DEFAULT_WORLD_NAME.equals(worldName))
		{
			txtName.addFocusListener(new FocusListener() {

				@Override
				public void focusLost(FocusEvent event)
				{
					JTextField txt = (JTextField) event.getSource();
					if (txt.getText().equals(""))
					{
						txt.setText(Q2DEditor.DEFAULT_WORLD_NAME);
						txt.setForeground(Color.GRAY);
					}
				}

				@Override
				public void focusGained(FocusEvent event)
				{
					JTextField txt = (JTextField) event.getSource();
					if (txt.getText().equals(Q2DEditor.DEFAULT_WORLD_NAME))
					{
						txt.setText("");
						txt.setForeground(Color.BLACK);
					}
				}
			});
		}

		lblWidth = new JLabel("Width:");
		txtWidth = createTextField();
		txtWidth.setText("" + Q2DEditor.INSTANCE.getMapWidth());

		lblHeight = new JLabel("Height:");
		txtHeight = createTextField();
		txtHeight.setText("" + Q2DEditor.INSTANCE.getMapHeight());

		lblTileSize = new JLabel("Tilesize:");
		sliderTileSize = createSlider(16, 128, Q2DEditor.INSTANCE.getTileSize(), 16.0);

		lblNumLayers = new JLabel("Layers:");
		sliderNumLayers = createSlider(1, 6, Q2DEditor.INSTANCE.getNumLayers(), 1.0);

		lblTilesetPath = new JLabel("Tileset:");
		btnTilesetPath = createButton();

		lblMusicPath = new JLabel("Music:");
		btnMusicPath = createButton();

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
		addComponent(lblTilesetPath, padX, 10 * padY);
		addComponent(btnTilesetPath, 150 + padX, 10 * padY);
		addComponent(lblMusicPath, 275 + padX, 10 * padY);
		addComponent(btnMusicPath, 275 + 150 + padX, 10 * padY);
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
						if (val <= 0)
							throw new NumberFormatException();
						Q2DEditor.INSTANCE.setMapWidth(val);
					}
					else if (txt.equals(txtHeight))
					{
						Integer val = Integer.parseInt(txtHeight.getText());
						if (val <= 0)
							throw new NumberFormatException();
						Q2DEditor.INSTANCE.setMapHeight(val);
					}
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(Q2DEditorTilesetPanel.this, "You must enter a correct numeric value for \"width\" and \"height\"", "Wrong numeric value", JOptionPane.ERROR_MESSAGE);
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
				int returnVal = chooser.showOpenDialog(Q2DEditorTilesetPanel.this);
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
							//TODO index bestimmen für tileSet
							Q2DEditor.INSTANCE.setTileSet(0, path);
							lblTilesetPath.setSize(150, lblTilesetPath.getHeight());
							lblTilesetPath.setText("Tileset: " + chooser.getSelectedFile().getName());
						}
					}
					else
					{
						JOptionPane.showMessageDialog(Q2DEditorTilesetPanel.this, "You must select a file from the \"resources\" folder of the project", "Wrong file location", JOptionPane.ERROR_MESSAGE);
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
				JSlider slider = (JSlider) event.getSource();
				int val = slider.getValue();
				val = (int) (Math.round(val / increment) * increment);
				slider.setValue(val);
				slider.setToolTipText("" + val);
				if (slider.equals(sliderNumLayers))
				{
					// layer slider
					Q2DEditor.INSTANCE.setNumLayers(val);
				}
				else
				{
					// tilesize slider
					Q2DEditor.INSTANCE.setTileSize(val);
					Q2DEditor.INSTANCE.setSelectionStartIndex(-1, -1);
					Q2DEditor.INSTANCE.setSelectionFinishIndex(-1, -1);
					selectedTiles.x = selectedTiles.y = selectedTiles.width = selectedTiles.height = -1;
				}
			}

		});

		return slider;
	}

	private void drawGrid(Graphics graphics, ImageIcon tileSet)
	{
		final int tileSize = Q2DEditor.INSTANCE.getTileSize();
		final int maxY = new Double(Math.ceil(1.0 * tileSet.getIconHeight() / tileSize)).intValue();
		final int maxX = new Double(Math.ceil(1.0 * tileSet.getIconWidth() / tileSize)).intValue();
		graphics.setColor(Color.DARK_GRAY);
		for (int y = 0; y < maxY; ++y)
		{
			for (int x = 0; x < maxX; ++x)
			{
				graphics.drawRect(TILESET_OFFSET_X + x * tileSize, TILESET_OFFSET_Y + y * tileSize, tileSize, tileSize);
			}
		}

		if (selectedTiles != null)
		{
			graphics.setColor(new Color(180, 160, 0, 128));
			int x = (selectedTiles.x * tileSize) + TILESET_OFFSET_X;
			int y = (selectedTiles.y * tileSize) + TILESET_OFFSET_Y;
			int width = ((selectedTiles.width * tileSize) + TILESET_OFFSET_X) - x;
			int height = ((selectedTiles.height * tileSize) + TILESET_OFFSET_Y) - y;
			graphics.fillRect(x, y, width, height);
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
			graphics.drawImage(tileSetIcon.getImage(), TILESET_OFFSET_X, TILESET_OFFSET_Y, null);
			drawGrid(graphics, tileSetIcon);
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
		if (e.getX() >= TILESET_OFFSET_X && e.getY() >= TILESET_OFFSET_Y)
		{
			isSelectingTiles = true;
			selectionStartIndexX = (e.getX() - TILESET_OFFSET_X) / Q2DEditor.INSTANCE.getTileSize();
			selectionStartIndexY = (e.getY() - TILESET_OFFSET_Y) / Q2DEditor.INSTANCE.getTileSize();
			onMouseUpdate(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		onMouseUpdate(e);
		isSelectingTiles = false;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		onMouseUpdate(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
	}

	private void onMouseUpdate(MouseEvent e)
	{
		if (isSelectingTiles)
		{
			//TODO richtigen index übergeben
			String tileSet = Q2DEditor.INSTANCE.getTileSet(0);
			ImageIcon tileSetIcon = Q2DEditor.INSTANCE.getTileSetImageIcon(tileSet);
			if (tileSetIcon != null)
			{
				final int tileSize = Q2DEditor.INSTANCE.getTileSize();
				final int maxY = new Double(Math.ceil(1.0 * tileSetIcon.getIconHeight() / tileSize)).intValue();
				final int maxX = new Double(Math.ceil(1.0 * tileSetIcon.getIconWidth() / tileSize)).intValue();

				int startTileIndexX = selectionStartIndexX;
				int startTileIndexY = selectionStartIndexY;
				int finishTileIndexX = ((e.getX() - TILESET_OFFSET_X) / tileSize) + 1;
				int finishTileIndexY = ((e.getY() - TILESET_OFFSET_Y) / tileSize) + 1;

				if (finishTileIndexX <= startTileIndexX)
				{
					selectedTiles.x = finishTileIndexX - 1;
					selectedTiles.width = startTileIndexX + 1;
				}
				else
				{
					selectedTiles.x = startTileIndexX;
					selectedTiles.width = finishTileIndexX;
				}
				if (finishTileIndexY <= startTileIndexY)
				{
					selectedTiles.y = finishTileIndexY - 1;
					selectedTiles.height = startTileIndexY + 1;
				}
				else
				{
					selectedTiles.y = startTileIndexY;
					selectedTiles.height = finishTileIndexY;
				}

				if (selectedTiles.x >= 0 && selectedTiles.x <= maxX && selectedTiles.width >= 0 && selectedTiles.width <= maxX && selectedTiles.y >= 0 && selectedTiles.y <= maxY && selectedTiles.height >= 0 && selectedTiles.height <= maxY)
				{
					Q2DEditor.INSTANCE.setSelectionStartIndex(selectedTiles.x, selectedTiles.y);
					Q2DEditor.INSTANCE.setSelectionFinishIndex(selectedTiles.width, selectedTiles.height);
				}
			}
		}
	}
}
