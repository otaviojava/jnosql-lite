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
