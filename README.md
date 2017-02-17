RuneScape Timer
========

![image](https://cloud.githubusercontent.com/assets/2666891/8512872/436d4bbc-230b-11e5-9900-aa71b9c9465f.png)

### About
For version 0.11.4

A simple timer program originally intended for, but not limited to, use for timing RuneScape dailies and farm runs. Displays a window with customizable timers shown with progress bars. Includes "duration from starting point" timers and daily/weekly timers, that reset at UTC 0:00 / RS daily and weekly reset times. Timers can be clicked to reset their time remaining to the length of the timer (or to the next tick for dailies/weeklies).

### Operations
- The green `T` adds a new tab
- The red `T` deletes the current tab
- The green `+` adds a new timer to the current tab
- The red `-` deletes the next timer you left click


Please note that features may be added, changed, or removed at any time. Also, no guarantee is given on the lack of existence of bugs. Information here is based on the listed version and may not match newer or older versions.


### Configuration/Syntax
A few options can be set by editing the cfg file present in the timer's directory. Line 1 shows syntax, line two shows an example. gridRows and gridColumns affects only the main tab, the auto-fill on input for new tab dimensions, and any imported tabs from old versions. Adding these to the config is optional.

cfg,mainTabName,name

cfg,mainTabName,Main Timer Stuff

cfg,gridRows,#

cfg,gridRows,3

cfg,gridColumns,#

cfg,gridColumns,0

cfg,winSize,#width,#height

cfg,winSize,400,300

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
