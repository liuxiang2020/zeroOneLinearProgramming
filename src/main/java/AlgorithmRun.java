import algorithm.BFA;
import algorithm.CplexSolve;
import algorithm.CplexSolveForDual;
import ilog.concert.IloException;
import tasks.Knapsack;
import utils.WriteToCsv;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class AlgorithmRun {

    public void testAllChu() throws IloException, IOException, IllegalAccessException {
        int[] consNum = {5, 10, 30};
        int[] itemNum = {100, 250, 500};
        double[] alphaNum = {0.25, 0.5, 0.75};
        int index = 0;
        int[] objectiveArray = new int[270];
        int[] usedTimeArray = new int[270];
        String[] fileNameArray = new String[270];
        int[] optimalArray = new int[270];
        String[] statusArray = new String[270];
        double[] gapArray = new double[270];
        String[] cplexStatus = new String[270];

        for(int cons: consNum){
            for(int item: itemNum){
                for(double alpha: alphaNum){
                    for(int i=1; i<11; i++){
                        String filename = String.format("src/main/resources/Chu's MKP Benchmarks/OR%dx%d/OR%dx%d-%.2f_%d.dat",
                                cons, item, cons, item, alpha, i);
                        long startTime =  System.currentTimeMillis();
                        fileNameArray[index] = String.format("OR%dx%d-%.2f_%d.dat", cons, item, alpha, i);
                        Knapsack problem = testOneProblem("Chu", filename);
                        objectiveArray[index] = problem.cplexObjective;
                        usedTimeArray[index] = (int)(System.currentTimeMillis()-startTime)/1000;
                        optimalArray[index] = problem.optimalValue;
                        statusArray[index] = problem.status;
                        gapArray[index] = problem.cplexGap;
                        cplexStatus[index] = problem.cplexStatus;
                        System.out.printf("index: %d, objective: %d, useTime: %d, cplexStatus: %s\n\n",
                                index, objectiveArray[index], usedTimeArray[index], cplexStatus[index]);
                        index += 1;
//                        if(index>2){
//                            String outputFile = "src/main/resources/result/CplexForChu.csv";
//                            String[] titles = {"filename", "objective", "useTime"};
//                            WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray);
//                            return;
//                        }
                    }
                }
            }
        }
        String outputFile = "src/main/resources/result/CplexForChu.csv";
        WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray, cplexStatus);
    }

    public void testAllWeingAndGK() throws IloException, IOException, IllegalAccessException {
        int index = 1;
        int[] objectiveArray = new int[19];
        int[] usedTimeArray = new int[19];
        String[] fileNameArray = new String[19];
        int[] optimalArray = new int[19];
        double[] gapArray = new double[19];
        String[] statusArray = new String[19];
        String[] cplexStatus = new String[270];

        String[] fileTypeArray = new String[]{"Weing", "GK"};
        int flag1;
        int flag2;
        String flag3;
        for(String fileType: fileTypeArray){
            if(fileType.equals("Weing")){
                flag1 = 9;
                flag2 = -1;
                flag3 = "weing";
            }else{
                flag1 = 12;
                flag2 = 7;
                flag3 = "gk";
            }
            for(int i=1; i<flag1; i++){
                String filename = String.format("src/main/resources/%s MKP Benchmarks/%s%d.dat", fileType, flag3, i);
                long startTime =  System.currentTimeMillis();
                fileNameArray[i+flag2] = String.format("%s%d.dat", flag3, i);
                Knapsack problem = testOneProblem(fileType, filename);
//            Knapsack problem = testByBFA(fileType, filename);
                objectiveArray[i+flag2] = problem.cplexObjective;
                usedTimeArray[i+flag2] = (int)(System.currentTimeMillis()-startTime)/1000;
                optimalArray[i+flag2] = problem.optimalValue;
                statusArray[i+flag2] = problem.status;
                gapArray[i+flag2] = problem.cplexGap;
                cplexStatus[i+flag2] = problem.cplexStatus;
                System.out.printf("index: %d, objective: %d, useTime: %d, cplexStatus: %s\n\n",
                        i+flag2, objectiveArray[i+flag2], usedTimeArray[i+flag2], cplexStatus[i+flag2]);
            }
        }
        String outputFile = "src/main/resources/result/CplexForWeingAndGK.csv";
        WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray, cplexStatus);
