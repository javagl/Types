/*
 * www.javagl.de - Types
 *
 * Copyright (c) 2012-2015 Marco Hutter - http://www.javagl.de
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods related to type bounds, like the upper and lower bounds 
 * of WildcardTypes and TypeVariables.
 */
class TypeBounds
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(TypeBounds.class.getName());

    /**
     * The log level for information about obsolete bounds
     */
    private static final Level OBSOLETE_BOUNDS_LEVEL = Level.FINE;

    
    /**
     * Creates an unmodifiable list containing the given old upper bounds and  
     * the upper bounds to add, but removes any bounds that are obsolete. 
     * An upper bound is obsolete when it is assignable from (but not equal
     * to) another upper bound. The order of the returned elements is 
     * unspecified.
     * 
     * @param oldUpperBounds The old upper bounds. 
     * @param upperBoundsToAdd The upper bounds to add
     * @return The new upper bounds
     */
    public static List<Type> mergeUpperBounds(
        Collection<? extends Type> oldUpperBounds, 
        Iterable<? extends Type> upperBoundsToAdd)
    {
        Set<Type> result = new LinkedHashSet<Type>(oldUpperBounds);
        for (Type upperBoundToAdd : upperBoundsToAdd)
        {
            result = addUpperBound(result, upperBoundToAdd);
        }
        return Collections.unmodifiableList(new ArrayList<Type>(result));
    }

    /**
     * Creates a set containing the given old upper bounds and  
     * the upper bound to add, but removes any bounds that are obsolete. 
     * An upper bound is obsolete when it is assignable from (but not 
     * equal to) another upper bound. 
     * 
     * @param oldUpperBounds The old upper bounds.
     * @param upperBoundToAdd The upper bound to add
     * @return The new upper bounds
     */
    private static Set<Type> addUpperBound(
        Collection<? extends Type> oldUpperBounds, Type upperBoundToAdd)
    {
        Set<Type> newUpperBounds = new LinkedHashSet<Type>(oldUpperBounds);
        List<Type> obsoleteUpperBounds = new ArrayList<Type>();
        for (Type oldUpperBound : newUpperBounds)
        {
            if (Types.isAssignable(oldUpperBound, upperBoundToAdd) &&
                !oldUpperBound.equals(upperBoundToAdd))
            {
                obsoleteUpperBounds.add(oldUpperBound);
            }
            if (Types.isAssignable(upperBoundToAdd, oldUpperBound) &&
                !upperBoundToAdd.equals(oldUpperBound))
            {
                obsoleteUpperBounds.add(upperBoundToAdd);
            }
        }
        
        if (logger.isLoggable(OBSOLETE_BOUNDS_LEVEL))
        {
            if (!obsoleteUpperBounds.isEmpty()) 
            {
                logger.log(OBSOLETE_BOUNDS_LEVEL,
                    "Old upper bounds: "+oldUpperBounds+", "+
                    "adding "+upperBoundToAdd+", "+
                    "obsolete: "+obsoleteUpperBounds);  
            }
        }

        newUpperBounds.add(upperBoundToAdd);
        newUpperBounds.removeAll(obsoleteUpperBounds);
        return newUpperBounds;
    }
    

    /**
     * Creates an unmodifiable list containing the given old lower bounds and 
     * the lower bounds to add, but removes any bounds that are obsolete. 
     * A lower bound is obsolete when another lower bound is assignable 
     * from (but not equal to) it. 
     * 
     * @param oldLowerBounds The old lower bounds. 
     * @param lowerBoundsToAdd The lower bounds to add
     * @return The new lower bounds
     */
    public static List<Type> mergeLowerBounds(
        Collection<? extends Type> oldLowerBounds, 
        Iterable<? extends Type> lowerBoundsToAdd)
    {
        Set<Type> result = new LinkedHashSet<Type>(oldLowerBounds);
        for (Type lowerBoundToAdd : lowerBoundsToAdd)
        {
            result = addLowerBound(result, lowerBoundToAdd);
        }
        return Collections.unmodifiableList(new ArrayList<Type>(result));
    }
    
    /**
     * Creates a set containing the given old lower bounds and the 
     * lower bound to add, but removes any bounds that are obsolete. 
     * A lower bound is obsolete when another lower bound is assignable 
     * from (but not equal to) it. 
     * 
     * @param oldLowerBounds The old lower bounds. 
     * @param lowerBoundToAdd The lower bound to add
     * @return The new lower bounds
     */
    private static Set<Type> addLowerBound(
        Collection<? extends Type> oldLowerBounds, Type lowerBoundToAdd)
    {
        Set<Type> newLowerBounds = new LinkedHashSet<Type>(oldLowerBounds);
        List<Type> obsoleteLowerBounds = new ArrayList<Type>();
        for (Type oldLowerBound : newLowerBounds)
        {
            if (Types.isAssignable(lowerBoundToAdd, oldLowerBound) &&
                !lowerBoundToAdd.equals(oldLowerBound))
            {
                obsoleteLowerBounds.add(oldLowerBound);
            }
            if (Types.isAssignable(oldLowerBound, lowerBoundToAdd) &&
                !oldLowerBound.equals(lowerBoundToAdd))
            {
                obsoleteLowerBounds.add(lowerBoundToAdd);
            }
        }
        
        if (logger.isLoggable(OBSOLETE_BOUNDS_LEVEL))
        {
            if (obsoleteLowerBounds.size()>0) 
            {
                logger.log(OBSOLETE_BOUNDS_LEVEL,
                    "Old lower bounds: "+oldLowerBounds+", "+
                    "adding "+lowerBoundToAdd+", "+
                    "obsolete: "+obsoleteLowerBounds);  
            }
        }

        newLowerBounds.add(lowerBoundToAdd);
        newLowerBounds.removeAll(obsoleteLowerBounds);
        return newLowerBounds;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private TypeBounds()
    {
        // Private constructor to prevent instantiation
    }
    
}
