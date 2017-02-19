/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.api.events;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;

/**
 * A clan event that is cancellable.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public abstract class CancellableClanEvent extends ClanEvent implements Cancellable {

    private final String defaultCancelMessage;
    private boolean cancelled;
    private String cancelMessage;

    public CancellableClanEvent(String defaultCancelMessage, Cause cause) {
        super(cause);
        this.defaultCancelMessage = defaultCancelMessage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Cancel this event.
     *
     * @param reasonMessage A user friendly explanation for why the event is cancelled
     */
    public void setCancelledWithMessage(String reasonMessage) {
        setCancelled(true);
        cancelMessage = reasonMessage;
    }

    /**
     * Get the reason why this event was cancelled
     *
     * @return the reason why this event was cancelled, returns null when the event isn't cancelled
     */
    public String getCancelMessage() {
        return cancelled && cancelMessage == null ? defaultCancelMessage : cancelMessage;
    }
}
