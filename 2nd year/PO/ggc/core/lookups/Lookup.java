package ggc.core.lookups;

import java.util.Collection;

import ggc.core.Warehouse;
import ggc.core.exception.UnknownObjectKeyException;

public class Lookup {
    private LookupStrategy _strategy;
    private Warehouse _store;

    public Lookup(Warehouse store) {
        _store = store;
    }
    
    public void setStrategy(LookupStrategy strategy) {
        _strategy = strategy;
    }

    public Collection<Object> execute() throws UnknownObjectKeyException {
        return _strategy.execute(_store);
    }
}
