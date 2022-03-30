package com.rkoyanagui.minesweeper.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Square implements Serializable
{
  private final int x;
  private final int y;
  private boolean mined;
  private boolean flagged;
  private boolean exposed;
  private final List<Square> neighbours;
  private final transient Set<SquareObserver> observers;

  public Square(int x, int y)
  {
    this.x = x;
    this.y = y;
    this.mined = false;
    this.flagged = false;
    this.exposed = false;
    this.neighbours = new ArrayList<>(8);
    this.observers = new HashSet<>();
  }

  public void registerObserver(SquareObserver observer)
  {
    observers.add(observer);
  }

  protected void notifyObservers(SquareEvent event)
  {
    observers.forEach(o -> o.observe(this, event));
  }

  protected boolean addNeighbour(Square other)
  {
    boolean onTheSameRow = this.y == other.y;
    boolean onTheSameColumn = this.x == other.x;
    boolean crosswise = onTheSameRow || onTheSameColumn;
    boolean diagonal = !crosswise;
    int deltaX = Math.abs(this.x - other.x);
    int deltaY = Math.abs(this.y - other.y);
    int delta = deltaX + deltaY;

    if (crosswise && delta == 1 || diagonal && delta == 2)
    {
      neighbours.add(other);
      return true;
    }
    return false;
  }

  public boolean isSafeNeighbourhood()
  {
    return neighbours.stream().noneMatch(n -> n.isMined());
  }

  public List<Square> getNeighbours()
  {
    return neighbours;
  }

  public int surroundingMineCount()
  {
    return (int) neighbours.stream().filter(n -> n.isMined()).count();
  }

  public int getX()
  {
    return x;
  }

  public int getY()
  {
    return y;
  }

  public boolean isMined()
  {
    return mined;
  }

  public boolean isNotMined()
  {
    return !mined;
  }

  protected void mine()
  {
    mined = true;
  }

  public boolean isFlagged()
  {
    return flagged;
  }

  public boolean isNotFlagged()
  {
    return !flagged;
  }

  public void flipFlag()
  {
    if (!exposed)
    {
      flagged = !flagged;
      if (flagged)
      {
        notifyObservers(SquareEvent.FLAGGED);
      }
      else
      {
        notifyObservers(SquareEvent.UNFLAGGED);
      }
    }
  }

  public boolean isExposed()
  {
    return exposed;
  }

  public boolean isNotExposed()
  {
    return !exposed;
  }

  public void setExposed(boolean exposed)
  {
    this.exposed = exposed;
    if (exposed)
    {
      notifyObservers(SquareEvent.EXPOSED);
    }
  }

  public boolean uncover()
  {
    if (!exposed && !flagged)
    {
      exposed = true;
      if (mined)
      {
        notifyObservers(SquareEvent.EXPLODED);
      }
      else
      {
        setExposed(true);
        if (isSafeNeighbourhood())
        {
          neighbours.forEach(n -> n.uncover());
        }
      }
      return true;
    }
    return false;
  }

  public boolean isCleared()
  {
    return mined ^ exposed;
  }

  protected void reset()
  {
    mined = false;
    flagged = false;
    exposed = false;
    notifyObservers(SquareEvent.RESET);
  }

  @Serial
  private static final long serialVersionUID = -1812446029736930780L;
}
