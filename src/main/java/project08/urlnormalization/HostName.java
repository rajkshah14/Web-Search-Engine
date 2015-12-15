/**
 * Copyright 2013 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package project08.urlnormalization;

/**
 * The {@link HostName} interface.
 */
public interface HostName {
    /**
     * This method will return an IP address as it is and a Domain Name with the
     * parts in reversed order.
     * 
     * @return optimized host name
     */
    public String getOptimizedForProximityOrder();

    /**
     * Returns the hostname.
     * 
     * @return hostname
     */
    public String getAsString();
}
