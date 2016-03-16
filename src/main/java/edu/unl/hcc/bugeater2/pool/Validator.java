package edu.unl.hcc.bugeater2.pool;

/**
 * Created by chehe on 16/3/15.
 */

/**
 * Represents the functionality to
 * validate an object of the pool
 * and to subsequently perform cleanup activities.
 *
 * @author Swaranga
 *
 * @param < T > the type of objects to validate and cleanup.
 */
public interface Validator < T >
{
  /**
   * Checks whether the object is valid.
   *
   * @param t the object to check.
   *
   * @return <code>true</code>
   * if the object is valid else <code>false</code>.
   */
  public boolean isValid(T t);

  /**
   * Performs any cleanup activities
   * before discarding the object.
   * For example before discarding
   * database connection objects,
   * the pool will want to close the connections.
   * This is done via the
   * <code>invalidate()</code> method.
   *
   * @param t the object to cleanup
   */

  public void invalidate(T t);
}
