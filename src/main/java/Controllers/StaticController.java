package Controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;

import java.util.Random;

public class StaticController {

    @FXML
    private ComboBox<String> selectOption;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private LineChart<String, Number> areaChart;

    private String[] labels = {"year1", "year2", "year3", "year4", "year5"};
    private Random random = new Random();

    @FXML
    public void initialize() {
        // Ajouter les options de la liste déroulante
        selectOption.getItems().addAll("Option 1", "Option 2", "Option 3");
        updateRandomValues();
    }

    @FXML
    private void updateRandomValues() {
        // Générer des valeurs aléatoires pour les graphiques
        XYChart.Series<String, Number> barData = new XYChart.Series<>();
        XYChart.Series<String, Number> areaData = new XYChart.Series<>();

        for (String label : labels) {
            int value = random.nextInt(50) + 1; // Valeurs entre 1 et 50
            barData.getData().add(new XYChart.Data<>(label, value));
            areaData.getData().add(new XYChart.Data<>(label, value));
        }

        // Mettre à jour les graphiques
        barChart.getData().clear();
        areaChart.getData().clear();
        barChart.getData().add(barData);
        areaChart.getData().add(areaData);
    }
}
