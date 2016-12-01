package step2;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by iwha on 11/23/2016.
 */
public class TreeOfFilesTest {

    private FileSystem fs;

    @Before
    public void createFileSystem() {
        fs = Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setMaxSize(1024 * 1024 * 1024) // 1 GB
                .setMaxCacheSize(256 * 1024 * 1024) // 256 MB
                .setRoots("/")
                .setWorkingDirectory("/src")
                .build());
    }

    @After
    public void tearDown() throws IOException {
        fs.close();
    }

    @Test
    public void shouldProduceExpectedFileStream() {
        try {
            Files.createDirectories(fs.getPath("main/resources/h2"));
            Files.createFile(fs.getPath("sample.txt"));
            Files.createFile(fs.getPath("rzenada.bmp"));
            Files.createFile(fs.getPath("main/resources/h2/test.mv.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path filePath = fs.getPath("/src");
        PathNode fn = new PathNode(filePath);
        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();

        TreeOfFiles.createConvert(fn).subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(
                Stream.of("/src/sample.txt",
                        "/src/rzenada.bmp",
                        "/src/main",
                        "/src/main/resources",
                        "/src/main/resources/h2",
                        "/src/main/resources/h2/test.mv.db")
                        .map(e -> fs.getPath(e))
                        .collect(Collectors.toList()));
    }

    @Test
    public void shouldProduceEmptyStream() {
        PathNode fn = null;
        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();

        TreeOfFiles.createConvert(fn).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
    }
}