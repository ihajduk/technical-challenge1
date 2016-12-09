package step3;

import abs.AbstractFilesysPreparation;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.subjects.ReplaySubject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by iwha on 12/8/2016.
 */
public class WatchingChangesTest extends AbstractFilesysPreparation {  // TODO: change on static class

    @Test
    public void shouldNotifyWhenCreateFile() throws IOException {
        String NEWFILEPATH = "/src/main/";
        String NEWFILENAME = "newfile.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        ReplaySubject<Path> testSubscriber = ReplaySubject.create();

        WatchingChanges.watchChanges(root).subscribe(testSubscriber);
        Files.createFile(fs.getPath(NEWFILEPATH+NEWFILENAME));

        Assertions.assertThat(testSubscriber.toBlocking().first()).isEqualTo(fs.getPath(NEWFILENAME));  // TODO: recursive folders: nowy/nowy/dupa.txt
    }
}