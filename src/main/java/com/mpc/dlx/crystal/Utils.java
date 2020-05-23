package com.mpc.dlx.crystal;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

  public static BufferedWriter getWriter(OutputStream outputStream) {
    return outputStream == null ? null : new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
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

  /**
   * booths algorithm for least rotation of a string
   * @param s string to compute rotation of
   * @return the index of the best starting place for the string
   */
  public static int leastRotation(String s) {
    s = s + s; // concat to self to avoid modular arithmetic
    int[] f = new int[s.length()];
    Arrays.fill(f, -1);
    int k = 0;
    for (int j = 1; j < s.length(); j++) {
      char sj = s.charAt(j);
      int i = f[j - k - 1];
      while (i != -1 && sj != s.charAt(k + i + 1)) {
        if (sj < s.charAt(k + i + 1)) {
          k = j - i - 1;
        }
        i = f[i];
      }
      if (sj != s.charAt(k + i + 1)) {
        if (sj < s.charAt(k)) {
          k = j;
        }
        f[j - k] = -1;
      }
      else {
        f[j - k] = i + 1;
      }
    }
    return k;
  }

  /**
   * finds the smallest repeating substring within a given string
   * @param s the string to find repeating substrings within
   * @return the smallest repeating substring (the given string if no repeating substrings)
   */
  public static String smallestSubstring(String s) {
    for (int i = 1; i <= s.length() / 2; i++) {
      String subStr = s.substring(0, i);
      String remaining = s.substring(i);
      while (remaining.startsWith(subStr)) {
        remaining = remaining.substring(i);
      }
      if (remaining.isEmpty()) {
        return subStr;
      }
    }
    return s;
  }

  public static String rotateOptimally(String s) {
    return rotate(s, leastRotation(s));
  }

  /**
   * rotates a string a number of times
   * @param str the string to rotate
   * @param amountToRotate the index where the string should begi
   * @return the rotated string
   */
  public static String rotate(String str, int amountToRotate) {
    if (amountToRotate == 0) {
      return str;
    }
    return str.substring(amountToRotate) + str.substring(0, amountToRotate);
  }

  public static void main(String[] args) {
    System.out.println(leastRotation("bccabccabcca"));
    System.out.println(leastRotation("ghiabcdef"));
    System.out.println(smallestSubstring("abcabcabc"));
    System.out.println(smallestSubstring("abcabcabd"));
    System.out.println(smallestSubstring("bccabccabcca"));
    String smallest = smallestSubstring("bccabccabcca");
    System.out.println(rotate(smallest, leastRotation(smallest)));
  }

}
