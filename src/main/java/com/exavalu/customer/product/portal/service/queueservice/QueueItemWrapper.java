package com.exavalu.customer.product.portal.service.queueservice;

public class QueueItemWrapper<T> {
    private T item;
    private String operation;

    public QueueItemWrapper(T item, String operation) {
        this.item = item;
        this.operation = operation;
    }

    public T getItem() {
        return item;
    }

    public String getOperation() {
        return operation;
    }
}
