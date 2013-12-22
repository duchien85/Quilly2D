package com.quilly2d.tests;

import java.awt.event.KeyEvent;

import com.quilly2d.core.Q2DApplication;
import com.quilly2d.core.Q2DTimer;
import com.quilly2d.sound.Q2DSound;

public class Q2DTimerTest extends Q2DApplication
{
	public Q2DTimerTest()
	{
		super("Timer Test", 	// window title 
				640, 			// window width
				480, 			// window height
				true,			// fullscreen?
				60, 			// frames per second
				true); 			// trippleBuffering?
		this.setWindowIcon("graphics/icons/window.png");
	}

	@Override
	public void onInit()
	{
		Q2DTimer timer = this.createTimer(	1.0, 				// start delay time in seconds
											1.0, 				// timer periodic time in seconds
											3.0, 				// timer duration in seconds
											TestTimer.class);	// timer type to be created
		Q2DSound sndFX = this.createSound("sounds/fire_cast.mp3");
		((TestTimer)timer).setSndFX(sndFX);
	}

	@Override
	public void onKeyPressed(int keyCode)
	{
	}

	@Override
	public void onKeyReleased(int keyCode)
	{
		if( KeyEvent.VK_ESCAPE == keyCode )
			this.stopGame();
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
		new Q2DTimerTest();
	}

	public static class TestTimer extends Q2DTimer
	{
		private Q2DSound	sndFX	= null;

		public TestTimer(double delayTime, double periodicTime, double endTime)
		{
			super(delayTime, periodicTime, endTime);
		}

		@Override
		public void onTick()
		{
			sndFX.setVolumne(0.5);
			sndFX.setLoop(false);
			sndFX.play();
		}

		@Override
		public void onFinish()
		{
			sndFX.setVolumne(0.5);
			sndFX.setLoop(false);
			sndFX.play();
		}

		public Q2DSound getSndFX()
		{
			return sndFX;
		}

		public void setSndFX(Q2DSound sndFX)
		{
			this.sndFX = sndFX;
		}

	}
}
