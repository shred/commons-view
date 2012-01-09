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

package org.shredzone.commons.view.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A view handler method is annotated with {@link View}.
 * <p>
 * If a handler method is able to handle multiple views, {@link ViewGroup} is used
 * instead to group several {@link View} annotations.
 *
 * @author Richard "Shred" Körber
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface View {

    /**
     * View name. If unset, a view name is generated from the method name.
     */
    String name() default "";

    /**
     * View pattern. This is the view's url with additional placeholders.
     */
    String pattern();

    /**
     * View signature. If unset, the signature is automatically generated from the pattern
     * placeholders.
     */
    String[] signature() default {};

}
