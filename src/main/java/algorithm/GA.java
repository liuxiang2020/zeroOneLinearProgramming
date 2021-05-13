package algorithm;

import tasks.Knapsack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class GA  extends metaHeuristicForOneZeroProgramming{
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
    public GA(Knapsack problem) {
        super(problem);
        this.maxIter = problem.dimension * 20;
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

    public void bgaSolve() throws InvocationTargetException, IllegalAccessException {

        int[] indexArray = new int[populationSize];
        for(int i=0; i<populationSize; i++){
            indexArray[i] = i;
        }
        // 生成初始解
        initialSolutionRandom();


        /*转换为可行解*/
        for(int p=0; p<populationSize;p++){
            repairFun(this, method1, population[p]);
            fitnessArray[p] = computeFitness(population[p]);
        }
        int iter = 0;
        int[] fitnessIterRecord = new int[maxIter];
        Random random = new Random();

        while(iter<maxIter & currentBestFitness < problem.optimalValue){

            /*生成一个与父代任何个体都不一样的个体*/
            boolean exitFlag = false;

            while(exitFlag){
                /*锦标赛选择，选择competitionNum个个体， 然后从中选择适应度最好的1个*/
                int index1 = chooseOneGene(random);
                boolean[] individual1 = population[index1].clone();

                int index2 = chooseOneGene(random);
                boolean[] individual2 = population[index2].clone();

                /*交叉: 均匀交叉*/
                boolean[] child1 = new boolean[problem.dimension];
                boolean[] child2 = new boolean[problem.dimension];
                for(int i=0; i<problem.dimension; i++){
                    if(random.nextDouble()<0.5){
                        child1[i] = individual1[i];
                        child2[i] = individual2[i];
                    }else{
                        child1[i] = individual2[i];
                        child2[i] = individual1[i];
                    }
                }

                /*变异： 只变异两个位置*/
                /*第一个个体变异*/
                int position = random.nextInt();
                for(int i=0; i<mutateSize; i++){
                    child1[position] = !child1[position];
                    int position2 = random.nextInt();
                    while(position != position2)
                        position2 = random.nextInt();
                    position = position2;
                }
                /*修复为可行解*/
                repairFun(this, method1, individual1);
                //计算适应度函数值
                int fitness1 = computeFitness(individual1);

                /*第二个个体变异*/
                position = random.nextInt();
                for(int i=0; i<mutateSize; i++){
                    child2[position] = !child2[position];
                    int position2 = random.nextInt();
                    while(position != position2)
                        position2 = random.nextInt();
                    position = position2;
                }
                repairFun(this, method1, individual2);

                /*选择个体*/
                int fitness2 = computeFitness(individual2);
                if(fitness1>fitness2){

                }else{

                }
            }





            /*更新种群*/

            iter += 1;
        }

    }

    public int chooseOneGene(Random random){
        int[] candidateEntities = new int[competitionNum];
        for(int i=0; i<competitionNum; i++){
            candidateEntities[i] = random.nextInt(populationSize);
        }
        int chooseEntity = candidateEntities[0];
        int fitness = fitnessArray[chooseEntity];
        for(int index: candidateEntities){
            if(fitnessArray[index] < fitness){
                chooseEntity = index;
                fitness = fitnessArray[chooseEntity];
            }
        }
        return chooseEntity;
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