package xyz.lilyflower.wavelength.util;

import java.util.ArrayList;

public class ChainedArrayList<T> extends ArrayList<T> {
    public ChainedArrayList<T> chainedAdd(T element) {
        super.add(element);
        return this;
    }
    
    public ChainedArrayList<T> chainedRemove(T element) {
        super.remove(element);
        return this;
    }
    
    public ChainedArrayList<T> chainedInsert(int index, T element) {
        super.add(index, element);
        return this;
    }
}
