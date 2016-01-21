package com.austin.retrohook.Sprites;

import com.austin.retrohook.RetroHook;
import com.austin.retrohook.Screens.PlayScreen;
import com.austin.retrohook.Tools.BodyData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by austin on 9/28/15.
 */
public class Guy extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, HOOKING}
    public State currentState;
    public State previousState;
    public World world;
    public PlayScreen screen;
    public Body b2body;
    private TextureRegion guyStand;
    private Animation guyRun;
    private Hook hook;
    private float stateTimer;
    private boolean runningRight;
    private boolean onGround;
    private boolean hooked;
    private boolean moving;
    public boolean hookOut;
    private int hookTimer;

    public Guy(World world, PlayScreen screen) {
        //super(screen.getAtlas().findRegion("guy"));
        this.world = world;
        this.screen = screen;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        hookTimer = 0;
        runningRight = true;
        onGround = true;
        hooked = false;
        moving = false;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 5; i++) {
            TextureRegion frameRegion;
            frameRegion = new TextureRegion(screen.getAtlas().findRegion("guy"), i*16, 0, 16, 16);
            PlayScreen.fixBleeding(frameRegion);
            frames.add(frameRegion);
        }

        guyRun = new Animation(0.1f, frames);
        frames.clear();

        guyStand = new TextureRegion(screen.getAtlas().findRegion("guy"), 0, 0, 16, 16);
        // Fix bleeding on standing animation
        PlayScreen.fixBleeding(guyStand);
        guyStand.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setBounds(0, 0, 16 / RetroHook.PPM, 16 / RetroHook.PPM);
        setRegion(guyStand);

        // define collision body and fixtures
        defineGuy();
        Gdx.app.log("guy", "created");
    }

    public void update(float dt) {
        // Set players sprite position to the position of our collision box
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        // Set region (sprite) to our current frame in animation
        setRegion(getFrame(dt));

        if (b2body.getPosition().y < 0) {
            kill();
        }
        if (hookOut && !hooked) {
            hookTimer += 1;
        }
        if (hook != null) {
            hook.update(dt);
            if (hookTimer > 10 || b2body.getPosition().dst(hook.b2body.getPosition()) > hook.maxDist + 0.2f)  {
                hook.cleanUp();
                hook = null;
                hookTimer = 0;
                hookOut = false;
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        if (hook != null) {
            hook.draw(batch);
        }
        super.draw(batch);
    }

    public void kill() {
       screen.restartLevel();
    }

    public void setOnGround(boolean grounded) {
        onGround = grounded;
    }

    public boolean getOnGround() {
        return onGround;
    }

    public void setHooked (boolean isHooked) {
        hooked = isHooked;
    }

    public boolean getHooked() {
        return hooked;
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch(currentState) {
            case JUMPING:
                region = guyStand;
                break;
            case RUNNING:
                                                        // looping
                region = guyRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = guyStand;
                break;
        }

        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        // if currentState = previousState, then add to stateTimer otherwise reset to 0
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState() {
        /*if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0) && previousState == State.JUMPING)
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
        */
        if (onGround && b2body.getLinearVelocity().x != 0 && moving) {
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void handleInput(PlayScreen.TouchInfo touchInfo) {
        float maxXVelocity;
        if (hooked && !onGround) {
            maxXVelocity = 4f;
        } else {
            maxXVelocity = 1.7f;
        }

        if (touchInfo.touched) {
            Gdx.app.log("screen touched", Float.toString(touchInfo.touchX) + " " + Float.toString(touchInfo.touchY));
            if (touchInfo.touchX < Gdx.graphics.getWidth() / 6) {
                if (b2body.getLinearVelocity().x >= -maxXVelocity)
                    b2body.applyLinearImpulse(new Vector2(-0.13f, 0), b2body.getWorldCenter(), true);
                touchInfo.type = "move";
                moving = true;
            } else if (touchInfo.touchX < Gdx.graphics.getWidth() / 3) {
                if (b2body.getLinearVelocity().x <= maxXVelocity)
                    b2body.applyLinearImpulse(new Vector2(0.13f, 0), b2body.getWorldCenter(), true);
                touchInfo.type = "move";
                moving = true;
            } else if (touchInfo.touchX < Gdx.graphics.getWidth() / 3) {
            } else if (onGround && touchInfo.type != "hook") {
                onGround = false;
                b2body.applyLinearImpulse(new Vector2(0, 2.7f), b2body.getWorldCenter(), true);
                if (!hooked)
                    touchInfo.touched = false;
            } else if (!hookOut && touchInfo.type != "hook") {
                hookOut = true;
                Vector2 hookDir;
                hookDir = runningRight ? new Vector2(5.0f, 5.0f) : new Vector2(-5.0f, 5.0f);
                hook = new Hook(world, screen, this, hookDir, new Vector2(b2body.getPosition().x + 6 / RetroHook.PPM, b2body.getPosition().y + 6 / RetroHook.PPM), 20);
                touchInfo.type = "hook";
            }
        } else {
            if (touchInfo.type == "hook") {
                if (hook != null) {
                    hook.cleanUp();
                    hook = null;
                    hookOut = false;
                    hookTimer = 0;
                    hooked = false;
                }
            } else if (touchInfo.type == "move") {
                moving = false;
            }
            touchInfo.type = "";
        }
    }

    public void defineGuy() {
        BodyDef bdef = new BodyDef();
        // set position in level 1
        bdef.position.set((8*16) / RetroHook.PPM, (9*16) / RetroHook.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);
        b2body.setBullet(true);

        FixtureDef fdef = new FixtureDef();
        //CircleShape shape = new CircleShape();
        //shape.setRadius(7.5f / RetroHook.PPM);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6.0f / RetroHook.PPM, 6.0f / RetroHook.PPM);

        fdef.shape = shape;
        fdef.friction = 0.4f;
        b2body.createFixture(fdef).setUserData(new BodyData(this, "guyBody"));

        EdgeShape feet = new EdgeShape();
        // bottom left of shape and bottom right of shape
        feet.set(new Vector2(-2.0f / RetroHook.PPM, -6.0f / RetroHook.PPM), new Vector2(2.0f / RetroHook.PPM, -6.0f / RetroHook.PPM));
        fdef.shape = feet;
        // Does not collide (i.e. can check but does not stop things)
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(new BodyData(this, "guyFeet"));

        EdgeShape head = new EdgeShape();
        // bottom left of shape and bottom right of shape
        head.set(new Vector2(-2.0f / RetroHook.PPM, 7.0f / RetroHook.PPM), new Vector2(2.0f / RetroHook.PPM, 7.0f / RetroHook.PPM));
        fdef.shape = head;
        // Does not collide (i.e. can check but does not stop things)
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(new BodyData(this, "guyHead"));
    }
}
