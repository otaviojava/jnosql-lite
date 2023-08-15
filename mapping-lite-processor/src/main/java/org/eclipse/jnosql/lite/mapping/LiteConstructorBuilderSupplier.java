/*
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.jnosql.lite.mapping;

import org.eclipse.jnosql.mapping.metadata.ConstructorBuilder;
import org.eclipse.jnosql.mapping.metadata.ConstructorBuilderSupplier;
import org.eclipse.jnosql.mapping.metadata.ConstructorMetadata;

/**
 * A supplier of constructor avoiding reflection.
 */
public class LiteConstructorBuilderSupplier implements ConstructorBuilderSupplier {
    @Override
    public ConstructorBuilder apply(ConstructorMetadata constructorMetadata) {
        throw new UnsupportedOperationException("Eclipse JNoSQL Lite does not support reflection, including the use of constructors.");
    }
}
