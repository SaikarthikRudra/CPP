package com.exavalu.customer.product.portal.service.queueservice;
import org.springframework.context.ApplicationEvent;

public class QueueEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    private final Class<?> itemClass;

    public QueueEvent(Object source, Class<?> itemClass) {
        super(source);
        this.itemClass = itemClass;
    }

    public Class<?> getItemClass() {
        return itemClass;
    }
}

