# specialization-plugin
Plugin for Archi to specialize concepts (figure, icon...).

It allows to replace the standard icon in Archi's elements by any image that you provide.

At the moment, the plugin is in beta state. Please do not hesitate to open issues if you discover bugs or require new functionality.

This plugin is compatible with all my others plugins and with the GitHub persistance plugin. People who open a model but who do not have the specialization images (or even do not have the specialization plugin) will just see the standard icons.

#### Installation procedure
To install the plugin:
1. download the .jar file to Archi's **_plugins_** folder
2. go to Archi's **_plugins/com.archimatetool.editor_4.0.3.201707201522/img_** folder and copy all the image files you wish (I would recommand to create sub folders)

#### Configuring the plugin
On Archi's preferences pages, you can :
1. check and automatically download and install for new versions of the plugin
2. choose whether the icons may be changes in Archi's views
3. choose whether the icons may be changes in Archi's model tree
4. create a log file (for debugging purpose)


#### Changing components icons
Create a property called **_icon_** on an element and give the path of your image file from the img folder

For instance, if you've got a file plugins/com.archimatetool.editor_4.0.3.201707201522/img/logistic/factory.png, simply write logistic/factory.png

The following image format are supported:
* JPG
* PNG 
* GIF
* BMP

Transparent colors are supported when one is specified in the image file.

![proof of concept](https://user-images.githubusercontent.com/9281982/29636398-b9bebcd6-8850-11e7-8abf-83915abdfde8.png)
