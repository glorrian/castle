package ru.bivchallenge.dto;

/**
 * The {@code RecordSeparable} interface defines a contract for objects that can
 * be separated into an array of {@link String} elements. This can be useful for
 * serialization, parsing, or any operation that requires breaking down an object
 * into a set of individual components.
 * <p>
 * Implementations of this interface are expected to provide their own logic for
 * separating the object's data into a string array.
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * public class Person implements RecordSeparable {
 *     private final String name;
 *     private final int age;
 *
 *     public Person(String name, int age) {
 *         this.name = name;
 *         this.age = age;
 *     }
 *
 *     @Override
 *     public String[] separate() {
 *         return new String[] {name, String.valueOf(age)};
 *     }
 * }
 * }</pre>
 *
 * @see String
 */
public interface RecordSeparable {
    String[] separate();
}
