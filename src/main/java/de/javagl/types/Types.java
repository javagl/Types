/*
 * www.javagl.de - Types
 *
 * Copyright (c) 2012-2017 Marco Hutter - http://www.javagl.de
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
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Methods related to Types. <br>
 * <br>
 * Most of the methods in this class are utility methods for creating 
 * Type instances or checking properties of Type instances.<br>
 * <br>
 * Some of the methods in this class are convenience methods for
 * or entry points for operations that are offered by other classes
 * in this package:
 * <ul>
 *   <li>
 *     The {@link Types#create(Type)} method may be used to create a 
 *     {@link TypeBuilder} instance
 *   </li>
 *   <li>
 *     The {@link Types#createTypeVariables()} method may be used to create a 
 *     {@link TypeVariableBuilder} instance
 *   </li>
 *   <li>
 *     The {@link Types#parse(String)} method may be used to parse a type
 *     from a string, using the internal type parser classes
 *   </li>
 *   <li>
 *     The {@link Types#isAssignable(Type, Type)} method internally uses
 *     a default implementation of a {@link TypeAssignabilityTester}
 *   </li>
 * </ul>
 *
 */
public final class Types
{
    /**
     * The default {@link TypeAssignabilityTester}
     */
    private static final TypeAssignabilityTester TYPE_ASSIGNABILITY_TESTER = 
        TypeAssignabilityTesters.create();
    
    /**
     * Create a new ParameterizedType, without an owner type
     * 
     * @param rawType The raw type
     * @param actualTypeArguments The actual type arguments
     * @return The ParameterizedType
     * @throws NullPointerException If the given rawType is <code>null</code>
     * @throws IllegalArgumentException If the given raw type is a 
     * generic declaration whose number of type parameters is not
     * equal to the given number of type arguments 
     */
    public static ParameterizedType create(
        Type rawType, Type ... actualTypeArguments)
    {
        return createParameterizedType(rawType, null, actualTypeArguments);
    }
    
    /**
     * Create a new ParameterizedType
     * 
     * @param rawType The raw type
     * @param ownerType The owner type
     * @param actualTypeArguments The actual type arguments
     * @return The ParameterizedType
     * @throws NullPointerException If the given rawType is <code>null</code>
     * @throws IllegalArgumentException If the given raw type is a 
     * generic declaration whose number of type parameters is not
     * equal to the given number of type arguments 
     */
    public static ParameterizedType createParameterizedType(
        Type rawType, Type ownerType, Type ... actualTypeArguments)
    {
        Objects.requireNonNull(rawType, "The rawType is null");
        if (rawType instanceof GenericDeclaration)
        {
            GenericDeclaration rawGenericDeclaration = 
                (GenericDeclaration)rawType;
            TypeVariable<?>[] typeParameters = 
                rawGenericDeclaration.getTypeParameters();
            if (typeParameters.length != actualTypeArguments.length)
            {
                throw new IllegalArgumentException(
                    "Raw type "+rawType+" requires "+typeParameters.length+
                    " type parameters, but there " +
                    "are "+actualTypeArguments.length+
                    " type arguments specified: "+
                    Arrays.toString(actualTypeArguments));
            }
        }
        
        return new DefaultParameterizedType(
            rawType, ownerType, actualTypeArguments);
    }

    
    
    /**
     * Returns a new WildcardType.      
     * If the lower bounds are <code>null</code>, then an empty array will
     * be used. If the upper bounds are <code>null</code> or an empty
     * array, then an array containing the implicit upper bound
     * <code>java.lang.Object</code> will be used.
     * 
     * @param lowerBounds The lower bounds
     * @param upperBounds The upper bounds
     * @return The WildcardType
     */
    public static WildcardType createWildcardType(
        Type[] lowerBounds, Type[] upperBounds)
    {
        return new DefaultWildcardType(lowerBounds, upperBounds);
    }
    
    /**
     * Creates a new GenericArrayType
     * 
     * @param genericComponentType The generic component type
     * @return The GenericArrayType
     * @throws NullPointerException If the type is <code>null</code>
     */
    public static GenericArrayType createGenericArrayType(
        Type genericComponentType)
    {
        Objects.requireNonNull(genericComponentType, 
            "The genericComponentType is null");
        return new DefaultGenericArrayType(genericComponentType);
    }
    
