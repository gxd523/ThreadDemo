package com.gxd.demo.thread

import kotlin.concurrent.thread

fun main() {
    val blockingQueue = BlockingQueue<Int>()
    thread(name = "Producer-1") {
        repeat(100) {
            Thread.sleep(0)// 0秒也会耗时一下，会影响被notify的拿到锁，还是下一个循环
            blockingQueue.put(it)
        }
    }

    thread(name = "Consumer-1") {
        repeat(100) {
            Thread.sleep(0)// 0秒也会耗时一下，会影响被notify的拿到锁，还是下一个循环
            blockingQueue.take()
        }
    }
}

class BlockingQueue<T> {
    private val list by lazy {
        emptyList<T>().toMutableList()
    }
    private val lock by lazy {
        Object()
    }

    fun put(id: T) = synchronized(lock) {
        while (list.size == 10) {// 注意这里不能改成if
            lock.wait()
        }
        list.add(id)
        Thread.sleep((0..2).random() * 100L)
        println("${Thread.currentThread().name} 生产了商品 $id,\t仓库还剩 ${list.size}个商品")
        lock.notifyAll()
    }

    @Synchronized
    fun take() = synchronized(lock) {
        while (list.size == 0) {
            lock.wait()
        }
        val id = list.removeAt(0)
        Thread.sleep((3..5).random() * 100L)
        println("${Thread.currentThread().name} 消费了商品 $id,\t仓库还剩 ${list.size}个商品")
        lock.notifyAll()
    }
}