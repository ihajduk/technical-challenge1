package step3;

import rx.Observable;
import rx.Observer;
import rx.observers.TestSubscriber;
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
import java.util.concurrent.Semaphore;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Created by iwha on 12/4/2016.
 */
public class WatchingChanges extends Observable<Path> implements AutoCloseable {

    private static Semaphore semaphore = new Semaphore(0);
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
                if (Optional.ofNullable(key).isPresent()) {
                    WatchKey finalKey = key;
                    key.pollEvents().stream()
                            .filter(event -> event.kind() == ENTRY_CREATE)
                            .forEach(event -> {
                                Path currentDirectoryPath = (Path) finalKey.watchable();
                                Path fullNewPath = currentDirectoryPath.resolve((Path) event.context());
                                if (Files.isDirectory(fullNewPath)) {
                                    try {
                                        fullNewPath.register(watchService, ENTRY_CREATE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                observers.forEach(o -> o.onNext(fullNewPath));
                            });
                }
            } catch (InterruptedException e) {
                observers.forEach(Observer::onCompleted);
            }
            key.reset();
            semaphore.release();
        }
    });

    static WatchingChanges watchChanges(Path root) throws IOException, InterruptedException {
        watchService = root.getFileSystem().newWatchService();

        Node<Path> pathNodeRoot = new PathNode(root);

        TreeOfFiles.createConvert(pathNodeRoot).subscribe((node) -> {
                    if (Files.isDirectory(node)) {
                        try {
                            node.register(watchService, ENTRY_CREATE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        observers.forEach(o -> o.onCompleted());
        executorService.shutdownNow();
        watchService.close();
    }

    public void addFile(Path path) {
        try {
            Files.createFile(path.getFileSystem().getPath(path.toString()));
            if(!executorService.isShutdown())
                semaphore.acquire();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addDirectory(Path path) {
        try {
            Files.createDirectory(path.getFileSystem().getPath(path.toString()));
            semaphore.acquire();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
