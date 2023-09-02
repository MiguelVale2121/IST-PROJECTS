package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.DuplicatePartnerKeyException;
import ggc.core.WarehouseManager;
//FIXME import classes

/**
 * Register new partner.
 */
class DoRegisterPartner extends Command<WarehouseManager> {

  DoRegisterPartner(WarehouseManager receiver) {
    super(Label.REGISTER_PARTNER, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addStringField("name", Message.requestPartnerName());
    addStringField("address", Message.requestPartnerAddress());

  }

  @Override
  public void execute() throws CommandException {
    String partnerId = stringField("partnerId");
    String name = stringField("name");
    String address = stringField("address");

    if (!_receiver.addPartner(partnerId, name, address)) {
      throw new DuplicatePartnerKeyException(partnerId);
    }
  }

}
