package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.core.WarehouseManager;
//FIXME import classes
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.exception.UnknownObjectKeyException.ObjectType;

/**
 * Lookup payments by given partner.
 */
public class DoLookupPaymentsByPartner extends Command<WarehouseManager> {

  public DoLookupPaymentsByPartner(WarehouseManager receiver) {
    super(Label.PAID_BY_PARTNER, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _display.addAll(_receiver.lookupPaymentsByPartner(stringField("partnerId")));
      _display.display();
    } catch (UnknownObjectKeyException exception) {
      if (exception.getType() == ObjectType.PARTNER) {
        throw new UnknownPartnerKeyException(exception.getObjectKey());
      } else {
        exception.printStackTrace();
      }
    }
  }

}
