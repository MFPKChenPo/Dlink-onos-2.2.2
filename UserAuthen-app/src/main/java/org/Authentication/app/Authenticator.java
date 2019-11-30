/*
 * Copyright 2019-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.Authentication.app;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;

import org.tinyradius.packet.*;
import org.tinyradius.util.*;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class Authenticator {
    public Authenticator() {
        rc = createRadiusClient();
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationId appId;

    private String host = "127.0.0.1";
    private String NASpassword = "testing123";
    private RadiusClient rc;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ComponentConfigService cfgService;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Activate
    protected void activate() {
        // cfgService.registerProperties(getClass());
        appId = coreService.registerApplication("org.authentication.app");
        log.info("User Authentication App Started",appId.id());
    }

    @Deactivate
    protected void deactivate() {
        // cfgService.unregisterProperties(getClass(), false);

        log.info("User Authentication App Stopped");
    }

    public String authWithRadius(AccessRequest auth_ar) {
        log.info("Packet before it is sent\n" + auth_ar + "\n");
        RadiusPacket response = null;
        try {
            log.info("Packet to RADIUS server.");
            if (rc == null)
                log.info("RadiusClient is null.");
            response = rc.authenticate(auth_ar);
        } catch (IOException e) {
            log.info("Exception from IO.");
            e.printStackTrace();
        } catch (RadiusException e) {
            log.info("Exception from RADIUS.");
            e.printStackTrace();
        }
        log.info("Packet aggter iw was sent\n" + auth_ar + "\n");
        log.info("Response\n" + response.getPacketTypeName() + "\n");
        rc.close();

        return response.getPacketTypeName();
    }

    public RadiusClient createRadiusClient() {
        log.info("Radius Client created!!");
        return new RadiusClient(host, NASpassword);
    }

    public AccessRequest createRequest(String myUser, String myPass) {
        AccessRequest thisAr = new AccessRequest(myUser, myPass);
        log.info("Access Request created!!");
        thisAr.setAuthProtocol("pap");
        thisAr.addAttribute("NAS-Identifier", "My localhost NAS~~");
        thisAr.addAttribute("NAS-IP-Address", "127.0.1.1");
        thisAr.addAttribute("Service-Type", "Login-User");
        return thisAr;
    }
}
