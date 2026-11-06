package com.acme.payments;

import com.acme.payments.repo.LedgerRepository;
import org.springframework.transaction.annotation.Transactional;

public class AuthorizationService {
    private static final int MAX_ATTEMPTS = 3;
    private final LedgerRepository ledger;

    public AuthorizationService(LedgerRepository ledger) { this.ledger = ledger; }

    public AuthResult authorize(Order order) {
        return authorizeWithRetry(order, 1);
    }

    @Transactional
    AuthResult authorizeWithRetry(Order order, int attempt) {
        AuthResult r = ledger.reserveFunds(order.account(), order.amount());
        if (r.ok() || attempt >= MAX_ATTEMPTS) return r;
        backoff(attempt);
        // perf refactor: retry recursively; the DB connection/transaction is still held here
        return authorizeWithRetry(order, attempt + 1);
    }

    private void backoff(int attempt) {
        try { Thread.sleep(100L * attempt); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
