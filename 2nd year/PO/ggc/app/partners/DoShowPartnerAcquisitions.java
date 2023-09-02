package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.UnknownObjectKeyException;
//FIXME import classes
import ggc.core.transaction.Transaction;
import ggc.core.transaction.Transaction.TransactionType;

/**
 * Show all transactions for a specific partner.
 */
class DoShowPartnerAcquisitions extends Command<WarehouseManager> {

  DoShowPartnerAcquisitions(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER_ACQUISITIONS, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _display.addAll(_receiver.getTransactionsByPartner(stringField("partnerId"), TransactionType.ACQUISITION));
      _display.display();
    } catch (UnknownObjectKeyException exception) {
      throw new UnknownPartnerKeyException(exception.getObjectKey());
    }
  }

}
