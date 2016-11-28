package step2;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
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
                .setRoots("/")
                .setWorkingDirectory("/src")
                .build());

        try {
            Files.createDirectories(fs.getPath("resources/h2"));
            Files.createFile(fs.getPath("sample.txt"));
            Files.createFile(fs.getPath("rzenada.bmp"));
            Files.createFile(fs.getPath("resources/h2/test.mv.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldProduceExpectedFileStream() {
        File file = new File(fs.getPath("/src").toString());
        FileNode fn = new FileNode(file);
        TestSubscriber<File> testSubscriber = new TestSubscriber<>();

        TreeOfFiles.createConvert(fn).subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(
                Stream.of("/src/sample.txt",
                        "/src/rzenada.bmp",
                        "/src/main",
                        "/src/main/resources",
                        "/src/main/resources/h2",
                        "/src/main/resources/h2/test.mv.db")
                        .map(e -> new File(e))
                        .collect(Collectors.toList()));
    }

    @Test
    public void shouldProduceEmptyStream() {
        FileNode fn = null;
        TestSubscriber<File> testSubscriber = new TestSubscriber<>();

        TreeOfFiles.createConvert(fn).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
    }
}