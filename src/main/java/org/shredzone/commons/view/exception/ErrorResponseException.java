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

/**
 * This exception is thrown when a generic HTTP error happened.
 *
 * @author Richard "Shred" Körber
 */
public class ErrorResponseException extends ViewException {
    private static final long serialVersionUID = 8993197244051374195L;

    private final int responseCode;

    /**
     * Creates a new {@link ErrorResponseException}.
     *
     * @param responseCode
     *            HTTP response code
     */
    public ErrorResponseException(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Creates a new {@link ErrorResponseException}.
     *
     * @param responseCode
     *            HTTP response code
     * @param msg
     *            HTTP response message
     */
    public ErrorResponseException(int responseCode, String msg) {
        super(msg);
        this.responseCode = responseCode;
    }

    /**
     * @return HTTP response code
     */
    public int getResponseCode() {
        return responseCode;
    }

}
