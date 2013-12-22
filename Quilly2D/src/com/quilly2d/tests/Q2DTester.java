package com.quilly2d.tests;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.quilly2d.core.Q2DApplication;
import com.quilly2d.core.Q2DEntity;
import com.quilly2d.core.Q2DTimer;
import com.quilly2d.graphics.Q2DSprite;
import com.quilly2d.graphics.Q2DText;
import com.quilly2d.sound.Q2DSound;

public class Q2DTester extends Q2DApplication
{
	private static Q2DApplication	INSTANCE				= null;
	private static Q2DSound			music					= null;
	private static MageEntity		gameEntity				= null;
	private static Q2DText			gameEntityActionInfo	= null;
	private boolean					isUpPressed				= false;
	private boolean					isRightPressed			= false;
	private boolean					isLeftPressed			= false;
	private boolean					isDownPressed			= false;
	private boolean					isPaused				= false;
	private static int				mapSizeX				= 1200;
	private static int				mapSizeY				= 900;
	public static int				renderedSprites			= 0;

	public Q2DTester()
	{
		//super("Quilly2D", 800, 600, true, 60, true);
		super("Quilly2D", 800, 600, false, 60, true);
		this.setWindowIcon("graphics/icons/window.png");
	}

	@Override
	public void onInit()
	{
		INSTANCE = this;

		Q2DSprite map = this.createSprite("graphics/maps/village.png", 0);
		map.setSize(mapSizeX, mapSizeY);
		INSTANCE.setCameraMaximumBoundaries(mapSizeX, mapSizeY);

		gameEntity = (MageEntity) this.createEntity("graphics/characters/boy.png", 4, 3, 6, MageEntity.class);

		gameEntityActionInfo = INSTANCE.createText("Idle", INSTANCE.getFont("FrizQuaBol", 20, true), new Color(255, 250, 250));

		Q2DSprite totem = this.createSprite("graphics/props/totem.png");
		totem.setSize(64, 160);
		totem.setLocation(460, 500);

		Q2DSprite totemBase = this.createSprite("graphics/props/totem_base.png");
		totemBase.setSize(32, 38);
		totemBase.setLocation(476, 620);
		totemBase.enableCollision();

		Q2DSprite npc2 = this.createSprite("graphics/characters/verminator.png", 2, 5, 3);
		npc2.setLocation(200, 550);
		npc2.setSize(48, 48);
		npc2.setLoopAnimations(4, 8);

		Q2DSprite npc3 = this.createSprite("graphics/characters/verminator.png", 2, 5, 5);
		npc3.setLocation(150, 525);
		npc3.setSize(64, 64);
		npc3.setLoopAnimations(0, 3);

		music = this.createSound("sounds/music.mp3");
		music.setVolumne(0.5);
		music.setLoop(true);
		music.play();

		Q2DText text = this.createText("Hello Q2DText", this.getFont("calibri", 20, true), new Color(255, 255, 0));
		text.setLocation(50, 450);
		text = this.createText("A long sentence with automatic linebreak", 15, this.getFont("calibri", 20, true), new Color(0, 0, 0));
		text.setLocation(50, 475);

		INSTANCE.createTimer(2, 2, 14, CameraTestTimer.class);

		Q2DSprite cloudFilter = this.createSprite("graphics/filters/clouds.png");
		cloudFilter.setSize(mapSizeX, mapSizeY);
	}

	@Override
	public void onKeyPressed(int keyCode)
	{
		switch (keyCode)
		{
		case KeyEvent.VK_RIGHT:
			isRightPressed = true;
			break;
		case KeyEvent.VK_LEFT:
			isLeftPressed = true;
			break;
		case KeyEvent.VK_DOWN:
			isDownPressed = true;
			break;
		case KeyEvent.VK_UP:
			isUpPressed = true;
			break;
		case KeyEvent.VK_E:
			if (!isPaused)
				music.pause();
			break;
		case KeyEvent.VK_R:
			if (!isPaused)
				music.play();
			break;
		case KeyEvent.VK_C:
			this.setCameraFocusOnEntity(gameEntity, false);
			break;
		case KeyEvent.VK_O:
			this.showCursor(true);
			break;
		case KeyEvent.VK_L:
			this.showCursor(false);
			break;
		case KeyEvent.VK_NUMPAD8:
			this.setCameraLocation(this.getCameraX(), this.getCameraY() - 10);
			break;
		case KeyEvent.VK_NUMPAD2:
			this.setCameraLocation(this.getCameraX(), this.getCameraY() + 10);
			break;
		case KeyEvent.VK_NUMPAD4:
			this.setCameraLocation(this.getCameraX() - 10, this.getCameraY());
			break;
		case KeyEvent.VK_NUMPAD6:
			this.setCameraLocation(this.getCameraX() + 10, this.getCameraY());
			break;
		case KeyEvent.VK_ADD:
			FireSpell fireSpell = (FireSpell) this.createEntity("graphics/effects/fire_cast.png", 5, 1, 6, FireSpell.class);
			fireSpell.setCaster(gameEntity);
			break;
		case KeyEvent.VK_PAUSE:
			isPaused = !isPaused;
			this.pauseGame(isPaused);
			break;
		case KeyEvent.VK_ESCAPE:
			this.stopGame();
		}

		if (!isPaused)
			checkMageEntityMovement();
	}

