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

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

/**
 * A signature is a hash key for a set of String elements. It is ensured that the same
 * collection of Strings results in an equal signature object, irregarding of the string
 * order.
 * <p>
 * Signatures are immutable.
 *
 * @author Richard "Shred" Körber
 */
public final class Signature {

    private final String signature;

    /**
     * Instantiates a new signature.
     *
     * @param elements
     *            collection of strings to build the signature for
     */
    public Signature(Collection<String> elements) {
        StringBuilder sb = new StringBuilder();
        for (String elem : new TreeSet<String>(elements)) {
            if (elem != null && !elem.isEmpty()) {
                sb.append('|').append(elem);
            }
        }
        this.signature = sb.toString();
    }

    /**
     * Instantiates a new signature.
     *
     * @param elements
     *            array of strings to build the signature for.
     */
    public Signature(String[] elements) {
        this(Arrays.asList(elements));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Signature)) {
            return false;
        }
        return ((Signature) obj).signature.equals(signature);
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    public String toString() {
        return (signature.isEmpty() ? "" : signature.substring(1));
    }

}
