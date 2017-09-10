package com.mpc.dlx.crystal;

import java.io.File;
import java.util.Collection;

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

}
