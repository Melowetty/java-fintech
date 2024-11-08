package ru.melowetty.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

import static java.lang.reflect.Array.newInstance;

public class CustomLinkedList<T> implements List<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T element = current.element;
                current = current.next;
                return element;
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                Objects.requireNonNull(action);

                while (hasNext()) {
                    action.accept(next());
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            array[i] = current.element;
            current = current.next;
        }
        return array;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        if (a.length < size) {
            a = (T1[]) newInstance(a.getClass().getComponentType(), size);
        }

        Node<T> current = head;
        Object[] result = a;
        for (int i = 0; i < size; i++) {
            result[i] = current.element;
            current = current.next;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean add(T t) {
        if (head == null) {
            head = new Node<>(t);
            tail = head;
        } else {
            Node<T> node = new Node<>(t);
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node<T> current = head;
        while (current != null) {
            if (Objects.equals(current.element, o)) {
                unlink(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private void unlink(Node<T> node) {
        Node<T> next = node.next;
        Node<T> prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.element = null;
        size--;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (var elem : c) {
            add(elem);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkPositionIndex(index);
        if (c.isEmpty()) {
            return false;
        }

        Node<T> prevNode = (index == 0) ? null : getNode(index - 1);
        Node<T> nextNode = (index == size) ? null : getNode(index);

        for (T element : c) {
            Node<T> newNode = new Node<>(element);
            if (prevNode == null) {
                head = newNode;
            } else {
                prevNode.next = newNode;
                newNode.prev = prevNode;
            }
            prevNode = newNode;
        }

        if (nextNode == null) {
            tail = prevNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }

        size += c.size();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.next;
            current.element = null;
            current.prev = null;
            current.next = null;
            current = next;
        }
        head = tail = null;
        size = 0;
    }

    @Override
    public T get(int index) {
        checkElementIndex(index);
        return getNode(index).element;
    }

    @Override
    public T set(int index, T element) {
        checkElementIndex(index);
        Node<T> node = getNode(index);
        T oldVal = node.element;
        node.element = element;
        return oldVal;
    }

    @Override
    public void add(int index, T element) {
        checkPositionIndex(index);
        if (index == size) {
            add(element);
        } else {
            Node<T> successor = getNode(index);
            Node<T> predecessor = successor.prev;
            Node<T> newNode = new Node<>(element);
            newNode.next = successor;
            newNode.prev = predecessor;
            successor.prev = newNode;

            if (predecessor == null) {
                head = newNode;
            } else {
                predecessor.next = newNode;
            }
            size++;
        }
    }

    @Override
    public T remove(int index) {
        checkElementIndex(index);
        Node<T> node = getNode(index);
        T elem = getNode(index).element;
        unlink(node);

        return elem;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        for (Node<T> x = head; x != null; x = x.next) {
            if (Objects.equals(o, x.element)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        for (Node<T> x = tail; x != null; x = x.prev) {
            if (Objects.equals(o, x.element)) {
                return index;
            }
            index--;
        }
        return -1;
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        checkPositionIndex(index);
        return new ListIterator<T>() {
            private Node<T> lastReturned = null;
            private Node<T> next = (index == size) ? null : getNode(index);
            private int nextIndex = index;

            @Override
            public boolean hasNext() {
                return nextIndex < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                lastReturned = next;
                next = next.next;
                nextIndex++;
                return lastReturned.element;
            }

            @Override
            public boolean hasPrevious() {
                return nextIndex > 0;
            }

            @Override
            public T previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                next = (next == null) ? tail : next.prev;
                lastReturned = next;
                nextIndex--;
                return lastReturned.element;
            }

            @Override
            public int nextIndex() {
                return nextIndex;
            }

            @Override
            public int previousIndex() {
                return nextIndex - 1;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                Node<T> lastNext = lastReturned.next;
                unlink(lastReturned);
                if (next == lastReturned) {
                    next = lastNext;
                } else {
                    nextIndex--;
                }
                lastReturned = null;
            }

            @Override
            public void set(T t) {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                lastReturned.element = t;
            }

            @Override
            public void add(T t) {
                lastReturned = null;
                if (next == null) {
                    add(t);
                } else {
                    Node<T> newNode = new Node<>(t);
                    Node<T> prev = next.prev;
                    newNode.next = next;
                    newNode.prev = prev;
                    next.prev = newNode;
                    if (prev == null) {
                        head = newNode;
                    } else {
                        prev.next = newNode;
                    }
                    size++;
                    nextIndex++;
                }
            }
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        checkPositionIndex(fromIndex);
        checkPositionIndex(toIndex);
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex > toIndex");
        }

        List<T> sublist = new CustomLinkedList<>();
        Node<T> current = getNode(fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            sublist.add(current.element);
            current = current.next;
        }
        return sublist;
    }

    private Node<T> getNode(int index) {
        Node<T> x;
        if (index < (size >> 1)) {
            x = head;
            for (int i = 0; i < index; i++) {
                x = x.next;
            }
        } else {
            x = tail;
            for (int i = size - 1; i > index; i--) {
                x = x.prev;
            }
        }
        return x;
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index)) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index)) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    private static class Node<T> {
        T element;
        Node<T> prev;
        Node<T> next;

        public Node(T element) {
            this.element = element;
        }
    }
}