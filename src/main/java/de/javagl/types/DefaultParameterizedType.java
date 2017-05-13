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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Default implementation of a ParameterizedType
 */
final class DefaultParameterizedType implements ParameterizedType
{
    /**
     * The raw type
     */
    private final Type rawType;
    
    /**
     * The owner type
     */
    private final Type ownerType;
    
    /**
     * The actual type arguments
     */
    private final Type actualTypeArguments[];
    
    /**
     * Creates a new parameterized type. Assumes that the given raw type
     * has the same number of type parameters as the given actual type
     * arguments.
     * 
     * @param rawType The raw type
     * @param ownerType The owner type
     * @param actualTypeArguments The actual type arguments
     */
    DefaultParameterizedType(Type rawType, Type ownerType, 
        Type actualTypeArguments[])
    {
        this.rawType = rawType;
        this.ownerType = ownerType;
        this.actualTypeArguments = actualTypeArguments.clone();
    }
    

    @Override
    public Type getRawType()
    {
        return rawType;
    }

    @Override
    public Type getOwnerType()
    {
        return ownerType;
    }
    
    @Override
    public Type[] getActualTypeArguments()
    {
        return actualTypeArguments.clone();
    }

    @Override
    public String toString()
    {
        return Types.stringFor(this); 
    }

    @Override
    public int hashCode()
    {
        return
            Arrays.hashCode(actualTypeArguments) ^
            (ownerType == null ? 0 : ownerType.hashCode() ) ^
            (rawType == null   ? 0 : rawType.hashCode() );
    }


    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof ParameterizedType))
            return false;
        ParameterizedType other = (ParameterizedType) object;
        if (!Arrays.equals(actualTypeArguments, other.getActualTypeArguments()))
            return false;
        if (ownerType == null)
        {
            if (other.getOwnerType() != null)
                return false;
        } else if (!ownerType.equals(other.getOwnerType()))
            return false;
        if (rawType == null)
        {
            if (other.getRawType() != null)
                return false;
        } else if (!rawType.equals(other.getRawType()))
            return false;
        return true;
    }
    

}
