package com.austin.retrohook;

import com.austin.retrohook.Screens.MainMenuScreen;
import com.austin.retrohook.Screens.PlayScreen;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RetroHook extends Game{
	public static final int V_WIDTH = 640;
	public static final int V_HEIGHT = 360;
	// Pixels per Meter
	public static final float PPM = 128;

	public SpriteBatch batch;
	public BitmapFont font;
	public int currentLevel, maxLevel;

	@Override
	public void create () {
		currentLevel = 0;
		maxLevel = 3;
		batch = new SpriteBatch();
		font = new BitmapFont();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
