package com.quilly2d.editor.core;

import java.awt.Dimension;
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
