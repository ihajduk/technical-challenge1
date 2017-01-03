package eu.iwha.step3;

import abs.AbstractFilesysPreparation;
import org.junit.Test;
import rx.observers.TestSubscriber;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Created by iwha on 12/8/2016.
 */

public class WatchingChangesTest extends AbstractFilesysPreparation {

    @Test
    public void shouldNotifyEverySubscriber() throws Exception {
        String NEWFILEPATH = "/src/main";
        String NEWFILENAME = "newfile.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        TestSubscriber<Path> testSubscriber1 = TestSubscriber.create();
        TestSubscriber<Path> testSubscriber2 = TestSubscriber.create();
        TestSubscriber<Path> testSubscriber3 = TestSubscriber.create();

        try (WatchingChanges pathObservable = WatchingChanges.watchChanges(root)) {
            pathObservable.subscribe(testSubscriber1);
            pathObservable.subscribe(testSubscriber2);
            pathObservable.subscribe(testSubscriber3);
            Files.createFile(fs.getPath(NEWFILEPATH,NEWFILENAME));
            testSubscriber1.awaitValueCount(1, 7000, TimeUnit.MILLISECONDS);

            testSubscriber1.assertValue(fs.getPath(NEWFILEPATH,NEWFILENAME));
            testSubscriber2.assertValue(fs.getPath(NEWFILEPATH,NEWFILENAME));
            testSubscriber3.assertValue(fs.getPath(NEWFILEPATH,NEWFILENAME));
        }
    }

    @Test
    public void shouldNotifyWhenCreateMultipleFiles() throws Exception {
        String NEWFILEPATH = "/src/main";
        String NEWFILEPATH2 = "/src/main/resources";
        String NEWFILENAME = "newfile1.txt";
        String NEWFILENAME2 = "newfile2.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        TestSubscriber<Path> testSubscriber = TestSubscriber.create();

        try (WatchingChanges pathObservable = WatchingChanges.watchChanges(root)) {
            pathObservable.subscribe(testSubscriber);
            Files.createFile(fs.getPath(NEWFILEPATH,NEWFILENAME));
            Files.createFile(fs.getPath(NEWFILEPATH2,NEWFILENAME2));
            testSubscriber.awaitValueCount(2, 7000, TimeUnit.MILLISECONDS);
            testSubscriber.assertValues(fs.getPath(NEWFILEPATH2,NEWFILENAME2), fs.getPath(NEWFILEPATH,NEWFILENAME));
        }
    }

    @Test
    public void shouldNotReceiveEventsAfterClose() throws Exception {
        String NEWFILEPATH = "/src/main";
        String NEWFILENAME2 = "newfileclosed2.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        TestSubscriber<Path> testSubscriber = TestSubscriber.create();

        WatchingChanges pathObservable = WatchingChanges.watchChanges(root);
        pathObservable.close();
        pathObservable.subscribe(testSubscriber);
        Files.createFile(fs.getPath(NEWFILEPATH,NEWFILENAME2));

        testSubscriber.awaitValueCount(1, 7000, TimeUnit.MILLISECONDS);
        testSubscriber.assertNoValues();
    }

    @Test
    public void shouldNotifyOnRecursiveCreation() throws Exception {
        String NEWFOLDER = "/src/main/newfolder";
        String NEWRECURSIVEFILENAME = "newrecursivefile.txt";
        String WORKDIR = "/src";
        Path root = fs.getPath(WORKDIR);
        TestSubscriber<Path> testSubscriber = TestSubscriber.create();

        try (WatchingChanges pathObservable = WatchingChanges.watchChanges(root)) {
            pathObservable.subscribe(testSubscriber);
            Files.createDirectory(fs.getPath(NEWFOLDER));
            testSubscriber.awaitValueCount(1, 7000, TimeUnit.MILLISECONDS);
            testSubscriber.assertValues(fs.getPath(NEWFOLDER));
            Files.createFile(fs.getPath(NEWFOLDER,NEWRECURSIVEFILENAME));
            testSubscriber.awaitValueCount(2, 7000, TimeUnit.MILLISECONDS);
            testSubscriber.assertValues(fs.getPath(NEWFOLDER), fs.getPath(NEWFOLDER,NEWRECURSIVEFILENAME));
        }
    }
}
















