package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.Job;
import com.amazon.coral.service.ServiceConstant;
import com.amazon.coral.service.http.HttpHeaders;
import com.google.common.base.Throwables;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static com.amazon.coral.service.HttpConstant.HTTP_HEADERS;
import static io.opentelemetry.javaagent.instrumentation.coral.CoralSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class CoralServerHttpInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.amazon.coral.service.HttpHandler");
//    return named("com.amazon.coral.service.HttpRpcHandler");
//    return named("com.amazon.coral.service.ActivityHandler");
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

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before()");
//      boolean isClientRequest = job.getAttribute(ServiceConstant.CLIENT_REQUEST) != null;
//      System.out.println("Coral server instrumentation - isClientRequest: " + isClientRequest);
      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before(): operationName = " + operationName);

//      if (operationName == null) {
//        HttpHeaders headers = job.getRequest().getAttribute(HTTP_HEADERS);
//        CharSequence cs = headers.getValue("X-Amz-Requested-Operation");
//        if (cs != null && cs.length() > 0) {
//          System.out.println("coral exit Before method start: X-Amz-Requested-Operation = " + cs);
//          operationName = Character.toUpperCase(cs.charAt(0)) + (cs.length() > 1 ? cs.subSequence(1, cs.length()).toString() : "");
//        }
//      }

      if (operationName == null) {
        System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before(): operationName = null => exit");
        return;
      }

//      HttpHeaders headers = job.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
//      System.out.println("DEBUG: HTTP header = " + headers.toString());
//      headers.getHeaderNames().forEach(name -> {
//        System.out.println("DEBUG: HTTP header name = " + name);
//      });


      Context parentContext = Java8BytecodeBridge.currentContext();
      // TODO: fix the current context cleanup work
//      Context parentContext = Context.root();
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before(): parentContext =" + parentContext.toString());
      if (!instrumenter().shouldStart(parentContext, job)) {
        System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before(): operationName is not null, but it's suppressed => exit");
        return;
      }
      context = instrumenter().start(parentContext, job);
      scope = context.makeCurrent();
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodExit for before() succeed");
    }
  }

  @SuppressWarnings("unused")
  public static class CoralReqAfterAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodEnter for after()");
      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodEnter for after(): operationName = " + operationName);
      if (operationName == null) {
        System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodEnter for after(): operationName = null => exit");
        return;
      }
      Context parentContext = Java8BytecodeBridge.currentContext();
      scope = parentContext.makeCurrent();
      if (scope == null) {
        System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodEnter for after(): scope is null => exit");
        return;
      }
      Span span = Span.fromContext(parentContext);
      try {
        scope.close();
        Throwable failure = job.getFailure();
        System.out.println("job get failure = " + failure);
        instrumenter().end(parentContext, job, job, job.getFailure());
      } catch (Throwable e) {
        System.out.println("End span in error: " + Throwables.getStackTraceAsString(e));
      }
      System.out.println("coral trace id: " + span.getSpanContext().getTraceId());
      job.getMetrics().addProperty("AwsXRayTraceId", span.getSpanContext().getTraceId());
      System.out.println("DEBUG: Coral server HTTP instrumentation - OnMethodEnter for after() succeed");
    }
  }

}
