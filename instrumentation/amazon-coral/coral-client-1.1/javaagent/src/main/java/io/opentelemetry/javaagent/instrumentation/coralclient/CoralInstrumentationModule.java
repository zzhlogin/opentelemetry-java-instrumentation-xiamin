package io.opentelemetry.javaagent.instrumentation.coralclient;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@AutoService(InstrumentationModule.class)
public class CoralInstrumentationModule extends InstrumentationModule {
  public CoralInstrumentationModule() {
    super("coral-client", "coral-client-1.1");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return asList( new CoralClientRpcInstrumentation());
  }
}
