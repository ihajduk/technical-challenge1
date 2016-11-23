package step2;

import org.junit.Test;
import rx.observers.TestSubscriber;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by iwha on 11/23/2016.
 */
public class TreeOfFilesTest {
    @Test
    public void shouldProduceExpectedFileStream(){
        File file = new File("c:\\src");
        FileNode fn = new FileNode(file);
        TestSubscriber<File> testSubscriber = new TestSubscriber<>();

        TreeOfFiles.createConvert(fn).subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(
                Arrays.asList(
                        new File("c:\\src\\sample.txt"),
                        new File("c:\\src\\rzenada.bmp"),
                        new File("c:\\src\\main"),
                        new File("c:\\src\\main\\resources"),
                        new File("c:\\src\\main\\resources\\h2"),
                        new File("c:\\src\\main\\resources\\h2\\test.mv.db")
                )
        );
    }

    @Test
    public void shouldProduceEmptyStream(){
        FileNode fn = null;
        TestSubscriber<File> testSubscriber = new TestSubscriber<>();

        TreeOfFiles.createConvert(fn).subscribe(testSubscriber);

        testSubscriber.assertReceivedOnNext(Collections.emptyList());
    }
}