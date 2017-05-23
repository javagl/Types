package de.javagl.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test cases for the {@link Types#isAssignable(Type, Type)} method
 * 
 * <code><pre>
 * Test case matrix:
 * 
 * C = Class
 * P = ParameterizedType
 * W = WildcardType
 * T = TypeVariable
 * G = GenericArrayType
 * 
 *   C P W T G < From
 * C + + * + +
 * P + + * + +
 * W * * * * *
 * T + + * + +
 * G + + *?? + 
 * ^
 * To  
 * 
 * * : Only appears in ParameterizedType <- ParameterizedType,
 *     and thus has not to be tested for assignability, but
 *     whether they are "matching" as type parameters
 * </pre></code>
 * 
 * TODO Omit the explicit "matching" tests, because they 
 * should already be covered by the (extensive) 
 * ParameterizedType <- ParameterizedType tests?
 */
@RunWith(JUnit4.class)
public class TestTypesIsAssignable
{
    /**
     * Whether debug information should be printed
     */
    private static boolean DEBUG = false;

    /**
     * Tests whether the given types are assignable
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @return Whether the types are assignable
     */
    private static boolean testIsAssignable(Type to, Type from)
    {
        if (DEBUG)
        {
            return printAssignable(to, from);
        }
        return Types.isAssignable(to, from);
    }

    /**
     * Print the given types and the result of their assignability test
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @return Whether the types are assignable
     */
    private static boolean printAssignable(Type to, Type from)
    {
        System.out.println("Is              "+Types.debugStringFor(to));
        System.out.println("assignable from "+Types.debugStringFor(from));
        boolean result = Types.isAssignable(to, from);
        System.out.println(" -> "+result);
        return result;
    }
    


    
    
    //=========================================================================
    // Test assignments to class
    
    /**
     * Test cases like 
     * Number = Integer
     */
    @Test
    public void test_ToClass_FromClass()
    {
        Type to = Number.class;
        Type from0 = Object.class;
        Type from1 = Number.class;
        Type from2 = Integer.class;

        assertFalse(testIsAssignable(to, from0));
        assertTrue (testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
    }
    
    /**
     * Test cases like 
     * List = ArrayList<? extends Integer>
     */
    @Test
    public void test_ToClass_FromParameterizedType()
    {
        Type to = List.class;
        Type from0 = Types.createParameterizedType(Collection.class, null, Number.class);
        Type from1 = Types.createParameterizedType(List.class, null, Number.class);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, Number.class);

        assertFalse(testIsAssignable(to, from0));
        assertTrue (testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
    }
    

    /**
     * Test cases like 
     * Comparable = T extends Number & Comparable
     */
    @Test
    public void test_ToClass_FromTypeVariable()
    {
        Type to = Comparable.class;
        Type from0 = Types.createTypeVariable("T", new Type[]{Object.class});
        Type from1 = Types.createTypeVariable("T", new Type[]{Number.class, Comparable.class});
        Type from2 = Types.createTypeVariable("T", new Type[]{Integer.class});

        assertFalse(testIsAssignable(to, from0));
        assertTrue (testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
    }

    /**
     * Test cases like 
     * Object = (T extends Integer)[]
     */
    @Test
    public void test_ToClass_FromGenericArrayType()
    {
        Type to0 = Object.class;
        Type to1 = Number.class;

        Type fromComponent0 = Types.createTypeVariable("T", new Type[]{Object.class});
        Type fromComponent1 = Types.createTypeVariable("T", new Type[]{Number.class, Comparable.class});
        Type fromComponent2 = Types.createTypeVariable("T", new Type[]{Integer.class});
        Type from0 = Types.createGenericArrayType(fromComponent0);
        Type from1 = Types.createGenericArrayType(fromComponent1);
        Type from2 = Types.createGenericArrayType(fromComponent2);

        assertTrue (testIsAssignable(to0, from0));
        assertTrue (testIsAssignable(to0, from1));
        assertTrue (testIsAssignable(to0, from2));

        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to1, from2));
    }

    
    //=========================================================================
    // Test assignments to parameterized types
    
    /**
     * Test cases like 
     * List<Number> = ArrayList
     */
    @Test
    public void test_ToParameterizedType_FromClass()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);
        Type from0 = Collection.class;
        Type from1 = List.class;
        Type from2 = ArrayList.class;

        assertFalse(testIsAssignable(to, from0));
        assertTrue (testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
    }

    
    
    //=========================================================================
    // Test the sub-cases of assignments between parameterized types
    
    /**
     * Test cases like 
     * List<Number> = ArrayList<Integer>
     */
    @Test
    public void testParameterized_ToClass_FromClass()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);
        Type from0 = Types.createParameterizedType(Collection.class, null, Number.class);
        Type from1 = Types.createParameterizedType(List.class, null, Object.class);
        Type from2 = Types.createParameterizedType(List.class, null, Number.class);
        Type from3 = Types.createParameterizedType(List.class, null, Integer.class);
        Type from4 = Types.createParameterizedType(ArrayList.class, null, Object.class);
        Type from5 = Types.createParameterizedType(ArrayList.class, null, Number.class);
        Type from6 = Types.createParameterizedType(ArrayList.class, null, Integer.class);

        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
        assertFalse(testIsAssignable(to, from3));
        assertFalse(testIsAssignable(to, from4));
        assertTrue (testIsAssignable(to, from5));
        assertFalse(testIsAssignable(to, from6));
    }

    /**
     * Test cases like 
     * List<Number> = ArrayList<? super Integer>
     */
    @Test
    public void testParameterized_ToClass_FromWildcardTypeWithLowerBound()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);
        
        Type fromArgument0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type fromArgument1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type fromArgument2 = Types.createWildcardType(new Type[]{Integer.class}, null);
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
    }

    /**
     * Test cases like 
     * List<Number> = ArrayList<? extends Integer>
     */
    @Test
    public void testParameterized_ToClass_FromWildcardTypeWithUpperBound()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);
        
        Type fromArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
    }

    /**
     * Test cases like 
     * List<? super Number> = ArrayList<Integer>
     */
    @Test
    public void testParameterized_ToWildcardTypeWithLowerBound_FromClass()
    {
        Type toArgument0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type toArgument1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type toArgument2 = Types.createWildcardType(new Type[]{Integer.class}, null);
        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);
        
        Type from = Types.createParameterizedType(List.class, null, Number.class);

        assertFalse(testIsAssignable(to0, from));
        assertTrue (testIsAssignable(to1, from));
        assertTrue (testIsAssignable(to2, from));
    }

    /**
     * Test cases like 
     * List<? extends Number> = ArrayList<Integer>
     */
    @Test
    public void testParameterized_ToWildcardTypeWithUpperBound_FromClass()
    {
        Type toArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type toArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type toArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);
        
        Type from = Types.createParameterizedType(List.class, null, Number.class);
        
        assertTrue (testIsAssignable(to0, from));
        assertTrue (testIsAssignable(to1, from));
        assertFalse(testIsAssignable(to2, from));
    }

    
    /**
     * Test cases like 
     * List<? super Number> = ArrayList<? super Integer>
     */
    @Test
    public void testParameterized_ToWildcardTypeWithLowerBound_FromWildcardTypeWithLowerBound()
    {
        Type toArgument0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type toArgument1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type toArgument2 = Types.createWildcardType(new Type[]{Integer.class}, null);

        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);
        
        Type fromArgument0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type fromArgument1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type fromArgument2 = Types.createWildcardType(new Type[]{Integer.class}, null);
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));
        
        assertFalse(testIsAssignable(to0, from1));
        assertTrue (testIsAssignable(to1, from1));
        assertTrue (testIsAssignable(to2, from1));

        assertFalse(testIsAssignable(to0, from2));
        assertFalse(testIsAssignable(to1, from2));
        assertTrue (testIsAssignable(to2, from2));
    }

    
    /**
     * Test cases like 
     * List<? extends Number> = ArrayList<? super Integer>
     */
    @Test
    public void testParameterized_ToWildcardTypeWithUpperBound_FromWildcardTypeWithLowerBound()
    {
        Type toArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type toArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type toArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});

        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);
        
        Type fromArgument0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type fromArgument1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type fromArgument2 = Types.createWildcardType(new Type[]{Integer.class}, null);
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));
        
        assertTrue (testIsAssignable(to0, from1));
        assertFalse(testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to2, from1));

        assertTrue (testIsAssignable(to0, from2));
        assertFalse(testIsAssignable(to1, from2));
        assertFalse(testIsAssignable(to2, from2));
    }

    
    /**
     * Test cases like 
     * List<? super Number> = ArrayList<? extends Integer>
     */
    @Test
    public void testParameterized_ToWildcardTypeWithLowerBound_FromWildcardTypeWithUpperBound()
    {
        Type toArgument0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type toArgument1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type toArgument2 = Types.createWildcardType(new Type[]{Integer.class}, null);

        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);
        
        Type fromArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));
        
        assertFalse(testIsAssignable(to0, from1));
        assertFalse(testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to2, from1));

        assertFalse(testIsAssignable(to0, from2));
        assertFalse(testIsAssignable(to1, from2));
        assertFalse(testIsAssignable(to2, from2));
    }

    
    /**
     * Test cases like 
     * List<? extends Number> = ArrayList<? extends Integer>
     */
    @Test
    public void testParameterized_ToWildcardTypeWithUpperBound_FromWildcardTypeWithUpperBound()
    {
        Type toArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type toArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type toArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});

        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);
        
        Type fromArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        assertFalse(testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));
        
        assertTrue (testIsAssignable(to0, from1));
        assertTrue (testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to2, from1));

        assertTrue (testIsAssignable(to0, from2));
        assertTrue (testIsAssignable(to1, from2));
        assertTrue (testIsAssignable(to2, from2));
    }

    
    /**
     * Test cases like 
     * List<? extends List<? extends Number>[]> = List<? extends List<? extends Integer>[]>
     */
    @Test
    public void testParameterized_ToGenericArrayType_FromGenericArrayType()
    {
        Type toInnerArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type toInnerArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type toInnerArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});

        Type toInner0 = Types.createParameterizedType(List.class, null, toInnerArgument0);
        Type toInner1 = Types.createParameterizedType(List.class, null, toInnerArgument1);
        Type toInner2 = Types.createParameterizedType(List.class, null, toInnerArgument2);
        
        Type toArrayType0 = Types.createGenericArrayType(toInner0);
        Type toArrayType1 = Types.createGenericArrayType(toInner1);
        Type toArrayType2 = Types.createGenericArrayType(toInner2);

        Type toArgument0 = Types.createWildcardType(null, new Type[]{toArrayType0});
        Type toArgument1 = Types.createWildcardType(null, new Type[]{toArrayType1});
        Type toArgument2 = Types.createWildcardType(null, new Type[]{toArrayType2});
        
        Type to0 = Types.createParameterizedType(List.class, null, toArgument0);
        Type to1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type to2 = Types.createParameterizedType(List.class, null, toArgument2);


        Type fromInnerArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromInnerArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromInnerArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});

        Type fromInner0 = Types.createParameterizedType(List.class, null, fromInnerArgument0);
        Type fromInner1 = Types.createParameterizedType(List.class, null, fromInnerArgument1);
        Type fromInner2 = Types.createParameterizedType(List.class, null, fromInnerArgument2);
        
        Type fromArrayType0 = Types.createGenericArrayType(fromInner0);
        Type fromArrayType1 = Types.createGenericArrayType(fromInner1);
        Type fromArrayType2 = Types.createGenericArrayType(fromInner2);

        Type fromArgument0 = Types.createWildcardType(null, new Type[]{fromArrayType0});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{fromArrayType1});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{fromArrayType2});
        
        Type from0 = Types.createParameterizedType(List.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(List.class, null, fromArgument2);
        
        assertTrue (testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));

        assertTrue (testIsAssignable(to0, from1));
        assertTrue (testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to2, from1));

        assertTrue (testIsAssignable(to0, from2));
        assertTrue (testIsAssignable(to1, from2));
        assertTrue (testIsAssignable(to2, from2));
    }
    

    /**
     * Test cases like 
     * List<? extends Set<? extends Number>> = ArrayList<? extends HashSet<Integer>>
     * and other freaky stuff.
     */
    @Test
    public void testParameterized_ToComplex_FromComplex()
    {
        Type number = Number.class;
        Type integer = Integer.class;
        
        Type setOfNumber = Types.createParameterizedType(Set.class, null, number);
        Type subtypeOfSetOfNumber = Types.createWildcardType(null, new Type[]{setOfNumber});
        Type listWithSubtypeOfSetOfNumber = Types.createParameterizedType(List.class, null, subtypeOfSetOfNumber);

        Type hashSetOfNumber = Types.createParameterizedType(HashSet.class, null, number);
        Type subtypeOfHashSetOfNumber = Types.createWildcardType(null, new Type[]{hashSetOfNumber});
        Type arrayListWithSubtypeOfHashSetOfNumber = Types.createParameterizedType(ArrayList.class, null, subtypeOfHashSetOfNumber);
        
        Type hashSetOfInteger = Types.createParameterizedType(HashSet.class, null, integer);
        Type subtypeOfHashSetOfInteger = Types.createWildcardType(null, new Type[]{hashSetOfInteger});
        Type arrayListWithSubtypeOfHashSetOfInteger = Types.createParameterizedType(ArrayList.class, null, subtypeOfHashSetOfInteger);
        
        Type subtypeOfNumber = Types.createWildcardType(null, new Type[]{number});
        Type setOfSubtypeOfNumber = Types.createParameterizedType(Set.class, null, subtypeOfNumber);
        Type subtypeOfSetOfSubtypeOfNumber = Types.createWildcardType(null, new Type[]{setOfSubtypeOfNumber});
        Type listWithSubtypeOfSetOfSubtypeOfNumber = Types.createParameterizedType(List.class, null, subtypeOfSetOfSubtypeOfNumber);
        
        // TRUE: List<? extends Set<Number>> = ArrayList<? extends HashSet<Number>>
        //List<? extends Set<Number>> x = null;
        //ArrayList<? extends HashSet<Number>> y = null;
        //x = y;
        assertTrue (testIsAssignable(
            listWithSubtypeOfSetOfNumber,
            arrayListWithSubtypeOfHashSetOfNumber));
        
        // FALSE: List<? extends Set<Number>> = ArrayList<? extends HashSet<Integer>>
        //List<? extends Set<Number>> x = null;
        //ArrayList<? extends HashSet<Integer>> y = null;
        //x = y;
        assertFalse(testIsAssignable(
            listWithSubtypeOfSetOfNumber,
            arrayListWithSubtypeOfHashSetOfInteger));
        
        // TRUE: List<? extends Set<? extends Number>> = ArrayList<? extends HashSet<Number>>
        //List<? extends Set<? extends Number>> x = null;
        //ArrayList<? extends HashSet<Number>> y = null;
        //x = y;
        assertTrue (testIsAssignable(
            listWithSubtypeOfSetOfSubtypeOfNumber,
            arrayListWithSubtypeOfHashSetOfNumber));
    }
    
    
    
    //=========================================================================
    // Test the remaining assignments to parameterized types

    /**
     * Test cases like 
     * List<Number> = ? super List
     */
    @Test
    public void test_ToParameterized_FromWildcardTypeWithLowerBound()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);

        Type from0 = Types.createWildcardType(new Type[]{Collection.class}, null);
        Type from1 = Types.createWildcardType(new Type[]{List.class}, null);
        Type from2 = Types.createWildcardType(new Type[]{ArrayList.class}, null);

        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
    }
    
    /**
     * Test cases like 
     * List<Number> = ? extends List
     */
    @Test
    public void test_ToParameterized_FromWildcardTypeWithUpperBound()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);

        Type from0 = Types.createWildcardType(null, new Type[]{Collection.class});
        Type from1 = Types.createWildcardType(null, new Type[]{List.class});
        Type from2 = Types.createWildcardType(null, new Type[]{ArrayList.class});

        assertFalse(testIsAssignable(to, from0));
        assertTrue (testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
    }
    
    /**
     * Test cases like 
     * List<Number> = T extends List
     */
    @Test
    public void test_ToParameterized_FromTypeVariable()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);

        Type from0 = Types.createTypeVariable("T", new Type[]{Collection.class});
        Type from1 = Types.createTypeVariable("T", new Type[]{List.class});
        Type from2 = Types.createTypeVariable("T", new Type[]{ArrayList.class});

        assertFalse(testIsAssignable(to, from0));
        assertTrue (testIsAssignable(to, from1));
        assertTrue (testIsAssignable(to, from2));
    }
    
    
    /**
     * Test cases like 
     * List<Number> = (T extends Number)[]
     */
    @Test
    public void test_ToParameterizedType_FromGenericArrayType()
    {
        Type to = Types.createParameterizedType(List.class, null, Number.class);
        
        Type fromComponent0 = Types.createTypeVariable("T", new Type[]{Number.class, Comparable.class});
        Type from0 = Types.createGenericArrayType(fromComponent0);

        assertFalse(testIsAssignable(to, from0));
    }
    
    
    
    
    
    

    
    
    
    //=========================================================================
    // Test assignability for type variables
    
    /**
     * Test cases like 
     * T extends Number = Integer
     */
    @Test
    public void test_ToTypeVariable_FromClass()
    {
        Type to = Types.createTypeVariable("T", new Type[]{Number.class});
        
        Type from0 = Object.class;
        Type from1 = Number.class;
        Type from2 = Integer.class;

        // By default, a type variable is not assignable by anything
        // (as long as there is no predefined mapping, and the type
        // variables are not assumed to be free)
        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
        assertTrue (testIsAssignable(to, to));
    }

    
    /**
     * Test cases like 
     * T extends List = List<Integer>
     */
    @Test
    public void test_ToTypeVariable_FromParameterizedType()
    {
        Type to = Types.createTypeVariable("T", new Type[]{List.class});
        
        Type fromArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type fromArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type fromArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type from0 = Types.createParameterizedType(Collection.class, null, fromArgument0);
        Type from1 = Types.createParameterizedType(List.class, null, fromArgument1);
        Type from2 = Types.createParameterizedType(ArrayList.class, null, fromArgument2);

        // By default, a type variable is not assignable by anything
        // (as long as there is no predefined mapping, and the type
        // variables are not assumed to be free)
        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
        assertTrue (testIsAssignable(to, to));
    }

    
    /**
     * Test cases like 
     * T extends Number = ? super Integer
     */
    @Test
    public void test_ToTypeVariable_FromWildcardTypeWithLowerBound()
    {
        Type to = Types.createTypeVariable("T", new Type[]{Number.class});
        
        Type from0 = Types.createWildcardType(new Type[]{Object.class}, null);
        Type from1 = Types.createWildcardType(new Type[]{Number.class}, null);
        Type from2 = Types.createWildcardType(new Type[]{Integer.class}, null);

        // By default, a type variable is not assignable by anything
        // (as long as there is no predefined mapping, and the type
        // variables are not assumed to be free)
        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
        assertTrue (testIsAssignable(to, to));
    }
    
    /**
     * Test cases like 
     * T extends Number = ? extends Integer
     */
    @Test
    public void test_ToTypeVariable_FromWildcardTypeWithUpperBound()
    {
        Type to = Types.createTypeVariable("T", new Type[]{Number.class});
        
        Type from0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type from1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type from2 = Types.createWildcardType(null, new Type[]{Integer.class});

        // By default, a type variable is not assignable by anything
        // (as long as there is no predefined mapping, and the type
        // variables are not assumed to be free)
        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
        assertTrue (testIsAssignable(to, to));
    }

    
    /**
     * Test cases like 
     * T extends Number = U extends Integer
     */
    @Test
    public void test_ToTypeVariable_FromTypeVariable()
    {
        Type to = Types.createTypeVariable("T", new Type[]{Number.class});
        
        Type from0 = Types.createTypeVariable("U", new Type[]{Object.class});
        Type from1 = Types.createTypeVariable("U", new Type[]{Number.class});
        Type from2 = Types.createTypeVariable("U", new Type[]{Integer.class});

        // By default, a type variable is not assignable by anything
        // (as long as there is no predefined mapping, and the type
        // variables are not assumed to be free)
        assertFalse(testIsAssignable(to, from0));
        assertFalse(testIsAssignable(to, from1));
        assertFalse(testIsAssignable(to, from2));
        assertTrue (testIsAssignable(to, to));
    }

    
    
    /**
     * Test cases like 
     * T extends List<Integer>[] = List<Integer>[]
     */
    @Test
    public void test_ToTypeVariable_FromGenericArrayType()
    {
        Type collectionWithObject = Types.createParameterizedType(Collection.class, null, Object.class);
        Type listWithNumber = Types.createParameterizedType(List.class, null, Number.class);
        Type arrayListWithInteger = Types.createParameterizedType(ArrayList.class, null, Integer.class);

        Type arrayOfCollectionWithObject = Types.createGenericArrayType(collectionWithObject);
        Type arrayOfListWithNumber = Types.createGenericArrayType(listWithNumber);
        Type arrayOfArrayListWithInteger = Types.createGenericArrayType(arrayListWithInteger);

        Type to0 = Types.createTypeVariable("T", new Type[]{arrayOfCollectionWithObject});
        Type to1 = Types.createTypeVariable("T", new Type[]{arrayOfListWithNumber});
        Type to2 = Types.createTypeVariable("T", new Type[]{arrayOfArrayListWithInteger});
        
        Type from0 = arrayOfCollectionWithObject;
        Type from1 = arrayOfListWithNumber;
        Type from2 = arrayOfArrayListWithInteger;

        // By default, a type variable is not assignable by anything
        // (as long as there is no predefined mapping, and the type
        // variables are not assumed to be free)
        assertFalse(testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));
        assertFalse(testIsAssignable(to0, from1));
        assertFalse(testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to2, from1));
        assertFalse(testIsAssignable(to0, from2));
        assertFalse(testIsAssignable(to1, from2));
        assertFalse(testIsAssignable(to2, from2));
    }

    
    
    /**
     * Test cases like 
     * Collection<Object>[] = Number.class
     */
    @Test
    public void test_ToGenericArrayType_FromClass()
    {
        Type collectionWithObject = Types.createParameterizedType(Collection.class, null, Object.class);
        Type arrayOfCollectionWithObject = Types.createGenericArrayType(collectionWithObject);
        
        Type to = arrayOfCollectionWithObject;
        
        Type from0 = Number.class;

        assertFalse(testIsAssignable(to, from0));
    }

    
    /**
     * Test cases like 
     * Collection<Object>[] = List<Number>
     */
    @Test
    public void test_ToGenericArrayType_FromParameterizedType()
    {
        Type collectionWithObject = Types.createParameterizedType(Collection.class, null, Object.class);
        Type arrayOfCollectionWithObject = Types.createGenericArrayType(collectionWithObject);
        
        Type to = arrayOfCollectionWithObject;
        
        Type from0 = Types.createParameterizedType(List.class, null, Number.class);

        assertFalse(testIsAssignable(to, from0));
    }

    
    /**
     * Test matching for cases like 
     * (T extends Number)[] = ? super (U extends Number)[] 
     * where the given types are type arguments
     */
    @Test
    public void test_ToGenericArrayType_FromWildcardTypeWithLowerBound()
    {
        // TODO Is this required?
    }
    
    /**
     * Test matching for cases like 
     * (T extends Number)[] = ? super (U extends Number)[] 
     * where the given types are type arguments
     */
    @Test
    public void test_ToGenericArrayType_FromWildcardTypeWithUpperBound()
    {
        // TODO Is this required?
    }
    

    /**
     * Test cases like 
     * Collection<Object>[] = T
     */
    @Test
    public void test_ToGenericArrayType_FromTypeVariable()
    {
        Type collectionWithObject = Types.createParameterizedType(Collection.class, null, Object.class);
        Type arrayOfCollectionWithObject = Types.createGenericArrayType(collectionWithObject);
        
        Type to = arrayOfCollectionWithObject;
        
        Type from0 = Types.createTypeVariable("T");

        assertFalse(testIsAssignable(to, from0));
        
    }


    /**
     * Test cases like 
     * List<? extends Number>[] = ArrayList<Integer>[] 
     */
    @Test
    public void test_ToGenericArrayType_FromGenericArrayType()
    {
        Type toArgument0 = Types.createWildcardType(null, new Type[]{Object.class});
        Type toArgument1 = Types.createWildcardType(null, new Type[]{Number.class});
        Type toArgument2 = Types.createWildcardType(null, new Type[]{Integer.class});
        
        Type toComponentType0 = Types.createParameterizedType(Collection.class, null, toArgument0);
        Type toComponentType1 = Types.createParameterizedType(List.class, null, toArgument1);
        Type toComponentType2 = Types.createParameterizedType(ArrayList.class, null, toArgument2);
        
        Type to0 = Types.createGenericArrayType(toComponentType0);
        Type to1 = Types.createGenericArrayType(toComponentType1);
        Type to2 = Types.createGenericArrayType(toComponentType2);

        Type fromComponentType0 = Types.createParameterizedType(Collection.class, null, Object.class);
        Type fromComponentType1 = Types.createParameterizedType(List.class, null, Number.class);
        Type fromComponentType2 = Types.createParameterizedType(ArrayList.class, null, Integer.class);
        
        Type from0 = Types.createGenericArrayType(fromComponentType0);
        Type from1 = Types.createGenericArrayType(fromComponentType1);
        Type from2 = Types.createGenericArrayType(fromComponentType2);

        assertTrue (testIsAssignable(to0, from0));
        assertFalse(testIsAssignable(to1, from0));
        assertFalse(testIsAssignable(to2, from0));

        assertTrue (testIsAssignable(to0, from1));
        assertTrue (testIsAssignable(to1, from1));
        assertFalse(testIsAssignable(to2, from1));

        assertTrue (testIsAssignable(to0, from2));
        assertTrue (testIsAssignable(to1, from2));
        assertTrue (testIsAssignable(to2, from2));
    }
    
    
    
    

}
