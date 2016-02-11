package de.fastfelix771.townywands.utils;

public interface ReturningInvoker<T, E> {

    public E invoke(T parameter);

}