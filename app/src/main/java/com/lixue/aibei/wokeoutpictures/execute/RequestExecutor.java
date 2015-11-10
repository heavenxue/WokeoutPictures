package com.lixue.aibei.wokeoutpictures.execute;

import java.util.concurrent.Executor;

/**
 * 请求执行器
 * Executor允许你管理异步任务的执行，而无须显式地管理线程的生命周期。
 * 执行已提交的 Runnable 任务对象。此接口提供一种将任务提交与每个任务将如何运行的机制（包括线程使用的细节、调度等）分离开来的方法。
 * 例如，可能会使用以下方法，而不是为一组任务中的每个任务调用 new Thread(new(RunnableTask())).start()
 * Executor executor = anExecutor;
 * executor.execute(new RunnableTask1());
 * executor.execute(new RunnableTask2());
 * ...
 * **Executor 接口并没有严格地要求执行是异步的
 * Created by Administrator on 2015/11/10.
 */
public interface RequestExecutor {
    //请求执行分发器
    Executor getRequestDispatchExecutor();
    //获取本地任务执行器
    Executor getLocalRequestExecutor();
    //获取网络任务执行器
    Executor getNetRequestExecutor();
    //获取标识符
    String getIdentifier();
    //追加标识符
    StringBuilder appendIdentifier(StringBuilder builder);
}
