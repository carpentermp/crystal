package au.id.bjf.dlx;

import au.id.bjf.dlx.data.ColumnObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSolver {

  private static final byte[][] TEST_MATRIX_1 = {
      {0, 0, 1, 0, 1, 1, 0},
      {1, 0, 0, 1, 0, 0, 1},
      {0, 1, 1, 0, 0, 1, 0},
      {1, 0, 0, 1, 0, 0, 0},
      {0, 1, 0, 0, 0, 0, 1},
      {0, 0, 0, 1, 1, 0, 1}};

  private ColumnObject h;

  @Before
  public void setUp() throws Exception {
    h = DLX.buildSparseMatrix(TEST_MATRIX_1,
        new Object[]{"A", "B", "C", "D", "E", "F", "G"}, true);
  }

  @After
  public void tearDown() throws Exception {
    h = null;
  }

  @Test
  public void testRunDlxSolver() {
    DLX.solve(h, false, new TestDLXResultProcessor(
        new Object[][]{
            {"A", "D"},
            {"B", "G"},
            {"C", "E", "F"}}));
  }

  @Test
  public void testRunDlxSolverWithSHeuristic() {
    DLX.solve(h, true, new TestDLXResultProcessor(
        new Object[][]{
            {"A", "D"},
            {"E", "F", "C"},
            {"B", "G"}}));
  }

  @SuppressWarnings("WeakerAccess")
  class TestDLXResultProcessor implements DLXResultProcessor {

    Object[][] expectedResults;

    public TestDLXResultProcessor(Object[][] expectedResults) {
      this.expectedResults = expectedResults;
    }

    public boolean processResult(DLXResult result) {
      System.out.println(result.toString());

      final Iterator<List<Object>> rows = result.rows();
      int i = 0;
      while (rows.hasNext()) {
        final List<Object> row = rows.next();
        final Iterator<Object> nodes = row.iterator();
        int j = 0;
        while (nodes.hasNext()) {
          final Object node = nodes.next();
          assertTrue("Encountered unexpected value '" + node +
                  "' on row " + i + ", node" + j,
              j < expectedResults[i].length);
          assertEquals("Object mismatch in output, row " + i +
              ", node" + j, expectedResults[i][j], node);

          j++;
        }

        assertEquals("Node count in row " + i + " is wrong",
            expectedResults[i].length, j);

        i++;
      }

      assertEquals("Row count in expected and actual results must " +
          "be equal", expectedResults.length, i);

      return false;  // we want first result only
    }

  }

}
