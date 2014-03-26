/* Copyright 2013 the original author or authors.
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

/**
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
includeTargets << grailsScript('_GrailsEvents')
includeTargets << grailsScript('_GrailsBootstrap')

geoPath = '/data/maxmind'
geoLiteSources = "${basedir}/web-app${geoPath}"
appDir = "$basedir/grails-app"

target(installGeoLite: 'Downloads GeoLite from dev.maxmind.com') {
    event('StatusUpdate', ['Downloading GeoLite'])

    mkdir(dir: "${geoLiteSources}")

    delete(file: "${geoLiteSources}/GeoLiteCity.dat")

    get(dest: "${geoLiteSources}/GeoLiteCity.dat.gz",
            src: 'http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz',
            verbose: true)

    gunzip(src: "${geoLiteSources}/GeoLiteCity.dat.gz")

    delete(file: "${geoLiteSources}/GeoLiteCity.dat.gz")

    updateConfig()

    event('StatusFinal', ['GeoLite installed successfully'])
}

private void updateConfig() {
    def configFile = new File(appDir, 'conf/Config.groovy')
    if (configFile.exists()) {
        configFile.withWriterAppend {
            it.writeLine '\n// Added by the GeoIP plugin:'
            it.writeLine "grails.plugin.geoip.data.resource = '${geoPath}/GeoLiteCity.dat'"
        }
    }
}

setDefaultTarget 'installGeoLite'
