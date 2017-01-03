package eu.iwha.step2;

import abs.AbstractFilesysPreparation;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by iwha on 11/23/2016.
 */
public class TreeOfFilesTest extends AbstractFilesysPreparation {

    @Test
    public void shouldProduceExpectedFileStream() {
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