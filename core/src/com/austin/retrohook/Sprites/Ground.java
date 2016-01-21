package com.austin.retrohook.Sprites;

import com.austin.retrohook.Tools.BodyData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by austin on 9/29/15.
 */
public class Ground extends InteractiveTileObject {
    public Ground(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);
        fixture.setUserData(new BodyData(this, "ground"));
        fixture.setFriction(0.3f);
    }

    @Override
    public void onFeetHit() {
       Gdx.app.log("Ground", "Collision");
    }

    @Override
    public void onBodyHit() {

    }
}
