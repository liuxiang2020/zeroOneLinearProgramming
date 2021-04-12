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
    public static <E extends Comparable<E>> void sortThreeWays(E[] arr, int[] indexArray) {
        Random random = new Random();
        sortThreeWays(arr, 0, arr.length - 1, indexArray, random);
    }

    /**
     * 私有排序方法
     *
     * @param arr 待排序数组
     * @param l   数组左边界
     * @param r   数组右边界
     * @param <E>
     */
    private static <E extends Comparable<E>> void sortThreeWays(E[] arr, int l, int r, int[] indexArray, Random random) {
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

        sortThreeWays(arr, l, lt - 1, indexArray, random);
        sortThreeWays(arr, gt, r, indexArray, random);
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

        Integer[] nums = new Integer[20];
        Random random = new Random();
        for(int i=0; i<20; i++){
            nums[i] = random.nextInt(100);
        }
        int[] indexArray = new int[nums.length];
        for(int i=0; i<nums.length; i++){
            indexArray[i] = i+1;
        }
        sortThreeWays(nums, indexArray);
        for (int j : indexArray) {
            System.out.print(j+",");
        }
        System.out.println("");
        for (Integer num : nums) {
            System.out.print(num+",");
        }
    }
}
