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

package org.shredzone.commons.view.util;

import org.shredzone.commons.view.PathContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * An {@link EvaluationContext} that offers additional string functions.
 * <p>
 * <dl>
 *   <dt><code>simplify</code></dt>
 *   <dd>simplifies a unicode string so it can be used in URLs (see {@link PathUtils#simplify(String)})</dd>
 *
 *   <dt><code>suffix</code></dt>
 *   <dd>suggests a suffix for a content type (see {@link PathUtils#suffix(String)})</dd>
 *
 *   <dt><code>encode</code></dt>
 *   <dd>url encodes a string (see {@link PathUtils#encode(String)})</dd>
 * </dl>
 *
 * @author Richard "Shred" Körber
 */
public class ViewPathEvaluationContext extends StandardEvaluationContext {

    /**
     * Instantiates a new view path evaluation context.
     *
     * @param context
     *            properties and variables to be used
     */
    public ViewPathEvaluationContext(PathContext context) {
        super(context);
        try {
            init();
            setVariables(context.getVariables());
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Exception while creating context", ex);
        }
    }

    /**
     * Initializes the evaluation context. Subclasses may override this method to register
     * more functions.
     */
    protected void init() throws NoSuchMethodException {
        registerFunction(
                "simplify",
                PathUtils.class.getDeclaredMethod("simplify", new Class[] { String.class })
        );
        registerFunction(
                "suffix",
                PathUtils.class.getDeclaredMethod("suffix", new Class[] { String.class })
        );
        registerFunction(
                "encode",
                PathUtils.class.getDeclaredMethod("encode", new Class[] { String.class })
        );
    }

}
