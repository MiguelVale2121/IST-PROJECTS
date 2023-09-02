package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.main.Message;
import ggc.core.WarehouseManager;

/**
 * Show current date.
 */
class DoDisplayDate extends Command<WarehouseManager> {

  DoDisplayDate(WarehouseManager receiver) {
    super(Label.SHOW_DATE, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    _display.addLine(Message.currentDate(_receiver.getDate().getValue()));
    _display.display();
  }

}
