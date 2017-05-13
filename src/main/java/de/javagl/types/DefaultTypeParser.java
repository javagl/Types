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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Default implementation of the {@link TypeParser} interface
 */
final class DefaultTypeParser implements TypeParser
{
    /**
     * The prefixes for imported package names. For an import string like
     * <code>java.util.*</code>, this list will contain the entry
     * <code>java.util.</code>. The names in this list will be used as 
     * prefixes when attempting to load a class. 
     */
    private final Set<String> importPackageNames = new LinkedHashSet<String>(); 
    
    /**
     * Import strings that correspond to single, fully qualified type names,
     * like <code>java.util.List</code>.  
     */
    private final Set<String> importedTypeNames = new LinkedHashSet<String>(); 
    
    /**
     * The names of type variables
     */
    private final List<String> typeVariableNames = new ArrayList<String>();
    
    /**
     * Creates a new type parser instance
     */
    DefaultTypeParser()
    {
        importPackageNames.add("");
        importPackageNames.add("java.lang.");
    }
    
    @Override
    public TypeParser addImport(String importString)
    {
        importString = importString.replaceAll(" ",  "");
        validateImportString(importString);
        String lastToken = getLastToken(importString);
        if (lastToken.equals("*"))
        {
            String importPackageName = 
                importString.substring(0, importString.length()-1);
            importPackageNames.add(importPackageName);
        }
        else
        {
            for (String alreadyImportedTypeName : importedTypeNames)
            {
                String typeName = getLastToken(alreadyImportedTypeName);
                if (typeName.equals(lastToken))
                {
                    throw new IllegalArgumentException(
                        "Import "+importString+
                        " collides with "+alreadyImportedTypeName);
                }
            }
            importedTypeNames.add(importString);
        }
        return this;
    }

    
    @Override
    public TypeParser addTypeVariableName(String typeVariableName)
    {
        if (!isValidJavaIdentifier(typeVariableName))
        {
            throw new IllegalArgumentException(
                "Type variable name "+typeVariableName+
                " is not a valid Java identifier");
        }
        typeVariableNames.add(typeVariableName);
        
        // Sort the type variable names by length, so that the
        // check whether a string starts with a type variable
        // name starts with the longest possible name. 
        Collections.sort(typeVariableNames, new Comparator<String>()
        {
            @Override
            public int compare(String s0, String s1)
            {
                return s1.length() - s0.length();
            }
        });
        return this;
    }
    
    @Override
    public Type parse(String string) 
        throws ClassNotFoundException
     {
        TypeVariableBuilder typeVariableBuilder = 
            Types.createTypeVariables();
        return parse(string, typeVariableBuilder);
     }
    
    /**
     * Parse the type from the given string, using the given 
     * {@link TypeVariableBuilder}
     * 
     * @param string The string
     * @param typeVariableBuilder The {@link TypeVariableBuilder}
     * @return The type
     * @throws ClassNotFoundException If a class can not be found
     * @throws IllegalArgumentException If the input string is invalid
     */
    private Type parse(String string, TypeVariableBuilder typeVariableBuilder) 
        throws ClassNotFoundException
    {
        if (string == null)
        {
            throw new IllegalArgumentException("Input string is null");
        }
        if (string.endsWith("[]"))
        {
            String s = string.substring(0, string.length()-2);
            Type componentType = parse(s);
            return Types.createGenericArrayType(componentType);
        }
        int index0 = string.indexOf("<");
        if (index0 == -1)
        {
            return parseNonParameterizedType(string, typeVariableBuilder);
        }
        int index1 = string.lastIndexOf(">");
        if (index1 == -1)
        {
            throw new IllegalArgumentException(
                "No matching '>' for '<' in input string: "+string);
        }
        String typeName = string.substring(0, index0);
        String typeParametersString = string.substring(index0+1, index1);
        Class<?> c = searchClass(typeName);
        TypeBuilder typeBuilder = Types.create(c);
        processTypeParameters(
            typeBuilder, typeVariableBuilder, typeParametersString);
        try
        {
            return typeBuilder.build();
        }
        catch (IllegalStateException e)
        {
            throw new IllegalArgumentException(
                "Invalid input string: "+string, e);
        }
    }

