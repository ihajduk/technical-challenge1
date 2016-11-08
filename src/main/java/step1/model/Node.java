package step1.model;
/*
 * Created by iwha on 11/1/2016.
 */

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Node<T> {
    T getPayload();
    @NotNull Collection<Node<T>> getChildren();
}
