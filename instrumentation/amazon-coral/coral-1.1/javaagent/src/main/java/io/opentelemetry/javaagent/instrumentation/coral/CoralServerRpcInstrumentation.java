package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.Job;
import com.amazon.coral.service.ServiceConstant;
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

import static io.opentelemetry.javaagent.instrumentation.coral.CoralSingletons.instrumenter;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class CoralServerRpcInstrumentation implements TypeInstrumentation {

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("com.amazon.coral.service.HttpRpcHandler");
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

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(
        @Advice.Argument(0) Job job,
        @Advice.Local("otelContext") Context context,
        @Advice.Local("otelScope") Scope scope) {

      String operationName = job.getRequest().getAttribute(
          ServiceConstant.SERVICE_OPERATION_NAME);

      if (operationName == null) {
        System.out.println("DEBUG: Coral server RPC instrumentation - OnMethodExit for before(): operationName = null => exit");
        return;
      }

      Context parentContext = Java8BytecodeBridge.currentContext();
      if (!instrumenter().shouldStart(parentContext, job)) {
        return;
      }

      // we know the RPC span is a local root, so we can call rootContext to create a new context each time
      parentContext = Java8BytecodeBridge.rootContext();
      context = instrumenter().start(parentContext, job);
      scope = context.makeCurrent();
    }
  }

}
