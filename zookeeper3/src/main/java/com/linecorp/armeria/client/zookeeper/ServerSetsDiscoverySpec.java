/*
 * Copyright 2020 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.zookeeper;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.client.Endpoint;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.zookeeper.ServerSetsInstance;
import com.linecorp.armeria.internal.common.zookeeper.ServerSetsNodeValueCodec;

final class ServerSetsDiscoverySpec implements ZooKeeperDiscoverySpec {

    private static final Logger logger = LoggerFactory.getLogger(ServerSetsDiscoverySpec.class);

    private final Function<? super ServerSetsInstance, Endpoint> converter;

    ServerSetsDiscoverySpec(Function<? super ServerSetsInstance, Endpoint> converter) {
        this.converter = converter;
    }

    @Nullable
    @Override
    public String path() {
        return null;
    }

    @Nullable
    @Override
    public Endpoint decode(byte[] data) {
        final ServerSetsInstance decodedInstance = ServerSetsNodeValueCodec.INSTANCE.decode(data);
        if (decodedInstance.serviceEndpoint() == null) {
            return null;
        }
        final Endpoint endpoint = converter.apply(decodedInstance);
        if (endpoint == null) {
            logger.warn("The endpoint converter returned null from {}.", decodedInstance);
        }
        return endpoint;
    }
}
