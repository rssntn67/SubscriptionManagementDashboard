
package it.arsinfo.smd.ui.vaadin;

import java.util.List;

public abstract class SmdBaseSearch<T>
        extends SmdChangeHandler {

        
    public abstract List<T> find();
        
}
