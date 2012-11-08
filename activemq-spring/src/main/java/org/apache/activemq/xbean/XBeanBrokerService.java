/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.xbean;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.usage.SystemUsage;
import org.springframework.beans.CachedIntrospectionResults;

/**
 * An ActiveMQ Message Broker. It consists of a number of transport
 * connectors, network connectors and a bunch of properties which can be used to
 * configure the broker as its lazily created.
 *
 * @org.apache.xbean.XBean element="broker" rootElement="true"
 * @org.apache.xbean.Defaults {code:xml}
 * <broker test="foo.bar">
 *   lets.
 *   see what it includes.
 * </broker>
 * {code}
 *
 */
public class XBeanBrokerService extends BrokerService {

    private boolean start;

    public XBeanBrokerService() {
        start = BrokerFactory.getStartDefault();
    }

    /**
     *
     * @throws Exception
     * @org.apache.xbean.InitMethod
     */
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        ensureSystemUsageHasStore();
        if (shouldAutostart()) {
            start();
        }
    }

    @Override
    protected boolean shouldAutostart() {
        return start;
    }

    private void ensureSystemUsageHasStore() throws IOException {
        SystemUsage usage = getSystemUsage();
        if (usage.getStoreUsage().getStore() == null) {
            usage.getStoreUsage().setStore(getPersistenceAdapter());
        }
        if (usage.getTempUsage().getStore() == null) {
            usage.getTempUsage().setStore(getTempDataStore());
        }
    }

    /**
     *
     * @throws Exception
     * @org.apache.xbean.DestroyMethod
     */
    @PreDestroy
    public void destroy() throws Exception {
        stop();
    }

    @Override
    public void stop() throws Exception {
        // must clear this Spring cache to avoid any memory leaks
        CachedIntrospectionResults.clearClassLoader(getClass().getClassLoader());
        super.stop();
    }

    /**
     * Sets whether or not the broker is started along with the ApplicationContext it is defined within.
     * Normally you would want the broker to start up along with the ApplicationContext but sometimes when working
     * with JUnit tests you may wish to start and stop the broker explicitly yourself.
     */
    public void setStart(boolean start) {
        this.start = start;
    }
}
