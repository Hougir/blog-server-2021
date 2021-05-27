package com.blog.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * @author huang hao
 * @version 1.0
 * @date 2021/3/25 17:28
 */
@SuppressWarnings("all")
public class ForkjoinTest extends RecursiveTask<Long> {
    private Long start; // 1
    private Long end; // 1990900000
    // 临界值
    private Long temp = 10000L;
    public ForkjoinTest(Long start, Long end) {
        this.start = start;
        this.end = end;
    }
    @Override
    protected Long compute() {
        if ((end-start)<temp){
            Long sum = 0L;
            for (Long i = start; i <= end; i++) {
                sum += i;
            }
            return sum;
        }else { // forkjoin 递归
            long middle = (start + end) / 2; // 中间值
            ForkjoinTest task1 = new ForkjoinTest(start, middle);
            task1.fork(); // 拆分任务，把任务压入线程队列
            ForkjoinTest task2 = new ForkjoinTest(middle+1, end);
            task2.fork(); // 拆分任务，把任务压入线程队列
            return task1.join() + task2.join();
        }

    }

    static class Test{
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            test1();//8731
            test2();//5745
            test3();//267
        }
        // 普通程序员
        public static void test1(){
            Long sum = 0L;
            long start = System.nanoTime();
            for (Long i = 1L; i <= 10_0000_0000; i++) {
                sum += i;
            }
            long end = System.nanoTime();
            System.out.println("sum="+sum+" 时间："+(end-start)/1000000);
        }
        // 会使用ForkJoin
        public static void test2() throws ExecutionException, InterruptedException {
            long start = System.nanoTime();
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            ForkJoinTask<Long> task = new ForkjoinTest(0L, 10_0000_0000L);
            ForkJoinTask<Long> submit = forkJoinPool.submit(task);// 提交任务
            Long sum = submit.get();
            long end = System.nanoTime();
            System.out.println("sum="+sum+" 时间："+(end-start)/1000000);
        }
        public static void test3(){
            long start = System.currentTimeMillis();
            // Stream并行流 () (]
            long sum = LongStream.rangeClosed(0L,
                    10_0000_0000L).parallel().reduce(0, Long::sum);
            long end = System.currentTimeMillis();
            System.out.println("sum=" + sum+" 时间："+(end-start));
        }
    }
}
