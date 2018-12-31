package net.sakuratrak.schoolstorycollection.core;

import java.util.List;

public final class ListDataProvider<T> implements IListedDataProvidable<T> {

    private List<T> _context;

    @Override
    public int count() {
        return _context.size();
    }

    @Override
    public T get(int index) {
        return _context.get(index);
    }

    public ListDataProvider(List<T> context){
        _context = context;
    }

    public List<T> get_context() {
        return _context;
    }

    public void set_context(List<T> _context) {
        this._context = _context;
    }
}
