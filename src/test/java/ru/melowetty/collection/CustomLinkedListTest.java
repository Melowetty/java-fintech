package ru.melowetty.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CustomLinkedListTest {
    private CustomLinkedList<Integer> list;

    @BeforeEach
    public void setUp() {
        list = new CustomLinkedList<>();
    }

    @Test
    public void testAddAndSize() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(3, list.size(), "Size should be 3 after adding three elements.");
    }

    @Test
    public void testGet() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertEquals(1, list.get(0), "Element at index 0 should be 1.");
        assertEquals(2, list.get(1), "Element at index 1 should be 2.");
        assertEquals(3, list.get(2), "Element at index 2 should be 3.");
    }

    @Test
    public void testContains() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertTrue(list.contains(1), "List should contain element 1.");
        assertTrue(list.contains(2), "List should contain element 2.");
        assertTrue(list.contains(3), "List should contain element 3.");
        assertFalse(list.contains(4), "List should not contain element 4.");
    }

    @Test
    public void testAddAll() {
        List<Integer> elements = Arrays.asList(4, 5, 6);
        list.addAll(elements);

        assertEquals(3, list.size(), "Size should be 3 after adding three elements from collection.");
        assertEquals(4, list.get(0), "Element at index 0 should be 4.");
        assertEquals(5, list.get(1), "Element at index 1 should be 5.");
        assertEquals(6, list.get(2), "Element at index 2 should be 6.");
    }

    @Test
    public void testRemove() {
        list.add(10);
        list.add(20);
        list.add(30);

        Integer removedElement = list.remove(1);

        assertEquals(20, removedElement, "Removed element should be 20.");
        assertEquals(2, list.size(), "Size should be 2 after removal.");
        assertEquals(10, list.get(0), "Element at index 0 should be 10.");
        assertEquals(30, list.get(1), "Element at index 1 should be 30.");
    }

    @Test
    public void testStreamToCustomLinkedList() {
        Stream<Integer> stream = Stream.of(7, 8, 9);
        CustomLinkedList<Integer> result = stream
                .reduce(
                        new CustomLinkedList<>(),
                        (list, element) -> {
                            list.add(element);
                            return list;
                        },
                        (list1, list2) -> {
                            list1.addAll(list2);
                            return list1;
                        }
                );

        assertEquals(3, result.size(), "Size should be 3 after adding three elements from stream.");
        assertEquals(7, result.get(0), "Element at index 0 should be 7.");
        assertEquals(8, result.get(1), "Element at index 1 should be 8.");
        assertEquals(9, result.get(2), "Element at index 2 should be 9.");
    }
}
