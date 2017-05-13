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
import java.lang.reflect.Type;

/**
 * Default implementation of a GenericArrayType
 */
final class DefaultGenericArrayType implements GenericArrayType
{
    /**
     * The generic component type
     */
    private final Type genericComponentType;
    
    /**
     * Creates a new generic array type with the given generic component type
     * 
     * @param genericComponentType The generic component type
     */
    DefaultGenericArrayType(Type genericComponentType)
    {
        this.genericComponentType = genericComponentType;
    }
    
    @Override
    public Type getGenericComponentType()
    {
        return genericComponentType;
    }
    
    @Override
    public String toString()
    {
        return Types.stringFor(genericComponentType)+"[]";
    }

    @Override
    public int hashCode()
    {
        return genericComponentType.hashCode();
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof GenericArrayType))
            return false;
        GenericArrayType other = (GenericArrayType) object;
        if (genericComponentType == null)
        {
            if (other.getGenericComponentType() != null)
                return false;
        } else if (!genericComponentType.equals(
            other.getGenericComponentType()))
            return false;
        return true;
    }

}
