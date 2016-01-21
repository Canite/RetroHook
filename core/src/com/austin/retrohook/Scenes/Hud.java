package com.austin.retrohook.Scenes;

import com.austin.retrohook.RetroHook;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 * Created by austin on 9/28/15.
 */
public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    //private Integer worldTimer;
    //private float timeCount;
    public float debug1;
    public float debug2;

    //Label countDownLabel;
    Label debugLabel1;
    Label debugLabel2;

    public Hud(SpriteBatch sb) {
        //worldTimer = 300;
        //timeCount = 0;
        debug1 = 0.0f;
        debug2 = 0.0f;

        viewport = new FitViewport(RetroHook.V_WIDTH, RetroHook.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        debugLabel1 = new Label(String.format("%03f", debug1), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        debugLabel2 = new Label(String.format("%03f", debug2), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add().expandX();
        table.add(debugLabel1).expandX();
        table.add(debugLabel2).expandX();

        stage.addActor(table);
    }

    public void setDebug(float val1, float val2) {
        debugLabel1.setText(String.format("%03f", val1));
        debugLabel2.setText(String.format("%03f", val2));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
