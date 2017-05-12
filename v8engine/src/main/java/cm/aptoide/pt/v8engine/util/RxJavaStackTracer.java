package cm.aptoide.pt.v8engine.util;

import cm.aptoide.pt.utils.BaseException;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.plugins.RxJavaObservableExecutionHook;

/**
 * Created by sithengineer on 25/09/16.
 *
 * https://github.com/ReactiveX/RxJava/issues/3521
 *
 * https://github.com/Cookizz/RxJavaStackTracer/blob/master/app/src/main/java/stacktracer/rxjava/cookizz/com/rxjavastacktracer/RxJavaStackTracer.java
 */

public class RxJavaStackTracer extends RxJavaObservableExecutionHook {

  @Override
  public <T> Observable.OnSubscribe<T> onSubscribeStart(Observable<? extends T> observableInstance,
      final Observable.OnSubscribe<T> onSubscribe) {
    // resembles Observable.lift( )
    return new Observable.OnSubscribe<T>() {
      @Override public void call(Subscriber<? super T> o) {
        try {
          Observable.Operator<T, T> operator = OperatorTraceOnError.traceOnError();
          Subscriber<? super T> st = RxJavaStackTracer.this.onLift(operator)
              .call(o);
          try {
            st.onStart();
            onSubscribe.call(st);
          } catch (Throwable e) {
            Exceptions.throwIfFatal(e);
            st.onError(e);
          }
        } catch (Throwable e) {
          Exceptions.throwIfFatal(e);
          o.onError(e);
        }
      }
    };
  }

  static class OperatorTraceOnError<T> implements Observable.Operator<T, T> {

    private final StackTraceElement[] trace = new Throwable().getStackTrace();

    public static <T> OperatorTraceOnError<T> traceOnError() {
      return new OperatorTraceOnError<>();
    }

    @Override public Subscriber<? super T> call(final Subscriber<? super T> child) {
      Subscriber<T> parent = new Subscriber<T>() {
        @Override public void onCompleted() {
          child.onCompleted();
        }

        @Override public void onError(Throwable throwable) {
          if (BaseException.class.isAssignableFrom(throwable.getClass())) {
            // it's a known error. don't handle it here
            child.onError(throwable);
          } else {
            child.onError(new TracedException(throwable, trace));
          }
        }

        @Override public void onNext(T t) {
          child.onNext(t);
        }
      };
      child.add(parent);
      return parent;
    }

    private static class TracedException extends RuntimeException {
      public TracedException(Throwable throwable, StackTraceElement[] trace) {
        super(throwable);
        setStackTrace(trace);
      }
    }
  }
}
