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
import java.util.regex.Pattern;

import javax.swing.filechooser.FileFilter;

public class PatternFileChooserFilter extends FileFilter {
	public static final String PATTERN_IMAGES = ".+?\\.(png|jpe?g|gif|tiff?)$";
	
    private final Pattern filePattern;
    private final String description;
    
    public PatternFileChooserFilter() {
    	this("", "Folders");
    }
    public PatternFileChooserFilter(String pattern, String don) {
    	this.description = don;
    	if (pattern != null) {
    		filePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    	} else {
    		filePattern = null;
    	}
    }
	 
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
 
        if (filePattern != null && filePattern.matcher(f.getName()).matches()) {
        	return true;
        }
 
        return false;
    }

    @Override
    public String getDescription() {
        return description;
    }
}