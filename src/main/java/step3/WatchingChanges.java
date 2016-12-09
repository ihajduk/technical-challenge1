package step3;

import rx.Observable;
import rx.Observer;
import step1.model.Node;
import step2.PathNode;
import step2.TreeOfFiles;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Created by iwha on 12/4/2016.
 */
class WatchingChanges { //TODO: extends Observable<Path> {    --> zwolnienie zasobów

    static Observable<Path> watchChanges(Path root) throws IOException {
        WatchService watchService = root.getFileSystem().newWatchService();

        Node<Path> pathNodeRoot = new PathNode(root);

        TreeOfFiles.createConvert(pathNodeRoot).subscribe((node) -> {
                    try {
                        if (Files.isDirectory(node)) {
                            node.register(watchService, ENTRY_CREATE);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        List<Observer<Path>> list;  //TODO: watek w osobnej metodzie

//TODO: create closable class
        return Observable.create(observer -> {  // nowy per subscriber

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                while (!Thread.interrupted()) {

                    WatchKey key = null;
                    try {
                        key = watchService.take();  // TODO: check InterruptedException
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (Optional.ofNullable(key).isPresent()) {
                        key.pollEvents().stream()
                                .filter(event -> event.kind() == ENTRY_CREATE)
                                .forEach(event -> {
                                    Path newPath = ((WatchEvent<Path>) event).context();
                                    observer.onNext(newPath);
                                });
                    }
                }
                try {
                    watchService.close(); // TODO: close thread and watchService
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
