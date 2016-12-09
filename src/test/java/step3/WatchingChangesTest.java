package step3;

import abs.AbstractFilesysPreparation;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Created by iwha on 12/8/2016.
 */
public class WatchingChangesTest extends AbstractFilesysPreparation {

    @Test
    public void shouldNotifyWhenCreateFile() throws IOException {
        String NEWFILE = "/src/main/newfile.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        TestSubscriber<Path> testSubscriber = new TestSubscriber<>();

        WatchingChanges.watchChanges(root).subscribe(testSubscriber);
        Files.createFile(fs.getPath(NEWFILE));

        testSubscriber.assertReceivedOnNext(Collections.singletonList(fs.getPath(NEWFILE)));
    }
}