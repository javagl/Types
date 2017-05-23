package de.javagl.types;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for the {@link Supertypes} class
 */
@RunWith(JUnit4.class)
public class TestSupertypes
{
    /**
     * Whether debug information should be printed
     */
    private static boolean DEBUG = false;
    
    /**
     * Test the computation of raw supertypes from parameterized types
     * that are concrete classes
     */
    @Test
    public void testRawSupertypesOfParameterizedClass()
    {
        ParameterizedType t = 
            Types.createParameterizedType(ArrayList.class, null,
                String.class);
        
        Set<Type> actual = 
            Supertypes.computeRawSupertypes(t);
        Set<Type> expected = new LinkedHashSet<Type>(Arrays.asList(
            ArrayList.class, 
            AbstractList.class, 
            AbstractCollection.class, 
            Object.class, 
            Collection.class,
            Iterable.class, 
            List.class,
            RandomAccess.class, 
            Cloneable.class,
            Serializable.class));

        print("rawSupertypes", t, actual);
        
        assertEquals(expected, actual);
    }
    
    

    /**
     * Test the computation of raw supertypes from parameterized types
     * that are interfaces
     */
    @Test
    public void testRawSupertypesOfParameterizedInterface()
    {
        ParameterizedType t = 
            Types.createParameterizedType(List.class, null, String.class);
        
        Set<Type> actual = 
            Supertypes.computeRawSupertypes(t);
        Set<Type> expected = new LinkedHashSet<Type>(Arrays.asList(
            List.class, 
            Collection.class, 
            Iterable.class));

        print("rawSupertypes", t, actual);
        
        assertEquals(expected, actual);
    }
    
    /**
     * Test the computation of raw supertypes from raw types (classes)
     */
    @Test
    public void testRawSupertypesOfClass()
    {
        Class<?> t = ArrayList.class; 
        
        Set<Type> actual = 
            Supertypes.computeRawSupertypes(t);
        Set<Type> expected = new LinkedHashSet<Type>(Arrays.asList(
            ArrayList.class, 
            AbstractList.class, 
            AbstractCollection.class, 
            Object.class, 
            Collection.class, 
            Iterable.class, 
            List.class,
            RandomAccess.class, 
            Cloneable.class, 
            Serializable.class));

        print("rawSupertypes", t, actual);
        
        assertEquals(expected, actual);
    }
    

    /**
     * Test the computation of raw supertypes from raw types (interfaces)
     */
    @Test
    public void testRawSupertypesOfInterface()
    {
        Class<?> t = List.class; 
        
        Set<Type> actual = 
            Supertypes.computeRawSupertypes(t);
        Set<Type> expected = new LinkedHashSet<Type>(Arrays.asList(
            List.class, 
            Collection.class, 
            Iterable.class));

        print("rawSupertypes", t, actual);
        
        assertEquals(expected, actual);
    }

    /**
     * Test for the supertypes computation
     */
    @Test
    public void testSupertypes()
    {
        Type type = Types.create(Set.class, Integer.class); 
        
        Set<Type> actual = Supertypes.computeSupertypes(type);
        Set<Type> expected = new LinkedHashSet<Type>(Arrays.asList(
            Types.parse("java.util.Set"),
            Types.parse("java.util.Collection"),
            Types.parse("java.lang.Iterable"),
            Types.parse("java.util.Set<java.lang.Integer>"),
            Types.parse("java.util.Set<? extends java.lang.Number>"),
            Types.parse("java.util.Set<? extends java.io.Serializable>"),
            Types.parse("java.util.Set<? extends java.lang.Comparable>"),
            Types.parse("java.util.Set<?>"),
            Types.parse("java.util.Collection<java.lang.Integer>"),
            Types.parse("java.util.Collection<? extends java.lang.Number>"),
            Types.parse("java.util.Collection<? extends java.io.Serializable>"),
            Types.parse("java.util.Collection<? extends java.lang.Comparable>"),
            Types.parse("java.util.Collection<?>"),
            Types.parse("java.lang.Iterable<java.lang.Integer>"),
            Types.parse("java.lang.Iterable<? extends java.lang.Number>"),
            Types.parse("java.lang.Iterable<? extends java.io.Serializable>"),
            Types.parse("java.lang.Iterable<? extends java.lang.Comparable>"),
            Types.parse("java.lang.Iterable<?>")
        ));
        
        print("supertypes", type, actual);
        
        assertEquals(expected, actual);
        
    }
 
    /**
     * Prints debug information
     * @param message message
     * @param t t
     * @param result result
     */
    private static void print(
        String message, Type t, Iterable<? extends Type> result)
    {
        if (DEBUG)
        {
            System.out.println(message + ", input " + Types.debugStringFor(t));
            for (Type type : result)
            {
                System.out.println("    " + Types.debugStringFor(type));
            }
        }
    }
    
    
}