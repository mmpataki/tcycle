﻿# tcycle

Tcycle is a simple application which is going to help you loop through the tasks you have in your workday. 

#### Need 

I had problems when I used to forget things like checking out whether "the installation is complete", "did I read go through some training document", "did I reply to this customer". I wished I had a ToDo list manager which has
	a. A local storage
	b. Works like a queue
	c. Where I can postpone tasks

But I couldn't find any. So I built one for me in weekend.


Built on the top of principle `KISS` (Keep it simple, stupid) it has the following use cases.

	1. You can add tasks to the session (We will add persistence storage soon)
	2. You can delete them when they are done.
	3. You can add priority [1-5] to them (I have an idea of prioritizing them in the cycle, but still in development, help me if you have any idea)
	4. You can postpone the work. (Currently this will put the task at the end of the queue)

#### Pros.
	1. It's open source. Feel free to play with it. 
	

#### Cons.
	1. Secure > NO, anyone access your page and play with your tasks if they have IP and port.
	2. Not a good scheduler. (Since it's simple)

#### How to use
    * Unzip the file attached and double click the jar file.

##### GUI
    * Copy paste the link in browser 
##### Shell
    ````
    java -jar tcycle.jar
    ````
   * Copy paste the link in browser (You may wish to use the IP of the machine you are running it on) 
