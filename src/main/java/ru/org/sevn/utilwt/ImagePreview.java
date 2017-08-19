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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class ImagePreview extends JPanel {
	private ImageIcon previewImage = null;
	private ImageIcon imageIcon = null;
	private int previewWidth = PREVIEW_WIDTH;
	private int previewHeight = PREVIEW_HEIGHT;
	public static int PREVIEW_WIDTH = 200;
	public static int PREVIEW_HEIGHT = 200;
	private ImageJComponent imageJComponent;
	private JTextArea label;

	public ImagePreview() {
		super(new BorderLayout());
		imageJComponent = new ImageJComponent(); 
		add(imageJComponent, BorderLayout.CENTER);
		
	    JTextArea textArea = new JTextArea(1, 20);
	    textArea.setText("");
	    textArea.setWrapStyleWord(true);
	    textArea.setLineWrap(true);
	    textArea.setOpaque(false);
	    textArea.setEditable(false);
	    textArea.setFocusable(false);
	    textArea.setBackground(UIManager.getColor("Label.background"));
	    textArea.setFont(UIManager.getFont("Label.font"));
	    textArea.setBorder(UIManager.getBorder("Label.border"));
	    label = textArea;
		add(label, BorderLayout.SOUTH);
	}
	
	public ImageIcon getImageIcon() {
		return imageIcon;
	}
	
	public void setImageIcon(ImageIcon i) {
		previewImage = null;
		imageIcon = i;
	}

	public void loadImage() {
		previewImage = scaleToView(getImageIcon());
	}
	
	private ImageIcon scaleToView(ImageIcon ii) {
		return ImageUtil.getScaledImageIcon(ii, previewWidth, previewHeight, false);
	}

	private class ImageJComponent extends JComponent {
		public ImageJComponent() {
			setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
		}
		protected void paintComponent(Graphics g) {
			if (getPreviewImage() == null) {
				loadImage();
			}
			ImageIcon thumbnail = getPreviewImage();
			if (thumbnail != null) {
				int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
				int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

				if (y < 0) {
					y = 0;
				}

				if (x < 5) {
					x = 5;
				}
				thumbnail.paintIcon(this, g, x, y);
			}
		}
	}

	public ImageIcon getPreviewImage() {
		return previewImage;
	}

	public JTextArea getLabel() {
		return label;
	}

}