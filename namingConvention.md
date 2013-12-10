# Android Naming Convention

The purpose of the Android Naming Convention is to create a collaboration baseline; helpful for scenarios where many people are creating, modifying, and contributing to the same project. The end goal is unity, consistency, and the notion that a single entity worked on the project.

//Edited based on 47Degrees naming convention


**Table of Contents**  

- [Android Naming Convention](#android-coding-standards)
	- [1. Naming Conventions](#1-naming-conventions)
		- [1.1. Common Resource Files](#11-common-resource-files)
		- [1.2. Java Packages & Class Names](#12-java-packages--class-names)
		- [1.3. Resource Names](#13-resource-names)
		- [1.4. String Resources](#14-string-resources)
		- [1.5. Style Resources](#15-style-resources)
 		- [1.6. Dimen Resources](#16-dimen-resources)
 		- [1.7. Layout Resources Names](#17-layout-resource-names)
	- [2. Conventions for devices 7 inches](#2-conventions-for-devices-7-inches)

##<a id="1-naming-conventions">1. Naming Conventions</a>

Developers should pay special attention to these naming conventions. 
###<a id="11-common-resource-files">1.1. Common Resource Files</a>


The folder *values* will have different files that will store information for our project. 
Some of the most common files and their name are

* **colors.xml**: Colors used in the application
* **config.xml**: Stores information to configure our project (ex. keys for services, urls, etc)
* **dimen.xml**: Dimensions used in application (ex. action bar height , paddings, etc)
* **strings.xml**: Localizable strings
* **plurals.xml**: Plurals. Contains references to strings.xml
* **arrays.xml**: Arrays. Contains references to strings.xml 

###<a id="12-java-packages--class-names">1.2. Java Packages & Class Names</a>

An android App should generally follow the following package structure

* **com.company.product**
	- **ui**: Packages that contains every component directly implements UI tasks.
        - **activities**: All Activities .With name-convention: *[Name]*Activity e.g. MainActivity
        - **views**: All Android custom views. with name-convention: *[Name]*View e.g. HomeView
        - **dialogs**: All Android Dialogs. with name-convention: *[Name]*Dialog e.g. QuestionDialog
        - **fragments**: All Android Fragments. with name-convention: *[Name]*Fragment e.g. QuestionFragment
	    - **adapters**: All Adapters . With name conventions: *[Name]*Adapter e.g. UserListAdapter
	- **services**: All Services including API clients and other persistence related services e.g. UserService
	- **remote**: The package contains all components that directly connect to outside enviroment e.g internet connection
	- **database**: The packages contains all components that work with database
	- **models**: Data models, with naming convention: [name]*Data e.g MyUserData
	- **utils**: All cross package utilities with the word Utils pre-fixed by the Utility name: *[Name]*Utils e.g. StringUtils

###<a id="13-resource-names">1.3. Resource Names</a>
The following structure should be followed when naming resoures.

**group** _ **type** _ **name** _ *[state]* _ *[suffix]*

* **group**: Application area or screen. If the resource is used in different parts of applications 'common' should be used instead. e.g. actionbar, menu, media, popup, footer, audio, etc.
* **type**: Resource Type. e.g. background, icon, button, textfield, list, menuitem, radiobutton, checkbox, tab, dialog, title, etc.
* **name**: Descriptive name as to what the resource is about. e.g. play, stop. 
* **state**: (Optional): The optional state of a parent resource. e.g. A button could be in 'normal', 'pressed', 'disabled' and 'selected'. A checkbox could be 'on' or 'off'. These resources should NEVER be used directly in layout but rather as state selectors.
* **suffix**: (Optional): An arbitrary suffix that helps to further identify a property of the resource. e.g. bright, dark, opaque, layer.

Below are some examples of properly named resources.

* common_background_app
* audio_icon_play_on
* common_icon_preferences
* actionbar_selector_send (XML resource)
	- action_button_send_normal
	- action_button_send_pressed
	- action_button_send_disabled


###<a id="14-string-resources">1.4. String Resources</a>
String resources placed in xml resources files such as strings.xml, config.xml, etc. 

Below are some examples of properly named string identifiers.

* server_api_url
* phone_number
* services
* url

###<a id="15-style-resources">1.5. Style Resources</a>
The following structure should be followed when naming style resoures.

**group** _ **type** _ **name** _ *[suffix]*

* **group** (Optional): Application area or screen. e.g. actionbar, menu, media, popup, footer, audio.
* **type**: Resource Type. e.g. Background, Icon, Button, Textfield, List, MenuItem, RadioButton, Checkbox, Tab, Dialog.
* **name**: Descriptive name as to what the resource is about. e.g. play, stop.
* **suffix**: (Optional): An arbitrary suffix that helps to further identify a property of the resource. e.g. Bright, Dark, Opaque, Layer.

Below are some examples of properly named string identifiers.

* button_send
* actionbar_button_back
* list_title

###<a id="16-dimen-resources">1.6. Dimen Resources</a>
Dimens resources placed in dimen.xml. The following structure should be followed when naming dimentions.

**property** _ **default** _ **group** _ **type** _ **name**

* **property**: Type of property reference. e.g. font_size, padding, margin, height, width.
* **default** (Optional): Write "default" if is a general dimen.
* **group** (Optional): Application area or screen. e.g. action_bar, menu, popup, wizard.
* **type** (Optional): Type of resource. e.g. button, title, text, edittext.
* **name** (Optional): Only if is necessary.

Below are some examples.

* padding_default
* font_size_action_bar_button
* height_default_action_bar

We should have some dimension in all projects by default. These are:

* padding_default
* margin_default
* font_size_default_button
* font_size_default_title
* font_size_default_text
* height_default_action_bar
* font_size_default_action_bar

###<a id="17-layout-resource-names">1.7. Layout Resource Names</a>
The following structure should be followed when naming resoures.

**name** _ **type**

* **type**: Layout resource Type. e.g. activity, view,dialog, listitem,.
* **name**: Descriptive name defines where or how the layout is used e.g main, note_detail...

Below are some examples of properly named resources.

* home_activity
* note_detail_custom_view
* note_question_dialog
* note_list_item_view

##<a id="2-conventions-for-devices-7-inches">2. Conventions for 7" devices</a>

7" devices require a special treatment. The problem is that these devices usually use "mdpi" as default density.

These are the consideration to follow when targeting apps in these devices

* Copy hdpi drawables to "drawable-sw600dp-mdpi".
* Create a new dimensions file for this screens. The file "dimen.xml" will be in the "values-sw600dp-mdpi" folder. Usually the dimensions will be at 150%. All fonts sizes should be in dimen.xml file as well.
