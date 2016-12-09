package abs;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

/**
 * Created by iwha on 12/8/2016.
 */
public abstract class AbstractFilesysPreparation {

    protected FileSystem fs;

    @Before
    public void createFileSystem() {
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
    }

    @After
    public void tearDown() throws IOException {
        fs.close();
    }
}
