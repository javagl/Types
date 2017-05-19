package de.javagl.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test cases for the {@link TypeParser} class
 */
@RunWith(JUnit4.class)
public class TestTypeParser
{
    /**
     * Whether debug information should be printed
     */
    private static boolean DEBUG = false;
    
    /**
     * Test for a basic class name
     */
    @Test
    public void testBasic()
    {
        test(Integer.class);
    }
    
    /**
     * Test for a primitive type name
     */
    @Test
    public void testPrimitive()
    {
        test(int.class);
    }
    
    /**
     * Test for a primitive type array
     */
    @Test
    public void testPrimitiveArray()
    {
        test(int[].class);
    }
    
    /**
     * Test for a generic array type
     */
    @Test
    public void testGenericArray()
    {
        Type t0 = Types.create(List.class).withType(Number.class).build();
        Type arrayType = Types.createGenericArrayType(t0);
        test(arrayType);
    }
    
    /**
     * Test for a basic parameterized type
     */
    @Test
    public void testParamterized()
    {
        test(Types.create(List.class).withType(Number.class).build());
    }

    /**
     * Test for a basic parameterized type with wildcard
     */
    @Test
    public void testParamterizedWithObject()
    {
        test(Types.create(List.class).withSubtypeOf(Object.class).build());
    }

    /**
     * Test for a parameterized type with multiple type parameters
     * which are upper/lower bounded wildcards
     */
    @Test
    public void testParamterizedWithComplexTypeParameters()
    {
        test(Types.create(Map.class)
            .withSubtypeOf(Number.class)
            .withSupertypeOf(Comparable.class)
            .build());
    }

    /**
     * Test for parameterized types whose type parameters are complex 
     * parameterized types
     */
    @Test
    public void testParamterizedWithNestedTypeParameters()
    {
        Type inner = 
            Types.create(Map.class)
            .withSubtypeOf(Number.class)
            .withSupertypeOf(Number.class)
            .build();
        Type outer = 
            Types.create(Map.class)
            .withSubtypeOf(inner)
            .withSupertypeOf(inner)
            .build();
        test(outer);
    }


    /**
     * Test for a parameterized type that has a type parameter that
     * is a wildcard type with a bound that is a type variable
     */
    @Test
    public void testParameterizedTypeWithWildcardWithTypeVariableBound()
    {
        Type typeVariable = Types.createTypeVariable("E");
        Type wildcardType = Types.createWildcardType(
            null,  new Type[]{typeVariable});
        Type parameterizedType = 
            Types.createParameterizedType(List.class,  null, wildcardType);
        test(parameterizedType);
    }
    
    
    /**
     * Test for the on-demand import string handling
     * @throws ClassNotFoundException If a class is not found
     */
    @Test
    public void testOnDemandImportStringHandling() throws ClassNotFoundException
    {
        TypeParser typeParser = TypeParsers.create();
        typeParser.addImport("java.util.*");

        Type t0 = typeParser.parse("List");
        assertEquals(t0, java.util.List.class);
    }
    
    /**
     * Test for the named import string handling
     * @throws ClassNotFoundException If a class is not found
     */
    @Test
    public void testNamedImportStringHandling() throws ClassNotFoundException
    {
        TypeParser typeParser = TypeParsers.create();
        typeParser.addImport("java.util.List");
        typeParser.addImport("java.   util  .   Collection ");

        Type t0 = typeParser.parse("List");
        assertEquals(t0, java.util.List.class);
        
        Type t1 = typeParser.parse("Collection");
        assertEquals(t1, java.util.Collection.class);
        
    }
    
    /**
     * Test for import string priorities
     * @throws ClassNotFoundException If a class is not found
     */
    @Test
    public void testImportStringPriorities() throws ClassNotFoundException
    {
        TypeParser typeParser0 = TypeParsers.create();
        typeParser0.addImport("java.awt.List");
        typeParser0.addImport("java.util.*");

        Type t0 = typeParser0.parse("List");
        assertEquals(t0, java.awt.List.class);
        
        TypeParser typeParser1 = TypeParsers.create();
        typeParser1.addImport("java.awt.*");
        typeParser1.addImport("java.util.List");

        Type t1 = typeParser1.parse("List");
        assertEquals(t1, java.util.List.class);
        
    }
    
    /**
     * Test for the import string validity tests
     * @throws ClassNotFoundException If a class is not found
     */
    @Test
    public void testImportStringHandling() throws ClassNotFoundException
    {
        TypeParser typeParser = TypeParsers.create();
        typeParser.addImport("java.util.List");
        try
        {
            typeParser.addImport("java.awt.List");
            fail("Expected exception");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
    
    /**
     * Test for the import string ambiguity check
     * @throws ClassNotFoundException If a class is not found
     */
    @Test
    public void testImportStringAmbiguity() throws ClassNotFoundException
    {
        TypeParser typeParser = TypeParsers.create();
        typeParser.addImport("java.util.*");
        typeParser.addImport("java.awt.*");
        try
        {
            typeParser.parse("List");
            fail("Expected exception");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
    
    
    /**
     * Test whether redundant import strings work
     * @throws ClassNotFoundException If a class is not found
     */
    @Test
    public void testImportRedundancy() throws ClassNotFoundException
    {
        TypeParser typeParser = TypeParsers.create();
        typeParser.addImport("java.util.*");
        typeParser.addImport("java.util.*");
        Type t1 = typeParser.parse("List");
        assertEquals(t1, java.util.List.class);
    }
    
    

    /**
     * Performs a single parsing test for the given type
     * 
     * @param t0 The type
     * @return Whether the test passed
     */
    static boolean test(Type t0)
    {
        String s0 = Types.stringFor(t0);
        TypeParser typeParser = TypeParsers.create();
        typeParser.addTypeVariableName("E");
        Type t1 = null;
        try
        {
            t1 = typeParser.parse(s0);
        } 
        catch (ClassNotFoundException e)
        {
            fail(e.getMessage());
        }
        
        String message = "";
        message += "Input         " + s0 + "\n";
        message += "should be     " + t0 + "\n";
        message += "was parsed to " + t1 + "\n";
        
        boolean passed = TypesEquivalent.areEquivalent(t0, t1);
        if (!passed || DEBUG)
        {
            PrintStream ps = System.out;
            if (!passed)
            {
                ps = System.err;
            }
            String detailedMessage = "";
            detailedMessage += "Input         " + s0 + "\n";
            detailedMessage += "should be     " + t0 + "    " 
                + "(Detailed: " + Types.debugStringFor(t0) + ")" + "\n";
            detailedMessage += "was parsed to " + t1 + "    " 
                + "(Detailed: " + Types.debugStringFor(t1) + ")" + "\n";
            ps.print(detailedMessage);
        }
        assertTrue(message, passed);
        return passed;
    }

}
