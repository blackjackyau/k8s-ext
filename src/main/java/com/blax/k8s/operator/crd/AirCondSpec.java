package com.blax.k8s.operator.crd;

import io.fabric8.generator.annotation.Required;
import lombok.Data;

@Data
public class AirCondSpec {

  @Required
  private int temperature;
  @Required
  private boolean on;

}
