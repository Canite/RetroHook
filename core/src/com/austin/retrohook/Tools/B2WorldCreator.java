package com.austin.retrohook.Tools;

import com.austin.retrohook.Screens.PlayScreen;
import com.austin.retrohook.Sprites.Exit;
import com.austin.retrohook.Sprites.Ground;
import com.austin.retrohook.Sprites.MovingPlatform;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by austin on 9/28/15.
 */
public class B2WorldCreator {
    public B2WorldCreator(World world, TiledMap map, PlayScreen screen) {
        // Create box2d for each object in the ground layer (layer 3)
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Ground(world, map, rect);
        }

        // Create box2d for each object in the exit layer (layer 2)
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Exit(world, map, rect, screen);
        }

        // create objects for moving platforms
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            float xspeed = Float.parseFloat((String)object.getProperties().get("xspeed"));
            float yspeed = Float.parseFloat((String)object.getProperties().get("yspeed"));
            float leftside = Float.parseFloat((String)object.getProperties().get("leftside"));
            float rightside = Float.parseFloat((String)object.getProperties().get("rightside"));
            float top = Float.parseFloat((String)object.getProperties().get("top"));
            float bottom = Float.parseFloat((String)object.getProperties().get("bottom"));
            float pause = Float.parseFloat((String)object.getProperties().get("pause"));
            screen.mapSprites.add(new MovingPlatform(world, map, rect, screen, xspeed, yspeed, leftside, rightside, top, bottom, pause));
        }
    }
}
