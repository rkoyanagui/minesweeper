package com.rkoyanagui.minesweeper.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class Field implements SquareObserver
{
  private final int width;
  private final int height;
  private final int mineCount;
  private final List<Square> squares;
  private final Set<Consumer<ResultEvent>> observers;

  public Field(int width, int height, int mineCount)
  {
    this.width = width;
    this.height = height;
    this.mineCount = mineCount;
    this.squares = new ArrayList<>(width * height);
    this.observers = new HashSet<>();
    fillWithSquares();
    associateAllSquares();
    placeMinesAtRandom();
  }

  public void forEach(Consumer<Square> f)
  {
    squares.forEach(f);
  }

  protected void fillWithSquares()
  {
    for (int y = 0; y < height; y++)
    {
      for (int x = 0; x < width; x++)
      {
        Square square = new Square(x, y);
        square.registerObserver(this);
        squares.add(square);
      }
    }
  }

  protected void associateAllSquares()
  {
    for (Square s1 : squares)
    {
      for (Square s2 : squares)
      {
        s1.addNeighbour(s2);
      }
    }
  }

  protected void placeMinesAtRandom()
  {
    SecureRandom random = new SecureRandom();
    for (int m = 0; m < mineCount; m++)
    {
      Optional<Square> square;
      do
      {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        square = squares.stream()
            .filter(s -> s.getX() == x && s.getY() == y)
            .filter(s -> s.isNotMined())
            .findFirst();
      }
      while (square.isEmpty());

      square.ifPresent(s -> s.mine());
    }
  }

  protected void flipFlag(int x, int y)
  {
    squares.stream()
        .filter(s -> s.getX() == x && s.getY() == y)
        .findFirst()
        .ifPresent(s -> s.flipFlag());
  }

  protected void uncover(int x, int y)
  {
    squares.stream()
        .filter(s -> s.getX() == x && s.getY() == y)
        .findFirst()
        .ifPresent(s -> s.uncover());
  }

  protected void revealMines()
  {
    squares.stream()
        .filter(s -> s.isMined() && s.isNotExposed())
        .forEach(s -> s.setExposed(true));
  }

  protected boolean isMinefieldCleared()
  {
    return squares.stream().allMatch(s -> s.isCleared());
  }

  public void reset()
  {
    squares.forEach(s -> s.reset());
    placeMinesAtRandom();
  }

  public void registerObserver(Consumer<ResultEvent> observer)
  {
    observers.add(observer);
  }

  protected void notifyObservers(boolean victorious)
  {
    observers.forEach(o -> o.accept(new ResultEvent(victorious)));
  }

  @Override
  public void observe(Square square, SquareEvent event)
  {
    if (SquareEvent.EXPLODED.equals(event))
    {
      revealMines();
      notifyObservers(false);
    }
    else if (isMinefieldCleared())
    {
      notifyObservers(true);
    }
  }

  public int getWidth()
  {
    return width;
  }

  public int getHeight()
  {
    return height;
  }

  public int getMineCount()
  {
    return mineCount;
  }

  public List<Square> getSquares()
  {
    return squares;
  }
}
