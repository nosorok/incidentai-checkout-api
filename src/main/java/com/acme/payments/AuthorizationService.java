package com.acme.payments;

import com.acme.payments.repo.LedgerRepository;
import org.springframework.transaction.annotation.Transactional;

public class AuthorizationService {
    private static final int MAX_ATTEMPTS = 3;
    private final LedgerRepository ledger;

    public AuthorizationService(LedgerRepository ledger) { this.ledger = ledger; }

    public AuthResult authorize(Order order) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            AuthResult r = tryAuthorize(order);      // each attempt opens and commits its own transaction
            if (r.ok()) return r;
            backoff(attempt);                        // no DB connection held while waiting
        }
        return AuthResult.declined("retries exhausted");
    }

    @Transactional
    AuthResult tryAuthorize(Order order) {
        return ledger.reserveFunds(order.account(), order.amount());
    }

    private void backoff(int attempt) {
        try { Thread.sleep(100L * attempt); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
