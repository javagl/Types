package de.javagl.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for the {@link PrimitiveTypes#isPrimitiveAssignableWithAutoboxing(
 * java.lang.reflect.Type, java.lang.reflect.Type)} method
 */
@RunWith(JUnit4.class)
public class TestPrimitiveTypesIsAssignableWithAutoboxing
{
    /**
     * The primitive types, except for 'char', ordered
     * according to their 'size' 
     */
    private static final List<Class<?>> primitiveTypes = 
        Arrays.<Class<?>>asList(
            double.class, float.class, long.class,
            int.class, short.class, byte.class);

    /**
     * The boxed primitive types, except for 'Character', ordered
     * according to their 'size' 
     */
    private static final List<Class<?>> boxedTypes = 
        Arrays.<Class<?>>asList(
            Double.class, Float.class, Long.class,
            Integer.class, Short.class, Byte.class);
    
    /**
     * The number of types in the above lists
     */
    private static final int numTypes = 6;
    
    /**
     * Test the basic assignabilities for primitive and boxed types,
     * excluding 'char'
     */
    @Test
    public void testIsPrimitiveAssignableWithAutoboxing()
    {
        for (int i=0; i<numTypes; i++)
        {
            Class<?> pi = primitiveTypes.get(i);
            Class<?> bi = boxedTypes.get(i);

            // Every type is assignable from itself
            assertTrue(pi+" <= "+pi, test(pi, pi));
            assertTrue(bi+" <= "+bi, test(bi, bi));
            
            // Every type is assignable from its boxed type, and vice versa
            assertTrue(pi+" <= "+bi, test(pi, bi));
            assertTrue(bi+" <= "+pi, test(bi, pi));
            
            for (int j=i+1; j<numTypes; j++)
            {
                Class<?> pj = primitiveTypes.get(j);
                Class<?> bj = boxedTypes.get(j);
                
                // Every type is assignable from a smaller type
                assertTrue(pi+" <= "+pj, test(pi, pj));

                // No type is assignable from a larger type
                assertFalse(pj+" </= "+pi, test(pj, pi));
                
                // No type is assignable from different-sized boxed type
                assertFalse(pi+" </= "+bj, test(pi, bj));

                // No boxed type is assignable from different-sized type
                assertFalse(bi+" </= "+pj, test(bi, pj));
            }
        }
    }
    
    /**
     * Test the basic assignabilities for primitive and boxed types,
     * invloving 'char'
     */
    @Test
    public void testIsPrimitiveAssignableWithAutoboxing_ForChar()
    {
        int s = primitiveTypes.indexOf(short.class);

        Class<?> pc = char.class;
        for (int i=0; i<s; i++)
        {
            Class<?> pi = primitiveTypes.get(i);

            // Every type >short is assignable from char
            assertTrue(pi+" <= "+pc, test(pi, pc));
        }

        for (int i=s; i<numTypes; i++)
        {
            Class<?> pi = primitiveTypes.get(i);

            // No type <=short is assignable from char
            assertFalse(pi+" </= "+pc, test(pi, pc));
        }
    }
    
    /**
     * The actual method to test
     * 
     * @param to The type to assign to 
     * @param from The type to assign from
     * @return Whether the types are assignable
     */
    private static boolean test(Type to, Type from)
    {
        return PrimitiveTypes.isPrimitiveAssignableWithAutoboxing(to, from);
    }
    
    
}