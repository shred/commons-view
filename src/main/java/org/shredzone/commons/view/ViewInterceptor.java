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

import javax.annotation.ParametersAreNonnullByDefault;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.shredzone.commons.view.exception.ErrorResponseException;

/**
 * Spring beans implementing this interface are allowed to inspect or change the output
 * of view handlers.
 *
 * @author Richard "Shred" Körber
 */
@ParametersAreNonnullByDefault
public interface ViewInterceptor {

    /**
     * Called when a HTTP request was sent to the view service. The request has not yet
     * been processed.
     *
     * @param req
     *            {@link HttpServletRequest} to be handled
     * @param resp
     *            {@link HttpServletResponse} with the response
     */
    void onRequest(HttpServletRequest req, HttpServletResponse resp);

    /**
     * Called when a ViewHandler is about to be invoked.
     *
     * @param context
     *            {@link ViewContext} passed to the view handler
     * @param bean
     *            Spring bean containing the view handler
     * @param method
     *            View handler method that will be invoked
     */
    void onViewHandlerInvocation(ViewContext context, Object bean, Method method);

    /**
     * Called when a ViewHandler successfully processed the request and returned a
     * template string that is now to be rendered. The interceptor can change the template
     * name.
     *
     * @param template
     *            Template name returned by the view handler
     * @param req
     *            {@link HttpServletRequest} that was handled
     * @param resp
     *            {@link HttpServletResponse} with the response
     * @return a different template name, or {@code null} for keeping the original
     *         template name
     */
    String onRendering(String template, HttpServletRequest req, HttpServletResponse resp);

    /**
     * Called when an {@link ErrorResponseException} occured. The interceptor is able to
     * catch the error.
     *
     * @param ex
     *            {@link ErrorResponseException} that occured
     * @param req
     *            {@link HttpServletRequest} that led to this error
     * @param resp
     *            {@link HttpServletResponse} with the response
     * @return {@code true} if the interceptor responded to the error. Other interceptors
     *         will not be invoked, and the {@link HttpServletResponse} is sent back.
     *         {@code false} if the error handling shall be continued.
     */
    boolean onErrorResponse(ErrorResponseException ex, HttpServletRequest req, HttpServletResponse resp);

}
