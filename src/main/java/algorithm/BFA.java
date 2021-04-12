package algorithm;

import tasks.Knapsack;

import java.util.Random;

public class BFA extends metaHeuristicForOneZeroProgramming {

    public double alpha = 0.01;
    public int gama = 100;
    public int bet0 = 1;
    // 种群大小

    public int maxIter = 10000;
    public double maxTime = 100;

    public BFA(Knapsack problem) {
        super(problem);
        this.maxIter = problem.dimension * 10;
    }

    public void setParam(double _alpha, int _gama, int _bet0, int _populationSize) {
        this.alpha = _alpha;
        this.bet0 = _bet0;
        this.gama = _gama;
        setPopulationSize(_populationSize);
    }

    public void bfaSolve() {
        // 生成初始解
        initialSolutionRandom();
        currentBestFitness = getMaxValue(fitnessArray);
        for(int p=0; p<populationSize; p++){
            if(fitnessArray[p]==currentBestFitness){
                currentBestSolution = population[p];
            }
        }

        int iter = 0;
        int[] fitnessIterRecord = new int[maxIter];
        Random random = new Random();
        // 迭代寻优
        while (iter < maxIter & currentBestFitness < problem.optimalValue) {
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
                            if (individual1[j] == individual2[j]) {
                                diffIndex[diffSize] = j;
                                diffSize += 1;
                            }
                        }
                        //定向移动
                        double flagValue = bet0 * Math.exp(-gama * Math.pow(diffSize, 2));
                        for (int j = 0; j < diffSize; j++) {
                            if (random.nextDouble() < flagValue) {
                                int index = diffIndex[j];
                                individual1[index] = individual2[index];
                            }
                        }
                        //随机移动
                        for (int j = 0; j < problem.dimension; j++) {
                            if (random.nextDouble() < alpha)
                                individual1[j] = !individual1[j];
                        }
                        // 可行化 todo 检查值是否改变
                        repairDropAddByGroup(individual1);
                        //计算适应度函数值
                        fitness = computeFitness(individual1);

                        // 判断是否移动
                        if (fitness <= fitnessArray[p1]) {
                            individual1 = population[p1].clone();
                            fitness = fitnessArray[p1];
                        } else {
                            population[p1] = individual1.clone();
                            fitnessArray[p1] = fitness;
                            if (fitness >= problem.optimalValue) {
                                bestSolution = individual1;
                                optimalValue = fitness;
                                runIter = iter;
                                endTime = System.currentTimeMillis() / 1000;
                                runTime = endTime - startTime;
                                boolean feasible = feasibleCheck(individual1);
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
            repairDropAddByGroup(individual);
            //计算适应度函数值
            int fitness = computeFitness(individual);
            if(fitness>fitnessArray[populationSize-1]){
                fitnessArray[populationSize-1] = fitness;
                population[populationSize - 1] = individual;
            }

            // 根据亮度重新排序
            int[] indexArray = new int[populationSize];
            for(int i=0; i<populationSize; i++){
                indexArray[i] = i;
            }
            QuickSortThreeWays.sortThreeWays(fitnessArray, indexArray);
            boolean[][] populationCopy = population.clone();
            for(int i=0; i<populationSize; i++){
                population[i] = populationCopy[populationSize-1-i];
            }

            // 更新当前最优解
            if(fitnessArray[populationSize-1]>=currentBestFitness){
                currentBestFitness = fitnessArray[populationSize-1];
                currentBestSolution = population[populationSize-1];
            }
            fitnessIterRecord[iter] = currentBestFitness;

            iter += 1;
        }
        // 结果正确性检查
        boolean feasible = feasibleCheck(currentBestSolution);
        if(feasible){
            bestSolution = currentBestSolution;
            optimalValue = currentBestFitness;
            runIter = maxIter;
            endTime = System.currentTimeMillis() / 1000;
            runTime = endTime - startTime;
            optimalValue = currentBestFitness;
            runIter = maxIter;
            endTime = System.currentTimeMillis() / 1000;
            runTime = endTime - startTime;
            System.out.printf("算法最优解为%d, cplex最优解为%d,运算时间为%d", optimalValue, problem.cplexObjective, runTime);
        }else{
            System.out.println("解不可行，请检查算法");
        }
        // 绘图展示
        plotIter("Iteration of BFA", fitnessIterRecord);

    }


    public void directionalMove() {

    }

    public void randomMove() {

    }

}
