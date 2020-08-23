import com.google.common.collect.Iterables;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Game extends Application {
    //game variables
    private Integer[][] gameMatrix = new Integer[4][4];
    private Integer score;
    private BrainController brainController;

    private TextArea[][] gameArea = new TextArea[4][4];
    private TextField scoreBoard;
    private boolean up, right, down, left, game, paused, automation, moved;
    private GridPane gameGrid;
    private VBox toolBox;
    private Scene scene;
    private Mutex mutex;
    private TextField timer;
    private Integer delay;
    private Robot robot;
    private Genetics genetics;
    private int index = 0;
    private int generationIndex = 0;
    private boolean groupset;
    private TextArea textArea;
    private int moves = 0;
    private int tries = 0;
    private boolean multipleTries;
    private boolean visual;

    //wizualizacja
    private GraphicsContext gc;
    private Stage neuralNetworkStage;
    private Canvas canvas;

    //debug menu
    private Stage debugStage;
    private GraphicsContext debugGc;
    private Canvas debugCanvas;
    private Canvas generalInfo;
    private GraphicsContext generalInfoGC;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //only for instance
        brainController = new BrainController();

        BorderPane borderPane = new BorderPane();
        genetics = new Genetics();
        robot = new Robot();
        mutex = new Mutex();
        gameGrid = new GridPane();
        toolBox = new VBox();
        timer = new TextField();

        delay = 100;
        game = true;
        score = 0;

        brainController.setCurrentInputs(gameMatrix);
        setGameArea(gameGrid);
        updateGameArea();
        borderPane.setLeft(gameGrid);

        scene = new Scene(borderPane);
        scene.getRoot().requestFocus();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        up = true;
                        break;
                    case D:
                        right = true;
                        break;
                    case S:
                        down = true;
                        break;
                    case A:
                        left = true;
                        break;
                }
                update();
            }
        });
        scene.setOnKeyReleased(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        up = false;
                        break;
                    case D:
                        right = false;
                        break;
                    case S:
                        down = false;
                        break;
                    case A:
                        left = false;
                        break;
                }
            }
        });

        scoreBoard = new TextField();
        scoreBoard.setEditable(false);

        Button pause = new Button("Pause");
        pause.setOnAction(e -> {
            paused = !paused;
            if (paused) {
                pause.setText("Start");
            } else {
                pause.setText("Pause");
            }
        });
        Button restart = new Button("Restart");
        restart.setOnAction(e -> {
            restart();
        });
        Button automatic = new Button("Start brain");
        automatic.setOnAction(e -> {
            automation = !automation;
            if (automation) {
                automatic.setText("Stop Brain");
            } else {
                automatic.setText("Start Brain");
            }
        });
        Button nextMove = new Button("next move");
        nextMove.setOnAction(e -> {
            simulateKeyPress(brainController.generateMove());
        });
        Label timerLabel = new Label("Podaj delay pomiedzy ruchami:");
        Button timerButton = new Button("Set delay");
        timerButton.setOnAction(e -> {
            try {
                delay = Integer.parseInt(timer.getText());
            } catch (Exception ex) {
                System.out.println("Podaj poprawną wartość opóźnienia");
            }
        });
        Button visualButton = new Button("Visual");
        visualButton.setOnAction(e->{
            visual = !visual;
            if(!visual){
                visualButton.setText("Visual");
            }else{
                visualButton.setText("No visual");
            }
        });
        Button multipleTriesButton = new Button("Single Try");
        multipleTriesButton.setOnAction(e->{
            multipleTries=!multipleTries;
            if(multipleTries){
                multipleTriesButton.setText("Multiple Tries");
            }else{
                multipleTriesButton.setText("Single Try");
            }
        });
        Button visualizeButton = new Button("Show net visualization");
        visualizeButton.setOnAction(e->{
            visualizeNeuralNetwork();
            updateNeuralNetworkVisualization();
        });
        Button debugMenuButton = new Button("Show debug menu");
        debugMenuButton.setOnAction(e->{
            debugMenu();
        });


        toolBox.getChildren().addAll(pause, restart, nextMove, automatic, scoreBoard, timerLabel, timer, timerButton,visualButton,multipleTriesButton,visualizeButton,debugMenuButton);
        borderPane.setRight(toolBox);

        primaryStage.setScene(scene);
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.show();
        brainController.setCurrentInputs(gameMatrix);
    }

    public void visualizeNeuralNetwork(){
        neuralNetworkStage = new Stage();
        neuralNetworkStage.setTitle("Visualization");

        BorderPane borderPane = new BorderPane();
        Scene neuralNetworkScene = new Scene(borderPane);
        neuralNetworkStage.setScene(neuralNetworkScene);
        neuralNetworkStage.setX(300);
        neuralNetworkStage.setY(300);
        neuralNetworkStage.show();

        canvas = new Canvas(1600,800);
        gc = canvas.getGraphicsContext2D();
        borderPane.setCenter(canvas);
    }
    public void updateNeuralNetworkVisualization(){
        if(neuralNetworkStage!=null){
            gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2f);

            int divisionSize = brainController.getBrain().getPerceptronMap().get(0).stream().findFirst().get().size();
            int layer = brainController.getBrain().getPerceptronMap().size();
            int neuronWidth = (int) canvas.getHeight()/divisionSize;
            int neuronHeight = (int) canvas.getHeight()/divisionSize;

            for (int i = 0; i < divisionSize; i++) {
                for (int k = 0; k < divisionSize; k++) {
                    //zaczyna sie przy kazdym wejsciu albo neuronie
                    //leci do kazdego nastepnego neuronu
                    // i to warstwa w sieci, j to numer danego neuronu w danej sieci
                    gc.setStroke(Color.RED);
                    gc.strokeLine(canvas.getWidth()/layer*0+neuronWidth/2,canvas.getHeight()/divisionSize*i+neuronHeight/2,canvas.getWidth()/layer*1-neuronWidth,canvas.getHeight()/divisionSize*k+neuronHeight/2);
                }
            }

            for (int i = 1; i < layer+1; i++) {
                int layerSize = brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().size();
                for (int j = 0; j < layerSize; j++) {
                    if(i==1){
                        gc.strokeText(gameMatrix[j/4][j%4].toString(),canvas.getWidth()/layer*(i-1),canvas.getHeight()/divisionSize*j+neuronHeight/2);
                    }
                    for (int k = 0; k < layerSize; k++) {
                        int layerSizeNext = layerSize;
                        if(i<layer) {
                            layerSizeNext = brainController.getBrain().getPerceptronMap().get(i).stream().findFirst().get().size();
                        }
                        //zaczyna sie przy kazdym wejsciu albo neuronie
                        //leci do kazdego nastepnego neuronu
                        // i to warstwa w sieci, j to numer danego neuronu w danej sieci
                        gc.setStroke(Color.RED);
                        if(k<layerSizeNext)
                        gc.strokeLine(canvas.getWidth()/layer*(i)+neuronWidth,canvas.getHeight()/divisionSize*j+neuronHeight/2,canvas.getWidth()/layer*(i+1)-neuronWidth,canvas.getHeight()/divisionSize*k+neuronHeight/2);
                    }
                    gc.setStroke(Color.BLACK);
                    gc.strokeOval(canvas.getWidth()/layer*i-neuronWidth,canvas.getHeight()/divisionSize*j,neuronWidth,neuronHeight);
                    gc.strokeText(brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getOutput().toString().substring(0,4),canvas.getWidth()/layer*i-neuronWidth,canvas.getHeight()/divisionSize*j+neuronHeight/2);
                    //System.out.println("input: "+brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getInput(0).toString());
                    //System.out.println(brainController.getBrain().getPerceptronMap().get(i-1).stream().findFirst().get().get(j).stream().findFirst().get().getOutput().toString());
                }
                //System.out.println("---------------------");
            }
        }
    }
    public void debugMenu(){
        debugStage = new Stage();
        debugCanvas = new Canvas(300,600);
        debugGc = debugCanvas.getGraphicsContext2D();
        debugGc.setStroke(Color.BLACK);
        debugGc.setLineWidth(1f);

        generalInfo = new Canvas(200,600);
        generalInfoGC = generalInfo.getGraphicsContext2D();

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);

        HBox hBox = new HBox();
        RadioButton[] radioButtons = new RadioButton[brainController.getBrain().getPerceptronMap().size()];
        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i <radioButtons.length; i++) {
            RadioButton radioButton = new RadioButton("layer "+i);
            radioButton.setToggleGroup(group);
            hBox.getChildren().add(radioButton);
            radioButtons[i] = radioButton;
        }
        Button showSelectedWeights = new Button("show selected weights");
        showSelectedWeights.setOnAction(e->{
            clearDebugMenuCanvas();
            for (int i = 0; i <radioButtons.length ; i++) {
                if(radioButtons[i].isSelected()){
                    int finalI = i;
                    AtomicInteger index = new AtomicInteger();
                    brainController.getBrain().getPerceptronMap().get(i).stream().findFirst().get().values().forEach(o->{
                            debugGc.strokeText("Waga nr. "+index+" to: "+ o.getWeights().get(index.get()).toString().substring(0,4),debugCanvas.getWidth()/2,debugCanvas.getHeight()/brainController.getBrain().getPerceptronMap().get(finalI).stream().findFirst().get().values().size()*index.get()+debugGc.getFont().getSize());
                            index.getAndIncrement();
                    });
                }
            }
        });

        VBox toolbox = new VBox();
        toolbox.getChildren().addAll(hBox,showSelectedWeights);
        borderPane.setRight(toolbox);
        borderPane.setCenter(debugCanvas);
        borderPane.setLeft(generalInfo);
        debugStage.setScene(scene);
        debugStage.setTitle("Debug menu");
        debugStage.show();
    }
    public void clearDebugMenuCanvas(){
        debugGc.clearRect(0,0,debugCanvas.getWidth(),debugCanvas.getHeight());
        debugGc.setStroke(Color.BLACK);
        debugGc.setLineWidth(1f);
    }
    public void updateGeneralInfo(){
        generalInfoGC.clearRect(0,0,generalInfo.getWidth(),generalInfo.getHeight());
        generalInfoGC.setStroke(Color.BLACK);
        generalInfoGC.setLineWidth(1f);
        double height = generalInfoGC.getFont().getSize();
        generalInfoGC.strokeText("Lista bloków: ",0,height);
        for (int i = 0; i < brainController.getBlocks().size(); i++) {
            generalInfoGC.strokeText(brainController.getBlocks().get(i).toString(),0,height*(2+i));
        }
        generalInfoGC.strokeText("Wartości na wyjściach: ",0,height*7);
        for (int i = 0; i < brainController.getBrain().getOutputLayer().size(); i++) {
            String pre = "";
            switch (i){
                case 0:
                    pre = "góra: ";
                    break;
                case 1:
                    pre = "prawo: ";
                    break;
                case 2:
                    pre = "dół: ";
                    break;
                case 3:
                    pre = "lewo: ";
                    break;
            }
            String middle = Iterables.get(brainController.getBrain().getOutputLayer().values(),i).toString();
            generalInfoGC.strokeText(pre+middle,0,height*(8+i));
        }
    }

    public void update() {
        if (game && !paused) {
            if (!mutex.isLocked() && automation) {
                if (brainController.isNotBlocked()) {
                    brainController.setCurrentMove(brainController.generateMove());
                } else {
                    brainController.setCurrentMove(brainController.generateMoveWithoutBlocks());
                }
                simulateKeyPress(brainController.getCurrentMove());
                if(!visual){
                    mutex.lock();
                }
            }
            if (left) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 4; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i + 1][j]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                score+=gameMatrix[i][j];
                                gameMatrix[i + 1][j] = 0;
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i + 1][j] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i + 1][j];
                                    gameMatrix[i + 1][j] = 0;
                                    moved = true;
                                }
                            }
                        }
                    }
                }
            } else if (right) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 3; i > 0; i--) {
                        for (int j = 0; j < 4; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i - 1][j]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                score+=gameMatrix[i][j];
                                gameMatrix[i - 1][j] = 0;
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i - 1][j] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i - 1][j];
                                    gameMatrix[i - 1][j] = 0;
                                    moved = true;
                                }
                            }
                        }
                    }
                }
            } else if (up) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 3; j++) {
                            if (gameMatrix[i][j].equals(gameMatrix[i][j + 1]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                score+=gameMatrix[i][j];
                                gameMatrix[i][j + 1] = 0;
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i][j + 1] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i][j + 1];
                                    gameMatrix[i][j + 1] = 0;
                                    moved = true;
                                }
                            }
                        }
                    }
                }
            } else if (down) {
                for (int k = 0; k < 4; k++) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 3; j > 0; j--) {
                            if (gameMatrix[i][j].equals(gameMatrix[i][j - 1]) && (gameMatrix[i][j] != 0)) {
                                gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                score+=gameMatrix[i][j];
                                gameMatrix[i][j - 1] = 0;
                                moved = true;
                            }
                            if (gameMatrix[i][j] == 0) {
                                if (gameMatrix[i][j - 1] != 0) {
                                    gameMatrix[i][j] = gameMatrix[i][j - 1];
                                    gameMatrix[i][j - 1] = 0;
                                    moved = true;
                                }
                            }
                        }
                    }
                }
            }
            if (moved) {
                moves++;
                if(!visual){
                    updateGameArea();
                }
                updateGeneralInfo();
                checkGameOver();
                //if (automation) {
                    brainController.setCurrentInputs(gameMatrix);
                    brainController.getBlocks().clear();
                //System.out.println(" ");
                //}
                //brainController.setCurrentInputs(gameMatrix);
                //values();
                moved = false;
            } else if (!moved && automation) {
                if(!brainController.getBlocks().contains(brainController.getCurrentMove())){
                    brainController.addBlock(brainController.getCurrentMove());
                    if(brainController.getBlocks().size()==4){
                        brainController.getBlocks().clear();
                    }
                }
                if(!visual){
                    mutex.unlock(delay);
                }
            }
        } else if (!game) {
            tries++;
            System.out.println(score);
            restart();
            if (index < genetics.getPopulation()) {
                update();
            } else {
                groupset = true;
                //before();
                System.out.println("generacja: " + generationIndex);
                System.out.println("Sredni fitness to: ");
                genetics.getAverageFitness();
                genetics.calculateGlobalFitness();
                genetics.calculateRFitness();
                //genetics.getFittest();
                genetics.createOffspringNew();
                genetics.resetPcPool();
                //after();
                index = 0;
                generationIndex++;
                System.out.println("---------------------------");
                if (generationIndex <= genetics.getGeneration()) {
                    update();
                } else {
                    for (int i = 0; i < genetics.getGenePool().size(); i++) {
                        System.out.println(genetics.getGenePool().get(i).getScore());
                    }

                }
            }
        }
    }

    public void checkGameOver() {
        int test = 0;
        int test2 = 0;
        int test3 = 0;
        int test4 = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameMatrix[i][j] != gameMatrix[i + 1][j] && (gameMatrix[i][j] > 0) && (gameMatrix[i + 1][j] > 0)) {
                    test++;
                }
            }
        }
        for (int i = 3; i > 0; i--) {
            for (int j = 0; j < 4; j++) {
                if (gameMatrix[i][j] != gameMatrix[i - 1][j] && (gameMatrix[i][j] > 0) && (gameMatrix[i - 1][j] > 0)) {
                    test2++;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameMatrix[i][j] != gameMatrix[i][j + 1] && (gameMatrix[i][j] > 0) && (gameMatrix[i][j + 1] > 0)) {
                    test3++;
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 3; j > 0; j--) {
                if (gameMatrix[i][j] != gameMatrix[i][j - 1] && (gameMatrix[i][j] > 0) && (gameMatrix[i][j - 1] > 0)) {
                    test4++;
                }
            }
        }
        boolean noUp = test == 12;
        boolean noRight = test2 == 12;
        boolean noDown = test3 == 12;
        boolean noLeft = test4 == 12;
        if (noUp && noRight && noDown && noLeft) {
            game = false;
        }
    }

    public void setGameArea(GridPane gridPane) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gameArea[i][j] = new TextArea();
                gameArea[i][j].setPrefSize(150, 150);
                gameArea[i][j].setText("");
                gameArea[i][j].setEditable(false);
                gridPane.add(gameArea[i][j], i, j);
            }
        }
    }

    public void updateGameArea() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameMatrix[i][j] == 0) {
                    gameArea[i][j].setText("");
                } else {
                    gameArea[i][j].setText(Integer.toString(gameMatrix[i][j]));
                }
            }
        }
        if (automation) {
            mutex.unlock(delay);
        }
        updateNeuralNetworkVisualization();
    }

    public void restart() {
        brainController.getBlocks().clear();
        if(multipleTries){
            if(tries<10 && automation){
                brainController.getBrain().setScore(brainController.getBrain().getScore()+score);
                brainController.getBrain().setLp(index);
                brainController.getBrain().setMoves(brainController.getBrain().getMoves()+moves);
            }
            if (!groupset && tries>=10) {
                brainController.getBrain().setScore((brainController.getBrain().getScore()+score)/10);
                brainController.getBrain().setLp(index);
                brainController.getBrain().setMoves((brainController.getBrain().getMoves()+moves)/10);
                index++;
                genetics.getGenePool().add(brainController.getBrain());
                Brain brain = new Brain();
                brain.createDefaultPerceptronMap();
                brainController.setBrain(brain);
                tries = 0;
            } else if(groupset && tries >=10){
                brainController.getBrain().setScore((brainController.getBrain().getScore()+score)/10);
                brainController.getBrain().setLp(index);
                brainController.getBrain().setMoves((brainController.getBrain().getMoves()+moves)/10);
                brainController.setBrain(genetics.getGenePool().get(index));
                index++;
                tries = 0;
            }
        }else{
            if (automation) {
                brainController.getBrain().setScore(score);
                brainController.getBrain().setLp(index);
                if (!groupset) {
                    genetics.getGenePool().add(brainController.getBrain());
                    Brain brain = new Brain();
                    brain.createDefaultPerceptronMap();
                    brainController.setBrain(brain);
                } else {
                    brainController.setBrain(genetics.getGenePool().get(index));
                }
            }
            index++;
        }
        System.out.println("index: "+index);
        brainController.getBlocks().clear();
        if (index == genetics.getPopulation()) {
            for (int i = 0; i < genetics.getGenePool().size(); i++) {
                System.out.println(genetics.getGenePool().get(i).getScore());
            }
        }
        moves = 0;
        score = 0;
        game = true;
        if(!visual){
            updateGameArea();
        }
        updateGeneralInfo();
        if(!visual){
            updateGameArea();
        }
        brainController.setCurrentInputs(gameMatrix);
    }

    public void simulateKeyPress(int i) {
        try {
            switch (i) {
                case 0:
                    robot.keyPress(java.awt.event.KeyEvent.VK_W);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_W);
                    break;
                case 1:
                    robot.keyPress(java.awt.event.KeyEvent.VK_D);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_D);
                    break;
                case 2:
                    robot.keyPress(java.awt.event.KeyEvent.VK_S);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_S);
                    break;
                case 3:
                    robot.keyPress(java.awt.event.KeyEvent.VK_A);
                    robot.delay(delay);
                    robot.keyRelease(java.awt.event.KeyEvent.VK_A);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(Game.class, args);
    }
}
