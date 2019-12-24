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

/**
 *
 * @author Thinh Pham
 */
public abstract class ResourceLoader implements Runnable {
    protected static ResourceLoader instance;
    
    private boolean loadingComplete = false;
    public boolean loadingComplete() { return loadingComplete; }
    
    private final int loadingBarSize;
    private int loadingBarWidth = 0;
    public int getLoadingBarWidth() { return loadingBarWidth; }
    
    protected ResourceLoader(int loadingBarSize) {
        this.loadingBarSize = loadingBarSize;
    }
    
    public void run() {
        loadingComplete = false;
        prepareResource();
        try {
            Thread.sleep(300);
            System.gc();
        } catch (InterruptedException ex) { }
        loadingComplete = true;
    }
    
    protected abstract void prepareResource();
    
    protected void setProgress(int percent) {
        loadingBarWidth = (int)(loadingBarSize * ((float)percent/100.f));
    }
    
    public void dispose() {
        instance = null;
    }
}
