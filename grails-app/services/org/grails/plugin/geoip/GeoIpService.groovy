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

    static List<String> ipHeaders = ['X-Real-IP',
                                     'Client-IP',
                                     'X-Forwarded-For',
                                     'Proxy-Client-IP',
                                     'WL-Proxy-Client-IP',
                                     'rlnclientipaddr']
    def geoLookupService

    def getLocation(def ip) {
        geoLookupService.getLocation(ip)
    }

    String getIpAddress(request) {
        String unknown = 'unknown'
        String ipAddress = unknown

        ipHeaders.each { header ->
            if (!ipAddress || unknown.equalsIgnoreCase(ipAddress))
                ipAddress = request.getHeader(header)
        }

        if (!ipAddress)
            ipAddress = request.remoteAddr

        return ipAddress
    }

    boolean isInCountry(Location location, String countryCode) {
        location.countryCode?.equals(countryCode)
    }

}
