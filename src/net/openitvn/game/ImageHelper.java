/*
 * Copyright © 2012 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */
package net.openitvn.game;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

public class ImageHelper {
    
    public static Image loadImage(String path) throws RuntimeException {
        Image image = null;

        try {
            InputStream in = Image.class.getResourceAsStream(path);
            image = Image.createImage(in);
        } catch (IOException ioe) {
            throw new RuntimeException("Missing file: " + path + " " + ioe.getMessage());
        }

        return image;
    }
}
