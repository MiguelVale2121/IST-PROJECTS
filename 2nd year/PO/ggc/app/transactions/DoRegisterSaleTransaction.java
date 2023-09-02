package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnavailableProductException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.NotEnoughResourcesException;
import ggc.core.exception.UnknownObjectKeyException;


/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addIntegerField("deadline", Message.requestPaymentDeadline());
    addStringField("productId", Message.requestProductKey());
    addIntegerField("amount", Message.requestAmount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.registerSaleByCredit(stringField("partnerId"), stringField("productId"), integerField("deadline"), integerField("amount"));
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
