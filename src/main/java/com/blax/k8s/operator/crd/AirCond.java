package com.blax.k8s.operator.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("com.blax.operator")
@Version("v1")
@ShortNames("ac")
public class AirCond extends CustomResource<AirCondSpec, AirCondStatus> implements Namespaced {
}
