package algorithm;

import java.util.Random;

public class QuickSortThreeWays {

    private QuickSortThreeWays() {

    }


    /**
     * 双路快速排序法
     *
     * @param arr 待排序数组
     * @param <E>
     */
    public static <E extends Comparable<E>> void sortThreeWays(E[] arr, int[] indexArray, boolean ascending) {
        Random random = new Random();
        if(ascending){
            sortThreeWayAscending(arr, 0, arr.length - 1, indexArray, random);
        }else{
            sortThreeWaysDescending(arr, 0, arr.length - 1, indexArray, random);
        }
    }

    /**
     * 私有排序方法
     * @param arr 待排序数组
     * @param l   数组左边界
     * @param r   数组右边界
     * @param <E>
     */
    private static <E extends Comparable<E>> void sortThreeWayAscending(E[] arr, int l, int r, int[] indexArray, Random random) {
        if (l >= r) {
            return;
        }

        // 生成 [l, r] 之间的随机索引
        int p = l + random.nextInt(r - l + 1);
        swap(arr, l, p, indexArray);

        // arr[l + 1, lt] < v, arr[lt + 1, i - 1] == v, arr[gt, r] > v
        int lt = l;
        int i = l + 1;
        int gt = r + 1;
        while (i < gt) {
            if (arr[i].compareTo(arr[l]) < 0) {
                lt++;
                swap(arr, i, lt, indexArray);
                i++;
            } else if (arr[i].compareTo(arr[l]) > 0) {
                gt--;
                swap(arr, i, gt, indexArray);
            } else {
                // arr[i] == arr[l]
                i++;
            }
        }
        swap(arr, l, lt, indexArray);
        // 此时 arr[l, lt] < v, arr[lt, gt - 1] == v, arr[gt, r] > v
        sortThreeWayAscending(arr, l, lt - 1, indexArray, random);
        sortThreeWayAscending(arr, gt, r, indexArray, random);
    }


    /**
     * 私有排序方法
     * @param arr 待排序数组
     * @param l   数组左边界
     * @param r   数组右边界
     * @param <E>
     */
    private static <E extends Comparable<E>> void sortThreeWaysDescending(E[] arr, int l, int r, int[] indexArray, Random random) {
        if (l >= r) {
            return;
        }

        // 生成 [l, r] 之间的随机索引
        int p = l + random.nextInt(r - l + 1);
        swap(arr, l, p, indexArray);

        // arr[l + 1, lt] < v, arr[lt + 1, i - 1] == v, arr[gt, r] > v
        int lt = l;
        int i = l + 1;
        int gt = r + 1;
        while (i < gt) {
            if (arr[i].compareTo(arr[l]) > 0) {
                lt++;
                swap(arr, i, lt, indexArray);
                i++;
            } else if (arr[i].compareTo(arr[l]) < 0) {
                gt--;
                swap(arr, i, gt, indexArray);
            } else {
                // arr[i] == arr[l]
                i++;
            }
        }
        swap(arr, l, lt, indexArray);
        // 此时 arr[l, lt] < v, arr[lt, gt - 1] == v, arr[gt, r] > v
        sortThreeWaysDescending(arr, l, lt - 1, indexArray, random);
        sortThreeWaysDescending(arr, gt, r, indexArray, random);
    }

    /**
     * 私有交换方法
     *
     * @param arr 待交换元素所在数组
     * @param i   元素i
     * @param j   元素j
     * @param <E>
     */
    private static <E> void swap(E[] arr, int i, int j, int[] indexArray) {
        E t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;

        int temp = indexArray[i];
        indexArray[i] = indexArray[j];
        indexArray[j] = temp;

    }







    public static void main(String[] args) {
        int k = 20;
        Integer[] nums = new Integer[k];
        Random random = new Random();
        for(int i=0; i<k; i++){
            nums[i] = random.nextInt(100);
        }
        int[] indexArray = new int[nums.length];
        for(int i=0; i<nums.length; i++){
            indexArray[i] = i+1;
        }

        System.out.println("排序前");
        for (Integer num : nums) {
            System.out.print(num+",");
        }

        sortThreeWays(nums, indexArray, false);
        System.out.println("\n排序后的下标值");
        for (int j : indexArray) {
            System.out.print(j+",");
        }
        System.out.println("\n排序后的元素值");
        for (Integer num : nums) {
            System.out.print(num+",");
        }
    }
}
