import model.Node;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Tree {
    static <T> Iterable<T> convert(Node<T> root){
        return recursiveStream(root).map(Node::getPayload).collect(Collectors.toList());
    }

    private static <T> Stream<Node<T>> recursiveStream(Node<T> node){
        if(node.getChildren().isEmpty()){
            return Stream.of(node);
        } else {
            return Stream.concat(Stream.of(node), node.getChildren().stream().flatMap(s -> recursiveStream(s)));
        }
    }

    class TreeIterable<T> implements Iterable<T> {

        @Override
        public Iterator<T> iterator() {
            return null;
        }
    }

}
