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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import ru.org.sevn.utilwt.filechooser.FileChooserImagePreview;
import ru.org.sevn.utilwt.filechooser.ImageFileView;

public class App extends JFrame {
    
    private File tempFile = new File("ru.org.sevn.takefiles.lst");
    
    private FilesModel filesModel = new FilesModel();
    
    private File dir2copy = null; //new File("C:/TEMP/test");
    
    private JFileChooser fileChooser = makeFileChooser();
    private JFileChooser dirChooser = makeDirChooser();
    private JFileChooser fileListChooser = makeFileListChooser();
    
    final JTextArea selectedFilesTA = new JTextArea();
    
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
        contentPane.add(selectedFilesTA, BorderLayout.CENTER);
        contentPane.add(copyToPanel, BorderLayout.SOUTH);
        
        {
            JButton clearFiles = new JButton("Clear");
            clearFiles.addActionListener(e -> {
                int res = JOptionPane.showConfirmDialog(this, "Clear file list?", TITLE_QUESTION, JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    filesModel.getFiles().clear();
                    updateSelectedFiles();
                }
            });
            buttons.add(clearFiles);
        }
        
        {
            JButton sizeFiles = new JButton("Size");
            sizeFiles.addActionListener(e -> {
                long size = countSize(filesModel);
                setTitle("Files size: " + filesModel.getSizeString());
            });
            buttons.add(sizeFiles);
        }
        
