package com.github.supermoonie.proxy.swing.gui.flow;

import javax.swing.*;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.function.Predicate;

/**
 * @author supermoonie
 * @since 2020/11/28
 */
public class FilterListModel<E> extends AbstractListModel<E> {

    private final Vector<E> all = new Vector<>();

    private final Vector<E> delegate = new Vector<>();

    private Predicate<E> predicate;

    public void setPredicate(Predicate<E> predicate) {
        this.predicate = predicate;
    }

    public void filter() {
        if (null == predicate) {
            return;
        }
        delegate.clear();
        all.stream().filter(predicate).forEach(delegate::add);
    }

    public Vector<E> getAll() {
        return all;
    }

    /**
     * Returns the number of components in this list.
     * <p>
     * This method is identical to {@code size}, which implements the
     * {@code List} interface defined in the 1.2 Collections framework.
     * This method exists in conjunction with {@code setSize} so that
     * {@code size} is identifiable as a JavaBean property.
     *
     * @return the number of components in this list
     * @see #size()
     */
    public int getSize() {
        return delegate.size();
    }

    /**
     * Returns the component at the specified index.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     * method to use is {@code get(int)}, which implements the {@code List}
     * interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param index an index into this list
     * @return the component at the specified index
     * @throws ArrayIndexOutOfBoundsException if the {@code index}
     *                                        is negative or greater than the current size of this
     *                                        list
     * @see #get(int)
     */
    public E getElementAt(int index) {
        return delegate.elementAt(index);
    }

    /**
     * Copies the components of this list into the specified array.
     * The array must be big enough to hold all the objects in this list,
     * else an {@code IndexOutOfBoundsException} is thrown.
     *
     * @param anArray the array into which the components get copied
     * @see Vector#copyInto(Object[])
     */
    public void copyInto(Object[] anArray) {
        delegate.copyInto(anArray);
    }

    /**
     * Trims the capacity of this list to be the list's current size.
     *
     * @see Vector#trimToSize()
     */
    public void trimToSize() {
        delegate.trimToSize();
    }

    /**
     * Increases the capacity of this list, if necessary, to ensure
     * that it can hold at least the number of components specified by
     * the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     * @see Vector#ensureCapacity(int)
     */
    public void ensureCapacity(int minCapacity) {
        delegate.ensureCapacity(minCapacity);
    }

    /**
     * Sets the size of this list.
     *
     * @param newSize the new size of this list
     * @see Vector#setSize(int)
     */
    public void setSize(int newSize) {
        int oldSize = delegate.size();
        delegate.setSize(newSize);
        if (oldSize > newSize) {
            fireIntervalRemoved(this, newSize, oldSize - 1);
        } else if (oldSize < newSize) {
            fireIntervalAdded(this, oldSize, newSize - 1);
        }
    }

    /**
     * Returns the current capacity of this list.
     *
     * @return the current capacity
     * @see Vector#capacity()
     */
    public int capacity() {
        return delegate.capacity();
    }

    /**
     * Returns the number of components in this list.
     *
     * @return the number of components in this list
     * @see Vector#size()
     */
    public int size() {
        return delegate.size();
    }

    /**
     * Tests whether this list has any components.
     *
     * @return {@code true} if and only if this list has
     * no components, that is, its size is zero;
     * {@code false} otherwise
     * @see Vector#isEmpty()
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * Returns an enumeration of the components of this list.
     *
     * @return an enumeration of the components of this list
     * @see Vector#elements()
     */
    public Enumeration<E> elements() {
        return delegate.elements();
    }

    /**
     * Tests whether the specified object is a component in this list.
     *
     * @param elem an object
     * @return {@code true} if the specified object
     * is the same as a component in this list
     * @see Vector#contains(Object)
     */
    public boolean contains(Object elem) {
        return delegate.contains(elem);
    }

    /**
     * Searches for the first occurrence of {@code elem}.
     *
     * @param elem an object
     * @return the index of the first occurrence of the argument in this
     * list; returns {@code -1} if the object is not found
     * @see Vector#indexOf(Object)
     */
    public int indexOf(Object elem) {
        return delegate.indexOf(elem);
    }

