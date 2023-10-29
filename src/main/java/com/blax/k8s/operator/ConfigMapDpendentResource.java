package com.blax.k8s.operator;

import java.util.Date;
import java.util.Map;

import com.blax.k8s.operator.crd.AirCond;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@KubernetesDependent(labelSelector = "app.kubernetes.io/managed-by=air-cond-operator")
public class ConfigMapDpendentResource
    extends CRUDKubernetesDependentResource<ConfigMap, AirCond> {

  private static final Logger log = LoggerFactory.getLogger(ConfigMapDpendentResource.class);

  public ConfigMapDpendentResource() {
    super(ConfigMap.class);
  }

  @Override
  protected ConfigMap desired(AirCond primary, Context<AirCond> context) {
    return new ConfigMapBuilder()
        .withMetadata(new ObjectMetaBuilder()
            .withName(primary.getMetadata().getName() + "config")
            .withNamespace(primary.getMetadata().getNamespace())
            .build())
        .addToData(Map.of("spec", primary.getSpec().toString()))
        .addToData(Map.of("updatedAt", new Date().toString()))
        .build();
  }
}
