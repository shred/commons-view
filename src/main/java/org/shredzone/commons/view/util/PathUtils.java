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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Utility methods for view path management.
 *
 * @author Richard "Shred" Körber
 */
@ParametersAreNonnullByDefault
public final class PathUtils {

    private PathUtils() {
        // Utility class without constructor
    }

    /**
     * Simplifies a path part. The resulting string only contains numbers ([0-9]) and
     * lowercase characters ([a-z]). One ore more consecutive whitespaces or a few
     * non-ascii characters are converted into a single dash '-'. All other characters are
     * either converted to ASCII characters, or removed.
     * <p>
     * This method can be used to convert e.g. titles into URL parts, for search engine
     * optimization.
     * <p>
     * On accented characters, the accent is removed. However, currently German umlauts
     * are converted into their respective ASCII counterparts ('ä' -&gt; 'ae'). Future
     * implementations may also contain translations for other language's accented
     * characters.
     * <p>
     * Consider this method as one-way encoding. Future releases may return different
     * strings.
     *
     * @param part
     *            path part to simplify
     * @return simplified path part
     */
    public static @Nonnull String simplify(String part) {
        StringBuilder result = new StringBuilder(part.length());

        boolean lastWasDash = false;

        for (char ch : part.toLowerCase().toCharArray()) {
            if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')) {
                result.append(ch);
                lastWasDash = false;

            } else if (ch == ' ' || ch == '+' || ch == '-' || ch == '_' || ch == '&') {
                if (!lastWasDash) {
                    result.append('-');
                }
                lastWasDash = true;

            } else if (ch >= 128) {
                // TODO: German-centric... Is there an international implementation?
                switch (ch) {
                    case 'ä':
                    case 'Ä':
                        result.append("ae");
                        lastWasDash = false;
                        break;

                    case 'ö':
                    case 'Ö':
                        result.append("oe");
                        lastWasDash = false;
                        break;

                    case 'ü':
                    case 'Ü':
                        result.append("ue");
                        lastWasDash = false;
                        break;

                    case 'ß':
                        result.append("ss");
                        lastWasDash = false;
                        break;

                    default:
                        String normalized = Normalizer.normalize(Character.toString(ch), Normalizer.Form.NFKD);
                        for (char nch : normalized.toLowerCase().toCharArray()) {
                            if (Character.isLetterOrDigit(nch)) {
                                lastWasDash = false;
                                result.append(nch);
                            }
                        }
                }
            }
        }

        return result.toString();
    }

    /**
     * Suggests a file name suffix for the given content type.
     * <p>
     * The current implementation only detects the standard HTML image types.
     *
     * @param mime
     *            content type to find a suffix for
     * @return suggested suffix, or "bin" if there is no known suffix
     */
    public static @Nonnull String suffix(String mime) {
        // Prominent Mime Types
        switch (mime) {
            case "image/png":     return "png";
            case "image/jpeg":    return "jpg";
            case "image/gif":     return "gif";
            case "image/svg+xml": return "svg";
            case "image/tiff":    return "tif";
        }

        // Try to guess
        Matcher m = Pattern.compile("^.*?/(.{1,6}?)(\\+.*)?$").matcher(mime);
        if (m.matches()) {
            return m.group(1);
        }

        // Is it a text?
        if (mime.startsWith("text/")) {
            return "txt";
        }

        // Fallback to bin
        return "bin";
    }

    /**
     * URL encodes a string. utf-8 charset is used for encoding.
     * <p>
     * This is a convenience call of {@link URLEncoder#encode(String, String)} with
     * exception handling.
     *
     * @param string
     *            string to be URL encoded
     * @return encoded string
     */
    public static @Nonnull String encode(String string) {
        return URLEncoder.encode(string, StandardCharsets.UTF_8);
    }

    /**
     * URL decodes a string. utf-8 charset is used for decoding.
     * <p>
     * This is a convenience call of {@link URLDecoder#decode(String, String)} with
     * exception handling.
     *
     * @param string
     *            the string to be URL decoded
     * @return decoded string
     */
    public static @Nonnull String decode(String string) {
        return URLDecoder.decode(string, StandardCharsets.UTF_8);
    }

}
