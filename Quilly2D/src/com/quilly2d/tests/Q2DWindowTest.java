package com.quilly2d.tests;

import com.quilly2d.core.Q2DApplication;

public class Q2DWindowTest extends Q2DApplication
{

	public Q2DWindowTest()
	{
		super(	"Fenster Test",		// window title 
				800, 				// window width
				600, 				// window height
				false, 				// fullscreen?
				60, 				// frames per second
				true);				// trippleBuffering?
		this.setWindowIcon("graphics/icons/window.png");
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

	public static void main(String[] args)
	{
		new Q2DWindowTest();
	}
}
