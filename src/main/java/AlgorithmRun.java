import algorithm.BFA;
import algorithm.CplexSolve;
import ilog.concert.IloException;
import org.jfree.ui.RefineryUtilities;
import tasks.Knapsack;
import utils.WriteToCsv;
import visualization.LineChartDemo;

import java.io.IOException;

public class AlgorithmRun {

    public void testAllChu() throws IloException, IOException, IllegalAccessException {
        int[] consNum = {5, 10, 30};
        int[] itemNum = {100, 250, 500};
        double[] alphaNum = {0.25, 0.5, 0.75};
        int index = 1;
        int[] objectiveArray = new int[270];
        int[] usedTimeArray = new int[270];
        String[] fileNameArray = new String[270];
        int[] optimalArray = new int[270];
        String[] statusArray = new String[270];
        double[] gapArray = new double[270];

        for(int cons: consNum){
            for(int item: itemNum){
                for(double alpha: alphaNum){
                    for(int i=1; i<11; i++){
                        String filename = String.format("src/main/resources/Chu's MKP Benchmarks/OR%dx%d/OR%dx%d-%.2f_%d.dat",
                                cons, item, cons, item, alpha, i);
                        long startTime =  System.currentTimeMillis();
                        fileNameArray[index-1] = String.format("OR%dx%d-%.2f_%d.dat", cons, item, alpha, i);
                        Knapsack problem = testOneProblem("Chu", filename);
                        objectiveArray[index-1] = problem.cplexObjective;
                        usedTimeArray[index-1] = (int)(System.currentTimeMillis()-startTime)/1000;
                        optimalArray[index-1] = problem.optimalValue;
                        statusArray[index-1] = problem.status;
                        gapArray[index-1] = problem.cplexGap;
                        System.out.printf("index: %d, objective: %d, useTime: %d", index, objectiveArray[index], usedTimeArray[index]);

                        index += 1;
                        if(index>2){
                            String outputFile = "src/main/resources/result/CplexForChu.csv";
                            String[] titles = {"filename", "objective", "useTime"};
                            WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray);
                            return;
                        }

                    }
                }
            }
        }
        String outputFile = "src/main/resources/result/CplexForChu.csv";

        WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray);
    }

    public void testAllWeingAndGK() throws IloException, IOException, IllegalAccessException {
        int index = 1;
        int[] objectiveArray = new int[19];
        int[] usedTimeArray = new int[19];
        String[] fileNameArray = new String[19];
        int[] optimalArray = new int[19];
        double[] gapArray = new double[19];
        String[] statusArray = new String[19];

        for(int i=1; i<9; i++){
            String filename = String.format("src/main/resources/Weing MKP Benchmarks/weing%d.dat", i);
            long startTime =  System.currentTimeMillis();
            fileNameArray[i-1] = String.format("weing%d.dat", i);
//            Knapsack problem = testOneProblem("Weing", filename);
            Knapsack problem = testByBFA("Weing", filename);
            objectiveArray[i-1] = problem.cplexObjective;
            usedTimeArray[i-1] = (int)(System.currentTimeMillis()-startTime)/1000;
            optimalArray[i-1] = problem.optimalValue;
            statusArray[i-1] = problem.status;
            gapArray[i-1] = problem.cplexGap;
            System.out.printf("index: %d, objective: %d, useTime: %d", index, objectiveArray[index], usedTimeArray[index]);
        }

        for(int i=1; i<12; i++){
            String filename = String.format("src/main/resources/GK MKP Benchmarks/gk%d.dat", i);
            long startTime =  System.currentTimeMillis();
            fileNameArray[i+7] = String.format("gk%d.dat", i);
            Knapsack problem = testOneProblem("GK", filename);
            objectiveArray[i+7] = problem.cplexObjective;
            usedTimeArray[i+7] = (int)(System.currentTimeMillis()-startTime)/1000;
            optimalArray[i+7] = problem.optimalValue;
            statusArray[i+7] = problem.status;
            gapArray[i+7] = problem.cplexGap;
            System.out.printf("index: %d, objective: %d, useTime: %d", index, objectiveArray[index], usedTimeArray[index]);
        }
        String outputFile = "src/main/resources/result/CplexForWeingAndChu.csv";

        WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray);
    }

    public Knapsack testOneProblem(String fileType, String filename) throws IloException {
        Knapsack problem = new Knapsack(fileType, filename);
        CplexSolve cplexSolve = new CplexSolve(problem);
        cplexSolve.solveModel();
        problem.setCplexObjective(cplexSolve.objectiveValue);
        problem.setCplexGap(cplexSolve.gap);
        return problem;
    }

    public Knapsack testByBFA(String fileType, String filename){
        Knapsack problem = new Knapsack(fileType, filename);
        BFA algorithm = new BFA(problem);
        algorithm.setParam(0.02,1,0,50);
        algorithm.bfaSolve();
        return problem;
    }



    public static void main(String[] args) throws IloException, IOException, IllegalAccessException {
        AlgorithmRun algorithmRun = new AlgorithmRun();
        algorithmRun.testAllWeingAndGK();
        algorithmRun.testAllChu();
    }
}

