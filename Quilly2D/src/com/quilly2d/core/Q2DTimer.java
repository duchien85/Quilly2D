package com.quilly2d.core;

public abstract class Q2DTimer
{
	private double	totalTime		= 0.0;
	private double	delayTime		= 0.0;
	private double	periodicTime	= 0.0;
	private double	endTime			= 0.0;
	private double	currentTime		= 0.0;
	private boolean	isFinished		= false;

	public Q2DTimer(double delayTime, double periodicTime, double endTime)
	{
		this.delayTime = delayTime;
		this.periodicTime = periodicTime;
		this.endTime = endTime;
		currentTime = 0.0;
	}

	public void update(double deltaTime)
	{
		if (isFinished)
			return;

		totalTime += deltaTime;
		if (endTime != 0 && totalTime >= endTime)
		{
			isFinished = true;
			return;
		}

		if (delayTime <= 0)
		{
			if (periodicTime == 0)
			{
				isFinished = true;
				return;
			}
			else
			{
				currentTime -= deltaTime;
				if (currentTime <= 0)
				{
					onTick();
					currentTime = periodicTime;
				}
			}
		}
		else
		{
			delayTime -= deltaTime;
			currentTime = 0.0;
		}
	}

	public void stop()
	{
		isFinished = true;
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public double getTotalTime()
	{
		return totalTime;
	}

	abstract public void onTick();

	abstract public void onFinish();
}
