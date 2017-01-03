package step3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Created by iwha on 12/4/2016.
 */
public class WatchingChanges extends Observable<Path> implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(WatchingChanges.class);

    private static Set<Observer> observers = new HashSet<>();
    private WatchService watchService;
    private ExecutorService executorService;

    private WatchingChanges(Path root, OnSubscribe<Path> f) throws IOException {
        super(f);
        logger.info("Creating Reactive Observable WatchingChanges");

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
        logger.info("Watch Service registered on each directory of /src subfolders");

        logger.info("Starting observing thread...");
        executorService = Executors.newSingleThreadExecutor();

        Thread notifyingThread = new Thread(() -> {
            while (!Thread.interrupted()) {

                WatchKey key = null;
                try {
                    logger.info("Waiting for events...");
                    key = watchService.take();
                    if (Optional.ofNullable(key).isPresent()) {
                        WatchKey finalKey = key;
                        key.pollEvents().stream()
                                .filter(event -> event.kind() == ENTRY_CREATE)
                                .forEach(event -> {
                                    Path currentDirectoryPath = (Path) finalKey.watchable();
                                    Path fullNewPath = currentDirectoryPath.resolve((Path) event.context());
                                    logger.info("Event found: " + fullNewPath);
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
                logger.info("Event handling has been completed");
            }
        });
        // starting thread
        executorService.submit(notifyingThread);
    }

    public static WatchingChanges watchChanges(Path root) throws IOException, InterruptedException {
        return new WatchingChanges(root, observer -> observers.add(observer));
    }

    @Override
    public void close() throws Exception {
        observers.forEach(o -> o.onCompleted());
        executorService.shutdownNow();
        watchService.close();
        logger.info("WatchingChanges has been closed");
    }
}
