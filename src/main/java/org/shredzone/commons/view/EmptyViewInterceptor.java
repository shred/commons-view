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

package org.shredzone.commons.view;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shredzone.commons.view.exception.ErrorResponseException;

/**
 * A {@link ViewInterceptor} that does nothing. May be used as a base class for
 * interceptor implementations.
 *
 * @author Richard "Shred" Körber
 */
public class EmptyViewInterceptor implements ViewInterceptor {

    @Override
    public void onRequest(HttpServletRequest req, HttpServletResponse resp) {
        // do nothing
    }

    @Override
    public void onViewHandlerInvocation(ViewContext context, Object bean, Method method) {
        // do nothing
    }

    @Override
    public String onRendering(String template, HttpServletRequest req, HttpServletResponse resp) {
        return null;
    }

    @Override
    public boolean onErrorResponse(ErrorResponseException ex, HttpServletRequest req, HttpServletResponse resp) {
        return false;
    }

}
