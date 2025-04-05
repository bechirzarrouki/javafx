package Models;

import java.time.LocalDateTime;

public class Messages {
    private Integer id;
    private Integer chatId;
    private Integer senderId;
    private String content;
    private LocalDateTime sentAt;

    public Messages() {
        this.sentAt = LocalDateTime.now();
    }

    public Messages(Integer id, Integer chatId, Integer senderId, String content) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Messages{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }
}