    /**
     * Searches for the first occurrence of {@code elem}, beginning
     * the search at {@code index}.
     *
     * @param elem  the desired component
     * @param index the index from which to begin searching
     * @return the index where the first occurrence of {@code elem}
     * is found after {@code index}; returns {@code -1}
     * if the {@code elem} is not found in the list
     * @see Vector#indexOf(Object, int)
     */
    public int indexOf(Object elem, int index) {
        return delegate.indexOf(elem, index);
    }

    /**
     * Returns the index of the last occurrence of {@code elem}.
     *
     * @param elem the desired component
     * @return the index of the last occurrence of {@code elem}
     * in the list; returns {@code elem} if the object is not found
     * @see Vector#lastIndexOf(Object)
     */
    public int lastIndexOf(Object elem) {
        return delegate.lastIndexOf(elem);
    }

    /**
     * Searches backwards for {@code elem}, starting from the
     * specified index, and returns an index to it.
     *
     * @param elem  the desired component
     * @param index the index to start searching from
     * @return the index of the last occurrence of the {@code elem}
     * in this list at position less than {@code index};
     * returns {@code -1} if the object is not found
     * @see Vector#lastIndexOf(Object, int)
     */
    public int lastIndexOf(Object elem, int index) {
        return delegate.lastIndexOf(elem, index);
    }

    /**
     * Returns the component at the specified index.
     * <blockquote>
     * <b>Note:</b> Although this method is not deprecated, the preferred
     * method to use is {@code get(int)}, which implements the
     * {@code List} interface defined in the 1.2 Collections framework.
     * </blockquote>
     *
     * @param index an index into this list
     * @return the component at the specified index
     * @throws ArrayIndexOutOfBoundsException if the index
     *                                        is negative or not less than the size of the list
     * @see #get(int)
     * @see Vector#elementAt(int)
     */
    public E elementAt(int index) {
        return delegate.elementAt(index);
    }

    /**
     * Returns the first component of this list.
     *
     * @return the first component of this list
     * @throws NoSuchElementException if this
     *                                vector has no components
     * @see Vector#firstElement()
     */
    public E firstElement() {
        return delegate.firstElement();
    }

    /**
     * Returns the last component of the list.
     *
     * @return the last component of the list
     * @throws NoSuchElementException if this vector
     *                                has no components
     * @see Vector#lastElement()
     */
    public E lastElement() {
        return delegate.lastElement();
    }


    /**
     * Adds the specified component to the end of this list.
     *
     * @param element the component to be added
     * @see Vector#addElement(Object)
     */
    public void addElement(E element) {
        all.add(element);
        if (null != predicate) {
            if (predicate.test(element)) {
                int index = delegate.size();
                delegate.addElement(element);
                fireIntervalAdded(this, index, index);
            }
        } else {
            int index = delegate.size();
            delegate.addElement(element);
            fireIntervalAdded(this, index, index);
        }
    }


    /**
     * Returns a string that displays and identifies this
     * object's properties.
     *
     * @return a String representation of this object
     */
    public String toString() {
        return delegate.toString();
    }


    /* The remaining methods are included for compatibility with the
     * Java 2 platform Vector class.
     */

    /**
     * Returns an array containing all of the elements in this list in the
     * correct order.
     *
     * @return an array containing the elements of the list
     * @see Vector#toArray()
     */
    public Object[] toArray() {
        Object[] rv = new Object[delegate.size()];
        delegate.copyInto(rv);
        return rv;
    }

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return
     * @return the element at the specified position in this list
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *                                        ({@code index &lt; 0 || index &gt;= size()})
     */
    public E get(int index) {
        return delegate.elementAt(index);
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns (unless it throws an exception).
     */
    public void clear() {
        int index1 = delegate.size() - 1;
        delegate.removeAllElements();
        if (index1 >= 0) {
            fireIntervalRemoved(this, 0, index1);
        }
        all.clear();
    }
}
