/*
 *  Copyright (c) 2021 Otávio Santana and others
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
package org.eclipse.jnosql.mapping.lite.metadata;

import jakarta.nosql.Settings;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.Converter;

import java.util.Map;
import java.util.Spliterator;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

/**
 * Converter the {@link String} to {@link Settings}
 */
public class SettingsConverter implements Converter<Settings> {

    @Override
    public Settings convert(String value) {
        Config config = ConfigProvider.getConfig();
        final Spliterator<String> spliterator = config.getPropertyNames().spliterator();

        final String settingsPrefix = getSettingsPrefix(value);
        final Map<String, Object> settings = stream(spliterator, false)
                .filter(isSettings(settingsPrefix))
                .distinct()
                .collect(toMap(s -> s.replace(value + ".settings.", ""), s ->
                        config.getValue(s, String.class)));

        return Settings.of(settings);
    }

    private Predicate<String> isSettings(String prefix) {
        return s -> s.startsWith(prefix);
    }

    private String getSettingsPrefix(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return "settings";
        }
        return prefix + '.' + "settings";
    }
}
