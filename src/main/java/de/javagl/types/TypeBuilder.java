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

/**
 * Interface of a builder for Type instances. Specifically, this class
 * can be used to build instances for parameterized types, like
 * <code>Map&lt;String, ? super Integer&gt;</code>. A {@link TypeBuilder} 
 * may be created with {@link Types#create(Type)}, and parameters may
 * be added fluently:
 * <pre><code>
 * // Build the type "List&lt;? extends String&gt;" :
 * Type type = Types.create(List.class).withSubtypeOf(String.class).build()
 * </code></pre>
 */
public interface TypeBuilder
{
    /**
     * Adds the type argument for a subtype the given Type to 
     * the Type that is currently being built. The type parameter
     * will be a wildcard type with the given type as its upper
     * bound. This corresponds to a generic parameter of the form 
     * <code>&lt;? extends TheType&gt;</code>
     * 
     * @param type The type
     * @return This Builder
     */
    TypeBuilder withSubtypeOf(Type type);
    
    /**
     * Adds the type argument for the given Type to the 
     * Type that is currently being built. This corresponds to
     * a generic parameter of the form 
     * <code>&lt;TheType&gt;</code>
     * 
     * @param type The type
     * @return This Builder
     */
    TypeBuilder withType(Type type);

    /**
     * Adds the type argument for a supertype the given Type to 
     * the Type that is currently being built. The type parameter
     * will be a wildcard type with the given type as its lower
     * bound. This corresponds to a generic parameter of the form 
     * <code>&lt;? super TheType&gt;</code>
     * 
     * @param type The type
     * @return This Builder
     */
    TypeBuilder withSupertypeOf(Type type);

    /**
     * Build the current Type. 
     * 
     * @return The Type
     * @throws IllegalStateException If the number of specified types
     * does not match the number of type parameters for the raw type
     * of the Type.
     */
    Type build();
}