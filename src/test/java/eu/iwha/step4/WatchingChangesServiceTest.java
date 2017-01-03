package eu.iwha.step4;

import eu.iwha.SpringConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rx.observers.TestSubscriber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Created by iwha on 1/3/2017.
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = SpringConfig.class)
public class WatchingChangesServiceTest {

    @Autowired
    WatchingChangesService watchingChangesService;

    @Test
    public void autowiredServiceTest() throws IOException {
        String NEWFILEPATH = "/src/main";
        String NEWFILENAME = "newfile.txt";

        TestSubscriber<Path> testSubscriber = TestSubscriber.create();

            watchingChangesService.addSubscriber(testSubscriber);

            Files.createFile(watchingChangesService.getFs().getPath(NEWFILEPATH,NEWFILENAME));
            testSubscriber.awaitValueCount(1, 7000, TimeUnit.MILLISECONDS);

            testSubscriber.assertValue(watchingChangesService.getFs().getPath(NEWFILEPATH,NEWFILENAME));
    }

}