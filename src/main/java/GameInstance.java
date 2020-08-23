import java.util.concurrent.ThreadLocalRandom;

public class GameInstance extends Thread {
    private Integer[][] gameMatrix = new Integer[4][4];
    private Integer randomA, randomB, score;
    private BrainController brainController;
    private Integer delay;
    private boolean game;
    private boolean paused;
    private boolean up, left, down, right;
    private boolean moved;
    private Integer tries;
    private Genetics genetics;
    private boolean groupset;
    private GameView gameView;
    private Integer index;

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public GameInstance() {
        brainController = new BrainController();
        paused = false;
        game = true;
        score = 0;
        setGameMatrix();
        random(2);
        brainController.setCurrentInputs(gameMatrix);
    }

    public void run() {
        index = gameView.getIndex();
        gameView.setIndex(gameView.getIndex()+1);
        update();
    }

    public void update() {
        while(game && !paused) {
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
                                score += gameMatrix[i][j];
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
            } else if (!moved) {
                if (!brainController.getBlocks().contains(brainController.getCurrentMove())) {
                    brainController.addBlock(brainController.getCurrentMove());
                    if (brainController.getBlocks().size() == 4) {
                        brainController.getBlocks().clear();
                    }
                }
            }
            up = false;
            left = false;
            down = false;
            right = false;
            checkGameOver();
        }if (!game) {
            tries++;
            System.out.println(this.getName() + " // " + score);
            restart();
            if (index < genetics.getPopulation()) {
                update();
            } else {
                this.interrupt();
                /*groupset = true;
                setGenerationIndex(getGenerationIndex()+1);
                if (getGenerationIndex() <= genetics.getGeneration()) {
                    update();
                }*/
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

    public void restart() {
        brainController.getBlocks().clear();
            if (tries < 10) {
                brainController.getBrain().setScore(brainController.getBrain().getScore() + score);
                brainController.getBrain().setLp(index);
            }
            if (!groupset && tries == 10) {
                brainController.getBrain().setScore((brainController.getBrain().getScore() + score) / 10);
                brainController.getBrain().setLp(index);
                index = gameView.getIndex();
                gameView.setIndex(gameView.getIndex()+1);
                genetics.getGenePool().add(brainController.getBrain());
                Brain brain = new Brain();
                brain.createDefaultPerceptronMap();
                brainController.setBrain(brain);
                tries = 0;
            } else if (groupset && tries == 10) {
                brainController.getBrain().setScore((brainController.getBrain().getScore() + score) / 10);
                brainController.getBrain().setLp(index);
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
        //index = gameView.getIndex();
        System.out.println(this.getName()+" // "+index);
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

    public void random(int times) {
        for (int k = 0; k < times; k++) {
            int iteration = 0;
            randomA = ThreadLocalRandom.current().nextInt(0, 4);
            randomB = ThreadLocalRandom.current().nextInt(0, 4);
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (gameMatrix[i][j] > 0) {
                        iteration++;
                    }
                }
            }
            if (iteration == 16) {
                return;
            }
            if (gameMatrix[randomA][randomB] != 0) {
                random(1);
            }
            int twoOrFour = ThreadLocalRandom.current().nextInt(0, 4);
            if (twoOrFour == 3) {
                gameMatrix[randomA][randomB] = 4;
            } else {
                gameMatrix[randomA][randomB] = 2;
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

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
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

    public boolean isGroupset() {
        return groupset;
    }

    public void setGroupset(boolean groupset) {
        this.groupset = groupset;
    }
}