    /**
     * Parse a type that is NOT a parameterized type from the given string.
     * 
     * @param string The input string
     * @param typeVariableBuilder The {@link TypeVariableBuilder}
     * @return The type
     * @throws ClassNotFoundException If a class can not be found
     */
    private Type parseNonParameterizedType(
        String string, TypeVariableBuilder typeVariableBuilder) 
            throws ClassNotFoundException
    {
        String typeVariableName = 
            getTypeVariableNamePrefix(string);
        if (typeVariableName != null)
        {
            typeVariableBuilder.add(typeVariableName);
            GenericDeclaration genericDeclaration =
                typeVariableBuilder.build();
            TypeVariable<?>[] typeVariables = 
                genericDeclaration.getTypeParameters();
            Type t = typeVariables[typeVariables.length-1];
            return t;
        }
        if (PrimitiveTypes.isPrimitiveTypeName(string))
        {
            return PrimitiveTypes.getPrimitiveType(string);
        }
        if (string.equals("void"))
        {
            return void.class;
        }
        Class<?> c = searchClass(string);
        return c;
    }
    
    /**
     * Process the given String, containing 0 or more comma-separated
     * type parameters, and add them to the type using the given
     * builder
     * 
     * @param typeBuilder The type builder
     * @param typeVariableBuilder The {@link TypeVariableBuilder}
     * @param typeParametersString The type parameters string
     * @throws ClassNotFoundException If a class is specified that
     * can not be loaded
     */
    private void processTypeParameters(
        TypeBuilder typeBuilder, TypeVariableBuilder typeVariableBuilder, 
        String typeParametersString) 
            throws ClassNotFoundException
    {
        List<String> typeParameterStrings = 
            splitTypes(typeParametersString, ',');
        //System.out.println("Type parameters "+typeParameterStrings);
        for (String typeParameterString : typeParameterStrings)
        {
            processTypeParameter(typeBuilder, typeVariableBuilder, 
                typeParameterString);
        }
    }

    /**
     * Process the given String, containing a single type parameter, 
     * and add it to the type using the given builder
     * 
     * @param typeBuilder The type builder
     * @param typeVariableBuilder The {@link TypeVariableBuilder}
     * @param typeParameterString The type parameter string
     * @throws ClassNotFoundException If a class is specified that
     * can not be loaded
     * @throws IllegalArgumentException If the type parameter string has
     * an invalid format
     */
    private void processTypeParameter(
        TypeBuilder typeBuilder, TypeVariableBuilder typeVariableBuilder, 
        String typeParameterString)
            throws ClassNotFoundException
    {
        //System.out.println("Type parameter: "+typeParameterString);
        
        String typeVariableName = 
            getTypeVariableNamePrefix(typeParameterString);
        if (typeVariableName != null)
        {
            processTypeVariableParameter(
                typeBuilder, typeVariableBuilder,
                typeParameterString, typeVariableName);
        }
        else if (typeParameterString.trim().startsWith("?"))
        {
            processWildcardTypeParameter(typeBuilder, typeVariableBuilder,
                typeParameterString);
        }
        else
        {
            Type typeParameter = 
                parse(typeParameterString, typeVariableBuilder);
            typeBuilder.withType(typeParameter);
        }
    }

