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
package ru.org.sevn.utilwt.filechooser;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

import ru.org.sevn.utilwt.ImageUtil;

public class ImageFileView extends FileView {

	private boolean previewInFileList = false;
 
    public void setPreviewInFileList(boolean previewInFileList) {
		this.previewInFileList = previewInFileList;
	}

    public Boolean isTraversable(File f) {
        return null;
    }
 
    private final int ICON_WIDTH = 32;
    
    public Icon getIcon(File f) {
    	Icon ret = null;
    	if (previewInFileList && !f.isDirectory()) {
    		ImageIcon r = new ImageIcon(f.getPath());
	    	if (r.getImage() != null) {
	    		ret = r;
	    	}
    	}
    	if(ret == null && FileSystemView.getFileSystemView() != null) {
    		ret = FileSystemView.getFileSystemView().getSystemIcon(f);
    	} else if (ret == null) {
    		ret = super.getIcon(f);
    	}
    	if (previewInFileList) {
    		return ImageUtil.getScaledIcon(ret, ICON_WIDTH, ICON_WIDTH, false);
    	}
    	
    	return ret;
    }
}