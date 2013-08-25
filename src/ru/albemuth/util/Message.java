package ru.albemuth.util;

import java.util.*;

public class Message {

    private static final Collection<Item> EMPTY_ITEMS = new ArrayList<Item>(0);

    public enum Status {
        INFO(true),
        WARN(true),
        DATA_NEED(false),
        ERROR(false);

        private boolean passes;

        private Status(boolean passes) {
            this.passes = passes;
        }

        public boolean passes() {
            return passes;
        }

        public boolean isHigherThen(Status otherStatus) {
            return otherStatus == null || ordinal() > otherStatus.ordinal();
        }
    }

    private Status status = Status.INFO;
    private Map<Object, Item> items = new HashMap<Object, Item>();
    private Map<Object, ObjectItem> objectItems = new HashMap<Object, ObjectItem>();

    public Message() {}

    public Message(Object key, Status status, String message) {
        addItem(key, status, message);
    }

    public Status getStatus() {
        return status;
    }

    public Status getStatus(Object objectKey) {
        Status ret = Status.INFO;
        ObjectItem oi = objectItems.get(objectKey);
        if (oi != null) {
            ret = oi.getStatus();
        }
        return ret;
    }

    public Status getStatus(Object objectKey, Object propertyKey) {
        Status ret = Status.INFO;
        Item item = getItem(objectKey, propertyKey);
        if (item != null) {
            ret = item.getStatus();
        }
        return ret;
    }

    public void addItem(Object key, Status status, String message) {
        addItem(key, key, status, message);
    }

    public Item removeItem(Object key) {
        return removeItem(key, key);
    }

    public void addItem(Object objectKey, Object propertyKey, Status status, String message) {
        if (status.isHigherThen(this.status)) {
            this.status = status;
        }
        ObjectItem objectItem = objectItems.get(objectKey);
        if (objectItem == null) {
            objectItem = new ObjectItem();
            objectItems.put(objectKey, objectItem);
        }
        objectItem.addItem(propertyKey, status, message);
    }

    public Item removeItem(Object objectKey, Object propertyKey) {
        ObjectItem objectItem = objectItems.get(objectKey);
        if (objectItem != null) {
            Item item = objectItem.removeItem(propertyKey);
            if (item != null && item.getStatus().equals(status)) {
                this.status = calculateStatus(Status.INFO, items.values());
                for (ObjectItem oi: objectItems.values()) {
                    this.status = calculateStatus(this.status, oi.items.values());
                }
            }
            if (objectItem.isEmpty()) {
                objectItems.remove(objectKey);
            }
            return item;
        } else {
            return null;
        }
    }

    public Item getItem(Object key) {
        return getItem(key, key);
    }

    public Item getItem(Object objectKey, Object propertyKey) {
        ObjectItem objectItem = objectItems.get(objectKey);
        if (objectItem != null) {
            return objectItem.getItem(propertyKey);
        } else {
            return null;
        }
    }

    public Collection<Item> getItems(Object objectKey) {
        ObjectItem objectItem = objectItems.get(objectKey);
        if (objectItem != null) {
            return objectItem.items.values();
        } else {
            return EMPTY_ITEMS;
        }
    }

    public Set getKeys() {
        return items.keySet();
    }

    public void clear() {
        this.status = Status.INFO;
        items.clear();
        objectItems.clear();
    }

    private Status calculateStatus(Status status, Collection<Item> items) {
        for (Item item: items) {
            if (item.getStatus().isHigherThen(status)) {
                status = item.getStatus();
            }
        }
        return status;
    }

    public class Item {

        private Object key;
        private Status status;
        private String message;
        private Object data;

        protected Item(Object key, Status status, String message, Object data) {
            this.key = key;
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public Object getKey() {
            return key;
        }

        public Status getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }

    }

    class ObjectItem {

        private Map<Object, Item> items = new HashMap<Object, Item>();
        private Status status;

        public void addItem(Object propertyKey, Status status, String message) {
            if (status.isHigherThen(this.status)) {
                this.status = status;
            }
            this.items.put(propertyKey, new Item(propertyKey, status, message, null));
        }

        public Item removeItem(Object propertyKey) {
            Item item = items.remove(propertyKey);
            if (item != null && item.getStatus().equals(this.status)) {
                this.status = calculateStatus(Status.INFO, items.values());
            }
            return item;
        }

        public Item getItem(Object propertyKey) {
            return items.get(propertyKey);
        }

        public boolean isEmpty() {
            return items.size() == 0;
        }

        public Status getStatus() {
            return status;
        }

    }

}
