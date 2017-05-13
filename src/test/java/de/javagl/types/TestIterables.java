package de.javagl.types;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test cases for the {@link Iterables} class.
 */
@RunWith(JUnit4.class)
public class TestIterables
{
    /**
     * Basic test for the {@link Iterables#cartesianProduct(Iterable)} method
     */
    @Test
    public void testBasicCartesianProduct()
    {
        List<List<String>> domain = Arrays.asList(
            Arrays.asList("A", "B"),
            Arrays.asList("1", "2"),
            Arrays.asList("X", "Y"));
        
        Iterable<List<String>> iterable = Iterables.cartesianProduct(domain);
        List<List<String>> result = new ArrayList<List<String>>();
        for (List<String> element : iterable)
        {
            result.add(element);
        }
        assertTrue(result.contains(Arrays.asList("A", "1", "X")));
        assertTrue(result.contains(Arrays.asList("A", "1", "Y")));
        assertTrue(result.contains(Arrays.asList("A", "2", "X")));
        assertTrue(result.contains(Arrays.asList("A", "2", "Y")));
        assertTrue(result.contains(Arrays.asList("B", "1", "X")));
        assertTrue(result.contains(Arrays.asList("B", "1", "Y")));
        assertTrue(result.contains(Arrays.asList("B", "2", "X")));
        assertTrue(result.contains(Arrays.asList("B", "2", "Y")));
    }
    
    /**
     * Test for the {@link Iterables#cartesianProduct(Iterable)} method
     * with a domain that is empty in one dimension
     */
    @Test
    public void testEmptyCartesianProduct()
    {
        List<List<String>> domain = Arrays.asList(
            Arrays.asList("A", "B"),
            Collections.<String>emptyList(),
            Arrays.asList("X", "Y"));
        
        Iterable<List<String>> iterable = Iterables.cartesianProduct(domain);
        Iterator<List<String>> iterator = iterable.iterator();
        assertFalse(iterator.hasNext());
    }
    

}
