RuneScape Timer
========

![image](https://cloud.githubusercontent.com/assets/2666891/8512872/436d4bbc-230b-11e5-9900-aa71b9c9465f.png)
(image currently out of date)
### About
For version 0.14.*

A simple timer program originally intended for, but not limited to, use for timing RuneScape dailies and farm runs. Displays a window with customizable timers shown with progress bars. Includes "duration from starting point" timers and daily/weekly timers, that reset at UTC 0:00 / RS daily and weekly reset times. Timers can be clicked to reset their time remaining to the length of the timer (or to the next tick for dailies/weeklies).

### Operations
- Left clicking on a timer's progress bar will reset its progress
- Right clicking on a timer's progress bar will set its status to complete
- Shift-left clicking on a timer's progress bar allows you to edit some settings of that timer
- Clicking the + button shows a GUI to a new timer in the current tab
- Shift-clicking the + button shows GUIs to add a new tab
- Clicking the - button (setting it to selected) then clicking a timer's bar will remove that timer
- Shift-clicking the - button will ask if you want to delete the current tab (and its timers)
- Clicking the pin icon will toggle whether the window stays always-on-top
- Adjusting the slider sets the transparency of the window. The minimum is 5%. This is saved when reopening the program


Please note that features may be added, changed, or removed at any time. Also, no guarantee is given on the lack of existence of bugs. Information here is based on the listed version and may not match newer or older versions.



### Configuration/Syntax
##Properties file
Several settings can be configured via the properties file. The settings file can be refreshed to defaults by deleting it. The order these appear in may not be maintained/consistent.
setting=default value | possible values... [Explanation]
logLevel=INFO | ALL|FINEST,FINER,FINE,CONFIG,INFO,WARNING,SEVERE,NONE [Values closer to ALL generate more information in the console]
transparency=1.0	| 0.0 to 1.0 		[Transparency value of the gui as a double decimal]
winWidth=400		| 0 to 2.1 billion	[Window width]
winHeight=205		| 0 to 2.1 billion	[Window height]
defaultTabColumns=0	| 0 to 2.1 billion	[number of default tab rows if no tabs exist. Imports from cfg,gridRows]
defaultTabRows=5	| 0 to 2.1 billion	[number of default tab rows if no tabs exist. Imports from cfg,gridRows]
defaultTabName=Main	| non-empty-string	[Name of the first tab if it is imported or does not exist yet. Imports from cfg,mainTabName]
framesPerUpdate=15	| 1-2.1 billion [number of frames per update of the gui. Lower values make the timer gui more fluid, higher values decrease cpu usage. Recommended maximum is 60, ie once per second]

## Deprecated
(for older versions, backwards compatible; any configuration options set here are imported then removed)
A few options can be set by editing the timers.cfg file present in the timer's directory. Line 1 shows syntax, line two shows an example. gridRows and gridColumns affects only the main tab, the auto-fill on input for new tab dimensions, and any imported tabs from old versions. Adding these to the config is optional.

cfg,mainTabName,name
cfg,mainTabName,Main Timer Stuff

cfg,gridRows,#
cfg,gridRows,3

cfg,gridColumns,#
cfg,gridColumns,0

cfg,winSize,#width,#height
cfg,winSize,400,300

###Disclaimer
THIS PROGRAM IS NOT AFFILIATED WITH RUNESCAPE NOR JAGEX. THIS PROGRAM DOES NOT INTERACT WITH ANY OF RUNESCAPE'S CONTENT OR PROGRAM. JAVAFX SKINS (javafx css/images) HERE WERE RECREATED BASED ON RUNESCAPE'S UI DESIGN BUT DO NOT DIRECTLY USE RUNESCAPE'S ASSETS. THE SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY KIND OF WARRANTY.

### Building and Versioning

This project includes preconfigured ANT versioning and packaging options.

**To display the current version used by ANT:**
```
ant
```

**To package the current version into a jar:**
```
ant dist
```

**To change the version and package the current version into a jar:**
```
ant revision (or ant major or ant minor)
ant dist
```
Note that changing the version and packaging the jar must occur as two separate ANT operations.
