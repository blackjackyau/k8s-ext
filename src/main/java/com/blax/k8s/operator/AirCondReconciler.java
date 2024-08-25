package com.blax.k8s.operator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.blax.k8s.operator.crd.AirCond;
import com.blax.k8s.operator.crd.AirCondSpec;
import com.blax.k8s.operator.crd.AirCondStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;

@ControllerConfiguration(dependents = {
        @Dependent(name = "config", type = ConfigMapDependentResource.class),
        },
        maxReconciliationInterval = @MaxReconciliationInterval(
                interval = 3000, timeUnit = TimeUnit.SECONDS)
)
public class AirCondReconciler implements Reconciler<AirCond>, Cleaner<AirCond> {

    private Map<String, AirCondSimulator> airCondStores = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(AirCondReconciler.class);

    public AirCondReconciler() {
    }

    @Override
    public UpdateControl<AirCond> reconcile(
            AirCond resource, Context<AirCond> context) {
        AirCondStatus status = resource.getStatus();

        if (status == null) {
            status = new AirCondStatus();
        }

        long currentGeneration = resource.getMetadata().getGeneration();
        long observedGeneration = resource.getStatus().getObservedGeneration();
        log.info("Reconciling: {}", resource.getMetadata().getName());
        log.info("current generation: {}", currentGeneration);
        log.info("observe generation: {}", observedGeneration);

        if (currentGeneration != observedGeneration) {
            final AirCondSpec spec = resource.getSpec();
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

            status.setOn(airCondSim.isOn());
            status.setCurrentTemp(airCondSim.getTemperature());

            resource.setStatus(status);

            return UpdateControl.updateStatus(resource);
        } else {
            return UpdateControl.noUpdate();
        }
    }

    @Override
    public DeleteControl cleanup(AirCond resource, Context<AirCond> context) {
        String resourceName = resource.getMetadata().getName();
        log.info("Cleaning up resource: {}", resourceName);
        airCondStores.remove(resourceName);
        return DeleteControl.defaultDelete();
    }
}
