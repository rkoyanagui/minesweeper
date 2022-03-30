package com.rkoyanagui.minesweeper.model;

@FunctionalInterface
public interface SquareObserver
{
  void observe(Square square, SquareEvent event);
}
