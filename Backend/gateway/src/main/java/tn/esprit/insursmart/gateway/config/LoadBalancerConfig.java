package tn.esprit.insursmart.gateway.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

/** Le nom doit etre ecrit EXACTEMENT comme dans lb://CONTRACT-SERVICE */
@Configuration
@LoadBalancerClient(name = "CONTRACT-SERVICE", configuration = ContractLoadBalancerConfig.class)
public class LoadBalancerConfig {
}