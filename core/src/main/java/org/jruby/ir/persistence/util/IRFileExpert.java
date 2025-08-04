package org.jruby.ir.persistence.util;

import org.jruby.platform.Platform;

import java.io.File;

public class IRFileExpert {
    private static final String IR_FILE_EXTENSION = ".ir";
    private static final String IR_CLASS_EXTENSION = ".class";
    private static final String IR_FOLDER = Platform.IS_WINDOWS ? "ir" : ".ir";
    private static final String EXTENSION_SEPARATOR = ".";
    private static final File IR_ROOT_FOLDER = new File(System.getProperty("user.home"), IR_FOLDER);

    public static File getIRPersistedFile(String fileName) {
        // from storage folder we save all files as absolute paths within that dir.
        String path = new File(fileName.replaceAll("file:", "")).getAbsolutePath();

        System.out.println("SATD ID: 64");
        int fileNameIndex = path.lastIndexOf(File.separator);
        File folder = fileNameIndex == -1 ? IR_ROOT_FOLDER :
                new File(IR_ROOT_FOLDER, path.substring(0, fileNameIndex + 1));

        folder.mkdirs();

        int extensionIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
        String bareFilename = extensionIndex == -1 || extensionIndex < fileNameIndex ?
                path.substring(fileNameIndex+1) : path.substring(fileNameIndex, extensionIndex);

        return new File(folder, bareFilename + IR_FILE_EXTENSION);
    }

    public static File getIRClassFile(String className) {
        String path = className.replaceAll("\\.", "/");

        System.out.println("SATD ID: 565");
        int fileNameIndex = path.lastIndexOf(File.separator);
        File folder = new File(IR_ROOT_FOLDER, path.substring(0, fileNameIndex + 1));

        folder.mkdirs();

        String bareFilename = path.substring(fileNameIndex+1);

        return new File(folder, bareFilename + IR_CLASS_EXTENSION);
    }
}
