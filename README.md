# specialization-plugin
Plugin for Archi to specialize concepts (figure, icon...)

At the moment, the plugin is in _proof of concept_ state. This means that it implements a very small subset of the final functionalities in order to show the feasibility.

#### Installation procedure
At the moment, the installation procedure is manual:
1. download the jar file and copy it to the Archi **_plugins_** folder
2. download the sources/util/FontAwesome/images folder, copy it to the **_plugins/com.archimatetool.editor_4.0.0.201705031219/img_** folder and rename it FonteAwesome

#### Configuring the plugin
At the moment, there is no configuration except the ability to create a log file. Open Archi preference pages (edit/Preferences menu) and select the **_logger_** tab on the **_specialization plugin_** page.

#### Changing components icons
Create a property called **_icon_** on a Business Actor or any element from the Technology Layer. The property value must be the name of one image file from the FontAwesome folder (without the extension):
![fontawesome](https://user-images.githubusercontent.com/9281982/27751630-8604ded6-5ddd-11e7-8595-b72968d956b1.png)
