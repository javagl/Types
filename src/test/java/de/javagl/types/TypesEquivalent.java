package de.javagl.types;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * Utility class solely required for the tests: Checks whether two types are 
 * "equivalent" - which means that they are structurally equal, but don't 
 * rely on the identity of TypeVariables.
 */
@SuppressWarnings("javadoc")
class TypesEquivalent
{
    static boolean areEquivalent(Type type0, Type type1)
    {
        if (type0 == null)
        {
            return type1 == null;
        }
        if (type1 == null)
        {
            return false;
        }
        if (type0 instanceof Class)
        {
            Class<?> class0 = (Class<?>)type0;
            return isEquivalentToClass(class0, type1);
        }
        else if (type0 instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType0 = (ParameterizedType)type0;
            return isEquivalentToParameterizedType(parameterizedType0, type1);
        }
        else if (type0 instanceof WildcardType)
        {
            WildcardType wildcardType0 = (WildcardType)type0;
            return isEquivalentToWildcardType(wildcardType0, type1);
        }
        else if (type0 instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable0 = (TypeVariable<?>)type0;
            return isEquivalentToTypeVariable(typeVariable0, type1);
        }
        else if (type0 instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType0 = (GenericArrayType)type0;
            return isEquivalentToGenericArrayType(genericArrayType0, type1);
        }
        throw new IllegalArgumentException("Unknown type: "+type0);
    }
    
    private static boolean isEquivalentToClass(
        Class<?> class0, Type type1)
    {
        if (type1 instanceof Class)
        {
            Class<?> class1 = (Class<?>)type1;
            return class0.equals(class1);
        }
        else if (type1 instanceof ParameterizedType)
        {
            return false;
        }
        else if (type1 instanceof WildcardType)
        {
            return false;
        }
        else if (type1 instanceof TypeVariable<?>)
        {
            return false;
        }
        else if (type1 instanceof GenericArrayType)
        {
            return false;
        }
        throw new IllegalArgumentException("Unknown type: "+type1);
    }
    
    
    
    private static boolean isEquivalentToParameterizedType(
        ParameterizedType parameterizedType0, Type type1)
    {
        if (type1 instanceof Class)
        {
            return false;
        }
        else if (type1 instanceof ParameterizedType)
        {
            ParameterizedType parameterizedType1 = (ParameterizedType)type1;
            Type rawType0 = parameterizedType0.getRawType();
            Type rawType1 = parameterizedType1.getRawType();
            if (!areEquivalent(rawType0, rawType1))
            {
                return false;
            }
            Type typeArguments0[] = parameterizedType0.getActualTypeArguments();
            Type typeArguments1[] = parameterizedType1.getActualTypeArguments();
            if (!allEquivalent(typeArguments0, typeArguments1))
            {
                return false;  
            }
            Type ownerType0 = parameterizedType0.getOwnerType();
            Type ownerType1 = parameterizedType1.getOwnerType();
            return areEquivalent(ownerType0, ownerType1);
        }
        else if (type1 instanceof WildcardType)
        {
            return false;
        }
        else if (type1 instanceof TypeVariable<?>)
        {
            return false;
        }
        else if (type1 instanceof GenericArrayType)
        {
            return false;
        }
        throw new IllegalArgumentException("Unknown type: "+type1);
    }

    
    
    
    private static boolean isEquivalentToWildcardType(
        WildcardType wildcardType0, Type type1)
    {
        if (type1 instanceof Class)
        {
            return false;
        }
        else if (type1 instanceof ParameterizedType)
        {
            return false;
        }
        else if (type1 instanceof WildcardType)
        {
            WildcardType wildcardType1 = (WildcardType)type1;
            Type lowerBounds0[] = wildcardType0.getLowerBounds();
            Type lowerBounds1[] = wildcardType1.getLowerBounds();
            if (!allEquivalent(lowerBounds0, lowerBounds1))
            {
                return false;
            }
            Type upperBounds0[] = wildcardType0.getUpperBounds();
            Type upperBounds1[] = wildcardType1.getUpperBounds();
            if (!allEquivalent(upperBounds0, upperBounds1))
            {
                return false;
            }
            return true;
        }
        else if (type1 instanceof TypeVariable<?>)
        {
            return false;
        }
        else if (type1 instanceof GenericArrayType)
        {
            return false;
        }
        throw new IllegalArgumentException("Unknown type: "+type1);
    }
    

    
    private static boolean isEquivalentToTypeVariable(
        TypeVariable<?> typeVariable0, Type type1)
    {
        if (type1 instanceof Class)
        {
            return false;
        }
        else if (type1 instanceof ParameterizedType)
        {
            return false;
        }
        else if (type1 instanceof WildcardType)
        {
            return false;
        }
        else if (type1 instanceof TypeVariable<?>)
        {
            TypeVariable<?> typeVariable1 = (TypeVariable<?>)type1;
            Type bounds0[] = typeVariable0.getBounds();
            Type bounds1[] = typeVariable1.getBounds();
            if (!allEquivalent(bounds0, bounds1))
            {
                return false;
            }
            return true;
        }
        else if (type1 instanceof GenericArrayType)
        {
            return false;
        }
        throw new IllegalArgumentException("Unknown type: "+type1);
    }
    
    
    
    private static boolean isEquivalentToGenericArrayType(
        GenericArrayType genericArrayType0, Type type1)
    {
        if (type1 instanceof Class)
        {
            return false;
        }
        else if (type1 instanceof ParameterizedType)
        {
            return false;
        }
        else if (type1 instanceof WildcardType)
        {
            return false;
        }
        else if (type1 instanceof TypeVariable<?>)
        {
            return false;
        }
        else if (type1 instanceof GenericArrayType)
        {
            GenericArrayType genericArrayType1 = (GenericArrayType)type1;
            Type componentType0 = genericArrayType0.getGenericComponentType();
            Type componentType1 = genericArrayType1.getGenericComponentType();
            return areEquivalent(componentType0, componentType1);
        }
        throw new IllegalArgumentException("Unknown type: "+type1);
    }

    private static boolean allEquivalent(Type types0[], Type types1[])
    {
        if (types0.length != types1.length)
        {
            return false;
        }
        for (int i=0; i<types0.length; i++)
        {
            if (!areEquivalent(types0[i], types1[i]))
            {
                return false;
            }
        }
        return true;
    }
}
