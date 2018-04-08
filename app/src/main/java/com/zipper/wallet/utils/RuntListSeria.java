package com.zipper.wallet.utils;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by EDZ on 2017/12/22.
 */

public class RuntListSeria<E> implements Serializable,List {
    List<E> list;

    public RuntListSeria(List<E> list){
        this.list = list;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @NonNull
    @Override
    public Iterator iterator() {
        return list.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray() ;
    }

    @Override
    public boolean add(Object o) {
        return list.add((E) o);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean addAll(@NonNull Collection collection) {
        return list.addAll(collection);
    }

    @Override
    public boolean addAll(int position, @NonNull Collection collection) {
        return list.addAll(position,collection);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object get(int position) {
        return list.get(position);
    }

    @Override
    public Object set(int position, Object o) {
        return list.set(position, (E) o);
    }

    @Override
    public void add(int position, Object o) {
        list.add(position, (E) o);
    }

    @Override
    public Object remove(int position) {
        return list.remove(position);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @NonNull
    @Override
    public ListIterator listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator listIterator(int i) {
        return list.listIterator(i);
    }

    @NonNull
    @Override
    public List subList(int i, int i1) {
        return list.subList(i,i1);
    }

    @Override
    public boolean retainAll(@NonNull Collection collection) {
        return list.retainAll(collection);
    }

    @Override
    public boolean removeAll(@NonNull Collection collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean containsAll(@NonNull Collection collection) {
        return list.containsAll(collection);
    }

    @NonNull
    @Override
    public Object[] toArray(@NonNull Object[] objects) {
        return list.toArray(objects);
    }
}
