package step2;

import org.jetbrains.annotations.NotNull;
import step1.model.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by iwha on 11/15/2016.
 */
final class FileNode implements Node<File> {
    private final File rootFile;

    FileNode(File rootFile) {
        this.rootFile = rootFile;
    }

    @Override
    public File getPayload() {
        return rootFile;
    }

    @NotNull
    @Override
    public Collection<Node<File>> getChildren() {
        return initChildren();
    }

    private Collection<Node<File>> initChildren() {
        Optional<File[]> optSubFiles = Optional.ofNullable(rootFile.listFiles());
        Collection<File> subFilesList = new ArrayList<>(Arrays.stream(optSubFiles.orElseGet(
                () -> new File[]{}
        )).collect(Collectors.toList()));

        return subFilesList.stream().map(file -> new FileNode(file)).collect(Collectors.toList());
    }
}
