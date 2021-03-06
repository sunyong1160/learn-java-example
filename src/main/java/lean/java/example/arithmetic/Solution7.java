package lean.java.example.arithmetic;

/**
 * @Author sunyong
 * @Date 2018-12-28 13:21
 * 插入排序
 * 插入排序也包含两种操作，一种是元素的比较，一种是元素的移动。当我们需要将一个数据a插入到已排序区间时，需要拿a与已排序区间的元素依次比较大小，找到合适的插入位置。
 * 找到插入点之后，我们还需要将插入点之后的元素顺序往后移动一位，这样才能腾出位置给元素a插入
 * <p>
 * 选择排序算法的实现类似插入排序，也分已排区间和未排区间，但是选择排序每次会从未排区间中找到最小的元素，将其放到已排区间的末尾
 * 选择排序空间复杂度为O(1)，是一种原地排序算法。选择排序的最好情况时间复杂度，最坏情况和平均情况时间复杂度都为O(n2)
 * 选择排序是稳定的排序算法吗？
 * 不是稳定的排序算法，选择排序每次都要找剩余未排序元素中的最小值，并和前面的元素交换位置，这样破坏了稳定性。
 *
 * 比如 5，8，5，2，9 这样一组数据，使用选择排序算法来排序的话，第一次找到最小元素 2，与第一个 5 交换位置，那第一个 5 和中间的 5 顺序就变了，所以就不稳定了。
 * 正是因此，相对于冒泡排序和插入排序，选择排序就稍微逊色了。
 *
 * <p> O(n2) ==> n次方
 * <p>             是否原地排序          是否稳定            最好      最坏      平均
 * <p>
 * 冒泡排序         是                   是                   O(n)    O(n2)     O(n2)
 * <p>
 * 插入排序         是                   是                   O(n)    O(n2)     O(n2)
 * <p>
 * 选择排序         是                   否                   O(n2)   O(n2)     O(n2)
 **/
public class Solution7 {

    public static void insertSort(int[] a, int n) {

        if (n <= 1) {
            return;
        }
        // 外层循环次数
        for (int i = 1; i < n; i++) {
            // 记录从第二个开始位置开始的元素
            int value = a[i];
            // 记录从第二个开始位置的前一位置
            int j = i - 1;
            // 查找插入的位置（从第二个开始位置的前一位置向前减与记录好的位置元素的比较）
            for (; j >= 0; --j) {
                if (a[j] > value) {
                    a[j + 1] = a[j];// 数据移动
                } else {
                    break;
                }
            }
            a[j + 1] = value;//插入数据
        }
        for (int i : a) {
            System.out.print(i + " ");
        }
    }

    public static void main(String[] args) {
        int[] a = {4, 5, 6, 3, 2, 1};
        int n = a.length;
        insertSort(a, n);
    }
}
