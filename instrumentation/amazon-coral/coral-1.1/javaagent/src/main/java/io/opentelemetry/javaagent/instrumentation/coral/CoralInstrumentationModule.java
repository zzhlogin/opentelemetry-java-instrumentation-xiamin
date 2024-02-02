package io.opentelemetry.javaagent.instrumentation.coral;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Collections;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class CoralInstrumentationModule extends InstrumentationModule {
  public CoralInstrumentationModule() {
    super("coral", "coral-1.1");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    return Collections.singletonList(new CoralServerInstrumentation());
  }
}
