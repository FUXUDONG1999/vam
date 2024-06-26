package com.xudong.vam;

import com.xudong.vam.selector.PackageSelector;
import com.xudong.vam.service.SelectService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;

@Slf4j
@SpringBootApplication
public class VamApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(loadFXML(), 320, 240);
        stage.setTitle("Vam");
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadFXML() throws IOException {
        return new FXMLLoader(getClass().getResource("vam-application.fxml")).load();
    }

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new SpringApplicationBuilder(VamApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE)
                .run(args);

        SelectService selectService = context.getBean(SelectService.class);
        long selectId = selectService.createSelect("test");

        PackageSelector selector = context.getBean(PackageSelector.class);
        List<Long> ids = selector.select(selectId, 18);
        selector.unselect(1);

        launch(args);
    }
}