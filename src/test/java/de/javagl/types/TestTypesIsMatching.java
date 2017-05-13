package de.javagl.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Test for the {@link DefaultTypeAssignabilityTester#isMatchingTypeArgument(
 * Type, Type)} method
 */
@RunWith(JUnit4.class)
public class TestTypesIsMatching
{
    /**
     * Whether debug information should be printed
     */
    private static boolean DEBUG = false;
    
    /**
     * Tests whether the given types are matching type arguments
     * 
     * @param to The type argument of the type to assign to
     * @param from The type argument of the type to assign from
     * @return Whether the type arguments are matching
     */
    static boolean testIsMatching(Type to, Type from)
    {
        if (DEBUG)
        {
            return printMatching(to, from);
        }
        return new DefaultTypeAssignabilityTester()
            .isMatchingTypeArgument(to, from);
    }

    
    /**
     * Print the given types and the result of their matching test
     * 
     * @param to The type argument of the type to assign to
     * @param from The type argument of the type to assign from
     * @return Whether the types are matching
     */
    private static boolean printMatching(Type to, Type from)
    {
        System.out.println("Is              "+Types.debugStringFor(to));
        System.out.println("matching   from "+Types.debugStringFor(from));
        boolean result = new DefaultTypeAssignabilityTester()
            .isMatchingTypeArgument(to, from);
        System.out.println(" -> "+result);
        return result;
    }
    
