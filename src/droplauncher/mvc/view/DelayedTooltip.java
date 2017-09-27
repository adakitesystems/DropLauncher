/**
 * Source: https://stackoverflow.com/questions/41462622/keep-tooltip-open-as-long-as-mouse-is-over-it
 * Date: Jan 5 at 11:38
 * Author: kerner1000
 *
 * Modified by: Adakite
 * Date: 2017-09-27
 */

package droplauncher.mvc.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class DelayedTooltip extends Tooltip {

  private int duration;
  private BooleanProperty isHoveringPrimary;
  private BooleanProperty isHoveringSecondary;

  public DelayedTooltip() {
    this.duration = 0;
    this.isHoveringPrimary = new SimpleBooleanProperty(false);
    this.isHoveringSecondary = new SimpleBooleanProperty(false);
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public BooleanProperty isHoveringPrimary() {
    return this.isHoveringPrimary;
  }

  public BooleanProperty isHoveringSecondary() {
    return this.isHoveringSecondary;
  }

  public void setHoveringTargetPrimary(Node node) {
    node.setOnMouseEntered(e -> this.isHoveringPrimary.set(true));
    node.setOnMouseExited(e -> this.isHoveringPrimary.set(false));
  }

  public void setHoveringTargetSecondary(Node node) {
    node.setOnMouseEntered(e -> this.isHoveringSecondary.set(true));
    node.setOnMouseExited(e -> this.isHoveringSecondary.set(false));
  }

  @Override
  public void hide() {
    if (this.isHoveringPrimary.get() == true || this.isHoveringSecondary.get() == true) {
      Timeline timeline = new Timeline();
      KeyFrame key = new KeyFrame(Duration.millis(this.duration));
      timeline.getKeyFrames().add(key);
      timeline.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent t) {
            DelayedTooltip.super.hide();
          }
      });
      timeline.play();
    } else {
      DelayedTooltip.super.hide();
    }
  }

}
