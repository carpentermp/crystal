package au.id.bjf.nqueens.dlx;

import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import junit.framework.TestCase;

import static com.sun.tools.internal.ws.wsdl.parser.Util.fail;

/**
 * Sanity-test the solution to the N-Queens problem by generating the answers
 * and sanity checking each one
 */
public class TestNQueens extends TestCase {

	public void testArgumentHandling() {
		try {
			new NQueensSolver(-1);
			fail("Should not be able to construct solver with argument");
		} catch (Throwable t) {
			// Do nothing; expected
		}
		try {
			new NQueensSolver(0);
			fail("Should not be able to construct solver with argument");
		} catch (Throwable t) {
			// Do nothing; expected
		}
		try {
			new NQueensSolver(1);
			fail("Should not be able to construct solver with argument");
		} catch (Throwable t) {
			// Do nothing; expected
		}
		try {
			new NQueensSolver(2);
			fail("Should not be able to construct solver with argument");
		} catch (Throwable t) {
			// Do nothing; expected
		}
		try {
			new NQueensSolver(3);
			fail("Should not be able to construct solver with argument");
		} catch (Throwable t) {
			// Do nothing; expected
		}
		NQueensSolver solver = new NQueensSolver(4);
		assertNotNull(solver);
		solver = new NQueensSolver(8);
		assertNotNull(solver);
	}
	
	public void testNQueens() {
		for (int i = 4; i <= 8; ++i) {
			testNQueens(i);
		}
	}
	
	private void testNQueens(int n) {
		NQueensSolver solver = new NQueensSolver(n);
		assertNotNull(solver);
		TestNQueensDLXResultProcessor resultProcessor = 
			new TestNQueensDLXResultProcessor(n);
		solver.solve(resultProcessor);
		assertTrue("Should see at least one set of results",
				resultProcessor.getResultCount() > 0);
		System.out.println("For " + n + " queens, there are " + 
				resultProcessor.getResultCount() + " solutions");
	}

	/**
	 * Process results from the algorithm test run; we do nothing but do some
	 * sanity checking over the results.
	 */
	private class TestNQueensDLXResultProcessor implements DLXResultProcessor {
		
		private int n;
		private int resultCount = 0;
		
		TestNQueensDLXResultProcessor(int n) {
			this.n = n;
		}
		
		public boolean processResult(DLXResult result) {
			++resultCount;
			byte[] chessboard = NQueensSolver.mapResultIntoChessboard(result, n);
			
			// Verbose form
//			NQueensSolver.printChessboard(chessboard, n);
			
			// Compact form
			System.out.print(resultCount);
			System.out.print(": ");
			NQueensSolver.printChessboardCompact(chessboard, n);
			
			sanityCheckSolution(chessboard);
			return true; // see ALL results
		}
		
		private void sanityCheckSolution(byte[] chessboard) {
			assertNotNull(chessboard);
			assertEquals(n*n, chessboard.length);
			
			// Ranks
			for (int rank = 0; rank < n; ++rank) {
				int count = 0;
				for (int file = 0; file < n; ++file) {
					count += (chessboard[rank * n + file] != 0) ? 1 : 0;
				}
				assertTrue("Must be only one queen in rank " + rank +
						", got " + count, count == 1);
			}
			
			// Files
			for (int file = 0; file < n; ++file) {
				int count = 0;
				for (int rank = 0; rank < n; rank++) {
					count += (chessboard[rank * n + file] != 0) ? 1 : 0;
				}
				assertTrue("Must be only one queen in file " + file +
						", got " + count, count == 1);					
			}
			
			// Diagonals
			for (int diag = 0; diag < (n - 2); ++diag) {
				// diagonal cells all lie on (x,y) where diag == x + y
				int count = 0;
				for (int rank = 0; rank < n; rank++) {
					for (int file = 0; file < n; file++) {
						if (rank + file == diag) {
							count += (chessboard[rank * n + file] != 0) ? 1 : 0;
						}
					}
				}
				assertTrue("Must be maximum of one queen in diagonal " + 
						diag, (count == 0 || count == 1));
			}
			
			// Reverse diagonals
			for (int diag = 0; diag < (n - 2); ++diag) {
				// diagonal cells all lie on (x,y) where diag == n - 1 - x + y
				int count = 0;
				for (int rank = 0; rank < n; rank++) {
					for (int file = 0; file < n; file++) {
						if ((n - 1 - rank + file) == diag) {
							count += (chessboard[rank * n + file] != 0) ? 1 : 0;
						}
					}
				}
				assertTrue("Must be maximum of one queen in reverse diagonal " + 
						diag, (count == 0 || count == 1));
			}
		}
		
		int getResultCount() {
			return resultCount;
		}
	}
	
}
