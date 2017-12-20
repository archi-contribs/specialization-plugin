# specialization-plugin
Plugin for Archi to specialize concepts (figure, icon...).

It allows to replace icons and labels of Archi components.

This plugin is compatible with all my others plugins and with the GitHub persistance plugin. People who open a model but who do not have the specialization images (or even do not have the specialization plugin) will just see the standard icons.

#### Installation procedure
To install the plugin, just download the .jar file to Archi's **_plugins_** folder and (re)start Archi.

#### Configuring the plugin
On Archi's preferences pages, you can :
1. check and automatically download and install for new versions of the plugin
2. choose whether the icons may be changes in Archi's views and model tree
3. choose whether the icons may be changes in Archi's model tree
4. specify the folders that contain images that can be used by the plugin
5. create a log file (for debugging purpose)


#### Changing components icons
Create a property called **_icon_** on an element and give the path of your image file from the img folder
You may change the icon size using a property called **_icon size_** with "width x height" format or "auto" value to fit to the element's rectangle.
You may change the icon location using a property called **_icon location_** with "x , y" format or "center" value to center the image in the element's rectangle

For instance, if you've got a file plugins/com.archimatetool.editor_4.0.3.201707201522/img/logistic/factory.png, simply write logistic/factory.png

The following image format are supported:
* JPG
* PNG 
* GIF
* BMP

Transparent colors are supported when one is specified in the image file.

![proof of concept](https://user-images.githubusercontent.com/9281982/29636398-b9bebcd6-8850-11e7-8abf-83915abdfde8.png)
