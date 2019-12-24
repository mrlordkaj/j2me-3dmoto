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
package net.openitvn.game.bounding;

/**
 *
 * @author Thinh Pham
 */
public class Triangle extends Shape {
    public void setVertex(int i, Point p) { vertex[i] = p; }
    
    public Triangle() {
        vertex = new Point[3];
    }
    
    public Triangle(Point v0, Point v1, Point v2) {
        vertex = new Point[3];
        vertex[0] = new Point(v0.x, v0.y);
        vertex[1] = new Point(v1.x, v1.y);
        vertex[2] = new Point(v2.x, v2.y);
    }
    
    public boolean contains(Point p) {
        Point e0 = Point.sub(p, vertex[0]);
        Point e1 = Point.sub(vertex[1], vertex[0]);
        Point e2 = Point.sub(vertex[2], vertex[0]);

        float u, v;
        if (e1.x == 0) {
            if (e2.x == 0) return false;
            u = e0.x / e2.x;
            if (u < 0 || u > 1) return false;
            if (e1.y == 0) return false;
            v = (e0.y - e2.y * u) / e1.y;
            if (v < 0) return false;
        }
        else {
            float d = e2.y * e1.x - e2.x * e1.y;
            if (d == 0) return false;
            u = (e0.y * e1.x - e0.x * e1.y) / d;
            if (u < 0 || u > 1) return false;
            v = (e0.x - e2.x * u) / e1.x;
            if (v < 0) return false;
            if ((u + v) > 1) return false;
        }

        return true;
    }
    
    public boolean collide(Triangle target) {
        if (contains(target.vertex[0]))
            return true;
        if (contains(target.vertex[1]))
            return true;
        if (contains(target.vertex[2]))
            return true;
        if (target.contains(vertex[0]))
            return true;
        if (target.contains(vertex[1]))
            return true;
        return target.contains(vertex[2]);
    }
    
    public boolean collide(Rectangle target) {
        return target.collides(this);
    }
}