//        for(int i=1; i<9; i++){
//            String filename = String.format("src/main/resources/Weing MKP Benchmarks/weing%d.dat", i);
//            long startTime =  System.currentTimeMillis();
//            fileNameArray[i-1] = String.format("weing%d.dat", i);
//            Knapsack problem = testOneProblem("Weing", filename);
////            Knapsack problem = testByBFA("Weing", filename);
//            objectiveArray[i-1] = problem.cplexObjective;
//            usedTimeArray[i-1] = (int)(System.currentTimeMillis()-startTime)/1000;
//            optimalArray[i-1] = problem.optimalValue;
//            statusArray[i-1] = problem.status;
//            gapArray[i-1] = problem.cplexGap;
//            System.out.printf("index: %d, objective: %d, useTime: %d\n\n",
//                    i-1, objectiveArray[i-1], usedTimeArray[i-1]);
//        }
//
//        for(int i=1; i<12; i++){
//            String filename = String.format("src/main/resources/GK MKP Benchmarks/gk%d.dat", i);
//            long startTime =  System.currentTimeMillis();
//            fileNameArray[i+7] = String.format("gk%d.dat", i);
//            Knapsack problem = testOneProblem("GK", filename);
////            Knapsack problem = testByBFA("GK", filename);
//            objectiveArray[i+7] = problem.cplexObjective;
//            usedTimeArray[i+7] = (int)(System.currentTimeMillis()-startTime)/1000;
//            optimalArray[i+7] = problem.optimalValue;
//            statusArray[i+7] = problem.status;
//            gapArray[i+7] = problem.cplexGap;
//            System.out.printf("index: %d, objective: %d, useTime: %d\n\n", i+7, objectiveArray[i+7], usedTimeArray[i+7]);
//        }
//        String outputFile = "src/main/resources/result/CplexForWeingAndGK.csv";
//        WriteToCsv.exportCsv(outputFile, fileNameArray, objectiveArray, optimalArray, statusArray, gapArray, usedTimeArray);
    }

    public Knapsack testOneProblem(String fileType, String filename) throws IloException {
        Knapsack problem = new Knapsack(fileType, filename);
        CplexSolve cplexSolve = new CplexSolve(problem);
        cplexSolve.solveModel();
        problem.setCplexObjective(cplexSolve.objectiveValue);
        problem.setCplexGap(cplexSolve.gap);
        problem.setCplexStatus((cplexSolve.status));
        return problem;
    }

    public Knapsack testOneProblemDual(String fileType, String filename) throws IloException {
        Knapsack problem = new Knapsack(fileType, filename);
        CplexSolveForDual cplexSolve = new CplexSolveForDual(problem);
        cplexSolve.solveModel();
        problem.setCplexObjective(cplexSolve.objectiveValue);
        problem.setCplexGap(cplexSolve.gap);
        problem.setCplexStatus((cplexSolve.status));
        return problem;
    }

    public void testOneProblem(Knapsack problem) throws IloException {
        CplexSolve cplexSolve = new CplexSolve(problem);
        cplexSolve.solveModel();
        problem.setCplexObjective(cplexSolve.objectiveValue);
        problem.setCplexGap(cplexSolve.gap);
    }

    public Knapsack testByBFA(String fileType, String filename) throws IloException, IOException, IllegalAccessException, InvocationTargetException {
        Knapsack problem = new Knapsack(fileType, filename);
        problem.itemPreSortForRo2();
//        testOneProblem(problem);
        BFA algorithm = new BFA(problem);
        algorithm.setParam(0.02,100,0,50);
        algorithm.bfaSolve();
        return problem;
    }


    public static void main(String[] args) throws IloException, IOException, IllegalAccessException {

        AlgorithmRun algorithmRun = new AlgorithmRun();
        Knapsack problem = algorithmRun.testOneProblemDual("Chu", "src/main/resources/Chu's MKP Benchmarks/OR5x100/OR5x100-0.25_5.dat");
//        Knapsack problem = algorithmRun.testByBFA("Chu", "src/main/resources/Chu's MKP Benchmarks/OR5x100/OR5x100-0.25_5.dat");
//        algorithmRun.testAllWeingAndGK();
        algorithmRun.testAllChu();
    }
}

