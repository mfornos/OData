/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2011 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2011 Sartini IT Solutions.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package play.modules.odata;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import play.exceptions.UnexpectedException;
import play.mvc.Http;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerListener;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseWriter;
import com.sun.jersey.spi.container.WebApplication;

/**
 * This is the container that handles all HTTP requests. Requests are adapted
 * for the enclosed {@link WebApplication} instances.
 * 
 * Large parts are taken from the SimpleContainer from Marc Hadley.
 * 
 * @author Marc.Hadley@Sun.Com
 * @author Piero Sartini
 */
public final class PlayContainer implements ContainerListener {

    private final static class Writer implements ContainerResponseWriter {
        final Http.Response response;
        @SuppressWarnings("unused")
        final Http.Request request;

        Writer(Http.Request request, Http.Response response) {
            this.response = response;
            this.request = request;
        }

        public void finish() throws IOException {
            response.out.close();
        }

        public OutputStream writeStatusAndHeaders(long contentLength, ContainerResponse cResponse) throws IOException {
            response.status = cResponse.getStatus();

            for (Map.Entry<String, List<Object>> e : cResponse.getHttpHeaders().entrySet()) {
                for (Object value : e.getValue()) {
                    response.setHeader(e.getKey(), ContainerResponse.getHeaderValue(value));
                }
            }

            String contentType = response.getHeader("Content-Type");
            if (contentType != null) {
                response.contentType = contentType;
            }

            return response.out;
        }
    }

    private WebApplication application;

    public PlayContainer(WebApplication application) throws ContainerException {
        this.application = application;
    }

    public void handle(Http.Request request, Http.Response response) {
        WebApplication target = application;

        try {
            final URI baseUri = new URI(request.getBase() + ODataPlugin.contextPath);
            final URI requestUri = baseUri.resolve(request.url);

            final ContainerRequest cRequest = new ContainerRequest(target, request.method, baseUri, requestUri,
                    getHeaders(request), request.body);

            target.handleRequest(cRequest, new Writer(request, response));
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        } finally {
            close(response);
        }
    }

    @Override
    public void onReload() {
        WebApplication oldApplication = application;
        application = application.clone();
        oldApplication.destroy();
    }

    private void close(Http.Response response) {
        try {
            response.out.close();
        } catch (Exception ex) {
            throw new UnexpectedException(ex);
        }
    }

    private InBoundHeaders getHeaders(Http.Request request) {
        InBoundHeaders header = new InBoundHeaders();

        for (Map.Entry<String, Http.Header> e : request.headers.entrySet()) {
            for (String h : e.getValue().values) {
                header.add(e.getKey(), h);
            }
        }
        return header;
    }
}
