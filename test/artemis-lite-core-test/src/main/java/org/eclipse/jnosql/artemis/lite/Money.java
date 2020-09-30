package org.eclipse.jnosql.artemis.lite;

import java.math.BigDecimal;
import java.util.Objects;

public class Money {

    private final String currency;

    private final BigDecimal bigDecimal;

    public Money(String currency, BigDecimal bigDecimal) {
        this.currency = currency;
        this.bigDecimal = bigDecimal;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Money money = (Money) o;
        return Objects.equals(currency, money.currency) &&
                Objects.equals(bigDecimal, money.bigDecimal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, bigDecimal);
    }

    @Override
    public String toString() {
        return "Money{" +
                "currency='" + currency + '\'' +
                ", bigDecimal=" + bigDecimal +
                '}';
    }
}
