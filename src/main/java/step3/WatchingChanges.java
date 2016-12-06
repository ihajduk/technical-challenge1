package step3;

import rx.Observable;
import rx.subjects.PublishSubject;
import step1.model.Node;
import step2.PathNode;
import step2.TreeOfFiles;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Created by iwha on 12/4/2016.
 */
public class WatchingChanges {

    private static Observable<Path> watchChanges(Node<Path> root) throws IOException {
        PublishSubject<Path> subject = PublishSubject.create();
        WatchService watchService = FileSystems.getDefault().newWatchService();

        TreeOfFiles.createConvert(root).subscribe((node) -> {
                    try {
                        if (Files.isDirectory(node)) {
                            node.register(watchService, ENTRY_CREATE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        Thread observation = new Thread(() -> {
            WatchKey key = null;
            while (!Thread.interrupted()) {

                key = watchService.poll();

                if (Optional.ofNullable(key).isPresent()) {
                    key.pollEvents().stream()
                            .filter(watchEventPredicate -> watchEventPredicate.kind() == ENTRY_CREATE)
                            .forEach(event -> {
                                Path newPath = ((WatchEvent<Path>) event).context();
                                subject.onNext(newPath);
                            });
                }
            }
        });
        observation.start();
        return subject.asObservable();
    }

    public static void main(String[] args) {
        PathNode root = new PathNode(Paths.get("c:\\src"));
        try {
            watchChanges(root).subscribe(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
