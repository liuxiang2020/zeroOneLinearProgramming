package algorithm;

import tasks.Knapsack;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Random;

public class BGA  extends metaHeuristicForOneZeroProgramming{
    /*变异率*/
    public double alpha = 0.01;
    /**/
    public int mutateSize = 2;
    /*锦标赛群体大小*/
    public int competitionNum = 2;

    /*最大迭代次数*/
    public int maxIter = 10000;
    /*最大搜索时间*/
    public double maxTime = 100;

    /*反射函数*/
    Method method1;

    /*构造函数*/
    public BGA(Knapsack problem) {
        super(problem);
        if(problem.dimension>250){
            this.maxIter = problem.dimension * 2000;
        }else{
            this.maxIter = problem.dimension * 5000;
        }

    }
    /*设置最大迭代次数*/
    public void setMaxIter(int value){
        this.maxIter = value;
    }
    /*设置参数*/
    public void setParam(double _alpha, int _mutateSize, int _competitionNum, int _populationSize) {
        this.alpha = _alpha;
        this.competitionNum = _competitionNum;
        this.mutateSize = _mutateSize;
        setPopulationSize(_populationSize);
    }

    public void setRepairOperation(String funcName) throws NoSuchMethodException {
        Class[] parameterTypes = new Class[1];
        parameterTypes[0] = boolean[].class;
        method1 = GA.class.getMethod(funcName, parameterTypes);
    }

    public void bgaSolve() throws InvocationTargetException, IllegalAccessException, IOException {

        // 生成初始解
//        initialSolutionRandom();
        /*转换为可行解*/
//        for(int p=0; p<populationSize;p++){
////            repairFun(this, method1, population[p]);
//            fitnessArray[p] = computeFitness(population[p]);
//        }

        // 生成初始解
        initialSolutionForBGA();
        fitnessArray = new Integer[populationSize];
        for(int p=0; p<populationSize;p++){
            fitnessArray[p] = computeFitness(population[p]);
        }

        /*更新当前最优解*/
        currentBestFitness = fitnessArray[0];
        currentBestSolution = population[0].clone();
        for(int p=1; p<populationSize;p++){
            if(fitnessArray[p]>currentBestFitness){
                currentBestFitness = fitnessArray[p];
                currentBestSolution = population[p].clone();
            }
        }

        int iter = 0;
        int[] fitnessIterRecord = new int[(int)maxIter/populationSize];
        Random random = new Random();

        while(iter<maxIter & currentBestFitness < problem.optimalValue){
            /*生成一个与父代任何个体都不一样的个体*/
            boolean haveSameChild = true;
            boolean[] child = new boolean[problem.dimension];
            int fitness = 0;
            while(haveSameChild){
                /*锦标赛选择，选择competitionNum个个体， 然后从中选择适应度最好的1个*/
                int index1 = chooseOneGene(random);
                boolean[] individual1 = population[index1].clone();
                int index2 = chooseOneGene(random);
                boolean[] individual2 = population[index2].clone();

                /*交叉: 均匀交叉*/
                for(int i=0; i<problem.dimension; i++){
                    if(random.nextDouble()<0.5){
                        child[i] = individual1[i];
                    }else{
                        child[i] = individual2[i];
                    }
                }

                /*变异： 只变异两个位置*/
                /*第一个个体变异*/
                int position = random.nextInt(problem.dimension);
                for(int i=0; i<mutateSize; i++){
                    child[position] = !child[position];
                    int position2 = random.nextInt(problem.dimension);
                    while(position == position2)
                        position2 = random.nextInt(problem.dimension);
                    position = position2;
                }

                /*修复为可行解*/
                repairFun(this, method1, child);

                //计算适应度函数值
                fitness = computeFitness(child);

//             /*判断是否有和现有种群中一样的个体*/
               boolean[] sameArray = new boolean[populationSize];
                for(int k=0; k<populationSize; k++){
                    if(fitness==fitnessArray[k]){
                        sameArray[k] = true;
                        for(int j=0; j < problem.dimension; j++){
                            if(child[j]!=population[k][j]){
                                sameArray[k] = false;
                                break;
                            }
                        }
                    }
                }
                haveSameChild = false;
                for(boolean sameFlag: sameArray){
                    if(sameFlag){
                        haveSameChild = true;
                        break;
                    }
                }
            }

            /*更新种群：替换最差的个体*/
            int replaceIndex = getWorstChrom();
//            if( fitness> fitnessArray[replaceIndex]){
                fitnessArray[replaceIndex] = fitness;
                population[replaceIndex] = child.clone();
                /*更新最优解等信息*/
                if(fitness > currentBestFitness){
                    currentBestFitness = fitness;
                    currentBestSolution = child;
                }
//            }
            int tempIter = iter % populationSize;
            if(tempIter % populationSize == 0){
                int tempIter2 = iter / populationSize;
                fitnessIterRecord[tempIter2] = currentBestFitness;
                if(tempIter2 % (problem.dimension) ==0)
                    System.out.printf("BGA算法，第%d代，当前最好解为:%d，与最优解的gap为%.2f%%, alpha为%.4f\n", tempIter2, currentBestFitness,
                            (problem.optimalValue-currentBestFitness)*100.0/problem.optimalValue, alpha);
            }
            iter += 1;
        }
        // 检查结果正确性， 并输出
        if(iter<maxIter){
            int tempIter2 = iter / populationSize + 1;
            fitnessIterRecord[tempIter2] = currentBestFitness;
            int[] fitnessIterRecord2 = new int[tempIter2+1];
            if (tempIter2 >= 0) System.arraycopy(fitnessIterRecord, 0, fitnessIterRecord2, 0, tempIter2+1);
            resultOutPut(fitnessIterRecord2, fitnessIterRecord2.length);
        }else{
            resultOutPut(fitnessIterRecord, fitnessIterRecord.length);
        }
    }

    public int chooseOneGene(Random random){
        Random random1 = new Random();
        int[] candidateEntities = new int[competitionNum];
        for(int i=0; i<competitionNum; i++){
            candidateEntities[i] = random1.nextInt(populationSize);
        }
        int chooseEntity = candidateEntities[0];
        int fitness = fitnessArray[chooseEntity];
        for(int index: candidateEntities){
            if(fitnessArray[index] > fitness){
                chooseEntity = index;
                fitness = fitnessArray[chooseEntity];
            }
        }
        return chooseEntity;
    }

    public int getWorstChrom(){
        int index = 0;
        int fitness = fitnessArray[0];
        for(int k=1; k<populationSize; k++){
            if(fitness > fitnessArray[k]){
                index = k;
                fitness = fitnessArray[k];
            }
        }
        return index;
    }
}

///*变异：均匀变异*/
//for(int i=0; i<problem.dimension; i++){
//if(random.nextDouble()<alpha){
//child1[i] = !child1[i];
//}
//}
//for(int i=0; i<problem.dimension; i++){
//if(random.nextDouble()<alpha){
//child2[i] = !child2[i];
//}
//}