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
 * Interface for classes that can parse types from strings.
 */
interface TypeParser
{
    /**
     * Add the given <code><b>import</b></code> to this parser. 
     * For example <code>"java.util.*"</code> or
     * <code>"java.awt.Component"</code> 
     * 
     * @param importString The import string
     * @return The instance
     * @throws IllegalArgumentException If the given string is not 
     * a valid import declaration, or collides with another import
     */
    TypeParser addImport(String importString);

    /**
     * Add the given string as the name of a type variable. For a String 
     * like <code>List&lt;Foo&gt;</code>, the name <code>Foo</code> will 
     * by default be considered as a class name, and the parser will try 
     * to resolve this class, unless the name <code>Foo</code> was 
     * declared as the name of a type variable using this method.
     * 
     * @param typeVariableName The type variable name
     * @return The instance
     * @throws IllegalArgumentException If the given variable name is not
     * a valid Java identifier
     */
    TypeParser addTypeVariableName(String typeVariableName);

    /**
     * Parse a Type instance from the given String. The given String
     * must have the following structure:
     * <code><pre>
     * Type := 
     *     <i>TypeName</i> |
     *     PrimitiveTypeName |
     *     ParameterizedType
     *     Type[]
     * PrimitiveTypeName :=
     *     void
     *     boolean
     *     byte
     *     char
     *     short
     *     int
     *     long 
     *     float 
     *     double
     * ParameterizedType :=
     *     <i>TypeName</i>&lt;?&gt; |
     *     <i>TypeName</i>&lt;TypeParameter&gt; |
     *     <i>TypeName</i>&lt;TypeParameter, TypeParameter ... &gt; |
     * TypeParameter :=
     *     Type |
     *     WildcardType |
     *     TypeVariable
     * WildcardType :=
     *     ? |
     *     ? extends Type |
     *     ? super Type
     * TypeVariable := 
     *     <i>TypeVariableName</i> |
     *     <i>TypeVariableName</i> extends Types
     * Types :=
     *     Type |
     *     Type &amp; Types
     * </pre></code>    
     * <br>
     * <br>
     * <code><i>TypeName</i></code> must be 
     * <ul>
     *   <li>
     *     a fully qualified class name, like <code>java.util.List</code> OR
     *   </li> 
     *   <li>
     *     the name of a class in one of the packages added with the 
     *     {@link #addImport(String)} method.
     *   </li> OR
     *   <li>
     *     the name of an array type, according to {@link Class#getName()}
     *   </li>
     * </ul>
     * <br>
     * <code><i>TypeVariableName</i></code> must be the name of a type variable 
     * that was registered with {@link #addTypeVariableName(String)}. <br>
     * <br>
     * 
     * @param string The input string
     * @return The Type
     * @throws ClassNotFoundException If a class is specified that
     * can not be loaded
     * @throws ClassNotFoundException If the given string contains
     * a class name that can not be resolved to a class.
     * @throws IllegalArgumentException If the number of parameters
     * given for a ParameterizedType is wrong, or if a class name
     * can not resolved due to ambiguities. Note that this method may also
     * throw an IllegalArgumentException if the format of the input
     * string is invalid, but rigorous checking is not guaranteed.
     */
    Type parse(String string) throws ClassNotFoundException;

}