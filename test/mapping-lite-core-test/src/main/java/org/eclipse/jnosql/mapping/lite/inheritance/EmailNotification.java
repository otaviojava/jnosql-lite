package org.eclipse.jnosql.mapping.lite.inheritance;


import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.DiscriminatorValue;
import jakarta.nosql.mapping.Entity;

@Entity
@DiscriminatorValue("Email")
public class EmailNotification extends Notification {

    @Column
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String send() {
        return "Sending message to email sms to the email: " + email;
    }
}