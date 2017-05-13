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

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of a GenericDeclaration. <br>
 * <br>
 * Note that this is an implementation of an interface that is usually 
 * intended to be implemented by classes that represent real elements 
 * of the Java Programming Language (namely Classes, Methods and 
 * Constructors). This "stand-alone" implementation has very limited 
 * usage. Particularly, it is solely used as an "artificial" 
 * GenericDeclaration that may be referred to by "artificial" 
 * TypeVariable instances.  
 */
final class DefaultGenericDeclaration implements GenericDeclaration
{
    /**
     * The type parameters of this generic declaration
     */
    private List<TypeVariable<?>> typeParameters = 
        new ArrayList<TypeVariable<?>>();
    
    /**
     * Add the given type parameter to this generic declaration
     * 
     * @param typeParameter The type parameter to add
     * @throws IllegalArgumentException If the given type parameter has
     * a generic declaration that is different from 'this'
     */
    void addTypeParameter(TypeVariable<?> typeParameter)
    {
        if (typeParameter.getGenericDeclaration() != this)
        {
            throw new IllegalArgumentException(
                "Type parameter has a different generic declaration");
        }
        typeParameters.add(typeParameter);
    }

    @Override
    public TypeVariable<?>[] getTypeParameters()
    {
        return typeParameters.toArray(new TypeVariable<?>[0]);
    }
    
    @Override
    public String toString()
    {
        return "DefaultGenericDeclaration[typeParameters="+typeParameters+"]";
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass)
    {
        return null;
    }

    @Override
    public Annotation[] getAnnotations()
    {
        return new Annotation[0];
    }

    @Override
    public Annotation[] getDeclaredAnnotations()
    {
        return new Annotation[0];
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        for (TypeVariable<?> typeParameter : typeParameters)
        {
            if (typeParameter != null)
            {
                String name = typeParameter.getName();
                if (name != null)
                {
                    result = prime * result + name.hashCode();
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
            return true;
        if (object == null)
            return false;
        return false;
    }

}