package com.projectgalen.lib.ui;

// ===========================================================================
//     PROJECT: PGUI
//    FILENAME: UIFuture.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 27, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public final class UIFuture<T> implements Future<T> {
    private final AtomicReference<Exception> error      = new AtomicReference<>(null);
    private final AtomicReference<T>         results    = new AtomicReference<>(null);
    private final Callable<T>                callable;
    private final Object                     lockObject = UUID.randomUUID().toString();
    private       boolean                    done       = false;

    public UIFuture(Callable<T> callable) {
        this.callable = callable;
        SwingUtilities.invokeLater(this::invokeCallable);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public @Nullable T get() throws InterruptedException, ExecutionException {
        synchronized(lockObject) {
            while(!done) lockObject.wait();
            return _get();
        }
    }

    @Contract(pure = true)
    @Override
    public @Nullable T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if(timeout < 0) throw new IllegalArgumentException(M.msgs.getString("msg.err.timeout_lessthan_zero"));
        long millis = unit.toMillis(timeout);

        synchronized(lockObject) {
            if(done) return _get();
            long when = (System.currentTimeMillis() + millis);
            do {
                lockObject.wait(unit.toMillis(when - System.currentTimeMillis()));
                if(done) return _get();
                if(System.currentTimeMillis() >= when) throw new TimeoutException();
            }
            while(true);
        }
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        synchronized(lockObject) { return done; }
    }

    private T _get() throws ExecutionException {
        Exception e = error.get();
        if(e != null) throw new ExecutionException(e);
        return results.get();
    }

    private void invokeCallable() {
        try {
            results.set(this.callable.call());
        }
        catch(Exception e) {
            error.set(e);
        }
        finally {
            synchronized(lockObject) {
                done = true;
                lockObject.notifyAll();
            }
        }
    }
}
