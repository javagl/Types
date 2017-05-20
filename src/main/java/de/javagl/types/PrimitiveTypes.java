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

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Utility methods related to primitive types and their conversion.<br>
 * <br>
 * Note that <code>void</code> and <code>Void</code> are not considered as 
 * types, according to the Java Language Specification.  
 */
public class PrimitiveTypes
{
    /**
     * The map from primitive types to their boxed counterparts
     */
    private static final Map<Class<?>, Class<?>> unboxedToBoxed;
    static
    {
        unboxedToBoxed = new LinkedHashMap<Class<?>, Class<?>>();
        unboxedToBoxed.put(boolean.class, Boolean.class);
        unboxedToBoxed.put(byte.class, Byte.class);
        unboxedToBoxed.put(char.class, Character.class);
        unboxedToBoxed.put(short.class, Short.class);
        unboxedToBoxed.put(int.class, Integer.class);
        unboxedToBoxed.put(long.class, Long.class);
        unboxedToBoxed.put(float.class, Float.class);
        unboxedToBoxed.put(double.class, Double.class);
    }

    /**
     * The map from boxed primitive types to the primitive types
     */
    private static final Map<Class<?>, Class<?>> boxedToUnboxed;
    static
    {
        boxedToUnboxed = new LinkedHashMap<Class<?>, Class<?>>();
        boxedToUnboxed.put(Boolean.class, boolean.class);
        boxedToUnboxed.put(Byte.class, byte.class);
        boxedToUnboxed.put(Character.class, char.class);
        boxedToUnboxed.put(Short.class, short.class);
        boxedToUnboxed.put(Integer.class, int.class);
        boxedToUnboxed.put(Long.class, long.class);
        boxedToUnboxed.put(Float.class, float.class);
        boxedToUnboxed.put(Double.class, double.class);
    }
    
    /**
     * The map from primitive types to their direct primitive supertypes.
     * 
     * This map is filled according to the JLS, section 4.10.1. "Subtyping 
     * among Primitive Types",
     * <a href=
     * http://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.10.1>
     * http://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.10.1
     * </a>.
     * Note that <code>double</code> and <code>boolean</code> do not have
     * a direct primitive supertype! Also note that <code>void</code> is
     * not a type. 
     */
    private static final Map<Class<?>, Class<?>> directPrimitiveSupertypes;
    static
    {
        directPrimitiveSupertypes = new LinkedHashMap<Class<?>, Class<?>>();
        directPrimitiveSupertypes.put(byte.class, short.class);
        directPrimitiveSupertypes.put(short.class, int.class);
        directPrimitiveSupertypes.put(char.class, int.class);
        directPrimitiveSupertypes.put(int.class, long.class);
        directPrimitiveSupertypes.put(long.class, float.class);
        directPrimitiveSupertypes.put(float.class, double.class);
        
    }
    
    
    /**
     * Returns whether the given string is the name of a primitive
     * type. Note that <code>void</code> is not a type.
     * 
     * @param string The string
     * @return Whether the given string is the name of a primitive type.
     */
    public static boolean isPrimitiveTypeName(String string)
    {
        return 
            "boolean".equals(string) ||
            "byte".equals(string) ||
            "char".equals(string) ||
            "short".equals(string) ||
            "int".equals(string) ||
            "long".equals(string) ||
            "float".equals(string) ||
            "double".equals(string);
    }
    
    /**
     * Returns the primitive type with the given name. 
     * Note that <code>void</code> is not a type.
     * 
     * @param name The name
     * @return The primitive type with the given name.
     * @throws IllegalArgumentException If the given name is not 
     * the name of a primitive type
     */
    public static Class<?> getPrimitiveType(String name)
    {
        if ("boolean".equals(name))
        {
            return boolean.class;
        }
        if ("byte".equals(name))
        {
            return byte.class;
        }
        if ("char".equals(name))
        {
            return char.class;
        }
        if ("short".equals(name))
        {
            return short.class;
        }
        if ("int".equals(name))
        {
            return int.class;
        }
        if ("long".equals(name))
        {
            return long.class;
        }
        if ("float".equals(name))
        {
            return float.class;
        }
        if ("double".equals(name))
        {
            return double.class;
        }
        throw new IllegalArgumentException(
            "Not the name of a primitive type: "+name);
    }

    /**
     * Returns whether the given type is a primitive type,
     * like int.class or boolean.class.
     * Note that <code>void</code> is not a type.
     * 
     * @param type The type
     * @return Whether the type is a primitive type
     */
    public static boolean isPrimitiveType(Type type)
    {
        return unboxedToBoxed.keySet().contains(type);
    }
    
    /**
     * Returns whether the given type is a boxed primitive type,
     * like Integer.class or Boolean.class.
     * Note that <code>Void</code> is not a type.
     * 
     * @param type The type
     * @return Whether the type is a boxed primitive type
     */
    public static boolean isBoxedPrimitiveType(Type type)
    {
        return boxedToUnboxed.keySet().contains(type);
    }
    
    /**
     * Returns the boxed type of the given type. That is, this method
     * returns <code>Boolean.class</code> for a given 
     * <code>boolean.class</code> etc. 
     * Note that <code>void</code> is not a type.
     * 
     * @param type The primitive type
     * @return The boxed type of the given primitive type
     * @throws IllegalArgumentException If the given type is no primitive type 
     */
    public static Class<?> getBoxedType(Type type)
    {
        Class<?> result = unboxedToBoxed.get(type);
        if (result == null)
        {
            throw new IllegalArgumentException(
                "Type is no primitive type: "+type);
        }
        return result;
    }

