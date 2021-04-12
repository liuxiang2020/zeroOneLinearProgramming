package algorithm;

import org.jfree.ui.RefineryUtilities;
import tasks.Knapsack;
import visualization.LineChartDemo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class metaHeuristicForOneZeroProgramming {
    public Knapsack problem;
    //问题维数
    private final int dimension;
    //问题约束个数
    private final int constraintNum;
    // 文献或Cplex最好解
    public int optimalValue;
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
    public double[][] localUnitPriceOfWeight;
    public double[][] localUnitWeightOfPrice;

    public metaHeuristicForOneZeroProgramming(Knapsack problem){
        startTime = System.currentTimeMillis()/1000;
        this.problem = problem;
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
     * 随机设生成初始解
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
            // todo 检查结果是否正确
            repairDropAddByGroup(population[p]);
            fitnessArray[p] = computeFitness(population[p]);
        }

    }

    public Integer getMaxValue(Integer[] array){
        Integer maxValue = 0;
        for(int value: array){
            if(value>maxValue)
                maxValue = value;
        }
        return maxValue;
    }

    /**
     * 按照每个约束的系数，按照轮盘赌的方式随机生成初始解
     */
    public void initialSolutionGreedy(){

        population = new boolean[populationSize][dimension];
        fitnessArray = new Integer[populationSize];
        int p=0;
        while(p<populationSize){
            for(int i=0; i<constraintNum; i++){
                int[] candidateItem = new int[dimension];
                Double[] addProbability = new Double[dimension];
                Random random = new Random();
                for(int j=0; j<dimension; j++){
                    candidateItem[j] = j;
                    addProbability[j] = localUnitPriceOfWeight[i][j]*random.nextDouble();
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

        for(int pp=0; pp<populationSize;pp++){
            fitnessArray[pp] = computeFitness(population[pp]);
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
        //  todo 检测是元素是否改变了
        // 丢弃操作,暂时分为两组测试

        int thred = new Random().nextInt(constraintNum);
        if(thred==0)
            thred = 1;
        dropItems(0, thred, individual);
        dropItems(thred, constraintNum, individual);

        // 增加操作
        //获得可添加的元素的序号
        AddItem addItem = new AddItem(individual);
        addItem.getCandidateItem();
        // 提供了三种增加操作
        int[] candidateItem = addItem.addRouletteByPrice();
        for (int index: candidateItem){
            //检查是否可添加
            for(int i=0; i<constraintNum; i++){
                if(addItem.leftCapacityArray[i] < addItem.unChooseItemMinWeightByCons[i])
                    return;
                if(addItem.leftCapacityArray[i] < problem.weights[i][index])
                    break;
            }
            //添加
            individual[index] = true;
            // 更新剩余容量
            for(int i=0; i<constraintNum; i++){
                addItem.leftCapacityArray[i] -= problem.weights[i][index];
            }
        }
    }

    private void dropItems(int startConstraintIndex, int endConstraintIndex, boolean[] individual){
        HashSet<Integer> dropIndexSet = new HashSet<>();
        for(int i=startConstraintIndex; i<endConstraintIndex; i++){
            int gap = getConstraintTotalWeight(individual, i) - problem.capacity[i];
            if(gap>0){
                int chooseItemNum = getChooseItemNum(individual);
                Double[] chooseItemWeight = new Double[chooseItemNum];
                int[] chooseItemIndex = new int[chooseItemNum];
                int k = 0;
                Random random = new Random();
                for(int j=0; j<dimension; j++){
                    if(individual[j]){
                        chooseItemWeight[k] = localUnitPriceOfWeight[i][j]*random.nextDouble();
                        chooseItemIndex[k] = j;
                        k +=1;
                    }
                }
                QuickSortThreeWays.sortThreeWays(chooseItemWeight, chooseItemIndex);
                // 排查对于此约束应该删除的变量
                int index = 0;
                int dropValue = 0;

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

        void getCandidateItem(){

            unChooseItemMinWeightByCons = new int[constraintNum];
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

            for(int i=0; i<constraintNum; i++){
                if(leftCapacityArray[i] < unChooseItemMinWeightByCons[i]){
                    return;
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

            candidateItem = new int[candidateIndex.size()];
            for(int i=0;i<candidateNum;i++){
                candidateItem[i] = candidateIndex.get(i);
            }
        }

        /**
         * 根据每个商品的价格贪婪选择方法
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

}