    /**
     * Creates a new {@link TypeBuilder} with the given raw type
     * 
     * @param rawType The raw type
     * @return The {@link TypeBuilder} instance
     * @throws NullPointerException If the given rawType is <code>null</code>
     */
    public static TypeBuilder create(Type rawType)
    {
        Objects.requireNonNull(rawType, "The rawType is null");
        return new DefaultTypeBuilder(rawType);
    }
    
    /**
     * Creates a new {@link TypeVariableBuilder}. 
     * 
     * @return The new {@link TypeVariableBuilder}
     */
    public static TypeVariableBuilder createTypeVariables()
    {
        return new DefaultTypeVariableBuilder();
    }

    /**
     * Creates a new type variable with the given name and upper bounds.
     * If no bounds are given, then <code>Object.class</code> will be used
     * as the implicit bound.<br>
     * <br> 
     * The type variable will belong to a locally constructed generic 
     * declaration that only contains the given type variable. In order 
     * to create a set of type variables that share one common generic 
     * declaration, a {@link TypeVariableBuilder} may be used, which can 
     * be created with {@link #createTypeVariables()} or
     * {@link #createTypeVariables(GenericDeclaration)}.
     * 
     * @param name The name
     * @param bounds The bounds
     * @return The new type variable
     * @throws NullPointerException If the given name is <code>null</code>
     */
    public static TypeVariable<?> createTypeVariable(
        String name, Type ... bounds)
    {
        return createTypeVariables().add(name, bounds).
            build().getTypeParameters()[0];
    }
    
    
    /**
     * Creates a new {@link TypeVariableBuilder}.<br>
     * <br>
     * The caller is responsible to give a generic declaration that will 
     * eventually contain the type variables as its type parameters. 
     * This consistency can not be checked here due to the circular 
     * dependency between type variables and generic declarations.
     * 
     * @param genericDeclaration The generic declaration that all type
     * variables should belong to
     * @return The new {@link TypeVariableBuilder}
     * @throws NullPointerException If the given genericDeclaration is 
     * <code>null</code>
     */
    static TypeVariableBuilder createTypeVariables(
        GenericDeclaration genericDeclaration)
    {
        Objects.requireNonNull(genericDeclaration, 
            "The genericDeclaration is null");
        return new DefaultTypeVariableBuilder(genericDeclaration);
    }
    
