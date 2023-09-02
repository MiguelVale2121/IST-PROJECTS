package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.core.WarehouseManager;
import ggc.core.product.Batch;

/**
 * Show available batches.
 */
class DoShowAvailableBatches extends Command<WarehouseManager> {

  DoShowAvailableBatches(WarehouseManager receiver) {
    super(Label.SHOW_AVAILABLE_BATCHES, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    for(Batch batch : _receiver.getAvailableBatches()) {
      _display.addLine(batch.toString());
    }
    _display.display();
  }

}
