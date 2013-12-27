package com.quilly2d.editor.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DAnimationDialog extends JDialog
{
	private final int	PREVIEW_OFFSET_X		= 350;
	private final int	PREVIEW_OFFSET_Y		= 50;
	private final int	PREVIEW_SIZE_X			= 256;
	private final int	PREVIEW_SIZE_Y			= 256;
	private JLabel		lblPreview				= null;
	private JLabel		lblPath					= null;
	private JButton		btnBrowse				= null;
	private JLabel		lblAnimationNumRows		= null;
	private JTextField	txtAnimationNumRows		= null;
	private JLabel		lblAnimationNumColumns	= null;
	private JTextField	txtAnimationNumColumns	= null;
	private JLabel		lblAnimationsPerSecond	= null;
	private JTextField	txtAnimationsPerSecond	= null;
	private JLabel		lblWidth				= null;
	private JTextField	txtWidth				= null;
	private JLabel		lblHeight				= null;
	private JTextField	txtHeight				= null;
	private JButton		btnOK					= null;
	private JButton		btnCANCEL				= null;
	private Timer		timer					= null;
	private Q2DSprite	sprite					= null;
	private String		path					= null;

	public Q2DAnimationDialog()
	{
		super();
		setName("Choose animated sprite");
		setLayout(null);
		setSize(640, 400);
		setModal(true);
		setResizable(false);

		lblPreview = new JLabel("Preview:");
		lblPath = new JLabel("Animation:");
		btnBrowse = createBrowseButton();
		lblAnimationNumRows = new JLabel("Rows:");
		txtAnimationNumRows = createTextField();
		txtAnimationNumRows.setText("1");
		lblAnimationNumColumns = new JLabel("Columns:");
		txtAnimationNumColumns = createTextField();
		txtAnimationNumColumns.setText("1");
		lblAnimationsPerSecond = new JLabel("FPS:");
		txtAnimationsPerSecond = createTextField();
		txtAnimationsPerSecond.setText("10");
		lblWidth = new JLabel("Width:");
		txtWidth = createTextField();
		txtWidth.setText("" + Q2DEditor.INSTANCE.getTileSize());
		lblHeight = new JLabel("Height:");
		txtHeight = createTextField();
		txtHeight.setText("" + Q2DEditor.INSTANCE.getTileSize());

		btnOK = new JButton("OK");
		btnOK.setEnabled(false);
		btnCANCEL = new JButton("Cancel");

		final int spaceY = 15;
		setContentPane(new DialogPanel(new Dimension(640, 400)));
		addComponent(lblPreview, PREVIEW_OFFSET_X, spaceY);
		addComponent(lblPath, 15, spaceY);
		addComponent(btnBrowse, 70 + 15, spaceY);
		addComponent(lblAnimationNumColumns, 15, 2 * spaceY + 20);
		addComponent(txtAnimationNumColumns, 70 + 15, 2 * spaceY + 20);
		addComponent(lblAnimationNumRows, 150 + 15, 2 * spaceY + 20);
		addComponent(txtAnimationNumRows, 150 + 70 + 15, 2 * spaceY + 20);
		addComponent(lblAnimationsPerSecond, 15, 4 * spaceY + 20);
		addComponent(txtAnimationsPerSecond, 70 + 15, 4 * spaceY + 20);
		addComponent(lblWidth, 15, 6 * spaceY + 20);
		addComponent(txtWidth, 70 + 15, 6 * spaceY + 20);
		addComponent(lblHeight, 150 + 15, 6 * spaceY + 20);
		addComponent(txtHeight, 150 + 70 + 15, 6 * spaceY + 20);

		addComponent(btnOK, 320 - 80 - 15, 330);
		btnOK.setSize(80, 30);
		addComponent(btnCANCEL, 320 + 15, 330);
		btnCANCEL.setSize(80, 30);

		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Q2DAnimationDialog.this.dispose();
			}
		});
		btnCANCEL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				path = null;
				Q2DAnimationDialog.this.dispose();
			}
		});

		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void addComponent(JComponent comp, int x, int y)
	{
		getContentPane().add(comp);
		comp.setLocation(x, y);
		if (comp instanceof JLabel)
			comp.setSize(70, 20);
		else if (comp instanceof JTextField)
			comp.setSize(70, 20);
		else if (comp instanceof JButton)
			comp.setSize(70, 20);
	}

	private JTextField createTextField()
	{
		final JTextField txt = new JTextField(5);
		txt.setText("");
		txt.setEnabled(false);

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
					Integer val = Integer.parseInt(txt.getText());
					if (val <= 0)
						throw new NumberFormatException();

					if (sprite != null)
					{
						if (txt.equals(txtWidth) || txt.equals(txtHeight))
						{
							int width = Integer.parseInt(txtWidth.getText());
							int height = Integer.parseInt(txtHeight.getText());
							sprite.setSize(width, height);
						}
						//						else if (txt.equals(txtAnimationsPerSecond))
						//						{
						//							int fps = Integer.parseInt(txtAnimationsPerSecond.getText());
						//							initAnimationTimer();
						//						}
						else
						{
							initSprite(path);
						}
					}
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(txt, "You must enter a correct numeric value for", "Wrong numeric value", JOptionPane.ERROR_MESSAGE);
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run()
						{
							txt.setText("1");
						}
					});
				}
			}
		});

		return txt;
	}

	private void initAnimationTimer()
	{
		if (timer != null)
			timer.cancel();

		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run()
			{
				if (Q2DAnimationDialog.this == null || !Q2DAnimationDialog.this.isVisible())
					this.cancel();
				else
				{
					sprite.update(1000.0 / Q2DEditor.FRAMES_PER_SECOND * 0.001);
					repaint();
				}
			}
		}, 0, 1000 / Q2DEditor.FRAMES_PER_SECOND);
	}

	private void initSprite(String path)
	{
		ImageIcon animation = new ImageIcon(this.getClass().getResource("/" + path));
		int columns = Integer.parseInt(txtAnimationNumColumns.getText());
		int rows = Integer.parseInt(txtAnimationNumRows.getText());
		int fps = Integer.parseInt(txtAnimationsPerSecond.getText());
		int width = Integer.parseInt(txtWidth.getText());
		int height = Integer.parseInt(txtHeight.getText());
		sprite = new Q2DSprite(animation.getImage(), animation.getIconWidth(), animation.getIconHeight(), columns, rows, fps, 0);
		sprite.setSize(width, height);
	}

	private JButton createBrowseButton()
	{
		final JButton btn = new JButton("Browse");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				final JFileChooser chooser = new JFileChooser("resources");
				FileNameExtensionFilter filter = null;
				filter = new FileNameExtensionFilter("*.jpg; *.png", "jpg", "png");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(btn);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = chooser.getSelectedFile();
					path = file.getPath();
					if (path.contains("resources"))
					{
						path = path.substring(path.indexOf("resources") + "resources".length() + 1);

						initSprite(path);
						initAnimationTimer();
						repaint();

						lblPreview.setSize(150, lblPreview.getHeight());
						lblPreview.setText("Preview: " + chooser.getSelectedFile().getName());

						txtAnimationNumColumns.setEnabled(true);
						txtAnimationNumRows.setEnabled(true);
						txtAnimationsPerSecond.setEnabled(true);
						txtHeight.setEnabled(true);
						txtWidth.setEnabled(true);
						btnOK.setEnabled(true);
					}
					else
						JOptionPane.showMessageDialog(btn, "You must select a file from the \"resources\" folder of the project", "Wrong file location", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		return btn;
	}

	public String getAnimationPath()
	{
		return path;
	}

	public int getNumColumns()
	{
		return Integer.parseInt(txtAnimationNumColumns.getText());
	}

	public int getNumRows()
	{
		return Integer.parseInt(txtAnimationNumRows.getText());
	}

	public int getAnimationsPerSecond()
	{
		return Integer.parseInt(txtAnimationsPerSecond.getText());
	}

	public int getAnimationWidth()
	{
		return Integer.parseInt(txtWidth.getText());
	}

	public int getAnimationHeight()
	{
		return Integer.parseInt(txtHeight.getText());
	}

	private class DialogPanel extends JPanel
	{
		public DialogPanel(Dimension dimension)
		{
			super();
			setMinimumSize(dimension);
			setPreferredSize(dimension);
			setBackground(Color.WHITE);
			setLayout(null);
		}

		@Override
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.drawRect(PREVIEW_OFFSET_X - 1, PREVIEW_OFFSET_Y - 1, PREVIEW_SIZE_X + 1, PREVIEW_SIZE_Y + 1);
			if (sprite != null)
				sprite.paint((Graphics2D) graphics, -PREVIEW_OFFSET_X, -PREVIEW_OFFSET_Y);
		}
	}
}
