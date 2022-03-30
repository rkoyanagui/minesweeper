package com.rkoyanagui.minesweeper.vision;

import com.rkoyanagui.minesweeper.model.Field;
import java.awt.GridLayout;
import java.io.Serial;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FieldPanel extends JPanel
{
  public FieldPanel(Field field)
  {
    setLayout(new GridLayout(field.getHeight(), field.getWidth()));
    field.forEach(s -> add(new SquareButton(s)));
    field.registerObserver(event -> SwingUtilities.invokeLater(() ->
    {
      if (event.isVictorious())
      {
        JOptionPane.showMessageDialog(this, "Victory \\o/");
      }
      else
      {
        JOptionPane.showMessageDialog(this, "Defeat ¯\\_(ツ)_/¯");
      }
      field.reset();
    }));
  }

  @Serial
  private static final long serialVersionUID = -2206392644733426220L;
}
