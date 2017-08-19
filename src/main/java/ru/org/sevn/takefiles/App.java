/** *****************************************************************************
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
 ****************************************************************************** */
package ru.org.sevn.takefiles;

import java.awt.BorderLayout;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ru.org.sevn.utilwt.filechooser.FileChooserImagePreview;
import ru.org.sevn.utilwt.filechooser.ImageFileView;
import ru.org.sevn.utilwt.filechooser.PatternFileChooserFilter;

public class App extends JFrame {
    
    JFileChooser fileChooser = makeFileChooser();
    public App() {
        //super(new BorderLayout());
        JButton center = new JButton("ZZZ");
        center.addActionListener(e -> {
            int returnVal = fileChooser.showDialog(App.this, "Open");
        });
        setContentPane(center);
        //add(center, BorderLayout.CENTER);
    }

    private static class MyJFileChooser extends JFileChooser {

        public MyJFileChooser() {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(this);
        }
    }
    
    private static JFileChooser makeFileChooser() {
        JFileChooser fileChooser = new MyJFileChooser();
        File file2open = new File(".");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //fileChooser.addChoosableFileFilter(new PatternFileChooserFilter(null, "All files"));
        //fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setFileView(new ImageFileView());
        fileChooser.setSelectedFile(file2open);
        fileChooser.setAccessory(new MyFileChooserImagePreview(fileChooser));
        return fileChooser;
    }
    
    public static class MyFileChooserImagePreview extends FileChooserImagePreview {
        JTextArea selectedFiles = new JTextArea();
        
        public MyFileChooserImagePreview(JFileChooser fc) {
            super(fc);
            //JButton 
            add(selectedFiles, BorderLayout.EAST);
        }
        
    }

    public void showFrame() {
        setVisible(true);
    }
    
    private static void runMain() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                App frame = new App();
                frame.showFrame();
            }
        });

    }
    
    public static void main(String[] args) throws Exception {
        runMain();
        
    }
}
