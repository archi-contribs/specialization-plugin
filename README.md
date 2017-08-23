# specialization-plugin
Plugin for Archi to specialize concepts (figure, icon...).

It allows to replace the standard icon in Archi's elements by any image that you provide.

At the moment, the plugin is in _proof of concept_ state. This means that it implements a very small subset of the final functionalities in order to show the feasibility.

It is important to note that the plugin does not change the model. People who open a model but who do not have the specialization images (or even do not have the specialization plugin) will just see the standard icons.

This plugin is compatible with all my others plugins and with the GitHub persistance plugin.

#### Installation procedure
To install the plugin:
1. download the .jar file to Archi's **_plugins_** folder
2. go to Archi's **_plugins/com.archimatetool.editor_4.0.3.201707201522/img_** folder and copy all the image files you wish (I would recommand to create sub folders)

#### Configuring the plugin
At the moment, there is no configuration except the ability to create a log file. Go to Archi's preferences pages (edit/Preferences menu) and select the **_logger_** tab on the **_specialization plugin_** page.

#### Changing components icons
Create a property called **_icon_** on an element and give the path of your image file from the img folder

For instance, if you've got a file plugins/com.archimatetool.editor_4.0.3.201707201522/img/logistic/factory.png, simply write logistic/factory.png

The following image format are supported:
* JPG
* PNG 
* GIF
* BMP

Transparent colors are supported when one is specified in the image file.

Please note that the icon cannot yet be changed on all the elements (but at least, Technology, application and business layer are done).

Here is an example of what can be acheived.
