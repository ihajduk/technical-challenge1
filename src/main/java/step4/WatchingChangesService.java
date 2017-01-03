package step4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import step3.WatchingChanges;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by iwha on 12/31/2016.
 */
@Service
public class WatchingChangesService {

    private static final Logger logger = LoggerFactory.getLogger(WatchingChangesService.class);
    public WatchingChanges watchingChanges;

    public WatchingChangesService(Path root) throws IOException, InterruptedException {
        watchingChanges = WatchingChanges.watchChanges(root);
    }
}

// TODO: integrate with Spring to get DI for service