        {
            JButton selectFiles = new JButton("Choose files");
            selectFiles.addActionListener(e -> {
                int returnVal = fileChooser.showDialog(App.this, "Open");
                setTitle("Files size: " + filesModel.getSizeString());
                updateSelectedFiles();
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
        {
            JButton openList = new JButton("Open list of files");
            openList.addActionListener(e -> {
                int returnVal = fileListChooser.showDialog(App.this, "Open");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile(fileListChooser.getSelectedFile(), false);
                }
            });
            buttons.add(openList);
        }
        {
            JButton saveList = new JButton("Save list of files");
            saveList.addActionListener(e -> {
                int returnVal = fileListChooser.showDialog(App.this, "Open to save");
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile(fileListChooser.getSelectedFile(), false);
                }
            });
            buttons.add(saveList);
        }
        
        setContentPane(contentPane);
        setBounds(0, 0, 640, 400);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    private static final String TITLE_RESULT = "Result";
    private static final String TITLE_ERROR = "Error";
    private static final String TITLE_QUESTION = "Question";
    
    private void saveFile(File fl, boolean quiet) {
        if (fl != null) {
            try {
                Files.write(fl.toPath(), filesModel.getFiles(), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                if (!quiet) {
                    JOptionPane.showMessageDialog(this, "File list saved in " + fl.getAbsolutePath(), TITLE_RESULT, JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                if (!quiet) {
                    JOptionPane.showMessageDialog(this, "Can't write into " + fl.getAbsolutePath(), TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void openFile(File fl, boolean quiet) {
        if (fl != null && fl.exists() && fl.canRead()) {
            try {
                Collection<String> lines = Files.readAllLines(fl.toPath(), StandardCharsets.UTF_8);
                filesModel.getFiles().clear();
                filesModel.getFiles().addAll(lines);
                updateSelectedFiles();
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                if (!quiet) {
                    JOptionPane.showMessageDialog(this, "Can't read from " + fl.getAbsolutePath(), TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void copyFiles() {
        if (dir2copy == null) {
            JOptionPane.showMessageDialog(this, "No destignation folder", TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder errors = new StringBuilder();
        Path top = Paths.get(dir2copy.getAbsolutePath());
        filesModel.getFiles().stream().forEach( s -> {
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
                errors.append("Can't copy " + p + " to " + np).append("\n");
                System.err.println("Can't copy " + p + " to " + np + ":" + ex.getMessage());
            }
        } );
        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, errors.toString(), TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Files copied", TITLE_RESULT, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static long countSize(FilesModel filesModel) {
        long ret = 0;
        for (String s : filesModel.getFiles()) {
            Path p = Paths.get(s);
            File fl = p.toFile();
            ret += countFileSize(fl);
        }
        filesModel.setSize(ret);
        return ret;
    }
    
    private static long countFileSize(File f) {
        if (f.isFile()) {
            return f.length();
        } else if (f.isDirectory()) {
            long ret = 0;
            for (File fl : f.listFiles()) {
                ret += countFileSize(fl);
            }
            return ret;
        }
        return 0;
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
        public int showDialog(Component parent, String approveButtonText) throws HeadlessException {
            MyFileChooserImagePreview acc = (MyFileChooserImagePreview)getAccessory();
            countSize(acc.getFilesModel());
            acc.updateSelectedFiles();
            int ret = super.showDialog(parent, approveButtonText);
            return ret;
        }
    }
    
    private JFileChooser makeFileListChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }
    private JFileChooser makeDirChooser() {
        JFileChooser fileChooser = new JFileChooser();
        File file2open = dir2copy;
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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
        fileChooser.setAccessory(new MyFileChooserImagePreview(fileChooser, filesModel));
        return fileChooser;
    }
    
    public static class FilesModel {
        LinkedHashSet<String> files = new LinkedHashSet<>();
        long size;

        public LinkedHashSet<String> getFiles() {
            return files;
        }

        public void setFiles(LinkedHashSet<String> files) {
            this.files = files;
        }

        public long getSize() {
            return size;
        }

        public String getSizeString() {
            StringBuilder sb = new StringBuilder();
            long thousand = 1000;
            long rl = size;
            while (rl > thousand) {
                long r = rl % thousand;
                StringBuilder rs = new StringBuilder();
                rs.append(r);
                while(rs.length() < 3) {
                    rs.insert(0, 0);
                }
                sb.insert(0, rs.toString()).insert(0, " ");
                rl = rl / thousand;
            }
            if (rl > 0) {
                sb.insert(0, rl);
            }
            return sb.toString();
        }
        
        public void setSize(long size) {
            this.size = size;
        }
        
    }
    
    public static class MyFileChooserImagePreview extends FileChooserImagePreview {
        private JTextArea selectedFiles = new JTextArea();
        private final FilesModel filesModel;
        private JLabel sizeLabel = new JLabel();
        
        private void removeFile(File fl) {
            if (fl != null) {
                long flSize = countFileSize(fl);
                if (filesModel.getFiles().remove(fl.getAbsolutePath())) {
                    filesModel.setSize(filesModel.getSize() - flSize);
                    updateSelectedFiles();
                }
            }            
        }
        
        public MyFileChooserImagePreview(final JFileChooser fc, final FilesModel filesModel) {
            super(fc);
            this.filesModel = filesModel;
            JPanel buttons = new JPanel(new FlowLayout());
            JButton add = new JButton("+");
            JButton del = new JButton("-");
            JButton delLst = new JButton("\"-\"");
            delLst.setToolTipText("Remove the line in the list");
            buttons.add(add);
            buttons.add(del);
            buttons.add(delLst);
            buttons.add(sizeLabel);
            add.addActionListener( e -> {
                File fl = getFile();
                if (fl != null) {
                    long flSize = countFileSize(fl);
                    if (filesModel.getFiles().add(fl.getAbsolutePath())) {
                        filesModel.setSize(filesModel.getSize() + flSize);
                        updateSelectedFiles();
                    }
                }
            } );
            del.addActionListener( e -> {
                removeFile(getFile());
            } );
            delLst.addActionListener( e -> {
                int cp = selectedFiles.getCaretPosition();
                int cpb = cp - 1;
                StringBuilder sb = new StringBuilder();
                String sa = "";
                do {
                    try {
                        sb.insert(0, sa);
                        sa = selectedFiles.getText(cpb, 1);
                    } catch (BadLocationException ex) {
                        break;
                    }
                    cpb --;
                } while (!sa.equals("\n"));
                
                sa = "";
                do {
                    try {
                        sb.append(sa);
                        sa = selectedFiles.getText(cp, 1);
                    } catch (BadLocationException ex) {
                        break;
                    }
                    cp ++;
                } while (!sa.equals("\n"));
                //System.err.println("--------->" + sb.toString()+"<-------");
                removeFile(new File(sb.toString()));
                
            } );
            add(buttons, BorderLayout.NORTH);
            add(selectedFiles, BorderLayout.CENTER);
            selectedFiles.setPreferredSize(new Dimension(FileChooserImagePreview.PREVIEW_WIDTH, FileChooserImagePreview.PREVIEW_HEIGHT));
            setPreferredSize(new Dimension(FileChooserImagePreview.PREVIEW_WIDTH * 2, FileChooserImagePreview.PREVIEW_HEIGHT));
        }
        
        public void updateSelectedFiles() {
            sizeLabel.setText(filesModel.getSizeString());
            updateSelectedFilesTA(selectedFiles, filesModel.getFiles());
        }
        
        @Override
        protected void addImagePreview() {}

        public FilesModel getFilesModel() {
            return filesModel;
        }
        
    }
    
    private void updateSelectedFiles() {
        updateSelectedFilesTA(selectedFilesTA, filesModel.getFiles());
        saveFile(tempFile, true);
    }
    
    public static void updateSelectedFilesTA(JTextArea selectedFiles, Collection<String> files) {
        final StringBuilder sb = new StringBuilder();
        files.stream().forEach(s -> {sb.append(s).append("\n");});
        selectedFiles.setText(sb.toString());
    }    

    public void showFrame() {
        setVisible(true);
        openFile(tempFile, true);
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
