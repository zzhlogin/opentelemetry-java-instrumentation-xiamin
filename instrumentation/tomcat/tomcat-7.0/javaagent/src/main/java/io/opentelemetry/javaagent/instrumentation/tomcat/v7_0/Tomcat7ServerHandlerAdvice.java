/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tomcat.v7_0;

import static io.opentelemetry.javaagent.instrumentation.tomcat.v7_0.Tomcat7Singletons.helper;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.bootstrap.http.HttpServerResponseCustomizerHolder;
import net.bytebuddy.asm.Advice;
import org.apache.coyote.Request;
import org.apache.coyote.Response;

@SuppressWarnings("unused")
public class Tomcat7ServerHandlerAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static void onEnter(
      @Advice.Argument(0) Request request,
      @Advice.Argument(1) Response response,
      @Advice.Local("otelContext") Context context,
      @Advice.Local("otelScope") Scope scope) {

    Context parentContext = Java8BytecodeBridge.currentContext();
    System.out.println("Tomcat7ServerHandlerAdvice onEnter method Start1: " + parentContext.toString());
    if (!helper().shouldStart(parentContext, request)) {
      return;
    }

    context = helper().start(parentContext, request);

    scope = context.makeCurrent();

    HttpServerResponseCustomizerHolder.getCustomizer()
        .customize(context, response, Tomcat7ResponseMutator.INSTANCE);
    System.out.println("Tomcat7ServerHandlerAdvice onEnter method End: " + context.toString());
  }

  @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
  public static void stopSpan(
      @Advice.Argument(0) Request request,
      @Advice.Argument(1) Response response,
      @Advice.Thrown Throwable throwable,
      @Advice.Local("otelContext") Context context,
      @Advice.Local("otelScope") Scope scope) {
    System.out.println("Tomcat7ServerHandlerAdvice stopSpan method Start: " + context.toString());
    System.out.println("Tomcat7ServerHandlerAdvice stopSpan method Start scope: " + scope.toString());
//    helper().end(request, response, throwable, context, scope);
    System.out.println("Tomcat7ServerHandlerAdvice stopSpan method End: " + context.toString());
  }
}
