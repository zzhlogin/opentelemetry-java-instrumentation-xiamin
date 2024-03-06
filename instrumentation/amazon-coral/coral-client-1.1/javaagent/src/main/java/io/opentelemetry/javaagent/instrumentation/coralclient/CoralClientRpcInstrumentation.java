package io.opentelemetry.javaagent.instrumentation.coralclient;

import com.amazon.coral.service.Constant;
import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.Reply;
import com.amazon.coral.service.Request;
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

import static io.opentelemetry.javaagent.instrumentation.coralclient.CoralClientSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class CoralClientRpcInstrumentation implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.amazon.coral.client.ApacheHttpHandler");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic())
            .and(named("before"))
            .and(takesArgument(0, named("com.amazon.coral.service.Job"))),
        this.getClass().getName() + "$CoralReqBeforeAdvice");
  }

  @SuppressWarnings("unused")
  public static class CoralReqBeforeAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {

      System.out.println("Coral client instrumentation - OnMethodEnter for before()");
      boolean isClientRequest = job.getAttribute(ServiceConstant.CLIENT_REQUEST) != null;
      System.out.println("Coral Client instrumentation - OnMethodEnter for before() isClientRequest: " + isClientRequest);

      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      System.out.println("Coral client instrumentation - OnMethodEnter for before() operationName = " + operationName);
      if (operationName == null) {
        System.out.println("Coral client instrumentation - OnMethodEnter for before() operationName = null, exit" + operationName);
        return;
      }

      Context parentContext = Java8BytecodeBridge.currentContext();
//      System.out.println("coral exit Before method context start: " + parentContext.toString());
      if (!instrumenter().shouldStart(parentContext, job)) {
        System.out.println("Coral client instrumentation - OnMethodEnter for before() shouldStart = false, exit" + operationName);
        return;
      }
      System.out.println("Coral client instrumentation - OnMethodEnter for before() before instrumenter.start()");
      context = instrumenter().start(parentContext, job);
      scope = context.makeCurrent();
      System.out.println("Coral client instrumentation - OnMethodEnter for before() make context current, finish" + operationName);
      System.out.println("Coral client instrumentation - OnMethodEnter for before() context = " + context.toString());
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      System.out.println("Coral client instrumentation - OnMethodExit for before()");

      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      System.out.println("Coral client instrumentation - OnMethodExit for before() operationName = " + operationName);
      if (operationName == null) {
        System.out.println("Coral client instrumentation - OnMethodExit for before() operationName = null, exit");
        return;
      }

      if (scope == null) {
        System.out.println("Coral client instrumentation - OnMethodExit for before() scope is null, exit");
        return;
      }

      Span span = Span.fromContext(context);
      try {
        scope.close();
        instrumenter().end(context, job, job, job.getFailure());
      } catch (Throwable e) {
        System.out.println("Coral client instrumentation - OnMethodExit for before() End span in error: " + e.getMessage());
      }
      System.out.println("Coral client instrumentation - OnMethodExit for before() trace id = " + span.getSpanContext().getTraceId());
      System.out.println("Coral client instrumentation - OnMethodExit for before() span id = " + span.getSpanContext().getSpanId());
      job.getMetrics().addProperty("AwsXRayTraceId", span.getSpanContext().getTraceId());
      System.out.println("Coral client instrumentation - OnMethodExit for before() finish");
    }
  }

}
