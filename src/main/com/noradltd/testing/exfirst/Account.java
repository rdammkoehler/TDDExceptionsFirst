package com.noradltd.testing.exfirst;

import java.math.BigDecimal;

class Account {
    BigDecimal threshold;
    BankClock bankClock;
    SavingsService savingsService;
    BigDecimal balance = BigDecimal.ZERO;

    Account(BigDecimal threshold_p, BankClock bankClock_p, SavingsService savingsService_p) {
        threshold = threshold_p;
        bankClock = bankClock_p;
        savingsService = savingsService_p;
    }

    TxnStatus deposit(BigDecimal amount) {
        if (bankClock.isOpen()) {
            balance = balance.add(amount);
            BigDecimal excessFunds = balance.subtract(threshold);
            if (excessFunds.compareTo(BigDecimal.ZERO) > 0) {
                CallStatus status = savingsService.deposit(excessFunds);
                if (status != CallStatus.SUCCESS) {
                    throw new RuntimeException("System Error: Savings Service Unavailable");
                }
                balance = threshold;
            }
            return TxnStatus.SUCCESS;
        }
        return TxnStatus.DEFERRED;
    }

    BigDecimal balance() {
        return balance;
    }
}
