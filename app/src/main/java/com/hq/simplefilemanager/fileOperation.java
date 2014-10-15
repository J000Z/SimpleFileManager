package com.hq.simplefilemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by jack on 9/4/14.
 */
public class FileOperation {
    String operation;
    List<File> sourceFiles;
    File destFile;

    FileOperation(String operation, List<File> sourceFiles, File destFile) {
        this.operation = operation;
        this.sourceFiles = sourceFiles;
        this.destFile = destFile;
    }

    FileOperation(String operation, List<File> sourceFiles) {
        this.operation = operation;
        this.sourceFiles = sourceFiles;
        this.destFile = null;
    }

    public boolean isCut() {
        return operation.equals("cut");
    }

    public boolean isDelete() {
        return operation.equals("delete");
    }

    public boolean isCopy() {
        return operation.equals("copy");
    }

    public int execute(){
        if (operation.equals("cut")) {
            File sourceFile = null;
            File destFile = null;
            for (int i=0; i<sourceFiles.size(); i++) {
                sourceFile = sourceFiles.get(i);
                destFile = new File(this.destFile.getAbsolutePath() + "/" + sourceFile.getName());
                System.out.println("cut " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
                try {
                    sourceFile.renameTo(destFile);
                    //copyFile(sourceFile, destFile);
                    //sourceFile.delete();
                    System.out.println("done cut");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (operation.equals("copy")) {
            File sourceFile = null;
            File destFile = null;
            for (int i=0; i<sourceFiles.size(); i++) {
                sourceFile = sourceFiles.get(i);
                destFile = new File(this.destFile.getAbsolutePath() + "/" + sourceFile.getName());
                System.out.println("copy " + sourceFile.getAbsolutePath() + " to " + destFile.getAbsolutePath());
                try {
                    copyFile(sourceFile, destFile);
                    System.out.println("done copy");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (operation.equals("delete")) {
            for (int i=0; i<sourceFiles.size(); i++) {
                System.out.println("delete " + sourceFiles.get(i).getAbsolutePath());
                try {
                    delete(sourceFiles.get(i));
                    //sourceFiles.get(i).delete();
                    System.out.println("done delete");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

        }
        return 0;
    }

    public void delete(File f){
        if (!f.isDirectory()) {
            f.delete();
            return;
        }
        File[] FileList = f.listFiles();
        if (FileList.length == 0) {
            f.delete();
        } else {
            for (int i = 0; i < FileList.length; i++) {
                delete(FileList[i]);
            }
        }
        f.delete();
    }

    public List<File[]> getArgumentPairs(File sourceFile, File destFile) {
        List<File[]> list = new LinkedList<File[]>();
        Stack<File[]> stack = new Stack<File[]>();
        if (!sourceFile.isDirectory()) {return null;}
        File[] files = sourceFile.listFiles();
        for (int i=0; i<files.length; i++) {
            File[] pair = new File[2];
            pair[0] = files[i];
            pair[1] = new File(destFile.getAbsolutePath() + "/" + files[i].getName());
            stack.push(pair);
            System.out.println("push [" + pair[0].getAbsolutePath() + " , " + pair[1].getAbsolutePath() + "]");
        }
        while (!stack.empty()) {
            File[] pair = stack.pop();
            if (pair[0].isDirectory()) {
                files = pair[0].listFiles();
                for (int i=0; i<files.length; i++) {
                    File[] pair2 = new File[2];
                    pair2[0] = files[i];
                    pair2[1] = new File(pair[1].getAbsolutePath() + "/" + files[i].getName());
                    stack.push(pair2);
                    System.out.println("push [" + pair2[0].getAbsolutePath() + " , " + pair2[1].getAbsolutePath() + "]");
                }
            } else {
                list.add(pair);
            }
        }
        return list;
    }

    public void copyFile(File sourceFile, File destFile) throws Exception {
        System.out.println(sourceFile.getAbsoluteFile() + " ---> " + destFile.getAbsolutePath());
        if (sourceFile.isDirectory() && sourceFile.listFiles().length == 0) {
            sourceFile.mkdir();
            return;
        }

        if (sourceFile.isDirectory()) {
            List<File[]> list = getArgumentPairs(sourceFile, destFile);
            for (int i=0; i<list.size(); i++) {
                copyFile(list.get(i)[0],list.get(i)[1]);
            }
            return;
        }

        if(!destFile.exists()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdir();
            }
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

}
