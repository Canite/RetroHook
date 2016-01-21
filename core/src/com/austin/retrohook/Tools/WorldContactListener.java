package com.austin.retrohook.Tools;

import com.austin.retrohook.Sprites.Guy;
import com.austin.retrohook.Sprites.Hook;
import com.austin.retrohook.Sprites.InteractiveTileObject;
import com.austin.retrohook.Sprites.MovingPlatform;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by austin on 9/29/15.
 */
public class WorldContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        BodyData bodyA, bodyB;

        if (fixA.getUserData() != null && BodyData.class.isAssignableFrom(fixA.getUserData().getClass())) {
            bodyA = (BodyData)fixA.getUserData();
            if (fixB.getUserData() != null && BodyData.class.isAssignableFrom(fixB.getUserData().getClass())) {
                bodyB = (BodyData)fixB.getUserData();
                if (bodyA.fixString == "guyFeet" || bodyB.fixString == "guyFeet") {
                    BodyData feet = bodyA.fixString == "guyFeet" ? bodyA : bodyB;
                    BodyData object = feet == bodyA ? bodyB : bodyA;

                    if (object.fixString == "ground") {
                        ((Guy) feet.parentObj).setOnGround(true);
                        ((InteractiveTileObject) object.parentObj).onFeetHit();
                    } else if (object.fixString == "movingplatform") {
                        ((Guy) feet.parentObj).setOnGround(true);
                        ((MovingPlatform) object.parentObj).passenger = ((Guy)feet.parentObj).b2body;
                        ((MovingPlatform) object.parentObj).onFeetHit();
                    }
                } else if (bodyA.fixString == "hook" || bodyB.fixString == "hook") {
                    BodyData hook = bodyA.fixString == "hook" ? bodyA : bodyB;
                    BodyData object = hook == bodyA ? bodyB : bodyA;

                    if (object.fixString == "ground" || object.fixString == "movingplatform") {
                        ((Hook) hook.parentObj).setHooked(true);
                        ((Hook) hook.parentObj).weldedObj = object.parentObj;
                    }
                } else if (bodyA.fixString == "guyBody" || bodyB.fixString == "guyBody") {
                    BodyData bod = bodyA.fixString == "guyBody" ? bodyA : bodyB;
                    BodyData object = bod == bodyA ? bodyB : bodyA;

                    if (object.fixString == "exit") {
                        ((InteractiveTileObject) object.parentObj).onBodyHit();
                    }
                } else if (bodyA.fixString == "guyHead" || bodyB.fixString == "guyHead") {
                    BodyData head = bodyA.fixString == "guyHead" ? bodyA : bodyB;
                    BodyData object = head == bodyA ? bodyB : bodyA;

                    if (object.fixString == "movingplatform") {
                        if (((Guy) head.parentObj).getOnGround()) {
                            Gdx.app.log("collision", "head - on ground");
                            ((Guy) head.parentObj).kill();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        BodyData bodyA, bodyB;

        if (fixA.getUserData() != null && BodyData.class.isAssignableFrom(fixA.getUserData().getClass())) {
            bodyA = (BodyData)fixA.getUserData();
            if (fixB.getUserData() != null && BodyData.class.isAssignableFrom(fixB.getUserData().getClass())) {
                bodyB = (BodyData)fixB.getUserData();
                if (bodyA.fixString == "guyFeet" || bodyB.fixString == "guyFeet") {
                    BodyData feet = bodyA.fixString == "guyFeet" ? bodyA : bodyB;
                    BodyData object = feet == bodyA ? bodyB : bodyA;

                    if (object.fixString == "ground") {
                        ((Guy) feet.parentObj).setOnGround(false);
                    } else if (object.fixString == "movingplatform") {
                        ((Guy) feet.parentObj).setOnGround(false);
                        ((MovingPlatform) object.parentObj).passenger = null;
                    }
                }
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
