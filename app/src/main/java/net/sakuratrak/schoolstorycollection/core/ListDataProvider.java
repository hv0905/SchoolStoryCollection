package net.sakuratrak.schoolstorycollection.core;

import java.util.List;

public final class ListDataProvider<T> implements IListedDataProvidable<T> {

    private List<T> _context;

    public ListDataProvider(List<T> context) {
        _context = context;
    }

    @Override
    public int count() {
        if (_context == null) return 0;
        return _context.size();
    }

    @Override
    public T get(int index) {
        return _context.get(index);
    }

    public List<T> get_context() {
        return _context;
    }

    public void set_context(List<T> _context) {
        this._context = _context;
    }
}
