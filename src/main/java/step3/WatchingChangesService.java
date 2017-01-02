package step3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by iwha on 12/31/2016.
 */
public class WatchingChangesService {
    public WatchingChanges watchingChanges;

    public WatchingChangesService(Path root) throws IOException, InterruptedException {
        watchingChanges = WatchingChanges.watchChanges(root);
    }

    public void addFile(Path path) {
        try {
            Files.createFile(path.getFileSystem().getPath(path.toString()));
            if(!watchingChanges.getExecutorService().isShutdown())
                watchingChanges.getSemaphore().acquire();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addDirectory(Path path) {
        try {
            Files.createDirectory(path.getFileSystem().getPath(path.toString()));
            watchingChanges.getSemaphore().acquire();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// TODO: integrate with Spring to get DI for service