package com.exavalu.customer.product.portal.service.queueservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class QueueServiceEvent {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private Map<Class<?>, Queue<Object>> queues = new ConcurrentHashMap<>();

    public <T> void addToQueue(QueueItemWrapper<T> item) {

    	Class<?> itemClass = item.getItem().getClass();
        queues.computeIfAbsent(itemClass, k -> new ConcurrentLinkedQueue<>()).add(item);
        eventPublisher.publishEvent(new QueueEvent(this, itemClass));
    }

    public <T> QueueItemWrapper<T> getFromQueue(Class<T> itemClass) {
        Queue<Object> queue = queues.get(itemClass);
        if (queue != null) {
            return (QueueItemWrapper<T>) queue.poll();
        }
        return null;
    }
}
