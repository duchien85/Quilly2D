package com.quilly2d.editor.core;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.quilly2d.tools.Q2DEditor;

@SuppressWarnings("serial")
public class Q2DEditorSplitPane extends JSplitPane implements PropertyChangeListener
{
	private JScrollPane				leftScrollPane	= null;
	private Q2DEditorTilesetPanel	tilesetPanel	= null;
	private JScrollPane				rightScrollPane	= null;
	private Q2DEditorMapPanel		mapPanel		= null;

	public Q2DEditorSplitPane(int maxWidth, int maxHeight)
	{
		super(JSplitPane.HORIZONTAL_SPLIT);

		tilesetPanel = new Q2DEditorTilesetPanel(new Dimension(maxWidth / 3, maxHeight));
		leftScrollPane = new JScrollPane(tilesetPanel);
		leftScrollPane.setMinimumSize(tilesetPanel.getMinimumSize());

		mapPanel = new Q2DEditorMapPanel(new Dimension(2 * maxWidth / 3, maxHeight));
		rightScrollPane = new JScrollPane(mapPanel);
		rightScrollPane.setMinimumSize(mapPanel.getMinimumSize());

		setOneTouchExpandable(false);
		setLeftComponent(leftScrollPane);
		setRightComponent(rightScrollPane);
		setResizeWeight(0.33);

		Q2DEditor.INSTANCE.addPropertyChangeListener(this);
		Q2DEditor.INSTANCE.addPropertyChangeListener(tilesetPanel);
		Q2DEditor.INSTANCE.addPropertyChangeListener(mapPanel);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent(KeyEvent event)
			{
				if (event.getID() == KeyEvent.KEY_PRESSED && event.isControlDown())
				{
					switch (event.getKeyCode())
					{
						case KeyEvent.VK_0:
						case KeyEvent.VK_1:
						case KeyEvent.VK_2:
						case KeyEvent.VK_3:
						case KeyEvent.VK_4:
						case KeyEvent.VK_5:
						case KeyEvent.VK_6:
							int layer = event.getKeyCode() - 49;
							if (layer < Q2DEditor.INSTANCE.getNumLayers())
								Q2DEditor.INSTANCE.setCurrentLayer(layer);
							break;
					}
				}
				return false;
			}
		});
	}

	public void onPencilPaste()
	{
		tilesetPanel.onPencilPaste();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		tilesetPanel.repaint();
		mapPanel.repaint();
	}
}
