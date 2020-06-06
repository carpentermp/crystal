package com.mpc.dlx.crystal;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public class SolverParms {

  public static final long HOUR = 1000 * 3600;
  public static final long NEVER = Long.MAX_VALUE;
  public static final long INFINITE = Long.MAX_VALUE;

  private Molecule molecule = null;
  private Molecule molecule2 = null;
  private String inputDir = null;
  private String outputDir = null;
  private int startingCrystal = 0;
  private int endingCrystal = 0;
  private int extraHoles = 0;
  private boolean dedup = true;
  private boolean doGZip = false;
  private long quitTime = NEVER;
  private long maxSolutionCount = INFINITE;
  private boolean requireSymmetry = false;

  public SolverParms(String... args) {
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      switch (arg) {
        case "-o":
          outputDir(args[++i]);
          break;
        case "-s":
          startingCrystal(Integer.parseInt(args[++i]));
          break;
        case "-e":
          endingCrystal(Integer.parseInt(args[++i]));
          break;
        case "-q":
          quitTime(getQuitTime(args[++i]));
          break;
        case "-m":
          maxSolutionCount(Long.parseLong(args[++i]));
          break;
        case "-d":
          dedup(false);
          break;
        case "-h":
          extraHoles(Integer.parseInt(args[++i]));
          break;
        case "-g":
          doGZip(true);
          break;
        case "-y":
          requireSymmetry(true);
          break;
        default:
          if (molecule == null) {
            parseMoleculeParameter(arg);
          }
          else {
            inputDir(arg);
          }
          break;
      }
    }
    if (molecule == null) {
      throw new IllegalArgumentException("Molecule must be specified.");
    }
    if (inputDir == null) {
      throw new IllegalArgumentException("Input directory must be specified.");
    }
    if ((extraHoles % molecule.size()) != 0) {
      throw new IllegalArgumentException("Hole count must be multiple of molecule size");
    }
  }

  private void parseMoleculeParameter(String parm) {
    try {
      int moleculeNumber = Integer.parseInt(parm);
      molecule(Molecule.fromNumber(moleculeNumber));
    }
    catch (NumberFormatException e) {
      String[] parts = parm.split("_");
      Molecule m1 = parseMolecule(parts[0]);
      Molecule m2 = null;
      if (parts.length > 1) {
        m2 = parseMolecule(parts[1]);
        if (m1.equals(m2)) {
          throw new IllegalArgumentException("both molecules must not be the same one!");
        }
        if (m1.size() != m2.size()) {
          throw new IllegalArgumentException("both molecules must be the same size");
        }
        if (m1.getName().compareTo(m2.getName()) > 0) {
          Molecule temp = m1;
          m1 = m2;
          m2 = temp;
        }
      }
      molecule(m1);
      if (m2 != null) {
        molecule2(m2);
      }
    }
  }

  private static Molecule parseMolecule(String moleculeStr) {
    if (moleculeStr.toLowerCase().equals("dimer")) {
      return Molecule.dimer;
    }
    if (moleculeStr.startsWith("m")) {
      moleculeStr = moleculeStr.substring(1).toLowerCase();
    }
    boolean isRight = false;
    if (moleculeStr.endsWith("l") || moleculeStr.endsWith("r")) {
      if (moleculeStr.endsWith("r")) {
        isRight = true;
      }
      moleculeStr = moleculeStr.substring(0, moleculeStr.length() - 1);
    }
    Molecule molecule = Molecule.fromNumber(Integer.parseInt(moleculeStr));
    if (isRight) {
      molecule = molecule.mirror(Direction.Right);
    }
    return molecule;
  }

  public SolverParms(SolverParms parms) {
    this.molecule = parms.molecule;
    this.molecule2 = parms.molecule2;
    this.inputDir = parms.inputDir;
    this.outputDir = parms.outputDir;
    this.startingCrystal = parms.startingCrystal;
    this.endingCrystal = parms.endingCrystal;
    this.extraHoles = parms.extraHoles;
    this.dedup = parms.dedup;
    this.doGZip = parms.doGZip;
    this.quitTime = parms.quitTime;
    this.maxSolutionCount = parms.maxSolutionCount;
    this.requireSymmetry = parms.requireSymmetry;
  }

  public Molecule getMolecule() {
    return molecule;
  }

  public Molecule getMolecule2() {
    return molecule2;
  }

  public int getStartingCrystal() {
    return startingCrystal;
  }

  public int getEndingCrystal() {
    return endingCrystal;
  }

  public int getExtraHoles() {
    return extraHoles;
  }

  public String getInputDir() {
    return inputDir;
  }

  public String getOutputDir() {
    return outputDir;
  }

  public boolean isDedup() {
    return dedup;
  }

  public boolean isDoGZip() {
    return doGZip;
  }

  public long getQuitTime() {
    return quitTime;
  }

  public long getMaxSolutionCount() {
    return maxSolutionCount;
  }

  public boolean isRequireSymmetry() {
    return requireSymmetry;
  }

  public SolverParms molecule(Molecule molecule) {
    this.molecule = molecule;
    return this;
  }

  public SolverParms molecule2(Molecule molecule) {
    this.molecule2 = molecule;
    return this;
  }

  public SolverParms crystal(int crystal) {
    startingCrystal = crystal;
    endingCrystal = crystal;
    return this;
  }

  public SolverParms startingCrystal(int startingCrystal) {
    this.startingCrystal = startingCrystal;
    if (endingCrystal < startingCrystal) {
      endingCrystal = startingCrystal;
    }
    return this;
  }

  public SolverParms endingCrystal(int endingCrystal) {
    this.endingCrystal = endingCrystal;
    if (startingCrystal > endingCrystal) {
      startingCrystal = endingCrystal;
    }
    return this;
  }

  public SolverParms extraHoles(int extraHoles) {
    this.extraHoles = extraHoles;
    return this;
  }

  public SolverParms inputDir(String inputDir) {
    this.inputDir = inputDir;
    return this;
  }

  public SolverParms outputDir(String outputDir) {
    this.outputDir = outputDir;
    return this;
  }

  public SolverParms dedup(boolean dedup) {
    this.dedup = dedup;
    return this;
  }

  public SolverParms doGZip(boolean doGZip) {
    this.doGZip = doGZip;
    return this;
  }

  public SolverParms quitTime(long quitTime) {
    this.quitTime = quitTime;
    return this;
  }

  @SuppressWarnings("unused")
  public SolverParms quitAfter(long intervalMillis) {
    return quitTime(System.currentTimeMillis() + intervalMillis);
  }

  public SolverParms maxSolutionCount(long maxSolutionCount) {
    this.maxSolutionCount = maxSolutionCount;
    return this;
  }

  public SolverParms requireSymmetry(boolean requireSymmetry) {
    this.requireSymmetry = requireSymmetry;
    return this;
  }

  private static long getQuitTime(String parm) {
    try {
      char ch = parm.charAt(parm.length() - 1);
      int multiplier = 3600; // default is hours
      if (!Character.isDigit(ch)) {
        parm = parm.substring(0, parm.length() - 1);
        switch (ch) {
          case 's':
            multiplier = 1;
            break;
          case 'm':
            multiplier = 60;
            break;
          case 'h':
            multiplier = 60 * 60;
            break;
          case 'd':
            multiplier = 60 * 60 * 24;
            break;
        }
      }
      return System.currentTimeMillis() + 1000 * multiplier * Long.parseLong(parm);
    }
    catch (Exception e) {
      throw new IllegalArgumentException("Invalid expiration value: " + parm + ", e.g. -q 12h");
    }
  }

  public static void usage() {
    System.out.println("Usage: java -jar crystal.jar [options] molecule(s) inputDir");
    System.out.println("  molecule(s) parameter should be one of:");
    System.out.println("      a number between 1 and 22, or...");
    System.out.println("      a string in this form: m09R_m10L or...");
    System.out.println("      the word 'dimer'");
    System.out.println("  inputDir points to parent directory where all crystal information is stored");
    System.out.println("  Options:");
    System.out.println("  -o dir         output directory (no output if not specified)");
    System.out.println("  -s num         starting crystal number (0 if not specified)");
    System.out.println("  -e num         ending crystal number (same as starting number if not specified)");
    System.out.println("  -d             don't deduplicate results");
    System.out.println("  -h num         count of extra holes (must be multiple of molecule size)");
    System.out.println("  -g             gzip output file(s)");
    System.out.println("  -q num{m|h|d}  quit after period of time e.g. -q 12h");
    System.out.println("  -m num         quit after {num} solutions have been found");
    System.out.println("  -y             require symmetry in all solutions");
  }

}
