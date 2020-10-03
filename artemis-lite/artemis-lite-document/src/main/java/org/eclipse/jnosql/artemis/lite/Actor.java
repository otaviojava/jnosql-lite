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
package org.eclipse.jnosql.artemis.lite;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.util.List;
import java.util.Map;

@Entity
public class Actor {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private int age;

    @Column
    private List<String> phones;

    private String ignore;

    @Column
    private Map<String, String> movieCharacter;

    @Column
    private Map<String, Integer> movieRating;

    Actor(long id, String name, int age, List<String> phones, String ignore, Map<String, String> movieCharacter,
          Map<String, Integer> movieRating) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phones = phones;
        this.ignore = ignore;
        this.movieCharacter = movieCharacter;
        this.movieRating = movieRating;
    }

    Actor() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public String getIgnore() {
        return ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

    public Map<String, String> getMovieCharacter() {
        return movieCharacter;
    }

    public void setMovieCharacter(Map<String, String> movieCharacter) {
        this.movieCharacter = movieCharacter;
    }

    public Map<String, Integer> getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(Map<String, Integer> movieRating) {
        this.movieRating = movieRating;
    }

    public static ActorBuilder actorBuilder() {
        return new ActorBuilder();
    }
}
