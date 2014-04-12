/* Copyright 2009-2013 the original author or authors.
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

import com.maxmind.geoip.LookupService
import grails.util.Environment
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsApplication

/**
 * @author Radu Andrei Tanasa
 * @author <a href='mailto:donbeave@gmail.com'>Alexey Zhokhov</a>
 */
class GeoipGrailsPlugin {

    def version = '0.3.2'
    def grailsVersion = '2.0 > *'
    def dependsOn = [:]
    def pluginExcludes = [
            'grails-app/views/error.gsp'
    ]

    def title = 'Grails GeoIP Plugin'
    def author = 'Radu Andrei Tanasa, Alexey Zhokhov'
    def authorEmail = 'radu.tanasa@lightwaysoftware.com'
    def description = '''\\
This plugin facilitates grails integration with the opensource GeoIP framework offered by MaxMind.
Using its straightforward API one can find out the country, area, city, geographical coordinates and 
others based on an IP.

This product includes GeoLite data created by MaxMind, available from
[www.maxmind.com|http://www.maxmind.com].
'''

    def documentation = 'http://grails.org/plugin/geoip'

    def license = 'LGPL2'

    def developers = [
            [name: 'Radu Andrei Tanasa', email: 'radu.tanasa@lightwaysoftware.com'],
            [name: 'Alexey Zhokhov', email: 'donbeave@gmail.com']
    ]

    def issueManagement = [system: 'GITHUB', url: 'https://github.com/donbeave/grails-geoip/issues']
    def scm = [url: 'https://github.com/donbeave/grails-geoip/']

    def doWithSpring = {
        def conf = getConfiguration(application)

        if (!conf || !conf.active) {
            return
        }

        boolean printStatusMessages = (conf.printStatusMessages instanceof Boolean) ? conf.printStatusMessages : true

        if (printStatusMessages) {
            println '\nConfiguring MaxMind GeoIP ...'
        }

        def dataResource = conf.data.path

        try {
            if (conf.data.resource) {
                dataResource = application.parentContext.getResource(conf.data.resource).getFile()
            }
        } catch (Exception e) {
            println "ERROR: GeoIP data file \"${conf.data.resource}\" not exist."
            return;
        }

        if (!dataResource) {
            println 'ERROR: GeoIP data file not installed.'
            return;
        }

        /** geoLookupService */
        geoLookupService(LookupService, dataResource, conf.data.cache ?:
                (LookupService.GEOIP_MEMORY_CACHE | LookupService.GEOIP_CHECK_CACHE))

        if (printStatusMessages) {
            println '... finished configuring MaxMind GeoIP\n'
        }
    }

    def doWithDynamicMethods = { ctx ->
        def conf = application.config.grails.plugin.geoip

        if (!conf || !conf.active) {
            return
        }

        for (cc in application.controllerClasses) {
            addDynamicMethods cc.clazz
        }
    }

    def onChange = { event ->
        def conf = application.config.grails.plugin.geoip

        if (!conf || !conf.active) {
            return
        }

        for (cc in application.controllerClasses) {
            addDynamicMethods cc.clazz
        }
    }

    // Get a configuration instance
    private getConfiguration(GrailsApplication application) {
        def config = application.config

        // try to load it from class file and merge into GrailsApplication#config
        // Config.groovy properties override the default one
        try {
            Class dataSourceClass = application.getClassLoader().loadClass('DefaultGeoConfig')
            ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
            Map binding = [:]
            binding.userHome = System.properties['user.home']
            binding.grailsEnv = application.metadata['grails.env']
            binding.appName = application.metadata['app.name']
            binding.appVersion = application.metadata['app.version']
            configSlurper.binding = binding

            ConfigObject defaultConfig = configSlurper.parse(dataSourceClass)

            ConfigObject newGeoConfig = new ConfigObject()
            newGeoConfig.putAll(defaultConfig.geoip.merge(config.grails.plugin.geoip))

            config.grails.plugin.geoip = newGeoConfig
            application.configChanged()
            return config.grails.plugin.geoip
        } catch (ClassNotFoundException e) {
            log.debug("GeoConfig default configuration file not found: ${e.message}")
        }

        // Here the default configuration file was not found, so we
        // try to get it from GrailsApplication#config and add some mandatory default values
        if (config.grails.plugin.containsKey('geoip')) {
            if (config.grails.plugin.geoip.active == [:]) {
                config.grails.plugin.geoip.active = true
            }
            if (config.grails.plugin.geoip.printStatusMessages == [:]) {
                config.grails.plugin.geoip.printStatusMessages = true
            }
            application.configChanged()
            return config.grails.plugin.geoip
        }

        // No config found, add some default and obligatory properties
        config.grails.plugin.geoip.active = true
        config.grails.plugin.geoip.printStatusMessages = true
        application.configChanged()
        return config
    }

    private void addDynamicMethods(klass) {
        klass.metaClass.withLocation = { Closure closure ->
            def geoIpService = Holders.applicationContext.geoIpService
            closure.call geoIpService.getLocation(geoIpService.getIpAddress(request))
        }

        klass.metaClass.isInCountry = { String countrycode ->
            def geoIpService = Holders.applicationContext.geoIpService
            def location = geoIpService.getLocation(geoIpService.getIpAddress(request))

            if (location) {
                return geoIpService.isInCountry(location, countrycode)
            } else {
                return false
            }
        }
    }
}
