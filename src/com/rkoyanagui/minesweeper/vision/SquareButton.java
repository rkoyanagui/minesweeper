package com.rkoyanagui.minesweeper.vision;

import com.rkoyanagui.minesweeper.model.Square;
import com.rkoyanagui.minesweeper.model.SquareEvent;
import com.rkoyanagui.minesweeper.model.SquareObserver;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class SquareButton extends JButton implements SquareObserver, MouseListener
{
  private static final Color FLAGGED_BG = new Color(8, 179, 247);
  private static final Color EXPLODED_BG = new Color(189, 66, 68);
  private static final Color DEFAULT_BG = new Color(184, 184, 184);
  private static final Color GREEN_TEXT = new Color(0, 100, 0);
  private final Square square;

  public SquareButton(Square square)
  {
    this.square = square;
    setBackground(DEFAULT_BG);
    setOpaque(true);
    setBorder(BorderFactory.createBevelBorder(0));
    addMouseListener(this);
    this.square.registerObserver(this);
  }

  @Serial
  private static final long serialVersionUID = -7761316540724833237L;

  @Override
  public void observe(Square square, SquareEvent event)
  {
    switch (event)
    {
      case EXPOSED -> applyExposedStyle();
      case FLAGGED -> applyFlaggedStyle();
      case EXPLODED -> applyExplodedStyle();
      default -> applyDefaultStyle();
    }
  }

  protected void applyExposedStyle()
  {
    setBackground(DEFAULT_BG);
    setBorder(BorderFactory.createLineBorder(Color.GRAY));
    if (square.isMined())
    {
      setForeground(Color.BLACK);
      setText("M");
    }
    else if (!square.isSafeNeighbourhood())
    {
      switch (square.surroundingMineCount())
      {
        case 1 -> setForeground(GREEN_TEXT);
        case 2 -> setForeground(Color.BLUE);
        case 3 -> setForeground(Color.YELLOW);
        case 4, 5, 6 -> setForeground(Color.RED);
        case 7, 8 -> setForeground(Color.MAGENTA);
        default -> setForeground(Color.BLACK);
      }
      setText(String.valueOf(square.surroundingMineCount()));
    }
    else
    {
      setText("");
    }
  }

  protected void applyFlaggedStyle()
  {
    setBackground(FLAGGED_BG);
    setForeground(Color.BLACK);
    setText("F");
  }

  protected void applyExplodedStyle()
  {
    setBackground(EXPLODED_BG);
    setForeground(Color.WHITE);
    setText("X");
  }

  protected void applyDefaultStyle()
  {
    setBackground(DEFAULT_BG);
    setBorder(BorderFactory.createBevelBorder(0));
    setText("");
  }

  @Override
  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == 1)
    {
      square.uncover();
    }
    else
    {
      square.flipFlag();
    }
  }

  public void mouseClicked(MouseEvent e) { /* Fall through. */ }

  public void mouseReleased(MouseEvent e) { /* Fall through. */ }

  public void mouseEntered(MouseEvent e) { /* Fall through. */ }

  public void mouseExited(MouseEvent e) { /* Fall through. */ }
}
