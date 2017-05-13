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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility methods for the conversion of types into strings. Used by the 
 * {@link Types#stringFor(Type)} and {@link Types#debugStringFor(Type)}
 * methods.
 */
class TypesToString
{
    /**
     * Creates a string representation of the given type, including bounds 
     * of type variables and other details.
     * 
     * @param type The type
     * @return The string for the type
     */
    static String stringFor(Type type)
    {
        return stringFor(type, new LinkedHashSet<Type>(), false);
    }

    /**
     * Implementation for {@link #stringFor(Type)} and 
     * {@link #debugStringFor(Type)}
     * 
     * @param type The type to create the string for
     * @param visitedTypeVariables The type variables that have already been
     * visited during the creation of the String. For a type like <code>
     * T extends Comparable&lt;T&gt;</code>, the type variable <code>T</code>
     * has to be stored as 'visited' to prevent the creation of an infinite
     * String like <code>T extends Comparable&lt;T extends 
     * Comparable&lt;T ... </code>
     * @param debug Whether debug information should be included
     * @return The string for the type
     */
    private static String stringFor(
        Type type, Set<Type> visitedTypeVariables, boolean debug)
    {
        if (type == null)
        {
            return "null";
        }
        else if (type instanceof Class)
        {
            Class<?> c = (Class<?>)type;
            return c.getName();
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            return stringForParameterizedType(
                parameterizedType, visitedTypeVariables, debug);
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wildcardType = (WildcardType)type;
            return stringForWildcardType(
                wildcardType, visitedTypeVariables, debug);
        }
        else if (type instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable = (TypeVariable<?>)type;
            return stringForTypeVariable(
                typeVariable, visitedTypeVariables, debug);
        }
        else if (type instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            return stringForGenericArrayType(
                genericArrayType, visitedTypeVariables, debug);
        }
        return String.valueOf(type);
    }


    /**
     * The implementation of {@link #stringFor(Type)} for parameterized types
     * 
     * @param parameterizedType The parameterized type
     * @param visitedTypeVariables The visited type variables
     * @param debug Whether debug information should be included
     * @return The string for the type
     */
    private static String stringForParameterizedType(
        ParameterizedType parameterizedType,
        Set<Type> visitedTypeVariables, boolean debug)
    {
        Type rawType = parameterizedType.getRawType();
        StringBuilder sb = new StringBuilder();
        sb.append(stringFor(rawType, visitedTypeVariables, debug));
        sb.append("<");
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        for (int i=0; i<typeArguments.length; i++)
        {
            Type typeArgument = typeArguments[i];
            sb.append(stringFor(typeArgument, visitedTypeVariables, debug));
            if (i < typeArguments.length - 1)
            {
                sb.append(", ");
            }
        }
        sb.append(">");
        return sb.toString();
    }
    
    
    /**
     * The implementation of {@link #stringFor(Type)} for wildcard types
     * 
     * @param wildcardType The wildcard type
     * @param visitedTypeVariables The visited type variables
     * @param debug Whether debug information should be included
     * @return The string for the type
     */
    private static String stringForWildcardType(WildcardType wildcardType,
        Set<Type> visitedTypeVariables, boolean debug)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        Type lowerBounds[] = wildcardType.getLowerBounds();
        sb.append(createLowerBoundsString(
            lowerBounds, visitedTypeVariables, debug));
        Type upperBounds[] = wildcardType.getUpperBounds();
        sb.append(createUpperBoundsString(
            upperBounds, visitedTypeVariables, debug));
        return sb.toString();
    }
    
    
    /**
     * The implementation of {@link #stringFor(Type)} for type variables
     * 
     * @param typeVariable The type variable
     * @param visitedTypeVariables The visited type variables
     * @param debug Whether debug information should be included
     * @return The string for the type
     */
    private static String stringForTypeVariable(TypeVariable<?> typeVariable,
        Set<Type> visitedTypeVariables, boolean debug)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(typeVariable.getName());
        if (debug)
        {
            sb.append(" (of "+typeVariable.getGenericDeclaration()+")");
        }
        if (!visitedTypeVariables.contains(typeVariable))
        {
            visitedTypeVariables.add(typeVariable);
            sb.append(createUpperBoundsString(
                typeVariable.getBounds(), visitedTypeVariables, debug));
        }
        return sb.toString();
    }
    
    
    /**
     * The implementation of {@link #stringFor(Type)} for generic array types
     * 
     * @param genericArrayType The generic array type
     * @param visitedTypeVariables The visited type variables
     * @param debug Whether debug information should be included
     * @return The string for the type
     */
    private static String stringForGenericArrayType(
        GenericArrayType genericArrayType, Set<Type> visitedTypeVariables,
        boolean debug)
    {
        Type componentType = genericArrayType.getGenericComponentType();
        String componentString = 
            stringFor(componentType, visitedTypeVariables, debug);
        if (debug)
        {
            return "("+componentString+")[]";
        }
        return componentString+"[]";
    }
    
    /**
     * Returns a string for the given lower bounds
     * 
     * @param lowerBounds The lower bounds
     * @param visitedTypeVariables The type variables that have already been
     * visited. See {@link #stringFor(Type, Set, boolean)}.
     * @param debug Whether debug information should be included
     * @return The string
     */
    private static String createLowerBoundsString(
        Type lowerBounds[], Set<Type> visitedTypeVariables, boolean debug)
    {
        return createBoundsString(Arrays.asList(lowerBounds), 
            "super", visitedTypeVariables, debug);
    }

    /**
     * Returns a string for the given upper bounds
     * 
     * @param upperBounds The upper bounds
     * @param visitedTypeVariables The type variables that have already been
     * visited. See {@link #stringFor(Type, Set, boolean)}.
     * @param debug Whether debug information should be included
     * @return The string
     */
    private static String createUpperBoundsString(
        Type upperBounds[], Set<Type> visitedTypeVariables, boolean debug)
    {
        return createBoundsString(boundsWithoutObject(upperBounds), 
            "extends", visitedTypeVariables, debug);
    }
    
    /**
     * Returns a list containing the given types except for the
     * type 'Object.class'
     * 
     * @param upperBounds The input
     * @return The list
     */
    private static List<Type> boundsWithoutObject(Type upperBounds[])
    {
        List<Type> bounds = new ArrayList<Type>();
        for (Type type : upperBounds)
        {
            if (!type.equals(Object.class))
            {
                bounds.add(type);
            }
        }
        return bounds;
    }

    /**
     * Returns a string for the given bounds.
     * 
     * @param bounds The bounds
     * @param relation The relation, either "super" or "extends"
     * @param visitedTypeVariables The type variables that have already been
     * visited. See {@link #stringFor(Type, Set, boolean)}.
     * @param debug Whether debug information should be included
     * @return The string
     */
    private static String createBoundsString(
        List<Type> bounds, String relation, Set<Type> visitedTypeVariables, 
        boolean debug)
    {
        if (!bounds.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            sb.append(" "+relation+" ");
            for (int i=0; i<bounds.size(); i++)
            {
                sb.append(stringFor(
                    bounds.get(i), visitedTypeVariables, debug));
                if (i < bounds.size()-1)
                {
                    sb.append(" & ");
                }
            }
            return sb.toString();
        }
        return "";
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
    static String debugStringFor(Type type)
    {
        return stringFor(type, new LinkedHashSet<Type>(), true);
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
    static String debugStringFor(Type types[])
    {
        return debugStringFor(Arrays.asList(types));
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
    static String debugStringFor(List<Type> types)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<types.size(); i++)
        {
            Type type = types.get(i);
            sb.append(Types.debugStringFor(type));
            if (i < types.size() - 1)
            {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private TypesToString()
    {
        // Private constructor to prevent instantiation
    }

}
