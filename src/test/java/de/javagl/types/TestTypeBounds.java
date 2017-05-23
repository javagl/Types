package de.javagl.types;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@SuppressWarnings("all")
interface TestInterface {}

@SuppressWarnings("all")
interface TestInterfaceExt extends TestInterface, Serializable {}

/**
 * Test cases for the {@link TypeBounds} class.
 */
@RunWith(JUnit4.class)
public class TestTypeBounds
{
    /**
     * Test {@link TypeBounds#mergeUpperBounds(Collection, Iterable)} 
     * with equal classes
     * 
     *    [? extends Integer] 
     *  + [? extends Integer]
     *  = [? extends Integer] 
     */
    @Test
    public void testMergeUpperBoundsWithEqualTypes()
    {
        List<Type> result = 
            TypeBounds.mergeUpperBounds(
                Arrays.asList(Integer.class),
                Arrays.asList(Integer.class));
        
        List<? extends Type> expected =
            Arrays.asList(Integer.class);
            
        assertEqualContents(expected, result); 
    }
    
    
    /**
     * Test {@link TypeBounds#mergeUpperBounds(Collection, Iterable)} 
     * with classes only
     * 
     *    [? extends Number & String] 
     *  + [? extends Integer]
     *  = [? extends Integer & String] 
     */
    @Test
    public void testMergeUpperBoundsWithClassesOnly()
    {
        List<Type> result = 
            TypeBounds.mergeUpperBounds(
                Arrays.asList(Number.class, String.class),
                Arrays.asList(Integer.class));
        
        List<? extends Type> expected =
            Arrays.asList(Integer.class, String.class);
            
        assertEqualContents(expected, result); 
    }

    /**
     * Test {@link TypeBounds#mergeUpperBounds(Collection, Iterable)} 
     * with interfaces only
     * 
     *    [? extends TestInterface & Serializable] 
     *  + [? extends TestInterfaceExt]
     *  = [? extends TestInterfaceExt] 
     */
    @Test
    public void testMergeUpperBoundsWithInterfacesOnly()
    {
        List<Type> result = 
            TypeBounds.mergeUpperBounds(
                Arrays.asList(TestInterface.class, Serializable.class),
                Arrays.asList(TestInterfaceExt.class));
        
        List<? extends Type> expected =
            Arrays.asList(TestInterfaceExt.class);
            
        assertEqualContents(expected, result);
    }
    
    /**
     * Test {@link TypeBounds#mergeUpperBounds(Collection, Iterable)} 
     * with classes and interfaces
     * 
     *    [? extends Comparable & Runnable & Number] 
     *  + [? extends Integer]
     *  = [? extends Integer & Runnable] 
     */
    @Test
    public void testMergeUpperBoundsWithClassAndInterface()
    {
        List<Type> result = 
            TypeBounds.mergeUpperBounds(
                Arrays.asList(Comparable.class, Runnable.class, Number.class),
                Arrays.asList(Integer.class));
        
        List<? extends Type> expected =
            Arrays.asList(Integer.class, Runnable.class);
            
        assertEqualContents(expected, result);
    }
    
    
    
    /**
     * Test {@link TypeBounds#mergeLowerBounds(Collection, Iterable)} 
     * with classes only
     * 
     *    [? super Integer & String] 
     *  + [? super Number]
     *  = [? super Number & String] 
     */
    @Test
    public void testMergeLowerBoundsWithClassesOnly()
    {
        List<Type> result = 
            TypeBounds.mergeLowerBounds(
                Arrays.asList(Integer.class, String.class),
                Arrays.asList(Number.class));
        
        List<? extends Type> expected =
            Arrays.asList(Number.class, String.class);
            
        assertEqualContents(expected, result);
    }

    /**
     * Test {@link TypeBounds#mergeLowerBounds(Collection, Iterable)} 
     * with interfaces only
     * 
     *    [? super TestInterfaceExt] 
     *  + [? super TestInterface & Serializable]
     *  = [? super TestInterface & Serializable] 
     */
    @Test
    public void testMergeLowerBoundsWithInterfacesOnly()
    {
        List<Type> result = 
            TypeBounds.mergeLowerBounds(
                Arrays.asList(TestInterfaceExt.class),
                Arrays.asList(TestInterface.class, Serializable.class));
        
        List<? extends Type> expected =
            Arrays.asList(TestInterface.class, Serializable.class);
            
        assertEqualContents(expected, result);
    }
    
    /**
     * Test {@link TypeBounds#mergeLowerBounds(Collection, Iterable)} 
     * with classes and interfaces
     * 
     *    [? super Integer] 
     *  + [? super Number & Comparable & Runnable]
     *  = [? super Number & Comparable & Runnable] 
     */
    @Test
    public void testMergeLowerBoundsWithClassAndInterface()
    {
        List<Type> result = 
            TypeBounds.mergeLowerBounds(
                Arrays.asList(Integer.class),
                Arrays.asList(Comparable.class, Runnable.class, Number.class));
        
        List<? extends Type> expected =
            Arrays.asList(Comparable.class, Runnable.class, Number.class);
            
        assertEqualContents(expected, result);
    }
    
    
    
    
    /**
     * Assert that the given collections have equal contents. That is,
     * they contain each element an equal number of times, regardless
     * of the order.
     * 
     * @param expected The expected collection
     * @param actual The actual collection
     */
    private static void assertEqualContents(
        Collection<?> expected, Collection <?> actual)
    {
        assertEquals(countElements(expected), countElements(actual));
    }
    
    /**
     * A test for the {@link #countElements(Collection)} method...
     */
    @Test
    public void testCountElements()
    {
        assertEquals(
            countElements(Arrays.asList("A", "B", "C")), 
            countElements(Arrays.asList("C", "B", "A")));

        assertEquals(
            countElements(Arrays.asList("A", "A", "B")), 
            countElements(Arrays.asList("A", "B", "A")));
        
        assertNotEquals(
            countElements(Arrays.asList("A", "A", "B")), 
            countElements(Arrays.asList("A", "B", "B")));
        
        assertNotEquals(
            countElements(Arrays.asList("A", "B", "C")), 
            countElements(Arrays.asList("A", "B", "X")));

        assertNotEquals(
            countElements(Arrays.asList("A", "B", "C")), 
            countElements(Arrays.asList("A", "B", "B")));
    }
    
    
    /**
     * Returns a map that maps each element of the given collection to 
     * the number of times that it occurs in the collection.
     * 
     * @param collection The collection
     * @return The count map
     */
    private static Map<Object, Integer> countElements(Collection<?> collection)
    {
        Map<Object, Integer> counts = new HashMap<Object, Integer>();
        for (Object element : collection)
        {
            Integer count = counts.get(element);
            if (count == null)
            {
                count = 0;
            }
            count++;
            counts.put(element, count);
        }
        return counts;
    }
}
