package de.sage.ready.web;

import de.sage.ready.ReadmeBot;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
@Controller
public class WebServer {

    public WebServer() {

    }

    public void startServer(){
        main(new String[0]);
    }

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(WebServer.class);
        app.setDefaultProperties(getProperties());

       ConfigurableApplicationContext config = app.run(args);


    }

    @RequestMapping(value="read/news", method=RequestMethod.GET)
    public void getDiscordRequest(HttpServletResponse httpR, @RequestParam String id) throws IOException {

        System.out.println("Someone requested picture with id: " + id);

        ReadmeBot.manager.registerWebRequest(id);


        InputStream in  = Files.newInputStream(Paths.get("./test.jpg"));
        httpR.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, httpR.getOutputStream());
    }

    private static Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();

        map.put("server.port", "9841");

        if(!ReadmeBot.testing) {
            map.put("server.ssl.key-store", "keystore.p12");
            map.put("server.ssl.key-store-password", "No more password .-.");
            map.put("server.ssl.key-store-type", "PCKS12");
            map.put("server.ssl.key-alias", "keystore");
            map.put("server.ssl.enabled", true);
        }

        return map;
    }


    //https://medium.com/nerd-for-tech/file-upload-with-springboot-and-mongodb-76a8f5b9f75d
    //https://o7planning.org/11765/spring-boot-file-download

  /*  @RequestMapping(value = "/userid/")
    public File hello() throws IOException {

        String base64String = "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkAQAAAABYmaj5AAAA7ElEQVR42tXUsZHEIAwFUHk2cHZuQDO0QeaWTAN4twK3REYbzNAAyhww1ombvd1NbBHeMQS8CPERAH+MAn9YBWCBzAEGTcR13W8cZaEpoLdpiuA6tIb86JWhHnH1tq7vyk4l53MR3fu0p2pZzbJ8JXiqYtHP6H53uBAH3mKadpg0HRZhRrCZNBHzxnWIadBUbILRbK/KzkXxRhEHNpumMuLXLPOZ4IVoz4flA5LTlTzkO+CkqeU/Sgy65G59q92QptbXLIEZVhXQsblDlxZIy8iPDsmrIn5mdiWui/QCoKr2pq35CUPRf/nBPvUNct67nP2Y9j8AAAAASUVORK5CYII=";

        // Convert Base64 String to File
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        File file = new File("userid.png");
        file.createNewFile();

        //Write the bytes to the file
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(decodedBytes);
        fos.close();

        return file;
    }

    @RequestMapping("/helloworld")
    public ModelAndView helloWorld() {
        String helloWorldMessage = "Hello world from java2blog!";
        return new ModelAndView("hello", "message", helloWorldMessage);
    }

    @GetMapping("/header")
    public ResponseEntity<String> usingResponseEntityBuilderAndHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Response with header using ResponseEntity");
    }*/

}
