import static org.junit.Assert.assertEquals;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class ChessKnightTest {
   @Rule
   public TestRule benchmarkRun = new BenchmarkRule();

   @Test
   public void count_8x8_S2x1_E7x5() {
      int expectedResult = 3;
      assertEquals(expectedResult, ChessKnight.countMoves(8, 8, 2, 1, 7, 5));
   }

   @Test
   public void count_10x10_S1x1_E4x4() {
      int expectedResult = 2;
      assertEquals(expectedResult, ChessKnight.countMoves(10, 10, 1, 1, 4, 4));
   }

   @Test
   public void count_10x10_S1x1_E1x1() {
      int expectedResult = 0;
      assertEquals(expectedResult, ChessKnight.countMoves(10, 10, 1, 1, 1, 1));
   }

   @Test
   public void count_2x2_S1x1_E1x2_noMove() {
      int expectedResult = -1;
      assertEquals(expectedResult, ChessKnight.countMoves(2, 2, 1, 1, 1, 2));
   }

   @Test
   public void count_100x100_S7x1_E26x88() {
      int expectedResult = 44;
      assertEquals(expectedResult, ChessKnight.countMoves(100, 100, 7, 1, 26, 88));
   }

   @Test
   public void count_45x50_S26x5_E1x1() {
      int expectedResult = 13;
      assertEquals(expectedResult, ChessKnight.countMoves(45, 50, 26, 5, 1, 1));
   }

   @Test
   public void count_2x5_S1x1_E1x5() {
      int expectedResult = 2;
      assertEquals(expectedResult, ChessKnight.countMoves(2, 5, 1, 1, 1, 5));
   }

   @Test
   public void count_3x3_S1x1_E2x2() {
      int expectedResult = -1;
      assertEquals(expectedResult, ChessKnight.countMoves(3, 3, 1, 1, 2, 2));
   }

   @Test
   public void count_1000x1000_S950x950_E1000x1000() {
      int expectedResult = 34;
      assertEquals(expectedResult, ChessKnight.countMoves(1000, 1000, 950, 950, 1000, 1000));
   }

   @Test
   public void count_3x3_S1x1_E3x3() {
      int expectedResult = 4;
      assertEquals(expectedResult, ChessKnight.countMoves(3, 3, 1, 1, 3, 3));
   }

   @Test(timeout = 400)
   @BenchmarkOptions(concurrency = 1, warmupRounds = 50, benchmarkRounds = 100)
   public void count_perfomance1() {
      int expectedResult = 132;
      assertEquals(expectedResult, ChessKnight.countMoves(1000, 1000, 312, 67, 51, 50));
   }

   @Test(timeout = 10000)
   @BenchmarkOptions(concurrency = 1, warmupRounds = 50, benchmarkRounds = 100)
   public void count_perfomance2() {
      int expectedResult = 251;
      assertEquals(expectedResult, ChessKnight.countMoves(1000, 1000, 1, 500, 2, 1000));
   }
}
