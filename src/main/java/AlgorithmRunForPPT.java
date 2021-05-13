import algorithm.*;
import ilog.concert.IloException;

import tasks.Knapsack;
import utils.WriteToCsv;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class AlgorithmRunForPPT {

    /*萤火虫算法测试*/
    public Solution testByBFA(Knapsack problem, String repairFUnName) throws NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException, IloException {
        /*问题预处理*/
        preProcess(problem, repairFUnName);
        /*算法初始化*/
        BFA algorithm = new BFA(problem);
        /*设置求解参数*/
        if (problem.dimension > 250) {
            algorithm.setParam(0.03, 100, 1, 50);
        } else {
            algorithm.setParam(0.05, 100, 1, 50);
        }
//        algorithm.setMaxIter(5);

        /*设置修复算子*/
        algorithm.setRepairOperation(repairFUnName);
        /*求解*/
        algorithm.bfaSolve();
        /*输出结果，并检查结果是否正确*/
        Solution solution = new Solution(problem.fileName, problem.optimalValue, problem.status, "BFA", repairFUnName, algorithm);
        if (solution.feasibleCheck(problem, algorithm.currentBestSolution)) {
            double gap = (problem.optimalValue - algorithm.metaOptimalValue) * 100.0 / problem.optimalValue;
            System.out.printf("解可行, 求解函数为%s, 修复算子为%s, 算法最优解为%d, 文献最优解为%d, gap为%.2f%%, 运算时间为%ds，迭代次数为%d\n\n",
                    "BFA", repairFUnName, algorithm.metaOptimalValue, problem.optimalValue, gap, algorithm.runTime, solution.runIter);
        } else {
            System.out.println("解不可行，请检查错误");
        }

        return solution;
    }

    /*遗传算法测试*/
    public Solution testByBGA(Knapsack problem, String repairFUnName) throws NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException, IloException {
        /*问题预处理*/
        preProcess(problem, repairFUnName);
        /*算法初始化*/
        BGA algorithm = new BGA(problem);
        /*设置求解参数*/
        if (problem.dimension > 250) {
            algorithm.setParam(0.01, 2, 2, 100);
        } else {
            algorithm.setParam(0.01, 2, 2, 100);
        }
//        algorithm.setMaxIter(5);

        /*设置修复算子*/
        algorithm.setRepairOperation(repairFUnName);
        /*求解*/
        algorithm.bgaSolve();
        /*输出结果，并检查结果是否正确*/
        Solution solution = new Solution(problem.fileName, problem.optimalValue, problem.status, "BGA", repairFUnName, algorithm);
        if (solution.feasibleCheck(problem, algorithm.currentBestSolution)) {
            double gap = (problem.optimalValue - algorithm.metaOptimalValue) * 100.0 / problem.optimalValue;
            System.out.printf("解可行, 求解函数为%s, 修复算子为%s, 算法最优解为%d, 文献最优解为%d, gap为%.2f%%, 运算时间为%ds，迭代次数为%d\n\n",
                    "BGA", repairFUnName, algorithm.metaOptimalValue, problem.optimalValue, gap, algorithm.runTime, solution.runIter);
        } else {
            System.out.println("解不可行，请检查错误");
        }

        return solution;
    }

    /*预处理*/
    public void preProcess(Knapsack problem, String repairFUnName) throws IloException {

        if (repairFUnName.equals("dropItemsByDual") | repairFUnName.equals("RO1")) {
            /*对偶排序*/
            CplexSolveForDual cplexSolveDual = new CplexSolveForDual(problem);
            cplexSolveDual.solveModel();
            problem.setDualVar(cplexSolveDual.getPrices());
            problem.iterPreSortForDual(problem.dualVar);
        } else if (repairFUnName.equals("RO2") | repairFUnName.equals("repairDropAddByGroup")) {
            problem.itemPreSortForRo2();
        }
    }

    /*获取系统日期*/
    public String getDate() {
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        return year + "" + month + "" + date + "-" + hour + "-" + minute;
    }

    /*调用算法主程序*/
//    public static void main(String[] args) throws IloException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//
//        String algorithmName = "BFA"; /*BFA*/
//
//        /*step 1 设置算例和初始化*/
//        double[][] testSuits = {{5, 100, 0.25, 10}, {5, 250, 0.5, 10}, {5, 500, 0.75, 10},
//                {10, 100, 0.25, 10}, {10, 250, 0.5, 10}, {10, 500, 0.75, 10},
//                {30, 100, 0.25, 10}, {30, 250, 0.5, 10}, {30, 500, 0.75, 10}};
//        String[] repairFunArray = {"repairDropAddByGroup", "RO2", "dropItemsByDual", "RO1"};
//        AlgorithmRunForPPT run = new AlgorithmRunForPPT();
//        List<Solution> solutionList = new ArrayList<>();
//
//        /*step 2 算法求解*/
//        for (int k = 0; k < testSuits.length; k++) {//testSuits.length
//            /*step 2.1 读取算例数据，并获得对偶变量值*/
//            String pathName = String.format("src/main/resources/Chu's MKP Benchmarks/OR%dx%d/OR%dx%d-%.2f_%d.dat",
//                    (int) testSuits[k][0], (int) testSuits[k][1], (int) testSuits[k][0], (int) testSuits[k][1], testSuits[k][2], (int) testSuits[k][3]);
//            String fileName = String.format("OR%dx%d-%.2f_%d.dat", (int) testSuits[k][0], (int) testSuits[k][1], testSuits[k][2], (int) testSuits[k][3]);
//            /*step2.2算法测试，测试8组算法*/
//            for (String repairFun : repairFunArray) {
//                for (int test = 0; test <2; test++) {
//                    Knapsack problem = new Knapsack("Chu", pathName);
//                    Solution solution;
//                    if (algorithmName.equals("BFA")) {
//                        solution = run.testByBFA(problem, repairFun);
//                    } else {
//                        solution = run.testByBGA(problem, repairFun);
//                    }
//                    solution.setFileName(fileName);
//                    solution.setTestK(test);
//                    /*step2.3 保存算法求解结果*/
//                    solutionList.add(solution);
//                }
//            }
//            System.out.printf("已完成第%d个测试算例", k);
//        }
//
//        /*step 3 将结果写出到csv文件中，供进一步分析*/
//        String time = run.getDate();
//        String outputFile = "src/main/resources/result/" + time + algorithmName + ".csv";
//        WriteToCsv.exportCsv(solutionList, outputFile);
//    }


//    public static void main(String[] args) throws IloException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//
//        String algorithmName = "BFA"; /*BFA*/
//
//        /*step 1 设置算例和初始化*/
//        double[][] testSuits = {{5, 100, 0.25, 10}, {5, 250, 0.5, 10}, {5, 500, 0.75, 10},
//                {10, 100, 0.25, 10}, {10, 250, 0.5, 10}, {10, 500, 0.75, 10},
//                {30, 100, 0.25, 10}, {30, 250, 0.5, 10}, {30, 500, 0.75, 10}};
//        String[] repairFunArray = {"RO2", "repairDropAddByGroup", "RO1", "dropItemsByDual"};
////        String[] repairFunArray = {"repairDropAddByGroup"};
//        AlgorithmRunForPPT run = new AlgorithmRunForPPT();
//        List<Solution> solutionList = new ArrayList<>();
//
//        /*step 2 算法求解*/
//        for (int k = 0; k < testSuits.length; k++) {//testSuits.length
//
//            /*step 2.1 读取算例数据，并获得对偶变量值*/
//            String pathName = String.format("src/main/resources/Chu's MKP Benchmarks/OR%dx%d/OR%dx%d-%.2f_%d.dat",
//                    (int) testSuits[k][0], (int) testSuits[k][1], (int) testSuits[k][0], (int) testSuits[k][1], testSuits[k][2], (int) testSuits[k][3]);
//            String fileName = String.format("OR%dx%d-%.2f_%d.dat", (int) testSuits[k][0], (int) testSuits[k][1], testSuits[k][2], (int) testSuits[k][3]);
//            /*step2.2算法测试，测试8组算法*/
//            List<Integer> testList = new ArrayList<>();
//            for (int i = 0; i < 8; i++) {
//                testList.add(i);
//            }
//            for (String repairFun : repairFunArray) {
//                Random random = new Random();
//                testList.parallelStream().forEach(i -> {
//                    try {
//                        Knapsack problem = new Knapsack("Chu", pathName);
//                        Solution solution;
//                        solution = run.testByBFA(problem, repairFun);
//                        solution.setFileName(fileName);
//                        solution.setTestK(i);
//                        /*step2.3 保存算法求解结果*/
//                        solutionList.add(solution);
//                        /*等待一段时间*/
//                        int time = random.nextInt(100);
//                        System.out.println("等待时间为: " + time);
//                        Thread.sleep(time);
//
//                    } catch (InterruptedException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException | IloException e) {
//                        e.printStackTrace();
//                    }
//                });
//            }
//            System.out.printf("已完成第%d个测试算例", k);
//        }
//
//        /*step 3 将结果写出到csv文件中，供进一步分析*/
//        String time = run.getDate();
//        String outputFile = "src/main/resources/result/" + time + algorithmName + ".csv";
//        WriteToCsv.exportCsv(solutionList, outputFile);
//    }

/*迭代实验*/
    public static void main(String[] args) throws IloException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        /*step 1 设置算例和初始化*/
        double[][] testSuits = {{5, 100, 0.25, 10}, {5, 250, 0.5, 10}, {5, 500, 0.75, 10},
                {10, 100, 0.25, 10}, {10, 250, 0.5, 10}, {10, 500, 0.75, 10},
                {30, 100, 0.25, 10}, {30, 250, 0.5, 10}, {30, 500, 0.75, 10}};
        String[] repairFunArray = {"RO2", "repairDropAddByGroup", "RO1", "dropItemsByDual"};

        AlgorithmRunForPPT run = new AlgorithmRunForPPT();
        List<Solution> solutionList = new ArrayList<>();
        List<Integer> testList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            testList.add(i);
        }
        /*step 2 算法求解*/
        int k=7;
        /*step 2.1 读取算例数据，并获得对偶变量值*/
        String pathName = String.format("src/main/resources/Chu's MKP Benchmarks/OR%dx%d/OR%dx%d-%.2f_%d.dat",
                (int) testSuits[k][0], (int) testSuits[k][1], (int) testSuits[k][0], (int) testSuits[k][1], testSuits[k][2], (int) testSuits[k][3]);
        String fileName = String.format("OR%dx%d-%.2f_%d.dat", (int) testSuits[k][0], (int) testSuits[k][1], testSuits[k][2], (int) testSuits[k][3]);
        /*step2.2算法测试，测试8组算法*/
        Random random = new Random();
        testList.parallelStream().forEach(i -> {
            try {
                Knapsack problem = new Knapsack("Chu", pathName);
                Solution solution;
                if(i<4){
                    solution = run.testByBGA(problem, repairFunArray[i]);
                }else{
                    solution = run.testByBFA(problem, repairFunArray[i-4]);
                }
                solution.setFileName(fileName);
                /*step2.3 保存算法求解结果*/
                solutionList.add(solution);
                /*等待一段时间*/
                int time = random.nextInt(100);
                System.out.println("等待时间为: " + time);
                Thread.sleep(time);
            } catch (InterruptedException | NoSuchMethodException | IllegalAccessException | IOException | InvocationTargetException | IloException e) {
                e.printStackTrace();
            }
        });

        System.out.printf("已完成第%d个测试算例", k);
        /*step 3 将结果写出到csv文件中，供进一步分析*/
        String time = run.getDate();
        String outputFile = "src/main/resources/result/" + time + "_Iter.csv";
        WriteToCsv.exportPlotCsv(solutionList, outputFile);
    }
}


