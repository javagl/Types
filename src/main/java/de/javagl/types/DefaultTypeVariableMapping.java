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
import java.lang.reflect.TypeVariable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Default implementation of a {@link TypeVariableMapping}
 */
class DefaultTypeVariableMapping implements TypeVariableMapping
{
    /**
     * The map that stores the mapping
     */
    private final Map<TypeVariable<?>, Type> map =
        new LinkedHashMap<TypeVariable<?>, Type>();
    
    @Override
    public void put(TypeVariable<?> typeVariable, Type type)
    {
        if (typeVariable.equals(type))
        {
            return;
        }
        map.put(typeVariable, type);
    }

    @Override
    public Type get(TypeVariable<?> typeVariable)
    {
        Type t = map.get(typeVariable);
        if (t instanceof TypeVariable<?>)
        {
            TypeVariable<?> u = (TypeVariable<?>)t;
            return get(u); 
        }
        return t;
    }
    
}