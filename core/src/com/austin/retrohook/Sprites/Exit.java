package com.austin.retrohook.Sprites;

import com.austin.retrohook.Screens.PlayScreen;
import com.austin.retrohook.Tools.BodyData;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by austin on 9/28/15.
 */
public class Exit extends InteractiveTileObject {
    private PlayScreen screen;
    public Exit(World world, TiledMap map, Rectangle bounds, PlayScreen screen) {
        super(world, map, bounds);
        this.screen = screen;
        fixture.setUserData(new BodyData(this, "exit"));
    }

    @Override
    public void onFeetHit() {

    }

    @Override
    public void onBodyHit() {
        screen.setTransition(true);
    }
}
