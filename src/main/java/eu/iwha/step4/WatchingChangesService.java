package eu.iwha.step4;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Subscriber;
import eu.iwha.step3.WatchingChanges;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by iwha on 12/31/2016.
 */
@Service
public class WatchingChangesService {

    private static final Logger logger = LoggerFactory.getLogger(WatchingChangesService.class);

    private FileSystem fs;

    public FileSystem getFs() {
        return fs;
    }

    private WatchingChanges watchingChanges;

    @Autowired
    public WatchingChangesService(WatchingChanges watchingChanges, FileSystem fs) throws IOException, InterruptedException {
        logger.info("Creating filesystem");
        this.watchingChanges = watchingChanges;
        this.fs = fs;
        fs = Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setMaxSize(1024 * 1024 * 1024) // 1 GB
                .setMaxCacheSize(256 * 1024 * 1024) // 256 MB
                .setRoots("/")
                .setWorkingDirectory("/src")
                .build());
        try {
            Files.createDirectories(fs.getPath("main/resources/h2"));
            Files.createFile(fs.getPath("sample.txt"));
            Files.createFile(fs.getPath("rzenada.bmp"));
            Files.createFile(fs.getPath("main/resources/h2/test.mv.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("WatchingChangesService is ready");
    }

    public void addSubscriber(Subscriber<Path> subscriber){
        watchingChanges.subscribe(subscriber);
    }
}