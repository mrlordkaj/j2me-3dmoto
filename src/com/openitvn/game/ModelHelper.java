/*
 * Copyright (C) 2012 Thinh Pham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openitvn.game;

import java.io.IOException;
import java.util.Vector;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Loader;
import javax.microedition.m3g.Mesh;
import javax.microedition.m3g.Node;
import javax.microedition.m3g.Object3D;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.World;

/**
 *
 * @author Thinh Pham
 */
public class ModelHelper {
    public static World loadWorld(String fileName) {
        fileName += ".msh";
        try {
            Object3D[] allNodes = Loader.load(fileName);
            for(int i = 0; i < allNodes.length; i++) {
                if(allNodes[i] instanceof World) {
                    return (World)allNodes[i];
                }
            }
        } catch (IOException ex) {
        }
        return null;
    }
    
    public static Node extractNode(int id, World mWorld) {
        if(id >= mWorld.getChildCount()) {
            throw new RuntimeException("Node ID is invalid");
        }
        
        Node node = mWorld.getChild(id);
        node.setTranslation(0, 0, 0);
        return (Node)node.duplicate();
    }
    
    public static Vector loadTexturePack(String fileName) {
        Vector items = new Vector();
        fileName += ".tex";
        try {
            Object3D[] allNodes = Loader.load(fileName);
            for(int i = 0; i < allNodes.length; i++) {
                if(allNodes[i] instanceof Texture2D) {
                    items.addElement(allNodes[i]);
                }
            }
            return items;
        } catch (IOException ex) {
            throw new RuntimeException("Missing file: " + fileName + " (" + ex.getMessage() + ")");
        }
    }
    
    public static Texture2D extractTexture(int id, Vector texturePack) {
        return (Texture2D)texturePack.elementAt(id);
    }
    
    public static Texture2D loadOpaqueTexture(String texturePath) {
        Image2D image = new Image2D(Image2D.RGB, ImageHelper.loadImage(texturePath));
        Texture2D texture = new Texture2D(image);
        return texture;
    }
    
    public static Texture2D loadTransparentTexture(String texturePath) {
        Image2D image = new Image2D(Image2D.RGBA, ImageHelper.loadImage(texturePath));
        Texture2D texture = new Texture2D(image);
        return texture;
    }
    
    public static void applyTexture(Texture2D texture, Mesh mesh) {
        Appearance appearance = (Appearance)mesh.getAppearance(0).duplicate();
        appearance.setTexture(0, texture);
        mesh.setAppearance(0, appearance);
    }
    
    public static void applyTexture(String texturePath, Mesh mesh) {
        Texture2D texture = loadOpaqueTexture(texturePath);
        applyTexture(texture, mesh);
    }
}