    /**
     * Returns the unboxed type of the given type. That is, this method
     * returns <code>boolean.class</code> for a given 
     * <code>Boolean.class</code> etc. 
     * Note that <code>Void</code> is not a type.
     * 
     * @param type The boxed primitive type
     * @return The unboxed primitive type
     * @throws IllegalArgumentException If the given type is no boxed primitive 
     * type 
     */
    public static Class<?> getUnboxedType(Type type)
    {
        Class<?> result = boxedToUnboxed.get(type);
        if (result == null)
        {
            throw new IllegalArgumentException(
                "Type is no boxed primitive type: "+type);
        }
        return result;
    }

    /**
     * Returns the direct supertype of the given primitive type. 
     * 
     * The result is the direct supertype according to the JLS, section 4.10.1,
     * "Subtyping among Primitive Types",
     * <a href=
     * http://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.10.1>
     * http://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.10.1
     * </a>
     * <pre><code>
     * double &gt;1 float
     * float &gt;1 long
     * long &gt;1 int
     * int &gt;1 char
     * int &gt;1 short
     * short &gt;1 byte
     * </code></pre>
     * where <code>&gt;1</code> means "is direct supertype of". <br>
     * <br>
     * Note that <code>double</code> and <code>boolean</code> do not have
     * a direct primitive supertype!<br>
     * <br> 
     * Also note that <code>void</code> is not a type.
     * 
     * @param type The input primitive type
     * @return The direct supertype
     * @throws IllegalArgumentException If the given type has no direct
     * supertype (which is the case if the give type is no primitive type,
     * or if the given type is <code>boolean.class</code> or
     * <code>double.class</code>!) 
     */
    public static Class<?> getDirectPrimitiveSupertype(Type type)
    {
        Class<?> result = directPrimitiveSupertypes.get(type);
        if (result == null)
        {
            throw new IllegalArgumentException(
                "Type has no direct primitive supertype: "+type);
        }
        return result;
    }
    
    
    /**
     * Returns whether the type to assign to is assignable from the type to 
     * assign from, including possible boxing/unboxing and widening primitive 
     * conversions. <br>
     * <br>
     * This method assumes that both types are primitive or boxed primitive
     * types. 
     * 
     * @param to The type to assign to
     * @param from The type to assign from
     * @return Whether the types are assignable
     * @throws IllegalArgumentException If the type to assign to
     * or the type to assign from is not a primitive or boxed 
     * primitive type
     */
    public static boolean isPrimitiveAssignableWithAutoboxing(
        Type to, Type from)
    {
        if (!Types.isPrimitive(to) && !isBoxedPrimitiveType(to))
        {
            throw new IllegalArgumentException(
                "Type to assign to is not a primitive or "
                + "boxed primitive type: "+to);
        }
        if (!Types.isPrimitive(from) && !isBoxedPrimitiveType(from))
        {
            throw new IllegalArgumentException(
                "Type to assign from is not a primitive or "
                + "boxed primitive type: "+from);
        }
        
        if (to.equals(from))
        {
            return true;
        }
        
        // If both types are primitive, check the primitive assignability
        if (Types.isPrimitive(to) && Types.isPrimitive(from))
        {
            return isPrimitiveAssignable(to, from);
        }

        // If the type to assign to is primitive, assigning is possible
        // via unboxing, if the type to assign from is the boxed version 
        // of the type to assign to 
        if (Types.isPrimitive(to) && !Types.isPrimitive(from))
        {
            if (isBoxedPrimitiveType(from))
            {
                Class<?> unboxedFrom = PrimitiveTypes.getUnboxedType(from);
                return to.equals(unboxedFrom);
            }
            return false;
        }

        // If the type to assign from is primitive, assigning is possible
        // via boxing, if the type to assign to is the boxed version 
        // of the type to assign from
        if (!Types.isPrimitive(to) && Types.isPrimitive(from))
        {
            if (isBoxedPrimitiveType(to))
            {
                Class<?> unboxedTo = PrimitiveTypes.getUnboxedType(to);
                return unboxedTo.equals(from);
            }
            return false;
        }

        // Both are non-primitive types, so they must be boxed
        // primitive types. But they are not equal, according
        // to the checks done at the beginning
        return false;
    }
    
    /**
     * Returns whether the given primitive type to assign to is assignable 
     * from the given primitive type to assign from. This corresponds to
     * the check for a possible widening primitive conversion, according
     * to the JLS, section 5.1.2, "Widening Primitive Conversion",
     * <a href=
     * http://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.2>
     * http://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.2
     * </a>.
     * 
     * @param to The type to assign to 
     * @param from The type to assign from
     * @return Whether the given primitive type to assign to is assignable 
     * from the given primitive type to assign from.
     * @throws IllegalArgumentException If either the type to assign to or
     * the type to assign from is not a primitive type
     */
    public static boolean isPrimitiveAssignable(Type to, Type from)
    {
        if (!Types.isPrimitive(to))
        {
            throw new IllegalArgumentException(
                "Type "+to+" is no primitive type");
        }
        if (!Types.isPrimitive(from))
        {
            throw new IllegalArgumentException(
                "Type "+from+" is no primitive type");
        }
        Type current = from;
        while (true)
        {
            if (current.equals(to))
            {
                return true;
            }
            if (current.equals(double.class))
            {
                break;
            }
            current = PrimitiveTypes.getDirectPrimitiveSupertype(current);
        }
        return false;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private PrimitiveTypes()
    {
        // Private constructor to prevent instantiation
    }
}
