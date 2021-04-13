package algorithm;

import org.jfree.ui.RefineryUtilities;
import tasks.Knapsack;
import utils.WriteToCsv;
import visualization.LineChartDemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class metaHeuristicForOneZeroProgramming {
    public Knapsack problem;
    public String outPutFileName;
    //问题维数
    private final int dimension;
    //问题约束个数
    private final int constraintNum;
    // 文献或Cplex最好解
    public int optimalValue;
    public int metaOptimalValue;
    // 种群大小
    public int populationSize = 100;
    //最大迭代次数
    public int maxIter = 10000;
    // 最大运行时间
    public double maxTime = 100;
    //种群值
    public boolean[][] population;
    //每个个体的适应度函数值
    public Integer[] fitnessArray;
    // 迄今最好解
    public int currentBestFitness;

    public boolean[] currentBestSolution;
    public boolean[] bestSolution;
    public int runIter;
    public long runTime;
    public long startTime;
    public long endTime;

    // 算法中需要用到的在每个约束中，每个元素的价值密度
    // drop操作使用
    public double[][] localUnitPriceOfWeight;
    // add操作使用
    public double[][] localUnitWeightOfPrice;

    public metaHeuristicForOneZeroProgramming(Knapsack problem){
        startTime = System.currentTimeMillis()/1000;
        this.problem = problem;
        outPutFileName = "src/main/resources/result/"+problem.fileType+".csv";
        optimalValue =Math.max(problem.optimalValue, problem.cplexObjective);
        constraintNum = problem.nConstraint;
        dimension = problem.dimension;
        localUnitPriceOfWeight = new double[constraintNum][dimension];
        localUnitWeightOfPrice = new double[constraintNum][dimension];
        for(int i=0; i<constraintNum; i++){
            for(int j=0; j < dimension; j++){
                assert localUnitPriceOfWeight != null;
                localUnitPriceOfWeight[i][j] = problem.weights[i][j]*1.0/problem.prices[j];
                if(localUnitPriceOfWeight[i][j]==0){
                    localUnitWeightOfPrice[i][j] = 1000;
                }else{
                    localUnitWeightOfPrice[i][j] = 1.0 / localUnitPriceOfWeight[i][j];
                }
            }
        }
    }
    public void setPopulationSize(int value){
        this.populationSize = value;
    }

    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }

    public void setMaxTime(double maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * 随机生成初始解
     */
    public void initialSolutionRandom(){
        //随机生成初始解
        population = new boolean[populationSize][dimension];
        fitnessArray = new Integer[populationSize];
        for(int p=0; p<populationSize;p++){
            for(int j=0; j<dimension; j++){
                population[p][j] = Math.random() > 0.5;
            }
        }
        //将其装换为可行解,并计算目标函数值
        for(int p=0; p<populationSize;p++){
            repairDropAddByGroup(population[p]);
            fitnessArray[p] = computeFitness(population[p]);
        }
    }

    public int getMaxValue(Integer[] array){
        int maxValue = 0;
        for(int value: array){
            if(value>maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    /**
     * 按照每个约束的系数，按照轮盘赌的方式随机生成初始解
     */
    public void initialSolutionRoulette(){

        population = new boolean[populationSize][dimension];
        fitnessArray = new Integer[populationSize];
        int p=0;
        while(p<populationSize){
            for(int i=0; i<constraintNum & p < populationSize; i++){
                int[] candidateItem = new int[dimension];
                Double[] addProbability = new Double[dimension];
                Random random = new Random();
                for(int j=0; j<dimension; j++){
                    candidateItem[j] = j;
                    addProbability[j] = localUnitWeightOfPrice[i][j]*random.nextDouble();
                }
                //随机排序
                QuickSortThreeWays.sortThreeWays(addProbability, candidateItem);
                //根据排序结果构造解
                int[] occupyCapacity = new int[constraintNum];
                for (int index: candidateItem){
                    boolean exitFlag = false;
                    for(int k=0; k<constraintNum; k++){
                        if (occupyCapacity[k] + problem.weights[k][index] > problem.capacity[i]) {
                            exitFlag = true;
                            break;
                        }
                    }
                    if(exitFlag)
                        break;
                    for(int k=0; k<constraintNum; k++){
                        occupyCapacity[k] += problem.weights[k][index];
                    }
                    population[p][index] = true;
                }
                p+=1;
            }
        }

        for(p=0; p<populationSize;p++){
            fitnessArray[p] = computeFitness(population[p]);
        }
//        population = new boolean[populationSize][dimension];
//        for(int p=0; p<populationSize; p++){
//            List<Integer> itemIndexList = new ArrayList<>();
//            for(int i=0; i<dimension; i++){
//                itemIndexList.add(i);
//            }
//            Random random = new Random();
//            Integer index = random.nextInt(dimension);
//            int[] occupyCapacity = new int[constraintNum];
//            while (itemIndexList.size()>0){
//                for(int i=0; i<constraintNum; i++){
//                    if (occupyCapacity[i] + problem.weights[i][index] > problem.capacity[i])
//                        break;;
//                }
//                for(int i=0; i<constraintNum; i++){
//                    occupyCapacity[i] += problem.weights[i][index];
//                }
//                population[p][index] = true;
//                itemIndexList.remove(index);
//                index = itemIndexList.get(random.nextInt(itemIndexList.size()));
//            }
//        }
    }

    public int computeFitness(boolean[] individual){
        int fitness = 0;
        for(int i=0; i<dimension; i++){
            if(individual[i])
                fitness += problem.prices[i];
        }
        return fitness;
    }

    public void repairDropAddByGroup(boolean[] individual){

        // 丢弃操作,暂时分为两组测试

        int thred = new Random().nextInt(constraintNum);
        if(thred==0)
            thred = 1;
        dropItems(0, thred, individual);
        dropItems(thred, constraintNum, individual);

        // 增加操作
        //获得可添加的元素的序号
        AddItem addItem = new AddItem(individual);
        if(addItem.getCandidateItem()){
            // 提供了三种增加操作
            int[] candidateItem = addItem.addRouletteByPrice();
            for (int index: candidateItem){
                boolean flag = true;
                //检查是否可添加
                for(int i=0; i<constraintNum; i++){
                    if(addItem.leftCapacityArray[i] < addItem.unChooseItemMinWeightByCons[i])
                        return;
                    if(addItem.leftCapacityArray[i] < problem.weights[i][index]){
                        flag = false;
                        break;
                    }
                }
                //添加
                if(flag){
                    individual[index] = true;
                    // 更新剩余容量
                    for(int i=0; i<constraintNum; i++){
                        addItem.leftCapacityArray[i] -= problem.weights[i][index];
                    }
                }
            }
        }
//        else{
//            System.out.println("无可添加元素");
//        }
    }

    private void dropItems(int startConstraintIndex, int endConstraintIndex, boolean[] individual){
        HashSet<Integer> dropIndexSet = new HashSet<>();
        for(int i=startConstraintIndex; i<endConstraintIndex; i++){
            int gap = getConstraintTotalWeight(individual, i) - problem.capacity[i];
            if(gap>0){
                int chooseItemNum = getChooseItemNum(individual);
                Double[] chooseItemDropProbability = new Double[chooseItemNum];
                int[] chooseItemIndex = new int[chooseItemNum];
                int k = 0;
                Random random = new Random();
                for(int j=0; j<dimension; j++){
                    if(individual[j]){
                        chooseItemDropProbability[k] = localUnitPriceOfWeight[i][j]*random.nextDouble();
                        chooseItemIndex[k] = j;
                        k +=1;
                    }
                }
                QuickSortThreeWays.sortThreeWays(chooseItemDropProbability, chooseItemIndex);
                // 排查对于此约束应该删除的变量
                int index = 0;
                int dropValue = 0;
                // 整理丢弃变量的下标
                while(dropValue<gap){
                    int dropIndex = chooseItemIndex[index];
                    dropValue += problem.weights[i][dropIndex];
                    dropIndexSet.add(dropIndex);
                    index += 1;
                }
            }
        }
        // 删除变量
        for(int index: dropIndexSet){
            individual[index] = false;
        }
    }

    private int getChooseItemNum(boolean[] individual){
        int chooseItemNum = 0;
        for(boolean x: individual){
            if(x)
                chooseItemNum += 1;
        }
        return chooseItemNum;
    }

    //增加操作准备类
    class AddItem{

        int[] candidateItem;
        int[] leftCapacityArray;
        int chooseItemNum;
        boolean[] individual;
        int[] unChooseItemMinWeightByCons;

        AddItem(boolean[] individual){
            this.individual = individual;
            candidateItem = null;
            leftCapacityArray = problem.capacity.clone();
            chooseItemNum = getChooseItemNum(individual);
        }

        /**
         *
         * @return 是否有可添加元素
         */
        boolean getCandidateItem(){

            unChooseItemMinWeightByCons = problem.capacity.clone();
            int[] unChooseItems = new int[dimension-chooseItemNum];
            int k=0;
            for(int j=0; j<dimension; j++){
                if(individual[j]){
                    for(int i=0; i<constraintNum; i++){
                        leftCapacityArray[i] -= problem.weights[i][j];
                    }
                }else{
                    unChooseItems[k] = j;
                    k += 1;
                    for(int i=0; i<constraintNum; i++){
                        unChooseItemMinWeightByCons[i] = Math.min(unChooseItemMinWeightByCons[i], problem.weights[i][j]);
                    }
                }
            }
//            Arrays.stream(problem.weights[0]).min().getAsInt();  //返回数组的最小值
            for(int i=0; i<constraintNum; i++){
                if(leftCapacityArray[i] < unChooseItemMinWeightByCons[i]){
                    return false;
                }
            }

            // 整理可添加元素
            List<Integer> candidateIndex = new ArrayList<>(unChooseItems.length);
            int candidateNum=0;
            for(int index: unChooseItems){
                boolean flag = true;
                for(int i=0; i<constraintNum; i++){
                    if (problem.weights[i][index] > leftCapacityArray[i]) {
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    candidateIndex.add(index);
                    candidateNum += 1;
                }
            }
            candidateItem = new int[candidateNum];
            for(int i=0;i<candidateNum;i++){
                candidateItem[i] = candidateIndex.get(i);
            }
            return true;
        }

        /**
         * 根据每个商品的价格贪婪add元素
         */
        int[] addByPrice(){
            Integer[] addProbability = new Integer[candidateItem.length];
            for(int i=0; i < addProbability.length; i++)
                addProbability[i] = problem.prices[i];
            QuickSortThreeWays.sortThreeWays(addProbability, candidateItem);
            return candidateItem;
        }

        /**
         * 根据每个商品的价格轮盘赌选择方法
         */
        int[] addRouletteByPrice(){
            Double[] addProbability = new Double[candidateItem.length];
            Random random = new Random();
            for(int i=0; i < addProbability.length; i++){
                addProbability[i] = problem.prices[i]*1.0*random.nextDouble();
            }
            QuickSortThreeWays.sortThreeWays(addProbability, candidateItem);
            return candidateItem;
        }

        /**
         * 根据每个商品的价格[j]/sum(重量[i][j])轮盘赌选择方法
         */
        int[] addProbabilityByGlobalUnitWeightOfPrices(){
            Double[] addProbability = new Double[candidateItem.length];
            Random random = new Random();
            for(int i=0; i < addProbability.length; i++){
                addProbability[i] = problem.unitWeightOfPrices[i]*1.0*random.nextDouble();
            }
            QuickSortThreeWays.sortThreeWays(addProbability, candidateItem);
            return candidateItem;
        }
    }


    int getConstraintTotalWeight(boolean[] individual, int index){
        int weightSum=0;
        for(int j=0; j<dimension; j++){
            if (individual[j])
                weightSum += problem.weights[index][j];
        }
        return weightSum;
    }

    public void repairDropAddAll(){

    }

    /**
     * 检查结果是否可行
     * @param individual
     * @return
     */
    public boolean feasibleCheck(boolean[] individual){

        for(int i=0; i<constraintNum; i++){
            int totalWeight=0;
            for(int j=0; j<dimension; j++){
                if(individual[j])
                    totalWeight += problem.weights[i][j];
            }
            if(totalWeight> problem.capacity[i])
                return false;
        }
        return true;
    }

    /**
     *
     * @param title
     * @param fitnessRecord
     */
    public void plotIter(String title, int[] fitnessRecord){
        LineChartDemo linechartdemo = new LineChartDemo(title, fitnessRecord);
        linechartdemo.pack();
        RefineryUtilities.centerFrameOnScreen(linechartdemo);
        linechartdemo.setVisible(true);
    }

    public void plotIter(){
        try {
            String pyFileName = "C:\\Users\\刘祥\\Documents\\python\\operation_research\\zeroOneLinearProgrmming\\iterPlot.py";
            String[] args = new String[] { "C:\\ProgramData\\Anaconda3\\python.exe", pyFileName, outPutFileName};
            Process proc = Runtime.getRuntime().exec(args);// 执行py文件
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public void writeIterToCSV(String fileName, String title, int[] fitnessIterRecord) throws IOException, IllegalAccessException {
//        WriteToCsv.exportCsv(fileName,title, fitnessIterRecord);
//    }

    void resultOutPut(int[] fitnessIterRecord, int iter) throws IOException, IllegalAccessException {
        if(feasibleCheck(currentBestSolution)){
            bestSolution = currentBestSolution;
            metaOptimalValue = currentBestFitness;
            runIter = maxIter;
            endTime = System.currentTimeMillis() / 1000;
            runTime = endTime - startTime;
            runIter = iter;
            endTime = System.currentTimeMillis() / 1000;
            runTime = endTime - startTime;
            WriteToCsv.exportCsv(outPutFileName, "BFA", fitnessIterRecord);
            System.out.printf("算法最优解为%d, 最优解为%d,运算时间为%d\n\n", metaOptimalValue, problem.optimalValue, runTime);
        }else{
            System.out.println("解不可行，请检查算法\n\n");
        }
    }
}
