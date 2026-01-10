package com.example.roy_2207099_crictrack_desktop;

import javafx.beans.property.*;

import javafx.scene.layout.HBox;

public class RequestRow {

    private final IntegerProperty requestId = new SimpleIntegerProperty();
    private final StringProperty userName = new SimpleStringProperty();
    private final StringProperty match = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<HBox> action = new SimpleObjectProperty<>();

    public RequestRow(int requestId, String userName, String match,
                      String status, HBox action) {
        this.requestId.set(requestId);
        this.userName.set(userName);
        this.match.set(match);
        this.status.set(status);
        this.action.set(action);
    }

    public IntegerProperty requestIdProperty() { return requestId; }
    public StringProperty userNameProperty() { return userName; }
    public StringProperty matchProperty() { return match; }
    public StringProperty statusProperty() { return status; }
    public ObjectProperty<HBox> actionProperty() { return action; }

    public int getRequestId() { return requestId.get(); }
    public String getUserName() { return userName.get(); }
    public String getMatch() { return match.get(); }
    public String getStatus() { return status.get(); }
    public HBox getAction() { return action.get(); }
}
