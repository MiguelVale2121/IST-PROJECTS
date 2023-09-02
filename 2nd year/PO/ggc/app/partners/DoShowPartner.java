package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.core.WarehouseManager;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.app.exception.UnknownPartnerKeyException;

//FIXME import classes

/**
 * Show partner.
 */
class DoShowPartner extends Command<WarehouseManager> {

  DoShowPartner(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
  }

  @Override
  public void execute() throws CommandException {
    String partnerId = stringField("partnerId");
    try {
      _display.addLine(_receiver.getPartner(partnerId));
      _display.addAll(_receiver.getPartner(partnerId).showNotifications());
      _display.display();
    } catch (UnknownObjectKeyException e) {
      throw new UnknownPartnerKeyException(e.getObjectKey());
    }
  }

}
