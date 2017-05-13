# Types

Methods and classes for handling types in Java.

The types of types that are handled by the classes in this library are

- `Class`
- `ParameterizedType`
- `WildcardType`
- `TypeVariable`
- `GenericArrayType`

and primitive types. 

Most of the functionality is offered via utility methods in the 
[`Types` class](https://github.com/javagl/Types/blob/master/src/main/java/de/javagl/types/Types.java).
This class allows creating complex types using a fluent interface:

    // Build the type "List<? extends Number>" type
     Type listWithSubtypeOfNumber = Types
         .create(List.class)
         .withSubtypeOf(Number.class)
         .build();

It also offers a functionality to parse complex types from strings:

     Type listWithIntegers = 
         Types.parse("java.util.List<java.lang.Integer>");
     Type listWithStrings = 
         Types.parse("java.util.List<java.lang.String>");


And finally, it offers a method to test whether such types are *assignable*:

    System.out.println(Types.isAssignable(
        listWithSubtypeOfNumber, listWithIntegers)); // true
    System.out.println(Types.isAssignable(
        listWithSubtypeOfNumber, listWithStrings));  // false


Additionally, the 
[`PrimitiveTypes` class](https://github.com/javagl/Types/blob/master/src/main/java/de/javagl/types/PrimitiveTypes.java)
offers methods to handle primitive types like `int` or `float`, and their
reference type counterparts like `Integer` or `Float`. For example, it 
also contains a method that checks whether primitive types are assignable
via widening primitive conversion, according to the Java Language Specification:

    System.out.println(PrimitiveTypes.isPrimitiveAssignableWithAutoboxing(
        double.class, float.class)); // true
    System.out.println(PrimitiveTypes.isPrimitiveAssignableWithAutoboxing(
        float.class, Float.class));  // true
    System.out.println(PrimitiveTypes.isPrimitiveAssignableWithAutoboxing(
        double.class, Float.class)); // false
    System.out.println(PrimitiveTypes.isPrimitiveAssignableWithAutoboxing(
        short.class, int.class));    // false


