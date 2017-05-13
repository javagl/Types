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

/**
 * Default implementation of a {@link TypeAssignabilityTester}
 */
class DefaultTypeAssignabilityTester implements TypeAssignabilityTester
{
    /**
     * The {@link TypeVariableMapping}
     */
    private final TypeVariableMapping typeVariableMapping;

    /**
     * Create a new instance
     */
    DefaultTypeAssignabilityTester()
    {
        this(TypeVariableMappings.create());
    }

    /**
     * Create a new instance using the given {@link TypeVariableMapping}
     * 
     * @param typeVariableMapping The {@link TypeVariableMapping}
     */
    DefaultTypeAssignabilityTester(TypeVariableMapping typeVariableMapping)
    {
        this.typeVariableMapping = typeVariableMapping;
    }
    
    @Override
    public boolean isAssignable(Type to, Type from)
    {
        if (to.equals(from))
        {
            return true;
        }
        
        // If both types are (non-boxed) primitive types, delegate to the 
        // primitive assignabilty tests
        if (Types.isPrimitive(to) && Types.isPrimitive(from)) 
        {
            return PrimitiveTypes.isPrimitiveAssignable(to, from);
        }

        // If both types are (possibly boxed) primitive types, delegate to 
        // the primitive assignabilty tests with autoboxing
        boolean toPrimitive =
            Types.isPrimitive(to) || 
            PrimitiveTypes.isBoxedPrimitiveType(to);
        boolean fromPrimitive =
            Types.isPrimitive(from) || 
            PrimitiveTypes.isBoxedPrimitiveType(from);
        if (toPrimitive && fromPrimitive) 
        {
            return PrimitiveTypes.isPrimitiveAssignableWithAutoboxing(to, from);
        }
        
        // If the type to assign from is a real (non-boxed) primitive
        // type, then check whether its boxed version is assignable
        // to the type to assign to.
        if (Types.isPrimitive(from))
        {
            Type boxedFrom = PrimitiveTypes.getBoxedType(from);
            return isAssignable(to, boxedFrom);
        }

        // Treat all combinations of type types
        if (to instanceof Class<?>)
        {
            Class<?> toClass = (Class<?>)to;
            return isAssignableToClass(toClass, from);
        }     
        else if (to instanceof ParameterizedType)
        {
            ParameterizedType toParameterizedType = (ParameterizedType)to;
            return isAssignableToParameterizedType(toParameterizedType, from);
        }
        else if (to instanceof WildcardType)
        {
            WildcardType toWildcardType = (WildcardType)to;
            return isAssignableToWildcardType(toWildcardType, from);
        }
        else if (to instanceof TypeVariable<?>)
        {
            TypeVariable<?> toTypeVariable = (TypeVariable<?>)to;
            return isAssignableToTypeVariable(toTypeVariable, from);
        }
        else if (to instanceof GenericArrayType)
        {
            GenericArrayType toGenericArrayType = (GenericArrayType)to;
            return isAssignableToGenericArrayType(toGenericArrayType, from);
        }
        throw new IllegalArgumentException("Unknown to-type: "+to);
    }

