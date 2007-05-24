/*
 * Copyright (C) 2006-2007 Mindquarry GmbH, All Rights Reserved
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package com.mindquarry.auth.cocoon;

/**
 * @author <a href="mailto:alexander(dot)klimetschek(at)mindquarry(dot)com">
 *         Alexander Klimetschek</a>
 *
 */
public class AuthorizationException extends RuntimeException {

    private static final long serialVersionUID = -6887647765941173457L;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable t) {
        super(message, t);
    }

    public AuthorizationException(Throwable t) {
        super(t);
    }

}
