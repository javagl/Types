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
import java.util.ArrayList;
import java.util.List;


/**
 * Default implementation of a {@link TypeBuilder}
 */
final class DefaultTypeBuilder implements TypeBuilder
{
    /**
     * The raw type of the type being built.
     */
    private final Type rawType;
    
    /**
     * The type arguments of the type being built.
     */
    private final List<Type> typeArguments = new ArrayList<Type>();
    
    /**
     * Create an instance of this class, using the given raw type
     * 
     * @param rawType The raw type
     */
    DefaultTypeBuilder(Type rawType)
    {
        this.rawType = rawType;
    }

    @Override
    public TypeBuilder withSubtypeOf(Type type)
    {
        Type typeArgument = Types.createWildcardType(
            null, new Type[]{type});
        typeArguments.add(typeArgument);
        return this;
    }

    @Override
    public TypeBuilder withType(Type type)
    {
        typeArguments.add(type);
        return this;
    }
    
    @Override
    public TypeBuilder withSupertypeOf(Type type)
    {
        Type typeArgument = Types.createWildcardType(
            new Type[]{type}, null);
        typeArguments.add(typeArgument);
        return this;
    }
    
    @Override
    public Type build()
    {
        if (typeArguments.isEmpty())
        {
            return rawType;
        }
        Type ownerType = null;
        if (rawType instanceof Class<?>)
        {
            Class<?> rawTypeAsClass = (Class<?>)rawType;
            ownerType = rawTypeAsClass.getEnclosingClass();
            TypeVariable<?>[] typeParameters = 
                rawTypeAsClass.getTypeParameters();
            if (typeParameters.length != typeArguments.size())
            {
                throw new IllegalStateException(
                    "Raw type "+rawType+" requires "+typeParameters.length+
                    " type parameters, but there are "+typeArguments.size()+
                    " type arguments specified: "+typeArguments);
            }
        }
        return Types.createParameterizedType(
            rawType, ownerType, typeArguments.toArray(new Type[0]));
    }
}
