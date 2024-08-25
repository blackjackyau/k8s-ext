package com.blax.k8s.operator.crd;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class AirCondStatus extends ObservedGenerationAwareStatus {

  private int currentTemp;
  private boolean on;

}