    //=========================================================================
    // Test matching for wildcard types. 
    // These appear only in parameterized types
    
    
    /**
     * Test matching for cases like 
     * Number = ? super Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToClass_FromWildcardTypeWithLowerBound()
    {
        Type to = Number.class;
        Type from0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type from1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type from2 = Types.createWildcardType(new Type[]{Integer.class}, null);

        assertFalse(testIsMatching(to, from0));
        assertFalse(testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }

    /**
     * Test cases like 
     * Number = ? extends Integer
     */
    @Test
    public void testMatching_ToClass_FromWildcardTypeWithUpperBound()
    {
        Type to = Number.class;
        Type from0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type from1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type from2 = Types.createWildcardType(null, new Type[]{Integer.class});

        assertFalse(testIsMatching(to, from0));
        assertFalse(testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }
    
    
    /**
     * Test matching for cases like 
     * ? super Number = Number
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithLowerBound_FromClass()
    {
        Type to = Types.createWildcardType(new Type[]{Number.class}, null);
        Type from0 = Object.class;
        Type from1 = Number.class;
        Type from2 = Integer.class;

        assertTrue (testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }
    
    /**
     * Test matching for cases like 
     * ? extends Number = Number
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithUpperBound_FromClass()
    {
        Type to = Types.createWildcardType(null, new Type[]{Number.class});
        Type from0 = Object.class;
        Type from1 = Number.class;
        Type from2 = Integer.class;

        assertFalse(testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertTrue (testIsMatching(to, from2));
    }
    
    
    /**
     * Test matching for cases like 
     * ? super List = List<Number>
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithLowerBound_FromParameterizedType()
    {
        Type to = Types.createWildcardType(new Type[]{List.class}, null);

        Type fromArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertTrue (testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }

    /**
     * Test matching for cases like 
     * ? extends List = List<? extends Number>
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithUpperBound_FromParameterizedType()
    {
        Type to = Types.createWildcardType(null, new Type[]{List.class});

        Type fromArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertTrue (testIsMatching(to, from2));
    }

    
    /**
     * Test matching for cases like 
     * ? super Number = ? super Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithLowerBound_FromWildcardTypeWithLowerBound()
    {
        Type to = Types.createWildcardType(new Type[]{Number.class}, null);
        
        Type from0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type from1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type from2 = Types.createWildcardType(new Type[]{Integer.class}, null);

        assertTrue (testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }
    
    /**
     * Test matching for cases like 
     * ? super Number = ? extends Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithLowerBound_FromWildcardTypeWithUpperBound()
    {
        Type to = Types.createWildcardType(new Type[]{Number.class}, null);
        
        Type from0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type from1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type from2 = Types.createWildcardType(null, new Type[]{Integer.class});

        assertFalse(testIsMatching(to, from0));
        assertFalse(testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }

    /**
     * Test matching for cases like 
     * ? extends Number = ? super Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithUpperBound_FromWildcardTypeWithLowerBound()
    {
        Type to = Types.createWildcardType(null, new Type[]{Number.class});
        
        Type from0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type from1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type from2 = Types.createWildcardType(new Type[]{Integer.class}, null);

        assertFalse(testIsMatching(to, from0));
        assertFalse(testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }
    
    /**
     * Test matching for cases like 
     * ? extends Number = ? extends Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithUpperBound_FromWildcardTypeWithUpperBound()
    {
        Type to = Types.createWildcardType(null, new Type[]{Number.class});
        
        Type from0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type from1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type from2 = Types.createWildcardType(null, new Type[]{Integer.class});

        assertFalse(testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertTrue (testIsMatching(to, from2));
    }
    
    
    /**
     * Test matching for cases like 
     * ? super Number = T extends Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithLowerBound_FromTypeVariable()
    {
        Type to = Types.createWildcardType(new Type[]{Number.class}, null);
        
        Type from0 = Types.createTypeVariable("T", new Type[]{Object.class});
        Type from1 = Types.createTypeVariable("T", new Type[]{Number.class});
        Type from2 = Types.createTypeVariable("T", new Type[]{Integer.class});
        
        assertFalse(testIsMatching(to, from0));
        assertFalse(testIsMatching(to, from1));
        assertFalse(testIsMatching(to, from2));
    }
    
    /**
     * Test matching for cases like 
     * ? extends Number = T extends Integer
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithUpperBound_FromTypeVariable()
    {
        Type to = Types.createWildcardType(null, new Type[]{Number.class});
        
        Type from0 = Types.createTypeVariable("T", new Type[]{Object.class});
        Type from1 = Types.createTypeVariable("T", new Type[]{Number.class});
        Type from2 = Types.createTypeVariable("T", new Type[]{Integer.class});
        
        assertFalse(testIsMatching(to, from0));
        assertTrue (testIsMatching(to, from1));
        assertTrue (testIsMatching(to, from2));
    }
    
    /**
     * Test matching for cases like 
     * ? super List<Number>[] = List<Number>[]
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithLowerBound_FromGenericArrayType()
    {
        Type collectionWithObject = Types.createParameterizedType(Collection.class, null, Object.class);
        Type listWithNumber = Types.createParameterizedType(List.class, null, Number.class);
        Type arrayListWithInteger = Types.createParameterizedType(ArrayList.class, null, Integer.class);

        Type arrayOfCollectionWithObject = Types.createGenericArrayType(collectionWithObject);
        Type arrayOfListWithNumber = Types.createGenericArrayType(listWithNumber);
        Type arrayOfArrayListWithInteger = Types.createGenericArrayType(arrayListWithInteger);

        Type to0 = Types.createWildcardType(new Type[]{arrayOfCollectionWithObject}, null);
        Type to1 = Types.createWildcardType(new Type[]{arrayOfListWithNumber}, null);
        Type to2 = Types.createWildcardType(new Type[]{arrayOfArrayListWithInteger}, null);
        
        Type from0 = arrayOfCollectionWithObject;
        Type from1 = arrayOfListWithNumber;
        Type from2 = arrayOfArrayListWithInteger;
        
        assertTrue (testIsMatching(to0, from0));
        assertFalse(testIsMatching(to1, from0));
        assertFalse(testIsMatching(to2, from0));

        assertFalse(testIsMatching(to0, from1));
        assertTrue (testIsMatching(to1, from1));
        assertFalse(testIsMatching(to2, from1));

        assertFalse(testIsMatching(to0, from2));
        assertFalse(testIsMatching(to1, from2));
        assertTrue (testIsMatching(to2, from2));
    }
    
    
    /**
     * Test matching for cases like 
     * ? extends List<Number>[] = List<Number>[]
     * where the given types are type arguments
     */
    @Test
    public void testMatching_ToWildcardTypeWithUpperBound_FromGenericArrayType()
    {
        Type collectionWithObject = Types.createParameterizedType(Collection.class, null, Object.class);
        Type listWithNumber = Types.createParameterizedType(List.class, null, Number.class);
        Type arrayListWithInteger = Types.createParameterizedType(ArrayList.class, null, Integer.class);

        Type arrayOfCollectionWithObject = Types.createGenericArrayType(collectionWithObject);
        Type arrayOfListWithNumber = Types.createGenericArrayType(listWithNumber);
        Type arrayOfArrayListWithInteger = Types.createGenericArrayType(arrayListWithInteger);

        Type to0 = Types.createWildcardType(null, new Type[]{arrayOfCollectionWithObject});
        Type to1 = Types.createWildcardType(null, new Type[]{arrayOfListWithNumber});
        Type to2 = Types.createWildcardType(null, new Type[]{arrayOfArrayListWithInteger});
        
        Type from0 = arrayOfCollectionWithObject;
        Type from1 = arrayOfListWithNumber;
        Type from2 = arrayOfArrayListWithInteger;
        
        assertTrue (testIsMatching(to0, from0));
        assertFalse(testIsMatching(to1, from0));
        assertFalse(testIsMatching(to2, from0));

        assertFalse(testIsMatching(to0, from1));
        assertTrue (testIsMatching(to1, from1));
        assertFalse(testIsMatching(to2, from1));

        assertFalse(testIsMatching(to0, from2));
        assertFalse(testIsMatching(to1, from2));
        assertTrue (testIsMatching(to2, from2));
    }
    

}
