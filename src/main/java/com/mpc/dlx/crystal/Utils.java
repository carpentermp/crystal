package com.mpc.dlx.crystal;

import java.io.*;
import java.util.Collection;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings({"WeakerAccess", "ConstantConditions", "SameParameterValue"})
public class Utils {

  private Utils() {
  }

  public static String getResourceFilename(String pathToResource) {
    return Utils.class.getClassLoader().getResource(pathToResource).getFile();
  }

  public static File createSubDir(String baseDir, String subDirName) {
    String subDirPath = addTrailingSlash(addTrailingSlash(baseDir) + subDirName);
    File file = new File(subDirPath);
    if (!file.exists() && !file.mkdir()) {
      throw new IllegalArgumentException("Couldn't create file");
    }
    return file;
  }

  public static String addTrailingSlash(String path) {
    return path + (path.endsWith("/") ? "" : "/");
  }

  public static String join(Collection collection, String separator) {
    StringBuilder sb = new StringBuilder();
    for (Object item : collection) {
      if (sb.length() > 0) {
        sb.append(separator);
      }
      sb.append(item.toString());
    }
    return sb.toString();
  }

  public static BufferedWriter getWriter(String filename) throws IOException {
    return filename == null ? null : getWriter(getOutputStream(filename));
  }

  public static BufferedWriter getWriter(OutputStream outputStream) throws IOException {
    return outputStream == null ? null : new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
  }

  public static OutputStream getOutputStream(String filename) throws IOException {
    int index = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
    if (index > 0) { // this is a directory, so make sure that the directories exist up to where the filename is to be created.
      String directory = filename.substring(0, index);
      File dir = new File(directory);
      //noinspection ResultOfMethodCallIgnored
      dir.mkdirs();
    }
    return getOutputStream(new File(filename));
  }

  public static OutputStream getOutputStream(File file) throws IOException {
    OutputStream outputStream = new FileOutputStream(file);
    if (file.getName().toLowerCase().endsWith(".gz")) {
      outputStream = new GZIPOutputStream(outputStream);
    }
    else if (file.getName().toLowerCase().endsWith(".zip")) {
      // Create a single-entry zip file.  Name the single internal file with the same
      //  filename as the zip file, except with "txt" instead of "zip".
      ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
      zipOutputStream.putNextEntry(new ZipEntry(swapExtension(file.getName(), "txt"))); // advance to the first (and hopefully only) entry.
      outputStream = zipOutputStream;
    }
    return outputStream;
  }

  /**
   * Swap the filename extension.  e.g., "a/b/test.zip", "txt" => "a/b/test.txt"
   * @param filename - original filename
   * @param newExtension - new extension to use (without dot, e.g., "txt")
   * @return original filename with its file extension (if any) removed and the new extension appended.
   */
  public static String swapExtension(String filename, String newExtension) {
    int pos = filename.lastIndexOf('.');
    if (pos > 0) {
      return filename.substring(0, pos) + "." + newExtension;
    }
    else {
      return filename + "." + newExtension;
    }
  }

}
