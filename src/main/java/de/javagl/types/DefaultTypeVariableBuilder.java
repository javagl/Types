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
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Default implementation of a {@link TypeVariableBuilder}
 */
final class DefaultTypeVariableBuilder implements TypeVariableBuilder
{
    /**
     * The generic declaration that all type variables will belong to
     */
    private final GenericDeclaration genericDeclaration;

    /**
     * The generic declaration that may be built by this class,
     * if no existing generic declaration was given
     */
    private final DefaultGenericDeclaration localGenericDeclaration;
    
    /**
     * The list of variable names
     */
    private final List<String> namesList;
    
    /**
     * The list of bounds corresponding to the variables
     */
    private final List<Type[]> boundsList;

    /**
     * Creates a new instance of this class
     */
    DefaultTypeVariableBuilder()
    {
        this.namesList = new ArrayList<String>();        
        this.boundsList = new ArrayList<Type[]>();
        this.localGenericDeclaration = new DefaultGenericDeclaration();
        this.genericDeclaration = localGenericDeclaration;
    }

    /**
     * Creates a new instance of this class
     * 
     * @param genericDeclaration The generic declaration to which 
     * the type variables should belong
     */
    DefaultTypeVariableBuilder(GenericDeclaration genericDeclaration)
    {
        this.namesList = new ArrayList<String>();        
        this.boundsList = new ArrayList<Type[]>();
        this.genericDeclaration = genericDeclaration;
        this.localGenericDeclaration = null;
    }
    
    @Override
    public TypeVariableBuilder add(String variableName, Type ... bounds)
    {
        Objects.requireNonNull(variableName, "The variableName is null");
        if (namesList.contains(variableName))
        {
            throw new IllegalArgumentException(
                "Variable name '"+variableName+
                "' already contained in "+namesList);
        }
        namesList.add(variableName);
        if (bounds == null)
        {
            boundsList.add(null);
        }
        else
        {
            boundsList.add(bounds.clone());
        }
        return this;
    }

    @Override
    public GenericDeclaration build()
    {
        Map<String, TypeVariable<?>> result = 
            new LinkedHashMap<String, TypeVariable<?>>();
        for (int i=0; i<namesList.size(); i++)
        {
            String name = namesList.get(i);
            Type[] bounds = boundsList.get(i);
            TypeVariable<?> typeParameter = 
                Types.createTypeVariable(genericDeclaration, name, bounds);
            if (localGenericDeclaration != null)
            {
                localGenericDeclaration.addTypeParameter(typeParameter);
            }
            result.put(name, typeParameter);
        }
        return genericDeclaration;
    }
    
}