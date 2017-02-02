package droplauncher.mvc.controller;

import adakite.utils.AdakiteUtils;
import droplauncher.bwheadless.BotFile;
import droplauncher.mvc.model.Model;
import droplauncher.mvc.view.View;
import droplauncher.starcraft.Race;
import javafx.event.ActionEvent;

public class Controller {

  private Model model;
  private View view;

  public Controller() {

  }

  public void setModel(Model model) {
    this.model = model;
  }

  public void setView(View view) {
    this.view = view;
  }

  public void handle(ActionEvent event) {
    updateView();
  }

  public void updateView() {
    this.view.update();
  }

  public String getBwapiDll() {
    return this.model.getBWHeadless().getBwapiDll();
  }

  public BotFile getBotModule() {
    return this.model.getBWHeadless().getBotModule();
  }

  public String getBotName() {
    return this.model.getBWHeadless().getBotName();
  }

  public Race getBotRace() {
    return this.model.getBWHeadless().getBotRace();
  }

  private void startBWHeadless() {
    this.model.startBWHeadless();
  }

  private void stopBWHeadless() {
    this.model.stopBWHeadless();
  }

}
