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
import javax.servlet.http.HttpServletResponse;

/**
 * This exception is thrown when an error happened because there is no view defined for
 * the requested URL.
 *
 * @author Richard "Shred" Körber
 */
@ParametersAreNonnullByDefault
public class PageNotFoundException extends ErrorResponseException {
    private static final long serialVersionUID = -1119789724918850606L;

    /**
     * Creates a new {@link PageNotFoundException}.
     */
    public PageNotFoundException() {
        super(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Creates a new {@link PageNotFoundException} with a message.
     *
     * @param msg
     *            Message
     */
    public PageNotFoundException(String msg) {
        super(HttpServletResponse.SC_NOT_FOUND, msg);
    }

}
