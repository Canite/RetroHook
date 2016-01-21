package com.austin.retrohook.Screens;

import com.austin.retrohook.RetroHook;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Created by austin on 10/3/15.
 */
public class MainMenuScreen implements Screen {
    private RetroHook game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private Texture titleImage;
    private GlyphLayout messageLayout;

    public MainMenuScreen(RetroHook game) {
        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(RetroHook.V_WIDTH, RetroHook.V_HEIGHT, gameCam);

        titleImage = new Texture("RHTitle.png");

        messageLayout = new GlyphLayout();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        String message;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        message = "Touch to start";
        messageLayout.setText(game.font, message);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        game.font.setColor(Color.WHITE);
        //game.font.draw(game.batch, message, Gdx.graphics.getWidth()/2 - messageLayout.width / 2, Gdx.graphics.getHeight()/2 - messageLayout.height / 2);
        game.batch.draw(titleImage, 0 - titleImage.getWidth() / 2, 0 + titleImage.getHeight()*2);
        game.font.draw(game.batch, message, 0 - messageLayout.width / 2, 0 + messageLayout.height / 2);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.currentLevel += 1;
            game.setScreen(new PlayScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
