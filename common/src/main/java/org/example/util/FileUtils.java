package org.example.util;

import cn.hutool.core.io.file.FileReader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @program: monitor
 * @description: 文件夹工具类
 * @author: stop.yc
 * @create: 2023-04-16 22:33
 **/
public class FileUtils {

    public static List<File> traverFolder(String path, String fileType) {

        if (path == null || "".equals(path.trim())) {
            return Collections.emptyList();
        }

        File file = new File(path);

        List<File> files = new ArrayList<>();

        traverFolder(file, files, fileType);

        return files;
    }

    public static String findFile(String path, String fileName) {

        if (path == null || "".equals(path.trim()) || fileName == null || "".equals(fileName.trim())) {
            return "";
        }

        File file = new File(path);

        return find(file, fileName);
    }

    private static void traverFolder(File folder, List<File> files, String filter) {
        if (folder.isFile()) {
            if (folder.getName().endsWith(filter)) {
                files.add(folder);
            }
            return;
        }

        if (folder.isDirectory()) {
            File[] listFiles = folder.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    traverFolder(listFile, files, filter);
                }
            }
        }
    }


    private static String find(File folder, String fileName) {
        if (folder.isFile()) {
            if (folder.getName().equalsIgnoreCase(fileName)) {
                FileReader fileReader = FileReader.create(folder);
                return fileReader.readString();
            }
            return null;
        }

        if (folder.isDirectory()) {
            File[] listFiles = folder.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    String s = find(listFile, fileName);
                    if (s != null) {
                        return s;
                    }
                }
            }
        }
        return null;
    }
}
