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

import org.shredzone.commons.view.ViewContext;

/**
 * This exception is thrown when an error happened because {@link ViewContext} was unable
 * to return the requested data.
 *
 * @author Richard "Shred" Körber
 */
public class ViewContextException extends ViewException {
    private static final long serialVersionUID = -1796171034784340995L;

    public ViewContextException() {
        super();
    }

    public ViewContextException(String msg) {
        super(msg);
    }

    public ViewContextException(Throwable cause) {
        super(cause);
    }

    public ViewContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
