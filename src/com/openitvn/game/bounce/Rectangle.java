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
package com.openitvn.game.bounce;

/**
 *
 * @author Thinh Pham
 */
public class Rectangle extends Shape {
    private boolean vertexUpdated = false;
    
    private int x = 0;
    public void setX(int value) {
        x = value;
        vertexUpdated = false;
    }
    public int getX() { return x; }
    
    private int y = 0;
    public void setY(int value) {
        y = value;
        vertexUpdated = false;
    }
    public int getY() { return y; }
    
    private int width = 0;
    public void setWidth(int value) {
        width = value;
        vertexUpdated = false;
    }
    public int getWidth() { return width; }
    
    private int height = 0;
    public void setHeight(int value) {
        height = value;
        vertexUpdated = false;
    }
    public int getHeight() { return height; }
    
    private Point pivot;
    public void setPivot(Point value) {
        pivot = value;
        vertexUpdated = false;
    }
    public Point getPivot() { return pivot; }
    
    private int angle = 0;
    public void setAngle(int value) {
        if(value >= 360) value -= 360;
        else if(value <= -360) value += 360;
        angle = value;
        vertexUpdated = false;
    }
    public int getAngle() { return angle; }
    
    private void updateVertex() {
        if(vertexUpdated) return;
        
        vertex = new Point[4];
        
        Point absolutePivot = new Point(x + pivot.x, y + pivot.y);
        switch(angle) {
            case 0:
                vertex[0] = new Point(x + width, y);
                vertex[1] = new Point(x, y);
                vertex[2] = new Point(x, y + height);
                vertex[3] = new Point(x + width, y + height);
                break;
                
            case 90:
                vertex[3] = new Point(absolutePivot.x + pivot.y, absolutePivot.y + (width - pivot.x));
                vertex[0] = new Point(vertex[3].x, absolutePivot.y - pivot.x);
                vertex[1] = new Point(absolutePivot.x - (height - pivot.y), vertex[0].y);
                vertex[2] = new Point(vertex[1].x, vertex[3].y);
                break;
                
            case 180:
                vertex[2] = new Point(absolutePivot.x - (width - pivot.x), absolutePivot.y + pivot.y);
                vertex[3] = new Point(absolutePivot.x + pivot.x, vertex[2].y);
                vertex[0] = new Point(vertex[3].x, (y + pivot.y) - (height - pivot.y));
                vertex[1] = new Point(vertex[2].x, vertex[0].y);
                break;
                
            case 270:
                vertex[1] = new Point(absolutePivot.x - pivot.y, absolutePivot.y - (width - pivot.x));
                vertex[2] = new Point(vertex[1].x, absolutePivot.y + pivot.x);
                vertex[3] = new Point(absolutePivot.x + (height - pivot.y), vertex[2].y);
                vertex[0] = new Point(vertex[3].x, vertex[1].y);
                break;
                
            default:
                double radAngle = angle * Math.PI/180;
                vertex[0] = new Point(x + width, y);
                vertex[1] = new Point(x, y);
                vertex[2] = new Point(x, y + height);
                vertex[3] = new Point(x + width, y + height);
                rotatePoint(vertex[0], absolutePivot, radAngle);
                rotatePoint(vertex[1], absolutePivot, radAngle);
                rotatePoint(vertex[2], absolutePivot, radAngle);
                rotatePoint(vertex[3], absolutePivot, radAngle);
                break;
        }
        
        vertexUpdated = true;
    }
    
    private void rotatePoint(Point target, Point absolutePivot, double radAngle) {
        int a = target.x - absolutePivot.x;
        int b = target.y - absolutePivot.y;
        target.x = absolutePivot.x + (int)(Math.cos(radAngle)*a - Math.sin(radAngle)*b);
        target.y = absolutePivot.y + (int)(Math.sin(radAngle)*a + Math.cos(radAngle)*b);
    }
    
    public Rectangle() {
        x = y = width = height = 0;
        pivot = new Point();
        angle = 0;
    }
    
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pivot = new Point();
        this.angle = 0;
    }
    
    public boolean contains(int x, int y) {
        return contains(new Point(x, y));
    }
    
    public boolean contains(Point p) {
        updateVertex();
        
        switch(angle) {
            case 0:
            case 90:
            case 180:
            case 270:
                return !(p.x < vertex[1].x || p.y < vertex[1].y || p.x > vertex[3].x || p.y > vertex[3].y);
                
            default:
                if(new Triangle(vertex[0], vertex[1], vertex[2]).contains(p)) return true;
                if(new Triangle(vertex[0], vertex[3], vertex[2]).contains(p)) return true;
                return false;
        }
    }
    
    public boolean collides(Triangle target) {
        updateVertex();
        
        for(byte i = 0; i < target.vertex.length; i++)
            if(contains(target.vertex[i])) return true;
        for(byte i = 0; i < vertex.length; i++)
            if(target.contains(vertex[i])) return true;
        return false;
    }
    
    public boolean collides(Rectangle target) {
        updateVertex();
        
        if((target.angle == 0 || target.angle == 90 || target.angle == 180 || target.angle == 270)
               && (angle == 0 || angle == 90 || angle == 180 || angle == 270)) {
            if(Math.max(vertex[0].y, target.vertex[0].y) >= Math.min(vertex[2].y, target.vertex[2].y)) return false;
            return Math.max(vertex[1].x, target.vertex[1].x) < Math.min(vertex[3].x, target.vertex[3].x);
        } else {
            for(byte i = 0; i < vertex.length; i++) {
                if(contains(target.vertex[i])) return true;
                if(target.contains(vertex[i])) return true;
            }
            return false;
        }
    }
}