	@Override
	public void onKeyReleased(int keyCode)
	{
		switch (keyCode)
		{
		case KeyEvent.VK_RIGHT:
			isRightPressed = false;
			break;
		case KeyEvent.VK_LEFT:
			isLeftPressed = false;
			break;
		case KeyEvent.VK_DOWN:
			isDownPressed = false;
			break;
		case KeyEvent.VK_UP:
			isUpPressed = false;
			break;
		}

		if (!isPaused)
			checkMageEntityMovement();
	}

	@Override
	public void onMousePressed(int mouseButton, int mouseX, int mouseY)
	{
		if (gameEntity.isLocationInside(mouseX + getCameraX(), mouseY + getCameraY()))
		{
			if (mouseButton == MouseEvent.BUTTON1)
				gameEntity.setSize(150, 150);
			else
				gameEntity.setSize(64, 64);
		}
	}

	@Override
	public void onMouseReleased(int mouseButton, int mouseX, int mouseY)
	{
	}

	private void checkMageEntityMovement()
	{
		if ((!isUpPressed || !isDownPressed) && (!isLeftPressed || !isRightPressed))
		{
			int angle = 0;
			if (isUpPressed)
			{
				if (isLeftPressed)
					angle = 135;
				else if (isRightPressed)
					angle = 45;
				else
					angle = 90;
			}
			else if (isDownPressed)
			{
				if (isLeftPressed)
					angle = 225;
				else if (isRightPressed)
					angle = 315;
				else
					angle = 270;
			}
			else if (isLeftPressed)
				angle = 180;
			else if (isRightPressed)
				angle = 0;

			if (!isUpPressed && !isRightPressed && !isLeftPressed && !isDownPressed)
				gameEntity.stop();
			else
				gameEntity.move(angle);
		}
	}

	public static class MageEntity extends Q2DEntity
	{
		private int	oldX	= 0;
		private int	oldY	= 0;

		public MageEntity(Image img, int width, int height, int numColumns, int numRows, double animationsPerSecond, int layer)
		{
			super(img, width, height, numColumns, numRows, animationsPerSecond, layer);
			setSize(64, 64);
			setAnimation(8);
			stopAnimation();
			setLocation(234, 300);
			setDirection(270);
			enableCollision();
		}

		public void move(int angle)
		{
			this.setDirection(angle);
			// move 300 pixels per second
			this.setVelocity(300);

			if (angle >= 45 && angle <= 135)
			{
				setLoopAnimations(0, 3);
				setFlippedHorizontal(false);
			}
			else if (angle > 135 && angle < 225)
			{
				setLoopAnimations(4, 7);
				setFlippedHorizontal(false);
			}
			else if (angle >= 225 && angle <= 315)
			{
				setLoopAnimations(8, 11);
				setFlippedHorizontal(false);
			}
			else
			{
				setLoopAnimations(4, 7);
				setFlippedHorizontal(true);
			}

			startAnimation();
		}

		public void stop()
		{
			setVelocity(0);
			stopAnimation();
		}

		@Override
		public void beforeUpdate()
		{
			oldX = this.getX();
			oldY = this.getY();

			gameEntityActionInfo.setLocation((int) (getCenterX() - gameEntityActionInfo.getWidth() / 2 - INSTANCE.getCameraX()), getY() - gameEntityActionInfo.getHeight() - INSTANCE.getCameraY());
		}

		@Override
		public void afterUpdate()
		{
			if (INSTANCE.getCollisionSprites(this).size() > 0)
				setLocation(oldX, oldY);

			if (INSTANCE.getEntitiesInRange(this, 100).size() > 0)
				gameEntityActionInfo.setText("In range of other entities");
		}

		@Override
		public void onRemove()
		{
		}
	}

