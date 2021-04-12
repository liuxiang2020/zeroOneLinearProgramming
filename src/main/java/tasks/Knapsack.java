package tasks;

import utils.ExcelData;

import java.io.File;
import java.io.FileNotFoundException;
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

    public int optimalValue;
    public String status = "unKnow";
    public String fileName;
    public String fileType;
    public int cplexObjective;
    public double cplexGap;

    public void setCplexObjective(int value){
        this.cplexObjective = value;
    }

    public void setCplexGap(double value){
        this.cplexGap = value;
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void readFromSpot5(String filename){


    }
}
