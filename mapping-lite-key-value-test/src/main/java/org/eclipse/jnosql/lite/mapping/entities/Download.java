/*
 *  Copyright (c) 2020 Otávio Santana and others
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

@Entity("download")
public class Download {

    @Id
    private Long id;

    @Column
    private byte[] contents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContents() {
        if(contents != null) {
            byte[] copiedArray = new byte[contents.length];
            System.arraycopy(contents, 0, copiedArray, 0, contents.length);
            return copiedArray;
        }
        return new byte[0];
    }

    public void setContents(byte[] contents) {
        if(contents != null) {
            this.contents = new byte[contents.length];
            System.arraycopy(contents, 0, this.contents, 0, contents.length);

        }
    }
}
