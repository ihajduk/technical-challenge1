package step3;

import abs.AbstractFilesysPreparation;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import rx.subjects.ReplaySubject;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by iwha on 12/8/2016.
 */
public class WatchingChangesTest extends AbstractFilesysPreparation {

    @Test
    public void shouldNotifyWhenCreateFile() throws Exception {
        String NEWFILEPATH = "/src/main/";
        String NEWFILENAME = "newfile.txt";
        String NEWFILENAME2 = "newfile2.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        ReplaySubject<Path> testSubscriber = ReplaySubject.create();
        ReplaySubject<Path> testSubscriber2 = ReplaySubject.create();
        ReplaySubject<Path> testSubscriber3 = ReplaySubject.create();
        ReplaySubject<Path> testSubscriber4 = ReplaySubject.create();

        WatchingChanges pathObservable = WatchingChanges.watchChanges(root);
        pathObservable.subscribe(testSubscriber);
        pathObservable.subscribe(testSubscriber2);
        pathObservable.subscribe(testSubscriber3);
        Files.createFile(fs.getPath(NEWFILEPATH + NEWFILENAME));

        Assertions.assertThat(testSubscriber.toBlocking().first()).isEqualTo(fs.getPath(NEWFILENAME));  // TODO: recursive folders: nowy/nowy/dupa.txt
        Assertions.assertThat(testSubscriber2.toBlocking().first()).isEqualTo(fs.getPath(NEWFILENAME));
        Assertions.assertThat(testSubscriber3.toBlocking().first()).isEqualTo(fs.getPath(NEWFILENAME));
        pathObservable.close();
        pathObservable.subscribe(testSubscriber4);
        Files.createFile(fs.getPath(NEWFILEPATH + NEWFILENAME2));

    }
}