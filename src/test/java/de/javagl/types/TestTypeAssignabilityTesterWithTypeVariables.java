package de.javagl.types;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test for the different {@link TypeAssignabilityTesters}: The one that
 * assumes free type variables, and the one that doesn't.
 */
@RunWith(JUnit4.class)
public class TestTypeAssignabilityTesterWithTypeVariables
{
    /**
     * Whether debug information should be printed
     */
    private static boolean DEBUG = false;

    /**
     * Tests whether the given types are assignable with bound type variables
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @return Whether the types are assignable
     */
    private static boolean testIsAssignableBound(Type to, Type from)
    {
        if (DEBUG)
        {
            return printAssignable(to, from, "with bound type variables");
        }
        TypeAssignabilityTester t = 
            TypeAssignabilityTesters.create();
        return t.isAssignable(to, from);
    }

    /**
     * Tests whether the given types are assignable with free type variables
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @return Whether the types are assignable
     */
    private static boolean testIsAssignableFree(Type to, Type from)
    {
        if (DEBUG)
        {
            return printAssignable(to, from, "with free type variables");
        }
        TypeAssignabilityTester t = 
            TypeAssignabilityTesters.createForFreeTypeVariables();
        return t.isAssignable(to, from);
    }
    
    /**
     * Print the given types and the result of their assignability test
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @param text Additional info text to be printed
     * @return Whether the types are assignable
     */
    private static boolean printAssignable(Type to, Type from, String text)
    {
        System.out.println("Is              "+Types.debugStringFor(to));
        System.out.println("assignable from "+Types.debugStringFor(from));
        System.out.println(text);
        boolean result = Types.isAssignable(to, from);
        System.out.println(" -> "+result);
        return result;
    }
    
    /**
     * Test cases like 
     * List<T> = List<Integer>
     * 
     * This is only assignable when assuming free type variables 
     */
    @Test
    public void testParameterized_ToTypeVariable_FromClass()
    {
        Type toComponent0 = Types.createTypeVariable("T");
        Type to = Types.createParameterizedType(List.class, null, toComponent0);
        
        Type fromComponent0 = Integer.class;
        Type from = Types.createParameterizedType(List.class, null, fromComponent0);

        assertFalse(testIsAssignableBound(to, from));
        assertTrue (testIsAssignableFree (to, from));
    }
    
    /**
     * Test cases like 
     * List<Integer> = List<T>
     * 
     * This is only assignable when assuming free type variables 
     */
    @Test
    public void testParameterized_ToClass_FromTypeVariable()
    {
        Type toComponent0 = Integer.class;
        Type to = Types.createParameterizedType(List.class, null, toComponent0);

        Type fromComponent0 = Types.createTypeVariable("T");
        Type from = Types.createParameterizedType(List.class, null, fromComponent0);
        
        assertFalse(testIsAssignableBound(to, from));
        assertTrue (testIsAssignableFree (to, from));
    }
    
    /**
     * Test cases like 
     * T = Integer
     * 
     * This is only assignable when assuming free type variables 
     */
    @Test
    public void test_ToTypeVariable_FromClass()
    {
        Type to = Types.createTypeVariable("T");
        Type from = Integer.class;

        assertFalse(testIsAssignableBound(to, from));
        assertTrue (testIsAssignableFree (to, from));
    }
    
    /**
     * Test cases like 
     * Integer = T
     * 
     * This is only assignable when assuming free type variables 
     */
    @Test
    public void test_ToClass_FromTypeVariable()
    {
        Type to = Integer.class;
        Type from = Types.createTypeVariable("T");

        assertFalse(testIsAssignableBound(to, from));
        assertTrue (testIsAssignableFree (to, from));
    }
    
    
}
