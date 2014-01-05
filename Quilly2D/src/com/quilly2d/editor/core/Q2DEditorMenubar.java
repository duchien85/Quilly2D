package com.quilly2d.editor.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.quilly2d.tools.Q2DEditor;

public class Q2DEditorMenubar
{
	private JFrame			parent				= null;
	private JMenuBar		menuBar				= null;
	private JMenu			menu				= null;
	private final String	ITEM_TEXT_SEPARATOR	= "SEPARATOR";
	private final String	ITEM_TEXT_NEW		= "New";
	private final String	ITEM_TEXT_SAVE		= "Save";
	private final String	ITEM_TEXT_LOAD		= "Load";
	private final String	ITEM_TEXT_TEST		= "Test";
	private final String	ITEM_TEXT_QUIT		= "Quit";
	private final char		noHotkey			= ' ';
	private String[]		fileMenuEntries		= { ITEM_TEXT_NEW, ITEM_TEXT_SEPARATOR, ITEM_TEXT_SAVE, ITEM_TEXT_LOAD, ITEM_TEXT_SEPARATOR, ITEM_TEXT_TEST, ITEM_TEXT_SEPARATOR, ITEM_TEXT_QUIT };
	private char[]			fileMenuHotkeys		= { 'N', noHotkey, 'S', 'L', noHotkey, 'T', noHotkey, noHotkey };

	public Q2DEditorMenubar(JFrame parent)
	{
		this.parent = parent;
		menuBar = new JMenuBar();

		menu = new JMenu("File");
		for (int i = 0; i < fileMenuEntries.length; ++i)
			addMenuItem(fileMenuEntries[i], fileMenuHotkeys[i]);
		menuBar.add(menu);

		//TODO add edit menu

		//TODO add about menu

		parent.setJMenuBar(menuBar);
	}

	private void addMenuItem(String text, char hotkey)
	{
		if (ITEM_TEXT_SEPARATOR.equals(text))
			menu.addSeparator();
		else
		{
			JMenuItem menuItem = new JMenuItem(text);
			if (hotkey != noHotkey)
				menuItem.setAccelerator(KeyStroke.getKeyStroke(hotkey, KeyEvent.CTRL_DOWN_MASK));
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event)
				{
					JMenuItem item = (JMenuItem) event.getSource();

					//TODO add menuitem logic
					if (item.getText().equals(ITEM_TEXT_SAVE))
						Q2DEditor.INSTANCE.save();
					else if (item.getText().equals(ITEM_TEXT_LOAD))
						Q2DEditor.INSTANCE.load();
					else if (item.getText().equals(ITEM_TEXT_TEST))
						Q2DEditor.INSTANCE.test();
					else if (item.getText().equals(ITEM_TEXT_QUIT))
						parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
				}
			});
			menu.add(menuItem);
		}
	}
}
