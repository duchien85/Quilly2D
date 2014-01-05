package com.quilly2d.editor.core;

import javax.swing.JFrame;

import com.quilly2d.core.Q2DApplication;

public class Q2DEditorTestApplication extends Q2DApplication
{

	public Q2DEditorTestApplication(String windowTitle, int width, int height, int fps, int numLayers)
	{
		super(windowTitle, width, height, false, fps, numLayers, true);
		try
		{
			Class<?> c = Q2DApplication.class;
			java.lang.reflect.Field field = c.getDeclaredField("frame");
			field.setAccessible(true);
			JFrame frame = (JFrame) field.get(this);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		catch (Exception e)
		{
			//TODO errormsg
			e.printStackTrace();
		}
	}

	@Override
	public void onInit()
	{
	}

	@Override
	public void onKeyPressed(int keyCode)
	{
	}

	@Override
	public void onKeyReleased(int keyCode)
	{
	}

	@Override
	public void onMousePressed(int mouseButton, int mouseX, int mouseY)
	{
	}

	@Override
	public void onMouseReleased(int mouseButton, int mouseX, int mouseY)
	{
	}
}
