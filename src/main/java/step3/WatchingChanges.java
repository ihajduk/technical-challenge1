package step3;

import rx.Observable;
import rx.Observer;
import step1.model.Node;
import step2.PathNode;
import step2.TreeOfFiles;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Created by iwha on 12/4/2016.
 */
class WatchingChanges extends Observable<Path> implements AutoCloseable {

    private static Set<Observer> observers = new HashSet<>();

    private static WatchService watchService;
    private static ExecutorService executorService;

    private WatchingChanges(OnSubscribe<Path> f) {
        super(f);
    }

    private static Thread notifyingThread = new Thread(() -> {
        while (!Thread.interrupted()) {

            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                observers.forEach(Observer::onCompleted);
            }

            if (Optional.ofNullable(key).isPresent()) {
                key.pollEvents().stream()
                        .filter(event -> event.kind() == ENTRY_CREATE)
                        .forEach(event -> {
                            Path newPath = ((WatchEvent<Path>) event).context();
                            observers.forEach(o -> o.onNext(newPath));
                        });
            }
        }
    });

    static WatchingChanges watchChanges(Path root) throws IOException {
        watchService = root.getFileSystem().newWatchService();

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
        // starting thread
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(notifyingThread);

        return new WatchingChanges(observer -> observers.add(observer));
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
        watchService.close();
    }
}