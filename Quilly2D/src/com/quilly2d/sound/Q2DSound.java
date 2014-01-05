package com.quilly2d.sound;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Q2DSound implements Runnable
{
	private Media					media		= null;
	private MediaPlayer				player		= null;
	private boolean					isStopped	= false;
	private static List<Q2DSound>	soundBuffer	= new ArrayList<Q2DSound>();

	public Q2DSound(String filePath)
	{
		URL resource = this.getClass().getResource("/" + filePath);

		media = new Media(resource.toString());
		player = new MediaPlayer(media);
		player.setOnEndOfMedia(this);

		soundBuffer.add(this);
	}

	public void play()
	{
		player.play();
	}

	public void pause()
	{
		player.pause();
	}

	public void stop()
	{
		player.stop();
		player.dispose();
	}

	public boolean hasStopped()
	{
		return isStopped;
	}

	public void setVolumne(double newVolumne)
	{
		player.setVolume(newVolumne);
	}

	public void setLoop(boolean loop)
	{
		if (loop)
			player.setCycleCount(AudioClip.INDEFINITE);
		else
			player.setCycleCount(1);
	}

	@Override
	public void run()
	{
		stop();
		isStopped = true;
		soundBuffer.remove(this);
	}

	public static void pauseAllSounds(boolean pause)
	{
		if (pause)
		{
			for (Q2DSound sound : soundBuffer)
				sound.pause();
		}
		else
		{
			for (Q2DSound sound : soundBuffer)
				sound.play();
		}
	}

	public static void stopAllSounds()
	{
		for (Q2DSound sound : soundBuffer)
			sound.stop();
	}
}
