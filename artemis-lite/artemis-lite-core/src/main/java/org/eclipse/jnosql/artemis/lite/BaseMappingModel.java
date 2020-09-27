package org.eclipse.jnosql.artemis.lite;

import java.time.LocalDateTime;

abstract class BaseMappingModel {

    public LocalDateTime getNow() {
        return LocalDateTime.now();
    }
}
