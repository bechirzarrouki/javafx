package Controllers;

import Main.DatabaseConnection;
import Models.UserTableData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import Utils.AnimationUtils;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.File;
import java.io.IOException;
import javafx.stage.FileChooser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardController {

    @FXML
    private TableView<UserTableData> usersTable;

    @FXML
    private TableColumn<UserTableData, String> usernameColumn;

    @FXML
    private TableColumn<UserTableData, String> emailColumn;

    @FXML
    private TableColumn<UserTableData, Integer> numberColumn;

    @FXML
    private TableColumn<UserTableData, String> roleColumn;

    @FXML
    private TableColumn<UserTableData, Void> actionsColumn;

    @FXML
    private TextField searchField;

    private ObservableList<UserTableData> users = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configure les colonnes
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Configure la colonne d'actions
        actionsColumn.setCellFactory(createActionsColumnCallback());

        // Animate the table
        AnimationUtils.fadeInUp(usersTable);

        // Add row animation
        usersTable.setRowFactory(tv -> {
            TableRow<UserTableData> row = new TableRow<>();
            row.setOnMouseEntered(event -> {
                if (!row.isEmpty()) {
                    AnimationUtils.pulseAnimation(row);
                }
            });
            return row;
        });

        // Set up window focus listener to refresh table
        usersTable.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                            if (isNowFocused) {
                                loadUsers(); // Refresh when window regains focus
                            }
                        });
                    }
                });
            }
        });

        // --- Live search logic ---
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTable(newValue);
            });
        }

        // Charge les utilisateurs
        loadUsers();
    }

    private Callback<TableColumn<UserTableData, Void>, TableCell<UserTableData, Void>> createActionsColumnCallback() {
        return new Callback<>() {
            @Override
            public TableCell<UserTableData, Void> call(final TableColumn<UserTableData, Void> param) {
                return new TableCell<>() {
                    private final Button deleteBtn = new Button("Delete");
                    private final Button updateBtn = new Button("Update");
                    private final Button banBtn = new Button("Ban");
                    private final HBox buttons = new HBox(5, updateBtn, deleteBtn, banBtn);

                    {
                        deleteBtn.getStyleClass().add("delete-button");
                        updateBtn.getStyleClass().add("update-button");
                        banBtn.getStyleClass().add("ban-button");

                        // Add hover animations
                        deleteBtn.setOnMouseEntered(e -> AnimationUtils.pulseAnimation(deleteBtn));
                        updateBtn.setOnMouseEntered(e -> AnimationUtils.pulseAnimation(updateBtn));
                        banBtn.setOnMouseEntered(e -> AnimationUtils.pulseAnimation(banBtn));

                        deleteBtn.setOnAction(event -> {
                            UserTableData user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });

                        updateBtn.setOnAction(event -> {
                            UserTableData user = getTableView().getItems().get(getIndex());
                            handleUpdateUser(user);
                        });

                        banBtn.setOnAction(event -> {
                            UserTableData user = getTableView().getItems().get(getIndex());
                            handleBanUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            UserTableData user = getTableView().getItems().get(getIndex());
                            banBtn.setText(user.isBanned() ? "Unban" : "Ban");
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    private void loadUsers() {
        users.clear();
        try {
            Connection conn = DatabaseConnection.getInstance().getCnx();
            String query = "SELECT * FROM user WHERE roles != 'ROLE_ADMIN'";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserTableData user = new UserTableData(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getObject("number") != null ? rs.getInt("number") : null,
                    rs.getString("roles"),
                    rs.getBoolean("banned")
                );
                users.add(user);
            }

            usersTable.setItems(users);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading users: " + e.getMessage());
        }
    }

    private void handleDeleteUser(UserTableData user) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete User");
        confirmDialog.setContentText("Are you sure you want to delete user: " + user.getUsername() + "?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Connection conn = DatabaseConnection.getInstance().getCnx();
                    String query = "DELETE FROM user WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setInt(1, user.getId());
                    
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        users.remove(user);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user: " + e.getMessage());
                }
            }
        });
    }

    private void handleUpdateUser(UserTableData user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/update_user_dialog.fxml"));
            Parent root = loader.load();
            
            UpdateUserDialogController controller = loader.getController();
            controller.setUser(user);
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(usersTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            dialogStage.setScene(scene);
            
            dialogStage.showAndWait();
            
            if (controller.isSaveClicked()) {
                UserTableData updatedUser = controller.getUser();
                Connection conn = DatabaseConnection.getInstance().getCnx();
                String query = "UPDATE user SET username = ?, email = ?, number = ?, roles = ? WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, updatedUser.getUsername());
                ps.setString(2, updatedUser.getEmail());
                ps.setInt(3, updatedUser.getNumber());
                ps.setString(4, updatedUser.getRole());
                ps.setInt(5, updatedUser.getId());
                
                int result = ps.executeUpdate();
                if (result > 0) {
                    usersTable.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully");
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user: " + e.getMessage());
        }
    }

    private void handleBanUser(UserTableData user) {
        try {
            Connection conn = DatabaseConnection.getInstance().getCnx();
            String query = "UPDATE user SET banned = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBoolean(1, !user.isBanned());
            ps.setInt(2, user.getId());
            
            int result = ps.executeUpdate();
            if (result > 0) {
                user.setBanned(!user.isBanned());
                usersTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    user.isBanned() ? "User banned successfully" : "User unbanned successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user ban status: " + e.getMessage());
        }
    }

    @FXML
    private void handleGeneratePDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("users.pdf");
        File file = fileChooser.showSaveDialog(usersTable.getScene().getWindow());
        if (file == null) return;

        float[] colWidths = {110, 180, 90, 110};
        String[] headers = {"Username", "Email", "Phone", "Role"};
        int cols = headers.length;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float margin = 50;
            float yStart = PDRectangle.A4.getHeight() - margin;
            float tableTopY = yStart - 30;
            float rowHeight = 24;
            float tableWidth = 0;
            for (float w : colWidths) tableWidth += w;
            float tableLeftX = margin;
            float y = tableTopY;

            // Title
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(tableLeftX, y + 40);
            contentStream.showText("Users Table");
            contentStream.endText();

            // Header background
            contentStream.setNonStrokingColor(new java.awt.Color(230, 230, 230));
            contentStream.addRect(tableLeftX, y, tableWidth, rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(java.awt.Color.BLACK);

            // Draw table grid
            int numRows = usersTable.getItems().size() + 1;
            for (int i = 0; i <= numRows; i++) {
                float lineY = y - i * rowHeight;
                contentStream.moveTo(tableLeftX, lineY);
                contentStream.lineTo(tableLeftX + tableWidth, lineY);
            }
            float nextX = tableLeftX;
            for (int i = 0; i <= cols; i++) {
                contentStream.moveTo(nextX, y);
                contentStream.lineTo(nextX, y - numRows * rowHeight);
                if (i < cols) nextX += colWidths[i];
            }
            contentStream.setLineWidth(0.8f);
            contentStream.stroke();

            // Write headers (bold, left-aligned, padded)
            float cellX = tableLeftX;
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            for (int i = 0; i < cols; i++) {
                float textX = cellX + 8;
                float textY = y + rowHeight / 2 - 5;
                contentStream.beginText();
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(headers[i]);
                contentStream.endText();
                cellX += colWidths[i];
            }

            // Write data rows (left-aligned, padded)
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            int rowNum = 1;
            for (UserTableData user : usersTable.getItems()) {
                float rowY = y - rowNum * rowHeight;
                cellX = tableLeftX;
                String[] vals = {
                    user.getUsername(),
                    user.getEmail(),
                    user.getNumber() != null ? user.getNumber().toString() : "",
                    user.getRole()
                };
                for (int i = 0; i < cols; i++) {
                    float textX = cellX + 8;
                    float textY = rowY + rowHeight / 2 - 5;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textX, textY);
                    contentStream.showText(vals[i]);
                    contentStream.endText();
                    cellX += colWidths[i];
                }
                rowNum++;
                // Handle page break if needed
                if (rowY - rowHeight < margin) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    y = tableTopY;
                    rowNum = 1;
                }
            }

            contentStream.close();
            document.save(file);
            showAlert(Alert.AlertType.INFORMATION, "PDF Generated", "PDF exported successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "PDF Error", "Failed to generate PDF: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            
            Stage stage = (Stage) usersTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading login page: " + e.getMessage());
        }
    }

    public void refreshTable() {
        loadUsers();
    }

    private void filterTable(String filter) {
        if (filter == null || filter.isEmpty()) {
            usersTable.setItems(users);
            return;
        }
        ObservableList<UserTableData> filtered = FXCollections.observableArrayList();
        for (UserTableData user : users) {
            if (user.getUsername().toLowerCase().contains(filter.toLowerCase()) ||
                user.getEmail().toLowerCase().contains(filter.toLowerCase()) ||
                (user.getNumber() != null && user.getNumber().toString().contains(filter)) ||
                user.getRole().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(user);
            }
        }
        usersTable.setItems(filtered);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
