package com.example.roy_2207099_crictrack_desktop;

import javafx.beans.property.*;
import javafx.scene.layout.HBox;

public class MatchRow {
    private final IntegerProperty matchId;
    private final StringProperty teams;
    private final StringProperty venue;
    private final StringProperty date;
    private final StringProperty result;
    private final ObjectProperty<HBox> action;

    public MatchRow(int matchId, String teams, String venue, String date, String result, HBox action) {
        this.matchId = new SimpleIntegerProperty(matchId);
        this.teams = new SimpleStringProperty(teams);
        this.venue = new SimpleStringProperty(venue);
        this.date = new SimpleStringProperty(date);
        this.result = new SimpleStringProperty(result);
        this.action = new SimpleObjectProperty<>(action);
    }

    public IntegerProperty matchIdProperty() { return matchId; }
    public StringProperty teamsProperty() { return teams; }
    public StringProperty venueProperty() { return venue; }
    public StringProperty dateProperty() { return date; }
    public StringProperty resultProperty() { return result; }
    public ObjectProperty<HBox> actionProperty() { return action; }
}
