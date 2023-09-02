package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;
import java.io.IOException;

import ggc.core.WarehouseManager;
import ggc.core.exception.MissingFileAssociationException;

import ggc.app.exception.FileOpenFailedException;

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  String filename;

  /** @param receiver */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    try {
      try {
        _receiver.save();
      } catch (MissingFileAssociationException ignored) {
        _receiver.saveAs(Form.requestString(Message.newSaveAs()));
      }
    } catch (IOException ignored) {
      throw new FileOpenFailedException(_receiver.getFilename());
    }
  }
}
