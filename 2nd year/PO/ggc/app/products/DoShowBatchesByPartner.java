package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.core.WarehouseManager;
import ggc.core.product.Batch;
import ggc.core.exception.UnknownObjectKeyException;

/**
 * Show batches supplied by partner.
 */
class DoShowBatchesByPartner extends Command<WarehouseManager> {

  DoShowBatchesByPartner(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_SUPPLIED_BY_PARTNER, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
  }

  @Override
  public final void execute() throws CommandException {
    String partnerId = stringField("partnerId");
    try {
      for(Batch batch : _receiver.getBatchesByPartner(partnerId)) {
        _display.addLine(batch.toString());
      }
      _display.display();
    } catch (UnknownObjectKeyException e) {
      throw new UnknownPartnerKeyException(e.getObjectKey());
    }
  }

}
