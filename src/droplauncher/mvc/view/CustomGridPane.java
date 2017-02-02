package droplauncher.mvc.view;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class CustomGridPane {

  private GridPane gridPane;
  private int x;
  private int y;
  private ArrayList<Node> nodes;

  public CustomGridPane() {
    this.gridPane = new GridPane();
    this.gridPane.setPadding(new Insets(0, 0, 0, 0));
    this.gridPane.setHgap(0);
    this.gridPane.setVgap(0);
    this.x = 0;
    this.y = 0;
    this.nodes = new ArrayList<>();
  }

  public GridPane get() {
    return this.gridPane;
  }

  public void pack() {
    for (Node node : nodes) {
      this.gridPane.getChildren().add(node);
    }
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public void setGap(int hGap, int vGap) {
    this.gridPane.setHgap(hGap);
    this.gridPane.setVgap(vGap);
  }

  public void add(Node node, boolean nextRow) {
    GridPane.setConstraints(node, this.x, this.y);
    this.nodes.add(node);
    nextColumn();
    if (nextRow) {
      nextRow();
    }
  }

  public void add(Node node) {
    add(node, false);
  }

  public void setPosition(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void nextColumn() {
    this.x++;
  }

  public void nextRow() {
    this.x = 0;
    this.y++;
  }

}
