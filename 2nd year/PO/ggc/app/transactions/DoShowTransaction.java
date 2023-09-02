package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnknownTransactionKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.exception.UnknownObjectKeyException.ObjectType;

/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<WarehouseManager> {

  public DoShowTransaction(WarehouseManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    addIntegerField("transactionId", Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _display.addLine(_receiver.viewTransaction(integerField("transactionId")));
      _display.display();
    } catch (UnknownObjectKeyException exception) {
      if (exception.getType() == ObjectType.TRANSACTION) {
        throw new UnknownTransactionKeyException(Integer.parseInt(exception.getObjectKey()));
      }
    }
  }

}
