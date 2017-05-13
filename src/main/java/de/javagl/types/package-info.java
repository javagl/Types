/**
 * Classes related to types.<br>
 * <br>
 * The types of types that are handled by these classes are
 * <ul>
 *   <li>Class</li>
 *   <li>ParameterizedType</li>
 *   <li>WildcardType</li>
 *   <li>TypeVariable</li>
 *   <li>GenericArrayType</li>
 * </ul>
 * as well as primitive types.<br>
 * <br>
 * All other type types (if they exist) are considered as "unknown types", 
 * and may cause an <code>IllegalArgumentException</code> to be thrown when 
 * occurring in <b>any</b> method of <b>any</b> class in this package. 
 * Additionally, some methods may assume that the raw type of a parameterized 
 * type is always a <code>Class</code>, and throw an 
 * <code>IllegalArgumentException</code> of this is not the case.
 */
package de.javagl.types;

