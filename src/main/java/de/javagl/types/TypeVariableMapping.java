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

/**
 * Interface for mappings from type variables to types
 */
interface TypeVariableMapping
{
    /**
     * Map the given type variable to the given type. If the given type is
     * equal to the given type variable, then this call will have no effect.
     * 
     * @param typeVariable The type variable
     * @param type The type
     */
    void put(TypeVariable<?> typeVariable, Type type);
    
    /**
     * Return the type that the given type variable is mapped to, or
     * <code>null</code> if the type variable is not mapped to any
     * type. This method will perform recursive lookups: If a type
     * variable <code>T</code> is mapped to a type variable <code>U</code>,
     * and <code>U</code> is mapped to <code>String.class</code>, then this
     * method will return <code>String.class</code> when looking up the
     * mapping of <code>T</code>.
     * 
     * @param typeVariable The type variable
     * @return The type that the variable is mapped to
     */
    Type get(TypeVariable<?> typeVariable);
}