    /**
     * Returns whether the given class is assignable from the given type
     * 
     * @param toClass The class to assign to
     * @param from The type to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToClass(Class<?> toClass, Type from)
    {
        if (from instanceof Class<?>)
        {
            Class<?> fromClass = (Class<?>)from;
            return toClass.isAssignableFrom(fromClass);
        }
        else if (from instanceof ParameterizedType)
        {
            ParameterizedType fromParameterizedType = (ParameterizedType)from;
            Class<?> fromClass = Types.getRawType(fromParameterizedType);
            return toClass.isAssignableFrom(fromClass);
        }
        else if (from instanceof WildcardType)
        {
            WildcardType fromWildcardType = (WildcardType)from;
            Type upperBounds[] = fromWildcardType.getUpperBounds();
            return anyAssignable(toClass, upperBounds);
        }
        else if (from instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariable = (TypeVariable<?>)from;
            Type upperBounds[] = fromTypeVariable.getBounds();
            return anyAssignable(toClass, upperBounds);
        }
        else if (from instanceof GenericArrayType)
        {
            GenericArrayType fromGenericArrayType = (GenericArrayType)from;
            return isAssignableToClassFromGenericArrayType(
                toClass, fromGenericArrayType);
        }
        throw new IllegalArgumentException("Unknown from-type: "+from);
    }
    

    /**
     * Return whether the given class is assignable from the given generic
     * array type.
     * 
     * @param toClass The class to assign to 
     * @param fromGenericArrayType The generic array type to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToClassFromGenericArrayType(
        Class<?> toClass, GenericArrayType fromGenericArrayType)
    {
        if (toClass.isArray())
        {
            Class<?> toComponentType = toClass.getComponentType();
            Type fromComponentType = 
                fromGenericArrayType.getGenericComponentType();
            return isAssignable(toComponentType, fromComponentType);
        }
        else if (toClass.equals(Object.class))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns whether the given parameterized type is assignable from
     * the given type
     * 
     * @param toParameterizedType The parameterized type to assign to
     * @param from The type to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToParameterizedType(
        ParameterizedType toParameterizedType, Type from)
    {
        if (from instanceof Class<?>)
        {
            Class<?> fromClass = (Class<?>)from;
            return isAssignableToParameterizedTypeFromClass(
                toParameterizedType, fromClass);
        }
        else if (from instanceof ParameterizedType)
        {
            ParameterizedType fromParameterizedType = (ParameterizedType)from;
            return isAssignableToParameterizedTypeFromParameterizedType(
                toParameterizedType, fromParameterizedType);
        }
        else if (from instanceof WildcardType)
        {
            WildcardType fromWildcardType = (WildcardType)from;
            Type upperBounds[] = fromWildcardType.getUpperBounds();
            return anyAssignable(toParameterizedType, upperBounds);
        }
        else if (from instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariable = (TypeVariable<?>)from;
            Type upperBounds[] = fromTypeVariable.getBounds();
            return anyAssignable(toParameterizedType, upperBounds);
        }
        else if (from instanceof GenericArrayType)
        {
            return false;
        }
        throw new IllegalArgumentException("Unknown from-type: "+from);
    }

    /**
     * Returns whether the given parameterized type is assignable from
     * the given class
     * 
     * @param toParameterizedType The parameterized type to assign to
     * @param fromClass The class to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToParameterizedTypeFromClass(
        ParameterizedType toParameterizedType,
        Class<?> fromClass)
    {
        Type fromSuperclass = fromClass.getGenericSuperclass();
        if (fromSuperclass == null)
        {
            Type toRawType = toParameterizedType.getRawType();
            return isAssignable(toRawType, fromClass);
        }
        if (isAssignable(toParameterizedType, fromSuperclass))
        {
            return true;
        }
        Class<?>[] fromInterfaces = fromClass.getInterfaces();
        for (Class<?> fromInterface : fromInterfaces)
        {
            if (isAssignable(toParameterizedType, fromInterface))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given parameterized type is assignable from
     * the given parameterized type
     * 
     * @param toParameterizedType The parameterized type to assign to
     * @param fromParameterizedType The parameterized type to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToParameterizedTypeFromParameterizedType(
        ParameterizedType toParameterizedType, 
        ParameterizedType fromParameterizedType)
    {
        Type toRawType = toParameterizedType.getRawType();
        Type fromRawType = fromParameterizedType.getRawType();
        if (!isAssignable(toRawType, fromRawType))
        {
            return false;
        }
        Type toTypeArguments[] = 
            toParameterizedType.getActualTypeArguments();
        Type fromTypeArguments[] = 
            fromParameterizedType.getActualTypeArguments();
        for (int i=0; i<toTypeArguments.length; i++)
        {
            Type toTypeArgument = toTypeArguments[i];
            Type fromTypeArgument = fromTypeArguments[i];
            if (!isMatchingTypeArgument(toTypeArgument, fromTypeArgument))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the given type argument of a type to assign to matches
     * the type argument of the type to assign from. This differs from normal
     * assignability checks, because a <code>List&lt;Number&gt;</code> is not 
     * assignable from a <code>List&lt;Integer&gt;</code>, although the type 
     * argument <code>Number</code> is assignable from <code>Integer</code>. 
     * Instead,
     * <ul> 
     *   <li>
     *     the type arguments either have to be equal OR
     *   </li>
     *   <li>
     *     the toTypeArgument must be a wildcard type that is assignable
     *     from the fromTypeArgument
     *   </li>
     * </ul>
     * 
     * @param toTypeArgument The type argument of the type to assign to
     * @param fromTypeArgument The type argument of the type to assign from
     * @return Whether the type arguments are matching
     */
    // Package-private for tests
    boolean isMatchingTypeArgument(
        Type toTypeArgument, Type fromTypeArgument)
    {
        if (toTypeArgument.equals(fromTypeArgument))
        {
            return true;
        }
        
        // Treat the case that either of the types is a type variable
        if (toTypeArgument instanceof TypeVariable<?>)
        {
            TypeVariable<?> toTypeVariableArgument = 
                (TypeVariable<?>)toTypeArgument;
            
            //System.out.println("Test for match "+toTypeVariableArgument);
            Type typeForTypeVariableArgument = 
                typeVariableMapping.get(toTypeVariableArgument);
            if (typeForTypeVariableArgument == null)
            {
                return true;
            }
            else
            {
                return isMatchingTypeArgument(
                    typeForTypeVariableArgument, fromTypeArgument);
            }
            
//            if (toTypeVariableArgument.equals(fromTypeArgument))
//            {
//                return true;
//            }
//            if (fromTypeArgument instanceof WildcardType)
//            {
//                return false;
//            }
//            return false;
        }
        if (fromTypeArgument instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariableArgument = 
                (TypeVariable<?>)fromTypeArgument;
            if (toTypeArgument.equals(fromTypeVariableArgument))
            {
                return true;
            }
            if (toTypeArgument instanceof WildcardType)
            {
                WildcardType toWildcardTypeArgument = (WildcardType)toTypeArgument;
                return isMatchingFromTypeVariableToWildcardTypeArgument(
                    toWildcardTypeArgument, fromTypeVariableArgument);
            }
            return false;
        }

        // Treat the other combination of type types
        if (toTypeArgument instanceof Class<?>)
        {
            // Equality was already checked above.
            return false;
        }
        else if (toTypeArgument instanceof ParameterizedType)
        {
            // Equality was already checked above.
            return false;
        }
        else if (toTypeArgument instanceof WildcardType)
        {
            WildcardType toWildcardTypeArgument = (WildcardType)toTypeArgument;
            return isAssignableToWildcardType(
                toWildcardTypeArgument, fromTypeArgument);
        }
        else if (toTypeArgument instanceof TypeVariable<?>)
        {
            return false;
        }
        else if (toTypeArgument instanceof GenericArrayType)
        {
            // Equality was already checked above 
            return false;
        }
        throw new IllegalArgumentException(
            "Unknown to-type argument: "+toTypeArgument);
    }

