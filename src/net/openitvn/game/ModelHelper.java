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
package net.openitvn.game;

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
public abstract class ModelHelper {
    
    /**
     * Loads m3g model world by provided file name without extension (.mod).
     */
    public static World loadWorld(String fileName) {
        fileName += ".mod";
        try {
            Object3D[] allNodes = Loader.load(fileName);
            for (int i = 0; i < allNodes.length; i++) {
                if (allNodes[i] instanceof World)
                    return (World) allNodes[i];
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Missing file: " + fileName + " (" + ex.getMessage() + ")");
        }
        return null;
    }
    
    /**
     * Clones a children node from provided world.
     */
    public static Node extractNode(int index, World world) {
        if (index >= 0 && index < world.getChildCount()) {
            Node node = world.getChild(index);
            node.setTranslation(0, 0, 0);
            return (Node) node.duplicate();
        }
        throw new IndexOutOfBoundsException("Node index is invalid");
    }
    
    /**
     * Loads m3g texture package by provided file name without extension (.tex).
     */
    public static Vector loadTexturePack(String fileName) {
        fileName += ".tex";
        try {
            Object3D[] allNodes = Loader.load(fileName);
            Vector rs = new Vector();
            for (int i = 0; i < allNodes.length; i++) {
                if (allNodes[i] instanceof Texture2D)
                    rs.addElement(allNodes[i]);
            }
            return rs;
        } catch (IOException ex) {
            throw new RuntimeException("Missing file: " + fileName + " (" + ex.getMessage() + ")");
        }
    }
    
    /**
     * Gets a children texture from texture package.
     */
    public static Texture2D extractTexture(int index, Vector texturePack) {
        return (Texture2D) texturePack.elementAt(index);
    }
    
    /**
     * Creates texture from an opaque image file.
     */
    public static Texture2D loadOpaqueTexture(String imagePath) {
        Image2D img = new Image2D(Image2D.RGB, ImageHelper.loadImage(imagePath));
        return new Texture2D(img);
    }
    
    /**
     * Creates texture from a transparent image file.
     */
    public static Texture2D loadTransparentTexture(String imagePath) {
        Image2D img = new Image2D(Image2D.RGBA, ImageHelper.loadImage(imagePath));
        return new Texture2D(img);
    }
    
    /**
     * Applies a texture to a mesh.
     */
    public static void applyTexture(Texture2D texture, Mesh mesh) {
        Appearance appr = (Appearance) mesh.getAppearance(0).duplicate();
        appr.setTexture(0, texture);
        mesh.setAppearance(0, appr);
    }
}
