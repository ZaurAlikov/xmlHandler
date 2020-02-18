package ru.alcotester.pricehandler.model;

import com.google.api.services.gmail.model.MessagePart;

import java.util.Date;
import java.util.List;

public class EmailInfo {

    private String id;
    private String threadId;
    private Date date;
    private String subject;
    private String fromName;
    private String fromEmail;
    private String toName;
    private String toEmail;
    private String snippet;
    private String mimeType;
    private List<MessagePart> messageParts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<MessagePart> getMessageParts() {
        return messageParts;
    }

    public void setMessageParts(List<MessagePart> messageParts) {
        this.messageParts = messageParts;
    }
}
