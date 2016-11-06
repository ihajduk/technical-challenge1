package model;
/*
 * Created by iwha on 11/1/2016.
 */
import java.util.Collection;

public interface Node<T> {
    T getPayload();
    Collection<Node<T>> getChildren();
}
