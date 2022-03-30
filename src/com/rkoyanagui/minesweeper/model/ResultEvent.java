package com.rkoyanagui.minesweeper.model;

public record ResultEvent(boolean victorious)
{
  public boolean isVictorious()
  {
    return victorious;
  }

  public boolean isDefeated()
  {
    return !victorious;
  }
}
