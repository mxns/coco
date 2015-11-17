package quadratisch.util;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Pool<T> {
    private final int maxPoolSize;
    private final BlockingQueue<PoolItem<T>> queue;
    private final CopyOnWriteArrayList<PoolItem<T>> list;
    private ResourceFactory<T> factory;

    public Pool(ResourceFactory<T> resourceFactory, int poolSize) {
        maxPoolSize = poolSize;
        queue = new ArrayBlockingQueue<>(poolSize);
        list = new CopyOnWriteArrayList<>();
        factory = resourceFactory;
    }

    public Iterator<PoolItem<T>> iterator() {
        return list.iterator();
    }

    public PoolItem<T> acquire() throws InterruptedException {
        if (queue.isEmpty() && list.size() < maxPoolSize) {
            T resource = factory.newResource();
            PoolItem<T> poolItem = new PoolItem<T>(resource);
            list.add(poolItem);
            return poolItem;
        }
        return queue.take();
    }

    public boolean release(PoolItem<T> poolItem) {
        boolean success = false;
        try {
            boolean initialized = factory.reinitializeResource(poolItem.resource);
            if (initialized) {
                success = queue.offer(poolItem);
            }
        } finally {
            if (!success) {
                list.remove(poolItem);
                factory.destroyResource(poolItem.resource);
            }
        }
        return success;
    }

    public static class PoolItem<X> {
        public final X resource;

        private PoolItem(X resource) {
            this.resource = resource;
        }
    }

    public interface ResourceFactory<X> {

        X newResource();

        boolean reinitializeResource(X resource);

        void destroyResource(X resource);
    }

}