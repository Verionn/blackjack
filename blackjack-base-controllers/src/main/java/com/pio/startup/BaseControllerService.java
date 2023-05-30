package com.pio.startup;

import com.pio.models.BaseModelService;
import com.pio.models.Player;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.io.IOException;

public class BaseControllerService {
    public static int MAX_PLAYERS = 4;

    @FXML
    private Text firstMoneyPlayer;

    @FXML
    private Text secondMoneyPlayer;

    @FXML
    private Text thirdMoneyPlayer;

    @FXML
    private Text fourthMoneyPlayer;

    private Text[] currentBet;

    private Stage stage;

    private int currentPlayerIndex = 0;

    private int betSum = 0;

    private final BaseModelService baseModelService = new BaseModelService();

    public BaseControllerService() {
    }

    @FXML
    private AnchorPane gamePane;

    boolean isFrontShowing = true;
    Image frontImage = new Image("Cards/ace_of_clubs.png");
    Image backImage = new Image("Cards/back.png");

    public void moveToMainStarterView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("startup/blackjack-starter-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 847.09);
        stage.setTitle("Blackjack!");
        stage.getIcons().add(new Image("startup/coin.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        BaseControllerService controller = fxmlLoader.getController();
        controller.setStage(stage);
    }

    public void moveToGameView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("startup/Game-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 847.09);
        stage.setTitle("Blackjack!");
        stage.getIcons().add(new Image("startup/coin.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        BaseControllerService controller = fxmlLoader.getController();
        controller.setStage(stage);

        initialize();
    }

    public void moveToInfoView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("startup/info-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 847.09);
        stage.setTitle("Blackjack!");
        stage.getIcons().add(new Image("startup/coin.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        BaseControllerService controller = fxmlLoader.getController();
        controller.setStage(stage);
    }

    public void moveCardToHand(){

        isFrontShowing = true;
        ImageView back = new ImageView(backImage);
        back.setFitWidth(65);
        back.setFitHeight(95);
        ImageView front = new ImageView(frontImage);

        gamePane.getChildren().add(back); // Dodanie karty do kontenera

        double startX = 203; // Współrzędne punktu początkowego
        double startY = 186;
        double endX = 500; // Współrzędne punktu docelowego
        double endY = 250;

        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), back); // Tworzenie animacji
        transition.setFromX(startX); // Ustawienie punktu początkowego
        transition.setFromY(startY);
        transition.setToX(endX); // Ustawienie punktu docelowego
        transition.setToY(endY);
        transition.play();

        transition.setOnFinished(event -> {
            System.out.println("obrot do polowy");
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), back);
            rotateTransition.setAxis(Rotate.Y_AXIS);
            rotateTransition.setFromAngle(0); // Początkowy kąt obrotu
            rotateTransition.setToAngle(90); // Końcowy kąt obrotu
            rotateTransition.play();
            rotateTransition.setOnFinished(event1 -> {
                if(isFrontShowing){
                    System.out.println("Obrot do konca");
                    back.setImage(frontImage);
                    rotateTransition.setFromAngle(90); // Początkowy kąt obrotu
                    rotateTransition.setToAngle(180);
                    rotateTransition.play();
                    isFrontShowing = false;
                }
            });
        });
    }

    private RotateTransition createRotator(ImageView card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(1000), card);
        rotator.setAxis(Rotate.Y_AXIS);

        if (isFrontShowing) {
            rotator.setFromAngle(0);
            rotator.setToAngle(180);
        } else {
            rotator.setFromAngle(180);
            rotator.setToAngle(360);
        }
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);

        return rotator;
    }

    private PauseTransition changeCardFace(ImageView card) {
        PauseTransition pause = new PauseTransition(Duration.millis(500));

        if (isFrontShowing) {
            pause.setOnFinished(
                    e -> {
                        card.setImage(backImage);
                    });
        } else {
            pause.setOnFinished(
                    e -> {
                        card.setImage(frontImage);
                    });
        }

        return pause;
    }

    public void leaveInfoScreen(MouseEvent event) throws IOException {
        moveToGameView();
    }

    public void hit(MouseEvent event) {
        /*if (betSum == 0) {
            return;
        }*/

        Player player = baseModelService.returnPlayer(currentPlayerIndex);
        player.placeBet(betSum);

        moveCardToHand();

        changePlayerMove();
    }

    public void leaveGame(MouseEvent event) throws IOException {

        Player player = baseModelService.returnPlayer(currentPlayerIndex);
        currentBet[currentPlayerIndex].setText("");
        player.setPlaying(false);

        if(returnAmountOfPlayingPlayers() == 0){
            moveToMainStarterView();
            return;
        }

        changePlayerMove();
    }

    public void stand(MouseEvent event) {
        Player player = baseModelService.returnPlayer(currentPlayerIndex);
        if(player.getBetAmount() == 0){
            return;
        }
        player.setStanding(true);
        changePlayerMove();
    }

    public void add1000Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 1000;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 1000;
        }
        updateBetText(betSum);
    }

    public void add500Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 500;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 500;
        }
        updateBetText(betSum);
    }

    public void add200Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 200;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 200;
        }
        updateBetText(betSum);
    }

    public void add100Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 100;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 100;
        }
        updateBetText(betSum);
    }

    public void add50Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 50;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 50;
        }
        updateBetText(betSum);
    }

    public void add20Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 20;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 20;
        }
        updateBetText(betSum);
    }

    public void add10Chip(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            betSum += 10;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            betSum -= 10;
        }
        updateBetText(betSum);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        currentBet = new Text[]{firstMoneyPlayer, secondMoneyPlayer, thirdMoneyPlayer, fourthMoneyPlayer};
    }

    private void changePlayerMove() {
        betSum = 0;
        currentPlayerIndex = returnNextPlayingPlayer();
        System.out.println("Ruch: " + currentPlayerIndex);
    }

    private void cleanMoneyFields() {
        for (int i = 0; i < MAX_PLAYERS; i++) {
            Player player = baseModelService.returnPlayer(i);
            if(player.isPlaying()){
                currentBet[i].setText("0$");
            }
        }
    }

    private void updateBetText(int amount) {
        if (amount < 0) {
            betSum = 0;
            amount = 0;
        }

        currentBet[currentPlayerIndex].setText(amount + "$");
    }

    private int returnNextPlayingPlayer() {
        while (true) {
            currentPlayerIndex++;
            if (currentPlayerIndex >= MAX_PLAYERS) {
                currentPlayerIndex = 0;
                cleanMoneyFields();
            }

            Player player = baseModelService.returnPlayer(currentPlayerIndex);
            if (player.isPlaying()) {
                return currentPlayerIndex;
            }
        }
    }

    private int returnAmountOfPlayingPlayers(){
        int onlinePlayers = 0;
        for (int i = 0; i < MAX_PLAYERS; i++){
            Player player = baseModelService.returnPlayer(i);
            if(player.isPlaying()){
                onlinePlayers++;
            }
        }
        return onlinePlayers;
    }
}
