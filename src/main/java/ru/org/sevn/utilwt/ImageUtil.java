/*******************************************************************************
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ru.org.sevn.utilwt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageUtil {
    public static ImageIcon createImageIcon(String path, Class resourceClass) {
        java.net.URL imgURL = resourceClass.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    public static ImageIcon getStretchedImageIcon(ImageIcon ii, int w, int h, boolean incr) {
		if (ii != null) {
			ii = new ImageIcon(ii.getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT));
		}
		return ii;
    }
    public static ImageIcon getScaledImageIcon(ImageIcon ii, int w, int h, boolean incr) {
		if (ii != null) {
			if (ii.getIconWidth() > w || incr) {
				ii = new ImageIcon(ii.getImage().getScaledInstance(w, -1, Image.SCALE_DEFAULT));
			}
			if (ii.getIconHeight() > h) {
				ii = new ImageIcon(ii.getImage().getScaledInstance(-1, h, Image.SCALE_DEFAULT));
			}
		}
		return ii;
    }
    public static Icon getScaledIcon(Icon tmpIcon, int w, int h, boolean incr) {
    	ImageIcon ii = null;
    	if (tmpIcon instanceof ImageIcon) {
    		return getScaledImageIcon((ImageIcon)tmpIcon, w, h, incr);
    	}
		return tmpIcon;
    }
    public static ImageIcon imageWithBackground(ImageIcon icon, Color colorBg) {
    	if (icon != null && colorBg != null) {
	        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
	        Graphics2D ig2 = bi.createGraphics();
	        ig2.setColor(colorBg);
	        ig2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
	        ig2.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
	        return new ImageIcon(bi);
    	}
    	return icon;
    }
    public static BufferedImage getBufferedImage(ImageIcon icon) {
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = bi.createGraphics();
        ig2.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
        return bi;
    }
    public static void writeImage(BufferedImage bi, String format, File fl) throws IOException {
    	ImageIO.write(bi, format, fl);
    }

}
