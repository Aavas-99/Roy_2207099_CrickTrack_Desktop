package com.example.roy_2207099_crictrack_desktop;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class ScoreUpdateController {
    private String battingTeam;
    private String bowlingTeam;
    private ArrayList<String> battingPlayers;
    private ArrayList<String> bowlingPlayers;
    private int oversLimit;
    private String stadium;
    private String date;
    private int matchId = -1;
    private String tossWinner;
    private String decision;
    private String firstInningsBattingTeam = null;
    private int firstInningsTotal = -1;

    private int currentOver = 0;
    private int currentBall = 0;
    private int wickets = 0;
    private int totalRuns = 0;
    private boolean firstInnings = true;
    private boolean isFreeHit = false;

    private int strikerIndex = 0;
    private int nonStrikerIndex = 1;
    private int currentBowlerIndex = 5;

    private ArrayList<Batsman> batsmenStats = new ArrayList<>();
    private ArrayList<Bowler> bowlerStats = new ArrayList<>();
    private ArrayList<BallEvent> ballHistory = new ArrayList<>();

    @FXML private Label lblBatsman1;
    @FXML private Label lblBatsman2;
    @FXML private Label lblBowler;
    @FXML private Label lblScore;
    @FXML private Label lblOvers;
    @FXML private Label lblTeamName;
    @FXML private Label lblStadium;
    @FXML private Label lblTossWinner;
    @FXML private Label lblDecision;
    @FXML private Label lblTarget;
    @FXML private Button btn1, btn2, btn3, btn4, btn6, btnWide, btnNoBall, btnLegBye, btnBye, btnWicket, btnUndo, btn0;
    @FXML private TableView<Batsman> tblBatsmen;
    @FXML private TableView<Bowler> tblBowlers;

    @FXML private TableColumn<Batsman, String> batsmanColName;
    @FXML private TableColumn<Batsman, Integer> batsmanColRuns;
    @FXML private TableColumn<Batsman, Integer> batsmanColBalls;
    @FXML private TableColumn<Batsman, String> batsmanColStatus;

    @FXML private TableColumn<Bowler, String> bowlerColName;
    @FXML private TableColumn<Bowler, Integer> bowlerColOvers;
    @FXML private TableColumn<Bowler, Integer> bowlerColRuns;
    @FXML private TableColumn<Bowler, Integer> bowlerColWickets;

    @FXML
    public void initialize() {
        if (batsmanColName != null) batsmanColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (batsmanColRuns != null) batsmanColRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        if (batsmanColBalls != null) batsmanColBalls.setCellValueFactory(new PropertyValueFactory<>("balls"));
        if (batsmanColStatus != null) batsmanColStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isOut() ? "Out" : "Not Out"));
        if (bowlerColName != null) bowlerColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (bowlerColOvers != null) bowlerColOvers.setCellValueFactory(new PropertyValueFactory<>("overs"));
        if (bowlerColRuns != null) bowlerColRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        if (bowlerColWickets != null) bowlerColWickets.setCellValueFactory(new PropertyValueFactory<>("wickets"));

        if (tblBatsmen != null) tblBatsmen.getItems().clear();
        if (tblBowlers != null) tblBowlers.getItems().clear();
    }
    public void initMatchData(int MatchId,String battingTeam, String bowlingTeam, ArrayList<String> battingPlayers,
                              ArrayList<String> bowlingPlayers, int overs, String stadium, String date) {
        this.matchId=MatchId;
        initMatchData(battingTeam, bowlingTeam, battingPlayers, bowlingPlayers, overs, stadium, date , null, null);
    }
    public void initMatchData(String battingTeam, String bowlingTeam, ArrayList<String> battingPlayers, ArrayList<String> bowlingPlayers, int overs, String stadium, String date, String tossWinner, String decision) {
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.battingPlayers = battingPlayers;
        this.bowlingPlayers = bowlingPlayers;
        this.oversLimit = overs;
        this.stadium = stadium;
        this.date = date;
        this.tossWinner = tossWinner;
        this.decision = decision;
        batsmenStats.clear();
        for (String player : battingPlayers) {
            batsmenStats.add(new Batsman(player));
        }
        bowlerStats.clear();
        for (String bowler : bowlingPlayers) {
            bowlerStats.add(new Bowler(bowler));
        }
        strikerIndex = 0;
        nonStrikerIndex = 1;
        currentBowlerIndex = 5;
        ensureStatsTables();

        if (lblStadium != null) lblStadium.setText(this.stadium == null ? "-" : this.stadium);
        if (lblTossWinner != null) lblTossWinner.setText(this.tossWinner == null ? "-" : this.tossWinner);
        if (lblDecision != null) lblDecision.setText(this.decision == null ? "-" : this.decision);
        if (lblTarget != null) lblTarget.setText("N/A");
        updateLabels();
        setupButtonHandlers();
    }

    public void setMatchMeta(int matchId, String tossWinner, String decision) {
        this.matchId = matchId;
        this.tossWinner = tossWinner;
        this.decision = decision;
        if (lblTossWinner != null) lblTossWinner.setText(this.tossWinner == null ? "-" : this.tossWinner);
        if (lblDecision != null) lblDecision.setText(this.decision == null ? "-" : this.decision);
        System.out.println("ScoreUpdateController: setMatchMeta called with matchId=" + this.matchId + ", tossWinner=" + this.tossWinner + ", decision=" + this.decision);

        if (this.matchId != -1) {
            try (Connection conn = Database.getConnection()) {
                if (conn != null) {
                    String q = "SELECT id, team_a, team_b, overs, stadium, date, toss_winner, decision, first_innings_total, result FROM matches WHERE id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(q)) {
                        ps.setInt(1, this.matchId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs != null && rs.next()) {
                                System.out.println("ScoreUpdateController: match row found for id=" + this.matchId + ": team_a=" + rs.getString("team_a") + ", team_b=" + rs.getString("team_b") + ", toss_winner=" + rs.getString("toss_winner") + ", decision=" + rs.getString("decision") + ", first_innings_total=" + rs.getObject("first_innings_total"));
                                Object fit = rs.getObject("first_innings_total");
                                if (fit != null && !fit.toString().equals("null")) {
                                    try {
                                        int fitInt = Integer.parseInt(fit.toString());
                                        firstInningsTotal = fitInt;
                                        if (lblTarget != null && !firstInnings) lblTarget.setText(String.valueOf(fitInt + 1));
                                    } catch (NumberFormatException nfe) {
                                        System.out.println("ScoreUpdateController: failed to parse first_innings_total: " + nfe.getMessage());
                                    }
                                }
                            } else {
                                System.out.println("ScoreUpdateController: no match row found for id=" + this.matchId);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("ScoreUpdateController: failed to query match id " + this.matchId + ": " + e.getMessage());
            }
        } else {
            System.out.println("ScoreUpdateController: setMatchMeta called with matchId = -1");
        }
    }

    private void updateLabels() {
        if (strikerIndex >= 0 && strikerIndex < batsmenStats.size()) {
            lblBatsman1.setText(batsmenStats.get(strikerIndex).getName());
        } else {
            lblBatsman1.setText("-");
        }
        if (nonStrikerIndex >= 0 && nonStrikerIndex < batsmenStats.size()) {
            lblBatsman2.setText(batsmenStats.get(nonStrikerIndex).getName());
        } else {
            lblBatsman2.setText("-");
        }

        lblBowler.setText(bowlerStats.get(currentBowlerIndex).getName());
        lblScore.setText(totalRuns + "/" + wickets);
        lblOvers.setText("Overs: " + currentOver + "." + currentBall);
        lblTeamName.setText(battingTeam);

        updateBatsmenTable();
        updateBowlersTable();
        saveStatsToDB();
    }

    private void updateBatsmenTable() {
        tblBatsmen.getItems().clear();
        tblBatsmen.getItems().addAll(batsmenStats);
    }
    private void updateBowlersTable() {
        tblBowlers.getItems().clear();
        tblBowlers.getItems().addAll(bowlerStats);
    }
    private Integer askRunsWithSpinner(String title) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        Spinner<Integer> spinner = new Spinner<>(0, 4, 1);
        spinner.setEditable(false);

        dialog.getDialogPane().setContent(spinner);

        ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, cancelBtn);

        dialog.setResultConverter(button -> {
            if (button == okBtn) return spinner.getValue();
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private void setupButtonHandlers() {
        btn1.setOnAction(e -> handleBall(1, false, false, false,false));
        btn2.setOnAction(e -> handleBall(2, false, false, false,false));
        btn3.setOnAction(e -> handleBall(3, false, false, false,false));
        btn4.setOnAction(e -> handleBall(4, false, false, false,false));
        btn6.setOnAction(e -> handleBall(6, false, false, false,false));
        btn0.setOnAction(e -> handleBall(0, false, false, false,false));
        btnWide.setOnAction(e -> handleBall(1, true, false, false, false));
        btnNoBall.setOnAction(e -> {handleBall(1, true, false, false,true); isFreeHit = true;});
        btnBye.setOnAction(e -> {
            Integer runs = askRunsWithSpinner("Bye Runs");
            if (runs != null) {handleBall(runs, false, false,true,false);}
        });

        btnLegBye.setOnAction(e -> {
            Integer runs = askRunsWithSpinner("Leg-bye Runs");
            if (runs != null) handleBall(runs, false, false,true,false);
        });

        btnWicket.setOnAction(e -> handleBall(0, false, true, false,false));
        btnUndo.setOnAction(e -> undoLastBall());
    }
    private void handleBall(int runs, boolean isExtra, boolean isWicket, boolean isblb,boolean isNo) {
        if (strikerIndex == -1) {
            saveStatsToDB();
            showAlert("Innings Over", "All batsmen are out!");
            return;
        }
        boolean actualWicket = isWicket ;
        if( isFreeHit && isWicket)
        {
            showAlert("Free Hit", "Cannot have a wicket on a Free Hit ball!");
            actualWicket = false;
        }

        BallEvent event = new BallEvent(strikerIndex, nonStrikerIndex, currentBowlerIndex, runs, isExtra,
                isWicket, currentOver, currentBall, wickets, totalRuns, isblb,isNo);
        ballHistory.add(event);
        if(isblb)
        {
            Batsman striker = batsmenStats.get(strikerIndex);
            striker.setBalls(striker.getBalls() + 1);
            totalRuns += runs;
            Bowler bowler = bowlerStats.get(currentBowlerIndex);
            bowler.setRuns(bowler.getRuns() + runs);
            bowler.incrementBallsBowled();
            isFreeHit = false;
        }
        else if (!isExtra) {
            Batsman striker = batsmenStats.get(strikerIndex);
            striker.setRuns(striker.getRuns() + runs);
            striker.setBalls(striker.getBalls() + 1);
            totalRuns += runs;
            Bowler bowler = bowlerStats.get(currentBowlerIndex);
            bowler.setRuns(bowler.getRuns() + runs);
            bowler.incrementBallsBowled();
            isFreeHit = false;
        } else {
            totalRuns += runs;
            if(isNo)
            {
                isFreeHit = true;
            }
            bowlerStats.get(currentBowlerIndex).setRuns(bowlerStats.get(currentBowlerIndex).getRuns() + runs);
        }

        if (!firstInnings && firstInningsTotal != -1 && totalRuns > firstInningsTotal) {
            int remainingWickets = Math.max(0, (battingPlayers != null ? battingPlayers.size() : 0) - wickets);
            String resultText = battingTeam + " won by " + remainingWickets + " wickets";
            saveStatsToDB();
            if (matchId != -1) {
                try (Connection conn = Database.getConnection()) {
                    if (conn != null) {
                        String update = "UPDATE matches SET result = ? WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(update)) {
                            ps.setString(1, resultText);
                            ps.setInt(2, matchId);
                            ps.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Failed to save match result (early finish): " + e.getMessage());
                }
            }

            showAlert("Match Over", "Match ended!\n" + resultText);
            Stage stage = (Stage) lblTeamName.getScene().getWindow();
            stage.close();
            return;
        }

        if (actualWicket) {
            wickets++;
            bowlerStats.get(currentBowlerIndex).setWickets(bowlerStats.get(currentBowlerIndex).getWickets() + 1);
            batsmenStats.get(strikerIndex).setOut(true);
            strikerIndex = getNextBatsman();
            if (strikerIndex == -1) {
                endInnings();
                return;
            }
        }

        if (!isExtra) currentBall++;
        if (currentBall == 6) {
            currentOver++;
            currentBall = 0;
            swapStriker();
            currentBowlerIndex = getNextBowler();
        }
        if (!isExtra && (runs == 1 || runs == 3)) {
            swapStriker();
        }
        if (currentOver == oversLimit) {
            endInnings();
            return;
        }
        updateLabels();
    }

    private void swapStriker() {
        int temp = strikerIndex;
        strikerIndex = nonStrikerIndex;
        nonStrikerIndex = temp;
    }

    private int getNextBatsman() {
        for (int i = 0; i < batsmenStats.size(); i++) {
            if (!batsmenStats.get(i).isOut() && i != strikerIndex && i != nonStrikerIndex) {
                return i;
            }
        }
        return -1;
    }

    private int getNextBowler() {
        currentBowlerIndex--;
        if (currentBowlerIndex < 3) {
            currentBowlerIndex = 5;
        }
        return currentBowlerIndex;
    }

    private void endInnings() {
        saveStatsToDB();
        showAlert("Innings Over", "Innings ended for " + battingTeam);

        if (firstInnings) {
            firstInningsTotal = totalRuns;
            firstInningsBattingTeam = battingTeam;
            if (matchId != -1) {
                try (Connection conn = Database.getConnection()) {
                    if (conn != null) {
                        String update = "UPDATE matches SET first_innings_total = ? WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(update)) {
                            ps.setInt(1, firstInningsTotal);
                            ps.setInt(2, matchId);
                            ps.executeUpdate();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Failed to save first innings total: " + e.getMessage());
                }
            }

            firstInnings = false;
            String tempTeam = battingTeam;
            battingTeam = bowlingTeam;
            bowlingTeam = tempTeam;
            ArrayList<String> tempPlayers = battingPlayers;
            battingPlayers = bowlingPlayers;
            bowlingPlayers = tempPlayers;

            currentOver = 0;
            currentBall = 0;
            wickets = 0;
            totalRuns = 0;
            batsmenStats.clear();
            for (String player : battingPlayers) {
                batsmenStats.add(new Batsman(player));
            }
            bowlerStats.clear();
            for (String player : bowlingPlayers) {
                bowlerStats.add(new Bowler(player));
            }
            strikerIndex = 0;
            nonStrikerIndex = 1;
            currentBowlerIndex = 5;

            if (lblTarget != null) {
                int target = firstInningsTotal + 1;
                lblTarget.setText(String.valueOf(target));
            }

            updateLabels();
        } else {

            saveStatsToDB();

             String resultText = "";
             if (firstInningsTotal == -1) {
                 resultText = "Result not available";
             } else {
                 if (totalRuns > firstInningsTotal) {
                     int remainingWickets = Math.max(0, (battingPlayers.size() - wickets));
                     resultText = battingTeam + " won by " + remainingWickets + " wickets";
                 } else if (totalRuns == firstInningsTotal) {
                     resultText = "Match tied";
                 } else {
                     String teamFirst = firstInningsBattingTeam != null ? firstInningsBattingTeam : bowlingTeam;
                     resultText = teamFirst + " won by " + (firstInningsTotal - totalRuns) + " runs";
                 }
             }

             if (matchId != -1) {
                 try (Connection conn = Database.getConnection()) {
                     if (conn != null) {
                         String update = "UPDATE matches SET result = ? WHERE id = ?";
                         try (PreparedStatement ps = conn.prepareStatement(update)) {
                             ps.setString(1, resultText);
                             ps.setInt(2, matchId);
                             ps.executeUpdate();
                         }
                     }
                 } catch (SQLException e) {
                     System.out.println("Failed to save match result: " + e.getMessage());
                 }
             }

            try {
                javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(getClass().getResource("hello-view.fxml"));
                javafx.scene.Scene scene = new javafx.scene.Scene(fxmlLoader.load());
                Stage stage = (Stage) lblTeamName.getScene().getWindow();

                stage.setScene(scene);
                stage.show();
            } catch (java.io.IOException e) {
                System.out.println("Error loading hello-view.fxml: " + e.getMessage());
                e.printStackTrace();
            }
         }
     }


    private void undoLastBall() {
        if (ballHistory.isEmpty()) return;

        BallEvent last = ballHistory.remove(ballHistory.size() - 1);
        strikerIndex = last.strikerIndex;
        nonStrikerIndex = last.nonStrikerIndex;
        currentBowlerIndex = last.bowlerIndex;
        currentOver = last.over;
        currentBall = last.ball;
        wickets = last.wickets;
        totalRuns = last.runsTotal;
        isFreeHit= false;
        for (Batsman b : batsmenStats) {
            b.reset();
        }
        for (Bowler b : bowlerStats) {
            b.reset();
        }

        for (BallEvent ev : ballHistory) {
            if(ev.blb)
            {
                Batsman striker = batsmenStats.get(ev.strikerIndex);
                striker.setBalls(striker.getBalls() + 1);
                Bowler bowler = bowlerStats.get(ev.bowlerIndex);
                bowler.setRuns(bowler.getRuns() + ev.runs);
                bowler.incrementBallsBowled();
                isFreeHit=false;
            }
            else if (!ev.isExtra) {
                Batsman striker = batsmenStats.get(ev.strikerIndex);
                striker.setRuns(striker.getRuns() + ev.runs);
                striker.setBalls(striker.getBalls() + 1);
                Bowler bowler = bowlerStats.get(ev.bowlerIndex);
                bowler.setRuns(bowler.getRuns() + ev.runs);
                bowler.incrementBallsBowled();
                isFreeHit= false;
            } else {
                if(ev.no)
                {
                    isFreeHit=true;
                }
                bowlerStats.get(ev.bowlerIndex).setRuns(bowlerStats.get(ev.bowlerIndex).getRuns() + ev.runs);
            }
            if (ev.isWicket) {
                bowlerStats.get(ev.bowlerIndex).setWickets(bowlerStats.get(ev.bowlerIndex).getWickets() + 1);
                batsmenStats.get(ev.strikerIndex).setOut(true);
            }
        }

        updateLabels();
    }
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
    private void ensureStatsTables() {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) return;
            try (var stmt = conn.createStatement()) {
                String sqlBats = "CREATE TABLE IF NOT EXISTS batsman_stats (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "match_id INTEGER, " +
                        "name TEXT NOT NULL, " +
                        "team TEXT, " +
                        "runs INTEGER, " +
                        "balls INTEGER, " +
                        "is_out INTEGER, " +
                        "UNIQUE(name, team, match_id)" +
                        ");";
                stmt.execute(sqlBats);

                String sqlBowl = "CREATE TABLE IF NOT EXISTS bowler_stats (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "match_id INTEGER, " +
                        "name TEXT NOT NULL, " +
                        "team TEXT, " +
                        "balls_bowled INTEGER, " +
                        "runs INTEGER, " +
                        "wickets INTEGER, " +
                        "UNIQUE(name, team, match_id)" +
                        ");";
                stmt.execute(sqlBowl);
            }
        } catch (SQLException e) {
            System.out.println("Failed to ensure stats tables: " + e.getMessage());
        }
    }

    private void saveStatsToDB() {
        try (Connection conn = Database.getConnection()) {
            if (conn == null) return;
            String updateBats = "UPDATE batsman_stats SET runs = ?, balls = ?, is_out = ? WHERE name = ? AND team = ? AND match_id = ?";
            String insertBats = "INSERT INTO batsman_stats (name, team, runs, balls, is_out, match_id) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psUpdate = conn.prepareStatement(updateBats);
                 PreparedStatement psInsert = conn.prepareStatement(insertBats)) {
                for (Batsman b : batsmenStats) {
                    psUpdate.setInt(1, b.getRuns());
                    psUpdate.setInt(2, b.getBalls());
                    psUpdate.setInt(3, b.isOut() ? 1 : 0);
                    psUpdate.setString(4, b.getName());
                    psUpdate.setString(5, battingTeam);
                    psUpdate.setInt(6, this.matchId);

                    int affected = psUpdate.executeUpdate();
                    if (affected == 0) {
                        psInsert.setString(1, b.getName());
                        psInsert.setString(2, battingTeam);
                        psInsert.setInt(3, b.getRuns());
                        psInsert.setInt(4, b.getBalls());
                        psInsert.setInt(5, b.isOut() ? 1 : 0);
                        psInsert.setInt(6, this.matchId);
                        psInsert.executeUpdate();
                    }
                }
            }

            String updateBowl = "UPDATE bowler_stats SET balls_bowled = ?, runs = ?, wickets = ? WHERE name = ? AND team = ? AND match_id = ?";
            String insertBowl = "INSERT INTO bowler_stats (name, team, balls_bowled, runs, wickets, match_id) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psUpdate = conn.prepareStatement(updateBowl);
                 PreparedStatement psInsert = conn.prepareStatement(insertBowl)) {
                for (Bowler b : bowlerStats) {
                    psUpdate.setInt(1, b.getBallsBowled());
                    psUpdate.setInt(2, b.getRuns());
                    psUpdate.setInt(3, b.getWickets());
                    psUpdate.setString(4, b.getName());
                    psUpdate.setString(5, bowlingTeam);
                    psUpdate.setInt(6, matchId);

                    int affected = psUpdate.executeUpdate();
                    if (affected == 0) {
                        psInsert.setString(1, b.getName());
                        psInsert.setString(2, bowlingTeam);
                        psInsert.setInt(3, b.getBallsBowled());
                        psInsert.setInt(4, b.getRuns());
                        psInsert.setInt(5, b.getWickets());
                        psInsert.setInt(6, matchId);
                        psInsert.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to save stats: " + e.getMessage());
        }
    }

    public static class Batsman {
        private final String name;
        private final SimpleIntegerProperty runs = new SimpleIntegerProperty(0);
        private final SimpleIntegerProperty balls = new SimpleIntegerProperty(0);
        private final SimpleBooleanProperty out = new SimpleBooleanProperty(false);

        public Batsman(String name) {
            this.name = name;
        }

        public String getName() { return name; }
        public int getRuns() { return runs.get(); }
        public void setRuns(int value) { runs.set(value); }
        public int getBalls() { return balls.get(); }
        public void setBalls(int value) { balls.set(value); }
        public boolean isOut() { return out.get(); }
        public void setOut(boolean value) { out.set(value); }
        public void reset() {
            runs.set(0); balls.set(0); out.set(false);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Bowler {
        private final String name;
        private final SimpleIntegerProperty overs = new SimpleIntegerProperty(0);
        private final SimpleIntegerProperty runsProp = new SimpleIntegerProperty(0);
        private final SimpleIntegerProperty wickets = new SimpleIntegerProperty(0);
        private int ballsBowled = 0;

        public Bowler(String name) {
            this.name = name;
        }
        public String getName() { return name; }
        public SimpleIntegerProperty oversProperty() { return overs; }
        public SimpleIntegerProperty runsProperty() { return runsProp; }
        public SimpleIntegerProperty wicketsProperty() { return wickets; }
        public int getOvers() { return overs.get(); }
        public int getRuns() { return runsProp.get(); }
        public void setRuns(int value) { runsProp.set(value); }
        public int getWickets() { return wickets.get(); }
        public void setWickets(int value) { wickets.set(value); }

        public void incrementBallsBowled() {
            ballsBowled++;
            updateOvers();
        }
        private void updateOvers() {
            int calculatedOvers = ballsBowled / 6;
            overs.set(calculatedOvers);
        }
        public int getBallsBowled() { return ballsBowled; }

        public void reset() {
            ballsBowled = 0; runsProp.set(0); wickets.set(0); overs.set(0);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class BallEvent {
        final int strikerIndex, nonStrikerIndex, bowlerIndex;
        final int runs, over, ball, wickets, runsTotal;
        final boolean isExtra, isWicket, blb, no;

        BallEvent(int s, int ns, int b, int r, boolean extra, boolean wicket, int o, int bl, int w, int rt, boolean isblb,boolean isNo) {
            strikerIndex = s; nonStrikerIndex = ns; bowlerIndex = b;
            runs = r; isExtra = extra; isWicket = wicket;
            over = o; ball = bl; wickets = w; runsTotal = rt; blb=isblb; no=isNo;
        }
    }
}