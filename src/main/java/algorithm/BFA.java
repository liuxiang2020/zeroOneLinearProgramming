package algorithm;

import tasks.Knapsack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class BFA extends metaHeuristicForOneZeroProgramming {

    public double alpha = 0.01;
    public double gama = 100.0;
    public int bet0 = 1;
    // 种群大小

    public int maxIter = 10000;
    public double maxTime = 100;

    /*选用的修补算子*/
    Method method1;

    public BFA(Knapsack problem) {
        super(problem);
        this.maxIter = problem.dimension * 10;
    }

    public void setMaxIter(int value){
        this.maxIter = value;
    }

    public void setParam(double _alpha, double _gama, int _bet0, int _populationSize) {
        this.alpha = _alpha;
        this.bet0 = _bet0;
        this.gama = _gama;
        setPopulationSize(_populationSize);
    }

    public void setRepairOperation(String funcName) throws NoSuchMethodException {
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = boolean[].class;
        method1 = BFA.class.getMethod(funcName, parameterTypes);
    }

    public void bfaSolve() throws IOException, IllegalAccessException, InvocationTargetException {

        int[] indexArray = new int[populationSize];
        for(int i=0; i<populationSize; i++){
            indexArray[i] = i;
        }

        // 生成初始解
        initialSolutionRandom();
//        initialSolutionRoulette();
        /*转换为可行解*/
        for(int p=0; p<populationSize;p++){
            repairFun(this, method1, population[p]);
            fitnessArray[p] = computeFitness(population[p]);
        }
        /*排序*/
        sortFitness(indexArray.clone());
        // 更新gamma值
        gama = gama/Math.pow(problem.dimension, 2);
        int iter = 0;
        int[] fitnessIterRecord = new int[maxIter];
        Random random = new Random();
        double alpha1 = alpha/4;
        double alpha2 = alpha*3/4;
        // 迭代寻优
        while (iter < maxIter & currentBestFitness < problem.optimalValue) {
            alpha = alpha1 + (1-iter*1.0/maxIter)*alpha2;
            for (int p1 = 0; p1 < populationSize - 1; p1++) {
                boolean[] individual1 = population[p1].clone();
                int fitness = fitnessArray[p1];
                for (int p2 = p1 + 1; p2 < populationSize; p2++) {
                    if (fitness < fitnessArray[p2]) {
                        // 获取不同元素所在位置
                        boolean[] individual2 = population[p2].clone();
                        int[] diffIndex = new int[problem.dimension];
                        int diffSize = 0;
                        for (int j = 0; j < problem.dimension; j++) {
                            if (individual1[j] != individual2[j]) {
                                diffIndex[diffSize] = j;
                                diffSize += 1;
                            }
                        }
                        //定向移动
                        double flagValue = bet0 * Math.exp(-gama * Math.pow(diffSize, 2));
                        for(int j=0; j<diffSize; j++){
                            if (random.nextDouble() < flagValue) {
                                int index =  diffIndex[j];
                                individual1[index] = individual2[index];
                            }
                        }
//                        for (int index: diffIndex) {
//                            if (random.nextDouble() < flagValue) {
//                                individual1[index] = individual2[index];
//                            }
//                        }
                        //随机移动
                        /*随机移动*/
                        for (int j = 0; j < problem.dimension; j++) {
                            if (random.nextDouble() < alpha)
                                individual1[j] = !individual1[j];
                        }

//                        repairDropAddByGroup(individual1);
                        repairFun(this, method1, individual1);
                        //计算适应度函数值
                        fitness = computeFitness(individual1);

                        // 判断是否移动
                        if (fitness <= fitnessArray[p1]) {
                            individual1 = population[p1].clone();
                            fitness = fitnessArray[p1];
                        } else {
                            population[p1] = individual1.clone();
                            fitnessArray[p1] = fitness;
                            if (fitness > problem.optimalValue) {
                                currentBestFitness = fitness;
                                currentBestSolution = individual1;
                                resultOutPut(fitnessIterRecord, iter);
                                return;
                            }
                        }
                    }
                }
            }
            // 最亮的萤火虫随机移动
            boolean[] individual = population[populationSize - 1].clone();
            for (int j = 0; j < problem.dimension; j++) {
                if (random.nextDouble() < alpha)
                    individual[j] = !individual[j];
            }
//            repairDropAddByGroup(individual);
            repairFun(this, method1, individual);

            //计算适应度函数值
            int fitness = computeFitness(individual);
            if(fitness > fitnessArray[populationSize-1]){
                fitnessArray[populationSize-1] = fitness;
                population[populationSize - 1] = individual.clone();
            }

            // 根据亮度重新排序
            sortFitness(indexArray.clone());

            // 更新当前最优解
            if(fitnessArray[populationSize-1]>=currentBestFitness){
                currentBestFitness = fitnessArray[populationSize-1];
                currentBestSolution = population[populationSize-1].clone();
            }
            fitnessIterRecord[iter] = currentBestFitness;

            iter += 1;

            if(iter % (problem.dimension) ==0)
                System.out.printf("BFA算法，第%d代，当前最好解为:%d，与最优解的gap为%.2f%%, alpha为%.4f\n", iter, currentBestFitness,
                        (problem.optimalValue-currentBestFitness)*100.0/problem.optimalValue, alpha);
        }
        // 检查结果正确性， 并输出
        resultOutPut(fitnessIterRecord, iter);
        // 绘图展示
//        plotIterByPython();
    }

    public void sortFitness(int[] indexArrayCopy){

        boolean[][] populationCopy = new boolean[populationSize][problem.dimension];
        for(int i=0; i<populationSize; i++){
            populationCopy[i] = population[i].clone();
        }

        QuickSortThreeWays.sortThreeWays(fitnessArray, indexArrayCopy, true);

        for(int i=0; i<populationSize; i++){
            population[i] = populationCopy[indexArrayCopy[i]];
        }
//        currentBestFitness = fitnessArray[populationSize-1];
//        currentBestSolution = population[populationSize-1];
    }
}