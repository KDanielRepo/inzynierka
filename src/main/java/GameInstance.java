import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameInstance extends Thread {
    private Integer[][] gameMatrix = new Integer[4][4];
    private Integer score;
    private BrainController brainController;
    private boolean game;
    private boolean up, left, down, right;
    private boolean moved;
    private Integer tries;
    private Genetics genetics;
    private GameView gameView;
    private Integer index;
    private boolean selectedAsView;
    private boolean scoreAdded;

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public GameInstance() {
        brainController = new BrainController();
        game = true;
        score = 0;
        setGameMatrix();
        random(2);
        brainController.setCurrentInputs(gameMatrix);
        selectedAsView = false;
    }

    public void run() {
        index = gameView.getIndex();
        gameView.setIndex(gameView.getIndex()+1);
        update();
    }

    public synchronized void update() {
        while (true) {
            while (game) {
                try {
                    while (gameView.isPaused()) {
                        sleep(1);
                    }
                    sleep(gameView.getDelay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (brainController.isNotBlocked()) {
                        brainController.setCurrentMove(brainController.generateMove());
                } else {
                    brainController.setCurrentMove(brainController.generateMoveWithoutBlocks());
                }
                simulateKeyPress(brainController.getCurrentMove());
                if (left) {
                    for (int k = 0; k < 4; k++) {
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 4; j++) {
                                if (gameMatrix[i][j].equals(gameMatrix[i + 1][j]) && (gameMatrix[i][j] != 0)) {
                                    gameMatrix[i][j] = gameMatrix[i][j] * 2;
                                    if(!scoreAdded){
                                        score += gameMatrix[i][j];
                                        scoreAdded = true;
                                    }
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
                                    score += gameMatrix[i][j];
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
                                    score += gameMatrix[i][j];
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
                                    score += gameMatrix[i][j];
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
                    random(1);
                    brainController.setCurrentInputs(gameMatrix);
                    brainController.getBlocks().clear();
                    moved = false;
                    scoreAdded = false;
                } else if (!moved) {
                    if (!brainController.getBlocks().contains(brainController.getCurrentMove())) {
                        brainController.addBlock(brainController.getCurrentMove());
                    }
                }
                up = false;
                left = false;
                down = false;
                right = false;
                if (selectedAsView) {
                    gameView.updateGameArea();
                }
                checkGameOver();
            }
            if (!game) {
                tries++;
                restart();
                 if(index>=genetics.getPopulation()){
                    gameView.setFinishedInstances(gameView.getFinishedInstances() + 1);
                    gameView.startGenetics();
                    while (!genetics.isGroupset() || !genetics.isGenerated()) {
                        try {
                            this.wait(1000);
                            //System.out.println(this.getName() + " // waiting for groupset");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (gameView.getGenerationIndex() < genetics.getGeneration()) {
                        index = gameView.getIndex();
                        gameView.setIndex(gameView.getIndex() + 1);
                    }
                }
            }
        }
    }
    //TODO: upewnij sie ze sama gra dziala 100% poprawnie, moze jakies testy??

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

    public void restart() {
        brainController.getBlocks().clear();
            if (tries <= 10) {
                //System.out.println(this.getName() + " // " + brainController.getBrain().getScore() + " -- " + index + " / " + genetics.getPopulation());
                brainController.getBrain().setScore(brainController.getBrain().getScore() + score);
                brainController.getBrain().setLp(index);
            }
            if (!genetics.isGroupset() && tries == 10 && index<=genetics.getPopulation()) {
                brainController.getBrain().setScore((brainController.getBrain().getScore() + score)/10);
                brainController.getBrain().setLp(index);
                //System.out.println(this.getName() + " // " + brainController.getBrain().getScore() + " -- " + index + " / " + genetics.getPopulation());
                index = gameView.getIndex();
                gameView.setIndex(gameView.getIndex()+1);
                genetics.getGenePool().add(brainController.getBrain());
                Brain brain = new Brain();
                brain.createDefaultPerceptronMap();
                brainController.setBrain(brain);
                tries = 0;
            } else if (genetics.isGroupset() && tries == 10 && index<=genetics.getPopulation()) {
                brainController.getBrain().setScore((brainController.getBrain().getScore() + score)/10);
                brainController.getBrain().setLp(index);
                //System.out.println(this.getName() + " // " + brainController.getBrain().getScore() + " -- " + index + " / " + genetics.getPopulation());
                brainController.setBrain(genetics.getGenePool().get(index));
                index = gameView.getIndex();
                gameView.setIndex(gameView.getIndex()+1);
                tries = 0;
            }
        score = 0;
        game = true;
        setGameMatrix();
        random(2);
        brainController.setCurrentInputs(gameMatrix);
        if(selectedAsView){
            gameView.updateGameArea();
        }
    }

    public void setGameMatrix() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gameMatrix[i][j] = 0;
            }
        }
    }

    public void simulateKeyPress(int move) {
        switch (move) {
            case 0:
                up = true;
                break;
            case 1:
                right = true;
                break;
            case 2:
                down = true;
                break;
            case 3:
                left = true;
                break;
        }
    }

    public void random(int times){
        List<Integer> xSlots = new ArrayList<>();
        List<Integer> ySlots = new ArrayList<>();
        for (int k = 0; k < times; k++) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (gameMatrix[i][j] == 0) {
                        xSlots.add(i);
                        ySlots.add(j);
                    }
                }
            }
            if (xSlots.isEmpty()) {
                return;
            }
            int random = ThreadLocalRandom.current().nextInt(0,xSlots.size());
            while(gameMatrix[xSlots.get(random)][ySlots.get(random)] != 0) {
                xSlots.remove(random);
                ySlots.remove(random);
                random = ThreadLocalRandom.current().nextInt(0,xSlots.size());
            }
            int twoOrFour = ThreadLocalRandom.current().nextInt(0, 4);
            if (twoOrFour == 3) {
                gameMatrix[xSlots.get(random)][ySlots.get(random)] = 4;
            } else {
                gameMatrix[xSlots.get(random)][ySlots.get(random)] = 2;
            }
        }
    }

    public Integer[][] getGameMatrix() {
        return gameMatrix;
    }

    public void setGameMatrix(Integer[][] gameMatrix) {
        this.gameMatrix = gameMatrix;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public BrainController getBrainController() {
        return brainController;
    }

    public void setBrainController(BrainController brainController) {
        this.brainController = brainController;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }

    public Genetics getGenetics() {
        return genetics;
    }

    public void setGenetics(Genetics genetics) {
        this.genetics = genetics;
    }

    public boolean isSelectedAsView() {
        return selectedAsView;
    }

    public void setSelectedAsView(boolean selectedAsView) {
        this.selectedAsView = selectedAsView;
    }
}
