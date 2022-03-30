package com.rkoyanagui.minesweeper.vision;

import com.rkoyanagui.minesweeper.model.Field;
import java.io.Serial;
import javax.swing.JFrame;

public class MainScreen extends JFrame
{
  public MainScreen()
  {
    Field field = new Field(30, 16, 50);
    add(new FieldPanel(field));

    setTitle("Minesweeper");
    setSize(690, 438);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setVisible(true);
  }

  public static void main(String[] args)
  {
    new MainScreen();
  }

  @Serial
  private static final long serialVersionUID = 3504972098504032945L;
}
