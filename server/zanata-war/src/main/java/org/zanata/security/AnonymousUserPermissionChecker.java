/*
 * Copyright 2016, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.security;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.apache.deltaspike.jsf.api.listener.phase.JsfPhaseListener;
import org.zanata.ApplicationConfiguration;
import org.zanata.exception.NotLoggedInException;

/**
 * This JSF phase listener will check permissions if the anonymous user is not
 * allowed to view resources.
 *
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@ApplicationScoped
@JsfPhaseListener
public class AnonymousUserPermissionChecker implements PhaseListener {
    private ApplicationConfiguration appConfig;
    private ZanataIdentity identity;

    private HttpServletRequest request;

    @Inject
    public AnonymousUserPermissionChecker(ApplicationConfiguration appConfig,
            ZanataIdentity identity, @DeltaSpike HttpServletRequest request) {
        this.appConfig = appConfig;
        this.identity = identity;
        this.request = request;
    }

    @SuppressWarnings("unused")
    AnonymousUserPermissionChecker() {
    }

    @Override
    public void afterPhase(PhaseEvent phaseEvent) {
    }

    @Override
    public void beforePhase(PhaseEvent phaseEvent) {
        if (!requestingPageIsSignInOrRegister() &&
                anonymousAccessIsNotAllowed()) {
            throw new NotLoggedInException();
        }
    }

    private boolean anonymousAccessIsNotAllowed() {
        return !appConfig.isAnonymousUserAllowed() && !identity.isLoggedIn();
    }

    private boolean requestingPageIsSignInOrRegister() {
        // the request URI will be the internal URI
        return request.getRequestURI().contains("account/login.xhtml") ||
                request.getRequestURI().contains("account/register.xhtml");
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
