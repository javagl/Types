/*
 * www.javagl.de - Types
 *
 * Copyright (c) 2012-2015 Marco Hutter - http://www.javagl.de
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.types;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods related to the computation of supertypes.
 */
public class Supertypes
{
    /**
     * Returns an unmodifiable set containing all raw supertypes of 
     * the given type. <br>
     * <br>
     * For a type <code>ArrayList&lt;String&gt;</code>, the returned
     * set will contain
     * <ul>
     *   <li><code>java.util.ArrayList</code></li>
     *   <li><code>java.util.AbstractList</code></li>
     *   <li><code>java.util.AbstractCollection</code></li>
     *   <li><code>java.lang.Object</code></li>
     *   <li><code>java.util.Collection</code></li>
     *   <li><code>java.lang.Iterable</code></li>
     *   <li><code>java.util.List</code></li>
     *   <li><code>java.util.RandomAccess</code></li>
     *   <li><code>java.lang.Cloneable</code></li>
     *   <li><code>java.io.Serializable</code></li>     
     * </ul> 
     *  
     * @param type The type
     * @return The set of all raw supertypes
     * @throws IllegalArgumentException If the given type or any of its
     * raw supertypes is neither a Class nor a ParameterizedType
     */
    public static Set<Type> computeRawSupertypes(Type type)
    {
        Set<Type> result = new LinkedHashSet<Type>();
        collectRawSupertypes(type, result);
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Stores all raw supertypes of the given type in the given set.
     *  
     * @param type The type
     * @param result The set of all raw supertypes
     * @throws IllegalArgumentException If the given type or any of its
     * raw supertypes is neither a Class nor a ParameterizedType
     */
    private static void collectRawSupertypes(
        Type type, Set<Type> result)
    {
        if (type == null)
        {
            return;
        }
        Class<?> c = Types.asClass(type);
        result.add(c);
        Type superclass = c.getSuperclass();
        collectRawSupertypes(superclass, result);
        for (Type i : c.getInterfaces())
        {
            collectRawSupertypes(i, result);
        }
    }
    
    
    
    /**
     * Returns an unmodifiable set containing all generic supertypes of 
     * the given type. <br>
     * <br>
     * For a parameterized type <code>List&lt;String&gt;</code>, the returned
     * set will contain 
     * <ul>
     *   <li><code>List&lt;String&gt;</code></li> 
     *   <li><code>Collection&lt;E</code> (of List)<code>&gt;</code></li> 
     *   <li><code>Iterable&lt;E</code> (of Collection)<code>&gt;</code></li>
     * </ul> 
     *  
     * @param type The type
     * @return The set of all generic supertypes
     * @throws IllegalArgumentException If the given type is neither
     * a parameterized type nor a Class
     */
    public static Set<ParameterizedType> computeGenericSupertypes(Type type)
    {
        Set<ParameterizedType> result = new LinkedHashSet<ParameterizedType>();
        collectGenericSupertypes(type, result);
        return Collections.unmodifiableSet(result);
    }

    /**
     * Stores all generic supertypes of the given type in the given set. 
     *  
     * @param type The type
     * @param result The set of all generic supertypes
     * @throws IllegalArgumentException If the given type is neither
     * a parameterized type nor a Class
     */
    private static void collectGenericSupertypes(
        Type type, Set<ParameterizedType> result)
    {
        if (type == null)
        {
            return;
        }
        Class<?> c = null;
        if (type instanceof Class<?>)
        {
            c = (Class<?>)type;
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            result.add(parameterizedType);
            c = Types.getRawType(parameterizedType);
            
        }
        else
        {
            throw new IllegalArgumentException(
                "Can not extract generic supertypes from "+type);
        }
        Type genericSuperclass = c.getGenericSuperclass();
        collectGenericSupertypes(genericSuperclass, result);
        for (Type genericInterface : c.getGenericInterfaces())
        {
            collectGenericSupertypes(genericInterface, result);
        }
    }
    
    
    
    
    /**
     * Returns an unmodifiable set containing all supertypes of the given 
     * type.<br>
     * <br>
     * Note that the set of all supertypes of a type will be infinite
     * in many cases. For example, the type <code>Integer</code> has the 
     * supertype <code>Comparable&lt;Integer&gt;</code>. This type, in turn, 
     * has the supertype 
     * <code>Comparable&lt;? extends Comparable&lt;Integer&gt;&gt;</code>,
     * which has the supertype
     * <code>Comparable&lt;? extends Comparable&lt;? extends 
     * Comparable&lt;Integer&gt;&gt;&gt;</code>
     * and so on. Thus, the returned set will in these cases contain only the 
     * supertypes where the type parameters are raw types.<br> 
     * <br>
     * For example, for the type <code>Integer</code>, the resulting set will 
     * only contain all raw supertypes: 
     * <br>
     * <ul>
     *   <li><code>java.lang.Integer</code></li>
     *   <li><code>java.lang.Number</code></li>
     *   <li><code>java.lang.Object</code></li>
     *   <li><code>java.lang.Serializable</code></li>
     *   <li><code>java.lang.Comparable</code></li>
     * </ul><br>
     * <br>
     * For the type <code>Set&lt;Integer&gt;</code>, the set will contain
     * the following 18 supertypes:
     * <ul>
     *   <li>java.util.Set</li>
     *   <li>java.util.Collection</li>
     *   <li>java.lang.Iterable</li>
     *   <li>java.util.Set&lt;java.lang.Integer&gt;</li>
     *   <li>java.util.Set&lt;? extends java.lang.Number&gt;</li>
     *   <li>java.util.Set&lt;? extends java.io.Serializable&gt;</li>
     *   <li>java.util.Set&lt;? extends java.lang.Comparable&gt;</li>
     *   <li>java.util.Set&lt;?&gt;</li>
     *   <li>java.util.Collection&lt;java.lang.Integer&gt;</li>
     *   <li>java.util.Collection&lt;? extends java.lang.Number&gt;</li>
     *   <li>java.util.Collection&lt;? extends java.io.Serializable&gt;</li>
     *   <li>java.util.Collection&lt;? extends java.lang.Comparable&gt;</li>
     *   <li>java.util.Collection&lt;?&gt;</li>
     *   <li>java.lang.Iterable&lt;java.lang.Integer&gt;</li>
     *   <li>java.lang.Iterable&lt;? extends java.lang.Number&gt;</li>
     *   <li>java.lang.Iterable&lt;? extends java.io.Serializable&gt;</li>
     *   <li>java.lang.Iterable&lt;? extends java.lang.Comparable&gt;</li>
     *   <li>java.lang.Iterable&lt;?&gt;</li>
     * </ul>
     * 
     * Note that the returned set may be large. For parameterized types, the 
     * returned set will contain all raw supertypes as well as all 
     * parameterized supertypes of the parameterized type, with all 
     * combinations of wildcard type parameters whose bounds are (raw) 
     * supertypes of the actual type arguments of the parameterized type.
     * For example, the type 
     * <code>LinkedHashMap&lt;JCheckBox,JCheckBox&gt;</code> has 791 
     * supertypes.
     *  
     * @param type The type
     * @return The set containing all supertypes of the given type
     */
    public static Set<Type> computeSupertypes(Type type)
    {
        Set<Type> result = new LinkedHashSet<Type>();
        collectSupertypes(new LinkedHashSet<Type>(), type, result);
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Returns an unmodifiable set containing all supertypes of the
     * given type. <br>
     * <br>
     * The given stack stores all parameterized types for which the 
     * computation of supertypes is currently in progress. This stack is 
     * used to resolve the cases where a type has an infinite number of
     * supertypes. Whenever this method is called with a type that is
     * already on the stack, the computation will stop and an empty
     * set will be returned.
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param type The type
     * @return The set containing all supertypes of the given type
     */
    private static Set<Type> collectSupertypes(
        Set<Type> typeStack, Type type)
    {
        // TODO This type stack thing is not very elegant and should be reviewed
        Set<Type> result = new LinkedHashSet<Type>();
        collectSupertypes(typeStack, type, result);
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Stores all supertypes of the given type in the given set
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param type The type
     * @param result The set storing all supertypes
     */
    private static void collectSupertypes(
        Set<Type> typeStack, Type type, Set<Type> result)
    {
        if (type == null)
        {
            return;
        }
        if (typeStack.contains(type))
        {
            return;
        }

        result.add(type);
        if (type instanceof Class<?>)
        {
            Class<?> c = (Class<?>)type;
            collectSupertypesOfClass(typeStack, c, result);
        }     
        else if (type instanceof ParameterizedType)
        {
            typeStack.add(type);
            ParameterizedType parameterizedType = (ParameterizedType)type;
            collectSupertypesOfParameterizedType(
                typeStack, parameterizedType, result);
            typeStack.remove(type);
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wildcardType = (WildcardType)type;
            collectSupertypesOfUpperBoundedType(
                typeStack, wildcardType.getUpperBounds(), result);
        }
        else if (type instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>)type;
            collectSupertypesOfUpperBoundedType(
                typeStack, typeVariable.getBounds(), result);
        }
        else if (type instanceof GenericArrayType)
        {
            //GenericArrayType genericArrayType = (GenericArrayType)t;
            // The only supertype of generic array types:
            result.add(Object.class);
        }
        else
        {
            throw new IllegalArgumentException("Unknown type: "+type);
        }
        
    }
    


    /**
     * Stores all supertypes of the given class in the given set
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param c The class
     * @param result The set storing all supertypes
     */
    private static void collectSupertypesOfClass(
        Set<Type> typeStack, Class<?> c, Set<Type> result)
    {
        Type superclass = c.getSuperclass();
        collectSupertypes(typeStack, superclass, result);
        for (Type i : c.getInterfaces())
        {
            collectSupertypes(typeStack, i, result);
        }
    }


    /**
     * Collect all supertypes of the given parameterized type, and store
     * them in the given set
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param parameterizedType The parameterized type
     * @param result The set storing all supertypes of the given type
     */
    private static void collectSupertypesOfParameterizedType(
        Set<Type> typeStack, ParameterizedType parameterizedType, 
        Set<Type> result)
    {
        Set<Type> rawSupertypes = computeRawSupertypes(parameterizedType);
        result.addAll(rawSupertypes);
        
        Map<Type, Type> typeParameterToTypeArgumentMap = 
            createTypeParameterToTypeArgumentMap(parameterizedType);
        
        Set<ParameterizedType> parameterizedSupertypes = 
            computeGenericSupertypes(parameterizedType);
        for (ParameterizedType parameterizedSupertype : parameterizedSupertypes)
        {
            Class<?> rawSupertype = Types.getRawType(parameterizedSupertype);

            List<List<Type>> supertypeTypeArgumentDomain = 
                computeSupertypeTypeArgumentDomain(
                    typeStack,  
                    parameterizedSupertype, typeParameterToTypeArgumentMap);
            
            Iterable<List<Type>> supertypeTypeArgumentCombinationIterable =
                Iterables.cartesianProduct(supertypeTypeArgumentDomain);
            
            for (List<Type> supertypeTypeArgumentCombination : 
                supertypeTypeArgumentCombinationIterable)
            {
                Type[] supertypeTypeArguments = 
                    supertypeTypeArgumentCombination.toArray(new Type[0]);
                Type supertype = Types.createParameterizedType(
                    rawSupertype, null, supertypeTypeArguments);
                result.add(supertype);
            }
        }
    }
    
    
    
    

    /**
     * Creates an unmodifiable map from the type parameters of all 
     * parameterized supertypes of the given type to the actual type 
     * arguments. <br>
     * <br>
     * For a parameterized type <code>HashMap&lt;String,Float&gt;</code>, 
     * the returned map will contain 
     * <ul>
     *   <li><code>K</code> (of HashMap) := <code>String</code></li>
     *   <li><code>V</code> (of HashMap) := <code>Float</code></li>
     *   <li><code>K</code> (of AbstractMap) := <code>String</code></li>
     *   <li><code>V</code> (of AbstractMap) := <code>Float</code></li>
     *   <li><code>K</code> (of Map) := <code>String</code></li>
     *   <li><code>V</code> (of Map) := <code>Float</code></li>
     * </ul>    
     * <br>
     * For a parameterized type <code>HashMap&lt;K,V&gt;</code>, 
     * the returned map will be empty, because no actual type
     * arguments are present.    
     * 
     * @param type The parameterized type
     * @return The mapping of type parameters to type arguments
     * @throws IllegalArgumentException If the raw type of the given
     * parameterized type is not a Class.
     */
    static Map<Type, Type> createTypeParameterToTypeArgumentMap(
        Type type)
    {
        Map<Type, Type> typeParameterToTypeArgument = 
            new LinkedHashMap<Type, Type>();

        Set<ParameterizedType> parameterizedSupertypes = 
            computeGenericSupertypes(type);
        for (ParameterizedType parameterizedSupertype : parameterizedSupertypes)
        {
            Type[] typeArguments = 
                parameterizedSupertype.getActualTypeArguments();
            
            Class<?> rawType = Types.getRawType(parameterizedSupertype);
            TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
            for (int i = 0; i < typeParameters.length; i++)
            {
                Type typeParameter = typeParameters[i];
                Type typeArgument = typeArguments[i];
                while (true)
                {
                    Type typeArgumentValue = 
                        typeParameterToTypeArgument.get(typeArgument);
                    if (typeArgumentValue == null)
                    {
                        break;
                    }
                    typeArgument = typeArgumentValue;
                }
                if (!Types.isTypeVariable(typeArgument))
                {
                    typeParameterToTypeArgument.put(
                        typeParameter, typeArgument);
                }
            }
        }
        return Collections.unmodifiableMap(typeParameterToTypeArgument);
    }
    
    
    /**
     * Computes the domain for the possible type arguments of the given
     * parameterized supertype of a parameterized type.<br> 
     * <br> 
     * The domain itself as well as the lists it contains will be
     * unmodifiable.<br>
     * <br>
     * As an example for the parameterized type 
     * <code>HashMap&lt;String,Float&gt;</code>:
     * The parameterized supertype that is passed to this method is
     * <code>Map&lt;K,V&gt;</code>. The map that is passed to this 
     * method contains <code>K := String, V := Float</code> (as computed
     * by {@link #createTypeParameterToTypeArgumentMap(Type)}).
     * For each dimension of the type parameter space, the returned 
     * domain will contain a list of types that can be type arguments of
     * the parameterized supertype. Namely, <br>
     * <br>
     * <code>
     * {String, ?, ? extends Serializable, 
     *  ? extends Comparable, ? extends CharSequence}
     * </code>
     * and <br>
     * <code> 
     * {Integer, ?, ? extends Number, ? extends Serializable, 
     * ? extends Comparable}
     * </code>
     * <br>
     * <br>
     * This domain describes the type parameters of parameterized supertypes 
     * of the original parameterized type. For 
     * <code>HashMap&lt;String,Float&gt;</code>, possible supertypes are 
     * given by the cartesian product of these lists: 
     * <ul>
     *   <li><code><i>Supertype</i>&lt;String,Float&gt;</code></li>
     *   <li><code><i>Supertype</i>&lt;?,Float&gt;</code></li>
     *   <li><code><i>Supertype</i>&lt;String,?&gt;</code></li>
     *   <li>
     *     <code><i>Supertype</i>&lt;? extends Serializable,Float&gt;</code>
     *   </li>
     *   <li><code><i>Supertype</i>&lt;String,? extends Number&gt;</code></li>
     *   <li><code>...</code></li>
     * </ul>
     * where <code><i>Supertype</i></code> denotes one of the
     * {@link #computeGenericSupertypes(Type) generic supertypes} of 
     * <code>HashMap</code>. 
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param parameterizedSupertype The parameterized type for which the
     * domain should be computed
     * @param typeParameterToTypeArgumentMap The map from type parameters
     * to the actual type arguments
     * @return The type argument domain
     */
    private static List<List<Type>> computeSupertypeTypeArgumentDomain(
        Set<Type> typeStack,  
        ParameterizedType parameterizedSupertype, 
        Map<Type, Type> typeParameterToTypeArgumentMap)
    {
        List<List<Type>> domain = new ArrayList<List<Type>>();
        Type supertypeArguments[] = 
            parameterizedSupertype.getActualTypeArguments();
        for (Type supertypeArgument : supertypeArguments)
        {
            Type instantiatedSupertypeArgument = supertypeArgument;
            if (supertypeArgument instanceof TypeVariable<?>)
            {
                instantiatedSupertypeArgument = 
                    typeParameterToTypeArgumentMap.get(supertypeArgument);
            }
            Set<Type> supertypeTypeArguments = 
                computeSupertypesWithWildcards(
                    typeStack,  
                    instantiatedSupertypeArgument);
            
            domain.add(Collections.unmodifiableList(
                new ArrayList<Type>(supertypeTypeArguments)));
        }
        return Collections.unmodifiableList(domain);
    }

    /**
     * Compute the unmodifiable set containing all supertypes of the
     * given type, including wildcard types. That is, for the type 
     * <code>String</code>, the returned set will contain <br>
     * <br>
     * <code>
     * { String, ?, ? extends Serializable, ? extends Comparable, 
     *   ? extends CharSequence}
     * </code><br>
     * <br> 
     * These are exactly the possible type parameters for parameterized
     * supertypes of a parameterized type that has a parameter of the
     * given type. This means that for a parameterized type like 
     * <code>List&lt;String&gt;</code>, the possible parameterized 
     * supertypes are <br>
     * <ul>
     *   <li><code><i>Supertype</i>&lt;String&gt;</code></li>
     *   <li><code><i>Supertype</i>&lt;?&gt;</code></li>
     *   <li><code><i>Supertype</i>&lt;? extends Serializable&gt;</code></li>
     *   <li><code><i>Supertype</i>&lt;? extends Comparable&gt;</code></li>
     *   <li><code><i>Supertype</i>&lt;? extends CharSequence&gt;</code></li>
     * </ul>  
     * where <code><i>Supertype</i></code> denotes one of the
     * {@link #computeGenericSupertypes(Type) generic supertypes} of 
     * <code>List</code>. 
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param type The type
     * @return The supertypes, including wildcard types
     */
    private static Set<Type> computeSupertypesWithWildcards(
        Set<Type> typeStack, Type type)
    {
        if (type instanceof WildcardType)
        {
            WildcardType wildcardType = (WildcardType)type;
            return computeSupertypesOfUpperBoundedType(
                typeStack, wildcardType.getUpperBounds());
        }
        else if (type instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>)type;
            return computeSupertypesOfUpperBoundedType(
                typeStack, typeVariable.getBounds());
        }
        
        Set<Type> supertypes = collectSupertypes(typeStack, type);
        Set<Type> supertypesWithWildcards = new LinkedHashSet<Type>();
        for (Type supertype : supertypes)
        {
            if (supertype.equals(type))
            {
                supertypesWithWildcards.add(supertype);
            }
            else
            {
                Type wildcardSupertype = Types.createWildcardType(
                    null, new Type[]{supertype});
                supertypesWithWildcards.add(wildcardSupertype);
            }
        }
        
        return Collections.unmodifiableSet(supertypesWithWildcards);
    }

    
    
    /**
     * Creates and returns an unmodifiable set that was filled with
     * {@link #collectSupertypesOfUpperBoundedType(Set, Type[], Set)}
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param upperBounds The upper bounds of the type
     * @return The set of supertypes, including wildcard types
     */
    private static Set<Type> computeSupertypesOfUpperBoundedType(
        Set<Type> typeStack, Type upperBounds[])
    {
        Set<Type> result = new LinkedHashSet<Type>();
        collectSupertypesOfUpperBoundedType(
            typeStack, upperBounds, result);
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Collect all supertypes of a type with the given upper bounds, 
     * including wildcard types, and store them in the given set.
     * 
     * @param typeStack The stack storing the types for which the 
     * computation of supertypes is currently in progress.
     * @param upperBounds The upper bounds of the type
     * @param result The set that will store all supertypes, including 
     * wildcard types
     */
    private static void collectSupertypesOfUpperBoundedType(
        Set<Type> typeStack, Type upperBounds[], Set<Type> result)
    {
        // Compute the set of all possible upper bounds, 
        // including their respective supertypes
        Set<Type> allUpperBounds = new LinkedHashSet<Type>();
        for (Type upperBound : upperBounds)
        {
            allUpperBounds.addAll(
                collectSupertypes(typeStack, upperBound));
        }
        
        // Each upper bound may be used as the single upper bound
        // for a wildcard that is a supertype of the current type
        for (Type upperBound : allUpperBounds)
        {
            Type supertype = Types.createWildcardType(
                null, new Type[]{upperBound});
            result.add(supertype);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Supertypes()
    {
        // Private constructor to prevent instantiation
    }

}
