package com.noradltd.testing.exfirst;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class TestExFirst {

    public static final BigDecimal THRESHOLD = BigDecimal.valueOf(1000.00);

    static class BankClockOpen implements BankClock {
        public boolean isOpen() {
            return true;
        }
    }

    static class BankClockClosed implements BankClock {
        public boolean isOpen() {
            return false;
        }
    }

    static abstract class SavingsServiceTestDouble implements SavingsService {
        protected boolean invoked = false;

        boolean isInvoked() {
            return invoked;
        }
    }

    static class FailingSavingsService extends SavingsServiceTestDouble {

        @Override
        public CallStatus deposit(BigDecimal amount) {
            invoked = true;
            return CallStatus.FAILED;
        }
    }

    static class SucceedingSavingsService extends SavingsServiceTestDouble {

        @Override
        public CallStatus deposit(BigDecimal amount) {
            invoked = true;
            return CallStatus.SUCCESS;
        }
    }

    @Test
    void testSavingsServiceIsUnavailable() {
        Account account = new Account(THRESHOLD, new BankClockOpen(), new FailingSavingsService());
        BigDecimal money = moreThanThreshold();
        String errorMessage = "System Error: Savings Service Unavailable";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> account.deposit(money));

        assertTrue(exception.getMessage().contains(errorMessage), "wrong error");
    }

    @Test
    void testTransactionOutsideBankingHoursIsDeferred() {
        Account account = new Account(THRESHOLD, new BankClockClosed(), new SucceedingSavingsService());
        BigDecimal money = moreThanThreshold();

        TxnStatus status = account.deposit(money);

        assertSame(status, TxnStatus.DEFERRED);
    }

    @Test
    void testTransactionInsideBankingHoursIsNotDeferred() {
        Account account = new Account(THRESHOLD, new BankClockOpen(), new SucceedingSavingsService());
        BigDecimal money = moreThanThreshold();

        TxnStatus status = account.deposit(money);

        assertNotSame(status, TxnStatus.DEFERRED);
    }

    @Test
    void testTransactionBelowThresholdIncreasesBalance() {
        Account account = new Account(THRESHOLD, new BankClockOpen(), new SucceedingSavingsService());
        BigDecimal money = lessThanThreshold();

        account.deposit(money);

        assertEquals(money, account.balance());
    }

    @Test
    void testTransactionBelowThresholdDoesntInvokeSavingsService() {
        SucceedingSavingsService savingsService = new SucceedingSavingsService();
        Account account = new Account(THRESHOLD, new BankClockOpen(), savingsService);
        BigDecimal money = lessThanThreshold();

        account.deposit(money);

        assertFalse(savingsService.isInvoked());
    }

    @Test
    void testTransactionAboveThresholdInvokesSavingsService() {
        SucceedingSavingsService savingsService = new SucceedingSavingsService();
        Account account = new Account(THRESHOLD, new BankClockOpen(), savingsService);
        BigDecimal money = moreThanThreshold();

        account.deposit(money);

        assertTrue(savingsService.isInvoked());
    }

    @Test
    void testTransactionAboveThreasholdCausesAccountBalanceToEqualThreshold() {
        SucceedingSavingsService savingsService = new SucceedingSavingsService();
        Account account = new Account(THRESHOLD, new BankClockOpen(), savingsService);
        BigDecimal money = moreThanThreshold();

        account.deposit(money);

        assertEquals(THRESHOLD, account.balance());
    }

    @Test
    void testTransactionAtThresholdDoesntInvokeSavingsService() {
        SucceedingSavingsService savingsService = new SucceedingSavingsService();
        Account account = new Account(THRESHOLD, new BankClockOpen(), savingsService);
        BigDecimal money = THRESHOLD;

        account.deposit(money);

        assertFalse(savingsService.isInvoked());
    }

    private static BigDecimal moreThanThreshold() {
        return THRESHOLD.multiply(BigDecimal.valueOf(2));
    }

    private static BigDecimal lessThanThreshold() {
        return THRESHOLD.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
}

