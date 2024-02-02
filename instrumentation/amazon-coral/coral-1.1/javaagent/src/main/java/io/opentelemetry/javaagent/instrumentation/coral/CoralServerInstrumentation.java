package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.Job;
import com.amazon.coral.service.ServiceConstant;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.instrumentation.coral.CoralSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class CoralServerInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.amazon.coral.service.HttpHandler");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("before"))
            .and(takesArgument(0, named("com.amazon.coral.service.Job"))),
        this.getClass().getName() + "$CoralReqBeforeAdvice");
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("after"))
            .and(takesArgument(0, named("com.amazon.coral.service.Job"))),
        this.getClass().getName() + "$CoralReqAfterAdvice");
  }

  @SuppressWarnings("unused")
  public static class CoralReqBeforeAdvice {

//    @Advice.OnMethodEnter(suppress = Throwable.class)
//    public static void methodEnter(
//        @Advice.Argument(0) Job job,
//        @Advice.Local("otelContext") Context context,
//        @Advice.Local("otelScope") Scope scope) {
//      Context parentContext = Java8BytecodeBridge.currentContext();
//      System.out.println("coral enter Before method start: " + parentContext.toString());
//      System.out.println("coral enter Before method job start: " + job.getRequest().getAttribute(
//          ServiceConstant.SERVICE_OPERATION_NAME));
////      if (!instrumenter().shouldStart(parentContext, job)) {
////        return;
////      }
////
////      context = instrumenter().start(parentContext, job);
////      scope = context.makeCurrent();
////      System.out.println("coral enter Before method end: " + context.toString());
//    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {

      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      System.out.println("coral exit Before method start:" + operationName);
      if (operationName == null) {
        return;
      }

      Context parentContext = Java8BytecodeBridge.currentContext();
//      System.out.println("coral exit Before method context start: " + parentContext.toString());
      if (!instrumenter().shouldStart(parentContext, job)) {
        return;
      }
      context = instrumenter().start(parentContext, job);
      scope = context.makeCurrent();
//      System.out.println("coral exit Before method end: " + context.toString());
    }
  }

  @SuppressWarnings("unused")
  public static class CoralReqAfterAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      System.out.println("coral enter After method start:" + operationName);
      if (operationName == null) {
        return;
      }
      Context parentContext = Java8BytecodeBridge.currentContext();
//      System.out.println("coral enter After method11: " + parentContext.toString());
      scope = parentContext.makeCurrent();
      if (scope == null) {
        System.out.println("coral enter After method end : scope is null");
        return;
      }
      Span span = Span.fromContext(parentContext);
      try {
        instrumenter().end(parentContext, job, job, job.getFailure());
      } catch (Throwable e) {
        System.out.println("End span in error: " + e.getMessage());
      }
      System.out.println("coral enter After method end");
    }
  }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      System.out.println("coral exit After method end: " + context.toString());
//      if (scope == null) {
//        System.out.println("coral exit After method end : scope is null");
//        return;
//      }
//      scope.close();
//
//      instrumenter().end(context, job, null, throwable);
//
//      System.out.println("coral exit After method end: " + context.toString());
    }
}