    /**
     * Process the given type parameter string which starts with the
     * given name of a type variable, and add the respective type
     * variable to the type- and type variable builder
     * 
     * @param typeBuilder The type builder
     * @param typeVariableBuilder The {@link TypeVariableBuilder}
     * @param typeParameterString The type parameter string
     * @param typeVariableName The type variable name
     * @throws ClassNotFoundException If a class is specified that
     * can not be loaded
     * @throws IllegalArgumentException If the type parameter string has
     * an invalid format
     */
    private void processTypeVariableParameter(TypeBuilder typeBuilder,
        TypeVariableBuilder typeVariableBuilder, String typeParameterString,
        String typeVariableName) throws ClassNotFoundException
    {
        String s = trim(typeParameterString, typeVariableName);
        if (s.startsWith("extends"))
        {
            String typeVariableBoundsString = trim(s, "extends");
            List<String> typeVariableBoundStrings = 
                splitTypes(typeVariableBoundsString, '&');
            
            List<Type> typeVariableBounds = new ArrayList<Type>();
            for (String typeVariableBoundString : typeVariableBoundStrings)
            {
                Type typeVariableBound = 
                    parse(typeVariableBoundString, typeVariableBuilder);
                typeVariableBounds.add(typeVariableBound);
            }
            typeVariableBuilder.add(typeVariableName, 
                typeVariableBounds.toArray(new Type[0]));
        }
        else if (!s.isEmpty())
        {
            throw new IllegalArgumentException(
                "Invalid type parameter string: "+typeParameterString);
        }
        else
        {
            typeVariableBuilder.add(typeVariableName);
        }
        
        // TODO This is not very elegant, and exploiting the fact that
        // the DefaultTypeVariableBuilder always returns the SAME
        // GenericDeclaration instance, extended with the most recent 
        // type variable. Clean this up!
        GenericDeclaration genericDeclaration =
            typeVariableBuilder.build();
        TypeVariable<?>[] typeVariables = 
            genericDeclaration.getTypeParameters();
        Type t = typeVariables[typeVariables.length-1];
        typeBuilder.withType(t);
    }
    
    
    /**
     * Process the given type parameter string which contains a 
     * wildcard type parameter, and add the appropriate type
     * to the type builder.
     * 
     * @param typeBuilder The type builder
     * @param typeVariableBuilder The {@link TypeVariableBuilder}
     * @param typeParameterString The type parameter string
     * @throws ClassNotFoundException If a class is specified that
     * can not be loaded
     * @throws IllegalArgumentException If the type parameter string has
     * an invalid format
     */
    private void processWildcardTypeParameter(TypeBuilder typeBuilder,
        TypeVariableBuilder typeVariableBuilder, String typeParameterString)
        throws ClassNotFoundException
    {
        String s = trim(typeParameterString, "?");
        if (s.startsWith("extends"))
        {
            String typeParameterBoundString = trim(s, "extends");
            Type typeParameterBound = 
                parse(typeParameterBoundString, typeVariableBuilder);
            typeBuilder.withSubtypeOf(typeParameterBound);
        }
        else if (s.startsWith("super"))
        {
            String typeParameterBoundString = trim(s, "super");
            Type typeParameterBound = 
                parse(typeParameterBoundString, typeVariableBuilder);
            typeBuilder.withSupertypeOf(typeParameterBound);
        }
        else
        {
            typeBuilder.withSubtypeOf(Object.class);
        }
    }
    
    
    /**
     * Will return <code>Class.forName(name)</code> for the given name.
     * If this fails, the names from the package names added with
     * {@link #addImport(String)} will be used as prefixes 
     * for further attempts.
     * 
     * @param name The class name
     * @return The class
     * @throws ClassNotFoundException If a class is specified that
     * can not be loaded
     * @throws IllegalArgumentException If multiple classes with the
     * given name exist in the {@link #addImport(String) imports}.
     */
    private Class<?> searchClass(String name) 
        throws ClassNotFoundException
    {
        // If the name does NOT contain a '.', then try to find an
        // imported type name whose last token is the given name
        if (!name.contains("."))
        {
            for (String importedTypeName : importedTypeNames)
            {
                String lastToken = getLastToken(importedTypeName);
                if (lastToken.equals(name))
                {
                    return searchClass(importedTypeName);
                }
            }
        }
        
        // Now assuming that the name is either a fully qualified name,
        // or the name of a class from the imported packages
        List<Class<?>> matchingClasses = new ArrayList<Class<?>>();
        for (String packageName : importPackageNames)
        {
            try
            {
                Class<?> c = Class.forName(packageName+name);
                matchingClasses.add(c);
            }
            catch (ClassNotFoundException e)
            {
                // Ignore here, throw later if necessary
            }
        }
        if (matchingClasses.isEmpty())
        {
            throw new ClassNotFoundException(name);
        }
        if (matchingClasses.size() > 1)
        {
            throw new IllegalArgumentException(
                "Class name "+name+" is ambiguous: "+matchingClasses);
        }
        return matchingClasses.get(0);
    }
    
    
    /**
     * Checks whether the given string is either equal to a type variable
     * name or starts with a type variable name followed by a space " ".
     * Returns this type variable name, or <code>null</code> if neither
     * is the case. 
     * 
     * @param string The string
     * @return The type variable name, or <code>null</code>
     */
    private String getTypeVariableNamePrefix(String string)
    {
        string = string.trim();
        for (String typeVariableName : typeVariableNames)
        {
            if (string.equals(typeVariableName))
            {
                return typeVariableName;
            }
            if (string.startsWith(typeVariableName+" "))
            {
                return typeVariableName;
            }
        }
        return null;
    }
    
