package com.austin.retrohook.Sprites;

import com.austin.retrohook.RetroHook;
import com.austin.retrohook.Screens.PlayScreen;
import com.austin.retrohook.Tools.BodyData;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

/**
 * Created by austin on 9/30/15.
 */
public class Hook extends Sprite {
    private Vector2 initialVelocity;
    private Vector2 initialPosition;
    public World world;
    public PlayScreen screen;
    public Body b2body;
    private TextureRegion hookImage;
    private float timer;
    private float maxTime;
    private boolean hooked, welded;
    public Object weldedObj;
    public Guy parentGuy;
    private Rope rope;
    public float maxDist;

    public Hook(World world, PlayScreen screen, Guy parentGuy, Vector2 iV, Vector2 iP, float mT) {
        this.world = world;
        this.screen = screen;
        this.parentGuy = parentGuy;
        initialVelocity = iV;
        initialPosition = iP;
        maxTime = mT;
        maxDist = 100000;

        hooked = false;
        welded = false;

        hookImage = new TextureRegion(screen.getAtlas().findRegion("hook"), 0, 0, 4, 4);
        PlayScreen.fixBleeding(hookImage);
        hookImage.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setBounds(0, 0, 4 / RetroHook.PPM, 4 / RetroHook.PPM);
        setRegion(hookImage);

        defineHook();
        b2body.setGravityScale(0);
        b2body.setLinearVelocity(initialVelocity);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        if (rope != null)
            rope.draw(batch);
    }

    public void setHooked(boolean isHooked) {
        hooked = isHooked;
    }

    public boolean getHooked() {
        return hooked;
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        if (hooked && !welded) {
            b2body.setLinearVelocity(0, 0);
            welded = true;
            WeldJointDef weldDef = new WeldJointDef();
            weldDef.initialize(b2body, ((Hookable) weldedObj).body, b2body.getWorldCenter());
            world.createJoint(weldDef);
            maxDist = Math.max(b2body.getPosition().dst(parentGuy.b2body.getPosition()),  0.2f);
            rope = new Rope(world, screen, maxDist, parentGuy.b2body, b2body);
            parentGuy.setHooked(true);
        }
        if (rope != null) {
            rope.update(dt);
        }
    }

    public void cleanUp() {
        world.destroyBody(b2body);
        if (rope != null)
            rope.cleanUp();
    }

    public void defineHook() {
        BodyDef bdef = new BodyDef();
        // set position in level 1
        bdef.position.set(initialPosition.x, initialPosition.y);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(2 / RetroHook.PPM);
        fdef.shape = shape;
        fdef.isSensor = true;
        b2body.createFixture(fdef).setUserData(new BodyData(this, "hook"));
        //b2body.setUserData(new BodyData(this, "hook"));
        b2body.setBullet(true);
    }
}
