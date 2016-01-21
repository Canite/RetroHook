package com.austin.retrohook.Sprites;

import com.austin.retrohook.RetroHook;
import com.austin.retrohook.Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by austin on 10/1/15.
 */
public class RopeSegment extends Sprite {
    public World world;
    public PlayScreen screen;
    public Body body;
    public TextureRegion ropeImage;

    public RopeSegment(World world, PlayScreen screen, float width, float height, Vector2 position, float rotation) {
        this.world = world;
        this.screen = screen;

        ropeImage = new TextureRegion(screen.getAtlas().findRegion("rope"), 0, 0, 4, 6);
        PlayScreen.fixBleeding(ropeImage);
        ropeImage.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        setBounds(0, 0, width, height);
        setRegion(ropeImage);
        setOrigin(getWidth()/2, getHeight()/2);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.density = 5;
        body.createFixture(fdef);
        body.setTransform(body.getPosition(), -rotation);
    }

    public void update(float dt) {
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }
}
