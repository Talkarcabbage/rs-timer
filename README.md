RuneScape Timer
========

![image](https://cloud.githubusercontent.com/assets/2666891/8512872/436d4bbc-230b-11e5-9900-aa71b9c9465f.png)

For version 0.8.2

A simple timer program originally intended for, but not limited to, use for timing RuneScape dailies and farm runs. Displays a window with customizable timers shown with progress bars. Includes "duration from starting point" timers and daily/weekly timers, that reset at UTC 0:00 / RS daily and weekly reset times. Timers can be clicked to reset their time remaining to the length of the timer (or to the next tick for dailies/weeklies). Clicking the green + adds a new tab, the green T a new timer, and displays input boxes for necessary information. Click the - button, then click a timer, to remove the timer. Click the red T to remove the current tab. To define a new timer as a daily or weekly countdown, precede the name of the timer with a `!` or `#`, respectively.


Please note that features may be added, changed, or removed at any time. Also, no guarantee is given on the lack of existence of bugs. Information here is based on the listed version and may not match newer or older versions.


Configuration/Syntax
A few options can be set by editing the cfg file present in the timer's directory. Line 1 shows syntax, line two shows an example. gridRows and gridColumns affects only the main tab, the auto-fill on input for new tab dimensions, and any imported tabs from old versions. Adding these to the config is optional.



cfg,mainTabName,name

cfg,mainTabName,Main Timer Stuff



cfg,gridRows,#

cfg,gridRows,3



cfg,gridColumns,#

cfg,gridColumns,0



cfg,winSize,#width,#height

cfg,winSize,400,300