    /**
     * Creates a new type variable with the given name and upper bounds.
     * If no bounds are given, then <code>Object.class</code> will be used
     * as the implicit bound.<br>
     * <br> 
     * The caller is responsible to give a generic declaration that will 
     * eventually contain the returned type variable as its type parameter. 
     * This consistency can not be checked here due to the circular 
     * dependency between type variables and generic declarations.
     * 
     * @param genericDeclaration The generic declaration that the type 
     * variable should belong to 
     * @param name The name
     * @param bounds The upper bounds. If no upper bounds are given, 
     * the type variable will have <code>Object.class</code> as its
     * implicit upper bound.
     * @return The new type variable
     * @throws NullPointerException If the name or the genericDeclaration is
     * <code>null</code>
     */
    public static TypeVariable<?> createTypeVariable(
        GenericDeclaration genericDeclaration,
        String name, Type ... bounds)
    {
        Objects.requireNonNull(name, "The name is null");
        Objects.requireNonNull(genericDeclaration, 
            "The genericDeclaration is null");
        return new DefaultTypeVariable<GenericDeclaration>(
            genericDeclaration, name, bounds);
    }
    
    
    /**
     * Parse a type from the given string. This method is a shortcut for <br>
     * <code>TypeParsers.create().parse(string)</code>
     * converting the possible <code>ClassNotFoundException</code> into an
     * <code>IllegalArgumentException</code>.
     * 
     * @param string The string
     * @return The parsed type
     * @throws IllegalArgumentException If the type could not be parsed
     */
    public static Type parse(String string) 
    {
        try
        {
            return TypeParsers.create().parse(string);
        } 
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException(
                "Invalid class name in string: "+string, e);
        }
    }
    
    /**
     * Returns an unmodifiable list with the same contents as the
     * given array
     * 
     * @param <T> The element type
     * @param array The array
     * @return The list
     * @throws NullPointerException If the given array is <code>null</code>
     */
    private static <T> List<T> unmodifiableCopy(T array[])
    {
        return Collections.unmodifiableList(
            new ArrayList<T>(Arrays.asList(array)));
    }
    
    
    /**
     * Returns whether the given type is a parameterized type, like
     * <code>List&lt;? extends Number&gt;</code>
     * 
     * @param type The type
     * @return Whether this type is a parameterized type
     */
    public static boolean isParameterizedType(Type type)
    {
        return (type instanceof ParameterizedType);
    }


    /**
     * Returns the list of type arguments of the given type. If the type is a 
     * {@link #isParameterizedType(Type) parameterized} type like 
     * <code>List&lt;? extends Number&gt;</code>, then
     * a list containing the Type for the class 
     * <code>? extends Number</code> is returned. Returns <code>null</code> 
     * if the type is no parameterized type. 
     * 
     * @param type The type
     * @return The list of type arguments if this a parameterized type,
     * or <code>null</code> if the given type is no parameterized type
     * @throws NullPointerException If the given type is <code>null</code>
     */
    public static List<Type> getTypeArguments(Type type)
    {
        Objects.requireNonNull(type, "The type is null");
        if (!isParameterizedType(type))
        {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType)type;
        return unmodifiableCopy(parameterizedType.getActualTypeArguments());
    }
    


    /**
     * Returns whether the given type is a wildcard type, like
     * <code>&lt;? extends Number&gt;</code>.
     * 
     * @param type The type
     * @return Whether the given type is a wildcard type
     */
    public static boolean isWildcardType(Type type)
    {
        return (type instanceof WildcardType);
    }

    /**
     * Returns the upper type bounds of the given type. If the given is a 
     * {@link #isWildcardType(Type) wildcard} type 
     * like <code>&lt;? extends Number&gt;</code>, then a list containing
     * the class <code>Number</code> is returned. If the given is no 
     * wildcard type, then <code>null</code> is returned. 
     * Note that even if no explicit bound is declared, there is at least 
     * the type <code>Object</code> as an implicit bound.
     * 
     * @param type The type
     * @return The upper bound for the given type, or <code>null</code> if
     * the given type is no wildcard type.
     * @throws NullPointerException If the given type is <code>null</code>
     */
    public static List<Type> getUpperBounds(Type type)
    {
        Objects.requireNonNull(type, "The type is null");
        if (!isWildcardType(type))
        {
            return null;
        }
        WildcardType wildcardType = (WildcardType)type;
        return unmodifiableCopy(wildcardType.getUpperBounds());
    }

    /**
     * Returns the lower type bounds of the given type. If the given is a 
     * {@link #isWildcardType(Type) wildcard} type 
     * like <code>&lt;? super Number&gt;</code>, then a list containing
     * the class <code>Number</code> is returned. If the given is no 
     * wildcard type, then <code>null</code> is returned. 
     * 
     * @param type The type
     * @return The lower bound for the given type, or <code>null</code> if
     * the given type is no wildcard type.
     * @throws NullPointerException If the given type is <code>null</code>
     */
    public static List<Type> getLowerBounds(Type type)
    {
        Objects.requireNonNull(type, "The type is null");
        if (!isWildcardType(type))
        {
            return null;
        }
        WildcardType wildcardType = (WildcardType)type;
        return unmodifiableCopy(wildcardType.getLowerBounds());
    }

    /**
     * Return whether the given type is a type variable. A type variable is,
     * for example, the <code><b>K</b></code> in a declaration like
     * <code>Map&lt;<b>K</b>, V&gt;</code>
     *  
     * @param type The type
     * @return Whether the given type is a type variable
     */
    public static boolean isTypeVariable(Type type)
    {
        return (type instanceof TypeVariable<?>);
    }

    /**
     * Returns the name of the given type if it is a 
     * {@link #isTypeVariable(Type) type variable}, or <code>null</code> 
     * otherwise
     *  
     * @param type The type
     * @return The name of the given type if it is a type variable
     * @throws NullPointerException If the given type is <code>null</code>
     */
    public static String getVariableName(Type type)
    {
        Objects.requireNonNull(type, "The type is null");
        if (!isTypeVariable(type))
        {
            return null;
        }
        TypeVariable<?> typeVariable = (TypeVariable<?>)type;
        return typeVariable.getName();
    }

    /**
     * Returns an unmodifiable list containing the upper bounds of the 
     * given type if it is a {@link #isTypeVariable(Type) type variable}, 
     * or <code>null</code> otherwise
     *  
     * @param type The type
     * @return The upper bounds of the given type if it is a type variable
     * @throws NullPointerException If the given type is <code>null</code>
     */
    public static List<Type> getVariableBounds(Type type)
    {
        Objects.requireNonNull(type, "The type is null");
        if (!isTypeVariable(type))
        {
            return null;
        }
        TypeVariable<?> typeVariable = (TypeVariable<?>)type;
        return unmodifiableCopy(typeVariable.getBounds());
    }

    /**
     * Returns the GenericDeclaration that declared the given type if it is a 
     * {@link #isTypeVariable(Type) type variable}, or <code>null</code> 
     * otherwise
     * 
     * @param type The type
     * @return Returns the GenericDeclaration that declared the given type 
     * if it is a type variable
     * @throws NullPointerException If the given type is <code>null</code>
     */
    public static GenericDeclaration getGenericDeclaration(Type type)
    {
        Objects.requireNonNull(type, "The type is null");
        if (!isTypeVariable(type))
        {
            return null;
        }
        TypeVariable<?> typeVariable = (TypeVariable<?>)type;
        GenericDeclaration genericDeclaration = 
            typeVariable.getGenericDeclaration();
        return genericDeclaration;
    }
    

    /**
     * Returns whether the given type is an interface. That is, whether
     * it is a Class that is an interface, or a ParameterizedType whose 
     * raw type is a Class that is an interface.
     * 
     * @param type The type
     * @return Whether the type is an interface
     */
    public static boolean isInterface(Type type)
    {
        if (type instanceof Class<?>)
        {
            Class<?> c = (Class<?>)type;
            return c.isInterface();
        }
        if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            return isInterface(parameterizedType.getRawType());
        }
        return false;
    }
    
    /**
     * Returns whether the given type is a primitive type. That is, 
     * whether it is a <code>Class</code> where 
     * {@link Class#isPrimitive()} returns <code>true</code>.
     * Note that this method will consider <code>void</code> as
     * a primitive type, although according to the JLS, 
     * <code>void</code> is not a type at all. 
     * 
     * @param type The type
     * @return Whether the given type is a primitive type
     */
    public static boolean isPrimitive(Type type)
    {
        if (!(type instanceof Class<?>))
        {
            return false;
        }
        Class<?> c = (Class<?>)type;
        return c.isPrimitive();
    }
    
    /**
     * Returns whether the given type is either <code>void.class</code>
     * or <code>Void.class</code>
     * 
     * @param type The type
     * @return Whether the type is a void type.
     */
    public static boolean isVoid(Type type)
    {
        return void.class.equals(type) || Void.class.equals(type);
    }
    

    /**
     * Returns whether the given type is an array type. That is, whether
     * it is a Class where {@link Class#isArray()} returns <code>true</code>,
     * or a GenericArrayType.
     * 
     * @param type The type
     * @return Whether the given type is an array type
     */
    public static boolean isArrayType(Type type)
    {
        if (type instanceof Class<?>)
        {
            Class<?> c = (Class<?>)type;
            return c.isArray();
        }
        if (type instanceof GenericArrayType)
        {
            return true;
        }
        return false;
    }
    
    /**
     * Returns the component type of the given array type. If the given 
     * type is not an {@link #isArrayType(Type) array type}, then
     * <code>null</code> is returned.
     * 
     * @param type The type
     * @return The component type of the given array type
     */
    public static Type getArrayComponentType(Type type)
    {
        if (type instanceof Class<?>)
        {
            Class<?> c = (Class<?>)type;
            return c.getComponentType();
        }
        if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            return genericArrayType.getGenericComponentType();
        }
        return null;
    }
    
    
    /**
     * Returns whether the given type 'to' is assignable from the given type
     * 'from'. <br>
     * <br>
     * This method tries to behave according to the Java Language 
     * Specification, and take into account all information that is 
     * available from the given type. <br>
     * <br> 
     * For example, for parameterized types, it will consider the type 
     * parameters: A <code>List&lt;? extends Number&gt;</code> or the raw 
     * type <code>List</code> will be assignable from a 
     * <code>List&lt;Integer&gt;</code>. In contrast, 
     * a <code>List&lt;Number&gt;</code> will <strong>not</strong> be 
     * assignable from a <code>List&lt;Integer&gt;</code>. <br>
     * <br>  
     * For primitive types, this method will consider boxing- and unboxing 
     * conversions. Thus, a type <code>int.class</code> will be assignable 
     * from a <code>Integer.class</code> or a <code>Short.class</code>. 
     * The types <code>short.class</code> or <code>Serializable.class</code>
     * will be assignable from <code>byte.class</code> or 
     * <code>Byte.class</code>.  
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @return Whether the type is assignable
     */
    public static boolean isAssignable(Type to, Type from)
    {
        return TYPE_ASSIGNABILITY_TESTER.isAssignable(to, from);
    }
    
    /**
     * Returns the raw type of the given parameterized type as a class.
     * 
     * @param parameterizedType The parameterized type
     * @return The raw type of the parameterized type, as a class
     * @throws IllegalArgumentException If the raw type of the given
     * parameterized type is not a class. According to the reference
     * implementation 
     * (sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl)
     * this will never be the case, but the JLS does not seem to state 
     * this explicitly.
     */
    public static Class<?> getRawType(ParameterizedType parameterizedType)
    {
        Type rawType = parameterizedType.getRawType();
        if (rawType instanceof Class<?>)
        {
            Class<?> rawTypeAsClass = (Class<?>)rawType;
            return rawTypeAsClass;
        }
        throw new IllegalArgumentException(
            "Raw type of "+parameterizedType+" is not a Class: " + rawType);
    }
    
    /**
     * Returns the given type as a Class. This may be the given type itself,
     * or the raw type of the given type, if the given type is a parameterized
     * type and its raw type is a Class.
     * 
     * @param type The input type 
     * @return The type as a class.
     * @throws IllegalArgumentException If the given type neither is a class
     * not a ParameterizedType whose raw type is a class.
     */
    public static Class<?> asClass(Type type)
    {
       if (type instanceof Class<?>)
       {
           return (Class<?>)type;
       }
       else if (isParameterizedType(type))
       {
           ParameterizedType parameterizedType = 
               (ParameterizedType)type;
           return getRawType(parameterizedType);
       }
       throw new IllegalArgumentException(
           "Type "+type+" can not be converted into a Class");
    }
    
    
    /**
     * If the given class has type parameters, a parameterized type
     * with the class as its raw type and these type parameters is 
     * returned. Otherwise, the class itself is returned.
     * 
     * @param c The class
     * @return The parameterized type, or the class itself
     * @throws NullPointerException If the given class is <code>null</code>
     */
    public static Type asParameterizedType(Class<?> c)
    {
        Objects.requireNonNull(c, "The class is null");
        Type typeParameters[] = c.getTypeParameters();
        if (typeParameters.length == 0)
        {
            return c;
        }
        TypeBuilder typeBuilder = create(c);
        for (Type type : typeParameters)
        {
            typeBuilder.withType(type);
        }
        return typeBuilder.build();
    }
    

    /**
     * Creates a string representation of the given type, including bounds 
     * of type variables and other details.
     * 
     * @param type The type
     * @return The string for the type
     */
    public static String stringFor(Type type)
    {
        return TypesToString.stringFor(type);
    }

    /**
     * Creates an elaborate string representation of the given type,
     * including bounds and generic declarations of type variables 
     * and other details. This method is mainly intended for debugging,
     * and should <b>not</b> be considered as part of the public API.
     * 
     * @param type The type
     * @return The string for the type
     */
    public static String debugStringFor(Type type)
    {
        return TypesToString.debugStringFor(type);
    }

    /**
     * Creates an elaborate string representation of the given types,
     * including bounds and generic declarations of type variables 
     * and other details. This method is mainly intended for debugging,
     * and should <b>not</b> be considered as part of the public API.
     * 
     * @param types The types
     * @return The string for the types
     */
    public static String debugStringFor(Type types[])
    {
        return TypesToString.debugStringFor(Arrays.asList(types));
    }
    
    /**
     * Creates an elaborate string representation of the given types,
     * including bounds and generic declarations of type variables 
     * and other details. This method is mainly intended for debugging,
     * and should <b>not</b> be considered as part of the public API.
     * 
     * @param types The types
     * @return The string for the types
     */
    public static String debugStringFor(List<Type> types)
    {
        return TypesToString.debugStringFor(types);
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private Types()
    {
        // Private constructor to prevent instantiation
    }
    
}
