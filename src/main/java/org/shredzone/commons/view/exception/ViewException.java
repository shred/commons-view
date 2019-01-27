/*
 * Shredzone Commons
 *
 * Copyright (C) 2012 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.shredzone.commons.view.exception;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This exception is raised when a view could not be rendered for various reasons.
 *
 * @author Richard "Shred" Körber
 */
@ParametersAreNonnullByDefault
public class ViewException extends Exception {
    private static final long serialVersionUID = 2960506285496985876L;

    /**
     * Creates a new {@link ViewException}.
     */
    public ViewException() {
        super();
    }

    /**
     * Creates a new {@link ViewException}.
     *
     * @param msg
     *            Message
     */
    public ViewException(String msg) {
        super(msg);
    }

    /**
     * Creates a new {@link ViewException}.
     *
     * @param cause
     *            Exception that caused this exception
     */
    public ViewException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new {@link ViewException}.
     *
     * @param msg
     *            Message
     * @param cause
     *            Exception that caused this exception
     */
    public ViewException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
