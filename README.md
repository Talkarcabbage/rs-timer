RuneScape Timer
========

[![License](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/Talkarcabbage/rs-timer/blob/master/LICENSE)
[![Build Status](https://travis-ci.org/Talkarcabbage/rs-timer.svg?branch=master)](https://travis-ci.org/Talkarcabbage/rs-timer)

![image](https://cloud.githubusercontent.com/assets/2666891/23511922/b9b51dc8-ff13-11e6-9891-c54818ec5c79.png)

# Summary
A simple timer program intended for, but not limited to, use for timing RuneScape repeatables (_e.g._ dailies) and farm runs. Displays a window with customizable timers shown with progress bars. Includes "duration from starting point" timers and daily/weekly/monthly timers, that reset at UTC 0:00 / RS daily, weekly, and monthly reset times. Timers can be left-clicked to reset their time remaining to the length of the timer (or to the next tick for dailies/weeklies/monthlies).

# Operations
- Left-clicking on a timer's progress bar will reset its progress; do this when you complete the task
- Right-clicking on a timer's progress bar will set its status to complete 
- Shift-left clicking on a timer's progress bar allows you to edit some settings of that timer
- Clicking the + button shows a GUI to a new timer in the current tab
- Shift-clicking the + button shows GUIs to add a new tab
- Clicking the - button (setting it to selected) then clicking a timer's bar will remove that timer
- Shift-clicking the - button will ask if you want to delete the current tab (and its timers)
- Clicking the pin icon will toggle whether the window stays always-on-top
- Adjusting the slider sets the transparency of the window. The minimum is 5%. This is saved when reopening the program


Please note that features may be added, changed, or removed at any time. Also, no guarantee is given on the lack of existence of bugs. Information here is based on the listed version and may not match newer or older versions.

# Configuration/Syntax
##Properties file
Several settings can be configured via the properties file. The settings file can be refreshed to defaults by deleting it. The order these appear in may not be maintained/consistent.

```
setting=default value # Possible values   [Explanation]
logLevel=INFO         # ALL|FINEST,FINER,FINE,CONFIG,INFO,WARNING,SEVERE,NONE [Values closer to ALL generate more information in the console]
transparency=1.0      # 0.0 to 1.0        [Transparency value of the GUI as a double decimal]
winWidth=400          # 0 to 2.1 billion  [Window width]
winHeight=205         # 0 to 2.1 billion  [Window height]
defaultTabColumns=0   # 0 to 2.1 billion  [Number of default tab columns if no tabs exist. Imports from cfg,gridRows]
defaultTabRows=5      # 0 to 2.1 billion  [Number of default tab rows if no tabs exist. Imports from cfg,gridRows]
defaultTabName=Main   # Non-empty-string  [Name of the first tab if it is imported or does not exist yet. Imports from cfg,mainTabName]
framesPerUpdate=15    # 1 to 2.1 billion  [Number of frames per update of the GUI. Lower values make the timer GUI more fluid, higher values decrease CPU usage. Recommended maximum is 60, i.e. once per second]
```

#Disclaimer
THIS PROGRAM IS NOT AFFILIATED WITH RUNESCAPE NOR JAGEX. THIS PROGRAM DOES NOT INTERACT WITH ANY OF RUNESCAPE'S CONTENT OR PROGRAM. JAVAFX SKINS (JavaFX CSS/images) HERE WERE INSPIRED RUNESCAPE'S UI DESIGN, BUT DO NOT DIRECTLY USE RUNESCAPE'S ASSETS. THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY KIND OF WARRANTY.

# Building and Versioning

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
ant revision
ant dist
```
Use `ant major`, `ant minor`, or `ant revision` to increment the respective parts of the version number.

Note that changing the version and packaging the jar must occur as two separate ANT operations.

**To automatically draft a new release using Travis CI:**
```
git tag v0.0.1
git push origin v0.0.1
```
Make sure to include a 'v' before all tags
