package com.noradltd.testing.exfirst;

import java.math.BigDecimal;

interface SavingsService {
    CallStatus deposit(BigDecimal amount);
}
