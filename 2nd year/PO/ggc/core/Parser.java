package ggc.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.FileReader;
import java.io.BufferedReader;

import ggc.core.exception.BadEntryException;
import ggc.core.exception.UnknownObjectKeyException;
import ggc.core.partner.Partner;
import ggc.core.product.DerivativeProduct;
import ggc.core.product.RecipeComponent;
import ggc.core.product.SimpleProduct;

public class Parser {
    
  private Warehouse _store;

  public Parser(Warehouse w) {
    _store = w;
  }

  void parseFile(String filename) throws IOException, BadEntryException, UnknownObjectKeyException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;

      while ((line = reader.readLine()) != null)
        parseLine(line);
    }
  }

  private void parseLine(String line) throws BadEntryException, BadEntryException, UnknownObjectKeyException {
    String[] components = line.split("\\|");

    switch (components[0]) {
      case "PARTNER":
        parsePartner(components, line);
        break;
      case "BATCH_S":
        parseSimpleProduct(components, line);
        break;

      case "BATCH_M":
        parseAggregateProduct(components, line);
        break;
        
      default:
        throw new BadEntryException("Invalid type element: " + components[0]);
    }
  }

  //PARTNER|id|nome|endereço
  private void parsePartner(String[] components, String line) throws BadEntryException {
    if (components.length != 4)
      throw new BadEntryException("Invalid partner with wrong number of fields (4): " + line);
    
    String id = components[1];
    String name = components[2];
    String address = components[3];
    
    _store.addPartner(new Partner(id, name, address));
  }

  //BATCH_S|idProduto|idParceiro|prec ̧o|stock-actual
  private void parseSimpleProduct(String[] components, String line) throws BadEntryException, UnknownObjectKeyException {
    if (components.length != 5)
      throw new BadEntryException("Invalid number of fields (4) in simple batch description: " + line);
    
    String idProduct = components[1];
    String idPartner = components[2];
    double price = Double.parseDouble(components[3]);
    int stock = Integer.parseInt(components[4]);
    
    try {
      _store.getProduct(idProduct);
    } catch (UnknownObjectKeyException ignored) {
      _store.addProduct(new SimpleProduct(idProduct));
    }

    _store.addBatch(idPartner, idProduct, stock, price);

  }
 
    
  //BATCH_M|idProduto|idParceiro|prec ̧o|stock-actual|agravamento|componente-1:quantidade-1#...#componente-n:quantidade-n
  private void parseAggregateProduct(String[] components, String line) throws BadEntryException, UnknownObjectKeyException {
    if (components.length != 7)
      throw new BadEntryException("Invalid number of fields (7) in aggregate batch description: " + line);
    
    String idProduct = components[1];
    String idPartner = components[2];

    try {
      _store.getProduct(idProduct);
    } catch (UnknownObjectKeyException ignored) {
      List<RecipeComponent> recipe = new ArrayList<>();
      
      for (String component : components[6].split("#")) {
        String[] recipeComponent = component.split(":");
        recipe.add(new RecipeComponent(_store.getProduct(recipeComponent[0]), Integer.parseInt(recipeComponent[1])));
      }
      _store.addProduct(new DerivativeProduct(idProduct, recipe, Double.parseDouble(components[5])));
    }
    
    double price = Double.parseDouble(components[3]);
    int stock = Integer.parseInt(components[4]);

    _store.addBatch(idPartner, idProduct, stock, price);

  }
}