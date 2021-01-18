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
package org.eclipse.jnosql.mapping.lite;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Locale.ENGLISH;

public final class ProcessorUtil {

    public static final Pattern COMPILE = Pattern.compile("<(.*?)>");

    private ProcessorUtil() {
    }

    static String getPackageName(TypeElement classElement) {
        return ((PackageElement) classElement.getEnclosingElement()).getQualifiedName().toString();
    }

    static String getSimpleNameAsString(Element element) {
        return element.getSimpleName().toString();
    }

    static String capitalize(String name) {
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }

    public static boolean isTypeElement(Element element) {
        return element instanceof TypeElement;
    }

    public static String extractFromType(String returnType) {
        Matcher matcher = COMPILE.matcher(returnType);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return returnType;
        }
    }
}
