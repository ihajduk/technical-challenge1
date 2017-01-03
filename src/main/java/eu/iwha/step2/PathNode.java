package eu.iwha.step2;

import org.jetbrains.annotations.NotNull;
import eu.iwha.step1.model.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by iwha on 11/15/2016.
 */
public final class PathNode implements Node<Path> {
    private final Path rootPath;

    public PathNode(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public Path getPayload() {
        return rootPath;
    }

    @NotNull
    @Override
    public Collection<Node<Path>> getChildren() {
        return initChildren();
    }

    private Collection<Node<Path>> initChildren() {
        Collection<Node<Path>> children = new ArrayList<>();
        if (Files.isDirectory(rootPath)) {
            try {
                children = Files.list(rootPath).map(path -> new PathNode(path)).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return children;
    }
}
