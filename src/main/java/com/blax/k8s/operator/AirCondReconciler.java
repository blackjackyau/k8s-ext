package com.blax.k8s.operator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.blax.k8s.operator.crd.AirCond;
import com.blax.k8s.operator.crd.AirCondSpec;
import com.blax.k8s.operator.crd.AirCondStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;

@ControllerConfiguration(dependents = {
        @Dependent(name = "config", type = ConfigMapDependentResource.class),
},
        maxReconciliationInterval = @MaxReconciliationInterval(
                interval = 30, timeUnit = TimeUnit.SECONDS)
)
public class AirCondReconciler implements Reconciler<AirCond> {

    private Map<String, AirCondSimulator> airCondStores = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(AirCondReconciler.class);

    private final KubernetesClient kubernetesClient;

    public AirCondReconciler() {
        this(new DefaultKubernetesClient());
    }

    public AirCondReconciler(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public UpdateControl<AirCond> reconcile(
            AirCond resource, Context<AirCond> context) {

        final AirCondSpec spec = resource.getSpec();
        log.info("Reconciling: {}", resource.getMetadata().getName());
        AirCondSimulator airCondSim = airCondStores.get(resource.getMetadata().getName());

        if (airCondSim == null) {
            airCondSim = new AirCondSimulator();
            airCondSim.setName(resource.getMetadata().getName());
            airCondStores.put(airCondSim.getName(), airCondSim);
        }

        airCondSim.setName(resource.getMetadata().getName());
        airCondSim.setOn(spec.isOn());

        if (airCondSim.on) {
            if (airCondSim.getTemperature() < spec.getTemperature()) {
                airCondSim.increaseTemp();
            }

            if (airCondSim.getTemperature() > spec.getTemperature()) {
                airCondSim.decreaseTemp();
            }
        }

        AirCondStatus status = new AirCondStatus();
        status.setOn(airCondSim.isOn());
        status.setCurrentTemp(airCondSim.getTemperature());

        resource.setStatus(status);
        return UpdateControl.updateStatus(resource);
    }
}
