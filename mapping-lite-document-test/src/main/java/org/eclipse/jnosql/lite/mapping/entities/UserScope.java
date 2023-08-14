/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.eclipse.jnosql.lite.mapping.entities;


import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

import java.util.HashMap;
import java.util.Map;

@Entity
public class UserScope {

    @Id
    private String userName;

    @Column("scope")
    private String scope;

    @Column("properties")
    private Map<String, Object> properties = new HashMap<>();

    public String getUserName() {
        return userName;
    }

    public String getScope() {
        return scope;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}