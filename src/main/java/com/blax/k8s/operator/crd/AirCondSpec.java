package com.blax.k8s.operator.crd;

import lombok.Data;

@Data
public class AirCondSpec {

  private int temperature;
  private boolean on;

}
