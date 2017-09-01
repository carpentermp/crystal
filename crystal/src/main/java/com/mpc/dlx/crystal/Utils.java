package com.mpc.dlx.crystal;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
public class Utils {

  public static String getResourceFilename(String pathToResource) {
    return Utils.class.getClassLoader().getResource(pathToResource).getFile();
  }

  public static String getResourceDirectory(String pathToResource) {
    String resourceFilename = getResourceFilename(pathToResource);
    return resourceFilename.substring(0, resourceFilename.lastIndexOf("/") + 1);
  }

  public static File createSubDir(String baseDir, String subDirName) throws IOException {
    String subDirPath = addTrailingSlash(addTrailingSlash(baseDir) + subDirName);
    File file = new File(subDirPath);
      if (!file.mkdir()) {
        throw new IllegalArgumentException("Couldn't create file");
      }
    return file;
  }

  public static String addTrailingSlash(String path) {
    return path + (path.endsWith("/") ? "" : "/");
  }

}
