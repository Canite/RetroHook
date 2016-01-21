package com.austin.retrohook.Sprites;

import com.austin.retrohook.RetroHook;
import com.austin.retrohook.Screens.PlayScreen;
import com.austin.retrohook.Tools.BodyData;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


/**
 * Created by austin on 10/4/15.
 */
public class MovingPlatform extends MapSprite{
    public float xSpeed;
    public float ySpeed;
    private float leftSide;
    private float pause;
    private float pauseTimer;
    private float rightSide;
    private float topSide;
    private float bottomSide;
    private Fixture fixture;
    private World world;
    private PlayScreen screen;
    private Rectangle rect;
    private TextureRegion platformImage;
    public Body passenger;

    public MovingPlatform(World world, TiledMap map, Rectangle bounds, PlayScreen screen, float xspeed, float yspeed, float leftside, float rightside, float top, float bottom, float pause) {
        this.world = world;
        this.screen = screen;
        this.rect = bounds;
        this.pause = pause;
        pauseTimer = 0;
        xSpeed = xspeed;
        ySpeed = yspeed;
        leftSide = (leftside * screen.tileWidth) / RetroHook.PPM;
        rightSide = (rightside * screen.tileWidth) / RetroHook.PPM;
        topSide = (screen.mapPixelHeight - (top * screen.tileWidth)) / RetroHook.PPM;
        bottomSide = (screen.mapPixelHeight - (bottom * screen.tileWidth)) / RetroHook.PPM;

        // define sprite
        platformImage = new TextureRegion(screen.getAtlas().findRegion("platform"), 0, 0, 16, 16);
        PlayScreen.fixBleeding(platformImage);
        platformImage.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setBounds(0, 0, rect.getWidth() / RetroHook.PPM, rect.getHeight() / RetroHook.PPM);
        setRegion(platformImage);

        // define body
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.type = BodyDef.BodyType.KinematicBody;
        bdef.position.set((rect.getX() + rect.getWidth()/2) / RetroHook.PPM, (rect.getY() + rect.getHeight()/2) / RetroHook.PPM);

        body = world.createBody(bdef);

        shape.setAsBox((rect.getWidth()/2) / RetroHook.PPM, (rect.getHeight()/2) / RetroHook.PPM);
        fdef.shape = shape;
        fdef.friction = 0.55f;
        fixture = body.createFixture(fdef);
        fixture.setUserData(new BodyData(this, "movingplatform"));

        //body.setGravityScale(0);
        body.setLinearVelocity(xSpeed, ySpeed);
    }

    public void update(float dt) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        if (pauseTimer == 0) {
            if ((body.getPosition().x - getWidth() / 2 <= leftSide && xSpeed < 0) || (body.getPosition().x + getWidth() / 2 >= rightSide && xSpeed > 0)) {
                pauseTimer = pause;
                xSpeed = -xSpeed;
                if (passenger != null) {
                    if (Math.abs(passenger.getLinearVelocity().x + xSpeed) < 1.0f)
                        passenger.applyLinearImpulse(new Vector2(xSpeed * 2, 0), passenger.getWorldCenter(), true);
                }
            }
            if ((body.getPosition().y - getWidth() / 2 <= bottomSide && ySpeed < 0) || (body.getPosition().y + getWidth() / 2 >= topSide && ySpeed > 0)) {
                pauseTimer = pause;
                ySpeed = -ySpeed;
                if (passenger != null) {
                    if (Math.abs(passenger.getLinearVelocity().y + ySpeed) < 1.0f)
                        passenger.applyLinearImpulse(new Vector2(0, ySpeed * 2), passenger.getWorldCenter(), true);
                }
            }
            body.setLinearVelocity(xSpeed, ySpeed);
        } else {
            body.setLinearVelocity(0, 0);
            pauseTimer -= 1;
        }
    }

    public void onFeetHit() {
    }
}
