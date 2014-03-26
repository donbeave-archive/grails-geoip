/* Copyright 2009 Lightway Software SRL. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.grails.plugin.geoip

import com.maxmind.geoip.Location

/**
 * Service for determining a geographical location based on an IP. 
 *
 * @author Radu Andrei Tanasa
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
public class GeoIpService {

    def geoLookupService

    def getLocation(def ip) {
        geoLookupService.getLocation(ip)
    }

    def getIpAddress(request) {
        def ipAddress = request.getHeader('X-Real-IP')

        if (!ipAddress)
            ipAddress = request.getHeader('Client-IP')

        if (!ipAddress)
            ipAddress = request.getHeader('X-Forwarded-For')

        if (!ipAddress)
            ipAddress = request.remoteAddr

        return ipAddress
    }

    boolean isInCountry(Location location, String countryCode) {
        location.countryCode?.equals(countryCode)
    }

}
