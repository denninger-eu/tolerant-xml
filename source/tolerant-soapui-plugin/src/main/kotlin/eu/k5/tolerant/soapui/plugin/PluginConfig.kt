package eu.k5.tolerant.soapui.plugin

import com.eviware.soapui.plugins.PluginAdapter
import com.eviware.soapui.plugins.PluginConfiguration


@PluginConfiguration(groupId = "eu.k5", name = "Tolerant Reader", version = "1.0.0", autoDetect = true, description = "TolerantReader Plugin to migrate and repair xml requests", infoUrl = "http://olensmar.blogspot.com/2014/07/getting-started-with-new-soapui-plugin.html")
class PluginConfig : PluginAdapter()