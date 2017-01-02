package step3;

import rx.Observable;
import rx.Observer;
import step1.model.Node;
import step2.PathNode;
import step2.TreeOfFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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

    private static Set<Observer> observers = new HashSet<>();
    private Semaphore semaphore = new Semaphore(0);
    private WatchService watchService;
    private ExecutorService executorService;

    private WatchingChanges(Path root, OnSubscribe<Path> f) throws IOException {
        super(f);
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

        executorService = Executors.newSingleThreadExecutor();

        Thread notifyingThread = new Thread(() -> {
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
        // starting thread
        executorService.submit(notifyingThread);
    }

    static WatchingChanges watchChanges(Path root) throws IOException, InterruptedException {
        return new WatchingChanges(root, observer -> observers.add(observer));
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public ExecutorService getExecutorService() {
        return executorService;
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
