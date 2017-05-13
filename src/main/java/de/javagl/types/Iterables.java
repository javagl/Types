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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Utility methods related to iterables.
 */
class Iterables
{
    /**
     * Returns the elements from the given iterable as a list
     * 
     * @param <T> The element type
     * @param iterable The iterable
     * @return The list
     */
    static <T> List<T> asList(Iterable<? extends T> iterable)
    {
        List<T> result = new ArrayList<T>();
        for (T t : iterable)
        {
            result.add(t);
        }
        return result;
    }
    
    
    /**
     * Returns an iterable that allows iterating over the elements of
     * the cartesian product of the given domain, in form of 
     * unmodifiable lists.
     * 
     * @param <T> The element type of the domain
     * 
     * @param domain The domain
     * @return The iterable
     */
    static <T> Iterable<List<T>> cartesianProduct(
        final Iterable<? extends Iterable<? extends T>> domain)
    {
        return new Iterable<List<T>>()
        {
            @Override
            public Iterator<List<T>> iterator()
            {
                return new CartesianProductIterator<T>(domain);
            }
        };
    }
    
    /**
     * Implementation of an Iterator that allows iterating over the 
     * elements of the cartesian product of a domain, in form of 
     * unmodifiable lists.
     *
     * @param <T> The element type of the domain
     */
    private static class CartesianProductIterator<T> 
        implements Iterator<List<T>>
    {
        /**
         * The dimensions of the domain 
         */
        private final int dimensions;
        
        /**
         * The iterables for each domain dimension
         */
        private final List<Iterable<? extends T>> iterables;
        
        /**
         * The current iterators for each domain dimension
         */
        private final List<Iterator<? extends T>> iterators;
        
        /**
         * The current element of the cartesian product. An unmodifiable
         * copy of this list will be returned in {@link #next()}.
         */
        private List<T> current;
        
        /**
         * Creates a new iterator that allows iterating over the 
         * cartesian product of the given domain
         * 
         * @param domain The domain
         */
        CartesianProductIterator(
            Iterable<? extends Iterable<? extends T>> domain)
        {
            // Obtain the iterable for each dimension
            this.iterables = new ArrayList<Iterable<? extends T>>();
            int dim = 0;
            for (Iterable<? extends T> iterable : domain)
            {
                iterables.add(iterable);
                dim++;
            }
            this.dimensions = dim;
            
            // Obtain the iterator for each dimension
            this.iterators = new ArrayList<Iterator<? extends T>>();
            for (Iterable<? extends T> iterable : iterables)
            {
                iterators.add(iterable.iterator());
            }
            
            // Initialize the current list with the first value
            // of each iterator
            this.current = new ArrayList<T>();
            for (Iterator<? extends T> iterator : iterators)
            {
                if (!iterator.hasNext())
                {
                    current = null;
                    break;
                }
                current.add(iterator.next());
            }
        }
        
        /**
         * Prepare the next element to be returned
         */
        private void prepareNext()
        {
            boolean increased = increase(dimensions-1);
            if (!increased)
            {
                current = null;
            }
        }
        
        /**
         * Increase the entry at the specified dimension for the 
         * current element. If the iterator for the given dimension
         * is exhausted, it will be reset to the first element, and
         * the method will be called recursively with the next dimension.
         * 
         * @param dim The dimension to increase
         * @return Whether the dimension (or any of the next dimensions)
         * could be increased
         */
        private boolean increase(int dim)
        {
            if (dim < 0)
            {
                return false;
            }
            Iterator<? extends T> iterator = iterators.get(dim);
            if (iterator.hasNext())
            {
                current.set(dim, iterator.next());
                return true;
            }
            iterator = iterables.get(dim).iterator();
            current.set(dim, iterator.next());
            iterators.set(dim, iterator);
            return increase(dim-1);
        }

        @Override
        public boolean hasNext()
        {
            return current != null;
        }

        @Override
        public List<T> next()
        {
            if (current == null)
            {
                throw new NoSuchElementException("No more elements");
            }
            List<T> result = 
                Collections.unmodifiableList(new ArrayList<T>(current));
            prepareNext();
            return result;
        }


        @Override
        public void remove()
        {
            throw new UnsupportedOperationException(
                "May not remove elements with this iterator");
        }
        
    }

    
    /**
     * Private constructor to prevent instantiation
     */
    private Iterables()
    {
        // Private constructor to prevent instantiation
    }
    
    
    
}