    /**
     * Returns the part of the given string behind the given trimmed part,
     * omitting any leading or trailing whitepsaces
     * 
     * @param string The string
     * @param trimmedPart The trimmed part
     * @return The remaining string
     */
    private String trim(String string, String trimmedPart)
    {
        int index = string.indexOf(trimmedPart);
        return string.substring(index+trimmedPart.length()).trim();
    }
    
    /**
     * Split the given string into a list of strings defining types.
     * That is, the given string will be split at all occurrences of
     * the given delimiter char that are not inside any &lt; brackets &gt;.
     * 
     * @param typesString The input string
     * @param delimiter The delimiter char
     * @return The list of tokens
     */
    private static List<String> splitTypes(String typesString, char delimiter)
    {
        // Feel free to write a RegEx for that, and compare the
        // performance of the RegEx to this approach....
        List<String> typeStrings = new ArrayList<String>();
        int openBrackets = 0;
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<typesString.length(); i++)
        {
            char c = typesString.charAt(i);
            if (c == '<')
            {
                openBrackets++;
                sb.append("<");
            }
            else if (c == '>')
            {
                openBrackets--;
                sb.append(">");
            }
            else if (openBrackets==0 && c == delimiter)
            {
                typeStrings.add(sb.toString().trim());
                sb = new StringBuilder();
            }
            else
            {
                sb.append(c);                
            }
        }
        if (sb.length() > 0)
        {
            typeStrings.add(sb.toString().trim());
        }
        if (openBrackets > 0)
        {
            throw new IllegalArgumentException(
                "No matching '>' for '<' in input string: "+typesString);
        }
        if (openBrackets < 0)
        {
            throw new IllegalArgumentException(
                "No matching '<' for '>' in input string: "+typesString);
        }
        return typeStrings;
    }

    
    /**
     * Make sure that the given import string is valid. That is,
     * that it consists of a sequence of '.'-separated Java
     * identifiers, and ends with a Java identifier or a '*'
     *  
     * @param importString The import string
     * @throws IllegalArgumentException If the string is not valid
     */
    private static void validateImportString(String importString)
    {
        String tokens[] = importString.split("\\.");
        for (int i=0; i<tokens.length; i++)
        {
            String token = tokens[i];
            if (!isValidJavaIdentifier(token) &&
                !(i == tokens.length-1 && token.equals("*")))
            {
                throw new IllegalArgumentException(
                    "Invalid import string: "+importString);
            }
        }
    }
    
    /**
     * Returns whether the given string is a valid Java identifier.
     * The empty string and <code>null</code> are no valid Java
     * identifiers.
     * 
     * @param s The string
     * @return Whether the given string is a valid Java identifier.
     */
    private static boolean isValidJavaIdentifier(String s)
    {
       if (s == null || s.length() == 0)
       {
          return false;
       }
       char[] c = s.toCharArray();
       if (!Character.isJavaIdentifierStart(c[0]))
       {
          return false;
       }
       for (int i = 1; i < c.length; i++)
       {
          if (!Character.isJavaIdentifierPart(c[i]))
          {
             return false;
          }
       }
       return true;
    }

    /**
     * Returns the part of the given string behind the last <code>'.'</code>
     * character, or <code>null</code> if the string does not contain a 
     * <code>'.'</code>.
     *  
     * @param s The input string
     * @return The last token
     */
    private static String getLastToken(String s)
    {
        int index = s.lastIndexOf('.');
        if (index == -1)
        {
            return null;
        }
        return s.substring(index+1);
    }
    
}
