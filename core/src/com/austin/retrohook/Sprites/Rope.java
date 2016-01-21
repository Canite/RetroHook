package com.austin.retrohook.Sprites;

import com.austin.retrohook.RetroHook;
import com.austin.retrohook.Screens.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

/**
 * Created by austin on 10/1/15.
 */
public class Rope {
    public int numSegments;
    public RopeSegment[] segments;
    public double length;
    public World world;
    public PlayScreen screen;
    private int ropeDisplayTimer;

    public Rope(World world, PlayScreen screen, float dist, Body guyBody, Body hookBody) {
        this.world = world;
        this.screen = screen;
        //this.numSegments = numSegments;
        length = (double) dist;
        Gdx.app.log("rope", Double.toString(length));
        this.numSegments = (int)Math.max(Math.floor(length * 15), 1.0);
        float height = (float)length / numSegments;
        float rotation = (float) (Math.atan2((double)hookBody.getPosition().y - (double)guyBody.getPosition().y, (double)hookBody.getPosition().x - (double)guyBody.getPosition().x));
        ropeDisplayTimer = numSegments;

        segments = new RopeSegment[numSegments];
        for (int i = 0; i < numSegments; i++) {
            float segX = (float)((height * (i + 1)) * Math.cos((double)rotation));
            float segY = (float)((height * (i + 1)) * Math.sin((double)rotation));
            Vector2 position = new Vector2(guyBody.getPosition().x + segX, guyBody.getPosition().y + segY);
            RopeSegment segment = new RopeSegment(world, screen, 0.03f, height, position, rotation);
            segments[i] = segment;
            RopeJointDef jDef = new RopeJointDef();
            RopeJointDef aDef = new RopeJointDef();
            RevoluteJointDef rDef = new RevoluteJointDef();
            if (i == 0) {
                rDef.bodyA = guyBody;
                rDef.bodyB = segment.body;
                rDef.localAnchorA.set(0, 7.5f / RetroHook.PPM);
                rDef.localAnchorB.set(0, -height / 2);

                aDef.bodyA = guyBody;
                aDef.bodyB = segment.body;
                aDef.localAnchorA.set(0, 7.5f / RetroHook.PPM);
                aDef.localAnchorB.set(0, -height / 2);
                aDef.collideConnected = true;
                aDef.maxLength = segment.body.getPosition().dst(hookBody.getPosition()) - (7.5f / RetroHook.PPM);

                jDef.bodyA = guyBody;
                jDef.bodyB = hookBody;
                jDef.localAnchorA.set(0, 7.5f / RetroHook.PPM);
                jDef.localAnchorB.set(0, 0);
                jDef.collideConnected = true;
                jDef.maxLength = dist;
            } else {
                RopeSegment prevRope = segments[i-1];
                rDef.bodyA = prevRope.body;
                rDef.bodyB = segment.body;
                rDef.localAnchorA.y = height / 2;
                rDef.localAnchorB.y = -height / 2;

                aDef.bodyA = segment.body;
                aDef.bodyB = hookBody;
                aDef.localAnchorA.set(0, height / 2);
                aDef.localAnchorB.set(0, 0);
                aDef.collideConnected = true;
                aDef.maxLength = segment.body.getPosition().dst(hookBody.getPosition());

                jDef.bodyA = prevRope.body;
                jDef.bodyB = segment.body;
                jDef.localAnchorA.set(0.03f / 2, height / 2);
                jDef.localAnchorB.set(0.03f / 2, height / 2);
                jDef.collideConnected = true;
                jDef.maxLength = height;
            }
            world.createJoint(rDef);
            world.createJoint(jDef);
            world.createJoint(aDef);
        }

        RopeJointDef jDef = new RopeJointDef();
        RevoluteJointDef rDef = new RevoluteJointDef();
        rDef.bodyA = segments[numSegments - 1].body;
        rDef.bodyB = hookBody;
        rDef.localAnchorA.set(0, height / 2);
        rDef.localAnchorB.set(0, 0);
        world.createJoint(rDef);

        jDef.bodyA = segments[numSegments - 1].body;
        jDef.bodyB = hookBody;
        jDef.localAnchorA.set(0, height / 2);
        jDef.localAnchorB.set(0, 0);
        jDef.collideConnected = true;
        jDef.maxLength = segments[numSegments - 1].body.getPosition().dst(hookBody.getPosition());
        world.createJoint(jDef);
    }

    public void update(float dt) {
        for (int i = 0; i < numSegments; i++) {
            segments[i].update(dt);
        }
        if (ropeDisplayTimer > 0) {
            ropeDisplayTimer -= Math.ceil((double)60 / (double)numSegments);
        }
        if (ropeDisplayTimer < 0) {
            ropeDisplayTimer = 0;
        }
    }

    public void draw(Batch batch) {
        int drawIndex;
        drawIndex = ropeDisplayTimer;
        for (int i = numSegments - 1; i >= drawIndex; i--) {
            segments[i].draw(batch);
        }
    }

    public void cleanUp() {
        for (int i = 0; i < numSegments; i++) {
            world.destroyBody(segments[i].body);
        }
    }
}
