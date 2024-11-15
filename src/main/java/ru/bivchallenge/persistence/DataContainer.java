package ru.bivchallenge.persistence;

public interface DataContainer<T> {
    T get();
    void set(T data);
}
