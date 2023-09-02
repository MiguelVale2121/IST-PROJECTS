package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.core.WarehouseManager;
import ggc.core.product.Batch;
import ggc.core.exception.UnknownObjectKeyException;

/**
 * Show all products.
 */
class DoShowBatchesByProduct extends Command<WarehouseManager> {

  DoShowBatchesByProduct(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_BY_PRODUCT, receiver);
    addStringField("productId", Message.requestProductKey());
  }

  @Override
  public final void execute() throws CommandException {
    String productId = stringField("productId");
    try {
      for(Batch batch : _receiver.getProduct(productId).getBatches()) {
        _display.addLine(batch.toString());
      }
      _display.display();
    } catch (UnknownObjectKeyException e) {
      throw new UnknownProductKeyException(e.getObjectKey());
    }
  }

}
