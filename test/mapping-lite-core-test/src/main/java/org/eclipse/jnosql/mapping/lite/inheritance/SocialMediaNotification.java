package org.eclipse.jnosql.mapping.lite.inheritance;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;

@Entity
public class SocialMediaNotification extends Notification {

    @Column
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String send() {
        return "Sending message to via social media to the nickname: " + nickname;
    }
}