Sensor based music player : Setup/configuration on android studio.

There are two ways two imports this project into android studio:-
1. In android studio IDE, goto File (extreme left of screen) --> New --> Import project and specify the path of project in input panel.

2. For some reasons (may be your android studio IDE version may not be the same as developer of the project) you should create a new project
	and go through following steps:
	i. Copy paste all the classes in your project i.e. main classes of app in main folder of project and testing classes in testing folder.
	ii. Copy paste my manifest file in your project.
	iii. paste my layout files in your project.
	iv. Add dependency for cardview in build.gradle (Module:App).
	In this way, you can manually setup your project with my source code.