package eu.iwha;

import com.google.common.jimfs.Jimfs;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import eu.iwha.step3.WatchingChanges;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by iwha on 1/3/2017.
 */
@Configuration
@ComponentScan(basePackages = "eu.iwha.step4")
public class SpringConfig {

    @Bean
    public FileSystem fs(){
        FileSystem fileSys = Jimfs.newFileSystem(com.google.common.jimfs.Configuration.unix()
                .toBuilder()
                .setMaxSize(1024 * 1024 * 1024) // 1 GB
                .setMaxCacheSize(256 * 1024 * 1024) // 256 MB
                .setRoots("/")
                .setWorkingDirectory("/src")
                .build());
        try {
            Files.createDirectories(fileSys.getPath("main/resources/h2"));
            Files.createFile(fileSys.getPath("sample.txt"));
            Files.createFile(fileSys.getPath("rzenada.bmp"));
            Files.createFile(fileSys.getPath("main/resources/h2/test.mv.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSys;
    }

    @Bean
    public WatchingChanges watchingChanges() throws IOException, InterruptedException {
        return WatchingChanges.watchChanges(root());
    }

    @Bean
    public Path root(){
        return fs().getPath("/src");
    }

}


/* TODO:
1. Beans: fs, watchingChanges DONE ; -)
2. Test the service
3. Create websocket
 */