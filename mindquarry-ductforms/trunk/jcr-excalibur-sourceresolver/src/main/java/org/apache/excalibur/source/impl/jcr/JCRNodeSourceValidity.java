/*
 * Copyright 2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.source.impl.jcr;

import javax.jcr.Value;

import org.apache.excalibur.source.SourceValidity;

/**
 * Validity of a {@link JCRNodeSource}. It's a wrapper around a JCR
 * <code>Value</code>.
 *
 * @version $Id: JCRNodeSourceValidity.java 240329 2005-08-26 20:04:15Z vgritsenko $
 */
public class JCRNodeSourceValidity implements SourceValidity {

    private Value value;

    public JCRNodeSourceValidity(Value value) {
        this.value = value;
    }

    public int isValid() {
        // Don't know, need another validity to compare with
        return 0;
    }

    public int isValid(SourceValidity other) {
        if (other instanceof JCRNodeSourceValidity) {
            // compare the two values
            return ((JCRNodeSourceValidity) other).value.equals(this.value) ? 1 : -1;
        } else {
            // invalid
            return -1;
        }
    }
}
