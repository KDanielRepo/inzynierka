public class Testing {
    Mutex mutex = new Mutex();
    Integer index = 0;
    Integer generationIndex = 0;
    public void onstart(){
        GameInstance gameInstance = new GameInstance();
        GameInstance gameInstance1 = new GameInstance();
        Genetics genetics = new Genetics();
        genetics.setMutex(mutex);
        genetics.setPopulation(2);
        gameInstance.setTries(0);
        //gameInstance.setTesting(this);
        genetics.setPopulation(200);
        gameInstance.setGenetics(genetics);
        gameInstance.start();

        //setIndex(getIndex()+1);
        gameInstance1.setTries(0);
        //gameInstance1.setTesting(this);
        gameInstance1.setGenetics(genetics);
        gameInstance1.start();
    }

    public Integer getGenerationIndex() {
        try{
            mutex.lock();
            return generationIndex;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setGenerationIndex(Integer generationIndex) {
        try{
            mutex.lock();
            this.generationIndex = generationIndex;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mutex.unlock();
        }
    }

    public Integer getIndex() {
        try{
            mutex.lock();
            return index;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mutex.unlock();
        }
        return 0;
    }

    public void setIndex(Integer index) {
        try{
            mutex.lock();
            this.index = index;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mutex.unlock();
        }
    }

    public static void main(String[] args) {
        Testing testing = new Testing();
        testing.onstart();
    }
}
