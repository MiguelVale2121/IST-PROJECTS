package ggc.app.transactions;

import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnavailableProductException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.NotEnoughResourcesException;
import ggc.core.exception.UnknownObjectKeyException;

/**
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addStringField("productId", Message.requestProductKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerBreakdown(stringField("partnerId"), stringField("productId"), Form.requestInteger(Message.requestAmount()));
    } catch (UnknownObjectKeyException exception) {
      switch(exception.getType()) {
        case PARTNER:
          throw new UnknownPartnerKeyException(exception.getObjectKey());
        case PRODUCT:
          throw new UnknownProductKeyException(exception.getObjectKey());
        default:
      }
    } catch (NotEnoughResourcesException exception) {
      throw new UnavailableProductException(exception.getObjectKey(), exception.getRequestedAmount(), exception.getAvailableAmount());
    }
  }

}
