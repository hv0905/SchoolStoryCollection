package net.sakuratrak.schoolstorycollection.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class ReadOnlyList<E> implements List<E> {
    @Override
    public abstract int size();

    @Override
    public abstract E get(int index);



    @Override
    public boolean isEmpty() {
        return size() == 0;
    }



    @Override
    public boolean contains(@Nullable Object o) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(@Nullable T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean remove(@Nullable Object o) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();

    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();

    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();

    }

    @Override
    public int indexOf(@Nullable Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        throw new UnsupportedOperationException();

    }

    @NonNull
    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();

    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();

    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();

    }
}
