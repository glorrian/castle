package ru.bivchallenge.executor;


/**
 * The {@code Executor} interface is a functional interface that represents a single execution task.
 * It is designed to encapsulate a unit of work that may throw a generic {@link Exception}.
 * <p>
 * This interface can be used as a lambda expression or method reference to define a task
 * that needs to be executed, potentially handling checked exceptions.
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * Executor executor = () -> {
 *     // Task logic here
 *     if (someCondition) {
 *         throw new Exception("Task failed");
 *     }
 * };
 * executor.execute();
 * }</pre>
 *
 * @see java.util.concurrent.Executor
 */
@FunctionalInterface
public interface Executor {
    void execute() throws Exception;
}
