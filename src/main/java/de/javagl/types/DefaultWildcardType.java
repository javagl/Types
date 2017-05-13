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
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * Default implementation of a WildcardType
 */
final class DefaultWildcardType implements WildcardType
{
    /**
     * The lower bounds
     */
    private final Type lowerBounds[];

    /**
     * The upper bounds
     */
    private final Type upperBounds[];
    
    
    /**
     * Creates a new wildcard type with the given upper and lower bounds.
     * If the lower bounds are <code>null</code>, then an empty array will
     * be stored. If the upper bounds are <code>null</code> or an empty
     * array, then an array containing the implicit upper bound
     * <code>java.lang.Object</code> will be stored.
     * 
     * @param lowerBounds the lower bounds
     * @param upperBounds the upper bounds
     */
    DefaultWildcardType(Type lowerBounds[], Type upperBounds[])
    {
        if (lowerBounds == null)
        {
            this.lowerBounds = new Type[0];
        }
        else
        {
            this.lowerBounds = lowerBounds.clone();
        }
        if (upperBounds == null || upperBounds.length == 0)
        {
            this.upperBounds = new Type[] { Object.class };
        }
        else
        {
            this.upperBounds = upperBounds.clone();
        }
        
    }

    @Override
    public Type[] getUpperBounds()
    {
        return upperBounds.clone();
    }

    @Override
    public Type[] getLowerBounds()
    {
        return lowerBounds.clone();
    }
    
    @Override
    public String toString()
    {
        return Types.stringFor(this);
    }

    @Override
    public int hashCode()
    {
        Type lowerBounds[] = getLowerBounds();
        Type upperBounds[] = getUpperBounds();
        return Arrays.hashCode(lowerBounds) ^ Arrays.hashCode(upperBounds);        
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (!(object instanceof WildcardType))
            return false;
        WildcardType other = (WildcardType) object;
        if (!Arrays.equals(lowerBounds, other.getLowerBounds()))
            return false;
        if (!Arrays.equals(upperBounds, other.getUpperBounds()))
            return false;
        return true;
    }

}
