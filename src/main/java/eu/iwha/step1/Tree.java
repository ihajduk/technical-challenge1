package eu.iwha.step1;

import eu.iwha.step1.model.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;

final class Tree {

    static <T> Iterable<T> convert(@Nullable Node<T> root){
        if(root==null) //noinspection unchecked
            return Collections.EMPTY_LIST;

        return new TreeIterable<>(root);
    }

    private static class TreeIterable<T> implements Iterable<T> {
        final Node<T> node;

        TreeIterable(@NotNull Node<T> node) {
            this.node = node;
        }

        @Override
        public Iterator<T> iterator() {
            return new TreeIterator();
        }

        class TreeIterator implements Iterator<T>{
            Stack<Node<T>> remainingChildren = new Stack<>();

            TreeIterator() {
                remainingChildren.addAll(node.getChildren());
            }

            @Override
            public boolean hasNext() {
                return !remainingChildren.isEmpty();
            }

            @Override
            public T next() {
                Node<T> node = remainingChildren.pop();
                remainingChildren.addAll(node.getChildren());
                return node.getPayload();
            }
        }
    }

}
