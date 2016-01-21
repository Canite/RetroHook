package com.austin.retrohook.Screens;

import com.austin.retrohook.RetroHook;
import com.austin.retrohook.Scenes.Hud;
import com.austin.retrohook.Sprites.Guy;
import com.austin.retrohook.Sprites.MapSprite;
import com.austin.retrohook.Tools.B2WorldCreator;
import com.austin.retrohook.Tools.WorldContactListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by austin on 9/28/15.
 */
public class PlayScreen implements Screen, InputProcessor{
    private RetroHook game;
    private TextureAtlas atlas;

    private OrthographicCamera gameCam;
    private Hud hud;
    private Viewport gamePort;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    // player
    private Guy guy;

    private boolean transition;

    // Map properties
    public int mapWidth, mapHeight, tileWidth, tileHeight, mapPixelWidth, mapPixelHeight;
    public Array<MapSprite> mapSprites = new Array<MapSprite>();

    public class TouchInfo {
        public int touchX = 0;
        public int touchY = 0;
        public boolean touched = false;
        public String type = "";
    }

    private Map<Integer, TouchInfo> touches = new HashMap<Integer, TouchInfo>();

    public PlayScreen(RetroHook game) {
        Gdx.app.log("started", "play screen");
        atlas = new TextureAtlas("RHPack.pack");

        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(RetroHook.V_WIDTH / RetroHook.PPM, RetroHook.V_HEIGHT / RetroHook.PPM, gameCam);
        hud = new Hud(game.batch);

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        params.textureMinFilter = Texture.TextureFilter.Nearest;

        mapLoader = new TmxMapLoader();
        map = mapLoader.load(String.format("level%d.tmx", game.currentLevel), params);

        MapProperties mapProperties = map.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tileWidth = mapProperties.get("tilewidth", Integer.class);
        tileHeight = mapProperties.get("tileheight", Integer.class);
        mapPixelWidth = mapWidth * tileWidth;
        mapPixelHeight = mapHeight * tileHeight;

        renderer = new OrthogonalTiledMapRenderer(map, 1 / RetroHook.PPM);

        gameCam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);

        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map, this);

        guy = new Guy(world, this);

        world.setContactListener(new WorldContactListener());

        Gdx.input.setInputProcessor(this);
        // set up touch screen controls
        // 3 touches will be handled at a time
        for (int i = 0; i < 3; i++) {
            touches.put(i, new TouchInfo());
        }

        transition = false;
    }

    public static void fixBleeding(TextureRegion region) {
        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();
        region.setRegion((x + 1.0f) * invTexWidth, (y + 1.0f) * invTexHeight, (x + width - 1.0f) * invTexWidth, (y + height - 1.0f) * invTexHeight);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        for (int i = 0; i < 3; i++) {
            guy.handleInput(touches.get(i));
        }
    }

    public void restartLevel() {
        Gdx.app.log("screen", "restart");
        game.setScreen(new PlayScreen(game));
    }

    public void setTransition(boolean value) {
        transition = value;
    }

    public boolean getTransition() {
        return transition;
    }

    public void update(float dt) {
        if (!transition) {
            handleInput(dt);

            world.step(1 / 60f, 20, 10);

            for (int i = 0; i < mapSprites.size; i++) {
                mapSprites.get(i).update(dt);
            }

            guy.update(dt);

            hud.setDebug(guy.b2body.getPosition().y, mapPixelHeight / RetroHook.PPM);
            // center camera on players body
            if (guy.b2body.getPosition().x > (RetroHook.V_WIDTH / 2) / RetroHook.PPM && guy.b2body.getPosition().x < (mapPixelWidth - (RetroHook.V_WIDTH / 2)) / RetroHook.PPM)
                gameCam.position.x = guy.b2body.getPosition().x;
            if (guy.b2body.getPosition().y > (RetroHook.V_HEIGHT / 2) / RetroHook.PPM && guy.b2body.getPosition().y < (mapPixelHeight - (RetroHook.V_HEIGHT / 2)) / RetroHook.PPM)
                gameCam.position.y = guy.b2body.getPosition().y;

            gameCam.update();

            renderer.setView(gameCam);
        } else {
            game.currentLevel += 1;
            if (game.currentLevel > game.maxLevel)
                game.currentLevel = 1;
            game.setScreen(new PlayScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render graphics (in gameCam view)
        renderer.render();

        // render box2d world objects in debugger
        //b2dr.render(world, gameCam.combined);

        // render player, enemies, etc.
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        for (int i = 0; i < mapSprites.size; i++) {
            mapSprites.get(i).draw(game.batch);
        }
        guy.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (pointer < 3) {
            touches.get(pointer).touchX = screenX;
            touches.get(pointer).touchY = screenY;
            touches.get(pointer).touched = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer < 3) {
            touches.get(pointer).touchX = 0;
            touches.get(pointer).touchY = 0;
            touches.get(pointer).touched = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer < 3 && touches.get(pointer).type == "move" && screenX < Gdx.graphics.getWidth() / 3) {
            touches.get(pointer).touchX = screenX;
            touches.get(pointer).touchY = screenY;
            touches.get(pointer).touched = true;
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
