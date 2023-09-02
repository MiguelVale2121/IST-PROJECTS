package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.app.exception.UnknownProductKeyException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.UnknownObjectKeyException;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addStringField("productId", Message.requestProductKey());
  }

  @Override
  public void execute() throws CommandException {
    String partnerId = stringField("partnerId");
    String productId = stringField("productId");

    try {
      _receiver.toggleNotificationStatus(partnerId, productId);
    } catch (UnknownObjectKeyException exception) {
      switch(exception.getType()) {
        case PARTNER:
          throw new UnknownPartnerKeyException(exception.getObjectKey());
        case PRODUCT:
          throw new UnknownProductKeyException(exception.getObjectKey());
        default:
          exception.printStackTrace();
      }
    }
  }
  
}
