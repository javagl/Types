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

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;

/**
 * A builder for TypeVariable instances. Type variables may be added
 * by calling {@link #add(String, Type...)}. These type variables
 * will share a common generic declaration. This generic declaration
 * (that contains all these type variables) may then be obtained via
 * the {@link #build()} method.  
 */
public interface TypeVariableBuilder
{
    /**
     * Add the given type variable with the given name and upper bounds. 
     * This type variable will share the same generic declaration with all
     * type variables that are created with this builder.
     * If no bounds are given, then <code>Object.class</code> will be used
     * as the implicit bound.
     * 
     * @param variableName The variable name
     * @param bounds The bounds of the type variable
     * @return This builder
     * @throws NullPointerException If the given variableName is 
     * <code>null</code>
     * @throws IllegalArgumentException If a variable with the
     * given name was already defined
     */
    TypeVariableBuilder add(String variableName, Type ... bounds);
    
    /**
     * Build the generic declaration that contains the type variables
     * that have been built with this instance.
     * 
     * @return The GenericDeclaration containing the type variables
     */
    GenericDeclaration build();
}