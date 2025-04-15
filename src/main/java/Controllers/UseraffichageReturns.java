package Controllers;

import Models.Investment;
import Models.TReturn;
import Services.TReturnService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UseraffichageReturns {
    private Investment investment;
    private final TReturnService service = new TReturnService();
    private static final int ITEMS_PER_PAGE = 5;

    @FXML private Pagination pagination;

    @FXML
    public void initialize() {
        pagination.setPageFactory(this::createPage);
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
        updatePagination();
    }

    private void updatePagination() {
        List<TReturn> returns = service.getByInvestmentId(investment.getId());
        int pageCount = (int) Math.ceil((double) returns.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
    }

    private Node createPage(int pageIndex) {
        VBox pageBox = new VBox(15);
        pageBox.setAlignment(Pos.TOP_CENTER);
        pageBox.setPadding(new Insets(20));

        List<TReturn> returns = service.getByInvestmentId(investment.getId());
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, returns.size());

        List<TReturn> currentPageReturns = returns.subList(fromIndex, toIndex);

        for (TReturn tReturn : currentPageReturns) {
            HBox returnBox = createReturnBox(tReturn);
            pageBox.getChildren().add(returnBox);
        }

        return pageBox;
    }

    private HBox createReturnBox(TReturn tReturn) {
        HBox returnBox = new HBox(20);
        returnBox.getStyleClass().add("return-box");
        returnBox.setAlignment(Pos.CENTER_LEFT);
        returnBox.setPadding(new Insets(15));

        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
                createInfoLabel("Description: " + tReturn.getDescription()),
                createInfoLabel("Type: " + tReturn.getReturnType()),
                createStatusLabel(tReturn.getStatus()),
                createInfoLabel("Deadline: " + tReturn.getDeadline())
        );

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);


        returnBox.getChildren().addAll(infoBox, buttonBox);
        return returnBox;
    }
    private Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("info-label");
        label.setWrapText(true);
        return label;
    }

    private Label createStatusLabel(String status) {
        Label label = new Label("Status: " + status);
        label.getStyleClass().add(status.equalsIgnoreCase("completed") ? "status-completed" : "status-pending");
        return label;
    }

    private Button createActionButton(String text, String styleClass, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setMinWidth(80);
        button.setOnAction(event -> action.run());
        return button;
    }

}
