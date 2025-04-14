package Controllers;

import Models.Investment;
import Models.Messages;
import Services.MessagesServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

public class MessageController {

    @FXML
    private VBox messagesContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    private Investment currentInvestment;

    private MessagesServices messagesServices = new MessagesServices();

    public void setInvestmentId(Investment investment) {
        this.currentInvestment = investment;
        loadMessages(); // Charger les messages dès qu’on reçoit l’investissement
    }

    // Charger les messages associés à cet investissement (chatId)
    private void loadMessages() {
        messagesContainer.getChildren().clear();

        List<Messages> messagesList = messagesServices.getByChatId(currentInvestment.getId());

        for (Messages message : messagesList) {
            Label messageLabel = new Label(message.getSenderId() + " : " + message.getContent()
                    + " (" + message.getSentAt().toString() + ")");
            messageLabel.setWrapText(true);
            messageLabel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 8; -fx-background-radius: 6;");
            messagesContainer.getChildren().add(messageLabel);
        }
    }

    @FXML
    public void initialize() {
        // Action bouton "Envoyer"
        sendButton.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        String content = messageInput.getText().trim();
        if (content.isEmpty() || currentInvestment == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Message");
            alert.setHeaderText(null);
            alert.setContentText("Please type a message before sending.");
            alert.showAndWait();
            return;
        }

        // Exemple : senderId = 1 (tu pourras remplacer ça par le user connecté)
        Messages newMessage = new Messages();
        newMessage.setChatId(currentInvestment.getId());
        newMessage.setSenderId(1);
        newMessage.setContent(content);
        newMessage.setSentAt(LocalDateTime.now());

        messagesServices.add(newMessage);
        messageInput.clear();
        loadMessages();
    }
}
