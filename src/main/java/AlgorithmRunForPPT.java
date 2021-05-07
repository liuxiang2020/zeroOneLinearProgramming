import algorithm.*;
import ilog.concert.IloException;

import tasks.Knapsack;
import utils.WriteToCsv;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class AlgorithmRunForPPT {

    public Solution testByBFA(Knapsack problem, String repairFUnName) throws NoSuchMethodException, IllegalAccessException, IOException, InvocationTargetException, IloException {

        /*问题预处理*/
        preProcess(problem, repairFUnName);

        /*算法初始化*/
        BFA algorithm = new BFA(problem);
        /*设置求解参数*/
        if(problem.dimension>250){
            algorithm.setParam(0.02,100,0,50);
        }else{
            algorithm.setParam(0.05,100,0,50);
        }
        algorithm.setMaxIter(20);

        /*设置修复算子*/
        algorithm.setRepairOperation(repairFUnName);
        /*求解*/
        algorithm.bfaSolve();
        /*输出结果，并检查结果是否正确*/
        Solution solution = new Solution(problem.fileName, problem.optimalValue, problem.status, "BFA", repairFUnName, algorithm);
        if(solution.feasibleCheck(problem, algorithm.currentBestSolution)){
            System.out.printf("解可行, 求解函数为%s, 修复算子为%s, 算法最优解为%d, 文献最优解为%d, 运算时间为%ds，迭代次数为%d\n\n",
                    "BFA", repairFUnName, algorithm.metaOptimalValue, problem.optimalValue, algorithm.runTime, solution.runIter);
        }

        return solution;
    }
    /*预处理*/
    public void preProcess(Knapsack problem, String repairFUnName)throws IloException{
        if(repairFUnName.equals("dropItemsByDual") | repairFUnName.equals("RO1")){
            /*对偶排序*/
            CplexSolveForDual cplexSolveDual = new CplexSolveForDual(problem);
            cplexSolveDual.solveModel();
            problem.setDualVar(cplexSolveDual.getPrices());
            problem.iterPreSortForDUal(problem.dualVar);
        }else if(repairFUnName.equals("RO2")){
            problem.itemPreSortForRo2();
        }
    }

    /*调用算法主程序*/


    public static void main(String[] args) throws IloException, IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        /*step 1 设置算例和初始化*/
        double[][] testSuits = {{5, 100, 0.25, 10}, {5, 250, 0.5, 10}, {5, 500, 0.75, 10},
                                {10, 100, 0.25, 10}, {10, 250, 0.5, 10}, {10, 500, 0.75, 10},
                                {30, 100, 0.25, 10}, {30, 250, 0.5, 10}, {30, 500, 0.75, 10}};
        String[] repairFunArray = {"repairDropAddByGroup", "RO2", "RO1", "dropItemsByDual"};
        AlgorithmRunForPPT run = new AlgorithmRunForPPT();
        List<Solution> solutionList = new ArrayList<>();

        /*step 2 算法求解*/
        for(int k=0; k<testSuits.length; k++){//testSuits.length

            /*step 2.1 读取算例数据，并获得对偶变量值*/
            String pathName = String.format("src/main/resources/Chu's MKP Benchmarks/OR%dx%d/OR%dx%d-%.2f_%d.dat",
                    (int)testSuits[k][0], (int)testSuits[k][1], (int)testSuits[k][0], (int)testSuits[k][1], testSuits[k][2], (int)testSuits[k][3]);
            String  fileName = String.format("OR%dx%d-%.2f_%d.dat",  (int)testSuits[k][0], (int)testSuits[k][1], testSuits[k][2], (int)testSuits[k][3]);
            /*step2.2算法测试，测试8组算法*/
            for(String repairFun: repairFunArray){
                Knapsack problem = new Knapsack("Chu", pathName);
                Solution solution = run.testByBFA(problem, repairFun);
                solution.setFileName(fileName);
                /*step2.3 保存算法求解结果*/
                solutionList.add(solution);
            }
            System.out.printf("已完成第%d个测试算例", k);
        }

        /*step 3 将结果写出到csv文件中，供进一步分析*/
        String outputFile = "src/main/resources/result/MetaHeuristicResult.csv";
        WriteToCsv.exportCsv(solutionList, outputFile);
    }

}


