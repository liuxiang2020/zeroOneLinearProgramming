package algorithm;

import tasks.Knapsack;
import utils.WriteToCsv;

import java.io.IOException;
import java.util.List;

public class Solution {
    /*算例基础信息*/
    public String fileName;
    public int optimal;
    public String status;
    public int testK;

    /*算法基础信息*/
    public String metaAlgorithm;
    public String repairOperation;
    /*求解结果*/
    public long usedTime;
    public int runIter;
    public int[] iterValueList;
    public int metaOptimalValue;
    public boolean[] bestSolution;

    public Solution(String fileName, int optimal, String status, String metaAlgorithm, String repairOperation, metaHeuristicForOneZeroProgramming algorithm){
        this.fileName = fileName;
        this.metaAlgorithm = metaAlgorithm;
        this.repairOperation = repairOperation;
        this.optimal = optimal;
        this.status = status;
        metaOptimalValue = algorithm.metaOptimalValue;
        usedTime = algorithm.runTime;
        runIter = algorithm.runIter;
        bestSolution = algorithm.currentBestSolution;
        iterValueList = algorithm.iterValueRecord;
    }

    public void setFileName(String fileName) {
       this.fileName = fileName;
    }

    public void setTestK(int test){
        testK = test;
    }

    /**
     * 检查结果是否可行
     * @param individual
     * @return
     */
    public boolean feasibleCheck(Knapsack problem, boolean[] individual){

        for(int i=0; i< problem.nConstraint; i++){
            int totalWeight=0;
            for(int j=0; j< problem.dimension; j++){
                if(individual[j])
                    totalWeight += problem.weights[i][j];
            }
            if(totalWeight > problem.capacity[i])
                return false;
        }
        return true;
    }
}
