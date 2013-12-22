package com.quilly2d.editor.core;

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

@SuppressWarnings("serial")
public class Q2DEditorSplitPane extends JSplitPane
{
	private JScrollPane				leftScrollPane	= null;
	private Q2DEditorTilesetPanel	tilesetPanel	= null;
	private JScrollPane				rightScrollPane	= null;
	private Q2DEditorMapPanel		mapPanel		= null;

	public Q2DEditorSplitPane(Dimension screenSize)
	{
		super(JSplitPane.HORIZONTAL_SPLIT);

		tilesetPanel = new Q2DEditorTilesetPanel(new Dimension(screenSize.width / 3, screenSize.height));
		leftScrollPane = new JScrollPane(tilesetPanel);
		leftScrollPane.setMinimumSize(tilesetPanel.getMinimumSize());

		mapPanel = new Q2DEditorMapPanel(new Dimension(2 * screenSize.width / 3, screenSize.height));
		rightScrollPane = new JScrollPane(mapPanel);
		rightScrollPane.setMinimumSize(mapPanel.getMinimumSize());

		setOneTouchExpandable(false);
		setLeftComponent(leftScrollPane);
		setRightComponent(rightScrollPane);
		setResizeWeight(0.33);
	}
}
