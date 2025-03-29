package Main;

import Models.User;
import Models.Post;
import Services.UserServices;
import Services.PostServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {
        // Ensure the FXML file is placed in src/main/resources
        Parent root = FXMLLoader.load(getClass().getResource("/Feed.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("My JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    public static void main(String[] args) {
        DatabaseConnection.getInstance();
        launch(args);
    }
}