    /**
     * Returns whether the given wildcard type argument is matching
     * for being assigned from the given type variable argument
     * 
     * @param toWildcardTypeArgument The wildard type argument
     * @param fromTypeVariableArgument The type variable argument
     * @return Whether the type is matching
     */
    private boolean isMatchingFromTypeVariableToWildcardTypeArgument(
        WildcardType toWildcardTypeArgument,
        TypeVariable<?> fromTypeVariableArgument)
    {
        Type toLowerBounds[] = toWildcardTypeArgument.getLowerBounds();

        Type toUpperBounds[] = toWildcardTypeArgument.getUpperBounds();
        
        if (toLowerBounds.length != 0)
        {
            return false;
        }
        Type[] fromUpperBounds = fromTypeVariableArgument.getBounds();
        for (Type toUpperBound : toUpperBounds)
        {
            if (anyAssignable(toUpperBound, fromUpperBounds))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given wildcard type is assignable from the
     * given type.
     * 
     * @param toWildcardType The wildcard type to assign to
     * @param from The type to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToWildcardType(
        WildcardType toWildcardType, Type from)
    {
        Type toUpperBounds[] = toWildcardType.getUpperBounds();
        for (Type toUpperBound : toUpperBounds)
        {
            if (!isWildcardWithUpperBoundAssignable(toUpperBound, from))
            {
                return false;
            }
        }

        Type toLowerBounds[] = toWildcardType.getLowerBounds();
        for (Type toLowerBound : toLowerBounds)
        {
            if (!isWildcardWithLowerBoundAssignable(toLowerBound, from))
            {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Checks whether a wildcard with the given upper bound is assignable
     * from the given type. A wildcard with an upper bound A is assignable 
     * if the type to assign from is a subtype of A, or a wildcard 
     * with an upper bound that is a subtype of A, or a type variable
     * with a (upper) bound that is a subtype of A.
     * 
     * @param toUpperBound The upper bound of the wildcard
     * @param from The type to assign from
     * @return Whether the type is assignable
     */
    private boolean isWildcardWithUpperBoundAssignable(
        Type toUpperBound, Type from)
    {
        if (from instanceof Class<?>)
        {
            Class<?> fromClass = (Class<?>)from;
            return isAssignable(toUpperBound, fromClass);
        }
        else if (from instanceof ParameterizedType)
        {
            ParameterizedType fromParameterizedType = (ParameterizedType)from;
            return isAssignable(toUpperBound, fromParameterizedType);
        }
        else if (from instanceof WildcardType)
        {
            WildcardType fromWildcardType = (WildcardType)from;
            Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
            return anyAssignable(toUpperBound, fromUpperBounds);
        }
        else if (from instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariable = (TypeVariable<?>)from;
            Type[] fromUpperBounds = fromTypeVariable.getBounds();
            return anyAssignable(toUpperBound, fromUpperBounds);
        }
        else if (from instanceof GenericArrayType)
        {
            GenericArrayType fromGenericArrayType = (GenericArrayType)from;
            return isAssignable(toUpperBound, fromGenericArrayType);
        }
        throw new IllegalArgumentException("Unknown from-type: "+from);
    }

    /**
     * Checks whether a wildcard with the given lower bound is assignable
     * from the given type. A wildcard with a lower bound A is assignable if 
     * the type to assign from is a supertype of A, or a wildcard 
     * with a lower bound that is a supertype of A, or a type variable
     * with a (upper) bound that is a supertype of A.
     * 
     * @param toLowerBound The lower bound of the wildcard
     * @param from The type to assign from
     * @return Whether the type is assignable
     */
    private boolean isWildcardWithLowerBoundAssignable(
        Type toLowerBound, Type from)
    {
        if (from instanceof Class<?>)
        {
            Class<?> fromClass = (Class<?>)from;
            return isAssignable(fromClass, toLowerBound);
        }
        else if (from instanceof ParameterizedType)
        {
            ParameterizedType fromParameterizedType = (ParameterizedType)from;
            return isAssignable(fromParameterizedType, toLowerBound);
        }
        else if (from instanceof WildcardType)
        {
            WildcardType fromWildcardType = (WildcardType)from;
            Type[] fromLowerBounds = fromWildcardType.getLowerBounds();
            return anyAssignable(fromLowerBounds, toLowerBound);
        }
        else if (from instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariable = (TypeVariable<?>)from;
            Type[] fromUpperBounds = fromTypeVariable.getBounds();
            return anyAssignable(fromUpperBounds, toLowerBound);
        }
        else if (from instanceof GenericArrayType)
        {
            GenericArrayType fromGenericArrayType = (GenericArrayType)from;
            return isAssignable(fromGenericArrayType, toLowerBound);
        }
        throw new IllegalArgumentException("Unknown from-type: "+from);
    }

    /**
     * Returns whether the given type variable may be assigned from the
     * given type to assign from
     * 
     * @param toTypeVariable The type variable to assign to
     * @param from The type variable to assign from
     * @return Whether the type is assignable
     */
    private boolean isAssignableToTypeVariable(
        TypeVariable<?> toTypeVariable, Type from)
    {
        Type toMappedType = typeVariableMapping.get(toTypeVariable);
        //System.out.println("Check typeMapping for "+toTypeVariable);
        //System.out.println("   which is mapped to "+toMappedType);
        
        if (toMappedType == null)
        {
            return true;
        }
        if (from instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariable = (TypeVariable<?>)from;
            Type fromMappedType = typeVariableMapping.get(fromTypeVariable);
            if (fromMappedType == null)
            {
                return true;
            }
        }
        return isAssignable(toMappedType, from);
    }


    /**
     * Returns whether the given generic array type is assignable from
     * the given type
     * 
     * @param toGenericArrayType The type to assign to
     * @param from The type to assign from
     * @return Whether the types are assignable
     */
    private boolean isAssignableToGenericArrayType(
        GenericArrayType toGenericArrayType, Type from)
    {
        if (from instanceof Class<?>)
        {
            return false;
        }
        else if (from instanceof ParameterizedType)
        {
            return false;
        }
        else if (from instanceof WildcardType)
        {
            WildcardType fromWildcardType = (WildcardType)from;
            Type[] fromUpperBounds = fromWildcardType.getUpperBounds();
            return anyAssignable(toGenericArrayType, fromUpperBounds);
        }
        else if (from instanceof TypeVariable<?>)
        {
            TypeVariable<?> fromTypeVariable = (TypeVariable<?>)from;
            Type[] fromUpperBounds = fromTypeVariable.getBounds();
            return anyAssignable(toGenericArrayType, fromUpperBounds);
        }
        else if (from instanceof GenericArrayType)
        {
            GenericArrayType fromGenericArrayType = (GenericArrayType)from;
            Type toComponentType = 
                toGenericArrayType.getGenericComponentType();
            Type fromComponentType = 
                fromGenericArrayType.getGenericComponentType();
            return isAssignable(toComponentType, fromComponentType);
        }
        throw new IllegalArgumentException("Unknown from-type: "+from);
    }

    /**
     * Returns whether all of the given 'to' types are assignable
     * from the given 'from' type. 
     * 
     * @param tos The types to assign to
     * @param from The type to assign from
     * @return Whether all types are assignable
     */
    boolean allAssignable(Type tos[], Type from)
    {
        for (Type to : tos)
        {
            if (!isAssignable(to, from))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether any of the given 'from' types is assignable
     * to the given 'to' type. 
     * 
     * @param to The type to assign to
     * @param froms The types to assign from
     * @return Whether any type is assignable
     */
    private boolean anyAssignable(Type to, Type froms[])
    {
        for (Type from : froms)
        {
            if (isAssignable(to, from))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether any of the given 'to' types is assignable
     * from the given 'from' type. 
     * 
     * @param tos The types to assign to
     * @param from The type to assign from
     * @return Whether any type is assignable
     */
    private boolean anyAssignable(Type tos[], Type from)
    {
        for (Type to : tos)
        {
            if (isAssignable(to, from))
            {
                return true;
            }
        }
        return false;
    }

}









