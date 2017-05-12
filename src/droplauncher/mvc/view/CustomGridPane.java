/*
 * Copyright (C) 2017 Adakite
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package droplauncher.mvc.view;

import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Container class which makes it easier to manipulate and organize GridPane
 * objects.
 */
public class CustomGridPane {

  private GridPane gridPane;
  private int column; /* current column cursor */
  private int row; /* current row cursor */
  private ArrayList<Node> nodes;

  public CustomGridPane() {
    this.gridPane = new GridPane();
//    this.gridPane.setPadding(new Insets(0, 0, 0, 0));
//    this.gridPane.setHgap(0);
//    this.gridPane.setVgap(0);
    this.column = 0;
    this.row = 0;
    this.nodes = new ArrayList<>();
  }

  public GridPane get() {
    this.gridPane.getChildren().clear();
    this.nodes.forEach((node) -> {
      this.gridPane.getChildren().add(node);
    });
//    this.gridPane.setMinWidth(Region.USE_PREF_SIZE);
    return this.gridPane;
  }

  public void setGaps(int hGap, int vGap) {
    this.gridPane.setHgap(hGap);
    this.gridPane.setVgap(vGap);
  }

  /**
   * Adds the specified node to the class GridPane object.
   *
   * @param node specified node to add
   * @param nextRow whether to set the cursor to the next row after adding
   */
  public void add(Node node, boolean nextRow) {
    GridPane.setConstraints(node, this.column, this.row);
    this.nodes.add(node);
    nextColumn();
    if (nextRow) {
      nextRow();
    }
  }

  /**
   * Adds the specified node to the class GridPane object.
   *
   * @param node specified node to add
   * @see #add(javafx.scene.Node, boolean)
   */
  public void add(Node node) {
    add(node, false);
  }

  /**
   * Sets the cursor to the next column.
   */
  public void nextColumn() {
    this.column++;
  }

  /**
   * Sets the cursor to the next row, first column.
   */
  public void nextRow() {
    this.column = 0;
    this.row++;
  }

}
