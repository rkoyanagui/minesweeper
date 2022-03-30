package com.rkoyanagui.minesweeper.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldTest
{
  private static final int WIDTH = 30;
  private static final int HEIGHT = 16;
  private static final int MINE_COUNT = 50;
  private Field field;

  @BeforeEach
  void setup()
  {
    field = new Field(WIDTH, HEIGHT, MINE_COUNT);
  }

  @Test
  void sizeTest()
  {
    assertEquals(WIDTH * HEIGHT, field.getSquares().size());
  }

  @Test
  void dimensionsTest()
  {
    Square first = field.getSquares().get(0);
    assertEquals(0, first.getX());
    assertEquals(0, first.getY());

    Square last = field.getSquares().get(WIDTH * HEIGHT - 1);
    assertEquals(WIDTH - 1, last.getX());
    assertEquals(HEIGHT - 1, last.getY());
  }

  @Test
  void cornerNeighbourhoodTest()
  {
    Optional<Square> s1 = field.getSquares()
        .stream()
        .filter(s -> s.getX() == 0 && s.getY() == 0)
        .findFirst();
    assumeTrue(s1.isPresent());
    assertEquals(3, s1.get().getNeighbours().size());
  }

  @Test
  void nonCornerNeighbourhoodTest()
  {
    Optional<Square> s2 = field.getSquares()
        .stream()
        .filter(s -> s.getX() == 1 && s.getY() == 1)
        .findFirst();
    assumeTrue(s2.isPresent());
    assertEquals(8, s2.get().getNeighbours().size());
  }

  @Test
  void mineCountTest()
  {
    long mineCount = field.getSquares().stream().filter(s -> s.isMined()).count();
    assertEquals(MINE_COUNT, mineCount);
  }

  @Test
  void flipFlagTest()
  {
    int x = 1;
    int y = 2;
    field.flipFlag(x, y);
    Optional<Square> square = field.getSquares()
        .stream()
        .filter(s -> s.getX() == x && s.getY() == y)
        .findFirst();
    assumeTrue(square.isPresent());
    assertTrue(square.get().isFlagged());
  }

  @Test
  void uncoverTest()
  {
    int x = 1;
    int y = 2;
    Optional<Square> square = field.getSquares()
        .stream()
        .filter(s -> s.getX() == x && s.getY() == y)
        .findFirst();
    assumeTrue(square.isPresent());
    square.get().reset();
    field.uncover(x, y);
    assertTrue(square.get().isExposed());
  }

  @Test
  void explosionTest()
  {
    field.registerObserver(event -> assertTrue(event.isDefeated()));
    Optional<Square> square = field.getSquares().stream().filter(s -> s.isMined()).findFirst();
    assumeTrue(square.isPresent());
    square.get().uncover();
  }

  @Test
  void winConditionTest()
  {
    field.registerObserver(event -> assertTrue(event.isVictorious()));
    assertFalse(field.isMinefieldCleared());
    field.getSquares().forEach(s ->
    {
      if (s.isMined())
      {
        s.flipFlag();
      }
      else
      {
        s.uncover();
      }
    });
    assertTrue(field.isMinefieldCleared());
  }

  @Test
  void resetTest()
  {
    Integer checkSumBefore = field.getSquares()
        .stream()
        .filter(s -> s.isMined())
        .map(s -> (s.getX() + 1) * (s.getY() + 1))
        .reduce(0, (a, b) -> a + b);

    field.getSquares().get(0).flipFlag();
    field.reset();
    assertTrue(field.getSquares().stream().allMatch(s -> s.isNotFlagged() && s.isNotExposed()));

    int actualMineCount = (int) field.getSquares().stream().filter(s -> s.isMined()).count();
    assertEquals(field.getMineCount(), actualMineCount);

    Integer checkSumAfter = field.getSquares()
        .stream()
        .filter(s -> s.isMined())
        .map(s -> (s.getX() + 1) * (s.getY() + 1))
        .reduce(0, (a, b) -> a + b);
    // Checking that the mines have been randomly replaced on the field.
    assertNotEquals(checkSumBefore, checkSumAfter);
  }
}
