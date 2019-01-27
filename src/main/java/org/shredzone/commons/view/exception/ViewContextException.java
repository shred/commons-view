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

import org.shredzone.commons.view.ViewContext;

/**
 * This exception is thrown when an error happened because {@link ViewContext} was unable
 * to return the requested data.
 *
 * @author Richard "Shred" Körber
 */
@ParametersAreNonnullByDefault
public class ViewContextException extends ViewException {
    private static final long serialVersionUID = -1796171034784340995L;

    /**
     * Creates a new {@link ViewContextException}.
     */
    public ViewContextException() {
        super();
    }

    /**
     * Creates a new {@link ViewContextException}.
     *
     * @param msg
     *            Reason for the failure
     */
    public ViewContextException(String msg) {
        super(msg);
    }

    /**
     * Creates a new {@link ViewContextException}.
     *
     * @param cause
     *            Exception that caused the failure
     */
    public ViewContextException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new {@link ViewContextException}.
     *
     * @param msg
     *            Reason for the failure
     * @param cause
     *            Exception that caused the failure
     */
    public ViewContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
