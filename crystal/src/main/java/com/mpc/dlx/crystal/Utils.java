package com.mpc.dlx.crystal;

@SuppressWarnings({"WeakerAccess", "ConstantConditions"})
public class Utils {

  public static String getResourceFilename(String pathToResource) {
    return Utils.class.getClassLoader().getResource(pathToResource).getFile();
  }

}
