/**
 * og-node-core - The core resources of an Open Groups node
 * Copyright (C) 2020  Adrian Cristian Ionescu - https://github.com/acionescu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.segoia.ogeg.services.core.agents;

import java.util.List;

import net.segoia.event.conditions.Condition;
import net.segoia.event.conditions.TrueCondition;
import net.segoia.ogeg.node.vo.core.PeerNodeConfig;

public class NodeInteroperabilityConfig {
    /**
     * Nodes we connect to
     */
    private List<PeerNodeConfig> upstreamNodes;
    
    /**
     * The condition that a service provider needs to respect in order for this node to consume its services
     * <br>
     * By default accepts everything.
     */
    private Condition serviceProviderCondition=new TrueCondition();
    
    /**
     * The condition that a service consumer needs to respect in order to allow it to access our services
     * <br>
     * By default accepts everything.
     */
    private Condition serviceConsumerCondition = new TrueCondition();

    public List<PeerNodeConfig> getUpstreamNodes() {
        return upstreamNodes;
    }

    public void setUpstreamNodes(List<PeerNodeConfig> upstreamNodes) {
        this.upstreamNodes = upstreamNodes;
    }

    public Condition getServiceProviderCondition() {
        return serviceProviderCondition;
    }

    public void setServiceProviderCondition(Condition serviceProviderCondition) {
        this.serviceProviderCondition = serviceProviderCondition;
    }

    public Condition getServiceConsumerCondition() {
        return serviceConsumerCondition;
    }

    public void setServiceConsumerCondition(Condition serviceConsumerCondition) {
        this.serviceConsumerCondition = serviceConsumerCondition;
    }
    
}
