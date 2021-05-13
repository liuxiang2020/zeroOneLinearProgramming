package tasks;

import algorithm.QuickSortThreeWays;
import utils.ExcelData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Knapsack {

    public int dimension;
    public int nConstraint;
    public int[] capacity;
    public int[][] weights;
    public int[] prices;
    public int[] priceWeightLineNumbers;
    public double[] unitWeightOfPrices;
    public double[] unitPriceOfWeight;
    // drop操作使用
    public double[][] localUnitPriceOfWeight;
    public int[][] localDropSeq;
    // add操作使用
    public double[][] localUnitWeightOfPrice;

    public double[] dualUtility;

    public int optimalValue;
    public String status = "unKnow";
    public String fileName;
    public String fileType;
    public int cplexObjective;
    public double cplexGap;
    public String cplexStatus;
    public double[] dualVar;

    public void setCplexObjective(int value){
        this.cplexObjective = value;
    }

    public void setCplexGap(double value){
        this.cplexGap = value;
    }

    public void setCplexStatus(String status){
        this.cplexStatus = status;
    }

    /*设置对偶变量值*/
    public void setDualVar(double[] duals){
        dualVar = new double[nConstraint];
        if (nConstraint >= 0) System.arraycopy(duals, 0, dualVar, 0, nConstraint);
    }

    public Knapsack(String filetype, String fileName){
        this.fileType = filetype;
        this.fileName = fileName;
        switch (filetype) {
            case "Chu":
                readFromChu(fileName);
                break;
            case "GK":
                readFromGKOrWeing(fileName);
                break;
            case "Weing":
                readFromGKOrWeing(fileName);
                break;
            default:
                System.out.println("类型输入错误");
                break;
        }
    }

    public void readFromChu(String fileName){
        try {
            Scanner fileIn = new Scanner(new File(fileName));
            dimension = fileIn.nextInt();
            nConstraint = fileIn.nextInt();
            fileIn.nextInt();
            prices = new int[dimension];
            unitWeightOfPrices = new double[dimension];
            unitPriceOfWeight = new double[dimension];
            weights = new int[nConstraint][dimension];
            capacity = new int[nConstraint];
            priceWeightLineNumbers = new int[dimension];
            for (int i = 0; i < dimension; i++)
                priceWeightLineNumbers[i] = i;

            for (int i = 0; i < dimension; i++) {
                prices[i] = fileIn.nextInt();
            }

            for(int j=0; j<nConstraint; j++){
                for (int i = 0; i < dimension; i++) {
                    weights[j][i] = fileIn.nextInt();
                }
            }

            for (int i = 0; i < dimension; i++){
                int weightSum = 0;
                for(int j=0; j<nConstraint; j++){
                    weightSum += weights[j][i];
                }
                unitWeightOfPrices[i] = prices[i] * 1.0 / weightSum;
                unitPriceOfWeight[i] = 1.0/unitWeightOfPrices[i];
            }

            for(int i=0; i<nConstraint; i++)
                capacity[i] = fileIn.nextInt();
            //计算价值密度
            vdCompute();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if(status.equals("unKnow")){
            ExcelData excelData = new ExcelData("src/main/resources/Chu's MKP Benchmarks/Best Known Values.xlsx", "Sheet1");
            String[] strings = this.fileName.split("/");
            String caseName = strings[strings.length-1];
            caseName = caseName.replaceFirst(".dat", "");
            char c = caseName.charAt(caseName.length() - 2);
            if(c=='_'){
                char index = caseName.charAt(caseName.length() - 1);
                caseName = caseName.replaceFirst("_"+index, "_0"+index);
            }
            optimalValue = (int)Double.parseDouble(excelData.getCellByCaseName(caseName, 0, 1));
            status = excelData.getCellByCaseName(caseName, 0, 2);
        }
    }

    public void readFromGKOrWeing(String filename){
        try {
            Scanner fileIn = new Scanner(new File(fileName));
            dimension = fileIn.nextInt();
            nConstraint = fileIn.nextInt();
            optimalValue = fileIn.nextInt();
            if (optimalValue >100){
                status = "Optimal";
            }else{
                status = "UnKnow";
            }
            prices = new int[dimension];
            unitWeightOfPrices = new double[dimension];
            unitPriceOfWeight = new double[dimension];
            weights = new int[nConstraint][dimension];
            capacity = new int[nConstraint];
            priceWeightLineNumbers = new int[dimension];
            for (int i = 0; i < dimension; i++)
                priceWeightLineNumbers[i] = i;

            for (int i = 0; i < dimension; i++) {
                prices[i] = fileIn.nextInt();
            }

            for(int j=0; j<nConstraint; j++){
                for (int i = 0; i < dimension; i++) {
                    weights[j][i] = fileIn.nextInt();
                }
            }

            for (int i = 0; i < dimension; i++){
                int weightSum = 0;
                for(int j=0; j<nConstraint; j++){
                    weightSum += weights[j][i];
                }
                unitWeightOfPrices[i] = weightSum*1.0/prices[i];
                unitPriceOfWeight[i] = 1.0/unitWeightOfPrices[i];
            }

            for(int i=0; i<nConstraint; i++)
                capacity[i] = fileIn.nextInt();

            // 计算价值密度
            vdCompute();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readFromSpot5(String filename){

    }

    public void vdCompute(){
        localUnitPriceOfWeight = new double[nConstraint][dimension];
        localUnitWeightOfPrice = new double[nConstraint][dimension];
        for(int i=0; i<nConstraint; i++){
            for(int j=0; j < dimension; j++){
                localUnitPriceOfWeight[i][j] = weights[i][j]*1.0/prices[j];
                if(localUnitPriceOfWeight[i][j]==0){
                    localUnitWeightOfPrice[i][j] = 1000;
                }else{
                    localUnitWeightOfPrice[i][j] = 1.0 / localUnitPriceOfWeight[i][j];
                }
            }
        }
    }

    /**
     * 提前对变量和约束排序
     * 排序指标：price, weight， weight/price
     */
    public void itemPreSortForRo2(){
        localDropSeq = new int[nConstraint][dimension];
        for(int i=0; i<nConstraint; i++){
            for(int j=0; j < dimension; j++){
                localDropSeq[i][j] = j;
            }
        }
        for(int i=0; i<nConstraint; i++){
            Double[] temp = new Double[dimension];
            for(int j=0; j<dimension; j++)
                temp[j] = localUnitPriceOfWeight[i][j];
            QuickSortThreeWays.sortThreeWays(temp, localDropSeq[i], false);
            System.out.println(temp);
        }
    }

    public void itemPreSortForDAOR(){
        int[] seq = new int[dimension];
        Double[] temp = new Double[dimension];
        for(int i=0; i<dimension; i++){
            seq[i] = i;
            temp[i] = unitWeightOfPrices[i];
        }
        QuickSortThreeWays.sortThreeWays(temp, seq, false);
        updateVarIndex(seq);
    }

    /*按照对偶变量值排序*/
    public void iterPreSortForDual(double[] dualVar){

        localDropSeq = new int[nConstraint][dimension];
        for(int i=0; i<nConstraint; i++){
            for(int j=0; j < dimension; j++){
                localDropSeq[i][j] = j;
            }
        }

        int[] seq = new int[dimension];
        Double[] temp = new Double[dimension];
        for(int j=0; j<dimension; j++){
            /*求伪效用*/
            int sum = 0;
            for(int i=0; i<nConstraint; i++){
                sum += weights[i][j]*dualVar[i];
            }
            temp[j] = prices[j]*1.0/sum;
            seq[j] = j;
        }
        QuickSortThreeWays.sortThreeWays(temp, seq, true);
        /*更新所有变量的下标*/
        updateVarIndex(seq);

        /*设置对偶伪效用*/
        dualUtility = new double[dimension];
        for(int i=0; i<dimension; i++){
            dualUtility[i] = temp[i];
        }
    }

    private void updateVarIndex(int[] seq){

        int[] pricesCopy = prices.clone();
        double[] unitWeightOfPricesCopy = unitWeightOfPrices.clone();
        double[] unitPriceOfWeightCopy = unitPriceOfWeight.clone();
        // 更新所有变量
        int[][] weightsCopy = new int[nConstraint][dimension];
        // drop操作使用
        double[][] localUnitPriceOfWeightCopy = new double[nConstraint][dimension];
        int[][] localDropSeqCopy = new int[nConstraint][dimension];
        // add操作使用
        double[][] localUnitWeightOfPriceCopy = new double[nConstraint][dimension];

        for(int i=0; i<nConstraint; i++){
            weightsCopy[i] = weights[i].clone();
            localUnitPriceOfWeightCopy[i] = localUnitPriceOfWeight[i].clone();
            localDropSeqCopy[i] = localDropSeq[i].clone();
            localUnitWeightOfPriceCopy[i] = localUnitWeightOfPrice[i].clone();
        }

        for(int j=0; j<dimension; j++){
            int index = seq[j];
            prices[j] = pricesCopy[index];
            unitWeightOfPrices[j] = unitWeightOfPricesCopy[index];
            unitPriceOfWeight[j] = unitPriceOfWeightCopy[index];
            for(int i=0; i<nConstraint; i++){
                weights[i][j] = weightsCopy[i][index];
                localUnitPriceOfWeight[i][j] = localUnitPriceOfWeightCopy[i][index];
                localDropSeq[i][j] = localDropSeqCopy[i][index];
                localUnitWeightOfPrice[i][j] = localUnitWeightOfPriceCopy[i][index];
            }
        }
    }
}
