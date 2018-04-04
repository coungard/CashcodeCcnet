package BillToBill;

import helper.MenuItem;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jssc.SerialPortList;

public class Display extends Application {

    Pane root;
    String[] ports;
    Label label;

    private Pane createRoot() {
        root = new Pane();
        root.setPrefSize(600,400);
        ports = SerialPortList.getPortNames();
        label = new Label("Выберите com-port, к которому подключенно устройство");
        root.getChildren().add(label);
        label.setTranslateX(30);

        for (int i = 0; i < ports.length; i++) {
            MenuItem item = new MenuItem(ports[i]);
            root.getChildren().add(item);
            item.setTranslateX(20);
            item.setTranslateY(i*30 + 20);
        }

        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(createRoot());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Logger");
        primaryStage.getIcons().add(new Image("loggerIcon.jpg"));

        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
