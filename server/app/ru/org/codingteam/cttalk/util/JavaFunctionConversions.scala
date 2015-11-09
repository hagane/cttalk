package ru.org.codingteam.cttalk.util

import java.util.function.{Function => JFunction}

/**
 * Created by hgn on 25.10.2015.
 */
object JavaFunctionConversions {
  implicit def asJavaFunction[T,R](f: (T) => R):JFunction[T,R] = new JFunction[T,R] {
    override def apply(t: T): R = f(t)
  }
}
