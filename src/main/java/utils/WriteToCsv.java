package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.List;

public class WriteToCsv {

    public static Object exportCsv;

    public static <T> String exportCsv(String fileName,
                                       String[] titles, String[] propertys, List<T> list) throws IOException, IllegalArgumentException, IllegalAccessException{
        File file = new File(fileName);
        //构建输出流，同时指定编码
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");

        //csv文件是逗号分隔，除第一个外，每次写入一个单元格数据后需要输入逗号
        for(String title : titles){
            ow.write(title);
            ow.write(",");
        }
        //写完文件头后换行
        ow.write("\r\n");
        //写内容
        for(Object obj : list){
            //利用反射获取所有字段
            Field[] fields = obj.getClass().getDeclaredFields();
            for(String property : propertys){
                for(Field field : fields){
                    //设置字段可见性
                    field.setAccessible(true);
                    if(property.equals(field.getName())){
                        ow.write(field.get(obj).toString());
                        ow.write(",");
                        continue;
                    }
                }
            }
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
        return "0";
    }

    public static void exportCsv(String fileName, String[] fileNameArray, int[] objectiveArray, int[] optimalArray,
                                 String[] statusArray, double[] gapArray, int[] usedTimeArray, String[] cplexStatus) throws IOException, IllegalArgumentException, IllegalAccessException{
        File file = new File(fileName);
        //构建输出流，同时指定编码
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");

        //csv文件是逗号分隔，除第一个外，每次写入一个单元格数据后需要输入逗号
        String[] titles = {"文件名称", "cplex求解结果", "文献最优解", "文献最优解状态", "cplexGap", "cplex耗时", "cplex状态"};
        for(String title : titles){
            ow.write(title);
            ow.write(",");
        }
        //写完文件头后换行
        ow.write("\r\n");
        //写内容
        for(int i=0; i<objectiveArray.length; i++){
            if (fileNameArray[i]==null){
                break;
            }
            String line = fileNameArray[i]+"," + objectiveArray[i]  + "," + optimalArray[i] + "," + statusArray[i] + "," +
                    gapArray[i] + "," + usedTimeArray[i]+ "," + cplexStatus[i]+ ",";
            ow.write(line);
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
    }

    public static void exportCsv(String fileName,  String title, int[] array) throws IOException, IllegalArgumentException, IllegalAccessException{
        File file = new File(fileName);
        //构建输出流，同时指定编码
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file), "gbk");
        ow.write(title);
        ow.write(",");
        //写完文件头后换行
        ow.write("\r\n");
        //写内容
        for (int j : array) {
            ow.write(j+",");
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
    }
}