import model.Node;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by iwha on 11/1/2016.
 */
class Corporate implements Node<String> {
    private String personName=null;
    private Collection<Node<String>> children= new ArrayList<>();

    Corporate(String personName) {
        this.personName = personName;
    }

    @Override
    public String getPayload() {
        return personName;
    }

    @Override
    @NotNull
    public Collection<Node<String>> getChildren() {
        return children;
    }

    @SafeVarargs
    final void addChildren(Node<String>... nodes) {
        Collections.addAll(children, nodes);
    }
}
