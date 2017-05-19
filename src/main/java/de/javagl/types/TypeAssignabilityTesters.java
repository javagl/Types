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

/**
 * Methods to create {@link TypeAssignabilityTester} instances
 */
public class TypeAssignabilityTesters
{
    /**
     * Create a new, default {@link TypeAssignabilityTester}
     * 
     * @return The {@link TypeAssignabilityTester}
     */
    public static TypeAssignabilityTester create()
    {
        return new DefaultTypeAssignabilityTester(
            TypeVariableMappings.create());
    }

    /**
     * Create a new {@link TypeAssignabilityTester} that assumes that 
     * type variables may be assigned any value (so they are assumed
     * to be unbound).
     * 
     * @return The {@link TypeAssignabilityTester}
     */
    public static TypeAssignabilityTester createForFreeTypeVariables()
    {
        return new DefaultTypeAssignabilityTester(
            TypeVariableMappings.create(), true);
    }
    
    /**
     * Create a new, default {@link TypeAssignabilityTester}
     * 
     * @param typeVariableMapping The {@link TypeVariableMapping}
     * @return The {@link TypeAssignabilityTester}
     */
    static TypeAssignabilityTester create(
        TypeVariableMapping typeVariableMapping)
    {
        return new DefaultTypeAssignabilityTester(typeVariableMapping);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private TypeAssignabilityTesters()
    {
        // Private constructor to prevent instantiation
    }
}
