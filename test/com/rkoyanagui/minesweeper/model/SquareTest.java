package com.rkoyanagui.minesweeper.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SquareTest
{
  private Square s33;

  @BeforeEach
  void setup()
  {
    s33 = new Square(3, 3);
  }

  @Test
  void positionTest()
  {
    Square s = new Square(4, 5);
    assertEquals(4, s.getX());
    assertEquals(5, s.getY());
  }

  @Test
  void miningTest()
  {
    assumeFalse(s33.isMined());
    s33.mine();
    assertTrue(s33.isMined());
  }

  @Test
  void flaggingTest()
  {
    assumeFalse(s33.isFlagged());
    s33.registerObserver((s, e) -> assertEquals(SquareEvent.FLAGGED, e));
    s33.flipFlag();
    assertTrue(s33.isFlagged());
  }

  @Test
  void unflaggingTest()
  {
    assumeFalse(s33.isFlagged());
    s33.flipFlag();
    assumeTrue(s33.isFlagged());
    s33.registerObserver((s, e) -> assertEquals(SquareEvent.UNFLAGGED, e));
    s33.flipFlag();
    assertFalse(s33.isFlagged());
  }

  @Test
  void cannotFlagExposedSquareTest()
  {
    assumeFalse(s33.isFlagged());
    s33.uncover();
    s33.flipFlag();
    assertFalse(s33.isFlagged());
  }

  @Test
  void resetTest()
  {
    s33.mine();
    s33.flipFlag();
    s33.reset();
    assertFalse(s33.isMined());
    assertFalse(s33.isFlagged());

    s33.uncover();
    s33.reset();
    assertFalse(s33.isExposed());
  }

  @Test
  void addSameColumnNeighbourTest()
  {
    Square s32 = new Square(3, 2);
    assertTrue(s33.addNeighbour(s32));
  }

  @Test
  void addSameRowNeighbourTest()
  {
    Square s23 = new Square(2, 3);
    assertTrue(s33.addNeighbour(s23));
  }

  @Test
  void addDiagonalNeighbourTest()
  {
    Square s44 = new Square(4, 4);
    assertTrue(s33.addNeighbour(s44));
  }

  @Test
  void notANeighbourTest()
  {
    Square s31 = new Square(3, 1);
    assertFalse(s33.addNeighbour(s31));
  }

  @Test
  void uncoverSingleSquareTest()
  {
    assertTrue(s33.uncover());
  }

  @Test
  void doesNotUncoverFlaggedSquareTest()
  {
    s33.flipFlag();
    assertFalse(s33.uncover());
  }

  @Test
  void uncoverMinedSquareTest()
  {
    s33.registerObserver((s, e) -> assertEquals(SquareEvent.EXPLODED, e));
    s33.mine();
    assertTrue(s33.uncover());
  }

  @Test
  void chainedUncoveringTest()
  {
    Square s32 = new Square(3, 2);
    Square s31 = new Square(3, 1);
    s33.addNeighbour(s32);
    s32.addNeighbour(s31);
    s33.uncover();
    assertTrue(s33.isExposed());
    assertTrue(s32.isExposed());
    assertTrue(s31.isExposed());
  }

  @Test
  void safeNeighbourhoodTest()
  {
    Square s32 = new Square(3, 2);
    Square s22 = new Square(2, 2);
    s33.addNeighbour(s32);
    s33.addNeighbour(s22);
    s33.uncover();
    assertTrue(s33.isExposed());
    assertTrue(s32.isExposed());
    assertTrue(s22.isExposed());
    assertTrue(s33.isSafeNeighbourhood());
  }

  @Test
  void unsafeNeighbourhoodTest()
  {
    Square s32 = new Square(3, 2);
    Square s22 = new Square(2, 2);
    s33.addNeighbour(s32);
    s33.addNeighbour(s22);
    s22.mine();
    s33.uncover();
    assertTrue(s33.isExposed());
    assertFalse(s32.isExposed());
    assertFalse(s22.isExposed());
    assertFalse(s33.isSafeNeighbourhood());
  }

  @Test
  void surroundingMineCountTest()
  {
    Square s32 = new Square(3, 2);
    Square s22 = new Square(2, 2);
    s33.addNeighbour(s32);
    s33.addNeighbour(s22);
    s22.mine();
    assertEquals(1, s33.surroundingMineCount());
    s32.mine();
    assertEquals(2, s33.surroundingMineCount());
  }

  @Test
  void chainedUncoveringInterruptedByFlagTest()
  {
    Square s32 = new Square(3, 2);
    Square s31 = new Square(3, 1);
    s33.addNeighbour(s32);
    s32.addNeighbour(s31);
    s32.flipFlag();
    s33.uncover();
    assertTrue(s33.isExposed());
    assertFalse(s32.isExposed());
    assertFalse(s31.isExposed());
  }

  @Test
  void chainedUncoveringInterruptedByMineTest()
  {
    Square s32 = new Square(3, 2);
    Square s31 = new Square(3, 1);
    s31.mine();
    Square s21 = new Square(3, 1);
    s33.addNeighbour(s32);
    s32.addNeighbour(s31);
    s31.addNeighbour(s21);
    s33.uncover();
    assertTrue(s33.isExposed());
    assertTrue(s32.isExposed());
    assertFalse(s31.isExposed());
    assertFalse(s21.isExposed());
  }

  @Test
  void doesNotUncoverSameSquareTwiceTest()
  {
    assumeFalse(s33.isExposed());
    assertTrue(s33.uncover());
    assertTrue(s33.isExposed());
    assertFalse(s33.uncover());
    assertTrue(s33.isExposed());
  }

  @Test
  void clearedSquareTest()
  {
    assertFalse(s33.isCleared());

    s33.uncover();
    assertTrue(s33.isCleared());

    s33.reset();
    s33.mine();
    assertTrue(s33.isCleared());

    s33.reset();
    s33.mine();
    s33.uncover();
    assertFalse(s33.isCleared());
  }
}