	public static class FireSpell extends Q2DEntity
	{

		public FireSpell(Image img, int width, int height, int numColumns, int numRows, double animationsPerSecond, int layer)
		{
			super(img, width, height, numColumns, numRows, animationsPerSecond, layer);
		}

		public void setCaster(MageEntity caster)
		{
			FireTimer timer = (FireTimer) INSTANCE.createTimer(0.5, 0.5, 4, FireTimer.class);
			timer.setSpellInstance(this);

			Q2DSound sound = INSTANCE.createSound("sounds/fire_cast.mp3");
			sound.setVolumne(0.5);
			sound.play();

			setLoopAnimations(0, 1);
			setLocation(caster.getX(), caster.getY());
			setVelocity(150);
			setDirection(caster.getDirection());

			gameEntityActionInfo.setText("Casting");
		}

		@Override
		public void beforeUpdate()
		{
		}

		@Override
		public void afterUpdate()
		{
		}

		@Override
		public void onRemove()
		{
			gameEntityActionInfo.setText("Idle");
		}

		public static class FireTimer extends Q2DTimer
		{
			private FireSpell		spellInstance	= null;
			private List<Q2DSprite>	areaFX			= null;
			private int				spellPhase		= 0;

			public FireTimer(double delayTime, double periodicTime, double endTime)
			{
				super(delayTime, periodicTime, endTime);
			}

			public void setSpellInstance(FireSpell instance)
			{
				spellInstance = instance;
				areaFX = new ArrayList<Q2DSprite>();
			}

			@Override
			public void onTick()
			{
				double totalTime = this.getTotalTime();
				if (totalTime >= 1.5 && spellPhase == 2)
				{
					++spellPhase;
					spellInstance.hide();

					gameEntityActionInfo.setText("Exploding Firesphere");

					Q2DSound sound = INSTANCE.createSound("sounds/fire_effect.mp3");
					sound.setVolumne(0.5);
					sound.play();

					double spellX = spellInstance.getCenterX();
					double spellY = spellInstance.getCenterY();
					int offset = 35;
					double angle = Math.PI / 2;
					for (int i = 0; i < 4; i++)
					{
						Q2DSprite sprite = INSTANCE.createSprite("graphics/effects/fire_area.png", 8, 1, 6);
						double currentAngle = angle * i;
						double newX = spellX + offset * Math.cos(currentAngle);
						double newY = spellY - offset * Math.sin(currentAngle);
						newX -= sprite.getWidth() / 1.5;
						newY -= sprite.getHeight() / 1.5;
						sprite.setLocation((int) newX, (int) newY);
						areaFX.add(sprite);
					}
				}
				else if (totalTime >= 1.0 && spellPhase == 1)
				{
					++spellPhase;
					spellInstance.setAnimation(4);
					spellInstance.stopAnimation();
				}
				else if (totalTime >= 0.5 && spellPhase == 0)
				{
					++spellPhase;
					spellInstance.setLoopAnimations(2, 3);
				}
			}

			@Override
			public void onFinish()
			{
				for (Q2DSprite sprite : areaFX)
					sprite.remove();
				spellInstance.remove();
			}
		}
	}

	public static class CameraTestTimer extends Q2DTimer
	{
		public CameraTestTimer(double delayTime, double periodicTime, double endTime)
		{
			super(delayTime, periodicTime, endTime);
		}

		private int	phase	= 0;

		@Override
		public void onTick()
		{
			if (this.getTotalTime() >= 10 && phase == 5)
			{
				phase++;
				INSTANCE.panCamera(0, 0, 2);
			}
			else if (this.getTotalTime() >= 8 && phase == 4)
			{
				phase++;
				INSTANCE.panCamera(mapSizeX, mapSizeY, 2);
			}
			else if (this.getTotalTime() >= 6 && phase == 3)
			{
				phase++;
				INSTANCE.panCamera(0, 0, 2);
			}
			else if (this.getTotalTime() >= 4 && phase == 2)
			{
				phase++;
				INSTANCE.panCamera(0, mapSizeY, 2);
			}
			else if (this.getTotalTime() >= 2 && phase == 1)
			{
				phase++;
				INSTANCE.panCamera(0, 0, 2);
			}
			else if (phase == 0)
			{
				phase++;
				INSTANCE.panCamera(mapSizeX, 0, 2);
			}
		}

		@Override
		public void onFinish()
		{
			INSTANCE.setCameraFocusOnEntity(gameEntity, true);
		}

	}

	public static void main(String[] args)
	{
		new Q2DTester();
	}
}
