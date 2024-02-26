package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.Constant;
import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
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

import static io.opentelemetry.javaagent.instrumentation.coral.CoralSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPrivate;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class CoralClientInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.amazon.coral.client.Dispatcher");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPrivate())
            .and(named("getRequestResponse"))
            .and(takesArgument(0, named("com.amazon.coral.service.Job"))),
        this.getClass().getName() + "$CoralReqAfterAdvice");
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
      System.out.println("DEBUG: Coral client instrumentation - OnMethodEnter");
      System.out.println("DEBUG: coral enter After method start:" + operationName);
      if (operationName == null) {
        return;
      }

      String serviceName = job.getRequest().getAttribute(ServiceConstant.SERVICE_NAME);
      System.out.println("DEBUG: serviceName = " + serviceName);

      String serviceTarget = job.getRequest().getAttribute(ServiceConstant.X_AMZN_SERVICE_TARGET);
      System.out.println("DEBUG: serviceTarget = " + serviceTarget);

      String operationTarget = job.getRequest().getAttribute(ServiceConstant.X_AMZN_OPERATION_TARGET);
      System.out.println("DEBUG: operationTarget = " + operationTarget);

      String httpHost = job.getRequest().getAttribute(HttpConstant.HTTP_HOST);
      System.out.println("DEBUG: httpHost = " + httpHost);

      String httpTarget = job.getRequest().getAttribute(HttpConstant.HTTP_TARGET);
      System.out.println("DEBUG: httpTarget = " + httpTarget);

      String httpClientIp = job.getRequest().getAttribute(HttpConstant.HTTP_CLIENT_IP);
      System.out.println("DEBUG: httpClientIp = " + httpClientIp);

      String httpVerb = job.getRequest().getAttribute(HttpConstant.HTTP_VERB);
      System.out.println("DEBUG: httpVerb = " + httpVerb);

      String httpUserAgent = job.getRequest().getAttribute(HttpConstant.HTTP_USER_AGENT);
      System.out.println("DEBUG: httpUserAgent = " + httpUserAgent);

      String httpProtocol = job.getRequest().getAttribute(HttpConstant.HTTP_PROTOCOL);
      System.out.println("DEBUG: httpProtocol = " + httpProtocol);

      String httpRemoteAddress = job.getRequest().getAttribute(HttpConstant.HTTP_REMOTE_ADDRESS);
      System.out.println("DEBUG: httpRemoteAddress = " + httpRemoteAddress);

      System.out.println("DEBUG: print out all attributes in request");
      Request request = job.getRequest();
      for (Constant<?> attribute: request.getAttributeConstants()) {
        System.out.println(attribute);
      }

      Context parentContext = Java8BytecodeBridge.currentContext();
//      System.out.println("coral enter After method11: " + parentContext.toString());
      scope = parentContext.makeCurrent();
      if (scope == null) {
        System.out.println("DEBUG: coral enter After method end : scope is null");
        return;
      }
      Span span = Span.fromContext(parentContext);
      try {
        instrumenter().end(parentContext, job, job, job.getFailure());
      } catch (Throwable e) {
        System.out.println("DEBUG: End span in error: " + e.getMessage());
      }
      System.out.println("DEBUG: coral enter After method end");
    }
  }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Return(readOnly = false) Object returnVal,
        @Advice.Thrown Throwable throwable,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {
      System.out.println("DEBUG: Coral client instrumentation - OnMethodExit");
      System.out.println("DEBUG: coral exit After method end: " + context.toString());
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
