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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ru.org.sevn.utilwt.filechooser.FileChooserImagePreview;
import ru.org.sevn.utilwt.filechooser.ImageFileView;

public class App extends JFrame {
    
    private LinkedHashSet<String> files = new LinkedHashSet<>();
    
    private File dir2copy = null; //new File("C:/TEMP/test");
    
    JFileChooser fileChooser = makeFileChooser();
    JFileChooser dirChooser = makeDirChooser();
    public App() {
        if (dir2copy != null && !dir2copy.exists()) {
            dir2copy.mkdirs();
        }
        //super(new BorderLayout());
        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout());
        JPanel copyToPanel = new JPanel(new FlowLayout());
        final JLabel copyToDirLabel = new JLabel();
        if (dir2copy != null) {
            copyToDirLabel.setText(dir2copy.getAbsolutePath());
        }
        copyToPanel.add(copyToDirLabel);
        JButton changeBt = new JButton("Choose dir");
        copyToPanel.add(changeBt);
        changeBt.addActionListener( e -> {
            int returnVal = dirChooser.showDialog(App.this, "Open");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = dirChooser.getSelectedFile();
                if (file != null) {
                    copyToDirLabel.setText(file.getAbsolutePath());
                    dir2copy = file;
                }
            }
        } );
        contentPane.add(buttons, BorderLayout.NORTH);
        final JTextArea selectedFiles = new JTextArea();
        contentPane.add(selectedFiles, BorderLayout.CENTER);
        contentPane.add(copyToPanel, BorderLayout.SOUTH);
        
        {
            JButton selectFiles = new JButton("Choose files");
            selectFiles.addActionListener(e -> {
                int returnVal = fileChooser.showDialog(App.this, "Open");
                updateSelectedFilesTA(selectedFiles, files);
            });
            buttons.add(selectFiles);
        }
        
        {
            JButton copyFiles = new JButton("Copy files");
            copyFiles.addActionListener(e -> {
                copyFiles();
            });
            buttons.add(copyFiles);
        }
        
        setContentPane(contentPane);
        setBounds(0, 0, 400, 400);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    private void copyFiles() {
        if (dir2copy == null) {
            return;
        }
        Path top = Paths.get(dir2copy.getAbsolutePath());
        files.stream().forEach( s -> {
            Path p = Paths.get(s);
            Path r = p.getRoot();
            String rs = r.toString();
            String winDr = null;
            if (rs.contains(":")) {
                winDr = rs.substring(0, rs.indexOf(":"));
            }
            Path prel = r.relativize(p);
            Path np;
            if (winDr != null) {
                np = top.resolve(winDr);
                np = np.resolve(prel);
            } else {
                np = top.resolve(prel);
            }
            try {
                //System.out.println("->"+np.toString()+":"+winDr);
                File toDir = np.getParent().toFile();
                if (!toDir.exists()) {
                    toDir.mkdirs();
                }
                Files.copy(p, np, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Can't copy " + p + " to " + np + ":" + ex.getMessage());
            }
        } );
        System.err.println("Files copied");
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
    
    private JFileChooser makeDirChooser() {
        JFileChooser fileChooser = new JFileChooser();
        File file2open = dir2copy;
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //fileChooser.addChoosableFileFilter(new PatternFileChooserFilter(null, "All files"));
        //fileChooser.setAcceptAllFileFilterUsed(false);

        //fileChooser.setFileView(new ImageFileView());
        if (file2open != null) {
            fileChooser.setSelectedFile(file2open);
        }
        
        return fileChooser;
    }
    private JFileChooser makeFileChooser() {
        JFileChooser fileChooser = new MyJFileChooser();
        File file2open = new File(".");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        //fileChooser.addChoosableFileFilter(new PatternFileChooserFilter(null, "All files"));
        //fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setFileView(new ImageFileView());
        fileChooser.setSelectedFile(file2open);
        fileChooser.setAccessory(new MyFileChooserImagePreview(fileChooser, files));
        return fileChooser;
    }
    
    public static class MyFileChooserImagePreview extends FileChooserImagePreview {
        JTextArea selectedFiles = new JTextArea();
        private final LinkedHashSet<String> files;
        
        public MyFileChooserImagePreview(final JFileChooser fc, final LinkedHashSet<String> files) {
            super(fc);
            this.files = files;
            JPanel buttons = new JPanel(new FlowLayout());
            JButton add = new JButton("+");
            JButton del = new JButton("-");
            buttons.add(add);
            buttons.add(del);
            add.addActionListener( e -> {
                File fl = getFile();
                if (fl != null) {
                    if (files.add(fl.getAbsolutePath())) {
                        updateSelectedFiles();
                    }
                }
            } );
            del.addActionListener( e -> {
                File fl = getFile();
                if (fl != null) {
                    if (files.remove(fl.getAbsolutePath())) {
                        updateSelectedFiles();
                    }
                }
            } );
            add(buttons, BorderLayout.NORTH);
            add(selectedFiles, BorderLayout.CENTER);
            selectedFiles.setPreferredSize(new Dimension(FileChooserImagePreview.PREVIEW_WIDTH, FileChooserImagePreview.PREVIEW_HEIGHT));
            setPreferredSize(new Dimension(FileChooserImagePreview.PREVIEW_WIDTH * 2, FileChooserImagePreview.PREVIEW_HEIGHT));
        }
        
        private void updateSelectedFiles() {
            updateSelectedFilesTA(selectedFiles, files);
        }
        
        @Override
        protected void addImagePreview() {}
    }
    
    public static void updateSelectedFilesTA(JTextArea selectedFiles, Collection<String> files) {
        final StringBuilder sb = new StringBuilder();
        files.stream().forEach(s -> {sb.append(s).append("\n");});
        selectedFiles.setText(sb.toString());
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
