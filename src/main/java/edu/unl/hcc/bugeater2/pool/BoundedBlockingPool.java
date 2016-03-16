package edu.unl.hcc.bugeater2.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
public final class BoundedBlockingPool extends AbstractPool implements BlockingPool
{
private int size;
private BlockingQueue  objects;
private Validator  validator;
private ObjectFactory  objectFactory;
private ExecutorService executor =
    Executors.newCachedThreadPool();
private volatile boolean shutdownCalled;

public BoundedBlockingPool(
    int size,
    Validator  validator,
    ObjectFactory  objectFactory)
    {
    super();
    this.objectFactory = objectFactory;
    this.size = size;
    this.validator = validator;
    objects = new LinkedBlockingQueue (size);
    initializeObjects();
    shutdownCalled = false;
    }

public T get(long timeOut, TimeUnit unit)
    {
    if(!shutdownCalled)
    {
    T t = null;
    try
    {
    t = objects.poll(timeOut, unit);
    return t;
    }
    catch(InterruptedException ie)
    {
    Thread.currentThread().interrupt();
    }
    return t;
    }
    throw new IllegalStateException(
    'Object pool is already shutdown');
    }

public T get()
    {
    if(!shutdownCalled)
    {
    T t = null;
    try
    {
    t = objects.take();
    }

    catch(InterruptedException ie)
    {
    Thread.currentThread().interrupt();
    }
    return t;
    }

    throw new IllegalStateException(
    'Object pool is already shutdown');
    }

public void shutdown()
    {
    shutdownCalled = true;
    executor.shutdownNow();
    clearResources();
    }

private void clearResources()
    {
    for(T t : objects)
    {
    validator.invalidate(t);
    }
    }

@Override
protected void returnToPool(T t)
    {
    if(validator.isValid(t))
    {
    executor.submit(new ObjectReturner(objects, t));
    }
    }

@Override
protected void handleInvalidReturn(T t)
    {
    }

@Override
protected boolean isValid(T t)
    {
    return validator.isValid(t);
    }

private void initializeObjects()
    {
    for(int i = 0; i < size; i++)
    {
    objects.add(objectFactory.createNew());
    }
    }

    @Override
    protected void handleInvalidReturn(Object o) {

    }

    @Override
    protected void returnToPool(Object o) {

    }

    @Override
    protected boolean isValid(Object o) {
        return false;
    }

    private class ObjectReturner
    implements <Callable>
{
private BlockingQueue  queue;
private E e;
public ObjectReturner(BlockingQueue  queue, E e)
    {
    this.queue = queue;
    this.e = e;
    }

public Void call()
    {
    while(true)
    {
    try
    {
    queue.put(e);
    break;
    }
    catch(InterruptedException ie)
    {
    Thread.currentThread().interrupt();
    }
    }
    return null;
    }

    }

    }