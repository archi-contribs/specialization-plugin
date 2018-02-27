* v1.0.2 (27/02/2017)
  * Use CompoundCommands to change property values to allow undo/redo
  * Manage adapters (notifications) to trap property update
  * Changing the label of an element or a relationship now sets the model's dirty flag 

* v1.0.1 (08/01/2017)
  * Replace "\n" string by newline in labels
  * Expand variables ${name}, ${id}, ${property:xxx}; ...

* v1.0 (19/12/2017)
  * Better integration into Archi
  * The image files can now be located outside Archi's editor folder
  * The icon and label names can now contain variables (i.e. references to other properties)
  * Add the ability to change the images size
  * Add the ability to change the image location into the elements' rectangles 
  * Rewrite debug and trace messages
  * Update inline help
  * Various Fixes:
    * Fix elements shape (square corners instead of round ones)
    * Fix junction shape
    * Do not replace the icon in the properties window anymore

* v0.3 (07/12/2017)
  * Add the ability to change the name of the property that contains the icon filename
  * Add the ability to change the name of the property that contains the label text
  * Add debug and trace messages
  * Add a context menu to refresh the icon of all the elements of the model
	
* v0.2 (12/10/2017)
  * Icon replacement:
    * Increase the number of supported elements
    * Add an option on the preference page to use customized icons on all the views of those that have a "change icons" property set to "true"
    * Add a context menu to switch icons from Archi's standard icons to customized ones and back
  * Icon replacement:
    * Add new functionality to change label on relationships and elements
    * Add an option on the preference page to use customized icons on all the views of those that have a "change labels" property set to "true"
    * Add a context menu to switch icons from Archi's standard labels to customized ones and back
    * Add context menu "refresh view" to refresh the labels as they do not refresh automatically
		
* v0.1 (24/08/2017)
  * First beta version
  * Icons can be changed on few technical elements using the "icon" property