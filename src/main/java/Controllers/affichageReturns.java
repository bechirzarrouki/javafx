package Controllers;

import Models.Investment;
import Models.TReturn;
import Services.TReturnService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.VBox;
import java.util.List;

public class affichageReturns {
    private Investment investment;

    @FXML
    private VBox returnListContainer;

    @FXML
    private Pagination pagination;
    public void initialize() {
        loadReturns();
        System.out.println(pagination.getPageCount());
    }
    TReturnService service = new TReturnService();
    private void loadReturns() {
        List<TReturn> returns = service.getAll();
        returnListContainer.getChildren().clear();

        for (TReturn tReturn : returns) {
            Label label = new Label(
                    "Return: " + tReturn.getDescription() +
                            ", Type: " + tReturn.getReturnType() +
                            ", Status: " + tReturn.getStatus() +
                            ", Deadline: " + tReturn.getDeadline()
            );
            label.getStyleClass().add("return-label");
            returnListContainer.getChildren().add(label);
        }

        // Optionnel : configurer pagination si besoin
        pagination.setPageCount(1);
    }
}
