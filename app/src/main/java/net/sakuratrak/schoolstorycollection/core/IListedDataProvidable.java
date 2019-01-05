package net.sakuratrak.schoolstorycollection.core;

public interface IListedDataProvidable<T> {

    int count();

    T get(int index);

}
